package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;


public class Player{
    private Vector2 position;
    private float width;
    private float height;
    private Texture texture;
    private Sprite sprite;
    private Vector2 velocity;
    private float moveSpeed;
    
    
   

    Player(float spawnX, float spawnY, float widthfromMap, float heightfromMap){
        position = new Vector2(spawnX,spawnY);
        width = widthfromMap;
        height = heightfromMap;
        texture = new Texture("references/playerReference.png");
        sprite = new Sprite(texture);

        sprite.setPosition(position.x, position.y);
        sprite.setSize(width,height);
        velocity = new Vector2(0,0);
        moveSpeed = 300f;
    }

    public void drawPlayer(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void disposePlayer(){
        texture.dispose();
    }

    public void move(Vector2 direction, float dt, Vector2 worldSize){
        float directionX,directionY;
        float normalizer =(float)Math.sqrt(direction.x * direction.x + direction.y * direction.y);
        if(normalizer!=0){
            directionX =  direction.x / normalizer;
            directionY = direction.y / normalizer;
        }
        else{
            directionX = 0;
            directionY = 0;
        }
        
        float velocityX = directionX * moveSpeed;
        float velocityY = directionY * moveSpeed;

        position.x = position.x + velocityX * dt;
        position.y = position.y + velocityY * dt;

        if(position.x <= 0)
            position.x = 0;
        else if(position.x >= worldSize.x - width)
            position.x = worldSize.x - width;

        if(position.y <= 0)
            position.y = 0;
        else if(position.y >= worldSize.y - height)
            position.y = worldSize.y - height;

        sprite.setPosition(position.x , position.y);

    }

    public Vector2 getPlayerPosition(){
        return position;
    }

    public Vector2 getPlayerCentrePosition(){
        Vector2 centrePosition = new Vector2(position.x + width/2 , position.y + height/2);
        return centrePosition;
    }





    public static void main(String[] args){

    }


}