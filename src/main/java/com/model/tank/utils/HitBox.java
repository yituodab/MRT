package com.model.tank.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class HitBox extends AABB {
    private float YRot = 0;
    private float XRot = 0;
    private AABB MaxHitBox;

    public HitBox(BlockPos pPos,float XRot,float YRot) {
        this((double)pPos.getX(), (double)pPos.getY(), (double)pPos.getZ(), (double)(pPos.getX() + 1), (double)(pPos.getY() + 1), (double)(pPos.getZ() + 1),XRot,YRot);
    }

    public HitBox(BlockPos pStart, BlockPos pEnd,float XRot, float YRot) {
        this((double)pStart.getX(), (double)pStart.getY(), (double)pStart.getZ(), (double)pEnd.getX(), (double)pEnd.getY(), (double)pEnd.getZ(),XRot,YRot);
    }
    public HitBox(Vec3 startPos, Vec3 endPos, float XRot, float YRot){
        super(startPos,endPos);
        this.XRot = XRot;
        this.YRot = YRot;
    }

    public HitBox(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        super(pX1, pY1, pZ1, pX2, pY2, pZ2);
        double size = (new Vec3(minX,minY,minZ).distanceTo(new Vec3(maxX,maxY,maxZ)))/2;
        Vec3 Pos = this.getCenter();
        this.MaxHitBox = new AABB(Pos.add(size,size,size),Pos.subtract(size,size,size));
    }
    public HitBox(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2,float XRot,float YRot) {
        this(pX1, pY1, pZ1, pX2, pY2, pZ2);
        this.XRot = XRot;
        this.YRot = YRot;
    }

    @Override
    public Optional<Vec3> clip(Vec3 pFrom, Vec3 pTo) {
        Vec3 Pos = getCenter();
        Vec3 From = pFrom.subtract(Pos);
        Vec3 To = pTo.subtract(pTo);
        Vec3 from = From.xRot(XRot*0.017453292F).yRot(YRot*0.017453292F).add(pFrom);
        Vec3 to = To.xRot(XRot*0.017453292F).yRot(YRot*0.017453292F).add(pTo);
        return super.clip(from, to);
    }

    @Override
    public boolean intersects(double pX1, double pY1, double pZ1, double pX2, double pY2, double pZ2) {
        return true;// MaxHitBox.intersects(pX1, pY1, pZ1, pX2, pY2, pZ2);
    }
}
