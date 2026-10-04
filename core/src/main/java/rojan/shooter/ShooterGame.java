package rojan.shooter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import rojan.shooter.Enemy.EnemyType;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.math.Ellipse;

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
    private float jetpackThrust= 2200.f;

    private Texture bulletTexture;

    private float horizontalHoldTime = 0f;
    private final float RUN_THRESHOLD = 0.30f;

    private float mouseHeld = 0f;

    private Array<Enemy> enemies;
    private ShapeRenderer shapeRenderer;

    private float heavyPortalFxTimer = 0f;
    private boolean heavyPortalWasActive = false;
    private OrthographicCamera hudCamera;


    @Override
    public void create(){
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
        

        shapeRenderer = new ShapeRenderer();
        

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        solidPlatforms = extractSolidPlatformsFromMap(map);
        bullets = new Array<>();
        bulletTexture = new Texture(AssetPaths.Bullets.BASE+ "AR_bullet.png");

        FxAssets.loadOnce();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
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

        Vector2 mouseWorldPosition = getMouseWorldPosition();
        boolean shooting = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
        boolean shootOnce = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        float fireInterval = 1f/weapon.getFireRate();
        if(shooting)
            player.setShootingTrue();
        else
            player.setShootingFalse();

        if(shootOnce  && weapon.canShoot()){
            createBulletWhenFired(mouseWorldPosition);
            weapon.consumeAmmo();
            mouseHeld = 0f;
        }
        else if(shooting && weapon.isAutomatic() && weapon.canShoot()){
            mouseHeld+=dt;
            if(mouseHeld >= fireInterval){
                createBulletWhenFired(mouseWorldPosition);
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
            weapon.reload();
        /*if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
            if(player.getCurrentMode() == Player.PlayerMode.UNARMED)
                player.switchPlayerModeArmed();
            else if(player.getCurrentMode() == Player.PlayerMode.ARMED)
                player.switchPlayerModeUnarmed();
        }*/

        for(Bullet bullet : bullets)
            bullet.update(dt);
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
        player.drawPlayer(batch,dt);
        for(Enemy enemy: enemies)
            enemy.drawEnemy(batch, dt, player.getPlayerPosition(),solidPlatforms,player.getWorldHitbox(),player.getVelocity(),getHeavyPortalBounds());  
        weapon.determineWeaponDrawLocation(player.getCurrentGripWorldPosition());
        updatePlayerFacingDirection(mouseWorldPosition); 
        weapon.setFacingLeft(player.isFacingLeft());
        Vector2 grip = player.getCurrentGripWorldPosition();
        weapon.rotateWeapon(grip, mouseWorldPosition);
        weapon.drawWeapon(batch, grip);
        for(Bullet bullet : bullets)
            bullet.draw(batch);
        drawDroneOverlayFx();
        transferDroneLockToHeavy();
        drawHeavyLockFx();
        drawHeavyPortalFx(dt);
        bulletEnemyCollisionChecker();
        communicateDamage();
        removeEnemies();
        batch.end(); 
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        drawPlayerHealthHud();

        batch.end();
        batch.setProjectionMatrix(camera.combined);

        shapeRenderer.setProjectionMatrix(camera.combined);
shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

for(Enemy enemy : enemies){

    Rectangle hurtbox = enemy.getWorldHurtbox();

    shapeRenderer.setColor(1,0,0,1);
    shapeRenderer.rect(
        hurtbox.x,
        hurtbox.y,
        hurtbox.width,
        hurtbox.height
    );

    shapeRenderer.setColor(0,1,0,1);
    for(Rectangle rect : enemy.getHeadshotBoxes()){
        Rectangle world = enemy.getWorldHitRegion(rect);
        shapeRenderer.rect(world.x,world.y,world.width,world.height);
    }

    shapeRenderer.setColor(0,0,1,1);
    for(Rectangle rect : enemy.getBodyshotBoxes()){
        Rectangle world = enemy.getWorldHitRegion(rect);
        shapeRenderer.rect(world.x,world.y,world.width,world.height);
    }

    shapeRenderer.setColor(1,1,0,1);
    for(Rectangle rect : enemy.getLimbshotBoxes()){
        Rectangle world = enemy.getWorldHitRegion(rect);
        shapeRenderer.rect(world.x,world.y,world.width,world.height);
    }
}
shapeRenderer.end();
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
    Enemy heavy = null;
    Enemy drone = null;
    for(Enemy enemy : enemies){
        if(enemy.getType() == EnemyType.HEAVY)
            heavy = enemy;
        else if(enemy.getType() == EnemyType.HOVER_DRONE)
            drone = enemy;
    }
    if(heavy != null && drone != null && drone.isHeavyLocking()){
        if(!heavy.isHeavyLocking() && !heavy.isHeavyRangeAttackActive())
            heavy.setHeavyLock(true);
        drone.setHeavyLock(false);
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

        boolean active = enemy.isHeavyRangeAttackActive();

        if(!active){
            heavyPortalFxTimer = 0f;
            heavyPortalWasActive = false;
            continue;
        }

        if(!heavyPortalWasActive){
            heavyPortalFxTimer = 0f;
            heavyPortalWasActive = true;
        }

        heavyPortalFxTimer += dt;
        Vector2 position = enemy.getHeavyPredictedRainFirePosition();
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
        else{
            float activeTime = heavyPortalFxTimer-openDuration;
            int frameIndex = (int)(activeTime/0.08f) % activeFrames.length;
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
        if(frame!=null)
            batch.draw(frame,x,y,size,size);
    }
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
                            enemy.enemyHurt(20);
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
                            enemy.enemyHurt(40);
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
                                enemy.enemyHurt(20);
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
                                enemy.enemyHurt(10);
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

    private void removeEnemies(){
        for(int i = enemies.size-1 ; i >= 0 ; i--){
            if(enemies.get(i).isReadytoBeRemoved())
                enemies.removeIndex(i);
        }
    }

    private void removeBullets(){
        for(int i = bullets.size-1 ; i >= 0 ; i--){
            if(bullets.get(i).isWasted())
                bullets.removeIndex(i);        
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
        player.takeDamage(damage);
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
        
        batch.draw(FxAssets.playerHealthFrame,x,y,width,height);
         if(ratio > 0)
            batch.draw(FxAssets.playerHealthFill,fillX,fillY,fillWidth,fillHeight);
    }

    /*private void drawJetpackHud(){
        float ratio = player.getJetpackFuel()/player.getMaxJetpackFuel();
        float x = 24f;
        float y = Gdx.graphics.getHeight()-108f;
        float width = 220f;
        float height = 24f;

        float fillPaddingX = 8f;
        float fillPaddingY = 6f;

        float fillX = x + fillPaddingX;
        float fillY = y + fillPaddingY;
        float fillWidth = (width - fillPaddingX*2f) * ratio;
        float fillHeight = height - fillPaddingY*2f;

        batch.draw(FxAssets.jetpackFill,fillX,fillY,fillWidth,fillHeight);
        batch.draw(FxAssets.jetpackFrame,x,y,width,height);
    }*/

    
    





        


    



    




}