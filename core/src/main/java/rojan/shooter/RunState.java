package rojan.shooter;

import com.badlogic.gdx.utils.ObjectIntMap;
import rojan.shooter.Weapon.WeaponType;

public class RunState{
    private boolean keyOwned;
    private ObjectIntMap<WeaponType> ammo;

    private int currentScore;
    private int headshotCount;
    private int bodyshotCount;
    private int limbshotCount;
    private int killCount;

    private int pendingScore;
    private int nextHeadshotReward = 5;
    private int nextBodyshotReward = 10;
    private int nextLimbshotReward = 15;
    private int nextKillReward = 10;

    RunState(){
        ammo = new ObjectIntMap<>();
    }

    public void receiveKey(){
        keyOwned = true;
    }

    public void useKey(){
        keyOwned = false;
    }

    public boolean hasKey(){
        return keyOwned;
    }

    public void addScore(int amount){
        currentScore+=amount;
    }

    public int getCurrentScore(){
        return currentScore;
    }

    public void addHeadshot(){
        headshotCount++;
    }

    public int getHeadshotCount(){
        return headshotCount;
    }

    public void addBodyshot(){
        bodyshotCount++;
    }

    public int getBodyshotCount(){
        return bodyshotCount;
    }

    public void addLimbshot(){
        limbshotCount++;
    }

    public int getLimbshotCount(){
        return limbshotCount;
    }

    public void addKill(){
        killCount++;
    }

    public int getKillCount(){
        return killCount;
    }

    public void addAmmo(WeaponType type,int amount){
        int currentAmmo = ammo.get(type,0);
        ammo.put(type,currentAmmo+amount);
    }

    public int getAmmo(WeaponType type){
        return ammo.get(type,0);
    }

    public int takeAmmo(WeaponType type,int amount){
        int currentAmmo = ammo.get(type,0);
        int ammoTaken = Math.min(currentAmmo,amount);

        ammo.put(type,currentAmmo-ammoTaken);

        return ammoTaken;
    }

    public void addPendingScore(int amount){
        pendingScore+=amount;
    }

    public int getPendingScore(){
        return pendingScore;
    }

    public void clearPendingScore(){
        pendingScore = 0;
    }

    public int getNextHeadshotReward(){
        return nextHeadshotReward;
    }

    public void advanceHeadshotReward(){
        nextHeadshotReward+=5;
    }

    public int getNextBodyshotReward(){
        return nextBodyshotReward;
    }

    public void advanceBodyshotReward(){
        nextBodyshotReward+=10;
    }

    public int getNextLimbshotReward(){
        return nextLimbshotReward;
    }

    public void advanceLimbshotReward(){
        nextLimbshotReward+=15;
    }

    public int getNextKillReward(){
        return nextKillReward;
    }

    public void advanceKillReward(){
        nextKillReward+=10;
    }
}