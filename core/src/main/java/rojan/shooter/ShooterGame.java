package rojan.shooter;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.PointMapObject;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

//./gradlew lwjgl3:run

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ShooterGame extends ApplicationAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Player player;
    private SpriteBatch batch;
    private Array<Rectangle> solidPlatforms;

    private Weapon weapon;
    private Array<Bullet> bullets;

    private float gravity = -1200.f;
    private float jetpackThrust= 2200.f;

    private Texture bulletTexture;



    @Override
    public void create() {
        map = loadMap();

        mapRenderer = new OrthogonalTiledMapRenderer(map); 
        camera = createCameraFromMap(map);
        mapRenderer.setView(camera);

        player = createPlayerFromMap(map);
        weapon = createWeaponFromMap(map);
        player.receivePlayerMoveBox(extractPlayerMovementColliderFromMap(map));
        player.receivePlayerCrouchMoveBox(extractPlayerCrouchMovementColliderFromMap(map));
        player.receivePlayerGunHoldingOffset(extractPlayerGunHoldingOffsetFromMap(map));

        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        solidPlatforms = extractSolidPlatformsFromMap(map);
        bullets = new Array<>();
        bulletTexture = new Texture(AssetPaths.Weapons.Rifles.AR_BULLET);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        clampCameraToPlayer();
        Vector2 mouseWorldPosition = getMouseWorldPosition();
        Vector2 handPosition = player.getHandPosition();
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
            createBulletWhenFired(handPosition, mouseWorldPosition);
        }

        activateGravity(dt);

        Vector2 direction = new Vector2(0,0);
        if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            direction.x--;

        if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 
            direction.x++;

        if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)){
            if(player.getCrouchingState())
                player.stand(solidPlatforms);
            if(!player.getCrouchingState())
                player.reactToJetpack(jetpackThrust,dt);
        }
        else
             player.stopJetpack();
        
        player.move(direction,dt,getWorldSize(),solidPlatforms);
        player.ensureGrounding(solidPlatforms);

        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            if(!player.getCrouchingState())
                player.crouch();
            else
                player.stand(solidPlatforms);   
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.G)){
            if(player.getCurrentMode() == Player.PlayerMode.UNARMED)
                player.switchPlayerModeArmed();
            else if(player.getCurrentMode() == Player.PlayerMode.ARMED)
                player.switchPlayerModeUnarmed();
        }

        for(Bullet bullet : bullets)
            bullet.update(dt);
        
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        mapRenderer.render();
        batch.begin();
        player.drawPlayer(batch,dt);
        weapon.setHandPosition(player.getHandPosition());
        weapon.setFacingLeft(player.isFacingLeft());

        if(player.getCurrentMode() == Player.PlayerMode.ARMED)
            weapon.drawWeapon(batch);

        for(Bullet bullet : bullets)
            bullet.draw(batch);
        batch.end();
    }
    

    @Override
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
        batch.dispose();
        player.disposePlayer();
        weapon.disposeWeapon();
        bulletTexture.dispose();
    }


    private TiledMap loadMap(){
        TmxMapLoader loader = new TmxMapLoader();
        TiledMap loadedMap = loader.load(AssetPaths.Map.ALIEN_RUINS);
        return loadedMap;
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

    private void createBulletWhenFired(Vector2 handPos, Vector2 mousePos){
       Vector2 nozzleOffset = extractARNozzleOffsetFromMap(map);
       float x = handPos.x + nozzleOffset.x;
       float y = handPos.y + nozzleOffset.y;
       Vector2 nozzlePosition = new Vector2(x,y);
       Vector2 bulletDirection = normalizeBulletDirection(nozzlePosition,mousePos);
       Bullet bullet = new Bullet(nozzlePosition , bulletDirection, 500.f,bulletTexture);
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
        TiledMapTileMapObject forWeaponObject = (TiledMapTileMapObject) playerLayer.getObjects().get("forWeapon");
        RectangleMapObject weaponSocketObject = (RectangleMapObject) playerLayer.getObjects().get("weaponSocket");

        Rectangle weaponSocketRect = weaponSocketObject.getRectangle();

        float offsetX = weaponSocketRect.x - forWeaponObject.getX();
        float offsetY = weaponSocketRect.y - forWeaponObject.getY();

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

    private void activateGravity(float dt){
        player.reactToGravity(gravity, dt);
    }

    



    




}