package rojan.shooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import rojan.shooter.Enemy.EnemyType;
import rojan.shooter.LootCrate.CrateType;
import rojan.shooter.Weapon.WeaponType;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Align;

//./gradlew lwjgl3:run

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ShooterGame extends ApplicationAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Player player;
    private SpriteBatch batch;
    private Array<Rectangle> solidPlatforms;
    private Array<TiledMapTileMapObject> terrain;

    private Weapon weapon;
    private Array<Bullet> bullets;

    private float gravity = -1200.f;
    private float BASE_JETPACK_THRUST = 2200.f;
    private float jetpackThrust= BASE_JETPACK_THRUST;

    private ObjectMap<WeaponType,Texture> bulletTextures;

    private float horizontalHoldTime = 0f;
    private final float RUN_THRESHOLD = 0.30f;

    private float mouseHeld = 0f;

    private Array<Enemy> enemies;
    private ShapeRenderer shapeRenderer;

    private OrthographicCamera hudCamera;
    private Array<EnemyProjectile> enemyProjectiles = new Array<>();

    private Texture riflemanProjectileTexture;
    private Texture heavyProjectileTexture;

    private float poisonStatusFxTimer = 0f;
    private boolean poisonStatusWasActive = false;
    private boolean poisonStatusEnding = false;

    private float stunStatusFxTimer = 0f;
    private boolean stunStatusWasActive = false;
    private boolean stunStatusEnding = false;
    private BitmapFont hudFont;
    private Rectangle reloadButtonBounds = new Rectangle();
    private Rectangle scopeButtonBounds = new Rectangle();

    private int riflemanCount = 0;
    private int heavyCount = 0;
    private int drillerCount = 0;
    private int droneCount = 0;

    private LootCrate activeLootCrate;

    private final int WEAPON_INITIAL_CAPACITY =5;

    private static class BombardmentImpactFx{
        Vector2 position;
        float timer;
        BombardmentImpactFx(Vector2 position){
            this.position = new Vector2(position);
            timer = 0;
        }
    }
    private Array<BombardmentImpactFx> bombardmentImpactFx = new Array<>();

    private  int MAX_RIFLEMEN = 3;
    private  int MAX_DRILLERS = 3;
    private  int MAX_DRONES = 3;
    private  int MAX_HEAVIES = 2;

    private int MAX_ACTIVE_ENEMIES = 6;

    private float RIFLEMAN_RESPAWN_TIME = 12f;
    private float HEAVY_RESPAWN_TIME = 28f;
    private float DRILLER_RESPAWN_TIME = 22f;
    private float DRONE_RESPAWN_TIME = 17f;

    private float RIFLEMAN_RESPAWN_TIMER = 0f;
    private float HEAVY_RESPAWN_TIMER = 0f;
    private float DRILLER_RESPAWN_TIMER = 0f;
    private float DRONE_RESPAWN_TIMER = 0f;

    private float difficulty = 0;
    private boolean easyMode = true;
    private boolean mediumMode = false;
    private boolean hardMode = false;
    private boolean impossibleMode;

    private Array<Rectangle> groundEnemySpawnPoints;
    private Array<Rectangle> drillerSpawnPoints;
    private Array<LootCrate> lootCrates;

    private Inventory inventory;
    private RunState runState;
    private SaveManager saveManager;
    private PlayerProfile playerProfile;

    private Array<Rectangle> specialCrateSpawnPoints;
    private float SPECIAL_CRATE_TIMER = 0f;
    //private float SPECIAL_CRATE_SPAWN_TIME = MathUtils.random(45f,75f);
    private float SPECIAL_CRATE_SPAWN_TIME = MathUtils.random(1f,10f);
    private final float crateWidth = 75f;
    private final float crateHeight = 60f;
    private BitmapFont lootFont;
    private BitmapFont lootCountFont;
    private final GlyphLayout lootValueLayout = new GlyphLayout();
    private final ObjectMap<Texture,TextureRegion> lootIconRegions = new ObjectMap<>();
    private LootCrate.Contents pendingLoot;

    private boolean FIRE_RATE_ACTIVE = false;
    private float FIRE_RATE_TIMER = 0f;
    private final float FIRE_RATE_DURATION = 5f;
    private final float FIRE_RATE_MULTIPLIER = 2f;

    private boolean SHIELD_ACTIVE = false;
    private float SHIELD_AMOUNT = 0f;
    private final float SHIELD_CAPACITY = 60f;

    private boolean OVERDRIVE_ACTIVE = false;
    private float OVERDRIVE_TIMER = 0f;
    private final float OVERDRIVE_DURATION = 12f;
    private final float OVERDRIVE_DAMAGE_MULTIPLIER = 1.4f;
    private final float OVERDRIVE_FIRE_RATE_MULTIPLIER = 1.4f;

    private boolean RAPID_MOBILITY_ACTIVE = false;
    private float RAPID_MOBILITY_TIMER = 0f;
    private final float RAPID_MOBILITY_DURATION = 12f;
    private final float RAPID_MOBILITY_SPEED_MULTIPLIER = 1.5f;
    private final float RAPID_MOBILITY_JETPACK_MULTIPLIER = 1.5f;

    private boolean BOTTOMLESS_MAG_ACTIVE = false;
    private float BOTTOMLESS_MAG_TIMER = 0f;
    private final float BOTTOMLESS_MAG_DURATION = 10f;

    private boolean DAMAGE_SURGE_ACTIVE = false;
    private float DAMAGE_SURGE_TIMER = 0f;
    private final float DAMAGE_SURGE_DURATION = 10f;
    private final float DAMAGE_SURGE_MULTIPLIER = 2f;

    private boolean GOLD_RUSH_ACTIVE = false;
    private float GOLD_RUSH_TIMER = 0f;
    private final float GOLD_RUSH_DURATION = 15f;
    private final float GOLD_RUSH_MULTIPLIER = 2f;

    private boolean SCORE_FRENZY_ACTIVE = false;
    private float SCORE_FRENZY_TIMER = 0f;
    private final float SCORE_FRENZY_DURATION = 15f;
    private final float SCORE_FRENZY_MULTIPLIER = 2f;

    private boolean BERSERK_ACTIVE = false;
    private float BERSERK_TIMER = 0f;
    private final float BERSERK_DURATION = 12f;
    private final float BERSERK_DAMAGE_MULTIPLIER = 2f;
    private final float BERSERK_FIRE_RATE_MULTIPLIER = 1.6f;
    private final float BERSERK_INCOMING_DAMAGE_MULTIPLIER = 1.25f;

    private boolean INFINITE_ASSAULT_ACTIVE = false;
    private float INFINITE_ASSAULT_TIMER = 0f;
    private final float INFINITE_ASSAULT_DURATION = 15f;
    private final float INFINITE_ASSAULT_FIRE_RATE_MULTIPLIER = 1.4f;

    private boolean HYPERDRIVE_ACTIVE = false;
    private float HYPERDRIVE_TIMER = 0f;
    private final float HYPERDRIVE_DURATION = 12f;
    private final float HYPERDRIVE_SPEED_MULTIPLIER = 1.75f;

    private boolean FORTRESS_ACTIVE = false;
    private float FORTRESS_TIMER = 0f;
    private final float FORTRESS_DURATION = 15f;
    private final float FORTRESS_SHIELD = 100f;
    private final float FORTRESS_DAMAGE_REDUCTION = 0.50f;

    private boolean DOUBLE_FORTUNE_ACTIVE = false;
    private float DOUBLE_FORTUNE_TIMER = 0f;
    private final float DOUBLE_FORTUNE_DURATION = 20f;
    private final float DOUBLE_FORTUNE_GOLD_MULTIPLIER = 2f;
    private final float DOUBLE_FORTUNE_SCORE_MULTIPLIER = 2f;

    private boolean GOD_MODE_ACTIVE = false;
    private float GOD_MODE_TIMER = 0f;
    private final float GOD_MODE_DURATION = 8f;
    private final float GOD_MODE_DAMAGE_MULTIPLIER = 2f;
    private final float GOD_MODE_FIRE_RATE_MULTIPLIER = 2f;

    private boolean inventoryOpen = false;
    private Rectangle inventoryBagBounds = new Rectangle();
    private float inventoryAnimation = 0f;
    private final float INVENTORY_ANIMATION_SPEED = 4f;

    private enum InventorySelectionType{
                        NONE,
                        POWERUP,
                        WEAPON
                    }
    private InventorySelectionType inventorySelectionType = InventorySelectionType.NONE;
    private Weapon.WeaponType selectedWeapon = null;
    private Powerup.PowerupType selectedPowerup = null;
    private Powerup.SpicyType selectedSpicy = null;
    private Powerup.MoreSpicyType selectedMoreSpicy = null;
    private boolean selectedGodMode = false;

    private float POWER_AURA_VISUAL_TIMER = 0f;
    private String POWER_AURA_BURST = null;
    private float POWER_AURA_BURST_TIMER = 0f;

    private Rectangle bulletSweepRect = new Rectangle();

    private static class LootVisual{
        Texture icon;
        String value;

        LootVisual(Texture icon,String text){
            this.icon = icon;
            this.value = text;
        }
    }
    private static final float[][] SLOT_3 = {{0.10046729f,0.33088235f,0.1728972f,0.5441176f},{0.4135514f,0.33088235f,0.1728972f,0.5441176f},{0.7266355f,0.33088235f,0.1728972f,0.5441176f}};
    private Rectangle[] inventoryWeaponBounds = {new Rectangle(),new Rectangle(),new Rectangle(),new Rectangle(),new Rectangle(),new Rectangle()};
    private final Weapon.WeaponType[] INVENTORY_WEAPONS = {
                                    Weapon.WeaponType.PISTOL,
                                    Weapon.WeaponType.AR,
                                    Weapon.WeaponType.SMG,
                                    Weapon.WeaponType.SHOTGUN,
                                    Weapon.WeaponType.SNIPER,
                                    Weapon.WeaponType.ROCKET_LAUNCHER
                                };

    private Rectangle[] inventoryConsumableBounds = {
        new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(),
        new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(),
        new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle(),
        new Rectangle(), new Rectangle(), new Rectangle(), new Rectangle()
    };

    private final Powerup.PowerupType[] INVENTORY_NORMAL_POWERUPS = {
        Powerup.PowerupType.HEALTH,
        Powerup.PowerupType.SHIELD,
        Powerup.PowerupType.JETPACK,
        Powerup.PowerupType.FIRE_RATE
    };

    private final Powerup.SpicyType[] INVENTORY_SPICY_POWERUPS = {
        Powerup.SpicyType.OVERDRIVE,
        Powerup.SpicyType.RAPID_MOBILITY,
        Powerup.SpicyType.BOTTOMLESS_MAG,
        Powerup.SpicyType.DAMAGE_SURGE,
        Powerup.SpicyType.GOLD_RUSH,
        Powerup.SpicyType.SCORE_FRENZY
    };

    private final Powerup.MoreSpicyType[] INVENTORY_MORE_SPICY_POWERUPS = {
        Powerup.MoreSpicyType.BERSERK,
        Powerup.MoreSpicyType.INFINITE_ASSAULT,
        Powerup.MoreSpicyType.HYPERDRIVE,
        Powerup.MoreSpicyType.FORTRESS,
        Powerup.MoreSpicyType.DOUBLE_FORTUNE
    };
    private Rectangle inventoryActionBounds = new Rectangle();
    private Rectangle inventoryCloseBounds = new Rectangle();

    private BitmapFont inventoryTitleFont;
    private BitmapFont inventoryBodyFont;
    private BitmapFont inventoryInfoFont;
    private BitmapFont inventoryButtonFont;
    private GlyphLayout inventoryGlyphLayout;

    private static class RocketExplosionFx{
            Vector2 position;
            float timer;
            boolean ground;
            RocketExplosionFx(Vector2 position,boolean ground){
                this.position = new Vector2(position);
                this.ground = ground;
                timer = 0f;
            }
        }
    private Array<RocketExplosionFx>rocketExplosionFx = new Array<>();
    private final float ROCKET_EXPLOSION_RADIUS = 150f;
    private final float ROCKET_EXPLOSION_DAMAGE_MULTIPLIER = 5f;
    private final float ROCKET_EXPLOSION_MIN_MULTIPLIER = 0.40f;

    private int scopeStage = 0;
    private final int SCOPE_STAGE_COUNT = 4;

    private float targetCameraZoom = 1f;
    private final float SCOPE_ZOOM_SPEED = 6f;
    private final GlyphLayout scopeValueLayout = new GlyphLayout();
    
    private Rectangle shop;
    private final float shopIconWidth = 120f;
    private final float shopIconHeight = 120f;

    private boolean reloading = false;
    private float reloadTimer = 0f;

    private boolean pauseMenuOpen = false;
    private boolean reloadReadyFxActive = false;
    private float reloadReadyFxTimer = 0f;

    private final float RELOAD_ENTER_DURATION = 0.10f;
    private final float RELOAD_READY_DURATION = 0.12f;
    private final int RELOAD_PROGRESS_SEGMENTS = 6;
    private Texture reloadProgressPixel;
    private TextureRegion reloadProgressPixelRegion;

    private boolean gameOver = false;
    private boolean reloadInsertPlayed = false;

    private Rectangle pauseResumeBounds = new Rectangle();
    private Rectangle pauseRestartBounds = new Rectangle();
    private Rectangle pauseSettingsBounds = new Rectangle();
    private Rectangle pauseMainMenuBounds = new Rectangle();
    private Rectangle runRestartBounds = new Rectangle();
    private Rectangle runArmoryBounds = new Rectangle();
    private Rectangle runMainMenuBounds = new Rectangle();

    private Rectangle mainStartBounds = new Rectangle();
    private Rectangle mainArmoryBounds = new Rectangle();
    private Rectangle mainSettingsBounds = new Rectangle();
    private Rectangle mainExitBounds = new Rectangle();
    private GlyphLayout mainMenuLayout = new GlyphLayout();

    private Rectangle armoryCloseBounds = new Rectangle();
    private Rectangle armoryWeaponsCategoryBounds = new Rectangle();
    private Rectangle armoryPowerupsCategoryBounds = new Rectangle();
    private Rectangle armorySpecialCategoryBounds = new Rectangle();
    private Rectangle[] armoryWeaponBounds = new Rectangle[6];
    
    private BitmapFont runSummaryFont;
    private GlyphLayout runSummaryLayout = new GlyphLayout();
    private BitmapFont fieldShopTitleFont;
    private BitmapFont fieldShopBodyFont;
    private BitmapFont fieldShopInfoFont;

    private enum GameScreen{
                    MAIN_MENU,
                    GAMEPLAY,
                    ARMORY,
                    SETTINGS
                }
    private GameScreen currentScreen = GameScreen.MAIN_MENU;



    private Rectangle shopInteractionZone;
    private boolean fieldShopOpen = false;
    private Rectangle fieldShopCloseBounds = new Rectangle();
    private Rectangle[] fieldShopPowerupBounds = new Rectangle[16];
    private Rectangle[] fieldShopWeaponProductBounds = new Rectangle[6];
    private Rectangle[] fieldShopAmmoProductBounds = new Rectangle[6];
    private Rectangle[] fieldShopSpecialProductBounds = new Rectangle[6];
    private int selectedFieldShopProduct = -1;

    private Rectangle fieldShopWeaponsBounds = new Rectangle();
    private Rectangle fieldShopAmmoBounds = new Rectangle();
    private Rectangle fieldShopPowerupsBounds = new Rectangle();
    private Rectangle fieldShopSpecialBounds = new Rectangle();
    private enum FieldShopCategory{
                        WEAPONS,
                        AMMO,
                        POWERUPS,
                        SPECIAL
                    }
    private FieldShopCategory selectedFieldShopCategory = FieldShopCategory.POWERUPS;
    private final WeaponType[] FIELD_SHOP_WEAPONS = {
                                WeaponType.PISTOL,
                                WeaponType.AR,
                                WeaponType.SMG,
                                WeaponType.SHOTGUN,
                                WeaponType.SNIPER,
                                WeaponType.ROCKET_LAUNCHER};
    private final WeaponType[] FIELD_SHOP_AMMO = {
                                WeaponType.PISTOL,
                                WeaponType.AR,
                                WeaponType.SMG,
                                WeaponType.SHOTGUN,
                                WeaponType.SNIPER,
                                WeaponType.ROCKET_LAUNCHER};
    private Rectangle fieldShopActionBounds = new Rectangle();
    private enum ArmoryCategory{
                        WEAPONS,
                        POWERUPS,
                        SPECIAL
                    }
private ArmoryCategory selectedArmoryCategory = ArmoryCategory.WEAPONS;
private int selectedArmoryProduct = -1;
private Rectangle[] armoryIconBounds = new Rectangle[16];
private Rectangle armoryActionBounds = new Rectangle();

private float musicVolume = 1f;
private float sfxVolume = 1f;
private boolean draggingMusicSlider = false;
private boolean draggingSfxSlider = false;
private Rectangle musicSliderBounds = new Rectangle();
private Rectangle sfxSliderBounds = new Rectangle();
private Rectangle settingsFullscreenBounds = new Rectangle();
private Rectangle settingsResolutionBounds = new Rectangle();
private Rectangle settingsControlsBounds = new Rectangle();
private Rectangle settingsResetBounds = new Rectangle();
private Rectangle settingsBackBounds = new Rectangle();
private int settingsResolutionIndex = 0;
private final int[][] SETTINGS_RESOLUTIONS = {{1920,1080},{1600,900},{1280,720}};

private boolean controlsOverlayOpen = false;
private boolean resetConfirmOpen = false;
private Rectangle controlsOverlayBackBounds = new Rectangle();
private Rectangle resetConfirmYesBounds = new Rectangle();
private Rectangle resetConfirmNoBounds = new Rectangle();

private boolean settingsOpenedFromPause = false;
    
        
    

    @Override
    public void create(){
        loadFonts();
        initializeGame();
        loadPlayerProfile();

       // giveTestWeapons();
        AudioAssets.playMenuMusic();
    }
    

    @Override
    public void render(){
        float dt = Gdx.graphics.getDeltaTime();
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

        switch(currentScreen){
            case MAIN_MENU:
                renderMainMenu(mouseX,mouseY);
                return;

            case ARMORY:
                renderArmory(mouseX,mouseY);
                return;

            case SETTINGS:
                renderSettings();
                return;

            case GAMEPLAY:
                break;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)&& !gameOver){
            if(inventoryOpen || inventoryAnimation > 0f){
                inventoryOpen = false;
                AudioAssets.playUiPanelClose();
            }
            else{
                pauseMenuOpen =! pauseMenuOpen;
                if(pauseMenuOpen){
                    AudioAssets.playUiPanelOpen();
                    AudioAssets.pauseMusic();
                }
                else{
                    AudioAssets.playUiPanelClose();
                    AudioAssets.resumeMusic();
                }

            }
        }

        if(Gdx.input.justTouched()){
            if(gameOver)
                handleRunSummaryClick(mouseX,mouseY);
            else if(pauseMenuOpen)
                handlePauseClick(mouseX,mouseY);
            else if(fieldShopOpen)
                fieldShopMouseClick(mouseX, mouseY);
            else{
                if(inventoryOpen && inventoryAnimation >= 0.99f && inventoryCloseBounds.contains(mouseX,mouseY))
                    inventoryOpen = false;
                else if(inventoryBagBounds.contains(mouseX,mouseY)){
                    inventoryOpen = !inventoryOpen;
                    if(inventoryOpen)
                        AudioAssets.playUiPanelOpen();
                    else
                        AudioAssets.playUiPanelClose();
                }
            
                else if(inventoryOpen && inventoryAnimation >= 1f){
                    if(inventoryActionBounds.contains(mouseX,mouseY))
                        useInventoryAction();
                    else{
                        checkInventoryWeaponSelection(mouseX,mouseY);
                        checkInventoryConsumableSelection(mouseX,mouseY);
                    }
                }
            }
        }
        if(currentScreen != GameScreen.GAMEPLAY)
                return;

        if(Gdx.input.isKeyJustPressed(Input.Keys.E) && (player.getWorldHitbox()).overlaps(shopInteractionZone) && !pauseMenuOpen &&!gameOver &&!inventoryOpen)
            fieldShopOpen = true;
        
        updateInventoryAnimation(dt);
        boolean gamePaused = gameplayBlocked();
        float worldDt = gamePaused ? 0f : dt;
        Vector2 mouseWorldPosition = getMouseWorldPosition();
        if(!gamePaused){
            updatePowerups(dt);
            updateScope(dt);
            clampCameraToPlayer();
            activateGravity(dt);
            Vector2 direction = new Vector2(0,0);
            boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
            if(movingRight)
                direction.x++;
            if(movingLeft)
                direction.x--;

            boolean movingHorizontally = movingRight!= movingLeft;
            if(movingHorizontally)
                horizontalHoldTime+=dt;
            else
                horizontalHoldTime = 0;

            if(horizontalHoldTime > RUN_THRESHOLD)
                player.setRunningTrue();
            else
                player.setRunningFalse();
            
            boolean jumpJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP);
            boolean jumpHeld = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
            if(jumpJustPressed){
                if(player.getCrouchingState())
                    player.stand(solidPlatforms);
                if(!player.getCrouchingState() && player.jumpsAvailable())
                    player.jump();
            }
            if(jumpHeld)
                JumpVSFlight(dt);
            else{
                player.setAirTime(0f);
                player.stopJetpack();
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.K))
                restartGame();

            player.move(direction,dt,getWorldSize(),solidPlatforms);
            player.ensureGrounding(solidPlatforms);

            float hudMouseX = Gdx.input.getX();
            float hudMouseY = Gdx.graphics.getHeight()-Gdx.input.getY();
            boolean mouseOverHud = mouseOverGameplayHud(hudMouseX,hudMouseY);
            boolean shooting = Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !mouseOverHud;
            boolean shootOnce = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !mouseOverHud;

            float fireInterval = 1f/weapon.getFireRate();
            if(shooting && !reloading) 
                player.setShootingTrue();
            else
                player.setShootingFalse();

            if(!reloading && shootOnce  && (weapon.canShoot() || infiniteMagazineActive())){
                createBulletWhenFired(mouseWorldPosition);
                if(!infiniteMagazineActive()){
                    weapon.consumeAmmo();
                    if(weapon.getCurrentAmmo() <= 0)
                        reloadGun();
                }
                mouseHeld = 0f;
            }
            else if(!reloading && shooting && weapon.isAutomatic() && (weapon.canShoot() || infiniteMagazineActive())){
                mouseHeld+=dt;
                if(mouseHeld >= fireInterval){
                    createBulletWhenFired(mouseWorldPosition);
                    if(!infiniteMagazineActive()){
                        weapon.consumeAmmo();
                        if(weapon.getCurrentAmmo() <= 0)
                            reloadGun();
                    }
                    mouseHeld-=fireInterval;
                }
            }
            else
                mouseHeld = 0f;
            if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
                if(!player.getCrouchingState())
                    player.crouch();
                else
                    player.stand(solidPlatforms);   
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))
                reloadGun();

            if(Gdx.input.justTouched()){
                float mouseXa = Gdx.input.getX();
                float mouseYa = Gdx.graphics.getHeight()-Gdx.input.getY();

                if(reloadButtonBounds.contains(mouseXa,mouseYa))
                    reloadGun();
                 else if(scopeButtonBounds.contains(mouseXa,mouseYa))
                    cycleScopeStage();    
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.E))
                openNearbyCrate();
            

            if(Gdx.input.isKeyJustPressed(Input.Keys.Q))
                claimActiveCrate();

            for(Bullet bullet : bullets)
               bullet.update(dt);
            bulletPlatformCollision();
            updateReload(dt);
            updateReloadFx(dt);
        }
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        mapRenderer.render();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for(TiledMapTileMapObject terrainObject : terrain){
            TextureRegion region = terrainObject.getTextureRegion();
            float x = terrainObject.getX();
            float y = terrainObject.getY();

            float width = region.getRegionWidth();
            float height = region.getRegionHeight();

            float originX = terrainObject.getOriginX();
            float originY = terrainObject.getOriginY();

            float scaleX = terrainObject.getScaleX();
            float scaleY = terrainObject.getScaleY();

            float rotation = -terrainObject.getRotation();

            batch.draw(region,x,y,originX,originY,width,height,scaleX,scaleY,rotation);
        }

        drawDroneBeam();
        drawPowerAura(false);
        drawShop();
        player.drawPlayer(batch,worldDt);
        for(Enemy enemy: enemies)
            enemy.drawEnemy(batch, worldDt, player.getPlayerPosition(),solidPlatforms,player.getWorldHitbox(),player.getVelocity(),getHeavyPortalBounds());  

        if(!gamePaused){
            if(player.getJetPackStatus()){
                if(!infiniteJetpackActive())
                    player.consumeFuel(dt);
            }
            else{
                player.rechargeFuel(dt);
            }
            if(isPlayerStunned()){
                player.emptyJetpack();
                player.stopJetpack();
            }
        }
        weapon.determineWeaponDrawLocation(player.getCurrentGripWorldPosition());
        if(!gamePaused  && !reloading){
            updatePlayerFacingDirection(mouseWorldPosition); 
            weapon.setFacingLeft(player.isFacingLeft());
            weapon.rotateWeapon(player.getCurrentGripWorldPosition(), mouseWorldPosition);
        }
        Vector2 grip = player.getCurrentGripWorldPosition();
        weapon.drawWeapon(batch, grip);
        drawReloadLock(grip);
        drawPowerAura(true);
        drawPlayerPoisonFx(worldDt);
        drawPlayerStunFx(worldDt);
        drawRocketExplosions(worldDt);

        for(Bullet bullet : bullets)
            bullet.draw(batch);

        if(!gamePaused){
            createEnemyProjectile();
            for(EnemyProjectile projectile : enemyProjectiles)
                projectile.update(dt);
            for(EnemyProjectile projectile : enemyProjectiles){
                projectile.draw(batch);
            }
        }
        if(!gamePaused)
            spawnSpecialCrate(dt);
        for(LootCrate crate : lootCrates){
            if(!gamePaused)
                crate.update(dt);
            crate.drawCrate(batch,dt);
        }
        if(!gamePaused)
            removeLootCrates();
        drawLootDashboard();
        if(!gamePaused){
            playerEnemyProjectileCollisionChecker();
            removeEnemyProjectiles();

            transferDroneLockToHeavy();
            collectBombardmentImpacts();

            bulletEnemyCollisionChecker();
            communicateDamage();
            removeEnemies();
            updateGameTracks();
            respawnEnemies(dt);
        }
        drawDroneOverlayFx();
        drawHeavyLockFx();
        drawHeavyPortalFx(worldDt);
        drawBombardmentProjectiles();
        drawBombardmentImpacts(worldDt);

        batch.end(); 
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        drawPlayerHealthHud();
        drawJetpackHud();
        drawJetpackDisabledHud();

        drawScoreDashboard();
        drawAmmoHud();

        drawPoweredStateHud();

        drawInventoryBag();
        drawInventory();
        drawPauseMenu();
        drawRunSummary();
        HudDrawTheShop(mouseX,mouseY);

        batch.end();
        batch.setProjectionMatrix(camera.combined);
    }  

    

    @Override
    public void dispose() {
        saveManager.saveProfile(playerProfile);
        System.out.println("SAVING PROFILE: "+inventory.getGold());

        mapRenderer.dispose();
        map.dispose();
        batch.dispose();
        if(player!=null)
            player.disposePlayer();
        if(weapon!=null)
            weapon.disposeWeapon();
        if(bulletTextures != null){
            for(Texture texture :
                bulletTextures.values()){
                texture.dispose();
            }
            bulletTextures.clear();
        }
        shapeRenderer.dispose();
        FxAssets.dispose();
        riflemanProjectileTexture.dispose();
        heavyProjectileTexture.dispose();
        hudFont.dispose();
        lootFont.dispose();
        lootIconRegions.clear();
        inventoryTitleFont.dispose();
        inventoryBodyFont.dispose();
        inventoryInfoFont.dispose();
        inventoryButtonFont.dispose();
        reloadProgressPixel.dispose();
        AudioAssets.dispose();
        runSummaryFont.dispose();
        fieldShopTitleFont.dispose();
        fieldShopBodyFont.dispose();
        fieldShopInfoFont.dispose();
    }

    @Override
    public void resize(int width,int height){
        hudCamera.setToOrtho(false,width,height);
    }


    private TiledMap loadMap(){
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap loadedMap = loader.load(AssetPaths.Map.EARTH);
        return loadedMap;
    }

    private Array<TiledMapTileMapObject> getTerrainObjectArray(TiledMap map){
        MapLayer terrainLayer = map.getLayers().get("terrain");
        Array<TiledMapTileMapObject> terrainObjects  = terrainLayer.getObjects().getByType(TiledMapTileMapObject.class);
        return terrainObjects;
    }

    private OrthographicCamera createCameraFromMap(TiledMap map){
        MapLayer referenceLayer = map.getLayers().get("reference");
        RectangleMapObject cameraObject = (RectangleMapObject)referenceLayer.getObjects().get("Camera");
        Rectangle cameraRect = cameraObject.getRectangle();
        OrthographicCamera newCamera = new OrthographicCamera(cameraRect.width,cameraRect.height);
        newCamera.position.set(cameraRect.x + cameraRect.width/2 , cameraRect.y + cameraRect.height/2 , 0);
        newCamera.update();

        return newCamera; 
    }

    private Player createPlayerFromMap(TiledMap map){
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject playerSpawnObject = (RectangleMapObject)playerLayer.getObjects().get("PlayerSpawn"); 
        Rectangle playerRect = playerSpawnObject.getRectangle();
        Player newPlayer = new Player(playerRect.x,playerRect.y,playerRect.width,playerRect.height);

        return newPlayer;
    }

    private Weapon createWeaponFromMap(TiledMap map){
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject Ar_GunObject = (RectangleMapObject)playerLayer.getObjects().get("AR_origin");
        Rectangle AR_GunRect = Ar_GunObject.getRectangle();
        Weapon newWeapon = new Weapon(Weapon.WeaponType.PISTOL, AR_GunRect.x, AR_GunRect.y , AR_GunRect.width, AR_GunRect.height,extractARGripOffsetFromMap(map) );
        return newWeapon;
    }

    private Vector2 getMouseWorldPosition(){
        Vector3 mousePosition = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
        camera.unproject(mousePosition);
        return new Vector2(mousePosition.x, mousePosition.y);
    }

    private Vector2 normalizeBulletDirection(Vector2 handPos, Vector2 mousePos){
        float x = mousePos.x - handPos.x;
        float y = mousePos.y - handPos.y;
        float normalizer =(float) Math.sqrt(x*x + y*y);
        if(normalizer==0)
            return new Vector2(0,0);
        return new Vector2(x/normalizer, y/normalizer);
    }

    private void createBulletWhenFired(Vector2 mousePos){
        WeaponType type = weapon.getCurrentWeapon();
        AudioAssets.playWeaponFire(type);
        Vector2 gunGrip = player.getCurrentGripWorldPosition();
        Vector2 nozzlePosition = weapon.getMuzzleWorldPosition(gunGrip);
        Vector2 bulletDirection = normalizeBulletDirection(nozzlePosition,mousePos);
        Texture texture = bulletTextures.get(type);
        if(type == WeaponType.SHOTGUN){
            createShotgunSpread(nozzlePosition,bulletDirection,texture);
            return;
        }
        Bullet bullet =new Bullet(nozzlePosition,bulletDirection,getProjectileSpeed(type),texture, getBulletCollisionRadius(type),type,getBulletDrawSize(type),weapon.getHeadDamage(),weapon.getBodyDamage(),weapon.getLimbDamage());
        bullets.add(bullet);
    }

     private float getBulletCollisionRadius(WeaponType type){
        if(type == WeaponType.PISTOL)
            return 4.5f;
        if(type == WeaponType.AR)
            return 3.5f;
        if(type == WeaponType.SMG)
            return 3.5f;
        if(type == WeaponType.SHOTGUN)
            return 4f;
        if(type == WeaponType.SNIPER)
            return 3f;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return 12f;
        return 3f;
    }


    private void loadBulletTextures(){
        bulletTextures = new ObjectMap<>();
        bulletTextures.put(WeaponType.PISTOL, new Texture(AssetPaths.Bullets.PISTOL));
        bulletTextures.put(WeaponType.AR,new Texture(AssetPaths.Bullets.AR));
        bulletTextures.put(WeaponType.SMG,new Texture(AssetPaths.Bullets.SMG));
        bulletTextures.put(WeaponType.SHOTGUN, new Texture(AssetPaths.Bullets.SHOTGUN));
        bulletTextures.put(WeaponType.SNIPER,new Texture(AssetPaths.Bullets.SNIPER));
        bulletTextures.put(WeaponType.ROCKET_LAUNCHER,new Texture(AssetPaths.Bullets.ROCKET_LAUNCHER));
        for(Texture texture : bulletTextures.values()){
            texture.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
        }
    }

    private float getBulletDrawSize(WeaponType type){
        if(type == WeaponType.PISTOL)
            return 40f;

        if(type == WeaponType.AR)
            return 40f;

        if(type == WeaponType.SMG)
            return 50f;

        if(type == WeaponType.SHOTGUN)
            return 60f;

        if(type == WeaponType.SNIPER)
            return 80f;

        if(type == WeaponType.ROCKET_LAUNCHER)
            return 80f;
        return 25f;
    }

    private Vector2 getWorldSize(){
        Vector2 worldSize = new Vector2(0,0);

        int mapWidthTiles = map.getProperties().get("width", Integer.class);
        int mapHeightTiles = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);

        worldSize.x = mapWidthTiles * tileWidth;
        worldSize.y = mapHeightTiles * tileHeight;

        return worldSize;
    }

    private void clampCameraToPlayer(){
        Vector2 playerPos = player.getPlayerCentrePosition();
        Vector2 worldSize = getWorldSize();

        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        float deadZoneHalfWidth = 0.33f * visibleWidth/2;
        float deadZoneHalfHeight = 0.33f * visibleHeight/2;

        float rightThreshold = cameraX + deadZoneHalfWidth;
        float leftThreshold = cameraX - deadZoneHalfWidth;
        
        float topThreshold = cameraY + deadZoneHalfHeight;
        float bottomThreshold = cameraY - deadZoneHalfHeight;

        if(playerPos.x > rightThreshold)
            cameraX = cameraX + playerPos.x - rightThreshold;
        
        else if(playerPos.x < leftThreshold)
            cameraX = cameraX + playerPos.x - leftThreshold;
        
        if(playerPos.y > topThreshold)
            cameraY = cameraY+ playerPos.y - topThreshold;
        
        else if(playerPos.y < bottomThreshold)
            cameraY = cameraY + playerPos.y - bottomThreshold;
        
        float minCameraX = visibleWidth/ 2;
        float maxCameraX = worldSize.x - visibleWidth / 2;

        float minCameraY = visibleHeight/ 2;
        float maxCameraY = worldSize.y - visibleHeight / 2;
        if(visibleWidth >= worldSize.x)
            cameraX = worldSize.x/2f;

        if(visibleHeight >= worldSize.y)
            cameraY = worldSize.y/2f;

        if(visibleWidth < worldSize.x)
            cameraX = MathUtils.clamp(cameraX,minCameraX,maxCameraX);
        if(visibleHeight < worldSize.y)
            cameraY = MathUtils.clamp(cameraY,minCameraY,maxCameraY);

        camera.position.set(cameraX,cameraY,0);
        camera.update();
        mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    private boolean bulletHitsRectangle(Bullet bullet,Rectangle rectangle){
        float r = bullet.getRadius();
        bulletSweepRect.set(rectangle.x-r,rectangle.y-r,rectangle.width+r*2f,rectangle.height+r*2f);
        return Intersector.intersectSegmentRectangle(bullet.getPreviousPosition(),bullet.getPosition(),bulletSweepRect);
    }

    private void bulletPlatformCollision(){
        for(Bullet bullet : bullets){
            if(bullet.isWasted())
                continue;
           
            for(Rectangle solid : solidPlatforms){
                if(!bulletHitsRectangle(bullet,solid)){
                    continue;
                }
                if(bullet.getWeaponType() == WeaponType.ROCKET_LAUNCHER)
                    explodeRocket(bullet,true);
                bullet.considerWasted();
                break;
            }
        }
        removeBullets();
    }
    private void explodeRocket(Bullet bullet,boolean groundExplosion){
        AudioAssets.playRocketImpact();
        Vector2 explosionPosition = new Vector2(bullet.getPosition());
        float maxDamage = bullet.getBodyDamage() * ROCKET_EXPLOSION_DAMAGE_MULTIPLIER;
        for(Enemy enemy : enemies){
            Rectangle hurtbox = enemy.getWorldHurtbox();
            float closestX = MathUtils.clamp(explosionPosition.x, hurtbox.x, hurtbox.x+hurtbox.width);
            float closestY = MathUtils.clamp(explosionPosition.y,hurtbox.y,hurtbox.y+hurtbox.height);

            float dx = explosionPosition.x-closestX;
            float dy = explosionPosition.y-closestY;
            float distance = (float)Math.sqrt(dx*dx+dy*dy);

            if(distance >
            ROCKET_EXPLOSION_RADIUS)
                continue;

            float distancePercent = MathUtils.clamp(distance/ROCKET_EXPLOSION_RADIUS,0f,1f);
            float damageMultiplier =MathUtils.lerp(1f,ROCKET_EXPLOSION_MIN_MULTIPLIER,distancePercent);
            enemy.enemyHurt(maxDamage*damageMultiplier);
        }
        rocketExplosionFx.add(new RocketExplosionFx(explosionPosition,groundExplosion));
    }

    private void drawRocketExplosions(float dt){
    float frameDuration = 0.05f;
    float drawSize = 220f;
    for(int i = rocketExplosionFx.size-1;i>=0;i--){
        RocketExplosionFx fx = rocketExplosionFx.get(i);
        fx.timer += dt;
        TextureRegion[] frames;
        if(fx.ground)
            frames = FxAssets.groundExplosion;
        else
            frames = FxAssets.midAirExplosion;
        int frame = (int)(fx.timer/frameDuration);
        if(frame >= frames.length){
            rocketExplosionFx.removeIndex(i);
            continue;
        }
        batch.draw(frames[frame],fx.position.x-drawSize/2f,fx.position.y-drawSize/2f,drawSize,drawSize);
    }
}



    private Array<Rectangle> extractSolidPlatformsFromMap(TiledMap map){
        Array<Rectangle> solidPlatforms = new Array<>();
        MapLayer collisionLayer = map.getLayers().get("collision");
        Array<RectangleMapObject> platforms = collisionLayer.getObjects().getByType(RectangleMapObject.class);
        for(RectangleMapObject platform : platforms)
            solidPlatforms.add(platform.getRectangle());

        return solidPlatforms;
    }

    private Rectangle extractPlayerMovementColliderFromMap(TiledMap map){
        Rectangle moveBox = new Rectangle();
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject moveBoxObject = (RectangleMapObject)playerLayer.getObjects().get("playerMovement");
        moveBox = moveBoxObject.getRectangle();

        return moveBox;
    }

    private Rectangle extractPlayerCrouchMovementColliderFromMap(TiledMap map){
        Rectangle crouchMoveBox = new Rectangle();
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject crouchMoveBoxObject = (RectangleMapObject)playerLayer.getObjects().get("crouchMovement");
        crouchMoveBox = crouchMoveBoxObject.getRectangle();
        return crouchMoveBox;
    }

    private Vector2 extractPlayerGunHoldingOffsetFromMap(TiledMap map){
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject playerSpawMapObject = (RectangleMapObject) playerLayer.getObjects().get("PlayerSpawn");
        RectangleMapObject weaponSocketObject = (RectangleMapObject) playerLayer.getObjects().get("weaponSocket");

        Rectangle forWeaponRect = playerSpawMapObject.getRectangle();
        Rectangle weaponSocketRect = weaponSocketObject.getRectangle();

        float offsetX = weaponSocketRect.x - forWeaponRect.x;
        float offsetY = weaponSocketRect.y - forWeaponRect.y;

        return new Vector2(offsetX, offsetY);
    }

    private Vector2 extractARGripOffsetFromMap(TiledMap map){
        MapLayer playerLayer = map.getLayers().get("Player");

        RectangleMapObject weaponSocketObject = (RectangleMapObject)playerLayer.getObjects().get("weaponSocket");
        RectangleMapObject arOriginObject= (RectangleMapObject)playerLayer.getObjects().get("AR_origin");

        Rectangle weaponSocketRect = weaponSocketObject.getRectangle();
        Rectangle arOriginRect = arOriginObject.getRectangle();
        
        return new Vector2(weaponSocketRect.x - arOriginRect.x , weaponSocketRect.y - arOriginRect.y);
    }

    private Vector2 extractARNozzleOffsetFromMap(TiledMap map){
        MapLayer playerLayer = map.getLayers().get("Player");
        RectangleMapObject AR_NozzleObject = (RectangleMapObject) playerLayer.getObjects().get("AR_Nozzle");
        RectangleMapObject weaponSocketObject = (RectangleMapObject) playerLayer.getObjects().get("weaponSocket");
        Rectangle AR_NozzleRect = AR_NozzleObject.getRectangle();
        Rectangle weaponSocketRect = weaponSocketObject.getRectangle();

        return new Vector2(AR_NozzleRect.x- weaponSocketRect.x , AR_NozzleRect.y - weaponSocketRect.y );
    }

    private Rectangle extractJetpackFromMap(TiledMap map){
        MapLayer referenceLayer = map.getLayers().get("reference");
        RectangleMapObject forJetpackObject = (RectangleMapObject) referenceLayer.getObjects().get("forJetpack");
        RectangleMapObject jetPackObject = (RectangleMapObject) referenceLayer.getObjects().get("jetPack");
        Rectangle forJetpackRect = forJetpackObject.getRectangle();
        Rectangle jetPackRect = jetPackObject.getRectangle();
        
        return new Rectangle (jetPackRect.x - forJetpackRect.x , jetPackRect.y - forJetpackRect.y ,jetPackRect.width , jetPackRect.height); 
    }

    private Enemy create_Jackal_RifleMan_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject RiflemanSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Rifleman_Spawn");
        Rectangle RiflemanSpawnRect = RiflemanSpawnObject.getRectangle();
        float drawWidth = RiflemanSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = RiflemanSpawnObject.getProperties().get("drawHeight", Float.class);

        Enemy newRifleman = new Enemy(Enemy.EnemyType.RIFLEMAN, RiflemanSpawnRect.x, RiflemanSpawnRect.y , drawWidth, drawHeight, extract_Jackal_RifleMan_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));
        riflemanCount++;
        return newRifleman;
    }

    private Enemy create_Jackal_RifleMan_FromMap(TiledMap map,float x, float y){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject RiflemanSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Rifleman_Spawn");
        float drawWidth = RiflemanSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = RiflemanSpawnObject.getProperties().get("drawHeight", Float.class);

        Enemy newRifleman = new Enemy(Enemy.EnemyType.RIFLEMAN, x, y , drawWidth, drawHeight, extract_Jackal_RifleMan_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));
        riflemanCount++;
        return newRifleman;
    }

    private Rectangle extract_Jackal_RifleMan_Hitbox_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject Rifleman_HitboxObject = (RectangleMapObject) enemiesLayer.getObjects().get("Rifleman_Hitbox");
        RectangleMapObject RiflemanSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Rifleman_Spawn");
        Rectangle Rifleman_HitboxRect = Rifleman_HitboxObject.getRectangle();
        Rectangle RiflemanSpawnRect = RiflemanSpawnObject.getRectangle();
    
        return new Rectangle ( Rifleman_HitboxRect.x - RiflemanSpawnRect.x , Rifleman_HitboxRect.y - RiflemanSpawnRect.y ,Rifleman_HitboxRect.width , Rifleman_HitboxRect.height); 
    }

    private Enemy create_Jackal_Heavy_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject HeavySpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Heavy_Spawn");
        Rectangle HeavySpawnRect = HeavySpawnObject.getRectangle();
        float drawWidth = HeavySpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = HeavySpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy newHeavy = new Enemy(Enemy.EnemyType.HEAVY, HeavySpawnRect.x, HeavySpawnRect.y , drawWidth, drawHeight , extract_Jackal_Heavy_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        heavyCount++;
        return newHeavy;
    }

    private Enemy create_Jackal_Heavy_FromMap(TiledMap map,float x, float y){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject HeavySpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Heavy_Spawn");
        float drawWidth = HeavySpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = HeavySpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy newHeavy = new Enemy(Enemy.EnemyType.HEAVY, x, y , drawWidth, drawHeight , extract_Jackal_Heavy_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        heavyCount++;
        return newHeavy;
    }

    private Rectangle extract_Jackal_Heavy_Hitbox_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject Heavy_HitboxObject = (RectangleMapObject) enemiesLayer.getObjects().get("Heavy_Hitbox");
        RectangleMapObject HeavySpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Heavy_Spawn");
        Rectangle Heavy_HitboxRect = Heavy_HitboxObject.getRectangle();
        Rectangle HeavySpawnRect = HeavySpawnObject.getRectangle();
        return new Rectangle (Heavy_HitboxRect.x - HeavySpawnRect.x , Heavy_HitboxRect.y - HeavySpawnRect.y ,Heavy_HitboxRect.width , Heavy_HitboxRect.height); 
    }

    private Enemy create_Jackal_HoverDrone_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject HoverDroneSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Hover_Drone_Spawn");
        Rectangle HoverDroneSpawnRect = HoverDroneSpawnObject.getRectangle();
        float drawWidth = HoverDroneSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = HoverDroneSpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy HoverDrone = new Enemy(Enemy.EnemyType.HOVER_DRONE, HoverDroneSpawnRect.x, HoverDroneSpawnRect.y , drawWidth , drawHeight ,extract_Jackal_HoverDrone_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        droneCount++;
        return HoverDrone;
    }

    private Enemy create_Jackal_HoverDrone_FromMap(TiledMap map,float x, float y){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject HoverDroneSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Hover_Drone_Spawn");
        float drawWidth = HoverDroneSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = HoverDroneSpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy HoverDrone = new Enemy(Enemy.EnemyType.HOVER_DRONE, x, y , drawWidth , drawHeight ,extract_Jackal_HoverDrone_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        droneCount++;
        return HoverDrone;
    }

    private Rectangle extract_Jackal_HoverDrone_Hitbox_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject HoverDrone_HitboxObject = (RectangleMapObject) enemiesLayer.getObjects().get("Hover_Drone_Hitbox");
        RectangleMapObject HoverDroneSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Hover_Drone_Spawn");
        Rectangle HoverDrone_HitboxRect = HoverDrone_HitboxObject.getRectangle();
        Rectangle HoverDroneSpawnRect = HoverDroneSpawnObject.getRectangle();
        return new Rectangle (HoverDrone_HitboxRect.x - HoverDroneSpawnRect.x , HoverDrone_HitboxRect.y - HoverDroneSpawnRect.y ,HoverDrone_HitboxRect.width , HoverDrone_HitboxRect.height); 
    }

    private Enemy create_Jackal_DrillCrawler_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject DrillCrawlerSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Drill_Crawler_Spawn");
        Rectangle DrillCrawlerSpawnRect = DrillCrawlerSpawnObject.getRectangle();
        float drawWidth = DrillCrawlerSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = DrillCrawlerSpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy newDrillCrawler = new Enemy(Enemy.EnemyType.DRILL_CRAWLER, DrillCrawlerSpawnRect.x, DrillCrawlerSpawnRect.y , drawWidth, drawHeight, extract_Jackal_DrillCrawler_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        drillerCount++;
        return newDrillCrawler;
    }

    private Enemy create_Jackal_DrillCrawler_FromMap(TiledMap map,float x, float y){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject DrillCrawlerSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Drill_Crawler_Spawn");
        float drawWidth = DrillCrawlerSpawnObject.getProperties().get("drawWidth", Float.class);
        float drawHeight = DrillCrawlerSpawnObject.getProperties().get("drawHeight", Float.class);
        Enemy newDrillCrawler = new Enemy(Enemy.EnemyType.DRILL_CRAWLER, x, y , drawWidth, drawHeight, extract_Jackal_DrillCrawler_Hitbox_FromMap(map),extract_Jackal_DrillCrawler_Circles_FromMap(map));

        drillerCount++;
        return newDrillCrawler;
    }

    private Rectangle extract_Jackal_DrillCrawler_Hitbox_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject DrillCrawler_HitboxObject = (RectangleMapObject) enemiesLayer.getObjects().get("Drill_Crawler_Hitbox");
        RectangleMapObject DrillCrawlerSpawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Drill_Crawler_Spawn");
        Rectangle DrillCrawler_HitboxRect = DrillCrawler_HitboxObject.getRectangle();
        Rectangle DrillCrawlerSpawnRect = DrillCrawlerSpawnObject.getRectangle();
        return new Rectangle (DrillCrawler_HitboxRect.x - DrillCrawlerSpawnRect.x , DrillCrawler_HitboxRect.y - DrillCrawlerSpawnRect.y ,DrillCrawler_HitboxRect.width , DrillCrawler_HitboxRect.height); 
    }

    private void drawDroneBeam(){
        for(Enemy enemy : enemies){
            if(enemy.getType() != EnemyType.HOVER_DRONE)
                continue;
            if(enemy.getCurrentState() != Enemy.EnemyState.ATTACK)
                continue;
            Vector2 center = enemy.getDroneCenter();
            float magnitude = enemy.getDroneBeamMagnitude();
            float angle = enemy.getDroneBeamAngleDegrees();
            float originX = 64f;
            float originY = 512f;
            float drawX = center.x - originX;
            float drawY = center.y - originY;
            float scaleX = magnitude / 896f;
            float scaleY = 0.45f;
            int beamFrame = (int)(enemy.getStateTime()/0.08f) % FxAssets.phaseBeam.length;
            batch.draw(FxAssets.phaseBeam[beamFrame],drawX,drawY,originX,originY,1024f,1024f,scaleX,scaleY,angle);
        }
}

private void drawDroneOverlayFx(){
    for(Enemy enemy : enemies){
        if(enemy.getType() != EnemyType.HOVER_DRONE)
            continue;
        Vector2 center = enemy.getDroneCenter();
        float magnitude = enemy.getDroneBeamMagnitude();
        float angle = enemy.getDroneBeamAngleDegrees();
        float targetX = center.x + MathUtils.cosDeg(angle) * magnitude;
        float targetY = center.y + MathUtils.sinDeg(angle) * magnitude;
        float stateTime = enemy.getStateTime();
        
        if(enemy.getCurrentState() == Enemy.EnemyState.ATTACK){
            int burstFrame = (int)(stateTime/0.05f);
            if(burstFrame < FxAssets.droneBurst.length){
                float burstCanvasSize = 350f;
                batch.draw(FxAssets.droneBurst[burstFrame],center.x - burstCanvasSize/2f,center.y - burstCanvasSize/2f,burstCanvasSize,burstCanvasSize);
            }

            int flashFrame =(int)(stateTime/0.08f) % FxAssets.emitterFlash.length;
            float flashCanvasSize = 350f;
            batch.draw(FxAssets.emitterFlash[flashFrame],center.x - flashCanvasSize/2f,center.y - flashCanvasSize/2f,flashCanvasSize,flashCanvasSize);
            int contactFrame =(int)(stateTime/0.08f) % FxAssets.beamContact.length;
            float contactCanvasSize = 300f;
            batch.draw(FxAssets.beamContact[contactFrame],targetX - contactCanvasSize/2f,targetY - contactCanvasSize/2f,contactCanvasSize,contactCanvasSize);
        }
    }
}

private void transferDroneLockToHeavy(){
    for(int i=0;i<enemies.size;i++){
        Enemy drone = enemies.get(i);

        if(drone.getType() != EnemyType.HOVER_DRONE)
            continue;

        if(!drone.isHeavyLocking())
            continue;

        for(int j=0;j<enemies.size;j++){
            Enemy heavy = enemies.get(j);

            if(heavy.getType() != EnemyType.HEAVY)
                continue;

            if(heavy.isHeavyLocking())
                continue;

            if(heavy.isHeavyRangeAttackActive())
                continue;

            heavy.setHeavyLock(true);
            drone.setHeavyLock(false);
            break;
        }
    }
}
private void drawHeavyLockFx(){
    Rectangle playerHurtbox = player.getWorldHitbox();
    for(Enemy enemy : enemies){
        if(enemy.getType() != EnemyType.HEAVY)
            continue;

        if(!enemy.isHeavyLocking())
            continue;

        float targetX;
        float targetY;
        if(enemy.isHeavyLeftTarget()){
            Vector2 predicted = enemy.getHeavyPredictedRainFirePosition();
            targetX = predicted.x;
            targetY = predicted.y;
            boolean visible = ((int)(enemy.getHeavyLockTimer() * 10f) % 2) == 0;
            if(!visible)
                continue;
        }
        else{
            targetX = playerHurtbox.x + playerHurtbox.width/2f;
            targetY = playerHurtbox.y + playerHurtbox.height/2f;
        }
        int reticleFrame = (int)(enemy.getHeavyLockTimer()/0.10f) % FxAssets.targetReticle.length;
        float size = 350f;
        float x = targetX-size/2f;
        float y = targetY-size/2f;
        batch.setColor(1f,0f,1f,1f);
        batch.draw(FxAssets.targetReticle[reticleFrame],x,y,size,size);
        batch.setColor(Color.WHITE);
    }

}

private void drawHeavyPortalFx(float dt){
    for(Enemy enemy : enemies){
        if(enemy.getType() != EnemyType.HEAVY)
            continue;

        if(!enemy.isHeavyRangeAttackActive())
            continue;

        float heavyPortalFxTimer = enemy.getHeavyPortalFxTimer();
        Vector2 position = enemy.getHeavyPredictedRainFirePosition();

        heavyPortalFxTimer += dt;

        TextureRegion[] openFrames;
        TextureRegion[] activeFrames;
        int attackType = enemy.getHeavySelectedAttack();
        if(attackType == 1){
            openFrames = FxAssets.poisonPortalOpen;
            activeFrames = FxAssets.poisonPortalActive;
        }
        else if(attackType == 2){
            openFrames = FxAssets.bombardmentPortalOpen;
            activeFrames = FxAssets.bombardmentPortalActive;
        }
        else{
            openFrames = FxAssets.empPortalOpen;
            activeFrames = FxAssets.empPortalActive;
        }

        float size = 350f;
        float portalY = position.y + 200f;
        if(attackType == 2)
            portalY = position.y + 420f;
        float x = position.x-size/2f;
        float y = portalY-size/2f;

        float openFrameDuration = 0.08f;
        float openDuration = openFrames.length * openFrameDuration;
        TextureRegion frame = null;

        float releaseFrameDuration = 0.08f;
        float releaseDuration =FxAssets.poisonPortalRelease.length * releaseFrameDuration;

        if(heavyPortalFxTimer < openDuration){
            int frameIndex =(int)(heavyPortalFxTimer/openFrameDuration);
            frame = openFrames[frameIndex];
        }
        else if(attackType == 1){
            float releaseEnd = openDuration + releaseDuration;
            float portalActiveHoldDuration = 0.40f;
            float closeStart = releaseEnd + portalActiveHoldDuration;
            float closeFrameDuration = 0.08f;
            float closeDuration = FxAssets.poisonPortalClose.length * closeFrameDuration;

            if(heavyPortalFxTimer < releaseEnd){
                float releaseTime =heavyPortalFxTimer-openDuration;
                int frameIndex = (int)(releaseTime/releaseFrameDuration);
                frame = FxAssets.poisonPortalRelease[frameIndex];
            }
            else if(heavyPortalFxTimer < closeStart){
                float activeTime = heavyPortalFxTimer-releaseEnd;
                int frameIndex = (int)(activeTime/0.08f) % FxAssets.poisonPortalActive.length;
                frame = FxAssets.poisonPortalActive[frameIndex];
            }
            else if(heavyPortalFxTimer < closeStart + closeDuration){
                float closeTime = heavyPortalFxTimer-closeStart;
                int frameIndex = (int)(closeTime/closeFrameDuration);
                frame = FxAssets.poisonPortalClose[frameIndex];
            }
        }
        else if(attackType == 3){
            float chargeDuration = 0.40f;
            float pulseDuration = 0.60f;

            float chargeEnd = openDuration+chargeDuration;
            float pulseEnd = chargeEnd+pulseDuration;

            float closeFrameDuration = 0.07f;
            float closeDuration = FxAssets.empPortalClose.length*closeFrameDuration;
            if(heavyPortalFxTimer < chargeEnd){
                float activeTime = heavyPortalFxTimer-openDuration;
                int frameIndex = (int)(activeTime/0.10f) % FxAssets.empPortalActive.length;
                frame = FxAssets.empPortalActive[frameIndex];
            }

            else if(heavyPortalFxTimer < pulseEnd){
                float activeTime = heavyPortalFxTimer-openDuration;
                int frameIndex =(int)(activeTime/0.10f) % FxAssets.empPortalActive.length;
                frame = FxAssets.empPortalActive[frameIndex];
            }

            else if(heavyPortalFxTimer < pulseEnd+closeDuration){
                float closeTime = heavyPortalFxTimer-pulseEnd;
                int frameIndex = (int)(closeTime/closeFrameDuration);
                frame = FxAssets.empPortalClose[frameIndex];
            }
        }
        else{
            float activeTime = heavyPortalFxTimer-openDuration;
            int frameIndex =(int)(activeTime/0.08f) % activeFrames.length;
            frame = activeFrames[frameIndex];
        }
        
        if(attackType == 1){
            float fieldSpawnTime = openDuration + 0.27f;
            float poisonLifetime = 12f;
            float fieldStartFrameDuration = 0.06f;
            float fieldStartDuration = FxAssets.poisonFieldStart.length * fieldStartFrameDuration;
            float fieldEndFrameDuration = 0.08f;
            float fieldEndDuration = FxAssets.poisonFieldEnd.length * fieldEndFrameDuration;
            float fieldEndStart = poisonLifetime-fieldEndDuration;
            if(heavyPortalFxTimer >= fieldSpawnTime){
                TextureRegion fieldFrame;
                float fieldTime = heavyPortalFxTimer-fieldSpawnTime;

                if(fieldTime < fieldStartDuration){
                    int frameIndex = (int)(fieldTime/fieldStartFrameDuration);
                    fieldFrame = FxAssets.poisonFieldStart[frameIndex];
                }
                else if(heavyPortalFxTimer < fieldEndStart){
                    float loopTime = fieldTime-fieldStartDuration;
                    int frameIndex = (int)(loopTime/0.10f) % FxAssets.poisonFieldLoop.length;
                    fieldFrame = FxAssets.poisonFieldLoop[frameIndex];
                }
                else{
                    float endTime = heavyPortalFxTimer-fieldEndStart;
                    int frameIndex = (int)(endTime/fieldEndFrameDuration);
                    if(frameIndex >= FxAssets.poisonFieldEnd.length)
                        frameIndex = FxAssets.poisonFieldEnd.length-1;
                    fieldFrame = FxAssets.poisonFieldEnd[frameIndex];
                }
                float fieldSize = 380f;
                batch.draw(fieldFrame,position.x-fieldSize/2f,position.y-fieldSize/2f,fieldSize,fieldSize);
            }
        }
        if(attackType == 3){
            float pulseStartTime = openDuration + 0.40f;
            float pulseFrameDuration = 0.05f;
            float pulseDuration =FxAssets.empPulse.length * pulseFrameDuration;

            if(heavyPortalFxTimer >= pulseStartTime && heavyPortalFxTimer < pulseStartTime + pulseDuration){
                float pulseTime = heavyPortalFxTimer-pulseStartTime;
                int frameIndex =(int)(pulseTime/pulseFrameDuration);
                TextureRegion pulseFrame =FxAssets.empPulse[frameIndex];
                float pulseSize = 80f*(1024f/355f);
                batch.draw(pulseFrame,position.x-pulseSize/2f,position.y-pulseSize/2f,pulseSize,pulseSize);
            }
        }

        if(frame!=null)
            batch.draw(frame,x,y,size,size);
    }
}


private void drawJetpackDisabledHud(){
    if(player.getJetpackFuel() > 0)
        return;

    float size = 175f;

    float x = 5f;
    float healthWidth = 220f;
    float scale = healthWidth/512f;
    float height = 80f*scale;
    float gap = 10f;

    float healthY = Gdx.graphics.getHeight()-height-24f;
    float jetpackY = healthY-height-gap;

    float iconX = x-50f;
    float iconY = jetpackY-size*0.65f;

    batch.draw(player.getJetpackDisabledTexture(),iconX,iconY,size,size);
}
    private void activateGravity(float dt){
        player.reactToGravity(gravity, dt);
        for(Enemy enemy: enemies)
            enemy.reactToGravity(gravity,dt);
    }

    private void updatePlayerFacingDirection(Vector2 mousePos){
        Vector2 playerPos = player.getPlayerCentrePosition();
        if(mousePos.x > playerPos.x)
            player.setFacingDirectionRight();

        else if(mousePos.x < playerPos.x)
            player.setFacingDirectionLeft();
    }

    private void JumpVSFlight(float dt){
        player.setAirTime(player.getAirTime()+dt);
        if(player.getAirTime() > 0.5)
            player.reactToJetpack(jetpackThrust,dt);
    }

    private Array<Rectangle> extractEnemiesHurtBoxesByTypeFromMap(TiledMap map , String hitClass, String owner){
        Array<Rectangle> enemyHurtboxes = new Array<>();
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        Array<RectangleMapObject> enemyHurtBox = enemiesLayer.getObjects().getByType(RectangleMapObject.class);
        Rectangle spawnRect = new Rectangle();
        if("heavy".equals(owner)){
            RectangleMapObject spawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Heavy_Spawn");
            spawnRect = spawnObject.getRectangle();
        }
        else if("rifleman".equals(owner)){
            RectangleMapObject spawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Rifleman_Spawn");
            spawnRect = spawnObject.getRectangle();
        }
        else if("driller".equals(owner)){
            RectangleMapObject spawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Drill_Crawler_Spawn");
            spawnRect = spawnObject.getRectangle();
        }
        else if("drone".equals(owner)){
            RectangleMapObject spawnObject = (RectangleMapObject) enemiesLayer.getObjects().get("Hover_Drone_Spawn");
            spawnRect = spawnObject.getRectangle();
        }

        for(RectangleMapObject hurtbox : enemyHurtBox){
            String className = hurtbox.getProperties().get("type", String.class);
            String ownerName = hurtbox.getProperties().get("owner", String.class);

            if(hitClass.equals(className) && owner.equals(ownerName)){
                Rectangle specificHurtBox = new Rectangle(hurtbox.getRectangle());
                specificHurtBox.x = Enemy.getEnemyScale() * (specificHurtBox.x - spawnRect.x);
                specificHurtBox.y = Enemy.getEnemyScale() * (specificHurtBox.y - spawnRect.y);
                specificHurtBox.width *= Enemy.getEnemyScale();
                specificHurtBox.height*= Enemy.getEnemyScale();
                enemyHurtboxes.add(specificHurtBox);
            }  
        }
        return enemyHurtboxes;  
    }


    private void extractAllEnemyHitboxes(){
        Array<Rectangle> Rifleman_Headshot = extractEnemiesHurtBoxesByTypeFromMap(map,"headshot","rifleman");
        Array<Rectangle> Rifleman_Bodyshot = extractEnemiesHurtBoxesByTypeFromMap(map,"bodyshot","rifleman");
        Array<Rectangle> Rifleman_Limbshot = extractEnemiesHurtBoxesByTypeFromMap(map,"limbshot","rifleman");

        Array<Rectangle> Heavy_Headshot = extractEnemiesHurtBoxesByTypeFromMap(map,"headshot","heavy");
        Array<Rectangle> Heavy_Bodyshot = extractEnemiesHurtBoxesByTypeFromMap(map,"bodyshot","heavy");
        Array<Rectangle> Heavy_Limbshot = extractEnemiesHurtBoxesByTypeFromMap(map,"limbshot","heavy");

        Array<Rectangle> Drone_Headshot = extractEnemiesHurtBoxesByTypeFromMap(map,"headshot","drone");
        Array<Rectangle> Drone_Bodyshot = extractEnemiesHurtBoxesByTypeFromMap(map,"bodyshot","drone");
        Array<Rectangle> Drone_Limbshot = extractEnemiesHurtBoxesByTypeFromMap(map,"limbshot","drone");

        Array<Rectangle> Drill_Headshot = extractEnemiesHurtBoxesByTypeFromMap(map,"headshot","driller");
        Array<Rectangle> Drill_Bodyshot = extractEnemiesHurtBoxesByTypeFromMap(map,"bodyshot","driller");
        Array<Rectangle> Drill_Limbshot = extractEnemiesHurtBoxesByTypeFromMap(map,"limbshot","driller");

        Enemy.setEnemyHurtBox(EnemyType.RIFLEMAN, Rifleman_Headshot,Rifleman_Bodyshot,Rifleman_Limbshot);
        Enemy.setEnemyHurtBox(EnemyType.HEAVY, Heavy_Headshot,Heavy_Bodyshot,Heavy_Limbshot);
        Enemy.setEnemyHurtBox(EnemyType.HOVER_DRONE, Drone_Headshot,Drone_Bodyshot,Drone_Limbshot);
        Enemy.setEnemyHurtBox(EnemyType.DRILL_CRAWLER, Drill_Headshot,Drill_Bodyshot,Drill_Limbshot);
    }     


    private void bulletEnemyCollisionChecker(){
    for(Bullet bullet : bullets){
        if(bullet.isWasted())
            continue;
        for(Enemy enemy : enemies){
            if(bullet.isWasted())
                break;
            Rectangle hurtbox = enemy.getWorldHurtbox();

            //ROCKET
            if(bullet.getWeaponType() == WeaponType.ROCKET_LAUNCHER){
                if(bulletHitsRectangle(bullet,hurtbox)){
                    explodeRocket(bullet,false);
                    bullet.considerWasted();
                    break;
                }
                continue;
            }
            //DRILL CRAWLER
            if(enemy.getType() == EnemyType.DRILL_CRAWLER){
                boolean hit = false;
                for(Circle circle : enemy.getWorldDrillCircles()){
                    if(bulletHitsCircle(bullet,circle)){
                        enemy.enemyHurt(bullet.getLimbDamage()+ bullet.getBodyDamage()/2f);
                        AudioAssets.playArmorHit();
                        bullet.considerWasted();
                        hit = true;
                        break;
                    }
                }
                if(hit)
                    break;
                continue;
            }
            boolean headshot = false;
            boolean bodyshot = false;
            //HEAD
            for(Rectangle rect :enemy.getHeadshotBoxes()){
                Rectangle worldRect = enemy.getWorldHitRegion(rect);
                if(bulletHitsRectangle(bullet,worldRect)){
                    runState.addHeadshot();
                    AudioAssets.playHeadshot();
                    enemy.enemyHurt(bullet.getHeadDamage());
                    bullet.considerWasted();
                    headshot = true;
                    break;
                }
            }
            if(headshot)
                break;
            //BODY
            for(Rectangle rect : enemy.getBodyshotBoxes()){
                Rectangle worldRect = enemy.getWorldHitRegion(rect);
                if(bulletHitsRectangle(bullet,worldRect)){
                    runState.addBodyshot();
                    AudioAssets.playBodyHit();
                    enemy.enemyHurt(bullet.getBodyDamage());
                    bullet.considerWasted();
                    bodyshot = true;
                    break;
                }
            }
            if(bodyshot)
                break;
            //LIMB
            for(Rectangle rect : enemy.getLimbshotBoxes()){
                Rectangle worldRect = enemy.getWorldHitRegion(rect);
                if(bulletHitsRectangle(bullet,worldRect)){
                    runState.addLimbshot();
                    enemy.enemyHurt(bullet.getLimbDamage());
                    bullet.considerWasted();
                    break;
                }
            }
        }
    }
    removeBullets();
    }

    private boolean bulletHitsCircle(Bullet bullet,Circle circle){
    Vector2 start = bullet.getPreviousPosition();
    Vector2 end = bullet.getPosition();
    float dx = end.x-start.x;
    float dy = end.y-start.y;
    float lengthSquared = dx*dx+dy*dy;
    float t;
    if(lengthSquared == 0f)
        t = 0f;

    else{
        t = ((circle.x-start.x)*dx + (circle.y-start.y)*dy)/ lengthSquared;
        t = MathUtils.clamp(t, 0f, 1f);
    }

    float closestX = start.x+t*dx;
    float closestY = start.y+t*dy;
    float cx = circle.x-closestX;
    float cy = circle.y-closestY;
    float combinedRadius = circle.radius + bullet.getRadius();

    return cx*cx+cy*cy <=combinedRadius*combinedRadius;
}

    private void playerEnemyProjectileCollisionChecker(){
        for(EnemyProjectile projectile : enemyProjectiles){
            Circle projectileHitbox = new Circle(projectile.getPosition().x, projectile.getPosition().y,projectile.getRadius());
            for(Rectangle platforms : solidPlatforms){
                if(Intersector.intersectSegmentRectangle(projectile.getPreviousPosition(),projectile.getPosition(),platforms)
                || (Intersector.overlaps(projectileHitbox,platforms))){
                    projectile.considerWasted();
                    break;
                }
            }
            if(projectile.isWasted())
                continue;

            if(Intersector.intersectSegmentRectangle(projectile.getPreviousPosition(),projectile.getPosition(),player.getWorldHitbox())
                || (Intersector.overlaps(projectileHitbox,player.getWorldHitbox()))){
                    damagePlayer(projectile.getDamage());
                    projectile.considerWasted();
            }
        }
    }

    private void removeEnemies(){
        for(int i = enemies.size-1 ; i >= 0 ; i--){
            if(enemies.get(i).isReadytoBeRemoved()){
                if(enemies.get(i).getType()== EnemyType.RIFLEMAN){
                    riflemanCount--;
                    runState.addPendingScore(10);
                }
                if(enemies.get(i).getType()== EnemyType.HEAVY){
                    heavyCount--;
                    runState.addPendingScore(25);
                }
                if(enemies.get(i).getType()== EnemyType.DRILL_CRAWLER){
                    drillerCount--;
                    runState.addPendingScore(10);
                }
                if(enemies.get(i).getType()== EnemyType.HOVER_DRONE){
                    droneCount--;
                    runState.addPendingScore(5);
                }
                runState.addKill();
                AudioAssets.playKill();
                spawnLootCrate(enemies.get(i).getEnemyDeathLocation());
                enemies.removeIndex(i);
            } 
        }
    }

    private void updateGameTracks(){
        if(runState.getKillCount() < 20){
            easyMode = true;
            mediumMode = false;
            hardMode = false;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 12f;
            DRONE_RESPAWN_TIME = 17f;
            DRILLER_RESPAWN_TIME = 22f;
            HEAVY_RESPAWN_TIME = 28f;
            MAX_ACTIVE_ENEMIES = 6;

            if(runState.getHeadshotCount()>= runState.getNextHeadshotReward()){
                runState.addPendingScore(40);
                giveGold(5);
                runState.advanceHeadshotReward();
            }

            if(runState.getBodyshotCount() >= runState.getNextBodyshotReward()){
                runState.addPendingScore(25);
                giveGold(3);
                runState.advanceBodyshotReward();
            }

            if(runState.getLimbshotCount() >= runState.getNextLimbshotReward()){
                runState.addPendingScore(15);
                giveGold(2);
                runState.advanceLimbshotReward();
            }

            if(runState.getKillCount() >= runState.getNextKillReward()){
                runState.addPendingScore(100);
                giveGold(10);
                runState.advanceKillReward();
            }
        }

        else if(runState.getKillCount() < 40){
            easyMode = false;
            mediumMode = true;
            hardMode = false;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 9f;
            DRONE_RESPAWN_TIME = 12f;
            DRILLER_RESPAWN_TIME = 15f;
            HEAVY_RESPAWN_TIME = 20f;
            MAX_ACTIVE_ENEMIES = 8;

            if(runState.getHeadshotCount() >= runState.getNextHeadshotReward()){
                runState.addPendingScore(50);
                giveGold(7);
                runState.advanceHeadshotReward();
            }

            if(runState.getBodyshotCount() >= runState.getNextBodyshotReward()){
                runState.addPendingScore(30);
                giveGold(4);
                runState.advanceBodyshotReward();
            }

            if(runState.getLimbshotCount() >= runState.getNextLimbshotReward()){
                runState.addPendingScore(20);
                giveGold(3);
                runState.advanceLimbshotReward();
            }

            if(runState.getKillCount() >= runState.getNextKillReward()){
                runState.addPendingScore(140);
                giveGold(15);
                runState.advanceKillReward();
            }
        }

        else if(runState.getKillCount()< 70){
            easyMode = false;
            mediumMode = false;
            hardMode = true;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 6f;
            DRONE_RESPAWN_TIME = 8f;
            DRILLER_RESPAWN_TIME = 10f;
            HEAVY_RESPAWN_TIME = 14f;
            MAX_ACTIVE_ENEMIES = 10;

            if(runState.getHeadshotCount() >= runState.getNextHeadshotReward()){
                runState.addPendingScore(70);
                giveGold(10);
                runState.advanceHeadshotReward();
            }

            if(runState.getBodyshotCount() >= runState.getNextBodyshotReward()){
                runState.addPendingScore(45);
                giveGold(6);
                runState.advanceBodyshotReward();
            }

            if(runState.getLimbshotCount() >= runState.getNextLimbshotReward()){
                runState.addPendingScore(30);
                giveGold(4);
                runState.advanceLimbshotReward();
            }

            if(runState.getKillCount() >= runState.getNextKillReward()){
                runState.addPendingScore(200);
                giveGold(20);
                runState.advanceKillReward();
            }
        }

        else{
            easyMode = false;
            mediumMode = false;
            hardMode = false;
            impossibleMode = true;

            RIFLEMAN_RESPAWN_TIME = 3.5f;
            DRONE_RESPAWN_TIME = 5f;
            DRILLER_RESPAWN_TIME = 6f;
            HEAVY_RESPAWN_TIME = 9f;
            MAX_ACTIVE_ENEMIES = 11;

            if(runState.getHeadshotCount() >= runState.getNextHeadshotReward()){
                runState.addPendingScore(100);
                giveGold(15);
                runState.advanceHeadshotReward();
            }

            if(runState.getBodyshotCount() >= runState.getNextBodyshotReward()){
                runState.addPendingScore(60);
                giveGold(8);
                runState.advanceBodyshotReward();
            }

            if(runState.getLimbshotCount() >= runState.getNextLimbshotReward()){
                runState.addPendingScore(40);
                giveGold(15);
                runState.advanceLimbshotReward();
            }

            if(runState.getKillCount() >= runState.getNextKillReward()){
                runState.addPendingScore(300);
                giveGold(30);
                runState.advanceKillReward();
            }
        }

        runState.addScore(modifyScoreReward(runState.getPendingScore()));
        playerProfile.updateBestScore(runState.getCurrentScore());
        runState.clearPendingScore();
    }


    private void respawnEnemies(float dt){
        int TOTAL_ENEMY_COUNT = riflemanCount + heavyCount + drillerCount + droneCount;
        if(TOTAL_ENEMY_COUNT < MAX_ACTIVE_ENEMIES){
            if(riflemanCount < MAX_RIFLEMEN){
                RIFLEMAN_RESPAWN_TIMER+=dt;
                if(RIFLEMAN_RESPAWN_TIMER >= RIFLEMAN_RESPAWN_TIME){
                    Array<Rectangle> legalSpawns = new Array<>();
                    for(Rectangle rectangle : groundEnemySpawnPoints){
                        Rectangle platform = findPlatformBelow(rectangle);
                        if(platform != null && !isPlatformOccupied(platform))
                            legalSpawns.add(rectangle);
                    }
                    if(legalSpawns.size > 0){
                        Rectangle rectangle = legalSpawns.random();
                        Enemy enemy = create_Jackal_RifleMan_FromMap(map,rectangle.x+rectangle.width/2,rectangle.y+rectangle.height/2);
                        enemies.add(enemy);
                        RIFLEMAN_RESPAWN_TIMER = 0;
                    }
                }
            }
        }

        TOTAL_ENEMY_COUNT = riflemanCount + heavyCount + drillerCount + droneCount;
        if(TOTAL_ENEMY_COUNT < MAX_ACTIVE_ENEMIES){
            if(heavyCount < MAX_HEAVIES){
                HEAVY_RESPAWN_TIMER+=dt;
                if(HEAVY_RESPAWN_TIMER >= HEAVY_RESPAWN_TIME){
                    Array<Rectangle> legalSpawns = new Array<>();
                    for(Rectangle rectangle : groundEnemySpawnPoints){
                        Rectangle platform = findPlatformBelow(rectangle);
                        if(platform != null && !isPlatformOccupied(platform))
                            legalSpawns.add(rectangle);
                    }
                    if(legalSpawns.size > 0){
                        Rectangle rectangle = legalSpawns.random();
                        Enemy enemy = create_Jackal_Heavy_FromMap( map, rectangle.x+rectangle.width/2, rectangle.y+rectangle.height/2 );
                        enemies.add(enemy);
                        HEAVY_RESPAWN_TIMER = 0;
                    }
                }
            }
        }

        TOTAL_ENEMY_COUNT = riflemanCount + heavyCount + drillerCount + droneCount;
        if(TOTAL_ENEMY_COUNT < MAX_ACTIVE_ENEMIES){
            if(drillerCount < MAX_DRILLERS){
                DRILLER_RESPAWN_TIMER+=dt;
                if(DRILLER_RESPAWN_TIMER >= DRILLER_RESPAWN_TIME){
                    Array<Rectangle> legalSpawns = new Array<>();
                    for(Rectangle rectangle : drillerSpawnPoints){
                        Rectangle platform = findPlatformBelow(rectangle);
                        if(platform != null && !isPlatformOccupied(platform))
                            legalSpawns.add(rectangle);
                    }
                    if(legalSpawns.size > 0){
                        Rectangle rectangle = legalSpawns.random();
                        Enemy enemy = create_Jackal_DrillCrawler_FromMap( map,rectangle.x+rectangle.width/2,rectangle.y+rectangle.height/2);
                        enemies.add(enemy);
                        DRILLER_RESPAWN_TIMER = 0;
                    }
                }
            }
        }
        TOTAL_ENEMY_COUNT = riflemanCount + heavyCount + drillerCount + droneCount;
        if(TOTAL_ENEMY_COUNT < MAX_ACTIVE_ENEMIES){
            if(droneCount < MAX_DRONES){
                DRONE_RESPAWN_TIMER+=dt;
                 if(DRONE_RESPAWN_TIMER >= DRONE_RESPAWN_TIME){
                    Array<Rectangle> legalSpawns = new Array<>();
                    for(Rectangle rectangle : groundEnemySpawnPoints){
                        Rectangle platform = findPlatformBelow(rectangle);
                        if(platform != null && !isPlatformOccupied(platform))
                            legalSpawns.add(rectangle);
                    }
                    if(legalSpawns.size > 0){
                        Rectangle rectangle = legalSpawns.random();
                        Enemy enemy = create_Jackal_HoverDrone_FromMap(map, rectangle.x+rectangle.width/2, rectangle.y+rectangle.height/2 );
                        enemies.add(enemy);
                        DRONE_RESPAWN_TIMER = 0;
                    }
                }
            }
        }
    }

    private void removeBullets(){
        for(int i = bullets.size-1 ; i >= 0 ; i--){
            if(bullets.get(i).isWasted())
                bullets.removeIndex(i);        
        }
    }

    private void removeEnemyProjectiles(){
        for(int i = enemyProjectiles.size-1 ; i >= 0 ; i--){
            if(enemyProjectiles.get(i).isWasted())
                enemyProjectiles.removeIndex(i);        
        }
    }

    private Array<Circle> extract_Jackal_DrillCrawler_Circles_FromMap(TiledMap map){
        MapLayer enemiesLayer = map.getLayers().get("Enemies");
        RectangleMapObject spawnObject = (RectangleMapObject)enemiesLayer.getObjects().get("Drill_Crawler_Spawn");
        Rectangle spawn = spawnObject.getRectangle();
        Array<Circle> circles = new Array<>();
        for(EllipseMapObject object : enemiesLayer.getObjects().getByType(EllipseMapObject.class)){
            if(object.getName() == null || !object.getName().startsWith("Drill_Crawler_Circle_"))
                continue;
            Ellipse ellipse = object.getEllipse();
            float centerX = ellipse.x + ellipse.width/2f;
            float centerY = ellipse.y + ellipse.height/2f;
            float radius = ellipse.width/2f;
            circles.add(new Circle(centerX - spawn.x,centerY - spawn.y,radius));
        }
        return circles;
    }

    private Rectangle getHeavyPortalBounds(){
        float visibleWidth = camera.viewportWidth * camera.zoom;
        float visibleHeight = camera.viewportHeight * camera.zoom;

        float cameraLeft = camera.position.x-visibleWidth/2f;
        float cameraBottom = camera.position.y-visibleHeight/2f;
        float horizontalMargin = 200f;
        float bottomMargin = 200f;
        float topMargin = 350f;

        return new Rectangle(cameraLeft+horizontalMargin,cameraBottom+bottomMargin,visibleWidth-horizontalMargin*2f,visibleHeight-bottomMargin-topMargin);
    }

    private void communicateDamage(){
        float damage = 0;
        for(Enemy enemy : enemies){
            damage+= enemy.getPendingDamage();
            enemy.resetPendingDamage();
        }
        damagePlayer(damage);
    }

    private void drawPlayerHealthHud(){
        float ratio = MathUtils.clamp(player.getHP()/player.getMaxHp(),0f,1f);
        float x = 24f;
        float width = 220f;

        float scale = width/512f;
        float height = 80f*scale;
        
        float y = Gdx.graphics.getHeight()-height-24f;
        float fillX = x+88f*scale;
        float fillY = y+28f*scale;


        float fullFillWidth = 384f*scale;
        float fillWidth = fullFillWidth*ratio;
        float fillHeight = 24f*scale;

        float iconX = x+20f*scale;
        float iconY = y+16f*scale;
        float iconSize = 50f*scale;
        
        batch.draw(FxAssets.playerHealthFrame,x,y,width,height);
         if(ratio > 0)
            batch.draw(FxAssets.playerHealthFill,fillX,fillY,fillWidth,fillHeight);
         batch.draw(FxAssets.playerHealthIcon,iconX,iconY,iconSize,iconSize);
    }

    private float getScopeMagnification(){
        if(scopeStage == 0)
            return 1f;
        float progress = scopeStage / 3f;
        return (float)Math.pow(weapon.getMaxScope(),progress);
    }

    private void drawJetpackHud(){
        float ratio = MathUtils.clamp(player.getJetpackFuel()/player.getMaxJetpackFuel(),0f,1f);

        float x = 24f;
        float width = 220f;

        float scale = width/512f;
        float height = 80f * scale;

        float gap = 10f;
        float healthY = Gdx.graphics.getHeight() - height - 24f;
        float y = healthY - height - gap;
    

        float fullFillWidth = 384 * scale;
        float fillWidth = fullFillWidth*ratio;
        float fillHeight = 24f * scale;

        float fillX = x + 88 * scale;
        float fillY = y + 28f * scale;

        float iconX = x+20f*scale;
        float iconY = y+16f*scale;
        float iconSize = 50f*scale;

        batch.draw(FxAssets.playerJetpackFrame,x,y,width,height);

        if(ratio > 0)
            batch.draw(FxAssets.playerJetpackFill,fillX,fillY,fillWidth,fillHeight);
        batch.draw(FxAssets.playerJetpackIcon , iconX, iconY, iconSize , iconSize);
    }

    private void createEnemyProjectile(){
        for(Enemy enemy: enemies){
            if(enemy.isRiflemanShotReady()){
                Vector2 spawn = new Vector2(enemy.getRiflemanMuzzlePosition());
                Vector2 target = new Vector2(player.getPlayerCentrePosition());
                enemyProjectiles.add(new EnemyProjectile(spawn,target,1400f,riflemanProjectileTexture,enemy.getRiflemanDamage(),20f,80,65));
                enemy.consumeRiflemanShot();
            }

            if(enemy.isHeavyShotReady()){
                Vector2 spawn = new Vector2(enemy.getHeavyMuzzlePosition());
                Vector2 target = new Vector2(player.getPlayerCentrePosition());
                enemyProjectiles.add(new EnemyProjectile(spawn,target,1100f,heavyProjectileTexture,enemy.getHeavyCloseDamage(),40f,300,350));
                enemy.consumeHeavyShot();
            }
        }

    }

    private float getProjectileSpeed(WeaponType type){
        if(type == WeaponType.ROCKET_LAUNCHER)
            return 900f;
        return 2500f;
    }

    private boolean isPlayerPoisoned(){
        for(Enemy enemy : enemies){
            if(enemy.isPlayerPoisoned())
                return true;
        }

        return false;
    }

    private boolean isPlayerStunned(){
        for(Enemy enemy : enemies){
            if(enemy.isPlayerStunned())
                return true;
        }

        return false;
    }


    private void drawPlayerPoisonFx(float dt){
        boolean poisoned = isPlayerPoisoned();
        if(poisoned && !poisonStatusWasActive){
            poisonStatusFxTimer = 0f;
            poisonStatusWasActive = true;
            poisonStatusEnding = false;
        }

        if(!poisoned && poisonStatusWasActive){
            poisonStatusFxTimer = 0f;
            poisonStatusWasActive = false;
            poisonStatusEnding = true;
        }
        TextureRegion frame = null;

        if(poisoned){
            poisonStatusFxTimer += dt;
            float startFrameDuration = 0.05f;
            float startDuration = FxAssets.playerPoisonStart.length * startFrameDuration;
            if(poisonStatusFxTimer < startDuration){
                int frameIndex = (int)(poisonStatusFxTimer/startFrameDuration);
                frame = FxAssets.playerPoisonStart[frameIndex];
            }
            else{
                float lingeringTime = poisonStatusFxTimer-startDuration;
                int frameIndex = (int)(lingeringTime/0.10f) % FxAssets.playerPoisonLingering.length;
                frame = FxAssets.playerPoisonLingering[frameIndex];
            }
        }
        else if(poisonStatusEnding){
            poisonStatusFxTimer += dt;
            float endFrameDuration = 0.06f;
            float endDuration = FxAssets.playerPoisonEnd.length * endFrameDuration;
            if(poisonStatusFxTimer < endDuration){
                int frameIndex = (int)(poisonStatusFxTimer/endFrameDuration);
                frame = FxAssets.playerPoisonEnd[frameIndex];
            }
            else{
                poisonStatusEnding = false;
                poisonStatusFxTimer = 0f;
            }
        }
        if(frame == null)
            return;
        Vector2 playerPosition = player.getPlayerPosition();

        float sx = player.getPlayerWidth()/278f;
        float sy = player.getPlayerHeight()/461f;

        float drawX = playerPosition.x-61f*sx;
        float drawY = playerPosition.y-79.5f*sy;

        float drawWidth = 400f*sx;
        float drawHeight = 620f*sy;

        float lingeringScale = 2.0f;
        float lingeringWidth = drawWidth*lingeringScale;
        float lingeringHeight = drawHeight*lingeringScale;

        float lingeringX = drawX-(lingeringWidth-drawWidth)/2f;
        float lingeringY = drawY-(lingeringHeight-drawHeight)/2f;

        batch.draw(frame,lingeringX,lingeringY,lingeringWidth,lingeringHeight);
        
    }


    private void drawPlayerStunFx(float dt){
        boolean stunned = isPlayerStunned();
        if(stunned && !stunStatusWasActive){
            stunStatusFxTimer = 0f;
            stunStatusWasActive = true;
            stunStatusEnding = false;
        }
        if(!stunned && stunStatusWasActive){
            stunStatusFxTimer = 0f;
            stunStatusWasActive = false;
            stunStatusEnding = true;
        }
        TextureRegion frame = null;
        if(stunned){
            stunStatusFxTimer += dt;
            float startFrameDuration = 0.05f;
            float startDuration = FxAssets.playerStunStart.length * startFrameDuration;
            if(stunStatusFxTimer < startDuration){
                int frameIndex = (int)(stunStatusFxTimer/startFrameDuration);
                frame = FxAssets.playerStunStart[frameIndex];
            }
            else{
                float lingeringTime = stunStatusFxTimer-startDuration;
                int frameIndex = (int)(lingeringTime/0.08f) % FxAssets.playerStunLingering.length;
                frame = FxAssets.playerStunLingering[frameIndex];
            }
        }
        else if(stunStatusEnding){
            stunStatusFxTimer += dt;
            float endFrameDuration = 0.06f;
            float endDuration = FxAssets.playerStunEnd.length * endFrameDuration;
            if(stunStatusFxTimer < endDuration){
                int frameIndex = (int)(stunStatusFxTimer/endFrameDuration);
                frame = FxAssets.playerStunEnd[frameIndex];
            }
            else{
                stunStatusEnding = false;
                stunStatusFxTimer = 0f;
            }
        }

        if(frame == null)
            return;

        Vector2 playerPosition = player.getPlayerPosition();
        float sx = player.getPlayerWidth()/278f;
        float sy = player.getPlayerHeight()/461f;

        float drawX = playerPosition.x-61f*sx;
        float drawY = playerPosition.y-79.5f*sy;

        float drawWidth = 400f*sx;
        float drawHeight = 620f*sy;

        float lingeringScale = 1.8f;
        float lingeringWidth = drawWidth*lingeringScale;
        float lingeringHeight = drawHeight*lingeringScale;

        float lingeringX = drawX-(lingeringWidth-drawWidth)/2f;
        float lingeringY = drawY-(lingeringHeight-drawHeight)/2f;

        batch.draw(frame, lingeringX, lingeringY, lingeringWidth, lingeringHeight);
    }

    private void drawBombardmentProjectiles(){
        for(Enemy enemy : enemies){
            if(enemy.getType() != EnemyType.HEAVY)
                continue;
            float heavyPortalFxTimer = enemy.getHeavyPortalFxTimer();
            int frameIndex = (int)(heavyPortalFxTimer/0.06f) % FxAssets.bombardmentRainBolt.length;
            TextureRegion frame = FxAssets.bombardmentRainBolt[frameIndex];
            for(Rectangle projectile : enemy.getBombardmentProjectiles()){
                float centerX = projectile.x+projectile.width/2f;
                float centerY = projectile.y+projectile.height/2f;

                float width = 300f;
                float height = 250f;

                batch.draw(frame, centerX-width/2f, centerY-height/2f, width, height);
            }
        }
    }

    private void collectBombardmentImpacts(){
        for(Enemy enemy : enemies){
            if(enemy.getType() != EnemyType.HEAVY)
                continue;
            Vector2 impact;
            while((impact = enemy.consumeBombardmentImpact()) != null)
                bombardmentImpactFx.add(new BombardmentImpactFx(impact));
        }
    }


    private void drawBombardmentImpacts(float dt){
        for(int i=bombardmentImpactFx.size-1;i>=0;i--){
            BombardmentImpactFx impact = bombardmentImpactFx.get(i);
            impact.timer+=dt;
            int frameIndex = (int)(impact.timer/0.05f);

            if(frameIndex >= FxAssets.bombardmentRainImpact.length){
                bombardmentImpactFx.removeIndex(i);
                continue;
            }
            TextureRegion frame = FxAssets.bombardmentRainImpact[frameIndex];
            float size = 140f;
            batch.draw( frame,impact.position.x-size/2f,impact.position.y-size/2f, size,size);
        }
    }

    private void drawAmmoHud(){
        float ammoPanelWidth = 230f;
        float ammoPanelScale = ammoPanelWidth/256f;
        float ammoPanelHeight = 80f*ammoPanelScale;

        float controlScale = 0.70f;
        float scopeButtonWidth = 224f*controlScale;
        float scopeButtonHeight = 96f*controlScale;
        float reloadButtonSize = 65f;
        float controlsGap = 8f;
        float bottomMargin = 24f;

        float ammoPanelX = Gdx.graphics.getWidth()-ammoPanelWidth-24f;
        float ammoPanelY = bottomMargin+scopeButtonHeight+controlsGap;

        float ammoIconX = ammoPanelX+18f*ammoPanelScale;
        float ammoIconY = ammoPanelY+16f*ammoPanelScale;
        float ammoIconSize = 48f*ammoPanelScale;

        batch.draw(weapon.getAmmoPanelTexture(),ammoPanelX,ammoPanelY,ammoPanelWidth,ammoPanelHeight);

        if(weapon.getCurrentAmmo()>5)
            batch.draw(weapon.getAmmoIconTexture(),ammoIconX,ammoIconY,ammoIconSize,ammoIconSize);
        else
            batch.draw(weapon.getNoAmmoIconTexture(),ammoIconX,ammoIconY,ammoIconSize,ammoIconSize);

        String magazineText = weapon.getCurrentAmmo()+" / "+weapon.getMagazineSize();
        String reserveText = "RES "+runState.getAmmo(weapon.getCurrentWeapon());

        hudFont.getData().setScale(1.15f);
        hudFont.draw(batch,magazineText,ammoPanelX+90f*ammoPanelScale,ammoPanelY+55f*ammoPanelScale);

        hudFont.getData().setScale(0.75f);
        hudFont.draw(batch,reserveText,ammoPanelX+90f*ammoPanelScale,ammoPanelY+30f*ammoPanelScale);

        float scopeButtonX = ammoPanelX;
        float scopeButtonY = bottomMargin;
        scopeButtonBounds.set(scopeButtonX,scopeButtonY,scopeButtonWidth,scopeButtonHeight);

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

        Texture scopeButtonTexture = FxAssets.scopeButton;
        if(scopeButtonBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            scopeButtonTexture = FxAssets.scopeButtonPressed;
        else if(scopeButtonBounds.contains(mouseX,mouseY))
            scopeButtonTexture = FxAssets.scopeButtonHover;

        batch.draw(scopeButtonTexture,scopeButtonX,scopeButtonY,scopeButtonWidth,scopeButtonHeight);

        float scopeScaleX = scopeButtonWidth/224f;
        float scopeScaleY = scopeButtonHeight/96f;

        float scopeValueX = scopeButtonX+108f*scopeScaleX;
        float scopeValueY = scopeButtonY+42f*scopeScaleY;
        float scopeValueWidth = 96f*scopeScaleX;
        float scopeValueHeight = 36f*scopeScaleY;

        String scopeValueText = String.format("%.1fx",getScopeMagnification());
        hudFont.getData().setScale(0.85f);
        scopeValueLayout.setText(hudFont,scopeValueText);

        float scopeTextX = scopeValueX+(scopeValueWidth-scopeValueLayout.width)/2f;
        float scopeTextY = scopeValueY+(scopeValueHeight+scopeValueLayout.height)/2f;
        hudFont.draw(batch,scopeValueText,scopeTextX,scopeTextY);

        float reloadButtonX = scopeButtonX+scopeButtonWidth+controlsGap;
        float reloadButtonY = scopeButtonY+(scopeButtonHeight-reloadButtonSize)/2f;

        batch.draw(FxAssets.reloadIcon,reloadButtonX,reloadButtonY,reloadButtonSize,reloadButtonSize);
        reloadButtonBounds.set(reloadButtonX,reloadButtonY,reloadButtonSize,reloadButtonSize);

        float killBadgeWidth = 176f*controlScale;
        float killBadgeHeight = 68f*controlScale;
        float killBadgeX = ammoPanelX+ammoPanelWidth-killBadgeWidth;
        float killBadgeY = ammoPanelY+ammoPanelHeight+controlsGap;

        batch.draw(FxAssets.killBadge,killBadgeX,killBadgeY,killBadgeWidth,killBadgeHeight);

        float killScaleX = killBadgeWidth/176f;
        float killScaleY = killBadgeHeight/68f;

        float killValueX = killBadgeX+72f*killScaleX;
        float killValueY = killBadgeY+9f*killScaleY;
        float killValueWidth = 88f*killScaleX;
        float killValueHeight = 32f*killScaleY;

        String killValueText = Integer.toString(runState.getKillCount());
        hudFont.getData().setScale(0.85f);
        scopeValueLayout.setText(hudFont,killValueText);

        float killTextX = killValueX+(killValueWidth-scopeValueLayout.width)/2f;
        float killTextY = killValueY+(killValueHeight+scopeValueLayout.height)/2f;
        hudFont.draw(batch,killValueText,killTextX,killTextY);
    }

    private void updateScopeTarget(){
        float magnification = getScopeMagnification();
        targetCameraZoom = magnification;
    }

    private void cycleScopeStage(){
        float oldScope = getScopeMagnification();
        scopeStage++;
        if(scopeStage >= SCOPE_STAGE_COUNT)
            scopeStage = 0;

        float newScope = getScopeMagnification();
        if(newScope > oldScope)
            AudioAssets.playScopeWiden();
        else
            AudioAssets.playScopeNarrow();
        updateScopeTarget();
    }

    private void updateScope(float dt){
        camera.zoom = MathUtils.lerp(camera.zoom,targetCameraZoom,Math.min(1f,dt*SCOPE_ZOOM_SPEED));
        camera.update();
    }


    private Array<Rectangle> extractGroundEnemySpawnPoints(){
        Array<Rectangle> spawnPoints = new Array<>();
        MapLayer spawnLayer = map.getLayers().get("spawns");
        for(RectangleMapObject object : spawnLayer.getObjects().getByType(RectangleMapObject.class)){
            if(object.getName() != null &&
            object.getName().startsWith("rifleman_")){
                spawnPoints.add(new Rectangle(object.getRectangle()));
            }
        }
        return spawnPoints;
    }

    private Array<Rectangle> extractDrillerSpawnPoints(){
        Array<Rectangle> spawnPoints = new Array<>();
        MapLayer spawnLayer = map.getLayers().get("spawns");
        for(RectangleMapObject object : spawnLayer.getObjects().getByType(RectangleMapObject.class)){
            if("drill".equals(object.getName())){
                spawnPoints.add(new Rectangle(object.getRectangle()));
            }
        }
        return spawnPoints;
    }


    private boolean isPlatformOccupied(Rectangle platform){
        for(Enemy enemy : enemies){
            if(enemy.getType() == EnemyType.HOVER_DRONE)
                continue;
            if(enemy.isOnPlatform(platform))
                return true;
        }
        return false;
    }

    private Rectangle findPlatformBelow(Rectangle spawn){
        float spawnX = spawn.x+spawn.width/2f;
        float spawnY = spawn.y+spawn.height/2f;

        Rectangle closestPlatform = null;
        float closestDistance = Float.MAX_VALUE;
        for(Rectangle platform : solidPlatforms){
            float platformTop = platform.y+platform.height;
            boolean abovePlatform = spawnX >= platform.x && spawnX <= platform.x+platform.width && platformTop <= spawnY;
            if(abovePlatform){
                float distance = spawnY-platformTop;
                if(distance < closestDistance){
                    closestDistance = distance;
                    closestPlatform = platform;
                }
            }
        }

        return closestPlatform;
    }

    private void drawScoreDashboard(){
        float width = 360f*1.12f;
        float scale = width/640f;
        float height = 96f*scale;

        float x = Gdx.graphics.getWidth()-width-24f;
        float y = Gdx.graphics.getHeight()-height-8f;

        batch.draw(FxAssets.scoreDashboard,x,y,width,height);

        float zoneY = y+28f*scale;
        float iconSize = 36f*scale;

        float goldX = x+30f*scale;
        float scoreX = x+218f*scale;
        float bestX = x+442f*scale;

        float keyIconSize = 28f;
        float keyIconX = x+width-keyIconSize;
        float keyIconY =  y-keyIconSize-6f;

        String keyText = runState.hasKey() ? "x1" : "x0";

        batch.draw(FxAssets.goldIcon,goldX,zoneY,iconSize,iconSize);
        batch.draw(FxAssets.scoreIcon,scoreX,zoneY,iconSize,iconSize);
        batch.draw(FxAssets.bestScoreIcon,bestX,zoneY,iconSize,iconSize);
        batch.draw(FxAssets.keyIcon,keyIconX,keyIconY,keyIconSize,keyIconSize);

        hudFont.draw(batch,keyText,keyIconX-26f,keyIconY+21f);
        hudFont.draw(batch,Integer.toString(inventory.getGold()),goldX+48f*scale,zoneY+27f*scale);
        hudFont.draw(batch,Integer.toString(runState.getCurrentScore()),scoreX+48f*scale,zoneY+27f*scale);
        hudFont.draw(batch,Integer.toString(playerProfile.getBestScore()),bestX+48f*scale,zoneY+27f*scale);
    }


    private void reloadGun(){
        if(reloading)
            return;
        if(infiniteMagazineActive())
            return;
        if(weapon.getCurrentAmmo() >= weapon.getMagazineSize())
            return;
        if(runState.getAmmo(weapon.getCurrentWeapon()) <= 0)
            return;

        reloadInsertPlayed = false;
        AudioAssets.playWeaponReloadStart(weapon.getCurrentWeapon());
        reloadReadyFxActive = false;
        reloadReadyFxTimer = 0f;
        reloading = true;
        reloadTimer = 0f;
    }

    private void updateReload(float dt){
        if(!reloading)
            return;
        reloadTimer+=dt;
        if(!reloadInsertPlayed && getReloadProgress() >= 0.60f){
            AudioAssets.playWeaponReloadInsert(weapon.getCurrentWeapon());
            reloadInsertPlayed = true;
        }
        if(reloadTimer < weapon.getReloadSpeed())
            return;
        int neededAmmo = weapon.getMagazineSize()-weapon.getCurrentAmmo();
        int ammoToLoad = runState.takeAmmo(weapon.getCurrentWeapon(),neededAmmo);
        weapon.addCurrentAmmo(ammoToLoad);
        AudioAssets.playWeaponReloadFinish(weapon.getCurrentWeapon());

        reloadTimer = 0f;
        reloading = false;
        reloadReadyFxActive = true;
        reloadReadyFxTimer = 0f;
    }
    
    private void spawnLootCrate(Vector2 enemyDeathLocation){
        Rectangle enemyDeath = new Rectangle (enemyDeathLocation.x , enemyDeathLocation.y , crateWidth , crateHeight);
        Rectangle spawnPlatform = findPlatformBelow(enemyDeath);
        if(spawnPlatform == null)
            return;
        float crateX = enemyDeathLocation.x;
        float crateY = spawnPlatform.y + spawnPlatform.height;
        Rectangle crateRect = new Rectangle(crateX,crateY,crateWidth,crateHeight);
        //int roll = MathUtils.random(1,100);



        int roll = MathUtils.random(1,3);
        if(roll == 1){
            LootCrate crate = new LootCrate(CrateType.SUPER_RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll == 2){
            LootCrate crate = new LootCrate(CrateType.RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else{
            LootCrate crate = new LootCrate(CrateType.COMMON,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        
    /*if(easyMode){
        if(roll <= 3){
            LootCrate crate = new LootCrate(CrateType.SUPER_RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 12){
            LootCrate crate = new LootCrate(CrateType.RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 40){
            LootCrate crate = new LootCrate(CrateType.COMMON,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
    }

    else if(mediumMode){
        if(roll <= 5){
            LootCrate crate = new LootCrate(CrateType.SUPER_RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 18){
            LootCrate crate = new LootCrate(CrateType.RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 45){
            LootCrate crate = new LootCrate(CrateType.COMMON,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
    }

    else if(hardMode){
        if(roll <= 8){
            LootCrate crate = new LootCrate(CrateType.SUPER_RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 28){
            LootCrate crate = new LootCrate(CrateType.RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <=50){
            LootCrate crate = new LootCrate(CrateType.COMMON,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
    }

    else if(impossibleMode){
        if(roll <= 15){
            LootCrate crate = new LootCrate(CrateType.SUPER_RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 38){
            LootCrate crate = new LootCrate(CrateType.RARE,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
        else if(roll <= 60){
            LootCrate crate = new LootCrate(CrateType.COMMON,crateRect);
            lootCrates.add(crate);
            AudioAssets.playCrateSpawn(crate.getType());
        }
    }*/
}

private Array<Rectangle> extractSpecialCrateSpawnPoints(){
    Array<Rectangle> spawnPoints = new Array<>();
    MapLayer spawnLayer = map.getLayers().get("spawns");
    for(RectangleMapObject object : spawnLayer.getObjects().getByType(RectangleMapObject.class)){
        if("special_crate".equals(object.getName()))
            spawnPoints.add(new Rectangle(object.getRectangle()));
    }
    return spawnPoints;
}


private void spawnSpecialCrate(float dt){
    boolean specialAlreadyExists = false;
    for(LootCrate crate : lootCrates){
        if(crate.getType() == CrateType.SPECIAL){
            specialAlreadyExists = true;
            break;
        }
    }
    if(specialAlreadyExists)
        return;
    SPECIAL_CRATE_TIMER+=dt;
    if(SPECIAL_CRATE_TIMER < SPECIAL_CRATE_SPAWN_TIME)
        return;
    if(specialCrateSpawnPoints.size == 0)
        return;
    Rectangle spawn = specialCrateSpawnPoints.random();
    Rectangle crateRect = new Rectangle(spawn.x,spawn.y,crateWidth,crateHeight);
    LootCrate crate = new LootCrate(CrateType.SPECIAL,crateRect);
    lootCrates.add(crate);
    AudioAssets.playCrateSpawn(crate.getType());
    SPECIAL_CRATE_TIMER = 0f;
    //SPECIAL_CRATE_SPAWN_TIME = MathUtils.random(45f,90f);
    SPECIAL_CRATE_SPAWN_TIME = MathUtils.random(1f,10f);
}


private void openNearbyCrate(){
    Rectangle playerHitbox = player.getWorldHitbox();
    for(int i=0;i<lootCrates.size;i++){
        LootCrate crate = lootCrates.get(i);
        if(crate.isOpened())
            continue;
        Rectangle crateBounds = crate.getCrate();
        Rectangle interactionZone = new Rectangle(crateBounds.x-25f,crateBounds.y-15f,crateBounds.width+50f,crateBounds.height+30f);
        if(playerHitbox.overlaps(interactionZone)){
            activeLootCrate = crate;
            crate.openCrate();
            AudioAssets.playCrateOpen(crate.getType());
            break;
        }
    }
}

private void removeLootCrates(){
    for(int i=lootCrates.size-1;i>=0;i--){
        LootCrate crate = lootCrates.get(i);
        if(crate.isReadyToRemove()){
            if(crate == activeLootCrate)
                activeLootCrate = null;
            lootCrates.removeIndex(i);
        }
    }
}

private void drawLootDashboard(){
    if(activeLootCrate == null)
        return;
    if(!activeLootCrate.isOpened())
        return;
    if(!activeLootCrate.openingFinished())
        return;
    Texture dashboard = activeLootCrate.getDashboardTexture();
    Rectangle crate = activeLootCrate.getCrate();

    float height = 66f;
    float width = height * ((float)dashboard.getWidth()/dashboard.getHeight());
    float crateCenterX =crate.x+crate.width/2f;
    float x =crateCenterX-width/2f;
    float y =crate.y+crate.height+30f;
    batch.draw( dashboard, x, y, width, height);
    drawLootRewards(x,y,width,height,dashboard);
}

private void claimActiveCrate(){
    if(activeLootCrate == null)
        return;
    if(!activeLootCrate.isOpened())
        return;
    if(!activeLootCrate.openingFinished())
        return;
    updatePendingLoot(activeLootCrate.claim());
    activeLootCrate = null;
}

private void updatePendingLoot(LootCrate.Contents loot){
    runState.addScore(modifyScoreReward(loot.getScore()));
    playerProfile.updateBestScore(runState.getCurrentScore());
    giveGold(loot.getGold());
    runState.addAmmo(weapon.getCurrentWeapon(), (loot.getAmmoReward()* WEAPON_INITIAL_CAPACITY*weapon.getMagazineSize())/100);
    inventory.addPowerupFromArray(loot.getPowerups());

    for(WeaponType type : loot.getWeapons()){
        boolean alreadyOwned = inventory.ownsWeapon(type);
        inventory.addWeapon(type);
        int initialAmmo = WEAPON_INITIAL_CAPACITY*weapon.getBaseMagazineSize(type);
        if(alreadyOwned)
            runState.addAmmo(type,initialAmmo*2);
        else
            runState.addAmmo(type,initialAmmo);
    }

    if(loot.hasJackpot()){
        AudioAssets.playJackpot();
        int goldCount = MathUtils.random(200,350);
        giveGold(goldCount);
    }
    if(loot.hasBiggerJackpot()){
        AudioAssets.playBiggerJackpot();
        int goldCount = MathUtils.random(600,800);
        giveGold(goldCount);
    }
    player.heal(loot.getHpBoost());
    
    if(loot.getSpicy() != null){
        inventory.addSpicy(loot.getSpicy(),1);
    }

    if(loot.getMoreSpicy()!=null){
        inventory.addMoreSpicy(loot.getMoreSpicy(),1);
    }

    if(loot.hasGodMode()){
        inventory.addGodMode(1);
    }
     
}

private Array<LootVisual> getLootVisuals(LootCrate crate){
    Array<LootVisual> rewards = new Array<>();
    LootCrate.Contents contents = crate.getContents();
    if(crate.getType() == CrateType.SPECIAL)
        return rewards;

    rewards.add(new LootVisual(FxAssets.goldRewardIcon, "+"+contents.getGold()));
    rewards.add(new LootVisual(FxAssets.scoreRewardIcon,"+"+contents.getScore()));
    rewards.add(new LootVisual(FxAssets.ammoRewardIcon,"+"+contents.getAmmoReward()+"%"));

    for(Powerup.PowerupType powerup : contents.getPowerups())
        rewards.add(new LootVisual( FxAssets.powerupIcons.get(powerup), ""));

    if(contents.getSpicy() != null)
        rewards.add(new LootVisual(FxAssets.spicyIcons.get(contents.getSpicy()),""));

    if(contents.getMoreSpicy() != null)
        rewards.add(new LootVisual(FxAssets.moreSpicyIcons.get(contents.getMoreSpicy()),""));

    if(contents.hasJackpot())
        rewards.add(new LootVisual(FxAssets.jackpotIcon,""));
    
    if(contents.hasBiggerJackpot())
        rewards.add(new LootVisual(FxAssets.biggerJackpotIcon,""));
    
    if(contents.hasGodMode())
        rewards.add(new LootVisual(FxAssets.godModeIcon,""));
    
    return rewards;
}

private void drawRegularReward(LootVisual reward,int slot,float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    float sx = dashboardWidth/dashboard.getWidth();
    float sy = dashboardHeight/dashboard.getHeight();

    float x = dashboardX+(86f+268f*slot)*sx;
    float y = dashboardY+90f*sy;

    drawContain(reward.icon,x,y,148f*sx,148f*sy);

    drawLootValue(reward.value,slot,dashboardX,dashboardY,dashboardWidth,dashboardHeight,dashboard);
}

private void drawLootQuantity(String text,float x,float y,float width,float height){
    if(width <= 0f || height <= 0f)
        return;

    if(text == null || text.isEmpty())
        text = "x1";

    lootCountFont.getData().setScale(1f);
    lootValueLayout.setText(lootCountFont,text);

    float scale = Math.min(
        width*0.90f/Math.max(lootValueLayout.width,1f),
        height*0.85f/Math.max(lootValueLayout.height,1f)
    );

    lootCountFont.getData().setScale(scale);
    lootValueLayout.setText(lootCountFont,text);

    float textX = x+(width-lootValueLayout.width)/2f;
    float textY = y+(height+lootValueLayout.height)/2f;

    lootCountFont.draw(batch,lootValueLayout,textX,textY);
}


private void drawLootValue(String text,int slot,float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    float sx = dashboardWidth/dashboard.getWidth();
    float sy = dashboardHeight/dashboard.getHeight();

    float x = dashboardX+(52f+268f*slot)*sx;
    float y = dashboardY+34f*sy;

    drawLootQuantity(text,x,y,216f*sx,34f*sy);
}

private void drawContain(Texture texture,float x,float y,float width,float height){
    if(width <= 0f || height <= 0f)
        return;
    TextureRegion region = getLootRegion(texture);

    float scale = Math.min(width/region.getRegionWidth(),height/region.getRegionHeight())*0.95f;

    float w = region.getRegionWidth()*scale;
    float h = region.getRegionHeight()*scale;

    batch.draw(region,x+(width-w)/2f,y+(height-h)/2f,w,h);
}

private void drawWeaponReward(Weapon.WeaponType weapon,float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    float sx = dashboardWidth/dashboard.getWidth();
    float sy = dashboardHeight/dashboard.getHeight();

    float sourceX = dashboard.getWidth()-520f;

    drawContain(FxAssets.weaponRewardIcons.get(weapon),dashboardX+sourceX*sx,dashboardY+96f*sy,464f*sx,136f*sy);

    drawLootQuantity("x1",dashboardX+(sourceX-4f)*sx,dashboardY+34f*sy,472f*sx,34f*sy);
}

private void drawLootRewards(float x,float y,float width,float height,Texture dashboard){
    if(activeLootCrate == null)
        return;

    if(activeLootCrate.getType() == LootCrate.CrateType.SPECIAL){
        drawSpecialLootRewards(x,y,width,height,dashboard);
        return;
    }

    Array<LootVisual> rewards = getLootVisuals(activeLootCrate);
    Array<Weapon.WeaponType> weapons = activeLootCrate.getContents().getWeapons();

    for(int i=0;i<rewards.size;i++)
        drawRegularReward(rewards.get(i),i,x,y,width,height,dashboard);

    if(weapons.size > 0)
        drawWeaponReward(weapons.first(),x,y,width,height,dashboard);
    
}

private void drawSpecialWeapon(Weapon.WeaponType weapon,int index,float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    float sx = dashboardWidth/dashboard.getWidth();
    float sy = dashboardHeight/dashboard.getHeight();

    float sourceX = 56f+524f*index;

    drawContain(FxAssets.weaponRewardIcons.get(weapon),dashboardX+sourceX*sx,dashboardY+96f*sy,464f*sx,136f*sy);

    drawLootQuantity("x1",dashboardX+(sourceX-4f)*sx,dashboardY+34f*sy,472f*sx,34f*sy);
}

private void drawSpecialReward(Texture icon,int index,float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    float sx = dashboardWidth/dashboard.getWidth();
    float sy = dashboardHeight/dashboard.getHeight();

    float sourceX = 1134f+268f*index;

    drawContain(icon,dashboardX+sourceX*sx,dashboardY+90f*sy,148f*sx,148f*sy);

    drawLootQuantity("x1",dashboardX+(sourceX-34f)*sx,dashboardY+34f*sy,216f*sx,34f*sy);
}

private void drawSpecialLootRewards(float dashboardX,float dashboardY,float dashboardWidth,float dashboardHeight,Texture dashboard){
    LootCrate.Contents contents = activeLootCrate.getContents();
    Array<Weapon.WeaponType> weapons = contents.getWeapons();

    if(weapons.size > 0)
        drawSpecialWeapon(weapons.get(0),0,dashboardX,dashboardY,dashboardWidth,dashboardHeight,dashboard);
    
    if(weapons.size > 1)
        drawSpecialWeapon(weapons.get(1),1,dashboardX,dashboardY,dashboardWidth,dashboardHeight,dashboard);

    if(contents.getMoreSpicy() != null)
        drawSpecialReward(FxAssets.moreSpicyIcons.get(contents.getMoreSpicy()),0,dashboardX,dashboardY,dashboardWidth,dashboardHeight,dashboard);

    drawSpecialReward( FxAssets.biggerJackpotIcon, 1, dashboardX,dashboardY,dashboardWidth,dashboardHeight,dashboard);
    drawSpecialReward(FxAssets.godModeIcon,2, dashboardX,dashboardY, dashboardWidth,dashboardHeight, dashboard);
}

private TextureRegion getLootRegion(Texture texture){
    if(texture == null)
        throw new IllegalArgumentException("Missing loot icon texture");

    TextureRegion cached = lootIconRegions.get(texture);
    if(cached != null)
        return cached;

    TextureRegion region = new TextureRegion(texture);
    TextureData data = texture.getTextureData();

    if(data.getType() == TextureData.TextureDataType.Pixmap){
        if(!data.isPrepared())
            data.prepare();
        Pixmap pixmap = data.consumePixmap();
        try{
            int left = pixmap.getWidth(),top = pixmap.getHeight();
            int right = -1,bottom = -1;

            for(int py=0;py<pixmap.getHeight();py++){
                for(int px=0;px<pixmap.getWidth();px++){
                    if((pixmap.getPixel(px,py)&255) == 0)
                        continue;

                    left = Math.min(left,px);
                    right = Math.max(right,px);
                    top = Math.min(top,py);
                    bottom = Math.max(bottom,py);
                }
            }
            if(right >= left){
                // Keep two source pixels around the visible artwork.
                left = Math.max(0,left-2);
                top = Math.max(0,top-2);
                right = Math.min(pixmap.getWidth()-1,right+2);
                bottom = Math.min(pixmap.getHeight()-1,bottom+2);

                region = new TextureRegion(
                    texture,left,top,right-left+1,bottom-top+1
                );
            }
        }
        finally{
            if(data.disposePixmap())
                pixmap.dispose();
        }
    }

    lootIconRegions.put(texture,region);
    return region;
}


private void consumePowerup(Powerup.PowerupType type){
    if(!inventory.usePowerup(type))
        return;

    if(type == Powerup.PowerupType.HEALTH){
        player.heal(player.getMaxHp()*0.80f);
        AudioAssets.playPowerActivate("health");
        startPowerAuraBurst("health");
        return;
    }

    if(type == Powerup.PowerupType.JETPACK){
        player.setJetpackFuel(player.getMaxJetpackFuel());
        AudioAssets.playPowerActivate("jetpack");
        startPowerAuraBurst("jetpack");
        return;
    }

    if(type == Powerup.PowerupType.SHIELD){
        if(FORTRESS_ACTIVE)
            return;
        SHIELD_ACTIVE = true;
        SHIELD_AMOUNT = SHIELD_CAPACITY;
        AudioAssets.playPowerActivate("shield");
        return;
    }

    if(type == Powerup.PowerupType.FIRE_RATE){
        if(FIRE_RATE_ACTIVE || OVERDRIVE_ACTIVE || BERSERK_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE)
            return;

        FIRE_RATE_ACTIVE = true;
        FIRE_RATE_TIMER = 0f;
        AudioAssets.playPowerActivate("fire_rate");
        refreshWeaponPowerupStats();
    }
}

private void consumePowerup(Powerup.SpicyType type){
    if(!inventory.useSpicy(type))
        return;

    if(type == Powerup.SpicyType.OVERDRIVE){
        if(OVERDRIVE_ACTIVE ||
           DAMAGE_SURGE_ACTIVE ||
           BERSERK_ACTIVE ||
           INFINITE_ASSAULT_ACTIVE ||
           GOD_MODE_ACTIVE)
            return;

        if(FIRE_RATE_ACTIVE){
            FIRE_RATE_ACTIVE = false;
            FIRE_RATE_TIMER = 0f;
        }

        OVERDRIVE_ACTIVE = true;
        OVERDRIVE_TIMER = 0f;
        refreshWeaponPowerupStats();
        AudioAssets.playPowerActivate("overdrive");
        return;
    }

    if(type == Powerup.SpicyType.RAPID_MOBILITY){
        if(RAPID_MOBILITY_ACTIVE || HYPERDRIVE_ACTIVE)
            return;

        RAPID_MOBILITY_ACTIVE = true;
        RAPID_MOBILITY_TIMER = 0f;
        player.setMovementSpeed(RAPID_MOBILITY_SPEED_MULTIPLIER);
        jetpackThrust = BASE_JETPACK_THRUST * RAPID_MOBILITY_JETPACK_MULTIPLIER;
        AudioAssets.playPowerActivate("rapid_mobility");
        return;
    }

    if(type == Powerup.SpicyType.BOTTOMLESS_MAG){
        if(BOTTOMLESS_MAG_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE)
            return;
        BOTTOMLESS_MAG_ACTIVE = true;
        BOTTOMLESS_MAG_TIMER = 0f;
        AudioAssets.playPowerActivate("bottomless_mag");
        return;
    }

    if(type == Powerup.SpicyType.DAMAGE_SURGE){
        if(DAMAGE_SURGE_ACTIVE || OVERDRIVE_ACTIVE || BERSERK_ACTIVE || GOD_MODE_ACTIVE)
            return;
        DAMAGE_SURGE_ACTIVE = true;
        DAMAGE_SURGE_TIMER = 0f;
        AudioAssets.playPowerActivate("damage_surge");
        refreshWeaponPowerupStats();
        return;
    }

    if(type == Powerup.SpicyType.GOLD_RUSH){
        if(GOLD_RUSH_ACTIVE || DOUBLE_FORTUNE_ACTIVE)
            return;
        GOLD_RUSH_ACTIVE = true;
        GOLD_RUSH_TIMER = 0f;
        AudioAssets.playPowerActivate("gold_rush");
        return;
    }

    if(type == Powerup.SpicyType.SCORE_FRENZY){
        if(SCORE_FRENZY_ACTIVE || DOUBLE_FORTUNE_ACTIVE)
            return;
        SCORE_FRENZY_ACTIVE = true;
        AudioAssets.playPowerActivate("score_frenzy");
        SCORE_FRENZY_TIMER = 0f;
    }
}


private void consumePowerup(Powerup.MoreSpicyType type){
    if(!inventory.useMoreSpicy(type))
        return;

    if(type == Powerup.MoreSpicyType.BERSERK){
        if(BERSERK_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE)
            return;

        FIRE_RATE_ACTIVE = false;
        FIRE_RATE_TIMER = 0f;

        OVERDRIVE_ACTIVE = false;
        OVERDRIVE_TIMER = 0f;

        DAMAGE_SURGE_ACTIVE = false;
        DAMAGE_SURGE_TIMER = 0f;

        BERSERK_ACTIVE = true;
        BERSERK_TIMER = 0f;

        refreshWeaponPowerupStats();
        AudioAssets.playPowerActivate("berserk");
        return;
    }

    if(type == Powerup.MoreSpicyType.INFINITE_ASSAULT){
        if(INFINITE_ASSAULT_ACTIVE || BERSERK_ACTIVE || GOD_MODE_ACTIVE)
            return;

        FIRE_RATE_ACTIVE = false;
        FIRE_RATE_TIMER = 0f;

        OVERDRIVE_ACTIVE = false;
        OVERDRIVE_TIMER = 0f;

        BOTTOMLESS_MAG_ACTIVE = false;
        BOTTOMLESS_MAG_TIMER = 0f;

        INFINITE_ASSAULT_ACTIVE = true;
        INFINITE_ASSAULT_TIMER = 0f;

        AudioAssets.playPowerActivate("infinite_assault");
        refreshWeaponPowerupStats();
        return;
    }

    if(type == Powerup.MoreSpicyType.HYPERDRIVE){
        if(HYPERDRIVE_ACTIVE)
            return;

        if(RAPID_MOBILITY_ACTIVE){
            RAPID_MOBILITY_ACTIVE = false;
            RAPID_MOBILITY_TIMER = 0f;

            player.resetMovementSpeed();
            jetpackThrust = BASE_JETPACK_THRUST;
        }

        HYPERDRIVE_ACTIVE = true;
        HYPERDRIVE_TIMER = 0f;
        player.setMovementSpeed(HYPERDRIVE_SPEED_MULTIPLIER);
        player.setJetpackFuel(player.getMaxJetpackFuel());

        AudioAssets.playPowerActivate("hyperdrive");
        return;
    }

    if(type == Powerup.MoreSpicyType.FORTRESS){
        if(FORTRESS_ACTIVE)
            return;

        FORTRESS_ACTIVE = true;
        FORTRESS_TIMER = 0f;

        SHIELD_ACTIVE = true;
        SHIELD_AMOUNT = Math.max(SHIELD_AMOUNT,FORTRESS_SHIELD);
        AudioAssets.playPowerActivate("fortress");
        return;
    }

    if(type == Powerup.MoreSpicyType.DOUBLE_FORTUNE){
        if(DOUBLE_FORTUNE_ACTIVE)
            return;

        GOLD_RUSH_ACTIVE = false;
        GOLD_RUSH_TIMER = 0f;

        SCORE_FRENZY_ACTIVE = false;
        SCORE_FRENZY_TIMER = 0f;

        DOUBLE_FORTUNE_ACTIVE = true;
        DOUBLE_FORTUNE_TIMER = 0f;
        AudioAssets.playPowerActivate("double_fortune");
    }
}

private void consumeGodMode(){
    if(!inventory.useGodMode())
        return;

    if(GOD_MODE_ACTIVE)
        return;

    FIRE_RATE_ACTIVE = false;
    FIRE_RATE_TIMER = 0f;

    OVERDRIVE_ACTIVE = false;
    OVERDRIVE_TIMER = 0f;

    DAMAGE_SURGE_ACTIVE = false;
    DAMAGE_SURGE_TIMER = 0f;

    BERSERK_ACTIVE = false;
    BERSERK_TIMER = 0f;

    INFINITE_ASSAULT_ACTIVE = false;
    INFINITE_ASSAULT_TIMER = 0f;

    BOTTOMLESS_MAG_ACTIVE = false;
    BOTTOMLESS_MAG_TIMER = 0f;

    GOD_MODE_ACTIVE = true;
    GOD_MODE_TIMER = 0f;

    player.setJetpackFuel(player.getMaxJetpackFuel());
    AudioAssets.playPowerActivate("god_mode");
    refreshWeaponPowerupStats();
}

private void refreshWeaponPowerupStats(){
    weapon.resetFireRate();
    weapon.resetDamage();

    if(GOD_MODE_ACTIVE){
        weapon.setFireRate(GOD_MODE_FIRE_RATE_MULTIPLIER);
        weapon.setDamage(GOD_MODE_DAMAGE_MULTIPLIER);
        return;
    }

    if(BERSERK_ACTIVE){
        weapon.setFireRate(BERSERK_FIRE_RATE_MULTIPLIER);
        weapon.setDamage(BERSERK_DAMAGE_MULTIPLIER);
        return;
    }

    if(OVERDRIVE_ACTIVE){
        weapon.setFireRate(OVERDRIVE_FIRE_RATE_MULTIPLIER);
        weapon.setDamage(OVERDRIVE_DAMAGE_MULTIPLIER);
        return;
    }

    if(INFINITE_ASSAULT_ACTIVE)
        weapon.setFireRate(INFINITE_ASSAULT_FIRE_RATE_MULTIPLIER);

    else if(FIRE_RATE_ACTIVE)
        weapon.setFireRate(FIRE_RATE_MULTIPLIER);

    if(DAMAGE_SURGE_ACTIVE)
        weapon.setDamage(DAMAGE_SURGE_MULTIPLIER);
}


private void updatePowerups(float dt){
    POWER_AURA_VISUAL_TIMER += dt;
    if(POWER_AURA_BURST != null){
        POWER_AURA_BURST_TIMER += dt;
        if(POWER_AURA_BURST_TIMER >= 0.6f){
            POWER_AURA_BURST = null;
            POWER_AURA_BURST_TIMER = 0f;
        }
    }
    boolean refreshWeapon = false;
    if(FIRE_RATE_ACTIVE){
        FIRE_RATE_TIMER += dt;

        if(FIRE_RATE_TIMER >= FIRE_RATE_DURATION){
            FIRE_RATE_ACTIVE = false;
            FIRE_RATE_TIMER = 0f;
            refreshWeapon = true;
        }
    }

    if(OVERDRIVE_ACTIVE){
        OVERDRIVE_TIMER += dt;
        if(OVERDRIVE_TIMER >= OVERDRIVE_DURATION){
            OVERDRIVE_ACTIVE = false;
            OVERDRIVE_TIMER = 0f;
            refreshWeapon = true;
        }
    }


    if(RAPID_MOBILITY_ACTIVE){
        RAPID_MOBILITY_TIMER += dt;
        if(RAPID_MOBILITY_TIMER >= RAPID_MOBILITY_DURATION){
            RAPID_MOBILITY_ACTIVE = false;
            RAPID_MOBILITY_TIMER = 0f;
            player.resetMovementSpeed();
            jetpackThrust = BASE_JETPACK_THRUST;
        }
    }


    if(BOTTOMLESS_MAG_ACTIVE){
        BOTTOMLESS_MAG_TIMER += dt;
        if(BOTTOMLESS_MAG_TIMER >= BOTTOMLESS_MAG_DURATION){
            BOTTOMLESS_MAG_ACTIVE = false;
            BOTTOMLESS_MAG_TIMER = 0f;
        }
    }


    if(DAMAGE_SURGE_ACTIVE){
        DAMAGE_SURGE_TIMER += dt;
        if(DAMAGE_SURGE_TIMER >= DAMAGE_SURGE_DURATION){
            DAMAGE_SURGE_ACTIVE = false;
            DAMAGE_SURGE_TIMER = 0f;
            refreshWeapon = true;
        }
    }


    if(GOLD_RUSH_ACTIVE){
        GOLD_RUSH_TIMER += dt;
        if(GOLD_RUSH_TIMER >= GOLD_RUSH_DURATION){
            GOLD_RUSH_ACTIVE = false;
            GOLD_RUSH_TIMER = 0f;
        }
    }


    if(SCORE_FRENZY_ACTIVE){
        SCORE_FRENZY_TIMER += dt;
        if(SCORE_FRENZY_TIMER >= SCORE_FRENZY_DURATION){
            SCORE_FRENZY_ACTIVE = false;
            SCORE_FRENZY_TIMER = 0f;
        }
    }


    if(BERSERK_ACTIVE){
        BERSERK_TIMER += dt;
        if(BERSERK_TIMER >= BERSERK_DURATION){
            BERSERK_ACTIVE = false;
            BERSERK_TIMER = 0f;
            refreshWeapon = true;
        }
    }


    if(INFINITE_ASSAULT_ACTIVE){
        INFINITE_ASSAULT_TIMER += dt;
        if(INFINITE_ASSAULT_TIMER >= INFINITE_ASSAULT_DURATION){
            INFINITE_ASSAULT_ACTIVE = false;
            INFINITE_ASSAULT_TIMER = 0f;
            refreshWeapon = true;
        }
    }


    if(HYPERDRIVE_ACTIVE){
        HYPERDRIVE_TIMER += dt;
        if(HYPERDRIVE_TIMER >= HYPERDRIVE_DURATION){
            HYPERDRIVE_ACTIVE = false;
            HYPERDRIVE_TIMER = 0f;
            player.resetMovementSpeed();
        }
    }


    if(FORTRESS_ACTIVE){
        FORTRESS_TIMER += dt;
        if(FORTRESS_TIMER >= FORTRESS_DURATION){
            FORTRESS_ACTIVE = false;
            FORTRESS_TIMER = 0f;
        }
    }


    if(DOUBLE_FORTUNE_ACTIVE){
        DOUBLE_FORTUNE_TIMER += dt;
        if(DOUBLE_FORTUNE_TIMER >= DOUBLE_FORTUNE_DURATION){
            DOUBLE_FORTUNE_ACTIVE = false;
            DOUBLE_FORTUNE_TIMER = 0f;
        }
    }


    if(GOD_MODE_ACTIVE){
        GOD_MODE_TIMER += dt;
        if(GOD_MODE_TIMER >= GOD_MODE_DURATION){
            GOD_MODE_ACTIVE = false;
            GOD_MODE_TIMER = 0f;
            refreshWeapon = true;
        }
    }
    if(refreshWeapon)
        refreshWeaponPowerupStats();
}


private int modifyGoldReward(int amount){
    if(DOUBLE_FORTUNE_ACTIVE)
        return amount*2;
    if(GOLD_RUSH_ACTIVE)
        return amount*2;
    return amount;
}

private int modifyScoreReward(int amount){
    if(DOUBLE_FORTUNE_ACTIVE)
        return amount*2;
    if(SCORE_FRENZY_ACTIVE)
        return amount*2;
    return amount;
}

private void damagePlayer(float damage){
    if(GOD_MODE_ACTIVE)
        return;
    float finalDamage = damage;
    if(FORTRESS_ACTIVE)
        finalDamage *= 1f-FORTRESS_DAMAGE_REDUCTION;

    if(BERSERK_ACTIVE)
        finalDamage *= BERSERK_INCOMING_DAMAGE_MULTIPLIER;

    if(SHIELD_ACTIVE && SHIELD_AMOUNT > 0f){
        float absorbed = Math.min(SHIELD_AMOUNT,finalDamage);
        SHIELD_AMOUNT -= absorbed;
        finalDamage -= absorbed;
        if(SHIELD_AMOUNT <= 0f){
            AudioAssets.playShieldBreak();
            SHIELD_AMOUNT = 0f;
            SHIELD_ACTIVE = false;
        }
        if(absorbed > 0f)
            AudioAssets.playShieldHit();
    }
    if(finalDamage > 0f)
        player.takeDamage(finalDamage);
    if(player.getHP() <= 0)
        triggerGameOver();

}

private boolean infiniteMagazineActive(){
    return BOTTOMLESS_MAG_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE;
}

private boolean infiniteJetpackActive(){
    return HYPERDRIVE_ACTIVE || GOD_MODE_ACTIVE;
}

private boolean playerInvulnerable(){
    return GOD_MODE_ACTIVE;
}

private void drawInventoryBag(){
    float size = 64f;
    float x = 20f;
    float y = 20f;
    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

    inventoryBagBounds.set(x,y,size,size);
    Texture texture = FxAssets.inventoryBag;

    if(inventoryBagBounds.contains(mouseX,mouseY)){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            texture = FxAssets.inventoryBagPressed;
        else
            texture = FxAssets.inventoryBagHover;
    }
    batch.draw(texture,x,y,size,size);
}


private void drawInventory(){
    if(inventoryAnimation <= 0f)
        return;

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    float finalScale = Math.min(1f,Math.min((screenWidth-80f)/1280f,(screenHeight-80f)/720f));

    float finalWidth = 1280f*finalScale;
    float finalHeight = 720f*finalScale;

    float t = inventoryAnimation;
    float smoothT = t*t*(3f-2f*t);
    float startScale = 0.15f;
    float animatedScale = startScale+(1f-startScale)*smoothT;

    float width = finalWidth*animatedScale;
    float height = finalHeight*animatedScale;

    float sx = width/1280f;
    float sy = height/720f;

    float bagCenterX = inventoryBagBounds.x+inventoryBagBounds.width/2f;
    float bagCenterY =inventoryBagBounds.y+inventoryBagBounds.height/2f;
    float screenCenterX = screenWidth/2f;
    float screenCenterY = screenHeight/2f;

    float centerX = bagCenterX+ (screenCenterX-bagCenterX)*smoothT;
    float centerY = bagCenterY+ (screenCenterY-bagCenterY)*smoothT;

    float x = centerX-width/2f;
    float y = centerY-height/2f;

    inventoryActionBounds.set(x+844f*sx,y+54f*sy,372f*sx,42f*sy);
    inventoryCloseBounds.set(x+1174f*sx, y+655f*sy,50f*sx,45f*sy);

    Texture panelTexture = FxAssets.inventoryPanel;
    if(inventorySelectionType == InventorySelectionType.WEAPON)
        panelTexture = FxAssets.inventoryPanelGun;
    batch.draw(panelTexture,x,y,width,height);
    drawInventoryWeapons(x,y,width,height);
    drawInventoryConsumables(x,y,width,height);
    drawSelectedInventoryPreview(x,y,width,height);
    drawSelectedPowerupInfo(x,y,width,height);
    drawSelectedWeaponInfo(x,y,width,height);
}

private void updateInventoryAnimation(float dt){
    if(inventoryOpen){
        inventoryAnimation += dt*INVENTORY_ANIMATION_SPEED;
        if(inventoryAnimation > 1f)
            inventoryAnimation = 1f;
    }
    else{
        inventoryAnimation -= dt*INVENTORY_ANIMATION_SPEED;
        if(inventoryAnimation < 0f)
            inventoryAnimation = 0f;
    }
}

private boolean inventoryBlockingGameplay(){
    return inventoryOpen || inventoryAnimation > 0f;
}

private void drawInventoryConsumable(Texture icon, int amount, int row, int column, float panelX, float panelY, float panelWidth,float panelHeight){
    float sx = panelWidth/1280f;
    float sy = panelHeight/720f;

    float iconX = panelX+(94f+184f*column)*sx;
    float iconY = panelY+(418f-116f*row)*sy;

    float iconWidth = 84f*sx;
    float iconHeight = 84f*sy;

    int index = row*4+column;

    inventoryConsumableBounds[index].set(iconX, iconY,iconWidth,iconHeight);

    drawContain( icon, iconX, iconY, iconWidth, iconHeight);

    float quantityX = panelX+(108f+184f*column)*sx;
    float quantityY = panelY+(396f-116f*row)*sy;

    float quantityWidth = 56f*sx;
    float quantityHeight = 18f*sy;

    drawInventoryQuantity(amount,quantityX,quantityY,quantityWidth,quantityHeight);
}

private void drawInventoryQuantity(int amount, float x,float y, float width,float height){
    String text = "x"+amount;

    hudFont.getData().setScale(0.65f);
    GlyphLayout layout = new GlyphLayout(hudFont,text);

    float textX =  x+(width-layout.width)/2f;
    float textY = y+(height+layout.height)/2f;
    hudFont.draw(batch, text, textX, textY);
}


private void drawInventoryConsumables(float panelX,float panelY,float panelWidth,float panelHeight){
    drawInventoryConsumable(FxAssets.powerupIcons.get(Powerup.PowerupType.HEALTH),
        inventory.getPowerupAmount(Powerup.PowerupType.HEALTH),
        0,0,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable( FxAssets.powerupIcons.get(Powerup.PowerupType.SHIELD),
        inventory.getPowerupAmount(Powerup.PowerupType.SHIELD),
        0,1,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.powerupIcons.get(Powerup.PowerupType.JETPACK),
        inventory.getPowerupAmount(Powerup.PowerupType.JETPACK),
        0,2,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.powerupIcons.get(Powerup.PowerupType.FIRE_RATE),
        inventory.getPowerupAmount(Powerup.PowerupType.FIRE_RATE),
        0,3,
        panelX,panelY,panelWidth,panelHeight);



    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.OVERDRIVE),
        inventory.getSpicyAmount(Powerup.SpicyType.OVERDRIVE),
        1,0,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.RAPID_MOBILITY),
        inventory.getSpicyAmount(Powerup.SpicyType.RAPID_MOBILITY),
        1,1,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.BOTTOMLESS_MAG),
        inventory.getSpicyAmount(Powerup.SpicyType.BOTTOMLESS_MAG),
        1,2,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.DAMAGE_SURGE),
        inventory.getSpicyAmount(Powerup.SpicyType.DAMAGE_SURGE),
        1,3,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.GOLD_RUSH),
        inventory.getSpicyAmount(Powerup.SpicyType.GOLD_RUSH),
        2,0,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.spicyIcons.get(Powerup.SpicyType.SCORE_FRENZY),
        inventory.getSpicyAmount(Powerup.SpicyType.SCORE_FRENZY),
        2,1,
        panelX,panelY,panelWidth,panelHeight);



    drawInventoryConsumable(FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.BERSERK),
        inventory.getMoreSpicyAmount(Powerup.MoreSpicyType.BERSERK),
        2,2,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.INFINITE_ASSAULT),
        inventory.getMoreSpicyAmount(Powerup.MoreSpicyType.INFINITE_ASSAULT),
        2,3,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.HYPERDRIVE),
        inventory.getMoreSpicyAmount(Powerup.MoreSpicyType.HYPERDRIVE),
        3,0,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.FORTRESS),
        inventory.getMoreSpicyAmount(Powerup.MoreSpicyType.FORTRESS),
        3,1,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.DOUBLE_FORTUNE),
        inventory.getMoreSpicyAmount(Powerup.MoreSpicyType.DOUBLE_FORTUNE),
        3,2,
        panelX,panelY,panelWidth,panelHeight);

    drawInventoryConsumable(FxAssets.godModeIcon,inventory.getGodModeAmount(),3,3,panelX,panelY,panelWidth,panelHeight);
}
private void drawInventoryWeapons(float panelX,float panelY,float panelWidth, float panelHeight){
    float sx = panelWidth/1280f;
    float sy = panelHeight/720f;

    for(int i=0;i<INVENTORY_WEAPONS.length;i++){
        Weapon.WeaponType type = INVENTORY_WEAPONS[i];
        float slotX = panelX+(44f+200f*i)*sx;
        float slotY = panelY+556f*sy;
        float slotWidth = 188f*sx;
        float slotHeight = 88f*sy;

        inventoryWeaponBounds[i].set(slotX,slotY,slotWidth,slotHeight);
        Texture gun = FxAssets.weaponRewardIcons.get(type);
        drawContain(gun, slotX+10f*sx,slotY+26f*sy,168f*sx,54f*sy);

        if(!inventory.ownsWeapon(type))
            batch.draw(FxAssets.inventoryWeaponLocked,slotX,slotY,slotWidth,slotHeight);
    
        else if(type == weapon.getCurrentWeapon())            
            batch.draw(FxAssets.inventoryWeaponEquipped,slotX,slotY,slotWidth,slotHeight);
    }
}


private void checkInventoryWeaponSelection(float mouseX,float mouseY){
    for(int i=0;i<inventoryWeaponBounds.length;i++){
        if(!inventoryWeaponBounds[i].contains(mouseX,mouseY))
            continue;
        selectedPowerup = null;
        selectedSpicy = null;
        selectedMoreSpicy = null;
        selectedGodMode = false;

        selectedWeapon = INVENTORY_WEAPONS[i];
        inventorySelectionType = InventorySelectionType.WEAPON;
        return;
    }
}

private void checkInventoryConsumableSelection(float mouseX,float mouseY){
    for(int i=0;i<inventoryConsumableBounds.length;i++){
        if(!inventoryConsumableBounds[i].contains(mouseX,mouseY))
            continue;
        selectedPowerup = null;
        selectedSpicy = null;
        selectedMoreSpicy = null;
        selectedGodMode = false;
        selectedWeapon = null;
        if(i < 4)
            selectedPowerup = INVENTORY_NORMAL_POWERUPS[i];
    
        else if(i < 10)
            selectedSpicy = INVENTORY_SPICY_POWERUPS[i-4];
        
        else if(i < 15)
            selectedMoreSpicy = INVENTORY_MORE_SPICY_POWERUPS[i-10];

        else
            selectedGodMode = true;

        inventorySelectionType = InventorySelectionType.POWERUP;
        return;
    }
}

private void drawSelectedInventoryPreview(
    float panelX,
    float panelY,
    float panelWidth,
    float panelHeight
){
    float sx = panelWidth/1280f;
    float sy = panelHeight/720f;


    if(inventorySelectionType == InventorySelectionType.WEAPON){
        if(selectedWeapon == null)
            return;
        Texture gun = FxAssets.weaponRewardIcons.get(selectedWeapon);
        drawContain(gun,panelX+850f*sx,panelY+340f*sy,360f*sx,130f*sy);
        return;
    }


    if(inventorySelectionType == InventorySelectionType.POWERUP){
        Texture icon = getSelectedInventoryIcon();
        if(icon == null)
            return;
        drawContain(icon,panelX+935f*sx,panelY+300f*sy,190f*sx,190f*sy);
    }
}

private Texture getSelectedInventoryIcon(){
    if(selectedPowerup != null)
        return FxAssets.powerupIcons.get(selectedPowerup);

    if(selectedSpicy != null)
        return FxAssets.spicyIcons.get(selectedSpicy);

    if(selectedMoreSpicy != null)
        return FxAssets.moreSpicyIcons.get(selectedMoreSpicy);

    if(selectedGodMode)
        return FxAssets.godModeIcon;
    return null;
}


private void consumeSelectedInventoryPowerup(){
    if(inventorySelectionType != InventorySelectionType.POWERUP)
        return;

    if(selectedPowerup != null){
        consumePowerup(selectedPowerup);
        return;
    }

    if(selectedSpicy != null){
        consumePowerup(selectedSpicy);
        return;
    }

    if(selectedMoreSpicy != null){
        consumePowerup(selectedMoreSpicy);
        return;
    }

    if(selectedGodMode)
        consumeGodMode();
}

private String getSelectedPowerupName(){
    if(selectedPowerup != null){
        if(selectedPowerup == Powerup.PowerupType.HEALTH)
            return "HEALTH";
        if(selectedPowerup == Powerup.PowerupType.SHIELD)
            return "SHIELD";
        if(selectedPowerup == Powerup.PowerupType.JETPACK)
            return "JETPACK BOOST";
        if(selectedPowerup == Powerup.PowerupType.FIRE_RATE)
            return "FIRE RATE";
    }

    if(selectedSpicy != null){
        if(selectedSpicy == Powerup.SpicyType.OVERDRIVE)
            return "OVERDRIVE";
        if(selectedSpicy == Powerup.SpicyType.RAPID_MOBILITY)
            return "RAPID MOBILITY";
        if(selectedSpicy == Powerup.SpicyType.BOTTOMLESS_MAG)
            return "BOTTOMLESS MAG";
        if(selectedSpicy == Powerup.SpicyType.DAMAGE_SURGE)
            return "DAMAGE SURGE";
        if(selectedSpicy == Powerup.SpicyType.GOLD_RUSH)
            return "GOLD RUSH";
        if(selectedSpicy == Powerup.SpicyType.SCORE_FRENZY)
            return "SCORE FRENZY";
    }

    if(selectedMoreSpicy != null){
        if(selectedMoreSpicy == Powerup.MoreSpicyType.BERSERK)
            return "BERSERK";
        if(selectedMoreSpicy == Powerup.MoreSpicyType.INFINITE_ASSAULT)
            return "INFINITE ASSAULT";
        if(selectedMoreSpicy == Powerup.MoreSpicyType.HYPERDRIVE)
            return "HYPERDRIVE";
        if(selectedMoreSpicy == Powerup.MoreSpicyType.FORTRESS)
            return "FORTRESS";
        if(selectedMoreSpicy == Powerup.MoreSpicyType.DOUBLE_FORTUNE)
            return "DOUBLE FORTUNE";
    }

    if(selectedGodMode)
        return "GOD MODE";

    return "";
}

private String getSelectedPowerupDescription(){
    if(selectedPowerup != null){
        if(selectedPowerup == Powerup.PowerupType.HEALTH)
            return "Restores 80% of maximum health.";

        if(selectedPowerup == Powerup.PowerupType.SHIELD)
            return "Grants 60 shield HP. Damage hits the shield before health.";

        if(selectedPowerup == Powerup.PowerupType.JETPACK)
            return "Instantly restores jetpack fuel to maximum.";

        if(selectedPowerup == Powerup.PowerupType.FIRE_RATE)
            return "Doubles weapon fire rate.";
    }

    if(selectedSpicy != null){
        if(selectedSpicy == Powerup.SpicyType.OVERDRIVE)
            return "Increases weapon damage and fire rate by 40%.";

        if(selectedSpicy == Powerup.SpicyType.RAPID_MOBILITY)
            return "Increases movement speed and jetpack thrust by 50%.";

        if(selectedSpicy == Powerup.SpicyType.BOTTOMLESS_MAG)
            return "Weapons fire without consuming magazine ammo.";

        if(selectedSpicy == Powerup.SpicyType.DAMAGE_SURGE)
            return "Doubles weapon damage.";

        if(selectedSpicy == Powerup.SpicyType.GOLD_RUSH)
            return "Doubles all gold earned while active.";

        if(selectedSpicy == Powerup.SpicyType.SCORE_FRENZY)
            return "Doubles all score earned while active.";
    }

    if(selectedMoreSpicy != null){
        if(selectedMoreSpicy == Powerup.MoreSpicyType.BERSERK)
            return "2x damage and 1.6x fire rate, but incoming damage increases by 25%.";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.INFINITE_ASSAULT)
            return "Bottomless magazine with 1.4x fire rate.";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.HYPERDRIVE)
            return "1.75x movement speed with unlimited jetpack fuel.";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.FORTRESS)
            return "Grants 100 shield HP and reduces incoming damage by 50%.";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.DOUBLE_FORTUNE)
            return "Doubles both gold and score earned.";
    }
    if(selectedGodMode)
        return "Invulnerability, unlimited ammo and jetpack fuel, 2x damage and 2x fire rate.";

    return "";
}


private String getSelectedPowerupDuration(){
    if(selectedPowerup != null){
        if(selectedPowerup == Powerup.PowerupType.HEALTH)
            return "INSTANT";

        if(selectedPowerup == Powerup.PowerupType.SHIELD)
            return "UNTIL DEPLETED";

        if(selectedPowerup == Powerup.PowerupType.JETPACK)
            return "INSTANT";

        if(selectedPowerup == Powerup.PowerupType.FIRE_RATE)
            return "5 SEC";
    }


    if(selectedSpicy != null){
        if(selectedSpicy == Powerup.SpicyType.OVERDRIVE)
            return "12 SEC";

        if(selectedSpicy == Powerup.SpicyType.RAPID_MOBILITY)
            return "12 SEC";

        if(selectedSpicy == Powerup.SpicyType.BOTTOMLESS_MAG)
            return "10 SEC";

        if(selectedSpicy == Powerup.SpicyType.DAMAGE_SURGE)
            return "10 SEC";

        if(selectedSpicy == Powerup.SpicyType.GOLD_RUSH)
            return "15 SEC";

        if(selectedSpicy == Powerup.SpicyType.SCORE_FRENZY)
            return "15 SEC";
    }


    if(selectedMoreSpicy != null){
        if(selectedMoreSpicy == Powerup.MoreSpicyType.BERSERK)
            return "12 SEC";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.INFINITE_ASSAULT)
            return "15 SEC";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.HYPERDRIVE)
            return "12 SEC";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.FORTRESS)
            return "15 SEC";

        if(selectedMoreSpicy == Powerup.MoreSpicyType.DOUBLE_FORTUNE)
            return "20 SEC";
    }

    if(selectedGodMode)
        return "8 SEC";

    return "";
}


private int getSelectedPowerupAmount(){
    if(selectedPowerup != null)
        return inventory.getPowerupAmount(selectedPowerup);

    if(selectedSpicy != null)
        return inventory.getSpicyAmount(selectedSpicy);

    if(selectedMoreSpicy != null)
        return inventory.getMoreSpicyAmount(selectedMoreSpicy);

    if(selectedGodMode)
        return inventory.getGodModeAmount();

    return 0;
}

private void drawSelectedPowerupInfo(float panelX, float panelY,float panelWidth, float panelHeight){
    if(inventorySelectionType != InventorySelectionType.POWERUP)
        return;

    if(inventoryAnimation < 0.99f)
        return;

    float sx = panelWidth/1280f;
    float sy = panelHeight/720f;

    inventoryTitleFont.getData().setScale(sx);
    inventoryBodyFont.getData().setScale(sx);
    inventoryInfoFont.getData().setScale(sx);
    inventoryButtonFont.getData().setScale(sx);

    String name = getSelectedPowerupName();
    String description = getSelectedPowerupDescription();
    String duration = getSelectedPowerupDuration();
    int amount = getSelectedPowerupAmount();

    //TITLE BOX
    drawInventoryCenteredText(inventoryTitleFont,name,panelX+835f*sx,panelY+238f*sy, 380f*sx,48f*sy,false);

    //DESCRIPTION BOX
    drawInventoryCenteredText(inventoryBodyFont, description, panelX+850f*sx, panelY+166f*sy, 350f*sx, 72f*sy, true);

    //INFO ROWS
    drawInventoryInfoRow("DURATION",duration,panelX+855f*sx,panelX+1200f*sx, panelY+158f*sy);
    drawInventoryInfoRow( "OWNED","x"+amount,panelX+855f*sx,panelX+1200f*sx, panelY+128f*sy);

    //BUTTON
    String buttonText = amount > 0 ? "CONSUME" : "EMPTY";
    drawInventoryCenteredText(inventoryButtonFont,buttonText,panelX+844f*sx,panelY+54f*sy, 372f*sx,42f*sy, false);
}


private void drawInventoryInfoRow(String label,String value,float leftX,float rightX,float y){
    inventoryInfoFont.draw(batch,label,leftX,y);
    inventoryGlyphLayout.setText(inventoryInfoFont,value);
    inventoryInfoFont.draw(batch,value,rightX-inventoryGlyphLayout.width,y);
}

private void drawInventoryCenteredText(BitmapFont font, String text,float x,float y,float width,float height,boolean wrap){
    inventoryGlyphLayout.setText( font, text, Color.WHITE, width, Align.center, wrap);
    float textY =y+(height+inventoryGlyphLayout.height)/2f;
    font.draw(batch, text, x, textY, width, Align.center, wrap);
}

private String getSelectedWeaponName(){
    if(selectedWeapon == WeaponType.PISTOL)
        return "PISTOL";

    if(selectedWeapon == WeaponType.AR)
        return "ASSAULT RIFLE";

    if(selectedWeapon == WeaponType.SMG)
        return "SMG";

    if(selectedWeapon == WeaponType.SHOTGUN)
        return "SHOTGUN";

    if(selectedWeapon == WeaponType.SNIPER)
        return "SNIPER RIFLE";

    if(selectedWeapon == WeaponType.ROCKET_LAUNCHER)
        return "ROCKET LAUNCHER";

    return "";
}

private String getSelectedWeaponButtonText(){
    if(selectedWeapon == null)
        return "";

    if(!inventory.ownsWeapon(selectedWeapon))
        return "LOCKED";

    if(selectedWeapon == weapon.getCurrentWeapon())
        return "EQUIPPED";

    return "EQUIP";
}

private void drawSelectedWeaponInfo(float panelX,float panelY,float panelWidth,float panelHeight){
    if(inventorySelectionType != InventorySelectionType.WEAPON)
        return;

    if(selectedWeapon == null)
        return;

    if(inventoryAnimation < 0.99f)
        return;

    float sx = panelWidth/1280f;
    float sy = panelHeight/720f;

    inventoryTitleFont.getData().setScale(sx);
    inventoryInfoFont.getData().setScale(sx);
    inventoryButtonFont.getData().setScale(sx);

    String name = getSelectedWeaponName();

    int magazine = weapon.getBaseMagazineSize(selectedWeapon);
    float headDamage = weapon.getBaseHeadDamage(selectedWeapon);
    float bodyDamage = weapon.getBaseBodyDamage(selectedWeapon);
    float fireRate = weapon.getBaseFireRate(selectedWeapon);
    float reload = weapon.getBaseReloadSpeed(selectedWeapon);
    int reserveAmmo = runState.getAmmo(selectedWeapon);

    drawInventoryCenteredText(inventoryTitleFont, name, panelX+835f*sx, panelY+302f*sy, 380f*sx, sy, false);

    drawInventoryInfoRow("HEAD DAMAGE",Integer.toString((int)headDamage), panelX+855f*sx,panelX+1200f*sx,panelY+245f*sy);
    drawInventoryInfoRow("BODY DAMAGE", Integer.toString((int)bodyDamage), panelX+855f*sx, panelX+1200f*sx, panelY+272f*sy);
    drawInventoryInfoRow("FIRE RATE", fireRate < 0f ? "--" : Float.toString(fireRate),panelX+855f*sx, panelX+1200f*sx,panelY+215f*sy);
    drawInventoryInfoRow("MAGAZINE", Integer.toString(magazine), panelX+855f*sx, panelX+1200f*sx,panelY+185f*sy);
    drawInventoryInfoRow("RELOAD", reload+" SEC", panelX+855f*sx, panelX+1200f*sx, panelY+155f*sy);
    drawInventoryInfoRow("RESERVE", Integer.toString(reserveAmmo), panelX+855f*sx, panelX+1200f*sx, panelY+128f*sy);

    String buttonText = getSelectedWeaponButtonText();
    drawInventoryCenteredText(inventoryButtonFont,buttonText,panelX+844f*sx,panelY+54f*sy, 372f*sx,42f*sy, false);
}

private void useInventoryAction(){
    if(inventorySelectionType == InventorySelectionType.POWERUP){
        consumeSelectedInventoryPowerup();
        return;
    }
    if(inventorySelectionType == InventorySelectionType.WEAPON){
        equipSelectedWeapon();
    }
}

private void equipSelectedWeapon(){
    if(selectedWeapon == null)
        return;

    if(!inventory.ownsWeapon(selectedWeapon))
        return;

    if(selectedWeapon == weapon.getCurrentWeapon())
        return;
    if(reloading)
        AudioAssets.playReloadCancel();

    weapon.equipWeapon(selectedWeapon);
    AudioAssets.playWeaponEquip(selectedWeapon);
    updateScopeTarget();
    refreshWeaponPowerupStats();
    reloading = false;
    reloadTimer = 0f;
}

private Vector2 rotateBulletDirection(Vector2 direction,float angle){
    float cos = MathUtils.cosDeg(angle);
    float sin = MathUtils.sinDeg(angle);

    return new Vector2(direction.x*cos-direction.y*sin, direction.x*sin+direction.y*cos).nor();
}
private void createShotgunSpread(Vector2 nozzlePosition,Vector2 baseDirection,Texture texture){
    int pelletCount = 7;
    float totalSpread = 16.5f;

    float startAngle =-totalSpread/2f;
    float angleStep = totalSpread/(pelletCount-1);
    float damageBoost = 1.25f;
    float headDamage = weapon.getHeadDamage()*damageBoost/pelletCount;;
    float bodyDamage = weapon.getBodyDamage()*damageBoost/pelletCount;
    float limbDamage = weapon.getLimbDamage()*damageBoost/pelletCount;
    for(int i=0;i<pelletCount;i++){
        float angle = startAngle + angleStep*i;
        Vector2 pelletDirection =rotateBulletDirection(baseDirection, angle);
        Bullet pellet = new Bullet(nozzlePosition,pelletDirection,getProjectileSpeed(WeaponType.SHOTGUN),texture,
            getBulletCollisionRadius(WeaponType.SHOTGUN),WeaponType.SHOTGUN,getBulletDrawSize(WeaponType.SHOTGUN),
            headDamage,bodyDamage,limbDamage);
        bullets.add(pellet);
    }
}

private void startPowerAuraBurst(String id){
    POWER_AURA_BURST = id;
    POWER_AURA_BURST_TIMER = 0f;
}


private String getDominantPowerAura(){
    if(GOD_MODE_ACTIVE)
        return "god_mode";
    if(BERSERK_ACTIVE)
        return "berserk";
    if(INFINITE_ASSAULT_ACTIVE)
        return "infinite_assault";
    if(FORTRESS_ACTIVE)
        return "fortress";
    if(OVERDRIVE_ACTIVE)
        return "overdrive";
    if(DAMAGE_SURGE_ACTIVE)
        return "damage_surge";
    if(FIRE_RATE_ACTIVE)
        return "fire_rate";
    if(BOTTOMLESS_MAG_ACTIVE)
        return "bottomless_mag";
    if(HYPERDRIVE_ACTIVE)
        return "hyperdrive";
    if(RAPID_MOBILITY_ACTIVE)
        return "rapid_mobility";
    if(SHIELD_ACTIVE && SHIELD_AMOUNT > 0f)
        return "shield";
    if(DOUBLE_FORTUNE_ACTIVE)
        return "double_fortune";
    if(GOLD_RUSH_ACTIVE)
        return "gold_rush";
    if(SCORE_FRENZY_ACTIVE)
        return "score_frenzy";
    return null;
}

private String getCurrentPowerAura(){
    if(POWER_AURA_BURST != null)
        return POWER_AURA_BURST;

    return getDominantPowerAura();
}

private int getPowerAuraFrame(){
    if(POWER_AURA_BURST != null){
        int frame = (int)(POWER_AURA_BURST_TIMER/0.05f);
        return MathUtils.clamp(frame,0,11);
    }
    return (int)(POWER_AURA_VISUAL_TIMER*30f)%24;
}

private void drawPowerAura(boolean front){
    String id = getCurrentPowerAura();
    if(id == null)
        return;
    FxAssets.PowerAuraAnimation aura = FxAssets.getPowerAura(id);
    int frameIndex = getPowerAuraFrame();
    TextureRegion frame;
    if(front)
        frame = aura.frontFrames[frameIndex];
    else
        frame = aura.backFrames[frameIndex];

    Vector2 playerPos = player.getPlayerPosition();
    float playerWidth = player.getPlayerWidth();
    float playerHeight = player.getPlayerHeight();
    float playerCenterX = playerPos.x+playerWidth/2f;
    float playerFeetY = playerPos.y;
    float auraWidth = playerWidth*(512f/160f);
    float auraHeight = playerHeight*(640f/240f);
    float auraX = playerCenterX-auraWidth/2f;
    float auraY = playerFeetY - auraHeight*(92f/640f);

    batch.draw(frame, auraX, auraY, auraWidth, auraHeight);
}

private String getActivePowerName(String id){
    if("shield".equals(id))
        return "SHIELD";

    if("fire_rate".equals(id))
        return "FIRE RATE BOOST";

    if("overdrive".equals(id))
        return "OVERDRIVE";

    if("rapid_mobility".equals(id))
        return "RAPID MOBILITY";

    if("bottomless_mag".equals(id))
        return "BOTTOMLESS MAG";

    if("damage_surge".equals(id))
        return "DAMAGE SURGE";

    if("gold_rush".equals(id))
        return "GOLD RUSH";

    if("score_frenzy".equals(id))
        return "SCORE FRENZY";

    if("berserk".equals(id))
        return "BERSERK";

    if("infinite_assault".equals(id))
        return "INFINITE ASSAULT";

    if("hyperdrive".equals(id))
        return "HYPERDRIVE";

    if("fortress".equals(id))
        return "FORTRESS";

    if("double_fortune".equals(id))
        return "DOUBLE FORTUNE";

    if("god_mode".equals(id))
        return "GOD MODE";

    return "";
}

private float getActivePowerRemaining(String id){
    if("fire_rate".equals(id))
        return FIRE_RATE_DURATION-FIRE_RATE_TIMER;

    if("overdrive".equals(id))
        return OVERDRIVE_DURATION-OVERDRIVE_TIMER;

    if("rapid_mobility".equals(id))
        return RAPID_MOBILITY_DURATION-RAPID_MOBILITY_TIMER;

    if("bottomless_mag".equals(id))
        return BOTTOMLESS_MAG_DURATION-BOTTOMLESS_MAG_TIMER;

    if("damage_surge".equals(id))
        return DAMAGE_SURGE_DURATION-DAMAGE_SURGE_TIMER;

    if("gold_rush".equals(id))
        return GOLD_RUSH_DURATION-GOLD_RUSH_TIMER;

    if("score_frenzy".equals(id))
        return SCORE_FRENZY_DURATION-SCORE_FRENZY_TIMER;

    if("berserk".equals(id))
        return BERSERK_DURATION-BERSERK_TIMER;

    if("infinite_assault".equals(id))
        return INFINITE_ASSAULT_DURATION-INFINITE_ASSAULT_TIMER;

    if("hyperdrive".equals(id))
        return HYPERDRIVE_DURATION-HYPERDRIVE_TIMER;

    if("fortress".equals(id))
        return FORTRESS_DURATION-FORTRESS_TIMER;

    if("double_fortune".equals(id))
        return DOUBLE_FORTUNE_DURATION-DOUBLE_FORTUNE_TIMER;

    if("god_mode".equals(id))
        return GOD_MODE_DURATION-GOD_MODE_TIMER;

    return 0f;
}

private Texture getPowerTierTexture(String id){
    if("god_mode".equals(id))
        return FxAssets.powerSpecial;

    if("berserk".equals(id) || "infinite_assault".equals(id) ||
       "hyperdrive".equals(id) || "fortress".equals(id) ||"double_fortune".equals(id))
        return FxAssets.powerMoreSpicy;

    if("overdrive".equals(id) ||  "rapid_mobility".equals(id) ||  "bottomless_mag".equals(id) ||
       "damage_surge".equals(id) || "gold_rush".equals(id) || "score_frenzy".equals(id))
        return FxAssets.powerSpicy;

    return FxAssets.powerNormal;
}

private void drawPoweredStateHud(){
    String id = getDominantPowerAura();
    if(id == null)
        return;

    float scale = 1f;
    float width = 384f*scale;
    float height = 88f*scale;
    float x = (Gdx.graphics.getWidth()-width)/2f;
    float y = Gdx.graphics.getHeight()-height-14f;
    batch.draw(FxAssets.powerFrame,x,y, width,height);
    batch.draw(getPowerTierTexture(id),x,y,width,height);
    Texture icon = getPowerHudIcon(id);
    if(icon != null)
        drawContain(icon,x+12f*scale,y+14f*scale,64f*scale,64f*scale);
    inventoryInfoFont.getData().setScale(0.8f);
    inventoryInfoFont.draw(batch,"POWERED STATE",x+88f,y+72f);
    inventoryTitleFont.getData().setScale(0.72f);
    inventoryTitleFont.draw(batch,getActivePowerName(id), x+88f, y+49f);
    inventoryInfoFont.getData().setScale(0.85f);
    if("shield".equals(id)){
        inventoryInfoFont.draw(batch, Integer.toString((int)SHIELD_AMOUNT),x+305f, y+38f);
    }
    else{
        float remaining = Math.max( 0f, getActivePowerRemaining(id));
        String timer = String.format("%.1fs",remaining);
        inventoryInfoFont.draw(batch,timer, x+296f,y+38f);
    }
}

private Texture getPowerHudIcon(String id){
    if("shield".equals(id))
        return FxAssets.powerupIcons.get(Powerup.PowerupType.SHIELD);

    if("fire_rate".equals(id))
        return FxAssets.powerupIcons.get(Powerup.PowerupType.FIRE_RATE);

    if("overdrive".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.OVERDRIVE);

    if("rapid_mobility".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.RAPID_MOBILITY);

    if("bottomless_mag".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.BOTTOMLESS_MAG);

    if("damage_surge".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.DAMAGE_SURGE);

    if("gold_rush".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.GOLD_RUSH);

    if("score_frenzy".equals(id))
        return FxAssets.spicyIcons.get(Powerup.SpicyType.SCORE_FRENZY);

    if("berserk".equals(id))
        return FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.BERSERK);

    if("infinite_assault".equals(id))
        return FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.INFINITE_ASSAULT);

    if("hyperdrive".equals(id))
        return FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.HYPERDRIVE);

    if("fortress".equals(id))
        return FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.FORTRESS);

    if("double_fortune".equals(id))
        return FxAssets.moreSpicyIcons.get(Powerup.MoreSpicyType.DOUBLE_FORTUNE);

    if("god_mode".equals(id))
        return FxAssets.godModeIcon;

    return null;
}


private void createShopFromMap(TiledMap map){
    MapLayer spawnLayer = map.getLayers().get("spawns");
    RectangleMapObject shopObject = (RectangleMapObject) spawnLayer.getObjects().get("shop");
    Rectangle shopRect = shopObject.getRectangle();
    shop = new Rectangle(shopRect.x, shopRect.y , shopIconWidth, shopIconHeight);
}

private void drawShop(){
    batch.draw(FxAssets.shopIcon,shop.x,shop.y,shop.width,shop.height);
}

private float getReloadProgress(){
    if(!reloading)
        return 0f;
    return MathUtils.clamp(reloadTimer/weapon.getReloadSpeed(),0f,1f);
}

private boolean gameplayBlocked(){
    return currentScreen != GameScreen.GAMEPLAY || inventoryBlockingGameplay() || pauseMenuOpen || gameOver ||fieldShopOpen;
}

private void resumeGame(){
    pauseMenuOpen = false;
    AudioAssets.playUiPanelClose();
    AudioAssets.resumeMusic();
}

private void giveGold(int amount){
    int finalAmount = modifyGoldReward(amount);
    inventory.addGold(finalAmount);
    runState.addGoldEarned(finalAmount);
    AudioAssets.playGoldGain();
}

private void startNewRun(){
    AudioAssets.playRunStart();
    AudioAssets.playTacticalMusic();
    resetRunSystems();
    runState = new RunState();

    player = createPlayerFromMap(map);
    player.receiveJetPackDrawData(extractJetpackFromMap(map));
    player.receivePlayerMoveBox(extractPlayerMovementColliderFromMap(map));
    player.receivePlayerCrouchMoveBox(extractPlayerCrouchMovementColliderFromMap(map));
    player.receivePlayerGunHoldingOffset(extractPlayerGunHoldingOffsetFromMap(map));

    weapon = createWeaponFromMap(map);

    Vector2 playerCenter = player.getPlayerCentrePosition();
    camera.position.set(playerCenter.x,playerCenter.y,0f);
    camera.zoom = 1f;
    camera.update();
    mapRenderer.setView(camera);

    bullets = new Array<>();
    enemyProjectiles = new Array<>();
    lootCrates = new Array<>();
    bombardmentImpactFx = new Array<>();
    rocketExplosionFx = new Array<>();

    riflemanCount = 0;
    heavyCount = 0;
    drillerCount = 0;
    droneCount = 0;

    enemies = new Array<>();

    enemies.add(create_Jackal_RifleMan_FromMap(map));
    enemies.add(create_Jackal_Heavy_FromMap(map));
    enemies.add(create_Jackal_HoverDrone_FromMap(map));
    enemies.add(create_Jackal_DrillCrawler_FromMap(map));

    activeLootCrate = null;
    pendingLoot = null;

    reloading = false;
    reloadTimer = 0f;

    mouseHeld = 0f;
    horizontalHoldTime = 0f;

    scopeStage = 0;
    targetCameraZoom = 1f;

    inventoryOpen = false;
    inventoryAnimation = 0f;
    pauseMenuOpen = false;

    giveStartingAmmo();
}

private void giveStartingAmmo(){
    runState.addAmmo(WeaponType.PISTOL,5*weapon.getBaseMagazineSize(WeaponType.PISTOL));
}

private void loadFonts(){
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 20;
    hudFont = generator.generateFont(parameter);
    generator.dispose();

    FreeTypeFontGenerator lootGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter lootParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    lootParameter.size = 48;
    lootParameter.minFilter = Texture.TextureFilter.Linear;
    lootParameter.magFilter = Texture.TextureFilter.Linear;
    lootFont = lootGenerator.generateFont(lootParameter);
    lootGenerator.dispose();

    FreeTypeFontGenerator lootCountGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter lootCountParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    lootCountParameter.size = 26;
    lootCountParameter.minFilter = Texture.TextureFilter.Linear;
    lootCountParameter.magFilter = Texture.TextureFilter.Linear;
    lootCountFont = lootCountGenerator.generateFont(lootCountParameter);
    lootCountGenerator.dispose();
    lootCountFont.setUseIntegerPositions(false);

    FreeTypeFontGenerator inventoryGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter inventoryTitleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    inventoryTitleParameter.size = 28;
    inventoryTitleParameter.minFilter = Texture.TextureFilter.Linear;
    inventoryTitleParameter.magFilter = Texture.TextureFilter.Linear;
    inventoryTitleFont = inventoryGenerator.generateFont(inventoryTitleParameter);

    FreeTypeFontGenerator.FreeTypeFontParameter inventoryBodyParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    inventoryBodyParameter.size = 17;
    inventoryBodyParameter.minFilter = Texture.TextureFilter.Linear;
    inventoryBodyParameter.magFilter = Texture.TextureFilter.Linear;
    inventoryBodyFont = inventoryGenerator.generateFont(inventoryBodyParameter);

    FreeTypeFontGenerator.FreeTypeFontParameter inventoryInfoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    inventoryInfoParameter.size = 16;
    inventoryInfoParameter.minFilter = Texture.TextureFilter.Linear;
    inventoryInfoParameter.magFilter = Texture.TextureFilter.Linear;
    inventoryInfoFont = inventoryGenerator.generateFont(inventoryInfoParameter);

    FreeTypeFontGenerator.FreeTypeFontParameter inventoryButtonParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    inventoryButtonParameter.size = 20;
    inventoryButtonParameter.minFilter = Texture.TextureFilter.Linear;
    inventoryButtonParameter.magFilter = Texture.TextureFilter.Linear;
    inventoryButtonFont = inventoryGenerator.generateFont(inventoryButtonParameter);


    FreeTypeFontGenerator runSummaryGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter runSummaryParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    runSummaryParameter.size = 96;
    runSummaryParameter.minFilter = Texture.TextureFilter.Linear;
    runSummaryParameter.magFilter = Texture.TextureFilter.Linear;
    runSummaryFont = runSummaryGenerator.generateFont(runSummaryParameter);
    runSummaryGenerator.dispose();

    inventoryGenerator.dispose();
    inventoryGlyphLayout = new GlyphLayout();

    FreeTypeFontGenerator shopGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));

    FreeTypeFontGenerator.FreeTypeFontParameter shopTitleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    shopTitleParameter.size = 40;
    shopTitleParameter.minFilter = Texture.TextureFilter.Linear;
    shopTitleParameter.magFilter = Texture.TextureFilter.Linear;
    fieldShopTitleFont = shopGenerator.generateFont(shopTitleParameter);

    FreeTypeFontGenerator.FreeTypeFontParameter shopBodyParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    shopBodyParameter.size = 24;
    shopBodyParameter.minFilter = Texture.TextureFilter.Linear;
    shopBodyParameter.magFilter = Texture.TextureFilter.Linear;
    fieldShopBodyFont = shopGenerator.generateFont(shopBodyParameter);

    FreeTypeFontGenerator.FreeTypeFontParameter shopInfoParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    shopInfoParameter.size = 20;
    shopInfoParameter.minFilter = Texture.TextureFilter.Linear;
    shopInfoParameter.magFilter = Texture.TextureFilter.Linear;
    fieldShopInfoFont = shopGenerator.generateFont(shopInfoParameter);

    shopGenerator.dispose();
    }


private void initializeGame(){
    map = loadMap();
    createShopFromMap(map);
    AudioAssets.loadOnce();

    mapRenderer = new OrthogonalTiledMapRenderer(map);

    camera = createCameraFromMap(map);
    mapRenderer.setView(camera);

    hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
    hudCamera.position.set(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f,0);
    hudCamera.update();

    terrain = getTerrainObjectArray(map);
    terrain.sort((a,b)->Float.compare(b.getY(),a.getY()));
    shopInteractionZone = new Rectangle(shop.x-30f,shop.y-20f,shop.width+60f,shop.height+40f);

    Enemy.loadEnemyAssetOnce();
    extractAllEnemyHitboxes();

    groundEnemySpawnPoints = extractGroundEnemySpawnPoints();
    drillerSpawnPoints = extractDrillerSpawnPoints();
    specialCrateSpawnPoints = extractSpecialCrateSpawnPoints();

    solidPlatforms = extractSolidPlatformsFromMap(map);

    shapeRenderer = new ShapeRenderer();

    batch = new SpriteBatch();
    batch.setProjectionMatrix(camera.combined);

    loadBulletTextures();

    riflemanProjectileTexture = new Texture(AssetPaths.EnemyProjectile.RIFLEMAN_PROJECTILE);
    heavyProjectileTexture = new Texture(AssetPaths.EnemyProjectile.HEAVY_PROJECTILE);

    FxAssets.loadOnce();
    LootCrate.loadAssetsOnce();

    Pixmap reloadPixelPixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
    reloadPixelPixmap.setColor(Color.WHITE);
    reloadPixelPixmap.fill();
    reloadProgressPixel = new Texture(reloadPixelPixmap);
    reloadProgressPixelRegion = new TextureRegion(reloadProgressPixel);
    reloadPixelPixmap.dispose();

    for(int i = 0 ; i < armoryWeaponBounds.length ; i++)
        armoryWeaponBounds[i] = new Rectangle();

    for(int i = 0 ; i < fieldShopPowerupBounds.length ; i++)
        fieldShopPowerupBounds[i] = new Rectangle();

    for(int i=0;i<fieldShopWeaponProductBounds.length;i++)
        fieldShopWeaponProductBounds[i] = new Rectangle();

    for(int i=0;i<fieldShopAmmoProductBounds.length;i++)
        fieldShopAmmoProductBounds[i] = new Rectangle();

    for(int i=0;i<fieldShopSpecialProductBounds.length;i++)
        fieldShopSpecialProductBounds[i] = new Rectangle();
    
    for(int i=0;i<armoryWeaponBounds.length;i++)
        armoryWeaponBounds[i] = new Rectangle();

    for(int i=0;i<armoryIconBounds.length;i++)
        armoryIconBounds[i] = new Rectangle();
    }

private void loadPlayerProfile(){
    saveManager = new SaveManager();
    playerProfile = saveManager.loadProfile();
    inventory = playerProfile.getInventory();
}

private void giveTestWeapons(){
    inventory.addWeapon(WeaponType.PISTOL);
    inventory.addWeapon(WeaponType.AR);
    inventory.addWeapon(WeaponType.SMG);
    inventory.addWeapon(WeaponType.SHOTGUN);
    inventory.addWeapon(WeaponType.SNIPER);
    inventory.addWeapon(WeaponType.ROCKET_LAUNCHER);
}

private void endCurrentRun(){
    saveManager.saveProfile(playerProfile);
    if(player != null)
        player.disposePlayer();

    if(weapon != null)
        weapon.disposeWeapon();
}

private void resetRunSystems(){
    gameOver = false;

    RIFLEMAN_RESPAWN_TIMER = 0f;
    HEAVY_RESPAWN_TIMER = 0f;
    DRILLER_RESPAWN_TIMER = 0f;
    DRONE_RESPAWN_TIMER = 0f;

    RIFLEMAN_RESPAWN_TIME = 12f;
    HEAVY_RESPAWN_TIME = 28f;
    DRILLER_RESPAWN_TIME = 22f;
    DRONE_RESPAWN_TIME = 17f;

    MAX_ACTIVE_ENEMIES = 6;

    difficulty = 0f;
    easyMode = true;
    mediumMode = false;
    hardMode = false;
    impossibleMode = false;

    SPECIAL_CRATE_TIMER = 0f;

    FIRE_RATE_ACTIVE = false;
    FIRE_RATE_TIMER = 0f;

    SHIELD_ACTIVE = false;
    SHIELD_AMOUNT = 0f;

    OVERDRIVE_ACTIVE = false;
    OVERDRIVE_TIMER = 0f;

    RAPID_MOBILITY_ACTIVE = false;
    RAPID_MOBILITY_TIMER = 0f;

    BOTTOMLESS_MAG_ACTIVE = false;
    BOTTOMLESS_MAG_TIMER = 0f;

    DAMAGE_SURGE_ACTIVE = false;
    DAMAGE_SURGE_TIMER = 0f;

    GOLD_RUSH_ACTIVE = false;
    GOLD_RUSH_TIMER = 0f;

    SCORE_FRENZY_ACTIVE = false;
    SCORE_FRENZY_TIMER = 0f;

    BERSERK_ACTIVE = false;
    BERSERK_TIMER = 0f;

    INFINITE_ASSAULT_ACTIVE = false;
    INFINITE_ASSAULT_TIMER = 0f;

    HYPERDRIVE_ACTIVE = false;
    HYPERDRIVE_TIMER = 0f;

    FORTRESS_ACTIVE = false;
    FORTRESS_TIMER = 0f;

    DOUBLE_FORTUNE_ACTIVE = false;
    DOUBLE_FORTUNE_TIMER = 0f;

    GOD_MODE_ACTIVE = false;
    GOD_MODE_TIMER = 0f;

    POWER_AURA_VISUAL_TIMER = 0f;
    POWER_AURA_BURST = null;
    POWER_AURA_BURST_TIMER = 0f;

    poisonStatusFxTimer = 0f;
    poisonStatusWasActive = false;
    poisonStatusEnding = false;

    stunStatusFxTimer = 0f;
    stunStatusWasActive = false;
    stunStatusEnding = false;

    jetpackThrust = BASE_JETPACK_THRUST;

    reloading = false;
    reloadTimer = 0f;

    mouseHeld = 0f;
    horizontalHoldTime = 0f;

    scopeStage = 0;
    targetCameraZoom = 1f;
    camera.zoom = 1f;

    activeLootCrate = null;
    pendingLoot = null;

    inventoryOpen = false;
    inventoryAnimation = 0f;

    inventorySelectionType = InventorySelectionType.NONE;
    selectedWeapon = null;
    selectedPowerup = null;
    selectedSpicy = null;
    selectedMoreSpicy = null;
    selectedGodMode = false;

    pauseMenuOpen = false;
    reloadReadyFxActive = false;
    reloadReadyFxTimer = 0f;
    fieldShopOpen = false;
}

private void restartGame(){
    endCurrentRun();
    startNewRun();
}

private void updateReloadFx(float dt){
    if(!reloadReadyFxActive)
        return;
    reloadReadyFxTimer+=dt;
    if(reloadReadyFxTimer >= RELOAD_READY_DURATION){
        reloadReadyFxTimer = 0f;
        reloadReadyFxActive = false;
    }
}

private void drawReloadLock(Vector2 grip){
    if(!reloading && !reloadReadyFxActive)
        return;

    TextureRegion receiverFrame;
    TextureRegion muzzleFrame;

    if(reloading && reloadTimer < RELOAD_ENTER_DURATION){
        int frame = (int)(reloadTimer/RELOAD_ENTER_DURATION*6f);
        frame = MathUtils.clamp(frame,0,5);

        receiverFrame = FxAssets.reloadReceiverEnter[frame];
        muzzleFrame = FxAssets.reloadMuzzleEnter[frame];
    }

    else if(reloading){
        float reloadDuration = weapon.getReloadSpeed();
        float serviceDuration = reloadDuration-RELOAD_ENTER_DURATION;

        float progress;

        if(serviceDuration <= 0f)
            progress = 1f;
        else
            progress = MathUtils.clamp((reloadTimer-RELOAD_ENTER_DURATION)/serviceDuration,0f,1f);

        int frame = (int)(progress*36f);

        receiverFrame = FxAssets.reloadReceiverService[frame];
        muzzleFrame = FxAssets.reloadMuzzleService[frame];
    }

    else{
        int frame = (int)(reloadReadyFxTimer/RELOAD_READY_DURATION*8f);
        frame = MathUtils.clamp(frame,0,7);

        receiverFrame = FxAssets.reloadReceiverReady[frame];
        muzzleFrame = FxAssets.reloadMuzzleReady[frame];
    }

    Vector2 muzzle = weapon.getMuzzleWorldPosition(grip);

    float angle = weapon.getAngleOfRotation();
    float facingY = player.isFacingLeft() ? -1f : 1f;

    float cos = MathUtils.cosDeg(angle);
    float sin = MathUtils.sinDeg(angle);

    float receiverCenterX = grip.x+(muzzle.x-grip.x)*0.22f;
    float receiverCenterY = grip.y+(muzzle.y-grip.y)*0.22f;

    float localY = -5f*facingY;

    receiverCenterX += -localY*sin;
    receiverCenterY += localY*cos;

    float receiverWidth = MathUtils.clamp(grip.dst(muzzle)*1.20f,38f,64f);
    float receiverHeight = receiverWidth*(150f/192f);

    float receiverX = receiverCenterX-receiverWidth/2f;
    float receiverY = receiverCenterY-receiverHeight/2f;

    float scaleY = player.isFacingLeft() ? -1f : 1f;

    batch.draw(receiverFrame,receiverX,receiverY,receiverWidth/2f,receiverHeight/2f,receiverWidth,receiverHeight,1f,scaleY,angle);

    float progress = getReloadProgress();
    int activeSegments = (int)Math.ceil(progress*RELOAD_PROGRESS_SEGMENTS);

    float railLength = MathUtils.clamp(grip.dst(muzzle)*0.75f,28f,60f);
    float segmentGap = 2f;
    float segmentWidth = (railLength-segmentGap*(RELOAD_PROGRESS_SEGMENTS-1))/RELOAD_PROGRESS_SEGMENTS;
    float segmentHeight = 5.5f;

    float railCenterX = grip.x+(muzzle.x-grip.x)*0.45f;
    float railCenterY = grip.y+(muzzle.y-grip.y)*0.45f;

    float railOffsetY = -12f*facingY;

    railCenterX += -railOffsetY*sin;
    railCenterY += railOffsetY*cos;
    float railStart = -railLength/2f+segmentWidth/2f;

    for(int i=0;i<RELOAD_PROGRESS_SEGMENTS;i++){
        float localX = railStart+i*(segmentWidth+segmentGap);
        float segmentCenterX = railCenterX+localX*cos;
        float segmentCenterY = railCenterY+localX*sin;
        float segmentX = segmentCenterX-segmentWidth/2f;
        float segmentY = segmentCenterY-segmentHeight/2f;

        if(i < activeSegments){
            if(progress >= 0.75f)
                batch.setColor(0.25f,1f,1f,1f);
            else
                batch.setColor(1f,0.55f,0.10f,1f);
        }
        else
            batch.setColor(0.15f,0.18f,0.20f,0.75f);

        batch.draw(reloadProgressPixelRegion,segmentX,segmentY,segmentWidth/2f,segmentHeight/2f,segmentWidth,segmentHeight,1f,1f,angle);
    }

    batch.setColor(Color.WHITE);

    float muzzleCenterX = muzzle.x+cos*2f;
    float muzzleCenterY = muzzle.y+sin*2f;

    float muzzleWidth = 16f;
    float muzzleHeight = 24f;

    float muzzleX = muzzleCenterX-muzzleWidth/2f;
    float muzzleY = muzzleCenterY-muzzleHeight/2f;

    batch.draw(muzzleFrame,muzzleX,muzzleY,muzzleWidth/2f,muzzleHeight/2f,muzzleWidth,muzzleHeight,1f,scaleY,angle);
}

private void triggerGameOver(){
    if(gameOver)
        return;
    gameOver = true;
    playerProfile.updateBestScore(runState.getCurrentScore());
    saveManager.saveProfile(playerProfile);
    AudioAssets.playRunTerminated();
    AudioAssets.pauseMusic();
}


private void drawPauseMenu(){
    if(currentScreen != GameScreen.GAMEPLAY)
        return;

    if(!pauseMenuOpen)
        return;

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    float scale = Math.min(screenWidth/1920f,screenHeight/1080f);

    float width = 1920f*scale;
    float height = 1080f*scale;

    float x = (screenWidth-width)/2f;
    float y = (screenHeight-height)/2f;

    batch.setColor(0.05f,0.08f,0.12f,0.55f);
    batch.draw(reloadProgressPixel,x,y,width,height);
    batch.setColor(Color.WHITE);

    batch.draw(FxAssets.pauseShell,x,y,width,height);

    pauseResumeBounds.set(x+620f*scale,y+594f*scale,680f*scale,92f*scale);
    pauseRestartBounds.set(x+620f*scale,y+498f*scale,680f*scale,76f*scale);
    pauseSettingsBounds.set(x+620f*scale,y+402f*scale,328f*scale,76f*scale);
    pauseMainMenuBounds.set(x+972f*scale,y+402f*scale,328f*scale,76f*scale);

    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

    Texture resume = FxAssets.pauseResumeNormal;

    if(pauseResumeBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        resume = FxAssets.pauseResumePressed;
    else if(pauseResumeBounds.contains(mouseX,mouseY))
        resume = FxAssets.pauseResumeHover;

    Texture restart = FxAssets.pauseRestartNormal;

    if(pauseRestartBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        restart = FxAssets.pauseRestartPressed;
    else if(pauseRestartBounds.contains(mouseX,mouseY))
        restart = FxAssets.pauseRestartHover;

    batch.draw(resume,pauseResumeBounds.x,pauseResumeBounds.y,pauseResumeBounds.width,pauseResumeBounds.height);
    batch.draw(restart,pauseRestartBounds.x,pauseRestartBounds.y,pauseRestartBounds.width,pauseRestartBounds.height);

    Texture settings = FxAssets.pauseSettingsNormal;
    if(pauseSettingsBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        settings = FxAssets.pauseSettingsPressed;
    else if(pauseSettingsBounds.contains(mouseX,mouseY))
        settings = FxAssets.pauseSettingsHover;

    Texture mainMenu = FxAssets.pauseMainMenuNormal;
    if(pauseMainMenuBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        mainMenu = FxAssets.pauseMainMenuPressed;
    else if(pauseMainMenuBounds.contains(mouseX,mouseY))
        mainMenu = FxAssets.pauseMainMenuHover;

    batch.draw(settings,pauseSettingsBounds.x,pauseSettingsBounds.y,pauseSettingsBounds.width,pauseSettingsBounds.height);
    batch.draw(mainMenu,pauseMainMenuBounds.x,pauseMainMenuBounds.y,pauseMainMenuBounds.width,pauseMainMenuBounds.height);
}

private void handlePauseClick(float mouseX,float mouseY){
    if(pauseResumeBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        resumeGame();
        return;
    }

    if(pauseRestartBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        restartGame();
        return;
    }

    if(pauseSettingsBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        settingsOpenedFromPause = true;
        currentScreen = GameScreen.SETTINGS;
        return;
    }

    if(pauseMainMenuBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        endCurrentRun();
        pauseMenuOpen = false;
        gameOver = false;
        currentScreen = GameScreen.MAIN_MENU;
        AudioAssets.playMenuMusic();
        return;
    }
}

private boolean fieldShopAmmoAvailable(int index){
    WeaponType type = FIELD_SHOP_WEAPONS[index];
    return inventory.ownsWeapon(type);
}

private void fieldShopMouseClick(float mouseX, float mouseY){
    if(fieldShopCloseBounds.contains(mouseX,mouseY)){
        fieldShopOpen = false;
        return;
    }

    if(fieldShopWeaponsBounds.contains(mouseX,mouseY)){
        selectedFieldShopCategory = FieldShopCategory.WEAPONS;
        selectedFieldShopProduct = -1;
        return;
    }

    if(fieldShopAmmoBounds.contains(mouseX,mouseY)){
        selectedFieldShopCategory = FieldShopCategory.AMMO;
        selectedFieldShopProduct = -1;
        return;
    }

    if(fieldShopPowerupsBounds.contains(mouseX,mouseY)){
        selectedFieldShopCategory = FieldShopCategory.POWERUPS;
        selectedFieldShopProduct = -1;
        return;
    }

    if(fieldShopSpecialBounds.contains(mouseX,mouseY)){
        selectedFieldShopCategory = FieldShopCategory.SPECIAL;
        selectedFieldShopProduct = -1;
        return;
    }

    if(selectedFieldShopCategory == FieldShopCategory.AMMO && fieldShopActionBounds.contains(mouseX,mouseY)){
        buySelectedFieldShopAmmo();
        return;
    }

    Rectangle[] currentBounds = getCurrentFieldShopProductBounds();
    for(int i=0;i<currentBounds.length;i++){
        if(!currentBounds[i].contains(mouseX,mouseY))
            continue;
        if(selectedFieldShopCategory == FieldShopCategory.AMMO && !fieldShopAmmoAvailable(i))
            return;
        if(currentBounds[i].contains(mouseX,mouseY)){
            selectedFieldShopProduct = i;
            return;
        }
    }

    if(selectedFieldShopCategory == FieldShopCategory.WEAPONS && fieldShopActionBounds.contains(mouseX,mouseY)){
        buySelectedFieldShopWeapon();
        return;
    }
    if(selectedFieldShopCategory == FieldShopCategory.POWERUPS && fieldShopActionBounds.contains(mouseX,mouseY)){
        buySelectedFieldShopPowerup();
        return;
    }
}

private Rectangle[] getCurrentFieldShopProductBounds(){
    if(selectedFieldShopCategory == FieldShopCategory.WEAPONS)
        return fieldShopWeaponProductBounds;
    if(selectedFieldShopCategory == FieldShopCategory.AMMO)
        return fieldShopAmmoProductBounds;
    if(selectedFieldShopCategory == FieldShopCategory.POWERUPS)
        return fieldShopPowerupBounds;
    return fieldShopSpecialProductBounds;
}


private void drawRunSummaryValue(String text,float x,float baseline,float logicalSize,float scale){
    runSummaryFont.getData().setScale((logicalSize/96f)*scale);
    runSummaryFont.draw(batch,text,x,baseline);
}

private void drawRunSummaryRightValue(String text,float rightX,float baseline,float logicalSize,float scale){
    runSummaryFont.getData().setScale((logicalSize/96f)*scale);
    runSummaryLayout.setText(runSummaryFont,text);
    runSummaryFont.draw(batch,text,rightX-runSummaryLayout.width,baseline);
}

private void drawRunSummary(){
    if(currentScreen != GameScreen.GAMEPLAY)
        return;
    if(!gameOver)
        return;
    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    float scale = Math.min(screenWidth/1920f,screenHeight/1080f);

    float width = 1920f*scale;
    float height = 1080f*scale;

    float x = (screenWidth-width)/2f;
    float y = (screenHeight-height)/2f;

    batch.setColor(0.03f,0.05f,0.08f,0.65f);
    batch.draw(reloadProgressPixel,x,y,width,height);
    batch.setColor(Color.WHITE);

    batch.draw(FxAssets.runSummaryShell,x,y,width,height);
    runRestartBounds.set(x+116f*scale,y+98f*scale,544f*scale,96f*scale);
    runArmoryBounds.set(x+688f*scale,y+98f*scale,632f*scale,96f*scale);
    runMainMenuBounds.set(x+1348f*scale,y+98f*scale,456f*scale,96f*scale);

    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

    Texture restart = FxAssets.runRestartNormal;

    if(runRestartBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        restart = FxAssets.runRestartPressed;

    else if(runRestartBounds.contains(mouseX,mouseY))
        restart = FxAssets.runRestartHover;

    Texture armory = FxAssets.runArmoryNormal;
    if(runArmoryBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        armory = FxAssets.runArmoryPressed;

    else if(runArmoryBounds.contains(mouseX,mouseY))
        armory = FxAssets.runArmoryHover;

    Texture mainMenu = FxAssets.runMainMenuNormal;
    if(runMainMenuBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        mainMenu = FxAssets.runMainMenuPressed;

    else if(runMainMenuBounds.contains(mouseX,mouseY))
        mainMenu = FxAssets.runMainMenuHover;

    batch.draw(restart,runRestartBounds.x,runRestartBounds.y,runRestartBounds.width,runRestartBounds.height);
    batch.draw(armory,runArmoryBounds.x,runArmoryBounds.y,runArmoryBounds.width,runArmoryBounds.height);
    batch.draw(mainMenu,runMainMenuBounds.x,runMainMenuBounds.y,runMainMenuBounds.width,runMainMenuBounds.height);
    batch.draw(restart,runRestartBounds.x,runRestartBounds.y,runRestartBounds.width,runRestartBounds.height);

    drawRunSummaryValue(Integer.toString(runState.getCurrentScore()),x+156f*scale,y+635f*scale,96f,scale);
    drawRunSummaryValue(Integer.toString(playerProfile.getBestScore()),x+156f*scale,y+410f*scale,68f,scale);
    drawRunSummaryValue(Integer.toString(runState.getKillCount()),x+960f*scale,y+650f*scale,68f,scale);
    drawRunSummaryValue(Integer.toString(runState.getHeadshotCount()),x+1402f*scale,y+650f*scale,68f,scale);
    drawRunSummaryRightValue(Integer.toString(runState.getGoldEarned()),x+1768f*scale,y+470f*scale,48f,scale);
    drawRunSummaryRightValue(Integer.toString(inventory.getGold()),x+1768f*scale,y+360f*scale,48f,scale);
}

private void handleRunSummaryClick(float mouseX,float mouseY){
    if(runRestartBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        restartGame();
        return;
    }
     if(runArmoryBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        endCurrentRun();
        currentScreen = GameScreen.ARMORY;
        gameOver = false;
        AudioAssets.playMenuMusic();
        return;
    }
    if(runMainMenuBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        endCurrentRun();
        currentScreen = GameScreen.MAIN_MENU;
        gameOver = false;
        AudioAssets.playMenuMusic();
        return;
    }
}

private void renderMainMenu(float mouseX,float mouseY){
    ScreenUtils.clear(0.03f,0.05f,0.08f,1f);

    batch.setProjectionMatrix(hudCamera.combined);
    batch.begin();

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    float scale = Math.min(screenWidth/1920f,screenHeight/1080f);

    float width = 1920f*scale;
    float height = 1080f*scale;

    float x = (screenWidth-width)/2f;
    float y = (screenHeight-height)/2f;

    batch.draw(FxAssets.mainMenuShell,x,y,width,height);

    mainStartBounds.set(x+1212f*scale,y+666f*scale,528f*scale,112f*scale);
    mainArmoryBounds.set(x+1212f*scale,y+490f*scale,528f*scale,144f*scale);
    mainSettingsBounds.set(x+1212f*scale,y+354f*scale,252f*scale,84f*scale);
    mainExitBounds.set(x+1488f*scale,y+354f*scale,252f*scale,84f*scale);

    Texture start = FxAssets.mainStartNormal;
    Texture armory = FxAssets.mainArmoryNormal;
    Texture settings = FxAssets.mainSettingsNormal;
    Texture exit = FxAssets.mainExitNormal;

    if(mainStartBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        start = FxAssets.mainStartPressed;
    else if(mainStartBounds.contains(mouseX,mouseY))
        start = FxAssets.mainStartHover;

    if(mainArmoryBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        armory = FxAssets.mainArmoryPressed;
    else if(mainArmoryBounds.contains(mouseX,mouseY))
        armory = FxAssets.mainArmoryHover;

    if(mainSettingsBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        settings = FxAssets.mainSettingsPressed;
    else if(mainSettingsBounds.contains(mouseX,mouseY))
        settings = FxAssets.mainSettingsHover;

    if(mainExitBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        exit = FxAssets.mainExitPressed;
    else if(mainExitBounds.contains(mouseX,mouseY))
        exit = FxAssets.mainExitHover;

    batch.draw(start,mainStartBounds.x,mainStartBounds.y,mainStartBounds.width,mainStartBounds.height);
    batch.draw(armory,mainArmoryBounds.x,mainArmoryBounds.y,mainArmoryBounds.width,mainArmoryBounds.height);
    batch.draw(settings,mainSettingsBounds.x,mainSettingsBounds.y,mainSettingsBounds.width,mainSettingsBounds.height);
    batch.draw(exit,mainExitBounds.x,mainExitBounds.y,mainExitBounds.width,mainExitBounds.height);

    drawMainMenuValues(x,y,scale);

    batch.end();

    if(Gdx.input.justTouched())
        handleMainMenuClick(mouseX,mouseY);
}

private void drawMainMenuValues(float x,float y,float scale){
    runSummaryFont.getData().setScale((38f/96f)*scale);

    String goldText = Integer.toString(inventory.getGold());
    mainMenuLayout.setText(runSummaryFont,goldText);

    float goldRight = x+1395f*scale;
    float baseline = y+980f*scale;

    runSummaryFont.draw(batch,goldText,goldRight-mainMenuLayout.width,baseline);

    String bestText = Integer.toString(playerProfile.getBestScore());
    mainMenuLayout.setText(runSummaryFont,bestText);

    float bestRight = x+1790f*scale;

    runSummaryFont.draw(batch,bestText,bestRight-mainMenuLayout.width,baseline);
}

private void handleMainMenuClick(float mouseX,float mouseY){
    if(mainStartBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        currentScreen = GameScreen.GAMEPLAY;
        startNewRun();

        return;
    }

    if(mainArmoryBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        currentScreen = GameScreen.ARMORY;
        return;
    }

    if(mainSettingsBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        settingsOpenedFromPause = false;
        currentScreen = GameScreen.SETTINGS;
        return;
    }

    if(mainExitBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        saveManager.saveProfile(playerProfile);
        Gdx.app.exit();
    }
}
private void renderArmory(float mouseX,float mouseY){
    ScreenUtils.clear(0.03f,0.05f,0.08f,1f);

    batch.setProjectionMatrix(hudCamera.combined);
    batch.begin();

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();

    float scale = Math.min(screenWidth/1920f,screenHeight/1080f);

    float width = 1920f*scale;
    float height = 1080f*scale;

    float x = (screenWidth-width)/2f;
    float y = (screenHeight-height)/2f;

    batch.draw(FxAssets.armoryShell,x,y,width,height);

    if(selectedArmoryCategory == ArmoryCategory.WEAPONS)
        batch.draw(FxAssets.armoryWeaponsGrid,x+328f*scale,y+106f*scale,928f*scale,718f*scale);
    else
        batch.draw(FxAssets.armoryIconsGrid,x+328f*scale,y+106f*scale,928f*scale,718f*scale);

    drawArmoryCategories(x,y,scale,mouseX,mouseY);
    armoryActionBounds.set(x+1328f*scale,y+96f*scale,496f*scale,78f*scale);
    batch.draw(FxAssets.armoryActionUnavailable,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);

    if(selectedArmoryCategory == ArmoryCategory.WEAPONS)
        drawArmoryWeapons(x,y,scale,mouseX,mouseY);

    else if(selectedArmoryCategory == ArmoryCategory.POWERUPS)
        drawArmoryPowerups(x,y,scale,mouseX,mouseY);

    else if(selectedArmoryCategory == ArmoryCategory.SPECIAL)
        drawArmorySpecial(x,y,scale,mouseX,mouseY);

    armoryCloseBounds.set(x+1780f*scale,y+938f*scale,68f*scale,68f*scale);
    Texture close = FxAssets.armoryCloseNormal;

    if(armoryCloseBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        close = FxAssets.armoryClosePressed;
    else if(armoryCloseBounds.contains(mouseX,mouseY))
        close = FxAssets.armoryCloseHover;

    batch.draw(close,armoryCloseBounds.x,armoryCloseBounds.y,armoryCloseBounds.width,armoryCloseBounds.height);
    drawArmoryGold(x,y,scale);
    batch.end();
    if(Gdx.input.justTouched())
        handleArmoryClick(mouseX,mouseY);
}

private void drawArmoryCategories(float x,float y,float scale,float mouseX,float mouseY){
    armoryWeaponsCategoryBounds.set(x+72f*scale,y+740f*scale,220f*scale,88f*scale);
    armoryPowerupsCategoryBounds.set(x+72f*scale,y+628f*scale,220f*scale,88f*scale);
    armorySpecialCategoryBounds.set(x+72f*scale,y+516f*scale,220f*scale,88f*scale);

    if(selectedArmoryCategory == ArmoryCategory.WEAPONS)
        batch.draw(FxAssets.armoryCategorySelected,armoryWeaponsCategoryBounds.x,armoryWeaponsCategoryBounds.y,armoryWeaponsCategoryBounds.width,armoryWeaponsCategoryBounds.height);
    else if(armoryWeaponsCategoryBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.armoryCategoryHover,armoryWeaponsCategoryBounds.x,armoryWeaponsCategoryBounds.y,armoryWeaponsCategoryBounds.width,armoryWeaponsCategoryBounds.height);

    if(selectedArmoryCategory == ArmoryCategory.POWERUPS)
        batch.draw(FxAssets.armoryCategorySelected,armoryPowerupsCategoryBounds.x,armoryPowerupsCategoryBounds.y,armoryPowerupsCategoryBounds.width,armoryPowerupsCategoryBounds.height);
    else if(armoryPowerupsCategoryBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.armoryCategoryHover,armoryPowerupsCategoryBounds.x,armoryPowerupsCategoryBounds.y,armoryPowerupsCategoryBounds.width,armoryPowerupsCategoryBounds.height);

    if(selectedArmoryCategory == ArmoryCategory.SPECIAL)
        batch.draw(FxAssets.armoryCategorySelected,armorySpecialCategoryBounds.x,armorySpecialCategoryBounds.y,armorySpecialCategoryBounds.width,armorySpecialCategoryBounds.height);
    else if(armorySpecialCategoryBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.armoryCategoryHover,armorySpecialCategoryBounds.x,armorySpecialCategoryBounds.y,armorySpecialCategoryBounds.width,armorySpecialCategoryBounds.height);
}

private void setArmoryWeaponBounds(float x,float y,float scale){
    armoryWeaponBounds[0].set(x+328f*scale,y+606f*scale,448f*scale,218f*scale);
    armoryWeaponBounds[1].set(x+808f*scale,y+606f*scale,448f*scale,218f*scale);

    armoryWeaponBounds[2].set(x+328f*scale,y+362f*scale,448f*scale,218f*scale);
    armoryWeaponBounds[3].set(x+808f*scale,y+362f*scale,448f*scale,218f*scale);

    armoryWeaponBounds[4].set(x+328f*scale,y+118f*scale,448f*scale,218f*scale);
    armoryWeaponBounds[5].set(x+808f*scale,y+118f*scale,448f*scale,218f*scale);
}

private void drawArmoryWeapons(float x,float y,float scale,float mouseX,float mouseY){
    setArmoryWeaponBounds(x,y,scale);

    for(int i=0;i<armoryWeaponBounds.length;i++){
        Rectangle bounds = armoryWeaponBounds[i];
        WeaponType type = INVENTORY_WEAPONS[i];

        Texture gun = FxAssets.weaponRewardIcons.get(type);
        drawContain(gun,bounds.x+20f*scale,bounds.y+80f*scale,408f*scale,120f*scale);

        runSummaryFont.getData().setScale((26f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,getWeaponName(type),bounds.x+20f*scale,bounds.y+40f*scale,408f*scale,28f*scale);

        runSummaryFont.getData().setScale((24f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,Integer.toString(getWeaponPrice(type)),bounds.x+20f*scale,bounds.y+8f*scale,408f*scale,26f*scale);

        if(i == selectedArmoryProduct)
            batch.draw(FxAssets.armoryWeaponCardSelected,bounds.x,bounds.y,bounds.width,bounds.height);

        else if(bounds.contains(mouseX,mouseY))
            batch.draw(FxAssets.armoryWeaponCardHover,bounds.x,bounds.y,bounds.width,bounds.height);
    }

    drawArmoryWeaponDetails(x,y,scale,mouseX,mouseY);
}

private WeaponType getSelectedArmoryWeapon(){
    if(selectedArmoryCategory != ArmoryCategory.WEAPONS)
        return null;
    if(selectedArmoryProduct < 0 || selectedArmoryProduct >= INVENTORY_WEAPONS.length)
        return null;
    return INVENTORY_WEAPONS[selectedArmoryProduct];
}

private void drawArmoryWeaponDetails(float x,float y,float scale,float mouseX,float mouseY){
    WeaponType type = getSelectedArmoryWeapon();

    if(type == null)
        return;

    fieldShopTitleFont.getData().setScale(0.75f*scale);
    fieldShopBodyFont.getData().setScale(scale);
    fieldShopInfoFont.getData().setScale(1.1f*scale);

    Texture gun = FxAssets.weaponRewardIcons.get(type);
    drawContain(gun,x+1344f*scale,y+672f*scale,464f*scale,148f*scale);
    drawFieldShopCenteredText(fieldShopTitleFont,getWeaponName(type),x+1328f*scale,y+572f*scale,496f*scale,68f*scale);
    drawInventoryCenteredText(fieldShopBodyFont,getWeaponDescription(type),x+1328f*scale,y+448f*scale,496f*scale,84f*scale,true);

    String effect = "PERMANENT WEAPON UNLOCK\nAVAILABLE FOR EVERY RUN";
    drawInventoryCenteredText(fieldShopInfoFont,effect,x+1328f*scale,y+292f*scale,496f*scale,116f*scale,true);

    int price = getWeaponPrice(type);
    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(price),x+1328f*scale,y+194f*scale,206f*scale,40f*scale);

    String status = inventory.ownsWeapon(type) ? "OWNED" : "NOT OWNED";
    drawFieldShopCenteredText(fieldShopInfoFont,status,x+1568f*scale,y+194f*scale,256f*scale,40f*scale);

    drawArmoryWeaponButton(type,price,mouseX,mouseY);
}
private void drawArmoryWeaponButton(WeaponType type,int price,float mouseX,float mouseY){
    if(inventory.ownsWeapon(type)){
        batch.draw(FxAssets.armoryActionOwned,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
        return;
    }
    if(inventory.getGold() < price){
        batch.draw(FxAssets.armoryActionInsufficientGold,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
        return;
    }

    Texture button = FxAssets.armoryActionBuy;

    if(armoryActionBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.armoryActionBuyPressed;
    else if(armoryActionBounds.contains(mouseX,mouseY))
        button = FxAssets.armoryActionBuyHover;

    batch.draw(button,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
}

private void buySelectedArmoryWeapon(){
    WeaponType type = getSelectedArmoryWeapon();
    if(type == null)
        return;
    if(inventory.ownsWeapon(type))
        return;
    int price = getWeaponPrice(type);

    if(!inventory.spendGold(price))
        return;
    inventory.addWeapon(type);

    saveManager.saveProfile(playerProfile);
    AudioAssets.playUiConfirm();
}

private void drawArmoryPowerups(float x,float y,float scale,float mouseX,float mouseY){
    setArmoryIconBounds(x,y,scale);

    for(int i=0;i<armoryIconBounds.length;i++){
        Rectangle bounds = armoryIconBounds[i];
        Texture icon = getFieldShopPowerupIcon(i);

        drawContain(icon,bounds.x+64f*scale,bounds.y+64f*scale,92f*scale,92f*scale);

        runSummaryFont.getData().setScale((24f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,getFieldShopPowerupName(i),bounds.x+14f*scale,bounds.y+30f*scale,192f*scale,28f*scale);

        runSummaryFont.getData().setScale((22f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,Integer.toString(getFieldShopPowerupPrice(i)),bounds.x+14f*scale,bounds.y+4f*scale,192f*scale,24f*scale);

        if(i == selectedArmoryProduct)
            batch.draw(FxAssets.armoryIconCardSelected,bounds.x,bounds.y,bounds.width,bounds.height);

        else if(bounds.contains(mouseX,mouseY))
            batch.draw(FxAssets.armoryIconCardHover,bounds.x,bounds.y,bounds.width,bounds.height);
    }

    drawArmoryPowerupDetails(x,y,scale,mouseX,mouseY);
}

private void drawArmoryPowerupDetails(float x,float y,float scale,float mouseX,float mouseY){
    if(selectedArmoryCategory != ArmoryCategory.POWERUPS)
        return;

    if(selectedArmoryProduct < 0 || selectedArmoryProduct >= 16)
        return;

    int index = selectedArmoryProduct;

    fieldShopTitleFont.getData().setScale(0.75f*scale);
    fieldShopBodyFont.getData().setScale(scale);
    fieldShopInfoFont.getData().setScale(1.1f*scale);

    Texture icon = getFieldShopPowerupIcon(index);

    drawContain(icon,x+1344f*scale,y+672f*scale,464f*scale,148f*scale);

    drawFieldShopCenteredText(fieldShopTitleFont,getFieldShopPowerupName(index),x+1328f*scale,y+572f*scale,496f*scale,68f*scale);

    drawInventoryCenteredText(fieldShopBodyFont,getFieldShopPowerupDescription(index),x+1328f*scale,y+448f*scale,496f*scale,84f*scale,true);

    drawFieldShopCenteredText(fieldShopInfoFont,"DURATION  "+getFieldShopPowerupDuration(index),x+1328f*scale,y+292f*scale,496f*scale,116f*scale);

    int price = getFieldShopPowerupPrice(index);
    int amount = getFieldShopPowerupAmount(index);

    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(price),x+1328f*scale,y+194f*scale,206f*scale,40f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,"x"+amount,x+1568f*scale,y+194f*scale,256f*scale,40f*scale);

    drawArmoryPowerupButton(price,mouseX,mouseY);
}

private void drawArmoryPowerupButton(int price,float mouseX,float mouseY){
    if(inventory.getGold() < price){
        batch.draw(FxAssets.armoryActionInsufficientGold,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
        return;
    }

    Texture button = FxAssets.armoryActionBuy;

    if(armoryActionBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.armoryActionBuyPressed;
    else if(armoryActionBounds.contains(mouseX,mouseY))
        button = FxAssets.armoryActionBuyHover;

    batch.draw(button,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
}

private void buySelectedArmoryPowerup(){
    if(selectedArmoryCategory != ArmoryCategory.POWERUPS)
        return;
    if(selectedArmoryProduct < 0 || selectedArmoryProduct >= 16)
        return;

    int price = getFieldShopPowerupPrice(selectedArmoryProduct);
    if(!inventory.spendGold(price))
        return;

    addFieldShopPowerup(selectedArmoryProduct);

    saveManager.saveProfile(playerProfile);
    AudioAssets.playUiConfirm();
}
private void drawArmorySpecial(float x,float y,float scale,float mouseX,float mouseY){
    setArmoryIconBounds(x,y,scale);

    for(int i=0;i<6;i++){
        Rectangle bounds = armoryIconBounds[i];
        Texture icon = FxAssets.shopSpecialIcons[i];

        drawContain(icon,bounds.x+64f*scale,bounds.y+64f*scale,92f*scale,92f*scale);

        runSummaryFont.getData().setScale((24f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,getFieldShopSpecialName(i),bounds.x+14f*scale,bounds.y+30f*scale,192f*scale,28f*scale);

        runSummaryFont.getData().setScale((20f/96f)*scale);
        drawFieldShopCenteredText(runSummaryFont,"FIELD ONLY",bounds.x+14f*scale,bounds.y+4f*scale,192f*scale,24f*scale);

        if(i == selectedArmoryProduct)
            batch.draw(FxAssets.armoryIconCardSelected,bounds.x,bounds.y,bounds.width,bounds.height);

        else if(bounds.contains(mouseX,mouseY))
            batch.draw(FxAssets.armoryIconCardHover,bounds.x,bounds.y,bounds.width,bounds.height);
    }

    drawArmorySpecialDetails(x,y,scale);
}

private void drawArmorySpecialDetails(float x,float y,float scale){
    if(selectedArmoryCategory != ArmoryCategory.SPECIAL)
        return;

    if(selectedArmoryProduct < 0 || selectedArmoryProduct >= 6)
        return;

    int index = selectedArmoryProduct;

    fieldShopTitleFont.getData().setScale(0.75f*scale);
    fieldShopBodyFont.getData().setScale(scale);
    fieldShopInfoFont.getData().setScale(1.1f*scale);

    Texture icon = FxAssets.shopSpecialIcons[index];

    drawContain(icon,x+1344f*scale,y+672f*scale,464f*scale,148f*scale);

    drawFieldShopCenteredText(fieldShopTitleFont,getFieldShopSpecialName(index),x+1328f*scale,y+572f*scale,496f*scale,68f*scale);

    drawInventoryCenteredText(fieldShopBodyFont,getFieldShopSpecialDescription(index),x+1328f*scale,y+448f*scale,496f*scale,84f*scale,true);

    drawFieldShopCenteredText(fieldShopInfoFont,getFieldShopSpecialEffect(index),x+1328f*scale,y+292f*scale,496f*scale,116f*scale);

    drawFieldShopCenteredText(fieldShopInfoFont,"--",x+1328f*scale,y+194f*scale,206f*scale,40f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,"FIELD ONLY",x+1568f*scale,y+194f*scale,256f*scale,40f*scale);

    batch.draw(FxAssets.armoryActionUnavailable,armoryActionBounds.x,armoryActionBounds.y,armoryActionBounds.width,armoryActionBounds.height);
}

private void drawArmoryGold(float x,float y,float scale){
    String goldText = Integer.toString(inventory.getGold());
    runSummaryFont.getData().setScale((40f/96f)*scale);
    mainMenuLayout.setText(runSummaryFont,goldText);
    float boxX = x+1560f*scale;
    float boxY = y+956f*scale;
    float boxWidth = 170f*scale;
    float boxHeight = 40f*scale;

    float textX = boxX+(boxWidth-mainMenuLayout.width)/2f;
    float textY = boxY+(boxHeight+mainMenuLayout.height)/2f;
    runSummaryFont.draw(batch,goldText,textX,textY);
}
private void handleArmoryClick(float mouseX,float mouseY){
    if(armoryCloseBounds.contains(mouseX,mouseY)){
        AudioAssets.playUiConfirm();
        currentScreen = GameScreen.MAIN_MENU;
        return;
    }

    if(armoryWeaponsCategoryBounds.contains(mouseX,mouseY)){
        selectedArmoryCategory = ArmoryCategory.WEAPONS;
        selectedArmoryProduct = -1;
        AudioAssets.playUiConfirm();
        return;
    }

    if(armoryPowerupsCategoryBounds.contains(mouseX,mouseY)){
        selectedArmoryCategory = ArmoryCategory.POWERUPS;
        selectedArmoryProduct = -1;
        AudioAssets.playUiConfirm();
        return;
    }

    if(armorySpecialCategoryBounds.contains(mouseX,mouseY)){
        selectedArmoryCategory = ArmoryCategory.SPECIAL;
        selectedArmoryProduct = -1;
        AudioAssets.playUiConfirm();
        return;
    }

    if(armoryActionBounds.contains(mouseX,mouseY)){
        handleArmoryAction();
        return;
    }

    if(selectedArmoryCategory == ArmoryCategory.WEAPONS){
        for(int i=0;i<armoryWeaponBounds.length;i++){
            if(armoryWeaponBounds[i].contains(mouseX,mouseY)){
                selectedArmoryProduct = i;
                AudioAssets.playUiConfirm();
                return;
            }
        }
    }

    if(selectedArmoryCategory == ArmoryCategory.POWERUPS){
        for(int i=0;i<armoryIconBounds.length;i++){
            if(armoryIconBounds[i].contains(mouseX,mouseY)){
                selectedArmoryProduct = i;
                AudioAssets.playUiConfirm();
                return;
            }
        }
    }

    if(selectedArmoryCategory == ArmoryCategory.SPECIAL){
        for(int i=0;i<6;i++){
            if(armoryIconBounds[i].contains(mouseX,mouseY)){
                selectedArmoryProduct = i;
                AudioAssets.playUiConfirm();
                return;
            }
        }
    }
}

private void handleArmoryAction(){
    if(selectedArmoryCategory == ArmoryCategory.WEAPONS){
        buySelectedArmoryWeapon();
        return;
    }

    if(selectedArmoryCategory == ArmoryCategory.POWERUPS)
        buySelectedArmoryPowerup();
}

private void renderSettings(){
    ScreenUtils.clear(0.03f,0.05f,0.08f,1f);

    float mouseX = Gdx.input.getX();
    float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();

    batch.setProjectionMatrix(hudCamera.combined);
    batch.begin();

    float screenWidth = Gdx.graphics.getWidth();
    float screenHeight = Gdx.graphics.getHeight();
    float scale = Math.min(screenWidth/1920f,screenHeight/1080f);
    float width = 1920f*scale;
    float height = 1080f*scale;
    float x = (screenWidth-width)/2f;
    float y = (screenHeight-height)/2f;

    batch.draw(FxAssets.settingsPanel,x,y,width,height);

    drawSettingsSliders(x,y,scale,mouseX,mouseY);
    drawSettingsFullscreen(x,y,scale,mouseX,mouseY);
    drawSettingsResolution(x,y,scale,mouseX,mouseY);
    drawSettingsButtons(x,y,scale,mouseX,mouseY);

    if(controlsOverlayOpen)
        drawControlsOverlay(x,y,scale,mouseX,mouseY);
    if(resetConfirmOpen)
        drawResetConfirmation(x,y,scale,mouseX,mouseY);

    batch.end();
    updateSettingsInput(x,y,scale,mouseX,mouseY);
}




private void drawSettingsSliders(float x,float y,float scale,float mouseX,float mouseY){
    musicSliderBounds.set(x+144f*scale,y+644f*scale,744f*scale,64f*scale);
    sfxSliderBounds.set(x+144f*scale,y+472f*scale,744f*scale,64f*scale);

    drawSettingsSlider(musicVolume,x,y,scale,676f,musicSliderBounds,draggingMusicSlider,mouseX,mouseY);
    drawSettingsSlider(sfxVolume,x,y,scale,504f,sfxSliderBounds,draggingSfxSlider,mouseX,mouseY);

    fieldShopInfoFont.getData().setScale(scale);

    drawFieldShopCenteredText(fieldShopInfoFont,(int)(musicVolume*100f)+"%",x+766f*scale,y+717f*scale,96f*scale,36f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,(int)(sfxVolume*100f)+"%",x+766f*scale,y+545f*scale,96f*scale,36f*scale);
}

private void drawSettingsSlider(float value,float x,float y,float scale,float centerY,Rectangle hitbox,boolean dragging,float mouseX,float mouseY){
    float fillWidth = 696f*value;
    if(fillWidth > 0f)
        batch.draw(FxAssets.settingsSliderFill,x+168f*scale,y+(centerY-4f)*scale,fillWidth*scale,8f*scale);

    float centerX = 168f+696f*value;

    float thumbX = x+(centerX-18f)*scale;
    float thumbY = y+(centerY-24f)*scale;

    Texture thumb = FxAssets.settingsSliderThumbNormal;

    if(dragging)
        thumb = FxAssets.settingsSliderThumbPressed;
    else if(hitbox.contains(mouseX,mouseY))
        thumb = FxAssets.settingsSliderThumbHover;

    batch.draw(thumb,thumbX,thumbY,36f*scale,48f*scale);
}

private void drawSettingsFullscreen(float x,float y,float scale,float mouseX,float mouseY){
    settingsFullscreenBounds.set(x+1608f*scale,y+704f*scale,148f*scale,56f*scale);
    boolean fullscreen = Gdx.graphics.isFullscreen();
    boolean hover = settingsFullscreenBounds.contains(mouseX,mouseY);

    Texture toggle;

    if(fullscreen)
        toggle = hover ? FxAssets.settingsFullscreenOnHover : FxAssets.settingsFullscreenOn;
    else
        toggle = hover ? FxAssets.settingsFullscreenOffHover : FxAssets.settingsFullscreenOff;

    batch.draw(toggle,settingsFullscreenBounds.x,settingsFullscreenBounds.y,settingsFullscreenBounds.width,settingsFullscreenBounds.height);

    fieldShopInfoFont.getData().setScale(scale);

    String text = fullscreen ? "ON" : "OFF";

    drawFieldShopCenteredText(fieldShopInfoFont,text,x+1436f*scale,y+712f*scale,144f*scale,40f*scale);
}

private void drawSettingsResolution(float x,float y,float scale,float mouseX,float mouseY){
    settingsResolutionBounds.set(x+1000f*scale,y+446f*scale,756f*scale,74f*scale);

    Texture texture = FxAssets.settingsResolutionNormal;

    if(settingsResolutionBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        texture = FxAssets.settingsResolutionPressed;
    else if(settingsResolutionBounds.contains(mouseX,mouseY))
        texture = FxAssets.settingsResolutionHover;

    batch.draw(texture,settingsResolutionBounds.x,settingsResolutionBounds.y,settingsResolutionBounds.width,settingsResolutionBounds.height);

    int[] resolution = SETTINGS_RESOLUTIONS[settingsResolutionIndex];

    String text = resolution[0]+" × "+resolution[1];

    fieldShopInfoFont.getData().setScale(1.2f*scale);

    drawFieldShopCenteredText(fieldShopInfoFont,text,x+1024f*scale,y+464f*scale,632f*scale,38f*scale);
}

private void drawSettingsButtons(float x,float y,float scale,float mouseX,float mouseY){
    settingsControlsBounds.set(x+548f*scale,y+260f*scale,328f*scale,74f*scale);
    settingsResetBounds.set(x+1324f*scale,y+260f*scale,432f*scale,74f*scale);
    settingsBackBounds.set(x+116f*scale,y+88f*scale,328f*scale,78f*scale);

    Texture controls = FxAssets.settingsControlsNormal;
    Texture reset = FxAssets.settingsResetNormal;
    Texture back = FxAssets.settingsBackNormal;

    if(settingsControlsBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        controls = FxAssets.settingsControlsPressed;
    else if(settingsControlsBounds.contains(mouseX,mouseY))
        controls = FxAssets.settingsControlsHover;

    if(settingsResetBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        reset = FxAssets.settingsResetPressed;
    else if(settingsResetBounds.contains(mouseX,mouseY))
        reset = FxAssets.settingsResetHover;

    if(settingsBackBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        back = FxAssets.settingsBackPressed;
    else if(settingsBackBounds.contains(mouseX,mouseY))
        back = FxAssets.settingsBackHover;

    batch.draw(controls,settingsControlsBounds.x,settingsControlsBounds.y,settingsControlsBounds.width,settingsControlsBounds.height);
    batch.draw(reset,settingsResetBounds.x,settingsResetBounds.y,settingsResetBounds.width,settingsResetBounds.height);
    batch.draw(back,settingsBackBounds.x,settingsBackBounds.y,settingsBackBounds.width,settingsBackBounds.height);
}

private void updateSettingsInput(float x,float y,float scale,float mouseX,float mouseY){
    if(controlsOverlayOpen){
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) &&
           controlsOverlayBackBounds.contains(mouseX,mouseY)){
            controlsOverlayOpen = false;
            AudioAssets.playUiCancel();
        }
        return;
    }

    if(resetConfirmOpen){
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            if(resetConfirmYesBounds.contains(mouseX,mouseY)){
                resetSaveData();
                resetConfirmOpen = false;
                return;
            }

            if(resetConfirmNoBounds.contains(mouseX,mouseY)){
                resetConfirmOpen = false;
                AudioAssets.playUiCancel();
                return;
            }
        }
        return;
    }

    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
        if(musicSliderBounds.contains(mouseX,mouseY))
            draggingMusicSlider = true;

        else if(sfxSliderBounds.contains(mouseX,mouseY))
            draggingSfxSlider = true;

        else if(settingsFullscreenBounds.contains(mouseX,mouseY)){
            toggleFullscreen();
            return;
        }

        else if(settingsResolutionBounds.contains(mouseX,mouseY)){
            cycleSettingsResolution();
            return;
        }

        else if(settingsControlsBounds.contains(mouseX,mouseY)){
            controlsOverlayOpen = true;
            AudioAssets.playUiPanelOpen();
            return;
        }

        else if(settingsResetBounds.contains(mouseX,mouseY)){
            resetConfirmOpen = true;
            AudioAssets.playUiPanelOpen();
            return;
        }

        else if(settingsBackBounds.contains(mouseX,mouseY)){
            AudioAssets.playUiConfirm();
            if(settingsOpenedFromPause){
                settingsOpenedFromPause = false;
                currentScreen = GameScreen.GAMEPLAY;
            }
            else
                currentScreen = GameScreen.MAIN_MENU;
            return;
        }
    }

    if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
        draggingMusicSlider = false;
        draggingSfxSlider = false;
    }

    if(draggingMusicSlider){
        float logicalMouseX = (mouseX-x)/scale;
        musicVolume = MathUtils.clamp((logicalMouseX-168f)/696f,0f,1f);
        AudioAssets.setMusicVolume(musicVolume);
    }

    if(draggingSfxSlider){
        float logicalMouseX = (mouseX-x)/scale;
        sfxVolume = MathUtils.clamp((logicalMouseX-168f)/696f,0f,1f);
        AudioAssets.setSfxVolume(sfxVolume);
    }
}

private void drawResetConfirmation(float x,float y,float scale,float mouseX,float mouseY){
    batch.setColor(0.02f,0.04f,0.07f,0.96f);
    batch.draw(reloadProgressPixel,x+520f*scale,y+300f*scale,880f*scale,420f*scale);
    batch.setColor(Color.WHITE);

    fieldShopTitleFont.getData().setScale(scale);
    fieldShopBodyFont.getData().setScale(scale);
    fieldShopInfoFont.getData().setScale(scale);

    drawFieldShopCenteredText(fieldShopTitleFont, "RESET SAVE DATA?", x+620f*scale, y+610f*scale,680f*scale,60f*scale);
    drawInventoryCenteredText(fieldShopBodyFont,"This will permanently erase gold, best score, weapons and stored powerups.",x+650f*scale,y+470f*scale,620f*scale,100f*scale, true);

    resetConfirmYesBounds.set(x+650f*scale,y+350f*scale,280f*scale,70f*scale);
    resetConfirmNoBounds.set(x+990f*scale,y+350f*scale,280f*scale,70f*scale);

    Texture yes = FxAssets.settingsResetNormal;
    Texture no = FxAssets.settingsBackNormal;

    if(resetConfirmYesBounds.contains(mouseX,mouseY))
        yes = FxAssets.settingsResetHover;

    if(resetConfirmNoBounds.contains(mouseX,mouseY))
        no = FxAssets.settingsBackHover;

    batch.draw(yes,resetConfirmYesBounds.x,resetConfirmYesBounds.y,resetConfirmYesBounds.width,resetConfirmYesBounds.height);
    batch.draw(no,resetConfirmNoBounds.x,resetConfirmNoBounds.y,resetConfirmNoBounds.width,resetConfirmNoBounds.height);
}

private void resetSaveData(){
    saveManager.clearSave();

    playerProfile = new PlayerProfile();
    inventory = playerProfile.getInventory();

    saveManager.saveProfile(playerProfile);

    AudioAssets.playUiConfirm();
}


private void toggleFullscreen(){
    if(Gdx.graphics.isFullscreen()){
        int[] resolution = SETTINGS_RESOLUTIONS[settingsResolutionIndex];
        Gdx.graphics.setWindowedMode(resolution[0],resolution[1]);
    }
    else
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
}

private void cycleSettingsResolution(){
    settingsResolutionIndex++;

    if(settingsResolutionIndex >= SETTINGS_RESOLUTIONS.length)
        settingsResolutionIndex = 0;

    if(!Gdx.graphics.isFullscreen()){
        int[] resolution = SETTINGS_RESOLUTIONS[settingsResolutionIndex];
        Gdx.graphics.setWindowedMode(resolution[0],resolution[1]);
    }
}

private void drawControlsOverlay(float x,float y,float scale,float mouseX,float mouseY){
    batch.setColor(0.02f,0.04f,0.07f,0.94f);
    batch.draw(reloadProgressPixel,x+420f*scale,y+180f*scale,1080f*scale,720f*scale);
    batch.setColor(Color.WHITE);

    fieldShopTitleFont.getData().setScale(scale);
    drawFieldShopCenteredText(fieldShopTitleFont,"CONTROLS",x+620f*scale,y+810f*scale,680f*scale,60f*scale);

    batch.draw(FxAssets.interactionEInteract,x+520f*scale,y+690f*scale,224f*scale,48f*scale);
    batch.draw(FxAssets.interactionEOpen,x+520f*scale,y+620f*scale,176f*scale,48f*scale);
    batch.draw(FxAssets.interactionEShop,x+520f*scale,y+550f*scale,176f*scale,48f*scale);

    batch.draw(FxAssets.interactionQClaim,x+850f*scale,y+690f*scale,176f*scale,48f*scale);

    batch.draw(FxAssets.interactionRReload,x+850f*scale,y+620f*scale,224f*scale,48f*scale);
    batch.draw(FxAssets.interactionRmbOrRReload,x+850f*scale,y+550f*scale,280f*scale,48f*scale);

    fieldShopInfoFont.getData().setScale(scale);

    drawFieldShopCenteredText(fieldShopInfoFont,"MOVE   A / D",x+520f*scale,y+430f*scale,300f*scale,40f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,"JUMP   W",x+520f*scale,y+380f*scale,300f*scale,40f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,"CROUCH   C",x+520f*scale,y+330f*scale,300f*scale,40f*scale);
    drawFieldShopCenteredText(fieldShopInfoFont,"PAUSE   ESC",x+850f*scale,y+430f*scale,300f*scale,40f*scale);

    controlsOverlayBackBounds.set(x+760f*scale,y+220f*scale,400f*scale,70f*scale);

    Texture back = FxAssets.settingsBackNormal;

    if(controlsOverlayBackBounds.contains(mouseX,mouseY))
        back = FxAssets.settingsBackHover;

    batch.draw(back,controlsOverlayBackBounds.x,controlsOverlayBackBounds.y,controlsOverlayBackBounds.width,controlsOverlayBackBounds.height);
}

private void setFieldShopPowerupBounds(){
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;

    float startX = 316f;
    float startY = 666f;

    float cardWidth = 236f;
    float cardHeight = 172f;

    float horizontalStep = 252f;
    float verticalStep = 188f;

    for(int i = 0 ; i < fieldShopPowerupBounds.length ; i++){
        int column = i%4;
        int row = i/4;

        float x = startX+column*horizontalStep;
        float y = startY-row*verticalStep;

        fieldShopPowerupBounds[i].set(x*scaleX,y*scaleY,cardWidth*scaleX,cardHeight*scaleY);
    }
}

private void HudDrawTheShop(float mouseX, float mouseY){
    if(!fieldShopOpen)
        return;
    float scaleX = Gdx.graphics.getWidth() / 1920f;
    float scaleY = Gdx.graphics.getHeight() / 1080f;

    fieldShopCloseBounds.set(1782f*scaleX,944f*scaleY,68f*scaleX,66f*scaleY);
    batch.draw(FxAssets.shopPanel, 0 ,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

    if(selectedFieldShopCategory == FieldShopCategory.WEAPONS)
        batch.draw(FxAssets.shopWeaponsCatalog,316f*scaleX,102f*scaleY,992f*scaleX,736f*scaleY);
    else if(selectedFieldShopCategory == FieldShopCategory.AMMO)
        batch.draw(FxAssets.shopAmmoCatalog,316f*scaleX,102f*scaleY,992f*scaleX,736f*scaleY);
    else if(selectedFieldShopCategory == FieldShopCategory.SPECIAL)
        batch.draw(FxAssets.shopSpecialCatalog,316f*scaleX,102f*scaleY,992f*scaleX,736f*scaleY);

    if(fieldShopCloseBounds.contains(mouseX,mouseY)){
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            batch.draw(FxAssets.shopClosePressed,fieldShopCloseBounds.x,fieldShopCloseBounds.y,fieldShopCloseBounds.width,fieldShopCloseBounds.height);
            return;
        }
        batch.draw(FxAssets.shopCloseHover,fieldShopCloseBounds.x,fieldShopCloseBounds.y,fieldShopCloseBounds.width,fieldShopCloseBounds.height);
    }

    String goldText = Integer.toString(inventory.getGold());
    float fontScale = Math.min(scaleX,scaleY);
    runSummaryFont.getData().setScale(30f/96f * fontScale);
    mainMenuLayout.setText(runSummaryFont,goldText);
    float goldX = 1560 * scaleX;
    float goldY = 956 * scaleY;
    float goldWidth = 170 * scaleX;
    float goldHeight = 40 * scaleY;
    float goldTextX = goldX+(goldWidth-mainMenuLayout.width)/2f;
    float goldTextY = goldY+(goldHeight+mainMenuLayout.height)/2f;
    runSummaryFont.draw(batch,goldText,goldTextX,goldTextY);

    if(selectedFieldShopCategory == FieldShopCategory.POWERUPS){
        drawFieldShopPowerups();
        setFieldShopPowerupBounds();
        drawFieldShopPowerupDetails(mouseX, mouseY);
        for(int i = 0 ; i < fieldShopPowerupBounds.length ; i++){
            Rectangle productBounds = fieldShopPowerupBounds[i];
            if(i == selectedFieldShopProduct)
                batch.draw(FxAssets.shopCardSelected,productBounds.x,productBounds.y,productBounds.width,productBounds.height);
            else if(productBounds.contains(mouseX,mouseY))
                batch.draw(FxAssets.shopCardHover,productBounds.x,productBounds.y,productBounds.width,productBounds.height);

        }
    }

    if(selectedFieldShopCategory == FieldShopCategory.WEAPONS){
        setFieldShopWideBounds(fieldShopWeaponProductBounds);
        drawFieldShopWeapons();
        drawFieldShopWeaponDetails(mouseX,mouseY);
        for(int i=0;i<fieldShopWeaponProductBounds.length;i++){
            Rectangle bounds = fieldShopWeaponProductBounds[i];
            if(i == selectedFieldShopProduct)
                batch.draw(FxAssets.shopWeaponSelected,bounds.x,bounds.y,bounds.width,bounds.height);
            else if(bounds.contains(mouseX,mouseY))
                batch.draw(FxAssets.shopWeaponHover,bounds.x,bounds.y,bounds.width,bounds.height);
        }
    }

    if(selectedFieldShopCategory == FieldShopCategory.AMMO){
        setFieldShopAmmoBounds();
        drawFieldShopAmmo();
        drawFieldShopAmmoDetails(mouseX,mouseY);
        for(int i=0;i<fieldShopAmmoProductBounds.length;i++){
            Rectangle bounds = fieldShopAmmoProductBounds[i];
            if(i == selectedFieldShopProduct)
                batch.draw(FxAssets.shopAmmoSelected,bounds.x,bounds.y,bounds.width,bounds.height);
            else if(bounds.contains(mouseX,mouseY))
                batch.draw(FxAssets.shopAmmoHover,bounds.x,bounds.y,bounds.width,bounds.height);
        }
    }

    if(selectedFieldShopCategory == FieldShopCategory.SPECIAL){
        setFieldShopWideBounds(fieldShopSpecialProductBounds);
        drawFieldShopSpecialDetails(mouseX,mouseY);
        drawFieldShopSpecial();
        for(int i=0;i<fieldShopSpecialProductBounds.length;i++){
            Rectangle bounds = fieldShopSpecialProductBounds[i];
            if(i == selectedFieldShopProduct)
                batch.draw(FxAssets.shopSpecialSelected,bounds.x,bounds.y,bounds.width,bounds.height);
            else if(bounds.contains(mouseX,mouseY))
                batch.draw(FxAssets.shopSpecialHover,bounds.x,bounds.y,bounds.width,bounds.height);
        }
    }

    fieldShopWeaponsBounds.set(68f*scaleX,762f*scaleY,216f*scaleX,82f*scaleY);
    fieldShopAmmoBounds.set(68*scaleX,662*scaleY,216*scaleX,82*scaleY);
    fieldShopPowerupsBounds.set(68*scaleX,562*scaleY,216*scaleX,82*scaleY);
    fieldShopSpecialBounds.set(68*scaleX,462*scaleY,216*scaleX,82*scaleY);

    if(selectedFieldShopCategory == FieldShopCategory.WEAPONS)
        batch.draw(FxAssets.shopCategorySelected,fieldShopWeaponsBounds.x,fieldShopWeaponsBounds.y,fieldShopWeaponsBounds.width,fieldShopWeaponsBounds.height);
    else if(fieldShopWeaponsBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.shopCategoryHover,fieldShopWeaponsBounds.x,fieldShopWeaponsBounds.y,fieldShopWeaponsBounds.width,fieldShopWeaponsBounds.height);

    if(selectedFieldShopCategory == FieldShopCategory.AMMO)
        batch.draw(FxAssets.shopCategorySelected,fieldShopAmmoBounds.x,fieldShopAmmoBounds.y,fieldShopAmmoBounds.width,fieldShopAmmoBounds.height);
    else if(fieldShopAmmoBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.shopCategoryHover,fieldShopAmmoBounds.x,fieldShopAmmoBounds.y,fieldShopAmmoBounds.width,fieldShopAmmoBounds.height);
    
    if(selectedFieldShopCategory == FieldShopCategory.POWERUPS)
        batch.draw(FxAssets.shopCategorySelected,fieldShopPowerupsBounds.x,fieldShopPowerupsBounds.y,fieldShopPowerupsBounds.width,fieldShopPowerupsBounds.height);
    else if(fieldShopPowerupsBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.shopCategoryHover,fieldShopPowerupsBounds.x,fieldShopPowerupsBounds.y,fieldShopPowerupsBounds.width,fieldShopPowerupsBounds.height);
    
    if(selectedFieldShopCategory == FieldShopCategory.SPECIAL)
        batch.draw(FxAssets.shopCategorySelected,fieldShopSpecialBounds.x,fieldShopSpecialBounds.y,fieldShopSpecialBounds.width,fieldShopSpecialBounds.height); 
    else if(fieldShopSpecialBounds.contains(mouseX,mouseY))
        batch.draw(FxAssets.shopCategoryHover,fieldShopSpecialBounds.x,fieldShopSpecialBounds.y,fieldShopSpecialBounds.width,fieldShopSpecialBounds.height); 
        
        
}

private Texture getFieldShopPowerupIcon(int index){
    if(index < 4)
        return FxAssets.powerupIcons.get(INVENTORY_NORMAL_POWERUPS[index]);
    if(index < 10)
        return FxAssets.spicyIcons.get(INVENTORY_SPICY_POWERUPS[index-4]);
    if(index < 15)
        return FxAssets.moreSpicyIcons.get(INVENTORY_MORE_SPICY_POWERUPS[index-10]);
    if(index == 15)
        return FxAssets.godModeIcon;

    return null;
}

private void drawFieldShopPowerups(){
    if(selectedFieldShopCategory != FieldShopCategory.POWERUPS)
        return;
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    for(int i=0;i<fieldShopPowerupBounds.length;i++){
        Texture icon = getFieldShopPowerupIcon(i);
        if(icon == null)
            continue;
        Rectangle bounds = fieldShopPowerupBounds[i];
        float iconX = bounds.x+16f*scaleX;
        float iconY = bounds.y+64f*scaleY;
        float iconWidth = 204f*scaleX;
        float iconHeight = 94f*scaleY;
        drawContain(icon,iconX,iconY,iconWidth,iconHeight);

        String name = getFieldShopPowerupName(i);
        fieldShopInfoFont.getData().setScale(Math.min(scaleX,scaleY)*0.75f);
        float nameX = bounds.x+8f*scaleX;
        float nameY = bounds.y+18f*scaleY;
        float nameWidth = 220f*scaleX;
        float nameHeight = 32f*scaleY;

        drawFieldShopCenteredText(fieldShopInfoFont,name,nameX,nameY,nameWidth,nameHeight);
    }
}

private void drawFieldShopPowerupDetails(float mouseX,float mouseY){
    if(selectedFieldShopCategory != FieldShopCategory.POWERUPS)
        return;

    if(selectedFieldShopProduct < 0 || selectedFieldShopProduct >= 16)
        return;

    int index = selectedFieldShopProduct;

    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    float fontScale = Math.min(scaleX,scaleY);

    fieldShopTitleFont.getData().setScale(fontScale);
    fieldShopBodyFont.getData().setScale(fontScale);
    fieldShopInfoFont.getData().setScale(fontScale);

    Texture icon = getFieldShopPowerupIcon(index);
    drawContain(icon,1368f*scaleX,608f*scaleY,436f*scaleX,226f*scaleY);
    drawFieldShopCenteredText(fieldShopTitleFont,getFieldShopPowerupName(index), 1368f*scaleX,520f*scaleY+10,436f*scaleX,44f*scaleY);
    drawInventoryCenteredText(fieldShopBodyFont,getFieldShopPowerupDescription(index),1368f*scaleX,400f*scaleY+5,436f*scaleX,88f*scaleY,true);
    drawFieldShopCenteredText(fieldShopInfoFont,"DURATION  "+getFieldShopPowerupDuration(index),1368f*scaleX,288f*scaleY+5,436f*scaleX,78f*scaleY);

    int price = getFieldShopPowerupPrice(index);
    int amount = getFieldShopPowerupAmount(index);
    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(price),1368f*scaleX, 198f*scaleY, 288f*scaleX,40f*scaleY);
    drawFieldShopCenteredText(fieldShopInfoFont,"x"+amount,1680f*scaleX,198f*scaleY,124f*scaleX,40f*scaleY);

    fieldShopActionBounds.set(1368f*scaleX,106f*scaleY,436f*scaleX,72f*scaleY);

    drawFieldShopPowerupButton(price,mouseX,mouseY);
}

private void drawFieldShopPowerupButton(int price,float mouseX,float mouseY){
    if(inventory.getGold() < price){
        batch.draw(FxAssets.shopActionNotEnoughGold,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }

    Texture button = FxAssets.shopActionBuy;

    if(fieldShopActionBounds.contains(mouseX,mouseY) &&Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.shopActionBuyPressed;
    else if(fieldShopActionBounds.contains(mouseX,mouseY))
        button = FxAssets.shopActionBuyHover;

    batch.draw(button,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
}

private void addFieldShopPowerup(int index){
    if(index < 4){
        Array<Powerup.PowerupType> powerups = new Array<>();
        powerups.add(INVENTORY_NORMAL_POWERUPS[index]);
        inventory.addPowerupFromArray(powerups);
        return;
    }
    if(index < 10){
        inventory.addSpicy(INVENTORY_SPICY_POWERUPS[index-4],1);
        return;
    }
    if(index < 15){
        inventory.addMoreSpicy(INVENTORY_MORE_SPICY_POWERUPS[index-10],1);
        return;
    }
    if(index == 15)
        inventory.addGodMode(1);
}

private void buySelectedFieldShopPowerup(){
    if(selectedFieldShopCategory != FieldShopCategory.POWERUPS)
        return;
    if(selectedFieldShopProduct < 0)
        return;
    int price = getFieldShopPowerupPrice(selectedFieldShopProduct);
    if(!inventory.spendGold(price))
        return;

    addFieldShopPowerup(selectedFieldShopProduct);
    saveManager.saveProfile(playerProfile);
    AudioAssets.playUiConfirm();
}

private String getFieldShopPowerupName(int index){
    if(index == 0)
        return "HEALTH";
    if(index == 1)
        return "SHIELD";
    if(index == 2)
        return "JETPACK BOOST";
    if(index == 3)
        return "FIRE RATE";
    if(index == 4)
        return "OVERDRIVE";
    if(index == 5)
        return "RAPID MOBILITY";
    if(index == 6)
        return "BOTTOMLESS MAG";
    if(index == 7)
        return "DAMAGE SURGE";
    if(index == 8)
        return "GOLD RUSH";
    if(index == 9)
        return "SCORE FRENZY";
    if(index == 10)
        return "BERSERK";
    if(index == 11)
        return "INFINITE ASSAULT";
    if(index == 12)
        return "HYPERDRIVE";
    if(index == 13)
        return "FORTRESS";
    if(index == 14)
        return "DOUBLE FORTUNE";
    if(index == 15)
        return "GOD MODE";
    return "";
}

private String getFieldShopPowerupDescription(int index){
    if(index == 0)
        return "Restores 80% of maximum health.";
    if(index == 1)
        return "Grants 60 shield HP. Damage hits the shield before health.";
    if(index == 2)
        return "Instantly restores jetpack fuel to maximum.";
    if(index == 3)
        return "Doubles weapon fire rate.";
    if(index == 4)
        return "Increases weapon damage and fire rate by 40%.";
    if(index == 5)
        return "Increases movement speed and jetpack thrust by 50%.";
    if(index == 6)
        return "Weapons fire without consuming magazine ammo.";
    if(index == 7)
        return "Doubles weapon damage.";
    if(index == 8)
        return "Doubles all gold earned while active.";
    if(index == 9)
        return "Doubles all score earned while active.";
    if(index == 10)
        return "2x damage and 1.6x fire rate, but incoming damage increases by 25%.";
    if(index == 11)
        return "Bottomless magazine with 1.4x fire rate.";
    if(index == 12)
        return "1.75x movement speed with unlimited jetpack fuel.";
    if(index == 13)
        return "Grants 100 shield HP and reduces incoming damage by 50%.";
    if(index == 14)
        return "Doubles both gold and score earned.";

    if(index == 15)
        return "Invulnerability, unlimited ammo and jetpack fuel, 2x damage and 2x fire rate.";
    return "";
}

private String getFieldShopPowerupDuration(int index){
    if(index == 0)
        return "INSTANT";
    if(index == 1)
        return "UNTIL DEPLETED";
    if(index == 2)
        return "INSTANT";
    if(index == 3)
        return "5 SEC";
    if(index == 4 || index == 5)
        return "12 SEC";
    if(index == 6 || index == 7)
        return "10 SEC";
    if(index == 8 || index == 9)
        return "15 SEC";
    if(index == 10)
        return "12 SEC";
    if(index == 11)
        return "15 SEC";
    if(index == 12)
        return "12 SEC";
    if(index == 13)
        return "15 SEC";
    if(index == 14)
        return "20 SEC";
    if(index == 15)
        return "8 SEC";
    return "";
}

private int getFieldShopPowerupAmount(int index){
    if(index < 4)
        return inventory.getPowerupAmount(INVENTORY_NORMAL_POWERUPS[index]);
    if(index < 10)
        return inventory.getSpicyAmount(INVENTORY_SPICY_POWERUPS[index-4]);
    if(index < 15)
        return inventory.getMoreSpicyAmount(INVENTORY_MORE_SPICY_POWERUPS[index-10]);
    if(index == 15)
        return inventory.getGodModeAmount();
    return 0;
}

private int getFieldShopPowerupPrice(int index){
    if(index == 0)
        return 100;
    if(index == 1)
        return 140;
    if(index == 2)
        return 100;
    if(index == 3)
        return 160;
    if(index < 10)
        return 300;
    if(index < 15)
        return 600;
    return 1200;
}

private void setFieldShopWideBounds(Rectangle[] bounds){
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;

    float startX = 316f;
    float startY = 604f;

    float cardWidth = 488f;
    float cardHeight = 234f;

    float horizontalStep = 504f;
    float verticalStep = 250f;

    for(int i=0;i<bounds.length;i++){
        int column = i%2;
        int row = i/2;

        float x = startX+column*horizontalStep;
        float y = startY-row*verticalStep;

        bounds[i].set(x*scaleX,y*scaleY,cardWidth*scaleX,cardHeight*scaleY);
    }
}

private void setFieldShopAmmoBounds(){
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;

    float startX = 316f;
    float startY = 478f;

    float cardWidth = 320f;
    float cardHeight = 360f;

    float horizontalStep = 336f;
    float verticalStep = 376f;

    for(int i=0;i<fieldShopAmmoProductBounds.length;i++){
        int column = i%3;
        int row = i/3;
        float x = startX+column*horizontalStep;
        float y = startY-row*verticalStep;
        fieldShopAmmoProductBounds[i].set(x*scaleX,y*scaleY,cardWidth*scaleX,cardHeight*scaleY);
    }
}

private void drawFieldShopWeapons(){
    if(selectedFieldShopCategory != FieldShopCategory.WEAPONS)
        return;
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    for(int i=0;i<fieldShopWeaponProductBounds.length;i++){
        Rectangle bounds = fieldShopWeaponProductBounds[i];
        WeaponType type = FIELD_SHOP_WEAPONS[i];
        Texture gun = FxAssets.weaponRewardIcons.get(type);

        float iconX = bounds.x+20f*scaleX;
        float iconY = bounds.y+108f*scaleY;
        float iconWidth = 448f*scaleX;
        float iconHeight = 112f*scaleY;
        drawContain(gun,iconX,iconY,iconWidth,iconHeight);

        String name = getWeaponName(type);
        float fontScale = Math.min(scaleX,scaleY);
        runSummaryFont.getData().setScale((24f/96f)*fontScale);

        float nameX = bounds.x+20f*scaleX;
        float nameY = bounds.y+70f*scaleY;
        float nameWidth = 448f*scaleX;
        float nameHeight = 28f*scaleY;

        mainMenuLayout.setText(runSummaryFont,name,Color.WHITE,nameWidth,Align.center,false);
        float nameTextY = nameY+(nameHeight+mainMenuLayout.height)/2f;
        runSummaryFont.draw(batch,name,nameX,nameTextY,nameWidth,Align.center,false);

        if(!inventory.ownsWeapon(type))
            batch.draw(FxAssets.shopWeaponLocked,bounds.x,bounds.y,bounds.width,bounds.height);
    }
}

private void drawFieldShopWeaponDetails(float mouseX,float mouseY){
    WeaponType type = getSelectedFieldShopWeapon();

    if(type == null)
        return;

    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    float fontScale = Math.min(scaleX,scaleY);

    fieldShopTitleFont.getData().setScale(fontScale);
    fieldShopBodyFont.getData().setScale(fontScale);
    fieldShopInfoFont.getData().setScale(fontScale);

    Texture gun = FxAssets.weaponRewardIcons.get(type);
    drawContain(gun,1368f*scaleX,608f*scaleY,436f*scaleX,226f*scaleY);


    float nameX = 1368f*scaleX;
    float nameY = 520f*scaleY;
    float nameWidth = 436f*scaleX;
    float nameHeight = 44f*scaleY;
    drawFieldShopCenteredText(fieldShopTitleFont,getWeaponName(type),nameX,nameY+10,nameWidth,nameHeight);

    String description = getWeaponDescription(type);
    drawInventoryCenteredText(inventoryBodyFont,description,1368f*scaleX,400f*scaleY+5,436f*scaleX,88f*scaleY, true);

    float headDamage = weapon.getBaseHeadDamage(type);
    float bodyDamage = weapon.getBaseBodyDamage(type);
    float fireRate = weapon.getBaseFireRate(type);
    int magazine = weapon.getBaseMagazineSize(type);
    float reload = weapon.getBaseReloadSpeed(type);

    String fireRateText = fireRate < 0f ? "--" : Float.toString(fireRate);
    String stats = "HEAD "+(int)headDamage+"   BODY "+(int)bodyDamage+"   FIRE "+fireRateText+
        "\nMAG "+magazine+"   RELOAD "+reload+"s";

    drawInventoryCenteredText(fieldShopInfoFont,stats,1368f*scaleX,288f*scaleY+5,436f*scaleX,78f*scaleY, true);

    int price = getWeaponPrice(type);
    drawInventoryCenteredText(fieldShopInfoFont,Integer.toString(price),1368f*scaleX,198f*scaleY,288f*scaleX,40f*scaleY,false);

    String owned = inventory.ownsWeapon(type) ? "YES" : "NO";
    drawInventoryCenteredText(fieldShopInfoFont,owned,1680f*scaleX,198f*scaleY,124f*scaleX,40f*scaleY,false);
    fieldShopActionBounds.set(1368f*scaleX,106f*scaleY,436f*scaleX,72f*scaleY);

    drawFieldShopWeaponButton(type,price,mouseX,mouseY);
}

private void drawFieldShopWeaponButton(WeaponType type,int price,float mouseX,float mouseY){
    if(inventory.ownsWeapon(type)){
        batch.draw(FxAssets.shopActionOwned,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height );
        return;
    }
    if(inventory.getGold() < price){
        batch.draw(FxAssets.shopActionNotEnoughGold,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }
    Texture button = FxAssets.shopActionBuy;
    if(fieldShopActionBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.shopActionBuyPressed;
    else if(fieldShopActionBounds.contains(mouseX,mouseY))
        button = FxAssets.shopActionBuyHover;

    batch.draw(button, fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
}

private String getWeaponName(WeaponType type){
    if(type == WeaponType.PISTOL)
        return "PISTOL";
    if(type == WeaponType.AR)
        return "ASSAULT RIFLE";
    if(type == WeaponType.SMG)
        return "SMG";
    if(type == WeaponType.SHOTGUN)
        return "SHOTGUN";
    if(type == WeaponType.SNIPER)
        return "SNIPER RIFLE";
    if(type == WeaponType.ROCKET_LAUNCHER)
        return "ROCKET LAUNCHER";
    return "";
}

private WeaponType getSelectedFieldShopWeapon(){
    if(selectedFieldShopCategory != FieldShopCategory.WEAPONS)
        return null;
    if(selectedFieldShopProduct < 0 || selectedFieldShopProduct >= FIELD_SHOP_WEAPONS.length)
        return null;
    return FIELD_SHOP_WEAPONS[selectedFieldShopProduct];
}

private String getWeaponDescription(WeaponType type){
    if(type == WeaponType.PISTOL)
        return "Reliable sidearm with balanced damage and quick handling.";
    if(type == WeaponType.AR)
        return "Versatile automatic rifle built for sustained mid-range combat.";
    if(type == WeaponType.SMG)
        return "High-rate close-range weapon built for aggressive movement.";
    if(type == WeaponType.SHOTGUN)
        return "Heavy close-range burst damage delivered through a wide pellet spread.";
    if(type == WeaponType.SNIPER)
        return "High-impact precision rifle built for long-range headshots.";
    if(type == WeaponType.ROCKET_LAUNCHER)
        return "Explosive launcher dealing massive area damage on impact.";
    return "";
}

private int getWeaponPrice(WeaponType type){
    if(type == WeaponType.PISTOL)
        return 250;
    if(type == WeaponType.AR)
        return 500;
    if(type == WeaponType.SMG)
        return 650;
    if(type == WeaponType.SHOTGUN)
        return 800;
    if(type == WeaponType.SNIPER)
        return 1000;
    if(type == WeaponType.ROCKET_LAUNCHER)
        return 1400;
    return 0;
}

private void buySelectedFieldShopWeapon(){
    WeaponType type = getSelectedFieldShopWeapon();
    if(type == null)
        return;
    if(inventory.ownsWeapon(type))
        return;
    int price = getWeaponPrice(type);
    if(!inventory.spendGold(price))
        return;

    inventory.addWeapon(type);
    int startingAmmo = WEAPON_INITIAL_CAPACITY*weapon.getBaseMagazineSize(type);
    runState.addAmmo(type,startingAmmo);
    saveManager.saveProfile(playerProfile);
    AudioAssets.playUiConfirm();
}

private void drawFieldShopAmmo(){
    if(selectedFieldShopCategory != FieldShopCategory.AMMO)
        return;
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    for(int i=0;i<fieldShopAmmoProductBounds.length;i++){
        Rectangle bounds = fieldShopAmmoProductBounds[i];
        WeaponType type = FIELD_SHOP_AMMO[i];
        Texture icon = FxAssets.shopAmmoIcons.get(type);
        float iconX = bounds.x+62f*scaleX;
        float iconY = bounds.y+140f*scaleY;
        float iconWidth = 196f*scaleX;
        float iconHeight = 196f*scaleY;

        drawContain(icon,iconX,iconY,iconWidth,iconHeight);
        String name = getWeaponName(type);
        fieldShopInfoFont.getData().setScale(Math.min(scaleX,scaleY));

        float nameX = bounds.x+20f*scaleX;
        float nameY = bounds.y+82f*scaleY + 10;
        float nameWidth = 280f*scaleX;
        float nameHeight = 34f*scaleY;
        drawFieldShopCenteredText(fieldShopInfoFont,name,nameX,nameY,nameWidth,nameHeight);

        if(!inventory.ownsWeapon(type))
            batch.draw(FxAssets.shopAmmoUnavailable,bounds.x,bounds.y,bounds.width,bounds.height);
    }
}

private WeaponType getSelectedFieldShopAmmo(){
    if(selectedFieldShopCategory != FieldShopCategory.AMMO)
        return null;
    if(selectedFieldShopProduct < 0 || selectedFieldShopProduct >= FIELD_SHOP_AMMO.length)
        return null;
    return FIELD_SHOP_AMMO[selectedFieldShopProduct];
}

private String getFieldShopAmmoName(WeaponType type){
    return getWeaponName(type)+" AMMO";
}

private int getFieldShopAmmoPrice(WeaponType type){
    if(type == WeaponType.PISTOL)
        return 50;
    if(type == WeaponType.AR)
        return 100;
    if(type == WeaponType.SMG)
        return 100;
    if(type == WeaponType.SHOTGUN)
        return 120;
    if(type == WeaponType.SNIPER)
        return 150;
    if(type == WeaponType.ROCKET_LAUNCHER)
        return 200;
    return 0;
}

private String getFieldShopAmmoDescription(WeaponType type){
    return "Restocks reserve ammunition for the "+getWeaponName(type)+".";
}

private void drawFieldShopAmmoDetails(float mouseX,float mouseY){
    WeaponType type = getSelectedFieldShopAmmo();

    if(type == null)
        return;

    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    float fontScale = Math.min(scaleX,scaleY);

    fieldShopTitleFont.getData().setScale(fontScale);
    fieldShopBodyFont.getData().setScale(fontScale);
    fieldShopInfoFont.getData().setScale(fontScale);

    Texture icon = FxAssets.shopAmmoIcons.get(type);
    drawContain(icon,1368f*scaleX,608f*scaleY,436f*scaleX,226f*scaleY);

    drawFieldShopCenteredText(fieldShopTitleFont,getFieldShopAmmoName(type),1368f*scaleX,520f*scaleY,436f*scaleX,44f*scaleY);

    drawInventoryCenteredText(fieldShopBodyFont,getFieldShopAmmoDescription(type),1368f*scaleX,400f*scaleY,436f*scaleX,88f*scaleY,true);

    int restockAmount = getFieldShopAmmoAmount(type);
    drawFieldShopCenteredText(fieldShopInfoFont,"RESTOCK +"+restockAmount+"   /   3 MAGAZINES",1368f*scaleX,288f*scaleY,436f*scaleX,78f*scaleY);

    int price = getFieldShopAmmoPrice(type);
    int reserve = runState.getAmmo(type);

    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(price),1368f*scaleX,198f*scaleY,288f*scaleX,40f*scaleY);
    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(reserve),1680f*scaleX,198f*scaleY,124f*scaleX,40f*scaleY);

    fieldShopActionBounds.set(1368f*scaleX,106f*scaleY,436f*scaleX,72f*scaleY);

    drawFieldShopAmmoButton(type,price,mouseX,mouseY);
}

private void drawFieldShopAmmoButton(WeaponType type,int price,float mouseX,float mouseY){
    if(!inventory.ownsWeapon(type)){
        batch.draw(FxAssets.shopActionOwned,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }
    if(inventory.getGold() < price){
        batch.draw(FxAssets.shopActionNotEnoughGold,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }

    Texture button = FxAssets.shopActionBuy;

    if(fieldShopActionBounds.contains(mouseX,mouseY) &&Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.shopActionBuyPressed;
    else if(fieldShopActionBounds.contains(mouseX,mouseY))
        button = FxAssets.shopActionBuyHover;

    batch.draw(button,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
}

private void buySelectedFieldShopAmmo(){
    WeaponType type = getSelectedFieldShopAmmo();
    if(type == null)
        return;
    if(!inventory.ownsWeapon(type))
        return;
    int price = getFieldShopAmmoPrice(type);
    if(!inventory.spendGold(price))
        return;
    int ammoAmount = getFieldShopAmmoAmount(type);
    runState.addAmmo(type,ammoAmount);
    saveManager.saveProfile(playerProfile);
    AudioAssets.playUiConfirm();
}


private int getFieldShopAmmoAmount(WeaponType type){
    return weapon.getBaseMagazineSize(type)*3;
}

private String getFieldShopSpecialName(int index){
    if(index == 0)
        return "FIELD KEY";
    if(index == 1)
        return "FULL RESTOCK";
    if(index == 2)
        return "COMBAT CACHE";
    if(index == 3)
        return "ELITE CACHE";
    if(index == 4)
        return "CLASSIFIED";
    if(index == 5)
        return "LOCKED FUTURE";
    return "";
}

private String getFieldShopSpecialDescription(int index){
    if(index == 0)
        return "Issues one field access key for secured mission systems.";
    if(index == 1)
        return "Restocks reserve ammunition for every weapon currently owned.";
    if(index == 2)
        return "Tactical supply package containing useful combat resources.";
    if(index == 3)
        return "Premium requisition package containing high-value combat resources.";
    if(index == 4)
        return "Classified requisition. Access authorization unavailable.";
    if(index == 5)
        return "Restricted requisition slot reserved for future deployment.";
    return "";
}

private int getFieldShopSpecialPrice(int index){
    if(index == 0)
        return 500;
    if(index == 1)
        return 450;
    if(index == 2)
        return 700;
    if(index == 3)
        return 1200;
    return 0;
}

private void drawFieldShopSpecial(){
    if(selectedFieldShopCategory != FieldShopCategory.SPECIAL)
        return;
    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;

    for(int i=0;i<fieldShopSpecialProductBounds.length;i++){
        Rectangle bounds = fieldShopSpecialProductBounds[i];
        Texture icon = FxAssets.shopSpecialIcons[i];

        float iconX = bounds.x+20f*scaleX;
        float iconY = bounds.y+68f*scaleY;
        float iconWidth = 144f*scaleX;
        float iconHeight = 144f*scaleY;

        drawContain(icon,iconX,iconY,iconWidth,iconHeight);

        fieldShopInfoFont.getData().setScale(Math.min(scaleX,scaleY)*0.9f);

        float nameX = bounds.x+180f*scaleX;
        float nameY = bounds.y+82f*scaleY + 5;
        float nameWidth = 288f*scaleX;
        float nameHeight = 48f*scaleY;

        drawFieldShopCenteredText(fieldShopInfoFont,getFieldShopSpecialName(i),nameX,nameY,nameWidth,nameHeight);

        if(i >= 4)
            batch.draw(FxAssets.shopSpecialLocked,bounds.x,bounds.y,bounds.width,bounds.height);
    }
}

private void drawFieldShopSpecialDetails(float mouseX,float mouseY){
    if(selectedFieldShopCategory != FieldShopCategory.SPECIAL)
        return;

    if(selectedFieldShopProduct < 0 || selectedFieldShopProduct >= 6)
        return;

    int index = selectedFieldShopProduct;

    float scaleX = Gdx.graphics.getWidth()/1920f;
    float scaleY = Gdx.graphics.getHeight()/1080f;
    float fontScale = Math.min(scaleX,scaleY);

    fieldShopTitleFont.getData().setScale(fontScale);
    fieldShopBodyFont.getData().setScale(fontScale);
    fieldShopInfoFont.getData().setScale(fontScale);

    Texture icon = FxAssets.shopSpecialIcons[index];

    drawContain(icon,1368f*scaleX,608f*scaleY,436f*scaleX,226f*scaleY);
    drawFieldShopCenteredText(fieldShopTitleFont,getFieldShopSpecialName(index),1368f*scaleX,520f*scaleY+7,436f*scaleX,44f*scaleY);
    drawInventoryCenteredText(fieldShopBodyFont,getFieldShopSpecialDescription(index),1368f*scaleX,400f*scaleY,436f*scaleX,88f*scaleY,true);

    String effect = getFieldShopSpecialEffect(index);
    drawFieldShopCenteredText(fieldShopInfoFont,effect,1368f*scaleX,288f*scaleY,436f*scaleX,78f*scaleY);

    fieldShopActionBounds.set(1368f*scaleX,106f*scaleY,436f*scaleX,72f*scaleY);

    if(index >= 4)
        return;

    int price = getFieldShopSpecialPrice(index);
    drawFieldShopCenteredText(fieldShopInfoFont,Integer.toString(price),1368f*scaleX,198f*scaleY,288f*scaleX,40f*scaleY);

    String owned = index == 0 && runState.hasKey() ? "x1" : "--";
    drawFieldShopCenteredText(fieldShopInfoFont,owned,1680f*scaleX,198f*scaleY,124f*scaleX,40f*scaleY);

    drawFieldShopSpecialButton(index,price,mouseX,mouseY);
}

private String getFieldShopSpecialEffect(int index){
    if(index == 0)
        return "GRANTS 1 FIELD KEY";
    if(index == 1)
        return "FULL AMMO RESTOCK";
    if(index == 2)
        return "TACTICAL RESOURCE PACKAGE";
    if(index == 3)
        return "PREMIUM RESOURCE PACKAGE";
    if(index == 4)
        return "ACCESS DENIED";
    if(index == 5)
        return "NOT YET AVAILABLE";
    return "";
}

private void drawFieldShopSpecialButton(int index,int price,float mouseX,float mouseY){
    if(index >= 4){
        batch.draw(FxAssets.shopSpecialUnavailable,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }
    if(index == 0 && runState.hasKey()){
        batch.draw(FxAssets.shopActionOwned,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }
    if(inventory.getGold() < price){
        batch.draw(FxAssets.shopActionNotEnoughGold,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
        return;
    }

    Texture button = FxAssets.shopActionBuy;

    if(fieldShopActionBounds.contains(mouseX,mouseY) && Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        button = FxAssets.shopActionBuyPressed;
    else if(fieldShopActionBounds.contains(mouseX,mouseY))
        button = FxAssets.shopActionBuyHover;

    batch.draw(button,fieldShopActionBounds.x,fieldShopActionBounds.y,fieldShopActionBounds.width,fieldShopActionBounds.height);
}

private void drawFieldShopCenteredText(BitmapFont font,String text,float x,float y,float width,float height){
    mainMenuLayout.setText(font,text);
    float textX = x+(width-mainMenuLayout.width)/2f;
    float textY = y+(height+mainMenuLayout.height)/2f;
    font.draw(batch,text,textX,textY);
}

private void setArmoryIconBounds(float x,float y,float scale){
    float startX = 328f;
    float startY = 658f;
    float cardWidth = 220f;
    float cardHeight = 166f;
    float horizontalStep = 236f;
    float verticalStep = 184f;

    for(int i=0;i<armoryIconBounds.length;i++){
        int column = i%4;
        int row = i/4;

        float cardX = startX+column*horizontalStep;
        float cardY = startY-row*verticalStep;
        armoryIconBounds[i].set(x+cardX*scale,y+cardY*scale,cardWidth*scale,cardHeight*scale);
    }
}

private boolean mouseOverGameplayHud(float mouseX,float mouseY){
    return scopeButtonBounds.contains(mouseX,mouseY) || reloadButtonBounds.contains(mouseX,mouseY);
}





    
}








        






            


        



        





