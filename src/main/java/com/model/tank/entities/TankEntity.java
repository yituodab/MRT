package com.model.tank.entities;


import com.model.tank.api.client.interfaces.ITargetEntity;
import com.model.tank.api.entity.ModularEntity;
import com.model.tank.api.nbt.TankEntityDataManager;
import com.model.tank.init.ModEntities;
import com.model.tank.network.NetWorkManager;
import com.model.tank.network.S2C.ServerTankShoot;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.client.data.tank.TankDisplay;
import com.model.tank.resource.data.Module;
import com.model.tank.resource.data.index.TankIndex;
import com.model.tank.resource.data.tank.CannonballData;
import com.model.tank.resource.data.tank.TankData;
import com.model.tank.utils.MRTEntityHitResult;
import com.model.tank.utils.ModDimensions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TankEntity extends ModularEntity implements IEntityAdditionalSpawnData, TankEntityDataManager, ITargetEntity {
    private final Map<ResourceLocation, Cannonball> cannonballs = new HashMap<>();
    private ResourceLocation currentCannonball;
    private TankDisplay display;
    private ResourceLocation tankID;
    private List<Module.Armor> armors;
    private ModDimensions dimensions;
    private float tickSteeringSpeed;
    private double tickMaxSpeed;
    private double tickBackSpeed;
    private float tickAcceleration;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private int reloadTime = 0;
    private int maxReloadTime = 0;
    private int direction = 1;// 向前为+1，向后为-1
    public TankEntity(EntityType<?> p_19870_, Level p_19871_, TankIndex tank, ResourceLocation id) {
        super(p_19870_, p_19871_);
        this.tankID = id;
        fromTankIndex(tank);
    }
    public void fromTankIndex(TankIndex tankIndex){
        this.display = tankIndex.getDisplay();
        TankData tank = tankIndex.getTankData();
        this.modules.clear();
        this.modules.addAll(List.of(tank.getModules()));
        this.armors = List.of(tank.getArmors());
        for (ResourceLocation id : tank.getCannonballs()) {
            CannonballData cannonballData = DataLoader.getCannonballData(id);
            if(cannonballData == null)continue;
            Cannonball cannonball = new Cannonball(cannonballData, 0);
            cannonballs.put(id, cannonball);
            if(this.getCurrentCannonball() == null)this.setCurrentCannonball(id);
        }
        for (Module module : this.modules) {
            this.moduleHealth.put(module.getID(), module.getMaxHealth());
            switch (module.getType()){
                case CANNON -> {
                    this.maxReloadTime = (int)(module.getReloadTime() * 1000);
                }
                case ENGINE -> {
                    this.tickSteeringSpeed = module.getSteeringSpeed() / 20;
                    this.tickAcceleration = module.getAcceleration() /20;
                    this.tickBackSpeed = module.getBackSpeed() / 20;
                    this.tickMaxSpeed = module.getMaxSpeed() / 20;
                }
            }
        }
        float[] boundingBox = tank.getBoundingBox();
        this.dimensions = new ModDimensions(boundingBox[0],boundingBox[1],boundingBox[2],true);
    }



    @Override
    public InteractionResult interact(Player pPlayer, InteractionHand pHand) {
        if(pPlayer.isSecondaryUseActive())return InteractionResult.PASS;
        if(!this.level().isClientSide && !this.isVehicle())
            return pPlayer.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        else return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        Entity controllingPassenger = null;
        if(!this.getPassengers().isEmpty())controllingPassenger = this.getPassengers().get(0);
        return controllingPassenger instanceof Player player ? player : super.getControllingPassenger();
    }

    @Override
    protected boolean canAddPassenger(Entity pPassenger) {
        return !this.isVehicle() && pPassenger instanceof Player;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return dimensions;
    }

    @Override
    public void setPos(double p_20210_, double p_20211_, double p_20212_) {
        super.setPos(p_20210_, p_20211_, p_20212_);
    }

    public double getCurrentSpeed() {
        return Vec3.ZERO.distanceTo(this.getDeltaMovement());
    }

    @Override
    public void refreshDimensions() {}

    @Override
    protected AABB makeBoundingBox() {
        if(this.dimensions == null){
            return super.makeBoundingBox();
        }
        return this.dimensions.makeBoundingBox(this.position(),this.getXRot(),this.getYRot());
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level().isClientSide()){
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5F));
            controlTank();
        }
        refreshReloadTime();
        this.move(MoverType.SELF,getDeltaMovement());
    }

    public boolean isReload(){
        return reloadTime > 0;
    }
    public void refreshReloadTime(){
        reloadTime = isReload() ? reloadTime - 50 : 0;
    }
    public void resetReloadTime(){this.reloadTime = maxReloadTime;}
    public boolean shoot(ResourceLocation id) {
        Level level = this.level();
        ServerPlayer player = (ServerPlayer) this.getControllingPassenger();
        Cannonball currentCannonball = cannonballs.get(id);
        CannonballData cannonballData = currentCannonball.data;
        boolean shootSuccess = false;
        if(cannonballData != null && player != null){
            // 确认玩家是否为创造模式
            boolean isCreative = player.isCreative();
            // 获取当前炮弹数
            int number = currentCannonball.getNumber();
            if((number > 0 || isCreative) && !isReload()){
                CannonballEntity cannonball = new CannonballEntity(ModEntities.CANNONBALLENTITY.get(), level, player, cannonballData, id,
                        player.getXRot(), player.getYRot(), player.getEyePosition());
                level.addFreshEntity(cannonball);
                // 减少炮弹数
                if(!isCreative)currentCannonball.setNumber(number - 1);
                cannonballs.put(id,currentCannonball);
                resetReloadTime();
                shootSuccess = true;
            }
            // 向客户端发包同步炮弹数及发射情况
            NetWorkManager.sendToPlayer(new ServerTankShoot(id,number,shootSuccess),player);
        }
        return shootSuccess;
    }
    @Override
    public boolean onCannonballHit(CannonballEntity cannonball, MRTEntityHitResult result, DamageSource source, float damage) {
        Vec3 startPos = result.getStartPos();
        Vec3 endPos = result.getEndPos();
        Module.Armor onHitArmor = null;
        Vec3 onHitPos = null;
        for(Module.Armor armor : this.getArmors()){
            Vec3 vec3 = armor.getHitBox().clip(startPos,endPos).orElse(null);
            if(vec3 != null){
                if((onHitArmor == null && onHitPos == null) || onHitPos.distanceTo(startPos) > vec3.distanceTo(startPos)){
                    onHitArmor = armor;
                    onHitPos = vec3;
                }
            }
        }
        if(onHitArmor == null){
            return false;
        }
        this.discard();
        return true;
    }
    public void setInput(boolean up,boolean down,boolean left,boolean right){
        inputUp = up;
        inputDown = down;
        inputLeft = left;
        inputRight = right;
    }

    public void controlTank() {
        double acceleration = tickAcceleration;
        if(this.isVehicle()){
            Vec3 deltaMovement;
            double currentSpeed = getCurrentSpeed();
            float yRot = getYRot();
            // 转向
            if(inputLeft){
                yRot -= tickSteeringSpeed;
            }
            if(inputRight) {
                yRot += tickSteeringSpeed;
            }
            this.setRot(yRot,getXRot());

            if(inputUp){
                double a = tickMaxSpeed - currentSpeed;
                if (direction == 1 && a <= tickAcceleration){
                    acceleration = a * direction;
                    if(a < 0)
                        acceleration = 0;
                }
                acceleration = acceleration * direction;
            }
            if(inputDown){
                double a = tickBackSpeed - currentSpeed;
                if (direction == -1 && a <= tickAcceleration){
                    acceleration = a * direction;
                    if(a < 0)
                        acceleration = 0;
                }
                acceleration = acceleration * direction;
            }
            deltaMovement = new Vec3(Mth.sin(-this.getYRot() * 0.017453292F)*acceleration,0,
                     Mth.cos(this.getYRot() * 0.017453292F)*acceleration);
            this.addDeltaMovement(deltaMovement);
        }
    }

    @Override
    public void load(CompoundTag pCompound) {
        super.load(pCompound);
        if(pCompound.contains(TANK_ID)){
            getDataFromNBT(pCompound);
        }
    }

    private void getDataFromNBT(CompoundTag compoundTag){
        this.tankID = new ResourceLocation(compoundTag.getString(TANK_ID));
        TankIndex tankIndex = DataLoader.getTankIndex(tankID);
        fromTankIndex(tankIndex);
        CompoundTag cannonballs = compoundTag.getCompound("cannonballs");
        CompoundTag modules = compoundTag.getCompound("modules");
        this.cannonballs.forEach((id, cannonball)->{
            int i = cannonballs.getInt(id.toString());
            cannonball.setNumber(i);
        });
        this.modules.forEach((module -> {
            int health = modules.getInt(String.valueOf(module.getID()));
            this.moduleHealth.replace(module.getID(),health);
        }));
    }

    private void putDataToNBT(CompoundTag compoundTag){
        compoundTag.putString(TANK_ID, this.tankID.toString());
        CompoundTag cannonballs = new CompoundTag();
        CompoundTag modules = new CompoundTag();
        this.cannonballs.forEach((id, cannonball)-> cannonballs.putInt(id.toString(), cannonball.getNumber()));
        this.moduleHealth.forEach((id, health)-> modules.putInt(id.toString(),health));
        compoundTag.put("cannonballs", cannonballs);
        compoundTag.put("modules", modules);
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        getDataFromNBT(compoundTag);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        putDataToNBT(compoundTag);
    }



    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(this.tankID);
        this.cannonballs.forEach((id,cannonball)-> {
            friendlyByteBuf.writeResourceLocation(id);
            friendlyByteBuf.writeInt(cannonball.getNumber());
        });
        this.moduleHealth.forEach((id,health)->{
            friendlyByteBuf.writeInt(id);
            friendlyByteBuf.writeInt(health);
        });
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        this.tankID = friendlyByteBuf.readResourceLocation();
        TankIndex tankIndex = DataLoader.getTankIndex(tankID);
        fromTankIndex(tankIndex);
        for(int i = 0;i<cannonballs.size();i++){
            ResourceLocation id = friendlyByteBuf.readResourceLocation();
            int number = friendlyByteBuf.readInt();
            cannonballs.get(id).setNumber(number);
        }
        for(int i = 0;i<moduleHealth.size();i++){
            int id = friendlyByteBuf.readInt();
            int health = friendlyByteBuf.readInt();
            moduleHealth.replace(id,health);
        }
    }

    public ResourceLocation getTankID() {
        return tankID;
    }

    public Map<ResourceLocation, Cannonball> getCannonballs() {return cannonballs;}
    public ResourceLocation getCurrentCannonball() {return this.currentCannonball;}
    public void setCurrentCannonball(ResourceLocation currentCannonball) {this.currentCannonball = currentCannonball;}
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return GeckoLibUtil.createInstanceCache(this);}
    public ResourceLocation getModelLocation() {return display.getModel();}
    public ResourceLocation getTextureLocation() {return display.getTexture();}
    public List<Module> getModules() {return modules;}
    public List<Module.Armor> getArmors() {return armors;}
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {return NetworkHooks.getEntitySpawningPacket(this);}
    @Deprecated
    public TankEntity(EntityType<?> p_19870_, Level p_19871_) {super(p_19870_, p_19871_);}

    public static class Cannonball{
        private final CannonballData data;
        private int number;
        public Cannonball(CannonballData data, int number){
            this.data = data;
            this.number = number;
        }
        public CannonballData getData() {
            return data;
        }
        public int getNumber() {
            return number;
        }
        public void setNumber(int number) {
            this.number = number;
        }
    }
}
