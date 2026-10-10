package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet{
    private Vector2 position;
    private Vector2 direction;
    private float radius;
    private float speed;
    private float width;
    private float height;
    private float balancingX =0f;
    private float balancingY = 0f;
    private Texture texture;
    boolean considerWasted;

    private Weapon.WeaponType weaponType;
    private float headDamage;
    private float bodyDamage;
    private float limbDamage;
    private Vector2 previousPosition;


    public Bullet(Vector2 position,Vector2 direction,float speed,Texture texture,float radius,Weapon.WeaponType weaponType,float drawSize,float headDamage,float bodyDamage,float limbDamage){
        this.position = new Vector2(position);
        this.previousPosition = new Vector2(position);
        this.direction = new Vector2(direction);
        this.speed = speed;
        this.texture = texture;
        this.radius = radius;
        this.weaponType = weaponType;
        width = drawSize;
        height = drawSize;
        this.headDamage = headDamage;
        this.bodyDamage = bodyDamage;
        this.limbDamage = limbDamage;
        considerWasted = false;
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

    public Weapon.WeaponType getWeaponType(){
        return weaponType;
    }


 
    public void update(float dt) {
        previousPosition.set(position);
        position.x += direction.x * speed * dt;
        position.y += direction.y * speed * dt;
    }

    public Vector2 getPreviousPosition(){
        return previousPosition;
    }


    public void draw(SpriteBatch batch) {
        float drawX = position.x - width / 2f + balancingX;
        float drawY = position.y - height / 2f + balancingY;
        float angle = bulletRotation();
        batch.draw(texture, drawX, drawY, width / 2f, height / 2f, width, height,1f,1f,angle,0,0,texture.getWidth(),texture.getHeight(),false,false);
    }

    public float bulletRotation(){
       float angleInRadians = (float)Math.atan2(direction.y,direction.x);
       float angleInDegrees = angleInRadians * 180f /(float)Math.PI;
       return angleInDegrees;
    }


    public Vector2 getPosition() {
        return position;
    }

    public float getRadius(){
        return radius;
    }

    public void considerWasted(){
        considerWasted = true;
    }

    public boolean isWasted(){
        return considerWasted;
    }

}