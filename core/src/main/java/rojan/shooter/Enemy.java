package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

public class Enemy{
    private enum EnemyType{
        Jackal_RifleMan,
        Jackal_heavy,
        Jackal_hoverDrone;
    }
    private EnemyType type;
    private float hitPoints;
    private float Damage;
    private Vector2 position;
    private Vector2 speed;

    private Rectangle hitbox;

    private TextureRegion[] idleFrames;
    private Array<Texture> loadedTextures;
    private float stateTime;
    private float drawSize;



    Enemy(float x, float y, float drawSize){
        position = new Vector2(x, y);
        this.drawSize = drawSize;
        loadedTextures = new Array<>();
        idleFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.IDLE, AssetPaths.Enemies.IronJackals.Rifleman.IDLE_FRAMES);
        stateTime = 0f;
    }

    private TextureRegion[] loadFrames(String prefix, int frameCount){
        TextureRegion[] frames = new TextureRegion[frameCount];
        for(int i = 0; i < frameCount; i++){
            String frameNumber = String.format("%02d", i);
            String path = prefix + frameNumber + ".png";
            Texture texture = new Texture(path);
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
        return frames;
    }

    public void drawEnemy(SpriteBatch batch, float dt){
        stateTime += dt;
        int frameIndex = (int)(stateTime / 0.18f) % idleFrames.length;
        TextureRegion frame = idleFrames[frameIndex];
        float scale = drawSize / 256f;
        float drawX = position.x - 112f * scale;
        float drawY = position.y - 24f * scale;
        batch.draw(frame, drawX, drawY, drawSize, drawSize);
    }

    public void receiveHitbox(Rectangle hitbox){
        this.hitbox = new Rectangle(hitbox);
    }




public static void main(String[] args){

    }
}
