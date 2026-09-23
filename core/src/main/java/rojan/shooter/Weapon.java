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

    public void drawWeapon(SpriteBatch batch){
        if(ARregion.isFlipX() != facingLeft)
            ARregion.flip(true, false);
        float originY = offset.y;
        float originX;
        if(!facingLeft)
            originX = offset.x;
        else
            originX = width - offset.x;
        Vector2 drawPosition = determineWeaponDrawLocation();
        batch.draw(ARregion, drawPosition.x, drawPosition.y, originX, originY ,width, height,1f,1f,angleOfRotation);
    }

    public void setHandPosition(Vector2 coordinates){
        handPositionEveryFrame = coordinates;
    }

    public void disposeWeapon(){
        ARtexture.dispose();
    }

    private Vector2 determineWeaponDrawLocation(){
        float x;
        if(!facingLeft)
            x = handPositionEveryFrame.x - offset.x;
        else
            x = handPositionEveryFrame.x - (width - offset.x);

       float y = handPositionEveryFrame.y - offset.y;
       return new Vector2(x,y);
    }

    public void setFacingLeft(boolean facingLeft){
        this.facingLeft = facingLeft;
    }

    public Vector2 getHandPosition(){
        return handPositionEveryFrame;
    }

    public void rotateWeapon(Vector2 handPos ,Vector2 mousePos){
        float angleInRadians = (float) Math.atan2(mousePos.y - handPos.y , mousePos.x - handPos.x);
        angleOfRotation = (angleInRadians * 180 / (float)Math.PI);
    }


    public static void main(String[] args){

    }
}
