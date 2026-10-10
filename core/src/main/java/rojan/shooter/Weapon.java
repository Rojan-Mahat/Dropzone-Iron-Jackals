package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.ObjectMap;

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

    private final int BASE_MAGAZINE_PISTOL = 9;
    private final float BASE_RELOAD_PISTOL = 2.5f;
    private final float BASE_HEAD_DAMAGE_PISTOL = 120f;
    private final float BASE_BODY_DAMAGE_PISTOL = 28f;
    private final float BASE_LIMB_DAMAGE_PISTOL = 18f;
    private final float BASE_FIRE_RATE_PISTOL = 2.5f;
    private final float BASE_SCOPE_PISTOL = 1.25f;
    private final boolean BASE_AUTOMATIC_PISTOL = false;
    private final float PISTOL_OFFSET_X = 3f;
    private final float PISTOL_OFFSET_Y = -1f;
    private final float PISTOL_VISUAL_SCALE = 1.25f;

    private final int BASE_MAGAZINE_AR = 30;
    private final float BASE_RELOAD_AR = 3f;
    private final float BASE_HEAD_DAMAGE_AR = 50f;
    private final float BASE_BODY_DAMAGE_AR = 30f;
    private final float BASE_LIMB_DAMAGE_AR = 20f;
    private final float BASE_FIRE_RATE_AR = 4.5f;
    private final float BASE_SCOPE_AR = 1.4f;
    private final boolean BASE_AUTOMATIC_AR = true;
    private final float AR_OFFSET_X = 0f;
    private final float AR_OFFSET_Y = 0f;
    private final float AR_VISUAL_SCALE = 1f;

    private final int BASE_MAGAZINE_SHOTGUN = 6;
    private final float BASE_RELOAD_SHOTGUN = 3.5f;
    private final float BASE_HEAD_DAMAGE_SHOTGUN = 180f;
    private final float BASE_BODY_DAMAGE_SHOTGUN = 80f;
    private final float BASE_LIMB_DAMAGE_SHOTGUN = 50f;
    private final float BASE_FIRE_RATE_SHOTGUN = 1.15f;
    private final float BASE_SCOPE_SHOTGUN = 1.12f;
    private final boolean BASE_AUTOMATIC_SHOTGUN = false;
    private final float SHOTGUN_OFFSET_X = 0f;
    private final float SHOTGUN_OFFSET_Y = 0f;
    private final float SHOTGUN_VISUAL_SCALE = 1f;

    private final int BASE_MAGAZINE_SMG = 30;
    private final float BASE_RELOAD_SMG = 3f;
    private final float BASE_HEAD_DAMAGE_SMG = 18f;
    private final float BASE_BODY_DAMAGE_SMG = 12f;
    private final float BASE_LIMB_DAMAGE_SMG = 8f;
    private final float BASE_FIRE_RATE_SMG = 9f;
    private final float BASE_SCOPE_SMG = 1.35f;
    private final boolean BASE_AUTOMATIC_SMG = true;
    private final float SMG_OFFSET_X = 2f;
    private final float SMG_OFFSET_Y = 0f;
    private final float SMG_VISUAL_SCALE = 1.15f;

    private final int BASE_MAGAZINE_SNIPER = 6;
    private final float BASE_RELOAD_SNIPER = 4f;
    private final float BASE_HEAD_DAMAGE_SNIPER = 180f;
    private final float BASE_BODY_DAMAGE_SNIPER = 80f;
    private final float BASE_LIMB_DAMAGE_SNIPER = 50f;
    private final float BASE_FIRE_RATE_SNIPER = 1f;
    private final float BASE_SCOPE_SNIPER = 2.25f;
    private final boolean BASE_AUTOMATIC_SNIPER = false;
    private final float SNIPER_OFFSET_X = 0f;
    private final float SNIPER_OFFSET_Y = 0f;
    private final float SNIPER_VISUAL_SCALE = 1f;

    private final int BASE_MAGAZINE_ROCKET = 8;
    private final float BASE_RELOAD_ROCKET = 2.5f;
    private final float BASE_HEAD_DAMAGE_ROCKET = 60f;
    private final float BASE_BODY_DAMAGE_ROCKET = 30f;
    private final float BASE_LIMB_DAMAGE_ROCKET = 20f;
    private final boolean BASE_AUTOMATIC_ROCKET = false;
    private final float BASE_SCOPE_ROCKET = 1.7f;
    private final float BASE_FIRE_RATE_ROCKET_LAUNCHER = 0.75f;
    private final float ROCKET_OFFSET_X = 0f;
    private final float ROCKET_OFFSET_Y = 0f;
    private final float ROCKET_VISUAL_SCALE = 1f;

    private float reloadSpeed;
    private boolean automatic;
    private float maxScope;
    private int magazineSize;

    private int currentAmmo;

    private WeaponType currentWeapon;
    private float angleOfRotation;
    private boolean facingLeft;


    private ObjectMap<WeaponType,Texture> weaponTextures;
    private ObjectMap<WeaponType,TextureRegion> weaponRegions;

    private Texture ammoPanel;
    private Texture ammoIcon;
    private Texture noAmmoIcon;
    private ObjectIntMap<WeaponType> weaponMagazineAmmo;

    Weapon(WeaponType type, float spawnX, float spawnY ,float width,float height,Vector2 offsetCoordinates){
        currentWeapon = type;
        position = new Vector2(spawnX,spawnY);
        float weaponScale = 1.3f;
        this.width = width * weaponScale;
        this.height = height * weaponScale;
        offset = new Vector2(offsetCoordinates).scl(weaponScale);
        weaponMagazineAmmo = new ObjectIntMap<>();
        loadWeaponTextures();
        determineWeaponStats();
        currentAmmo = magazineSize;
        weaponMagazineAmmo.put(currentWeapon,currentAmmo);

        ammoPanel = new Texture(AssetPaths.Hud.AMMO_PANEL);
        ammoIcon = new Texture(AssetPaths.Hud.AMMO_ICON);
        noAmmoIcon = new Texture(AssetPaths.Hud.NO_AMMO_ICON);

    }

    public void drawWeapon(SpriteBatch batch,Vector2 playerHoldingGunPosition){
    TextureRegion region = weaponRegions.get(currentWeapon);
    float visualScale = getWeaponVisualScale();
    float drawWidth = width*visualScale;
    float drawHeight = height*visualScale;
    float originX = offset.x*visualScale;
    float originY = offset.y*visualScale;
    float visualOffsetX = getWeaponVisualOffsetX();
    float visualOffsetY = getWeaponVisualOffsetY();
    if(facingLeft)
        visualOffsetX *= -1f;

    float drawX = playerHoldingGunPosition.x - originX + visualOffsetX;
    float drawY = playerHoldingGunPosition.y - originY + visualOffsetY;
    float scaleY;
    if(facingLeft)
        scaleY = -1f;
    else
        scaleY = 1f;

    batch.draw(region,drawX,drawY,originX,originY,drawWidth,drawHeight,1f,scaleY,angleOfRotation);
}

    public void disposeWeapon(){
        if(weaponTextures != null){
            for(Texture texture : weaponTextures.values())
                texture.dispose();
            weaponTextures.clear();
            weaponRegions.clear();
        }
        ammoPanel.dispose();
        ammoIcon.dispose();
        noAmmoIcon.dispose();
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

    private void loadWeaponTextures(){
        weaponTextures = new ObjectMap<>();
        weaponRegions = new ObjectMap<>();

        weaponTextures.put(WeaponType.PISTOL,new Texture(AssetPaths.Weapons.PISTOL));
        weaponTextures.put(WeaponType.AR,new Texture(AssetPaths.Weapons.AR));
        weaponTextures.put(WeaponType.SMG,new Texture(AssetPaths.Weapons.SMG));
        weaponTextures.put(WeaponType.SHOTGUN,new Texture(AssetPaths.Weapons.SHOTGUN));
        weaponTextures.put(WeaponType.SNIPER,new Texture(AssetPaths.Weapons.SNIPER));
        weaponTextures.put(WeaponType.ROCKET_LAUNCHER,new Texture(AssetPaths.Weapons.ROCKET_LAUNCHER));


        for(ObjectMap.Entry<WeaponType,Texture> entry :
            weaponTextures.entries()){
            entry.value.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
            weaponRegions.put(entry.key,new TextureRegion(entry.value));
        }
    }

    private void determineWeaponStats(){
        if(currentWeapon == WeaponType.PISTOL){
            magazineSize = BASE_MAGAZINE_PISTOL;
            reloadSpeed = BASE_RELOAD_PISTOL;
            headDamage = BASE_HEAD_DAMAGE_PISTOL;
            bodyDamage = BASE_BODY_DAMAGE_PISTOL;
            limbDamage = BASE_LIMB_DAMAGE_PISTOL;
            automatic = BASE_AUTOMATIC_PISTOL;
            maxScope = BASE_SCOPE_PISTOL;
            fireRate = BASE_FIRE_RATE_PISTOL;
        }

        if(currentWeapon == WeaponType.AR){
            magazineSize = BASE_MAGAZINE_AR;
            reloadSpeed = BASE_RELOAD_AR;
            headDamage = BASE_HEAD_DAMAGE_AR;
            bodyDamage = BASE_BODY_DAMAGE_AR;
            limbDamage = BASE_LIMB_DAMAGE_AR;
            automatic = BASE_AUTOMATIC_AR;
            fireRate = BASE_FIRE_RATE_AR;
            maxScope = BASE_SCOPE_AR;
        }

        if(currentWeapon == WeaponType.SHOTGUN){
            magazineSize = BASE_MAGAZINE_SHOTGUN;
            reloadSpeed = BASE_RELOAD_SHOTGUN;
            headDamage = BASE_HEAD_DAMAGE_SHOTGUN;
            bodyDamage = BASE_BODY_DAMAGE_SHOTGUN;
            limbDamage = BASE_LIMB_DAMAGE_SHOTGUN;
            automatic = BASE_AUTOMATIC_SHOTGUN;
            fireRate = BASE_FIRE_RATE_SHOTGUN;
            maxScope = BASE_SCOPE_SHOTGUN;
        }

        if(currentWeapon == WeaponType.SMG){
            magazineSize = BASE_MAGAZINE_SMG;
            reloadSpeed = BASE_RELOAD_SMG;
            headDamage = BASE_HEAD_DAMAGE_SMG;
            bodyDamage = BASE_BODY_DAMAGE_SMG;
            limbDamage = BASE_LIMB_DAMAGE_SMG;
            automatic = BASE_AUTOMATIC_SMG;
            fireRate = BASE_FIRE_RATE_SMG;
            maxScope = BASE_SCOPE_SMG;
        }

        if(currentWeapon == WeaponType.SNIPER){
            magazineSize = BASE_MAGAZINE_SNIPER;
            reloadSpeed = BASE_RELOAD_SNIPER;
            headDamage = BASE_HEAD_DAMAGE_SNIPER;
            bodyDamage = BASE_BODY_DAMAGE_SNIPER;
            limbDamage = BASE_LIMB_DAMAGE_SNIPER;
            automatic = BASE_AUTOMATIC_SNIPER;
            fireRate = BASE_FIRE_RATE_SNIPER;
            maxScope = BASE_SCOPE_SNIPER;
        }

        if(currentWeapon == WeaponType.ROCKET_LAUNCHER){
            magazineSize = BASE_MAGAZINE_ROCKET;
            reloadSpeed = BASE_RELOAD_ROCKET;
            headDamage = BASE_HEAD_DAMAGE_ROCKET;
            bodyDamage = BASE_BODY_DAMAGE_ROCKET;
            limbDamage = BASE_LIMB_DAMAGE_ROCKET;
            automatic = BASE_AUTOMATIC_ROCKET;
            fireRate = BASE_FIRE_RATE_ROCKET_LAUNCHER;
            maxScope = BASE_SCOPE_ROCKET;
}
    }

    public int getBaseMagazineSize(WeaponType type){
        if(type == WeaponType.PISTOL)
            return BASE_MAGAZINE_PISTOL;
        if(type == WeaponType.AR)
            return BASE_MAGAZINE_AR;
        if(type == WeaponType.SMG)
            return BASE_MAGAZINE_SMG;
        if(type == WeaponType.SHOTGUN)
            return BASE_MAGAZINE_SHOTGUN;
        if(type == WeaponType.SNIPER)
            return BASE_MAGAZINE_SNIPER;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return BASE_MAGAZINE_ROCKET;

        return 0;
    }

    public float getBaseHeadDamage(WeaponType type){
        if(type == WeaponType.PISTOL)
            return BASE_HEAD_DAMAGE_PISTOL;
        if(type == WeaponType.AR)
            return BASE_HEAD_DAMAGE_AR;
        if(type == WeaponType.SMG)
            return BASE_HEAD_DAMAGE_SMG;
        if(type == WeaponType.SHOTGUN)
            return BASE_HEAD_DAMAGE_SHOTGUN;
        if(type == WeaponType.SNIPER)
            return BASE_HEAD_DAMAGE_SNIPER;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return BASE_HEAD_DAMAGE_ROCKET;

        return 0;
    }

    public float getBaseBodyDamage(WeaponType type){
        if(type == WeaponType.PISTOL)
            return BASE_BODY_DAMAGE_PISTOL;
        if(type == WeaponType.AR)
            return BASE_BODY_DAMAGE_AR;
        if(type == WeaponType.SMG)
            return BASE_BODY_DAMAGE_SMG;
        if(type == WeaponType.SHOTGUN)
            return BASE_BODY_DAMAGE_SHOTGUN;
        if(type == WeaponType.SNIPER)
            return BASE_BODY_DAMAGE_SNIPER;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return BASE_BODY_DAMAGE_ROCKET;

        return 0;
    }

    public float getBaseFireRate(WeaponType type){
        if(type == WeaponType.PISTOL)
            return BASE_FIRE_RATE_PISTOL;
        if(type == WeaponType.AR)
            return BASE_FIRE_RATE_AR;
        if(type == WeaponType.SMG)
            return BASE_FIRE_RATE_SMG;
        if(type == WeaponType.SHOTGUN)
            return BASE_FIRE_RATE_SHOTGUN;
        if(type == WeaponType.SNIPER)
            return BASE_FIRE_RATE_SNIPER;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return BASE_FIRE_RATE_ROCKET_LAUNCHER;
        return 0;
    }

    public float getBaseReloadSpeed(WeaponType type){
        if(type == WeaponType.PISTOL)
            return BASE_RELOAD_PISTOL;
        if(type == WeaponType.AR)
            return BASE_RELOAD_AR;
        if(type == WeaponType.SMG)
            return BASE_RELOAD_SMG;
        if(type == WeaponType.SHOTGUN)
            return BASE_RELOAD_SHOTGUN;
        if(type == WeaponType.SNIPER)
            return BASE_RELOAD_SNIPER;
        if(type == WeaponType.ROCKET_LAUNCHER)
            return BASE_RELOAD_ROCKET;

        return 0f;
    }

    

    public WeaponType getCurrentWeapon(){
        return currentWeapon;
    }

    public float getMaxScope(){
        return maxScope;
    }

    public float getHeadDamage(){
        return headDamage;
    }

    public float getBodyDamage(){
        return bodyDamage;
    }

    public float getLimbDamage(){
        return limbDamage;
    }


    public boolean canShoot(){
        return currentAmmo > 0;
    }

    public void consumeAmmo(){
        if(canShoot())
            currentAmmo--;
    }

    public int getCurrentAmmo(){
        return currentAmmo;
    }

    public void addCurrentAmmo(int amount){
        currentAmmo+=amount;
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

    public void setFireRate(float mag){
       fireRate *= mag;
    }
    public void resetFireRate(){
        if(currentWeapon == WeaponType.AR)
            fireRate = BASE_FIRE_RATE_AR;
        if(currentWeapon == WeaponType.SMG)
            fireRate = BASE_FIRE_RATE_SMG;
        if(currentWeapon == WeaponType.PISTOL)
            fireRate = BASE_FIRE_RATE_PISTOL;
        if(currentWeapon == WeaponType.SNIPER)
            fireRate = BASE_FIRE_RATE_SNIPER;
        if(currentWeapon == WeaponType.ROCKET_LAUNCHER)
            fireRate = BASE_FIRE_RATE_ROCKET_LAUNCHER;
        if(currentWeapon == WeaponType.SHOTGUN)
            fireRate = BASE_FIRE_RATE_SHOTGUN;
    }

    public void setDamage(float mag){
        headDamage *= mag;
        bodyDamage *= mag;
        limbDamage *= mag;
    }

    public void resetDamage(){
        if(currentWeapon == WeaponType.AR){
            headDamage = BASE_HEAD_DAMAGE_AR;
            bodyDamage = BASE_BODY_DAMAGE_AR;
            limbDamage = BASE_LIMB_DAMAGE_AR;
        }
        else if(currentWeapon == WeaponType.SMG){
            headDamage = BASE_HEAD_DAMAGE_SMG;
            bodyDamage = BASE_BODY_DAMAGE_SMG;
            limbDamage = BASE_LIMB_DAMAGE_SMG;
        }
        else if(currentWeapon == WeaponType.PISTOL){
            headDamage = BASE_HEAD_DAMAGE_PISTOL;
            bodyDamage = BASE_BODY_DAMAGE_PISTOL;
            limbDamage = BASE_LIMB_DAMAGE_PISTOL;
        }
        else if(currentWeapon == WeaponType.SNIPER){
            headDamage = BASE_HEAD_DAMAGE_SNIPER;
            bodyDamage = BASE_BODY_DAMAGE_SNIPER;
            limbDamage = BASE_LIMB_DAMAGE_SNIPER;
        }
        else if(currentWeapon == WeaponType.ROCKET_LAUNCHER){
            headDamage = BASE_HEAD_DAMAGE_ROCKET;
            bodyDamage = BASE_BODY_DAMAGE_ROCKET;
            limbDamage = BASE_LIMB_DAMAGE_ROCKET;
        }
        else if(currentWeapon == WeaponType.SHOTGUN){
            headDamage = BASE_HEAD_DAMAGE_SHOTGUN;
            bodyDamage = BASE_BODY_DAMAGE_SHOTGUN;
            limbDamage = BASE_LIMB_DAMAGE_SHOTGUN;
        }
    }

    public void equipWeapon(WeaponType type){
        if(type == currentWeapon)
            return;
        weaponMagazineAmmo.put(currentWeapon,currentAmmo);
        currentWeapon = type;
        determineWeaponStats();
        currentAmmo = weaponMagazineAmmo.get(type,magazineSize);
    }


    private float getWeaponVisualScale(){
        if(currentWeapon == WeaponType.PISTOL)
            return PISTOL_VISUAL_SCALE;

        if(currentWeapon == WeaponType.SMG)
            return SMG_VISUAL_SCALE;

        if(currentWeapon == WeaponType.SHOTGUN)
            return SHOTGUN_VISUAL_SCALE;

        if(currentWeapon == WeaponType.SNIPER)
            return SNIPER_VISUAL_SCALE;

        if(currentWeapon == WeaponType.ROCKET_LAUNCHER)
            return ROCKET_VISUAL_SCALE;

        return AR_VISUAL_SCALE;
    }

    private float getWeaponVisualOffsetX(){
        if(currentWeapon == WeaponType.PISTOL)
            return PISTOL_OFFSET_X;

        if(currentWeapon == WeaponType.SMG)
            return SMG_OFFSET_X;

        if(currentWeapon == WeaponType.SHOTGUN)
            return SHOTGUN_OFFSET_X;

        if(currentWeapon == WeaponType.SNIPER)
            return SNIPER_OFFSET_X;

        if(currentWeapon == WeaponType.ROCKET_LAUNCHER)
            return ROCKET_OFFSET_X;

        return AR_OFFSET_X;
    }

    private float getWeaponVisualOffsetY(){
        if(currentWeapon == WeaponType.PISTOL)
            return PISTOL_OFFSET_Y;

        if(currentWeapon == WeaponType.SMG)
            return SMG_OFFSET_Y;

        if(currentWeapon == WeaponType.SHOTGUN)
            return SHOTGUN_OFFSET_Y;

        if(currentWeapon == WeaponType.SNIPER)
            return SNIPER_OFFSET_Y;

        if(currentWeapon == WeaponType.ROCKET_LAUNCHER)
            return ROCKET_OFFSET_Y;

        return AR_OFFSET_Y;
    }

    private Vector2 getBaseMuzzleOffset(){
        if(currentWeapon == WeaponType.PISTOL)
            return new Vector2( 23.89533f,-1.08177f);

        if(currentWeapon == WeaponType.AR)
            return new Vector2(51.53433f,-2.46548f);

        if(currentWeapon == WeaponType.SMG)
            return new Vector2(38.65111f, -2.94446f);

        if(currentWeapon == WeaponType.SHOTGUN)
            return new Vector2(51.98374f,0.62127f);

        if(currentWeapon == WeaponType.SNIPER)
            return new Vector2(54.30572f,-1.61397f);

        if(currentWeapon == WeaponType.ROCKET_LAUNCHER)
            return new Vector2(53.48179f, -1.18821f);
        return new Vector2();
    }

    public Vector2 getMuzzleWorldPosition(Vector2 playerGrip){
        Vector2 muzzleOffset = getBaseMuzzleOffset();
        float visualScale = getWeaponVisualScale();
        float dx = muzzleOffset.x*visualScale;
        float dy = muzzleOffset.y*visualScale;
        if(facingLeft)
            dy *= -1f;

        float visualOffsetX = getWeaponVisualOffsetX();
        float visualOffsetY = getWeaponVisualOffsetY();
        if(facingLeft)
            visualOffsetX *= -1f;

        float pivotX = playerGrip.x+visualOffsetX;
        float pivotY = playerGrip.y+visualOffsetY;
        float cos = MathUtils.cosDeg(angleOfRotation);
        float sin = MathUtils.sinDeg(angleOfRotation);
        float rotatedX = dx*cos-dy*sin;
        float rotatedY = dx*sin+dy*cos;
        
        return new Vector2( pivotX+rotatedX, pivotY+rotatedY);
    }

    public float getReloadSpeed(){
        return reloadSpeed;
    }







    public static void main(String[] args){

    }
}
