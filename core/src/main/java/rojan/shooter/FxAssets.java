package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public final class FxAssets{
    private FxAssets(){}
    private static Array<Texture> loadedTextures;
    public static TextureRegion[] phaseBeam;
    public static TextureRegion[] emitterFlash;
    public static TextureRegion[] beamContact;
    public static TextureRegion[] targetReticle;
    public static TextureRegion[] droneBurst;

    public static TextureRegion[] bombardmentPortalOpen;
    public static TextureRegion[] bombardmentPortalActive;
    public static TextureRegion[] bombardmentPortalClose;
    public static TextureRegion[] poisonPortalOpen;
    public static TextureRegion[] poisonPortalActive;
    public static TextureRegion[] poisonPortalRelease;
    public static TextureRegion[] poisonPortalClose;
    public static TextureRegion[] empPortalOpen;
    public static TextureRegion[] empPortalActive;
    public static TextureRegion[] empPortalClose;

    public static TextureRegion[] poisonFieldStart;
    public static TextureRegion[] poisonFieldLoop;
    public static TextureRegion[] poisonFieldEnd;


    public static Texture playerHealthFrameTexture;
    public static Texture playerHealthFillTexture;
    public static Texture playerHealthIconTexture;
    public static TextureRegion playerHealthFrame;
    public static TextureRegion playerHealthFill;
    public static TextureRegion playerHealthIcon;


    public static Texture playerJetpackFrameTexture;
    public static Texture playerJetpackFillTexture;
    public static Texture playerJetpackIconTexture;
    public static TextureRegion playerJetpackFrame;
    public static TextureRegion playerJetpackFill;
    public static TextureRegion playerJetpackIcon;

    public static TextureRegion[] playerPoisonStart;
    public static TextureRegion[] playerPoisonLingering;
    public static TextureRegion[] playerPoisonEnd;

    public static TextureRegion[] playerStunStart;
    public static TextureRegion[] playerStunLingering;
    public static TextureRegion[] playerStunEnd;

    public static TextureRegion[] empPulse;

    public static TextureRegion[] bombardmentRainBolt;
    public static TextureRegion[] bombardmentRainImpact;

    public static Texture reloadIconTexture;
    public static TextureRegion reloadIcon;

    public static Texture emptyMagazineTexture;
    public static TextureRegion emptyMagazineIcon;

    public static Texture scoreDashboard;
    public static Texture goldIcon;
    public static Texture scoreIcon;
    public static Texture bestScoreIcon;

    public static Texture goldRewardIcon;
    public static Texture scoreRewardIcon;
    public static Texture ammoRewardIcon;

    public static Texture jackpotIcon;
    public static Texture biggerJackpotIcon;
    public static Texture godModeIcon;

    public static ObjectMap<Powerup.PowerupType,Texture> powerupIcons;
    public static ObjectMap<Powerup.SpicyType,Texture> spicyIcons;
    public static ObjectMap<Powerup.MoreSpicyType,Texture> moreSpicyIcons;
    public static ObjectMap<Weapon.WeaponType,Texture> weaponRewardIcons;
    public static Texture inventoryBag;
    public static Texture inventoryBagHover;
    public static Texture inventoryBagPressed;
    public static Texture inventoryPanel;
    public static Texture inventoryPanelGun;
    public static Texture inventoryWeaponLocked;
    public static Texture inventoryWeaponEquipped;

    public static TextureRegion[] groundExplosion;
    public static TextureRegion[] midAirExplosion;

    public static class PowerAuraAnimation{
        Texture backTexture;
        Texture frontTexture;
        TextureRegion[] backFrames;
        TextureRegion[] frontFrames;
        PowerAuraAnimation(String id){
            backTexture = new Texture(AssetPaths.FX.powerAuraBack(id));
            frontTexture = new Texture(AssetPaths.FX.powerAuraFront(id));
            backTexture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
            frontTexture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
            backFrames = new TextureRegion[24];
            frontFrames = new TextureRegion[24];
            for(int i=0;i<24;i++){
                int column = i%6;
                int row = i/6;
                int x = 2+column*260;
                int y = 2+row*324;
                backFrames[i] = new TextureRegion(backTexture,x, y, 256,320);
                frontFrames[i] = new TextureRegion(frontTexture, x, y, 256, 320);
            }
        }

        void dispose(){
            backTexture.dispose();
            frontTexture.dispose();
        }
    }
    private static ObjectMap<String,PowerAuraAnimation> powerAuraCache;
    public static Texture powerFrame;
    public static Texture powerNormal;
    public static Texture powerSpicy;
    public static Texture powerMoreSpicy;
    public static Texture powerSpecial;

    public static void loadOnce(){
        if(loadedTextures != null)
            return;
        loadedTextures = new Array<>();
        powerAuraCache = new ObjectMap<>();
        phaseBeam = loadFrames(AssetPaths.FX.PHASE_BEAM,AssetPaths.FX.PHASE_BEAM_FRAMES);
        emitterFlash = loadFrames(AssetPaths.FX.EMITTER_FLASH,AssetPaths.FX.EMITTER_FLASH_FRAMES);
        beamContact = loadFrames(AssetPaths.FX.BEAM_CONTACT,AssetPaths.FX.BEAM_CONTACT_FRAMES);
        targetReticle = loadFrames(AssetPaths.FX.TARGET_RETICLE,AssetPaths.FX.TARGET_RETICLE_FRAMES);
        droneBurst = loadFrames(AssetPaths.FX.DRONE_BURST,AssetPaths.FX.DRONE_BURST_FRAMES);
        bombardmentPortalOpen = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_OPEN,AssetPaths.FX.BOMBARDMENT_PORTAL_OPEN_FRAMES);
        bombardmentPortalActive = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_ACTIVE,AssetPaths.FX.BOMBARDMENT_PORTAL_ACTIVE_FRAMES);
        bombardmentPortalClose = loadFrames(AssetPaths.FX.BOMBARDMENT_PORTAL_CLOSE,AssetPaths.FX.BOMBARDMENT_PORTAL_CLOSE_FRAMES);

        poisonPortalOpen = loadFrames(AssetPaths.FX.POISON_PORTAL_OPEN,AssetPaths.FX.POISON_PORTAL_OPEN_FRAMES);
        poisonPortalActive = loadFrames(AssetPaths.FX.POISON_PORTAL_ACTIVE,AssetPaths.FX.POISON_PORTAL_ACTIVE_FRAMES);
        poisonPortalRelease = loadFrames(AssetPaths.FX.POISON_PORTAL_RELEASE,AssetPaths.FX.POISON_PORTAL_RELEASE_FRAMES);
        poisonPortalClose = loadFrames(AssetPaths.FX.POISON_PORTAL_CLOSE,AssetPaths.FX.POISON_PORTAL_CLOSE_FRAMES);

        empPortalOpen = loadFrames(AssetPaths.FX.EMP_PORTAL_OPEN,AssetPaths.FX.EMP_PORTAL_OPEN_FRAMES);
        empPortalActive = loadFrames(AssetPaths.FX.EMP_PORTAL_ACTIVE,AssetPaths.FX.EMP_PORTAL_ACTIVE_FRAMES);
        empPortalClose = loadFrames(AssetPaths.FX.EMP_PORTAL_CLOSE,AssetPaths.FX.EMP_PORTAL_CLOSE_FRAMES);

        poisonFieldStart = loadFrames(AssetPaths.FX.POISON_FIELD_START,AssetPaths.FX.POISON_FIELD_START_FRAMES);
        poisonFieldLoop =loadFrames(AssetPaths.FX.POISON_FIELD_LOOP,AssetPaths.FX.POISON_FIELD_LOOP_FRAMES);
        poisonFieldEnd =loadFrames(AssetPaths.FX.POISON_FIELD_END,AssetPaths.FX.POISON_FIELD_END_FRAMES);

        playerHealthFrameTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_FRAME);
        playerHealthFillTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_FILL);
        playerHealthFrame = new TextureRegion(playerHealthFrameTexture);
        playerHealthFill = new TextureRegion(playerHealthFillTexture);
        playerHealthIconTexture = new Texture(AssetPaths.Hud.PLAYER_HEALTH_ICON);

        playerJetpackFrameTexture = new Texture(AssetPaths.Hud.JETPACK_FRAME);
        playerJetpackFillTexture = new Texture(AssetPaths.Hud.JETPACK_FILL);
        playerJetpackFrame = new TextureRegion(playerJetpackFrameTexture);
        playerJetpackFill = new TextureRegion(playerJetpackFillTexture);
        playerJetpackIconTexture = new Texture(AssetPaths.Hud.PLAYER_JETPACK_ICON);

        playerPoisonStart = loadFrames(AssetPaths.FX.PLAYER_POISON_START, AssetPaths.FX.PLAYER_POISON_START_FRAMES);
        playerPoisonLingering = loadFrames(AssetPaths.FX.PLAYER_POISON_LINGERING, AssetPaths.FX.PLAYER_POISON_LINGERING_FRAMES);
        playerPoisonEnd = loadFrames(AssetPaths.FX.PLAYER_POISON_END, AssetPaths.FX.PLAYER_POISON_END_FRAMES);
        playerHealthIcon = new TextureRegion(playerHealthIconTexture);
        playerJetpackIcon = new TextureRegion(playerJetpackIconTexture);

        playerStunStart = loadFrames(AssetPaths.FX.PLAYER_STUN_START, AssetPaths.FX.PLAYER_STUN_START_FRAMES);
        playerStunLingering = loadFrames(AssetPaths.FX.PLAYER_STUN_LINGERING,AssetPaths.FX.PLAYER_STUN_LINGERING_FRAMES);
        playerStunEnd = loadFrames(AssetPaths.FX.PLAYER_STUN_END,AssetPaths.FX.PLAYER_STUN_END_FRAMES);

        empPulse = loadFrames(AssetPaths.FX.EMP_PULSE,AssetPaths.FX.EMP_PULSE_FRAMES);

        bombardmentRainBolt = loadFrames(AssetPaths.FX.BOMBARDMENT_RAIN_BOLT,AssetPaths.FX.BOMBARDMENT_RAIN_BOLT_FRAMES);
        bombardmentRainImpact = loadFrames(AssetPaths.FX.BOMBARDMENT_RAIN_IMPACT,AssetPaths.FX.BOMBARDMENT_RAIN_IMPACT_FRAMES);

        reloadIconTexture = new Texture(AssetPaths.Hud.RELOAD_ICON);
        reloadIcon = new TextureRegion(reloadIconTexture);

        scoreDashboard = new Texture(AssetPaths.Hud.SCORE_DASHBOARD);
        goldIcon = new Texture(AssetPaths.Hud.GOLD_ICON);
        scoreIcon = new Texture(AssetPaths.Hud.SCORE_ICON);
        bestScoreIcon = new Texture(AssetPaths.Hud.BEST_SCORE_ICON);

        String reward = "loot/lootIcons/icons/1024/";
        String guns = "loot/lootIcons/guns/1024/";

        goldRewardIcon = new Texture(reward+"gold.png");
        scoreRewardIcon = new Texture(reward+"score.png");
        ammoRewardIcon = new Texture(reward+"ammo.png");
        jackpotIcon = new Texture(reward+"jackpot.png");
        biggerJackpotIcon = new Texture(reward+"bigger_jackpot.png");
        godModeIcon = new Texture(reward+"god_mode.png");

        powerupIcons = new ObjectMap<>();
        powerupIcons.put(Powerup.PowerupType.HEALTH,new Texture(reward+"health.png"));
        powerupIcons.put(Powerup.PowerupType.SHIELD,new Texture(reward+"shield.png"));
        powerupIcons.put(Powerup.PowerupType.JETPACK,new Texture(reward+"jetpack_boost.png"));
        powerupIcons.put(Powerup.PowerupType.FIRE_RATE,new Texture(reward+"fire_rate_boost.png"));

        spicyIcons = new ObjectMap<>();
        spicyIcons.put(Powerup.SpicyType.OVERDRIVE,new Texture(reward+"overdrive.png"));
        spicyIcons.put(Powerup.SpicyType.RAPID_MOBILITY,new Texture(reward+"rapid_mobility.png"));
        spicyIcons.put(Powerup.SpicyType.BOTTOMLESS_MAG, new Texture(reward+"bottomless_mag.png"));
        spicyIcons.put(Powerup.SpicyType.DAMAGE_SURGE, new Texture(reward+"damage_surge.png"));
        spicyIcons.put(Powerup.SpicyType.GOLD_RUSH,new Texture(reward+"gold_rush.png"));
        spicyIcons.put(Powerup.SpicyType.SCORE_FRENZY,new Texture(reward+"score_frenzy.png"));

        moreSpicyIcons = new ObjectMap<>();
        moreSpicyIcons.put(Powerup.MoreSpicyType.BERSERK,new Texture(reward+"berserk.png"));
        moreSpicyIcons.put(Powerup.MoreSpicyType.INFINITE_ASSAULT,new Texture(reward+"infinite_assault.png"));
        moreSpicyIcons.put(Powerup.MoreSpicyType.HYPERDRIVE,new Texture(reward+"hyperdrive.png"));
        moreSpicyIcons.put(Powerup.MoreSpicyType.FORTRESS,new Texture(reward+"fortress.png"));
        moreSpicyIcons.put(Powerup.MoreSpicyType.DOUBLE_FORTUNE,new Texture(reward+"double_fortune.png"));

        weaponRewardIcons = new ObjectMap<>();
        weaponRewardIcons.put(Weapon.WeaponType.AR,new Texture(guns+"ar.png"));
        weaponRewardIcons.put(Weapon.WeaponType.PISTOL,new Texture(guns+"pistol.png"));
        weaponRewardIcons.put(Weapon.WeaponType.SMG,new Texture(guns+"smg.png"));
        weaponRewardIcons.put(Weapon.WeaponType.SHOTGUN,new Texture(guns+"shotgun.png"));
        weaponRewardIcons.put(Weapon.WeaponType.SNIPER,new Texture(guns+"sniper.png"));
        weaponRewardIcons.put(Weapon.WeaponType.ROCKET_LAUNCHER,new Texture(guns+"rocket_launcher.png"));

        inventoryBag = new Texture(AssetPaths.Hud.INVENTORY_BAG);
        inventoryBagHover = new Texture(AssetPaths.Hud.INVENTORY_BAG_HOVER);
        inventoryBagPressed = new Texture(AssetPaths.Hud.INVENTORY_BAG_PRESSED);
        inventoryPanel = new Texture(AssetPaths.Hud.INVENTORY_PANEL);
        inventoryPanelGun = new Texture(AssetPaths.Hud.INVENTORY_PANEL_GUN);
        inventoryWeaponLocked = new Texture(AssetPaths.Hud.INVENTORY_WEAPON_LOCKED);
        inventoryWeaponEquipped = new Texture(AssetPaths.Hud.INVENTORY_WEAPON_EQUIPPED);

        powerFrame = new Texture(AssetPaths.Hud.POWER_FRAME);
        powerNormal = new Texture(AssetPaths.Hud.POWER_NORMAL);
        powerSpicy = new Texture(AssetPaths.Hud.POWER_SPICY);
        powerMoreSpicy = new Texture(AssetPaths.Hud.POWER_MORE_SPICY);
        powerSpecial = new Texture(AssetPaths.Hud.POWER_SPECIAL);

        groundExplosion = loadFrames(AssetPaths.Explosion.GROUND,AssetPaths.Explosion.GROUND_FRAMES);
        midAirExplosion = loadFrames(AssetPaths.Explosion.MID_AIR,AssetPaths.Explosion.MID_AIR_FRAMES);
    }

    private static TextureRegion[] loadFrames(String prefix,int frameCount){
        TextureRegion[] frames = new TextureRegion[frameCount];
        for(int i=0;i<frameCount;i++){
            String frameNumber = String.format("%02d",i);
            Texture texture = new Texture(prefix + frameNumber + ".png");
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
        return frames;
    }

    public static PowerAuraAnimation getPowerAura(String id){
        PowerAuraAnimation aura = powerAuraCache.get(id);
        if(aura == null){
            aura = new PowerAuraAnimation(id);
            powerAuraCache.put(id,aura);
        }
        return aura;
    }

    public static void dispose(){
        if(loadedTextures == null)
            return;
        for(Texture texture : loadedTextures)
            texture.dispose();
        loadedTextures = null;
        playerHealthFrameTexture.dispose();
        playerHealthFillTexture.dispose();
        playerHealthIconTexture.dispose();
        reloadIconTexture.dispose();
        scoreDashboard.dispose();
        goldIcon.dispose();
        scoreIcon.dispose();
        bestScoreIcon.dispose();
        inventoryBag.dispose();
        inventoryBagHover.dispose();
        inventoryBagPressed.dispose();
        inventoryPanel.dispose();
        inventoryPanelGun.dispose();
        inventoryWeaponLocked.dispose();
        inventoryWeaponEquipped.dispose();
        if(powerAuraCache != null){
            for(PowerAuraAnimation aura : powerAuraCache.values())
                aura.dispose();
            powerAuraCache.clear();
        }
    }
}