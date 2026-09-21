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
        Vector2 drawPosition = determineWeaponDrawLocation();
        batch.draw(ARregion, drawPosition.x, drawPosition.y, width, height);
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





    public static void main(String[] args){

    }
}
