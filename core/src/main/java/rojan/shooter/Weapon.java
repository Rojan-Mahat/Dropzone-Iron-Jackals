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
    private float headDamage;
    private float bodyDamage;
    private float limbDamage;
    private float fireRate;
    public enum WeaponType{
        PISTOL,
        AR,
        SNIPER,
        ROCKET_LAUNCHER,
        SHOTGUN,
        SMG
    }

    private float reloadSpeed;
    private boolean automatic;
    private float maxScope;
    private int magazineSize;

    private int currentAmmo;
    private int reserveAmmo;

    private WeaponType currentWeapon;
    private float angleOfRotation;
    private boolean facingLeft;


    private Texture ARtexture;
    private TextureRegion ARregion;

    private Texture ammoPanel;
    private Texture ammoIcon;
    private Texture noAmmoIcon;


    Weapon(WeaponType type, float spawnX, float spawnY ,float width,float height,Vector2 offsetCoordinates){
        currentWeapon = type;
        position = new Vector2(spawnX,spawnY);
        float weaponScale = 1.3f;
        this.width = width * weaponScale;
        this.height = height * weaponScale;
        offset = new Vector2(offsetCoordinates).scl(weaponScale);
        determineWeaponStats();
        currentAmmo = magazineSize;
        ammoPanel = new Texture(AssetPaths.Hud.AMMO_PANEL);
        ammoIcon = new Texture(AssetPaths.Hud.AMMO_ICON);
        noAmmoIcon = new Texture(AssetPaths.Hud.NO_AMMO_ICON);

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

    private void determineWeaponStats(){
        if(currentWeapon == WeaponType.PISTOL){
            magazineSize = 8;
            reloadSpeed = 2.5f;
            headDamage = 60;
            bodyDamage = 30;
            limbDamage = 20;
            automatic = false;
            maxScope = 2.3f;
            fireRate = 2.5f;

             reserveAmmo = 48;
        }
        if(currentWeapon == WeaponType.AR){
            magazineSize = 30;
            reloadSpeed = 3f;
            headDamage = 50;
            bodyDamage = 30;
            limbDamage = 20;
            automatic = true;
            fireRate = 5;
            maxScope = 4.2f;
            reserveAmmo = 120;

            ARtexture = new Texture(AssetPaths.Weapons.Rifles.AR);
            ARregion = new TextureRegion(ARtexture);
        }

        /*if(currentWeapon == WeaponType.ROCKET_LAUNCHER){
            magazineSize = 8;
            reloadSpeed = 2.5f;
            headDamage = 60;
            bodyDamage = 30;
            limbDamage = 20;
            automatic = false;
            
        }*/
        
        if(currentWeapon == WeaponType.SHOTGUN){
            magazineSize = 6;
            reloadSpeed = 3.5f;
            headDamage = 180;
            bodyDamage = 80;
            limbDamage = 50;
            automatic = false;
            fireRate = 1.5f;
            maxScope = 2;

            reserveAmmo = 35;
        }

        if(currentWeapon == WeaponType.SMG){
            magazineSize = 30;
            reloadSpeed = 3f;
            headDamage = 25;
            bodyDamage = 18;
            limbDamage = 10;
            automatic = true;
            fireRate = 9;
            maxScope = 3;

            reserveAmmo = 150;
        }

        if(currentWeapon == WeaponType.SNIPER){
            magazineSize = 6;
            reloadSpeed = 4f;
            headDamage = 180;
            bodyDamage = 80;
            limbDamage = 50;
            automatic = false;
            maxScope = 8f;
            fireRate = 1;

            reserveAmmo = 24;
        }
    }

    public boolean canShoot(){
        return currentAmmo > 0;
    }

    public void consumeAmmo(){
        if(canShoot())
            currentAmmo--;
    }

    public void reload(){
        int neededAmmo = magazineSize-currentAmmo;
        int ammoToLoad = Math.min(neededAmmo,reserveAmmo);

        currentAmmo += ammoToLoad;
        reserveAmmo -= ammoToLoad;
    }

    public int getCurrentAmmo(){
        return currentAmmo;
    }

    public int getReserveAmmo(){
        return reserveAmmo;
    }

    public int getMagazineSize(){
        return magazineSize;
    }

    public boolean isAutomatic(){
        return automatic;
    }

    public Texture getAmmoPanelTexture(){
        return ammoPanel;
    }

    public Texture getAmmoIconTexture(){
        return ammoIcon;
    }

    public Texture getNoAmmoIconTexture(){
        return noAmmoIcon;
    }








    public static void main(String[] args){

    }
}
