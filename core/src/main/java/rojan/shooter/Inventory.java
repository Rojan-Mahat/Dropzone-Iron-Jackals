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
    private ObjectIntMap<Powerup.SpicyType> spicy;
    private ObjectIntMap<Powerup.MoreSpicyType> moreSpicy;
    private int godMode = 0;

    Inventory(){
        ownedWeapons = new Array<>();
        ammo = new ObjectIntMap<>();
        powerups = new ObjectIntMap<>();
        spicy = new ObjectIntMap<>();
        moreSpicy =new ObjectIntMap<>();
    }

    public void addGold(int amount){
        gold+=amount;
    }
    public int getGold(){
        return gold;
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

    public void addWeaponFromArray(Array<Weapon.WeaponType> receivedWeapons,int initial_ammo){
        for(Weapon.WeaponType type : receivedWeapons){
            if(!ownedWeapons.contains(type,true)){
                ownedWeapons.add(type);
                addAmmo(type,initial_ammo);
            }
            else
                addAmmo(type,2*initial_ammo);
        }
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

    public void addPowerupFromArray(Array<Powerup.PowerupType> receivedPowerups){
        for(Powerup.PowerupType type : receivedPowerups){
            int current = powerups.get(type,0);
            powerups.put(type,current+1);
        }
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


    public void addSpicy(Powerup.SpicyType type, int amount){
        int current = spicy.get(type,0);
        spicy.put(type,current+amount);
    }

    public int getSpicyAmount(Powerup.SpicyType type){
        return spicy.get(type,0);
    }

    public boolean useSpicy(Powerup.SpicyType type){
        int current = spicy.get(type,0);
        if(current <= 0)
            return false;

        spicy.put(type,current-1);
        return true;
    }

    public void addMoreSpicy(Powerup.MoreSpicyType type, int amount){
        int current = moreSpicy.get(type,0);
        moreSpicy.put(type,current+amount);
    }

    public int getMoreSpicyAmount(Powerup.MoreSpicyType type){
        return moreSpicy.get(type,0);
    }

    public boolean useMoreSpicy(Powerup.MoreSpicyType type){
        int current = moreSpicy.get(type,0);
        if(current <= 0)
            return false;

        moreSpicy.put(type,current-1);
        return true;
    }
    public void addGodMode(int amount){
        godMode += amount;
    }

    public int getGodModeAmount(){
        return godMode;
    }

    public boolean useGodMode(){
        if(godMode <= 0)
            return false;

        godMode--;
        return true;
    }




    public static void main(String[] args){

    }
}