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

    public static Texture shopIcon;
    public static Texture keyIcon;


    public static Texture pauseShell;
    public static Texture pauseResumeNormal;
    public static Texture pauseResumeHover;
    public static Texture pauseResumePressed;
    public static Texture pauseResumeDisabled;

    public static Texture pauseRestartNormal;
    public static Texture pauseRestartHover;
    public static Texture pauseRestartPressed;
    public static Texture pauseRestartDisabled;

    public static Texture pauseSettingsNormal;
    public static Texture pauseSettingsHover;
    public static Texture pauseSettingsPressed;
    public static Texture pauseSettingsDisabled;

    public static Texture pauseMainMenuNormal;
    public static Texture pauseMainMenuHover;
    public static Texture pauseMainMenuPressed;
    public static Texture pauseMainMenuDisabled;

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

    public static Texture scopeButton;
    public static Texture scopeButtonHover;
    public static Texture scopeButtonPressed;
    public static Texture killBadge;

    public static Texture reloadReceiverTexture;
    public static Texture reloadMuzzleTexture;
    public static TextureRegion[] reloadReceiverEnter;
    public static TextureRegion[] reloadReceiverService;
    public static TextureRegion[] reloadReceiverReady;
    public static TextureRegion[] reloadReceiverCancel;
    public static TextureRegion[] reloadMuzzleEnter;
    public static TextureRegion[] reloadMuzzleService;
    public static TextureRegion[] reloadMuzzleReady;
    public static TextureRegion[] reloadMuzzleCancel;

    public static Texture runSummaryShell;
    public static Texture runRestartNormal;
    public static Texture runRestartHover;
    public static Texture runRestartPressed;
    public static Texture runArmoryNormal;
    public static Texture runArmoryHover;
    public static Texture runArmoryPressed;
    public static Texture runMainMenuNormal;
    public static Texture runMainMenuHover;
    public static Texture runMainMenuPressed;


    public static Texture mainMenuShell;
    public static Texture mainStartNormal;
    public static Texture mainStartHover;
    public static Texture mainStartPressed;
    public static Texture mainStartDisabled;
    public static Texture mainArmoryNormal;
    public static Texture mainArmoryHover;
    public static Texture mainArmoryPressed;
    public static Texture mainArmoryDisabled;
    public static Texture mainSettingsNormal;
    public static Texture mainSettingsHover;
    public static Texture mainSettingsPressed;
    public static Texture mainSettingsDisabled;
    public static Texture mainExitNormal;
    public static Texture mainExitHover;
    public static Texture mainExitPressed;
    public static Texture mainExitDisabled;

    public static Texture armoryShell;
    public static Texture armoryWeaponsGrid;
    public static Texture armoryCloseNormal;
    public static Texture armoryCloseHover;
    public static Texture armoryClosePressed;
    public static Texture armoryCategoryHover;
    public static Texture armoryCategorySelected;
    public static Texture armoryWeaponCardHover;
    public static Texture armoryWeaponCardSelected;
    public static Texture armoryActionUnavailable;


    public static Texture shopPanel;
    public static Texture shopActionBuy;
    public static Texture shopActionBuyHover;
    public static Texture shopActionBuyPressed;
    public static Texture shopActionOwned;
    public static Texture shopActionEquipped;
    public static Texture shopActionNotEnoughGold;
    public static Texture shopCardHover;
    public static Texture shopCardSelected;
    public static Texture shopCategoryHover;
    public static Texture shopCategorySelected;
    public static Texture shopCloseNormal;
    public static Texture shopCloseHover;
    public static Texture shopClosePressed;

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

        scopeButton = new Texture(AssetPaths.Hud.SCOPE_BUTTON);
        scopeButtonHover = new Texture(AssetPaths.Hud.SCOPE_BUTTON_HOVER);
        scopeButtonPressed = new Texture(AssetPaths.Hud.SCOPE_BUTTON_PRESSED);
        killBadge = new Texture(AssetPaths.Hud.KILL_BADGE);

        shopIcon = new Texture(AssetPaths.Shop.ICON);
        keyIcon = new Texture(AssetPaths.Hud.KEY_ICON);
        shopIcon.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        keyIcon.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);

        reloadReceiverTexture = new Texture(AssetPaths.FX.RELOAD_RECEIVER);
        reloadMuzzleTexture = new Texture(AssetPaths.FX.RELOAD_MUZZLE);

        reloadReceiverTexture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        reloadMuzzleTexture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);

        reloadReceiverEnter = loadReloadAtlasFrames(reloadReceiverTexture,0,6,384,224,388,228);
        reloadReceiverService = loadReloadAtlasFrames(reloadReceiverTexture,6,37,384,224,388,228);
        reloadReceiverReady = loadReloadAtlasFrames(reloadReceiverTexture,43,8,384,224,388,228);
        reloadReceiverCancel = loadReloadAtlasFrames(reloadReceiverTexture,51,5,384,224,388,228);

        reloadMuzzleEnter = loadReloadAtlasFrames(reloadMuzzleTexture,0,6,64,96,68,100);
        reloadMuzzleService = loadReloadAtlasFrames(reloadMuzzleTexture,6,37,64,96,68,100);
        reloadMuzzleReady = loadReloadAtlasFrames(reloadMuzzleTexture,43,8,64,96,68,100);
        reloadMuzzleCancel = loadReloadAtlasFrames(reloadMuzzleTexture,51,5,64,96,68,100);

        pauseShell = loadUiTexture(AssetPaths.Pause.SHELL);
        pauseResumeNormal = loadUiTexture(AssetPaths.Pause.RESUME_NORMAL);
        pauseResumeHover = loadUiTexture(AssetPaths.Pause.RESUME_HOVER);
        pauseResumePressed = loadUiTexture(AssetPaths.Pause.RESUME_PRESSED);
        pauseResumeDisabled = loadUiTexture(AssetPaths.Pause.RESUME_DISABLED);
        pauseRestartNormal = loadUiTexture(AssetPaths.Pause.RESTART_NORMAL);
        pauseRestartHover = loadUiTexture(AssetPaths.Pause.RESTART_HOVER);
        pauseRestartPressed = loadUiTexture(AssetPaths.Pause.RESTART_PRESSED);
        pauseRestartDisabled = loadUiTexture(AssetPaths.Pause.RESTART_DISABLED);
        pauseSettingsNormal = loadUiTexture(AssetPaths.Pause.SETTINGS_NORMAL);
        pauseSettingsHover = loadUiTexture(AssetPaths.Pause.SETTINGS_HOVER);
        pauseSettingsPressed = loadUiTexture(AssetPaths.Pause.SETTINGS_PRESSED);
        pauseSettingsDisabled = loadUiTexture(AssetPaths.Pause.SETTINGS_DISABLED);
        pauseMainMenuNormal = loadUiTexture(AssetPaths.Pause.MAIN_MENU_NORMAL);
        pauseMainMenuHover = loadUiTexture(AssetPaths.Pause.MAIN_MENU_HOVER);
        pauseMainMenuPressed = loadUiTexture(AssetPaths.Pause.MAIN_MENU_PRESSED);
        pauseMainMenuDisabled = loadUiTexture(AssetPaths.Pause.MAIN_MENU_DISABLED);


        runSummaryShell = loadUiTexture(AssetPaths.RunSummary.SHELL);
        runRestartNormal = loadUiTexture(AssetPaths.RunSummary.RESTART_NORMAL);
        runRestartHover = loadUiTexture(AssetPaths.RunSummary.RESTART_HOVER);
        runRestartPressed = loadUiTexture(AssetPaths.RunSummary.RESTART_PRESSED);
        runArmoryNormal = loadUiTexture(AssetPaths.RunSummary.ARMORY_NORMAL);
        runArmoryHover = loadUiTexture(AssetPaths.RunSummary.ARMORY_HOVER);
        runArmoryPressed = loadUiTexture(AssetPaths.RunSummary.ARMORY_PRESSED);
        runMainMenuNormal = loadUiTexture(AssetPaths.RunSummary.MAIN_MENU_NORMAL);
        runMainMenuHover = loadUiTexture(AssetPaths.RunSummary.MAIN_MENU_HOVER);
        runMainMenuPressed = loadUiTexture(AssetPaths.RunSummary.MAIN_MENU_PRESSED);

        mainMenuShell = loadUiTexture(AssetPaths.MainMenu.SHELL);
        mainStartNormal = loadUiTexture(AssetPaths.MainMenu.START_NORMAL);
        mainStartHover = loadUiTexture(AssetPaths.MainMenu.START_HOVER);
        mainStartPressed = loadUiTexture(AssetPaths.MainMenu.START_PRESSED);
        mainStartDisabled = loadUiTexture(AssetPaths.MainMenu.START_DISABLED);
        mainArmoryNormal = loadUiTexture(AssetPaths.MainMenu.ARMORY_NORMAL);
        mainArmoryHover = loadUiTexture(AssetPaths.MainMenu.ARMORY_HOVER);
        mainArmoryPressed = loadUiTexture(AssetPaths.MainMenu.ARMORY_PRESSED);
        mainArmoryDisabled = loadUiTexture(AssetPaths.MainMenu.ARMORY_DISABLED);
        mainSettingsNormal = loadUiTexture(AssetPaths.MainMenu.SETTINGS_NORMAL);
        mainSettingsHover = loadUiTexture(AssetPaths.MainMenu.SETTINGS_HOVER);
        mainSettingsPressed = loadUiTexture(AssetPaths.MainMenu.SETTINGS_PRESSED);
        mainSettingsDisabled = loadUiTexture(AssetPaths.MainMenu.SETTINGS_DISABLED);
        mainExitNormal = loadUiTexture(AssetPaths.MainMenu.EXIT_NORMAL);
        mainExitHover = loadUiTexture(AssetPaths.MainMenu.EXIT_HOVER);
        mainExitPressed = loadUiTexture(AssetPaths.MainMenu.EXIT_PRESSED);
        mainExitDisabled = loadUiTexture(AssetPaths.MainMenu.EXIT_DISABLED);

        armoryShell = loadUiTexture(AssetPaths.Armory.SHELL);
        armoryWeaponsGrid = loadUiTexture(AssetPaths.Armory.WEAPONS_GRID);
        armoryCloseNormal = loadUiTexture(AssetPaths.Armory.CLOSE_NORMAL);
        armoryCloseHover = loadUiTexture(AssetPaths.Armory.CLOSE_HOVER);
        armoryClosePressed = loadUiTexture(AssetPaths.Armory.CLOSE_PRESSED);
        armoryCategoryHover = loadUiTexture(AssetPaths.Armory.CATEGORY_HOVER);
        armoryCategorySelected = loadUiTexture(AssetPaths.Armory.CATEGORY_SELECTED);
        armoryWeaponCardHover = loadUiTexture(AssetPaths.Armory.WEAPON_CARD_HOVER);
        armoryWeaponCardSelected = loadUiTexture(AssetPaths.Armory.WEAPON_CARD_SELECTED);
        armoryActionUnavailable = loadUiTexture(AssetPaths.Armory.ACTION_UNAVAILABLE);

        shopPanel = loadUiTexture(AssetPaths.Shop.PANEL);
        shopActionBuy = loadUiTexture(AssetPaths.Shop.ACTION_BUY);
        shopActionBuyHover = loadUiTexture(AssetPaths.Shop.ACTION_BUY_HOVER);
        shopActionBuyPressed = loadUiTexture(AssetPaths.Shop.ACTION_BUY_PRESSED);
        shopActionOwned = loadUiTexture(AssetPaths.Shop.ACTION_OWNED);
        shopActionEquipped = loadUiTexture(AssetPaths.Shop.ACTION_EQUIPPED);
        shopActionNotEnoughGold = loadUiTexture(AssetPaths.Shop.ACTION_NOT_ENOUGH_GOLD);
        shopCardHover = loadUiTexture(AssetPaths.Shop.CARD_HOVER);
        shopCardSelected = loadUiTexture(AssetPaths.Shop.CARD_SELECTED);
        shopCategoryHover = loadUiTexture(AssetPaths.Shop.CATEGORY_HOVER);
        shopCategorySelected = loadUiTexture(AssetPaths.Shop.CATEGORY_SELECTED);
        shopCloseNormal = loadUiTexture(AssetPaths.Shop.CLOSE_NORMAL);
        shopCloseHover = loadUiTexture(AssetPaths.Shop.CLOSE_HOVER);
        shopClosePressed = loadUiTexture(AssetPaths.Shop.CLOSE_PRESSED);
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
        scopeButton.dispose();
        scopeButtonHover.dispose();
        scopeButtonPressed.dispose();
        killBadge.dispose();
        shopIcon.dispose();
        keyIcon.dispose();
        reloadReceiverTexture.dispose();
        reloadMuzzleTexture.dispose();

        pauseShell.dispose();
        pauseResumeNormal.dispose();
        pauseResumeHover.dispose();
        pauseResumePressed.dispose();
        pauseResumeDisabled.dispose();
        pauseRestartNormal.dispose();
        pauseRestartHover.dispose();
        pauseRestartPressed.dispose();
        pauseRestartDisabled.dispose();
        pauseSettingsNormal.dispose();
        pauseSettingsHover.dispose();
        pauseSettingsPressed.dispose();
        pauseSettingsDisabled.dispose();
        pauseMainMenuNormal.dispose();
        pauseMainMenuHover.dispose();
        pauseMainMenuPressed.dispose();
        pauseMainMenuDisabled.dispose();

        shopPanel.dispose();
        shopActionBuy.dispose();
        shopActionBuyHover.dispose();
        shopActionBuyPressed.dispose();
        shopActionOwned.dispose();
        shopActionEquipped.dispose();
        shopActionNotEnoughGold.dispose();
        shopCardHover.dispose();
        shopCardSelected.dispose();
        shopCategoryHover.dispose();
        shopCategorySelected.dispose();
        shopCloseNormal.dispose();
        shopCloseHover.dispose();
        shopClosePressed.dispose();
    }


    private static TextureRegion[] loadReloadAtlasFrames(Texture texture,int start,int count,int frameWidth,int frameHeight,int pitchX,int pitchY){
        TextureRegion[] frames = new TextureRegion[count];
        for(int i=0;i<count;i++){
            int frame = start+i;
            int column = frame%8;
            int row = frame/8;

            int x = 2+column*pitchX;
            int y = 2+row*pitchY;

            frames[i] = new TextureRegion(texture,x,y,frameWidth,frameHeight);
        }
        return frames;
    }

    private static Texture loadUiTexture(String path){
        Texture texture = new Texture(path);
        texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        return texture;
    }
}