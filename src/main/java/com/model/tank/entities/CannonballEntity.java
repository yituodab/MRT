package com.model.tank.entities;

import com.model.tank.api.client.interfaces.ITargetEntity;
import com.model.tank.events.CannonballHitEntityEvent;
import com.model.tank.init.ModDamageTypes;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.data.tank.CannonballData;
import com.model.tank.utils.CannonballType;
import com.model.tank.utils.EntityHelper;
import com.model.tank.utils.ExplodeHelper;
import com.model.tank.utils.MRTEntityHitResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;

public class CannonballEntity extends Projectile implements GeoEntity, IEntityAdditionalSpawnData {
    public CannonballEntity(EntityType<? extends Projectile> entityType, Level level, Entity owner, CannonballData data, ResourceLocation id, float XRot, float YRot, Vec3 position){
        super(entityType, level);
        this.setOwner(owner);
        this.setPos(position);
        this.id = id;
        fromCannonballData(data);
        this.shoot(owner, XRot, YRot);
    }
    public CannonballEntity(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }
    private static final double resistance = 0.01;
    private static final double gravity = 0.49;
    private float entityDamage = 20;
    private CannonballType type = CannonballType.AP;
    private ResourceLocation id;
    private int life = 100;
    private float TNTmass;
    private double speed = 1000;

    public void fromCannonballData(CannonballData data){
        this.entityDamage = data.getEntityDamage();
        this.type = data.getType();
        this.speed = data.getSpeed();
        this.life = data.getLife();
        this.TNTmass = data.getTNTmass();
    }

    @Override
    public void tick() {
        super.tick();
        if(!this.level().isClientSide) {
            onServerTick();
        }
        // 更新朝向
        this.updateRotation();
        // 更新位置
        this.setPos(this.position().add(this.getDeltaMovement()));
        // 更新速度
        this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));
        this.setDeltaMovement(this.getDeltaMovement().scale(1 - resistance));
        if (this.tickCount >= this.life - 1) {
            this.discard();
        }
    }
    public void onServerTick(){
        Vec3 startPos = this.position();
        Vec3 endPos = this.position().add(this.getDeltaMovement());
        BlockHitResult blockHitResult = this.level().clip(new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
        if(blockHitResult.getType() != HitResult.Type.MISS) {
            endPos = blockHitResult.getLocation();
        }
        List<EntityHitResult> onHitEntities = EntityHelper.findEntitiesOnPath(this, startPos, endPos);
        if(!onHitEntities.isEmpty()){
            for(EntityHitResult entityHitResult : onHitEntities) {
                this.onHitEntity(new MRTEntityHitResult(entityHitResult, startPos, endPos));
                if(this.isRemoved()){
                    return;
                }
            }
        }
        this.onHitBlock(blockHitResult);
    }


    protected void onHitEntity(MRTEntityHitResult pResult) {
        Entity entity = pResult.getEntity();
        Vec3 hitPos = pResult.getLocation();
        DamageSource damageSource = ModDamageTypes.create(this.level(), this, this.getOwner());
        float damage = this.entityDamage;
        if(entity instanceof ITargetEntity iTargetEntity){
            // 发布事件
            CannonballHitEntityEvent.TargetEntity event = new CannonballHitEntityEvent.TargetEntity(this, entity, hitPos, damageSource, damage);
            if(MinecraftForge.EVENT_BUS.post(event)){
                return;
            }
            boolean discard = iTargetEntity.onCannonballHit(this, new MRTEntityHitResult(event.getEntity(), event.getHitPos(), pResult.getStartPos(), pResult.getStartPos()), event.getSource(), event.getDamage());
            if(discard){
                this.discard();
            }
            return;
        }
        // 发布事件
        CannonballHitEntityEvent event = new CannonballHitEntityEvent(this, entity, hitPos, damageSource, damage);
        if(MinecraftForge.EVENT_BUS.post(event)){
            return;
        }
        // 更新变量
        entity = event.getEntity();
        hitPos = event.getHitPos();
        damageSource = event.getSource();
        damage = event.getDamage();
        if(entity == null){
            return;
        }
        // 造成伤害
        entity.hurt(damageSource, damage);
        entity.invulnerableTime = 0;
        switch (this.type) {
            case HE,HEAT,HESH -> {
                ExplodeHelper.createExplode(Objects.requireNonNull(this.getOwner()), this.TNTmass, hitPos);
                this.discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        if(pResult.getType() == HitResult.Type.MISS)return;
        super.onHitBlock(pResult);
        switch (this.type){
            case HE,HEAT,HESH -> ExplodeHelper.createExplode(Objects.requireNonNull(this.getOwner()), this.TNTmass, this.position());
            case AP,APBC,APC,APCBC,APCR,APDS,APFSDS -> this.level().destroyBlock(pResult.getBlockPos(),true,this.getOwner());
        }
        this.discard();
    }

    public void shoot(Entity Shooter, float XRot, float YRot) {
        super.shootFromRotation(Shooter, XRot, YRot, 0, (float) speed / 20, 0);
    }

    @Override
    protected void defineSynchedData() {
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    public CannonballType getCannonballType() {
        return type;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {return GeckoLibUtil.createInstanceCache(this);}

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(id);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        this.id = friendlyByteBuf.readResourceLocation();
        fromCannonballData(DataLoader.getCannonballData(id));
    }
}
