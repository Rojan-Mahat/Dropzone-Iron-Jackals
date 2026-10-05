package rojan.shooter;

import com.badlogic.gdx.utils.ObjectIntMap;
import rojan.shooter.Weapon.WeaponType;
import com.badlogic.gdx.utils.Array;

public class Inventory{
    private int gold;
    private boolean keyOwned;
    private Array<Weapon.WeaponType> ownedWeapons;
    private ObjectIntMap<Weapon.WeaponType> ammo;
    private ObjectIntMap<Powerup.PowerupType> powerups;





    Inventory(){
        ownedWeapons = new Array<>();
        ammo = new ObjectIntMap<>();
        powerups = new ObjectIntMap<>();

    }

    public void addGold(int amount){
        gold+=amount;
    }

    public void receivekey(){
        keyOwned = true;
    }

    public void useKey(){
        keyOwned = false;
    }

    public void addWeapon(Weapon.WeaponType type){
        if(!ownedWeapons.contains(type,true))
            ownedWeapons.add(type);
    }

    public void addAmmo(Weapon.WeaponType type,int amount){
        int currentAmmo = ammo.get(type,0);
        ammo.put(type,currentAmmo+amount);
    }

    public int getAmmo(Weapon.WeaponType type){
        return ammo.get(type,0);
    }

    public int takeAmmo(WeaponType type, int amount){
        int currentAmmo = ammo.get(type,0);
        int ammoTaken = Math.min(currentAmmo,amount);
        ammo.put(type,currentAmmo-ammoTaken);
        return ammoTaken;
    }

    public void addPowerup(Powerup.PowerupType type, int amount){
        int current = powerups.get(type,0);
        powerups.put(type,current+amount);
    }

    public int getPowerupAmount(Powerup.PowerupType type){
        return powerups.get(type,0);
    }

    public boolean usePowerup(Powerup.PowerupType type){
        int current = powerups.get(type,0);
        if(current <= 0)
            return false;

        powerups.put(type,current-1);
        return true;
    }

    





    public static void main(String[] args){

    }
}