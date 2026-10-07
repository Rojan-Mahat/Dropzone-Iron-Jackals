package rojan.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SaveManager{
    private final Preferences save;

    SaveManager(){
        save = Gdx.app.getPreferences("IronJackalsSave");
    }

    public void saveProfile(PlayerProfile profile){
        Inventory inventory = profile.getInventory();
        save.putInteger("gold",inventory.getGold());

        save.putInteger("bestScore",profile.getBestScore());

        for(Weapon.WeaponType type : Weapon.WeaponType.values())
            save.putBoolean("weapon_"+type.name(),inventory.ownsWeapon(type));

        for(Powerup.PowerupType type : Powerup.PowerupType.values())
            save.putInteger("powerup_"+type.name(),inventory.getPowerupAmount(type));

        for(Powerup.SpicyType type : Powerup.SpicyType.values())
            save.putInteger("spicy_"+type.name(),inventory.getSpicyAmount(type));

        for(Powerup.MoreSpicyType type : Powerup.MoreSpicyType.values())
            save.putInteger("moreSpicy_"+type.name(),inventory.getMoreSpicyAmount(type));

        save.putInteger("godMode",inventory.getGodModeAmount());

        save.flush();
    }

    public PlayerProfile loadProfile(){
        System.out.println("LOADING PROFILE: "+save.getInteger("gold",0));
        PlayerProfile profile = new PlayerProfile();
        Inventory inventory = profile.getInventory();
        

        inventory.addGold(save.getInteger("gold",0));
        profile.updateBestScore(save.getInteger("bestScore",0));

        for(Weapon.WeaponType type : Weapon.WeaponType.values()){
            if(save.getBoolean("weapon_"+type.name(),false))
                inventory.addWeapon(type);
        }

        for(Powerup.PowerupType type : Powerup.PowerupType.values()){
            int amount = save.getInteger("powerup_"+type.name(),0);

            if(amount > 0)
                inventory.addPowerup(type,amount);
        }

        for(Powerup.SpicyType type : Powerup.SpicyType.values()){
            int amount = save.getInteger("spicy_"+type.name(),0);

            if(amount > 0)
                inventory.addSpicy(type,amount);
        }

        for(Powerup.MoreSpicyType type : Powerup.MoreSpicyType.values()){
            int amount = save.getInteger("moreSpicy_"+type.name(),0);

            if(amount > 0)
                inventory.addMoreSpicy(type,amount);
        }

        int godMode = save.getInteger("godMode",0);

        if(godMode > 0)
            inventory.addGodMode(godMode);

        return profile;
    }
}