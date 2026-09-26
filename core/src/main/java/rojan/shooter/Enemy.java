package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy{
    public enum EnemyType{
        RIFLEMAN,
        HEAVY,
        HOVER_DRONE,
        DRILL_CRAWLER
    }

    public enum EnemyState{
        ATTACK,
        DEAD,
        WALKING,
        FLYING,
        HURT,
        DRILL,
        IDLE,
        EXPLODING
    }
    private EnemyState currentState;
    private EnemyType type;
    private float hitPoints;
    private float maxHitPoints;
    private float Damage;
    private Vector2 position;
    private Vector2 velocity;
    private static class EnemyAnimations{
        private float originX;
        private float originY;
        private TextureRegion[] idleFrames;
        private TextureRegion[] walkFrames;
        private TextureRegion[] attackFrames;
        private TextureRegion[] hurtFrames;
        private TextureRegion[] drillFrames;
        private TextureRegion[] deathFrames;
        private TextureRegion[] flyFrames;
        EnemyAnimations(EnemyType type){
            if(type == EnemyType.RIFLEMAN){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.IDLE, AssetPaths.Enemies.IronJackals.Rifleman.IDLE_FRAMES);
                walkFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.WALK, AssetPaths.Enemies.IronJackals.Rifleman.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ATTACK, AssetPaths.Enemies.IronJackals.Rifleman.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.HURT, AssetPaths.Enemies.IronJackals.Rifleman.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.DEATH, AssetPaths.Enemies.IronJackals.Rifleman.DEATH_FRAMES);
            }

            else if(type == EnemyType.HEAVY){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.IDLE, AssetPaths.Enemies.IronJackals.Heavy.IDLE_FRAMES);
                walkFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.WALK, AssetPaths.Enemies.IronJackals.Heavy.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ATTACK, AssetPaths.Enemies.IronJackals.Heavy.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.HURT, AssetPaths.Enemies.IronJackals.Heavy.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.DEATH, AssetPaths.Enemies.IronJackals.Heavy.DEATH_FRAMES);
            }

            else if(type == EnemyType.HOVER_DRONE){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.IDLE, AssetPaths.Enemies.IronJackals.HoverDrone.IDLE_FRAMES);
                flyFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.WALK, AssetPaths.Enemies.IronJackals.HoverDrone.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.ATTACK, AssetPaths.Enemies.IronJackals.HoverDrone.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.HURT, AssetPaths.Enemies.IronJackals.HoverDrone.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.DEATH, AssetPaths.Enemies.IronJackals.HoverDrone.DEATH_FRAMES);

            }

            else if(type == EnemyType.DRILL_CRAWLER){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.IDLE, AssetPaths.Enemies.IronJackals.DrillCrawler.IDLE_FRAMES);
                walkFrames  = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.WALK, AssetPaths.Enemies.IronJackals.DrillCrawler.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.ATTACK, AssetPaths.Enemies.IronJackals.DrillCrawler.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.HURT, AssetPaths.Enemies.IronJackals.DrillCrawler.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.DEATH, AssetPaths.Enemies.IronJackals.DrillCrawler.DEATH_FRAMES);
                drillFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.DRILL, AssetPaths.Enemies.IronJackals.DrillCrawler.DRILL_FRAMES);
            }

            if(type == EnemyType.RIFLEMAN){   
                originX = 430f;
                originY = 94f;
            }
            else if(type == EnemyType.HEAVY){
                originX = 418f;
                originY = 94f;
            }
            else if(type == EnemyType.HOVER_DRONE){
                originX = 512f;
                originY = 564f;
            }
            else if(type == EnemyType.DRILL_CRAWLER){
                originX = 460f;
                originY = 224f;
            }
        }
    }
    private static Texture healthBarTexture;
    private static TextureRegion[] healthBarFrames;

    private static EnemyAnimations riflemanAnimations;
    private static EnemyAnimations heavyAnimations;
    private static EnemyAnimations hoverDroneAnimations;
    private static EnemyAnimations drillCrawlerAnimations;
    private EnemyAnimations animations;

    private static Array<Texture> loadedTextures;
    private float stateTime;
    private float drawWidth;
    private float drawHeight;
    private Rectangle hurtbox;
    private static final float ENEMY_SCALE = 1.3f;
    private final float DEATH_FRAME_DURATION = 0.09f;
    private final float IDLE_FRAME_DURATION = 0.12f;
    

    private static class EnemyHitRegions{
        Array<Rectangle> headshotBoxes;
        Array<Rectangle> bodyshotBoxes;
        Array<Rectangle> limbshotBoxes;

        EnemyHitRegions(Array<Rectangle> headshots, Array<Rectangle> bodyshots, Array<Rectangle> limbs){
            headshotBoxes = new Array<>(headshots);
            bodyshotBoxes = new Array<>(bodyshots);
            limbshotBoxes = new Array<>(limbs);
        } 
    
    }
    private static EnemyHitRegions riflemanHitRegions;
    private static EnemyHitRegions heavyHitRegions;
    private static EnemyHitRegions droneHitRegions;
    private static EnemyHitRegions drillerHitRegions;
    private EnemyHitRegions hitRegions;
    private boolean readyTobeRemoved;


    Enemy(EnemyType type, float x, float y, float drawWidth, float drawHeight, Rectangle hitbox){
        this.type = type;
        position = new Vector2(x, y);
        this.drawWidth = drawWidth * ENEMY_SCALE;
        this.drawHeight = drawHeight * ENEMY_SCALE;
        this.hurtbox = new Rectangle(scaleRectangle(hitbox));
        if(type == EnemyType.RIFLEMAN){
            animations = riflemanAnimations;
            this.hitRegions = riflemanHitRegions;
            maxHitPoints = 100;
            hitPoints = maxHitPoints;
        }
        else if(type == EnemyType.HEAVY){
            animations = heavyAnimations;
            this.hitRegions = heavyHitRegions;
            maxHitPoints = 250;
            hitPoints = maxHitPoints;
        }
        else if(type == EnemyType.HOVER_DRONE){
            animations = hoverDroneAnimations;
            this.hitRegions = droneHitRegions;
            maxHitPoints = 70;
            hitPoints = maxHitPoints;
        }
        else if(type == EnemyType.DRILL_CRAWLER){
            animations = drillCrawlerAnimations;
            this.hitRegions = drillerHitRegions;
            maxHitPoints = 180;
            hitPoints = maxHitPoints;
        }
        currentState = EnemyState.IDLE;
        stateTime = 0f;
        readyTobeRemoved = false;
        healthBarTexture = new Texture(AssetPaths.Resource.enemyHealthBar);
        TextureRegion[][] splitHealthBar = TextureRegion.split(healthBarTexture,256,48);
        healthBarFrames = splitHealthBar[0];
    }

    private static TextureRegion[] loadFrames(String prefix, int frameCount){
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

    public static void disposeEnemyAssets(){
        for(Texture texture:loadedTextures)
            texture.dispose();
    }

    public static void loadEnemyAssetOnce(){
        if(loadedTextures!=null)
            return;
        loadedTextures = new Array<>();
        riflemanAnimations = new EnemyAnimations(EnemyType.RIFLEMAN);
        heavyAnimations = new EnemyAnimations(EnemyType.HEAVY);
        hoverDroneAnimations = new EnemyAnimations(EnemyType.HOVER_DRONE);
        drillCrawlerAnimations = new EnemyAnimations(EnemyType.DRILL_CRAWLER);
    }

    public void drawEnemy(SpriteBatch batch, float dt){
        updateEnemy(dt);
        TextureRegion frame;
        if(currentState == EnemyState.DEAD){
            int frameIndex = (int)(stateTime / DEATH_FRAME_DURATION);
            if(frameIndex >= animations.deathFrames.length)
                frameIndex = animations.deathFrames.length-1;
            frame = animations.deathFrames[frameIndex];
        }
        else{
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.idleFrames.length;
            frame = animations.idleFrames[frameIndex];
        }
        
        float scaleX = drawWidth / 1024f;
        float scaleY = drawHeight/1024f;

        float drawX = position.x - animations.originX * scaleX;
        float drawY = position.y - animations.originY * scaleY;

        batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
        drawHealthBar(batch);
    }

    public float getHitPoints(){
        return hitPoints;
    }

    public void enemyHurt(float damage){
        if(currentState == EnemyState.DEAD || currentState == EnemyState.EXPLODING)
            return;
        hitPoints-=damage;
        if(hitPoints <= 0){
            currentState = EnemyState.DEAD;
            stateTime = 0f;
        }
    }

    public Rectangle getWorldHurtbox(){
        return new Rectangle(position.x + hurtbox.x,position.y + hurtbox.y, hurtbox.width, hurtbox.height);
    }

    private Rectangle scaleRectangle(Rectangle rect){
        return new Rectangle(rect.x * ENEMY_SCALE,rect.y * ENEMY_SCALE,rect.width * ENEMY_SCALE,rect.height * ENEMY_SCALE);
    }

    public static void setEnemyHurtBox(EnemyType type ,Array<Rectangle> headshots, Array<Rectangle> bodyshots, Array<Rectangle> limbs){
        if(type == EnemyType.RIFLEMAN)
            riflemanHitRegions = new EnemyHitRegions(headshots, bodyshots, limbs);
        else if(type == EnemyType.HEAVY)
            heavyHitRegions = new EnemyHitRegions(headshots, bodyshots, limbs);
        else if(type == EnemyType.HOVER_DRONE)
            droneHitRegions = new EnemyHitRegions(headshots, bodyshots, limbs);
        else if(type == EnemyType.DRILL_CRAWLER)
            drillerHitRegions = new EnemyHitRegions(headshots, bodyshots, limbs);
    }
    

    static public float getEnemyScale(){
        return  ENEMY_SCALE;
    }

    public Vector2 getPosition(){
        return position;
    }

    public Array<Rectangle> getHeadshotBoxes(){
        return hitRegions.headshotBoxes;
    }

    public Array<Rectangle> getBodyshotBoxes(){
        return hitRegions.bodyshotBoxes;  
    }

    public Array<Rectangle> getLimbshotBoxes(){
        return hitRegions.limbshotBoxes;  
    }

    private void updateEnemy(float dt){
        stateTime += dt;
        if(currentState == EnemyState.DEAD && !readyTobeRemoved){
            if(stateTime >= DEATH_FRAME_DURATION * animations.deathFrames.length)
                readyTobeRemoved = true;
        }
    }

    public boolean isReadytoBeRemoved(){
        return readyTobeRemoved;
    }

    private void drawHealthBar(SpriteBatch batch){
    if(currentState == EnemyState.DEAD || currentState == EnemyState.EXPLODING)
        return;
    float healthRatio = MathUtils.clamp(hitPoints / maxHitPoints,0f,1f);
    int frameIndex = 11 - (int)Math.ceil(11f * healthRatio);

    float barWidth = 90f;
    float barHeight = barWidth * (48f / 256f);

    Rectangle worldHurtbox = getWorldHurtbox();

    float barX = worldHurtbox.x + worldHurtbox.width/2f - barWidth/2f;
    float barY = worldHurtbox.y + worldHurtbox.height + 10f;
    batch.draw(healthBarFrames[frameIndex],barX,barY,barWidth,barHeight);
}




public static void main(String[] args){

    }
}
