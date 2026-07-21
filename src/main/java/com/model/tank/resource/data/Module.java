package com.model.tank.resource.data;

import com.google.gson.annotations.SerializedName;
import com.model.tank.utils.HitBox;
import net.minecraft.world.phys.Vec3;

public class Module {
     public static enum Type{
         UNKNOWN,
         OTHER,
         ENGINE,
         OIL_TANK,
         AMMO_RACKS,
         CANNON,
         LATCH,
         STEERiNG_GEAR
    }
    @SerializedName("id")
    private int id = 0;
    @SerializedName("type")
    private Type type = Type.UNKNOWN;
    @SerializedName("position")
    private Vec3 position = new Vec3(0,0,0);
    @SerializedName("size")
    private Vec3 size = new Vec3(0,0,0);
    @SerializedName("max_health")
    private int maxHealth = 100;
    public HitBox getHitBox() {
        return new HitBox(position.subtract(size.scale((double)1/2)),position.add(size.scale((double)1/2)),0,0);
    }
    public Type getType() {
        return type;
    }
    public int getID() {
        return id;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public Vec3 getPosition() {
        return position;
    }
    public double[] getPositionToArray(){
        return new double[]{position.x,position.y,position.z};
    }
    public Vec3 getSize() {
        return size;
    }
    public double[] getSizeToArray(){
        return new double[]{size.x,size.y,size.z};
    }

    // 炮
    @SerializedName("reload_time")
    private float reloadTime = 0;
    public float getReloadTime() {
        return reloadTime;
    }
    // 引擎
    @SerializedName("max_speed")
    private double maxSpeed = 36;
    @SerializedName("back_max_speed")
    private double backSpeed = 9;
    @SerializedName("steering_speed")
    private float steeringSpeed = 20;
    @SerializedName("acceleration")
    private float acceleration = 1;
    public double getMaxSpeed() {
        return maxSpeed;
    }
    public double getBackMaxSpeed() {
        return backSpeed;
    }
    public float getSteeringSpeed() {
        return steeringSpeed;
    }
    public float getAcceleration() {
        return acceleration;
    }
    // 方向机
    @SerializedName("turret_steering_speed")
    private float turretSteeringSpeed = 20;
    public float getTurretSteeringSpeed() {
        return turretSteeringSpeed;
    }

    public static class Armor{
        @SerializedName("XRot")
        private float XRot = 0;
        @SerializedName("YRot")
        private float YRot = 0;
        @SerializedName("position")
        private Vec3 position = new Vec3(0, 0, 0);
        @SerializedName("size")
        private double[] size = {1,1};
        @SerializedName("thickness")
        private double thickness = 1;
        public double getThickness() {
            return thickness;
        }
        public HitBox getHitBox() {
            return new HitBox(position.subtract(new Vec3(size[0],size[1], thickness)),position.add(new Vec3(size[0],size[1], thickness)),0,0);
        }
        public Vec3 getPosition() {
            return position;
        }
    }
}
