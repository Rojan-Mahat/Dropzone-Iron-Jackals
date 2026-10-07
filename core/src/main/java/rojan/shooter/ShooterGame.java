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

    private Texture bulletTexture;

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

    private int riflemanCount = 0;
    private int heavyCount = 0;
    private int drillerCount = 0;
    private int droneCount = 0;

    private int pendingScore= 0;
    private int currentScore = 0;
    private int bestScore = 0;

    private int nextHeadshotReward = 5;
    private int nextBodyshotReward = 10;
    private int nextLimbshotReward = 15;
    private int nextKillReward = 10;
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

    private static float HEADSHOTCOUNT = 0;
    private static float BODYSHOTCOUNT = 0;
    private static float LIMBSHOTCOUNT = 0;
    private static int TOTAL_KILL_COUNT =0;

    private float difficulty = 0;
    private boolean easyMode = true;
    private boolean mediumMode = false;
    private boolean hardMode = false;
    private boolean impossibleMode;

    private Array<Rectangle> groundEnemySpawnPoints;
    private Array<Rectangle> drillerSpawnPoints;
    private Array<LootCrate> lootCrates;
    private Inventory inventory;
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
    

    @Override
    public void create(){
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

        FreeTypeFontGenerator inventoryGenerator =new FreeTypeFontGenerator(Gdx.files.internal("fonts/Oxanium-SemiBold.ttf"));
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

        inventoryGenerator.dispose();
        inventoryGlyphLayout = new GlyphLayout();

        map = loadMap();

        mapRenderer = new OrthogonalTiledMapRenderer(map); 
        camera = createCameraFromMap(map);
        mapRenderer.setView(camera);

        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        hudCamera.position.set(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f,0);
        hudCamera.update();

        terrain = getTerrainObjectArray(map);
        terrain.sort((a,b)->Float.compare(b.getY(),a.getY()));

        player = createPlayerFromMap(map);
        player.receiveJetPackDrawData(extractJetpackFromMap(map));
        weapon = createWeaponFromMap(map);
        player.receivePlayerMoveBox(extractPlayerMovementColliderFromMap(map));
        player.receivePlayerCrouchMoveBox(extractPlayerCrouchMovementColliderFromMap(map));
        player.receivePlayerGunHoldingOffset(extractPlayerGunHoldingOffsetFromMap(map));

        Enemy.loadEnemyAssetOnce();
        extractAllEnemyHitboxes();
        enemies = new Array<>();
        Enemy rifleman = create_Jackal_RifleMan_FromMap(map);
        Enemy heavy = create_Jackal_Heavy_FromMap(map);
        Enemy hoverDrone = create_Jackal_HoverDrone_FromMap(map);
        Enemy drillCrawler = create_Jackal_DrillCrawler_FromMap(map);
        enemies.add(rifleman);
        enemies.add(heavy);
        enemies.add(hoverDrone);
        enemies.add(drillCrawler);

        groundEnemySpawnPoints = extractGroundEnemySpawnPoints();
        drillerSpawnPoints = extractDrillerSpawnPoints();
        

        shapeRenderer = new ShapeRenderer();
        

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        solidPlatforms = extractSolidPlatformsFromMap(map);
        bullets = new Array<>();
        bulletTexture = new Texture(AssetPaths.Bullets.BASE+ "AR_bullet.png");
        riflemanProjectileTexture = new Texture(AssetPaths.EnemyProjectile.RIFLEMAN_PROJECTILE);
        heavyProjectileTexture = new Texture(AssetPaths.EnemyProjectile.HEAVY_PROJECTILE);

        inventory = new Inventory();
        int roll = MathUtils.random(1,1);
        if(roll == 1){
            inventory.addWeapon(weapon.getCurrentWeapon());
            inventory.addAmmo(weapon.getCurrentWeapon(),WEAPON_INITIAL_CAPACITY* weapon.getMagazineSize());
        }
        lootCrates = new Array<>();
        FxAssets.loadOnce();
        LootCrate.loadAssetsOnce();
        specialCrateSpawnPoints = extractSpecialCrateSpawnPoints();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight()-Gdx.input.getY();
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            if(inventoryOpen || inventoryAnimation > 0f)
                inventoryOpen = false;
        }

        if(Gdx.input.justTouched()){
            if(inventoryOpen && inventoryAnimation >= 0.99f && inventoryCloseBounds.contains(mouseX,mouseY))
                inventoryOpen = false;
            else if(inventoryBagBounds.contains(mouseX,mouseY))
                inventoryOpen = !inventoryOpen;
            else if(inventoryOpen && inventoryAnimation >= 1f){
                if(inventoryActionBounds.contains(mouseX,mouseY))
                    useInventoryAction();
                else{
                    checkInventoryWeaponSelection(mouseX,mouseY);
                    checkInventoryConsumableSelection(mouseX,mouseY);
                }
            } 
        }
        updateInventoryAnimation(dt);
        boolean gamePaused = inventoryBlockingGameplay();
        float worldDt = gamePaused ? 0f : dt;
        Vector2 mouseWorldPosition = getMouseWorldPosition();
        if(!gamePaused){
            updatePowerups(dt);
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

            player.move(direction,dt,getWorldSize(),solidPlatforms);
            player.ensureGrounding(solidPlatforms);

            boolean shooting = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            boolean shootOnce = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
            float fireInterval = 1f/weapon.getFireRate();
            if(shooting)
                player.setShootingTrue();
            else
                player.setShootingFalse();

            if(shootOnce  && (weapon.canShoot() || infiniteMagazineActive())){
                createBulletWhenFired(mouseWorldPosition);
                if(!infiniteMagazineActive())
                    weapon.consumeAmmo();
                mouseHeld = 0f;
            }
            else if(shooting && weapon.isAutomatic() && (weapon.canShoot() || infiniteMagazineActive())){
                mouseHeld+=dt;
                if(mouseHeld >= fireInterval){
                    createBulletWhenFired(mouseWorldPosition);
                    if(!infiniteMagazineActive())
                        weapon.consumeAmmo();
                    mouseHeld-=fireInterval;
                }
            }
            else
                mouseHeld = 0f;
            bulletPlatformCollision();
            if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
                if(!player.getCrouchingState())
                    player.crouch();
                else
                    player.stand(solidPlatforms);   
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.R))
                reloadGun();

            if(Gdx.input.justTouched()){
                float mouseXa = Gdx.input.getX();
                float mouseYa = Gdx.graphics.getHeight()-Gdx.input.getY();

                if(reloadButtonBounds.contains(mouseXa,mouseYa))
                    reloadGun();
            }

            if(Gdx.input.isKeyJustPressed(Input.Keys.E))
                openNearbyCrate();

            if(Gdx.input.isKeyJustPressed(Input.Keys.Q))
                claimActiveCrate();

            for(Bullet bullet : bullets)
               bullet.update(dt);
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
        if(!gamePaused){
            updatePlayerFacingDirection(mouseWorldPosition); 
            weapon.setFacingLeft(player.isFacingLeft());
            weapon.rotateWeapon(player.getCurrentGripWorldPosition(), mouseWorldPosition);
        }
        Vector2 grip = player.getCurrentGripWorldPosition();
        weapon.drawWeapon(batch, grip);
        drawPlayerPoisonFx(worldDt);
        drawPlayerStunFx(worldDt);

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
        drawInventoryBag();
        drawInventory();

        batch.end();
        batch.setProjectionMatrix(camera.combined);
    }  
    

    @Override
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
        batch.dispose();
        player.disposePlayer();
        weapon.disposeWeapon();
        bulletTexture.dispose();
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
        Weapon newWeapon = new Weapon(Weapon.WeaponType.AR, AR_GunRect.x, AR_GunRect.y , AR_GunRect.width, AR_GunRect.height,extractARGripOffsetFromMap(map) );
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
        Vector2 nozzleOffset = extractARNozzleOffsetFromMap(map);
        Vector2 gunGrip = player.getCurrentGripWorldPosition();
        float angle = weapon.getAngleOfRotation();
        float cos = MathUtils.cosDeg(angle);
        float sin = MathUtils.sinDeg(angle);
        float rotatedX = nozzleOffset.x * cos - nozzleOffset.y * sin;
        float rotatedY = nozzleOffset.x * sin + nozzleOffset.y * cos;

        float x = gunGrip.x + rotatedX;
        float y = gunGrip.y + rotatedY;
        Vector2 nozzlePosition = new Vector2(x,y);
        Vector2 bulletDirection = normalizeBulletDirection(nozzlePosition,mousePos);
        Bullet bullet = new Bullet(nozzlePosition , bulletDirection, 2500.f,bulletTexture, 1.5f);
        bullets.add(bullet);
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

        float deadZoneHalfWidth = 0.33f * camera.viewportWidth/2;
        float deadZoneHalfHeight = 0.33f * camera.viewportHeight/2;

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
        
        float minCameraX = camera.viewportWidth / 2;
        float maxCameraX = worldSize.x - camera.viewportWidth / 2;

        float minCameraY = camera.viewportHeight / 2;
        float maxCameraY = worldSize.y - camera.viewportHeight / 2;

        if(cameraX < minCameraX)
            cameraX = minCameraX;
        
        else if(cameraX > maxCameraX)
            cameraX = maxCameraX;
        
        if(cameraY < minCameraY)
            cameraY = minCameraY;
        
        else if(cameraY > maxCameraY)
            cameraY = maxCameraY;
    
        camera.position.set(cameraX,cameraY,0);
        camera.update();
        mapRenderer.setView(camera);
        batch.setProjectionMatrix(camera.combined);
    }

    private void bulletPlatformCollision(){
        for(Bullet bullet : bullets){
            float x = bullet.getPosition().x;
            float y = bullet.getPosition().y;
            float r = bullet.getRadius();
            Circle bulletZone = new Circle(x,y,r);
            for(Rectangle solid : solidPlatforms){
                if(Intersector.overlaps(bulletZone,solid)){
                    bullet.considerWasted();
                    removeBullets();
                    break;
                }
            }
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
            for(Enemy enemy: enemies){
                boolean chanceOfCollision = false;
                boolean headshot = false;
                boolean bodyshot = false;
                boolean limbshot = false;
                Rectangle hurtbox = enemy.getWorldHurtbox();
                if(enemy.getType() == Enemy.EnemyType.DRILL_CRAWLER && !bullet.isWasted()){
                    Circle bulletCircle = new Circle(bullet.getPosition().x,bullet.getPosition().y,bullet.getRadius());
                    for(Circle circle : enemy.getWorldDrillCircles()){
                        if(Intersector.overlaps(bulletCircle,circle)){
                            enemy.enemyHurt(weapon.getLimbDamage()+ weapon.getBodyDamage()/2);
                            bullet.considerWasted();
                            break;
                        }
                    }
                    continue;
                }
                if((bullet.getPosition().y  - bullet.getRadius() < hurtbox.y + hurtbox.height)
                && (bullet.getPosition().y  + bullet.getRadius() > hurtbox.y)
                &&(bullet.getPosition().x  + bullet.getRadius() > hurtbox.x)
                &&(bullet.getPosition().x  - bullet.getRadius() <  hurtbox.x + hurtbox.width)){
                    chanceOfCollision = true;
                }

                if(chanceOfCollision && !bullet.isWasted()){
                    for(Rectangle rect : enemy.getHeadshotBoxes()){
                        Rectangle worldRect = enemy.getWorldHitRegion(rect);
                        float rectX = worldRect.x;
                        float rectY = worldRect.y;
                       
                        float closestX = MathUtils.clamp(bullet.getPosition().x, rectX, rectX + rect.width);
                        float closestY = MathUtils.clamp(bullet.getPosition().y, rectY, rectY + rect.height);
                        float dx = bullet.getPosition().x - closestX;
                        float dy = bullet.getPosition().y - closestY;
                        if(dx * dx + dy * dy <= bullet.getRadius() * bullet.getRadius()){
                            headshot = true;
                            HEADSHOTCOUNT++;
                            enemy.enemyHurt(weapon.getHeadDamage());
                            bullet.considerWasted();
                            break;
                        }
                        if(bullet.isWasted())
                            break;
                    }

                    if(!headshot && !bullet.isWasted()){
                        for(Rectangle rect : enemy.getBodyshotBoxes()){
                            Rectangle worldRect = enemy.getWorldHitRegion(rect);
                            float rectX = worldRect.x;
                            float rectY = worldRect.y;
                            
                            float closestX = MathUtils.clamp(bullet.getPosition().x, rectX, rectX + rect.width);
                            float closestY = MathUtils.clamp(bullet.getPosition().y, rectY, rectY + rect.height);
                            float dx = bullet.getPosition().x - closestX;
                            float dy = bullet.getPosition().y - closestY;
                            if(dx * dx + dy * dy <= bullet.getRadius() * bullet.getRadius()){
                                bodyshot = true;
                                BODYSHOTCOUNT++;
                                enemy.enemyHurt(weapon.getBodyDamage());
                                bullet.considerWasted();
                                break;
                            }
                        }
                        if(bullet.isWasted())
                            break;
                    }

                    if(!headshot && !bodyshot && !bullet.isWasted()){
                        for(Rectangle rect : enemy.getLimbshotBoxes()){
                            Rectangle worldRect = enemy.getWorldHitRegion(rect);
                            float rectX = worldRect.x;
                            float rectY = worldRect.y;

                            float closestX = MathUtils.clamp(bullet.getPosition().x, rectX, rectX + rect.width);
                            float closestY = MathUtils.clamp(bullet.getPosition().y, rectY, rectY + rect.height);
                            float dx = bullet.getPosition().x - closestX;
                            float dy = bullet.getPosition().y - closestY;
                            if(dx * dx + dy * dy <= bullet.getRadius() * bullet.getRadius()){
                                limbshot = true;
                                LIMBSHOTCOUNT++;
                                enemy.enemyHurt(weapon.getLimbDamage());
                                bullet.considerWasted();
                                break;
                            }
                        }
                        if(bullet.isWasted())
                            break;
                    }
                }
            }
        }
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
                    pendingScore+=10;
                }
                if(enemies.get(i).getType()== EnemyType.HEAVY){
                    heavyCount--;
                    pendingScore+=25;
                }
                if(enemies.get(i).getType()== EnemyType.DRILL_CRAWLER){
                    drillerCount--;
                    pendingScore+=10;
                }
                if(enemies.get(i).getType()== EnemyType.HOVER_DRONE){
                    droneCount--;
                    pendingScore+=5;
                }
                TOTAL_KILL_COUNT++;
                spawnLootCrate(enemies.get(i).getEnemyDeathLocation());
                enemies.removeIndex(i);
            } 
        }
    }

    private void updateGameTracks(){
        if(TOTAL_KILL_COUNT < 20){
            easyMode = true;
            mediumMode = false;
            hardMode = false;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 12f;
            DRONE_RESPAWN_TIME = 17f;
            DRILLER_RESPAWN_TIME = 22f;
            HEAVY_RESPAWN_TIME = 28f;
            MAX_ACTIVE_ENEMIES = 6;

            if(HEADSHOTCOUNT >= nextHeadshotReward){
                pendingScore+=40;
                inventory.addGold(modifyGoldReward(5));
                nextHeadshotReward+=5;
            }

            if(BODYSHOTCOUNT >= nextBodyshotReward){
                pendingScore+=25;
                inventory.addGold(modifyGoldReward(3));
                nextBodyshotReward+=10;
            }

            if(LIMBSHOTCOUNT >= nextLimbshotReward){
                pendingScore+=15;
                inventory.addGold(modifyGoldReward(2));
                nextLimbshotReward+=15;
            }

            if(TOTAL_KILL_COUNT >= nextKillReward){
                pendingScore+=100;
                inventory.addGold(modifyGoldReward(10));
                nextKillReward+=10;
            }
        }

        else if(TOTAL_KILL_COUNT < 40){
            easyMode = false;
            mediumMode = true;
            hardMode = false;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 9f;
            DRONE_RESPAWN_TIME = 12f;
            DRILLER_RESPAWN_TIME = 15f;
            HEAVY_RESPAWN_TIME = 20f;
            MAX_ACTIVE_ENEMIES = 8;

            if(HEADSHOTCOUNT >= nextHeadshotReward){
                pendingScore+=50;
                inventory.addGold(modifyGoldReward(7));
                nextHeadshotReward+=5;
            }

            if(BODYSHOTCOUNT >= nextBodyshotReward){
                pendingScore+=30;
                inventory.addGold(modifyGoldReward(4));
                nextBodyshotReward+=10;
            }

            if(LIMBSHOTCOUNT >= nextLimbshotReward){
                pendingScore+=20;
                inventory.addGold(modifyGoldReward(3));
                nextLimbshotReward+=15;
            }

            if(TOTAL_KILL_COUNT >= nextKillReward){
                pendingScore+=140;
                inventory.addGold(modifyGoldReward(15));
                nextKillReward+=10;
            }
        }

        else if(TOTAL_KILL_COUNT < 70){
            easyMode = false;
            mediumMode = false;
            hardMode = true;
            impossibleMode = false;

            RIFLEMAN_RESPAWN_TIME = 6f;
            DRONE_RESPAWN_TIME = 8f;
            DRILLER_RESPAWN_TIME = 10f;
            HEAVY_RESPAWN_TIME = 14f;
            MAX_ACTIVE_ENEMIES = 10;

            if(HEADSHOTCOUNT >= nextHeadshotReward){
                pendingScore+=70;
                inventory.addGold(modifyGoldReward(10));
                nextHeadshotReward+=5;
            }

            if(BODYSHOTCOUNT >= nextBodyshotReward){
                pendingScore+=45;
                inventory.addGold(modifyGoldReward(6));
                nextBodyshotReward+=10;
            }

            if(LIMBSHOTCOUNT >= nextLimbshotReward){
                pendingScore+=30;
                inventory.addGold(modifyGoldReward(4));
                nextLimbshotReward+=15;
            }

            if(TOTAL_KILL_COUNT >= nextKillReward){
                pendingScore+=200;
                inventory.addGold(modifyGoldReward(20));
                nextKillReward+=10;
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

            if(HEADSHOTCOUNT >= nextHeadshotReward){
                pendingScore+=100;
                inventory.addGold(modifyGoldReward(15));
                nextHeadshotReward+=5;
            }

            if(BODYSHOTCOUNT >= nextBodyshotReward){
                pendingScore+=60;
                inventory.addGold(modifyGoldReward(8));
                nextBodyshotReward+=10;
            }

            if(LIMBSHOTCOUNT >= nextLimbshotReward){
                pendingScore+=40;
                inventory.addGold(modifyGoldReward(15));
                nextLimbshotReward+=15;
            }

            if(TOTAL_KILL_COUNT >= nextKillReward){
                pendingScore+=300;
                inventory.addGold(modifyGoldReward(30));
                nextKillReward+=10;
            }
        }

        currentScore += modifyScoreReward(pendingScore);
        if(currentScore > bestScore)
            bestScore = currentScore;
        pendingScore = 0;
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
                enemyProjectiles.add(new EnemyProjectile(spawn,target,1350f,heavyProjectileTexture,enemy.getHeavyCloseDamage(),40f,300,350));
                enemy.consumeHeavyShot();
            }
        }

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
        float width = 230f;
        float scale = width/256f;
        float height = 80f*scale;

        float x = Gdx.graphics.getWidth()-width-24f;
        float y = 24f;

        float iconX = x+18f*scale;
        float iconY = y+16f*scale;
        float iconSize = 48f*scale;

        batch.draw(weapon.getAmmoPanelTexture(),x,y,width,height);
        if(weapon.getCurrentAmmo()>5)
            batch.draw(weapon.getAmmoIconTexture(),iconX,iconY,iconSize, iconSize);
        else
            batch.draw(weapon.getNoAmmoIconTexture(),iconX,iconY,iconSize, iconSize);
        String magazine = weapon.getCurrentAmmo()+" / "+weapon.getMagazineSize();

        String reserve ="RES "+inventory.getAmmo(weapon.getCurrentWeapon());

        float reloadSize = 65f;
        float reloadX = x+width-reloadSize;
        float reloadY = y+height+2f;
        batch.draw(FxAssets.reloadIcon,reloadX,reloadY,reloadSize,reloadSize);
        reloadButtonBounds.set(reloadX,reloadY,reloadSize,reloadSize);

        hudFont.getData().setScale(1.15f);
        hudFont.draw(batch,magazine,x+90f*scale,y+55f*scale);
        hudFont.getData().setScale(0.75f);
        hudFont.draw(batch,reserve,x+90f*scale,y+30f*scale);
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
        float width = 360f;
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
;

        batch.draw(FxAssets.goldIcon,goldX,zoneY,iconSize,iconSize);
        batch.draw(FxAssets.scoreIcon,scoreX,zoneY,iconSize,iconSize);
        batch.draw(FxAssets.bestScoreIcon,bestX,zoneY,iconSize,iconSize);

        hudFont.getData().setScale(0.8f);

        hudFont.draw(batch,Integer.toString(inventory.getGold()),goldX+48f*scale,zoneY+27f*scale);
        hudFont.draw(batch,Integer.toString(currentScore),scoreX+48f*scale,zoneY+27f*scale);
        hudFont.draw(batch,Integer.toString(bestScore),bestX+48f*scale,zoneY+27f*scale);
    }


    private void reloadGun(){
        int neededAmmo = weapon.getMagazineSize()-weapon.getCurrentAmmo();
        int ammoToLoad = inventory.takeAmmo(weapon.getCurrentWeapon(),neededAmmo);
        weapon.addCurrentAmmo(ammoToLoad);
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
        if(roll == 1)
            lootCrates.add(new LootCrate(CrateType.SUPER_RARE,crateRect));
        else if(roll == 2)
            lootCrates.add(new LootCrate(CrateType.RARE,crateRect));
        else
            lootCrates.add(new LootCrate(CrateType.COMMON,crateRect));
        
    /*if(easyMode){
        if(roll <= 3){
            lootCrates.add(new LootCrate(CrateType.SUPER_RARE,crateRect));
        }
        else if(roll <= 12){
            lootCrates.add(new LootCrate(CrateType.RARE,crateRect));
        }
        else if(roll <= 40){
            lootCrates.add(new LootCrate(CrateType.COMMON,crateRect));
        }
    }

    else if(mediumMode){
        if(roll <= 5){
            lootCrates.add(new LootCrate(CrateType.SUPER_RARE,crateRect));
        }
        else if(roll <= 18){
            lootCrates.add(new LootCrate(CrateType.RARE,crateRect));
        }
        else if(roll <= 45)
            lootCrates.add(new LootCrate(CrateType.COMMON,crateRect));
    }

    else if(hardMode){
        if(roll <= 8){
            lootCrates.add(new LootCrate(CrateType.SUPER_RARE,crateRect));
        }
        else if(roll <= 28){
            lootCrates.add(new LootCrate(CrateType.RARE,crateRect));
        }
        else if(roll <=50){
            lootCrates.add(new LootCrate(CrateType.COMMON,crateRect));
        }
    }

    else if(impossibleMode){
        if(roll <= 15){
            lootCrates.add(new LootCrate(CrateType.SUPER_RARE,crateRect));
        }
        else if(roll <= 38){
            lootCrates.add(new LootCrate(CrateType.RARE,crateRect));
        }
        else if(roll <= 60){
            lootCrates.add(new LootCrate(CrateType.COMMON,crateRect));
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
    lootCrates.add(new LootCrate(CrateType.SPECIAL,crateRect));
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
    currentScore+= modifyScoreReward(loot.getScore());
    if(currentScore > bestScore)
        bestScore = currentScore;
    inventory.addGold(modifyGoldReward(loot.getGold()));
    inventory.addAmmo(weapon.getCurrentWeapon(), (loot.getAmmoReward()* WEAPON_INITIAL_CAPACITY*weapon.getMagazineSize())/100);
    inventory.addPowerupFromArray(loot.getPowerups());
    inventory.addWeaponFromArray(loot.getWeapons(),WEAPON_INITIAL_CAPACITY*weapon.getMagazineSize());
    if(loot.hasJackpot()){
        int goldCount = MathUtils.random(200,350);
        inventory.addGold(modifyGoldReward(goldCount));
    }
    if(loot.hasBiggerJackpot()){
        int goldCount = MathUtils.random(600,800);
        inventory.addGold(modifyGoldReward(goldCount));
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
        return;
    }

    if(type == Powerup.PowerupType.JETPACK){
        player.setJetpackFuel(player.getMaxJetpackFuel());
        return;
    }

    if(type == Powerup.PowerupType.SHIELD){
        if(FORTRESS_ACTIVE)
            return;
        SHIELD_ACTIVE = true;
        SHIELD_AMOUNT = SHIELD_CAPACITY;
        return;
    }

    if(type == Powerup.PowerupType.FIRE_RATE){
        if(FIRE_RATE_ACTIVE || OVERDRIVE_ACTIVE || BERSERK_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE)
            return;

        FIRE_RATE_ACTIVE = true;
        FIRE_RATE_TIMER = 0f;
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
        return;
    }

    if(type == Powerup.SpicyType.RAPID_MOBILITY){
        if(RAPID_MOBILITY_ACTIVE || HYPERDRIVE_ACTIVE)
            return;

        RAPID_MOBILITY_ACTIVE = true;
        RAPID_MOBILITY_TIMER = 0f;
        player.setMovementSpeed(RAPID_MOBILITY_SPEED_MULTIPLIER);
        jetpackThrust = BASE_JETPACK_THRUST * RAPID_MOBILITY_JETPACK_MULTIPLIER;
        return;
    }

    if(type == Powerup.SpicyType.BOTTOMLESS_MAG){
        if(BOTTOMLESS_MAG_ACTIVE || INFINITE_ASSAULT_ACTIVE || GOD_MODE_ACTIVE)
            return;
        BOTTOMLESS_MAG_ACTIVE = true;
        BOTTOMLESS_MAG_TIMER = 0f;
        return;
    }

    if(type == Powerup.SpicyType.DAMAGE_SURGE){
        if(DAMAGE_SURGE_ACTIVE || OVERDRIVE_ACTIVE || BERSERK_ACTIVE || GOD_MODE_ACTIVE)
            return;
        DAMAGE_SURGE_ACTIVE = true;
        DAMAGE_SURGE_TIMER = 0f;
        refreshWeaponPowerupStats();
        return;
    }

    if(type == Powerup.SpicyType.GOLD_RUSH){
        if(GOLD_RUSH_ACTIVE || DOUBLE_FORTUNE_ACTIVE)
            return;
        GOLD_RUSH_ACTIVE = true;
        GOLD_RUSH_TIMER = 0f;
        return;
    }

    if(type == Powerup.SpicyType.SCORE_FRENZY){
        if(SCORE_FRENZY_ACTIVE || DOUBLE_FORTUNE_ACTIVE)
            return;
        SCORE_FRENZY_ACTIVE = true;
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
        return;
    }

    if(type == Powerup.MoreSpicyType.FORTRESS){
        if(FORTRESS_ACTIVE)
            return;

        FORTRESS_ACTIVE = true;
        FORTRESS_TIMER = 0f;

        SHIELD_ACTIVE = true;
        SHIELD_AMOUNT = Math.max(SHIELD_AMOUNT,FORTRESS_SHIELD);
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
            SHIELD_AMOUNT = 0f;
            SHIELD_ACTIVE = false;
        }
    }
    if(finalDamage > 0f)
        player.takeDamage(finalDamage);
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
    int reserveAmmo = inventory.getAmmo(selectedWeapon);

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

    if(selectedWeapon == WeaponType.ROCKET_LAUNCHER)
        return;

    if(selectedWeapon == weapon.getCurrentWeapon())
        return;

    weapon.equipWeapon(selectedWeapon);
    refreshWeaponPowerupStats();
}




 





}

        
        





            


        



        




