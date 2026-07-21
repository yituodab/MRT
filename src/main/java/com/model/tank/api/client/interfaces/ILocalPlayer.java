package com.model.tank.api.client.interfaces;
public interface ILocalPlayer {

    void shoot();

    void aim(boolean aim);

    boolean isAim();

    void checkModules(boolean isChecking);

    boolean isCheckingModules();
}
