package rojan.shooter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class EnemyProjectile{
    private Vector2 position;
    private Vector2 previousPosition;
    private Vector2 direction;
    private float velocity;
    private float damage;
    private Texture texture;
    private boolean considerWasted;
    private float radius;
    private float width;
    private float height;

    EnemyProjectile(Vector2 spawn,Vector2 target,float speed, Texture texture, float damage, float radius, float width, float height){
        this.velocity = speed;
        this.damage = damage;
        this.position = new Vector2(spawn);
        this.previousPosition = new Vector2(spawn);
        considerWasted = false;
        this.direction = new Vector2(target).sub(spawn).nor();
        this.texture = texture;
        this.radius = radius;
        this.width = width;
        this.height = height;
    }

    public void update(float dt) {
        previousPosition.set(position);
        position.x += direction.x * velocity * dt;
        position.y += direction.y * velocity * dt;
    }

    public void draw(SpriteBatch batch) {
        float drawX = position.x - width / 2f;
        float drawY = position.y - height / 2f;
        float angle = projectileRotation();
        batch.draw(texture, drawX, drawY, width / 2f, height / 2f, width, height,1f,1f,angle,0,0,texture.getWidth(),texture.getHeight(),false,false);
    }

    public float projectileRotation(){
       float angleInRadians = (float)Math.atan2(direction.y,direction.x);
       float angleInDegrees = angleInRadians * 180f /(float)Math.PI;
       return angleInDegrees;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
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

    public float getDamage(){
        return damage;
    }











    public static void main(String[] args){

    }

}