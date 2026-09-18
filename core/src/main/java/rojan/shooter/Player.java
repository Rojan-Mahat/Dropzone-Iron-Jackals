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
    
   

    Player(float spawnX, float spawnY, float widthfromMap, float heightfromMap){
        position = new Vector2(spawnX,spawnY);
        width = widthfromMap;
        height = heightfromMap;
        texture = new Texture("references/playerReference.png");
        sprite = new Sprite(texture);

        sprite.setPosition(position.x, position.y);
        sprite.setSize(width,height);
    }

    public void drawPlayer(SpriteBatch batch){
        sprite.draw(batch);
    }














    public static void main(String[] args){

    }


}