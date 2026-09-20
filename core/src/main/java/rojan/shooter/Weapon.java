package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Weapon{
    private Vector2 position;
    private Vector2 offset;
    private float width;
    private float height;
    private float damage;
    private Vector2 handPositionEveryFrame;
    public enum WeaponType{
        PISTOL,
        AR,
        SNIPER,
        ROCKET_LAUNCHER,
        SHOTGUN,
        SMG
    }



    private WeaponType currentWeapon;
    private Texture ARtexture;
    private Sprite ARsprite;


    Weapon(WeaponType type, float spawnX, float spawnY ,float width,float height,Vector2 offsetCoordinates){
        currentWeapon = type;
        position = new Vector2(spawnX,spawnY);
        float weaponScale = 1.3f;
        this.width = width * weaponScale;
        this.height = height * weaponScale;
        offset = new Vector2(offsetCoordinates).scl(weaponScale);

        ARtexture = new Texture(AssetPaths.Weapons.Rifles.AR);
        ARsprite = new Sprite(ARtexture);
    }

    public void drawWeapon(SpriteBatch batch){
        Vector2 drawPosition = determineWeaponDrawLocation();
        batch.draw(ARtexture, drawPosition.x, drawPosition.y, width, height);
    }

    public void setHandPosition(Vector2 coordinates){
        handPositionEveryFrame = coordinates;
    }

    public void disposeWeapon(){
        ARtexture.dispose();
    }

    private Vector2 determineWeaponDrawLocation(){
       float x = handPositionEveryFrame.x - offset.x;
       float y = handPositionEveryFrame.y - offset.y;
       return new Vector2(x,y);
    }

    public static void main(String[] args){

    }
}
