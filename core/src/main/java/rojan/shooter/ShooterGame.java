package rojan.shooter;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;



/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class ShooterGame extends ApplicationAdapter {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Player player;

    @Override
    public void create() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/world/AlienRuins.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map); 

        MapLayer referenceLayer = map.getLayers().get("reference");
        RectangleMapObject cameraObject = (RectangleMapObject)referenceLayer.getObjects().get("Camera");
        Rectangle cameraRect = cameraObject.getRectangle();
        camera = new OrthographicCamera(cameraRect.width,cameraRect.height);
        camera.position.set(cameraRect.x + cameraRect.width/2 , cameraRect.y + cameraRect.height/2 , 0);
        camera.update();
        mapRenderer.setView(camera);


        MapLayer PlayerLayer = map.getLayers().get("Player");
        RectangleMapObject playerSpawnObject = (RectangleMapObject)PlayerLayer.getObjects().get("PlayerSpawn"); 
        Rectangle playerRect = playerSpawnObject.getRectangle();
        
        player = new Player(playerRect.x,playerRect.y);

    


        Rectangle Player

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        mapRenderer.render();
        
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }
}
