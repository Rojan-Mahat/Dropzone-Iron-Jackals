package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Weapon{
    private Vector2 position;
    private Vector2 offset;
    private float width;
    private float height;
    private float damage;
    private float fireRate = 5f;
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
    private TextureRegion ARregion;
    private float angleOfRotation;

    private boolean facingLeft;
    


    Weapon(WeaponType type, float spawnX, float spawnY ,float width,float height,Vector2 offsetCoordinates){
        currentWeapon = type;
        position = new Vector2(spawnX,spawnY);
        float weaponScale = 1.3f;
        this.width = width * weaponScale;
        this.height = height * weaponScale;
        offset = new Vector2(offsetCoordinates).scl(weaponScale);

        ARtexture = new Texture(AssetPaths.Weapons.Rifles.AR);
        ARregion = new TextureRegion(ARtexture);
    }

    public void drawWeapon(SpriteBatch batch,Vector2 playerHoldingGunPosition ){
        float originY = offset.y;
        float originX = offset.x;
        float scaleY;
        if(facingLeft)
            scaleY = -1f;
        else
            scaleY = 1f;
        Vector2 drawPosition = determineWeaponDrawLocation(playerHoldingGunPosition);
        batch.draw(ARregion, drawPosition.x, drawPosition.y, originX, originY ,width, height,1f,scaleY,angleOfRotation);
    }

    public void disposeWeapon(){
        ARtexture.dispose();
    }

    public Vector2 determineWeaponDrawLocation(Vector2 playerHoldingGunPosition){
        float x = playerHoldingGunPosition.x - offset.x;
        float y = playerHoldingGunPosition.y  - offset.y;
        return new Vector2(x,y);
    }

    public void setFacingLeft(boolean facingLeft){
        this.facingLeft = facingLeft;
    }

    public void rotateWeapon(Vector2 handPos ,Vector2 mousePos){
        float angleInRadians = (float) Math.atan2(mousePos.y - handPos.y , mousePos.x - handPos.x);
        angleOfRotation = angleInRadians * 180f / (float)Math.PI;
    }

    public float getFireRate(){
        return fireRate;
    }

    public float getAngleOfRotation(){
        return angleOfRotation;
    }

    public static void main(String[] args){

    }
}
