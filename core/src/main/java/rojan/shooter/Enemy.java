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
    private float damage;
    private Vector2 position;
    private Vector2 velocity;
    private float moveSpeed;
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

    private float detectionRadius;
    private float attackRange;
    private final float maxFallSpeed = -800;
    private float maxRiseSpeed;
    private boolean isGrounded;
    private float drillRotation;
    private boolean facingRight;
    

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
    private boolean isChasing;


    Enemy(EnemyType type, float x, float y, float drawWidth, float drawHeight, Rectangle hitbox){
        this.type = type;
        position = new Vector2(x, y);
        this.drawWidth = drawWidth * ENEMY_SCALE;
        this.drawHeight = drawHeight * ENEMY_SCALE;
        this.hurtbox = new Rectangle(scaleRectangle(hitbox));
        velocity = new Vector2();
        if(type == EnemyType.RIFLEMAN){
            animations = riflemanAnimations;
            this.hitRegions = riflemanHitRegions;
            maxHitPoints = 100;
            hitPoints = maxHitPoints;
            this.detectionRadius = 700;
            this.attackRange = 350;
            this.moveSpeed = 325;
            this.maxRiseSpeed = 500;
        }
        else if(type == EnemyType.HEAVY){
            animations = heavyAnimations;
            this.hitRegions = heavyHitRegions;
            maxHitPoints = 250;
            hitPoints = maxHitPoints;
            this.detectionRadius = 600;
            this.attackRange = 300;
            this.moveSpeed = 250;
            this.maxRiseSpeed = 800;
        }
        else if(type == EnemyType.HOVER_DRONE){
            animations = hoverDroneAnimations;
            this.hitRegions = droneHitRegions;
            maxHitPoints = 70;
            hitPoints = maxHitPoints;
            this.detectionRadius = 850;
            this.attackRange = 250;
            this.moveSpeed = 450;
            this.maxRiseSpeed = 500;
        }
        else if(type == EnemyType.DRILL_CRAWLER){
            animations = drillCrawlerAnimations;
            this.hitRegions = drillerHitRegions;
            maxHitPoints = 180;
            hitPoints = maxHitPoints;
            this.detectionRadius = 500;
            this.attackRange = 20;
            this.moveSpeed = 125;
            maxRiseSpeed = 0 ;
        }
        currentState = EnemyState.IDLE;
        stateTime = 0f;
        readyTobeRemoved = false;
        facingRight = true;
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

        healthBarTexture = new Texture(AssetPaths.Resource.enemyHealthBar);
        TextureRegion[][] splitHealthBar = TextureRegion.split(healthBarTexture,256,48);
        healthBarFrames = splitHealthBar[0];
    }

    public void drawEnemy(SpriteBatch batch, float dt,Vector2 playerPosition,Array<Rectangle> solidPlatforms){
        if(type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY || (type == EnemyType.DRILL_CRAWLER && currentState != EnemyState.DRILL)){
            position.y += velocity.y * dt;
            resolveVerticalCollision(solidPlatforms);
            ensureGrounding(getWorldHurtbox(),solidPlatforms);
        }
    
        updateEnemy(dt,playerPosition,solidPlatforms);
        TextureRegion frame;
        if(currentState == EnemyState.DEAD){
            int frameIndex = (int)(stateTime / DEATH_FRAME_DURATION);
            if(frameIndex >= animations.deathFrames.length)
                frameIndex = animations.deathFrames.length-1;
            frame = animations.deathFrames[frameIndex];
        }     
        else if(currentState == EnemyState.WALKING){
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.walkFrames.length;
            frame = animations.walkFrames[frameIndex];
        }
        else if(currentState == EnemyState.FLYING){
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.flyFrames.length;
            frame = animations.flyFrames[frameIndex];
        }
        else if(currentState == EnemyState.DRILL){
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.drillFrames.length;
            frame = animations.drillFrames[frameIndex];
        }
        else if(currentState == EnemyState.ATTACK){
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.attackFrames.length;
            frame = animations.attackFrames[frameIndex];
        }
        else{
            int frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.idleFrames.length;
            frame = animations.idleFrames[frameIndex];
        }

        float scaleX = drawWidth / 1024f;
        float scaleY = drawHeight/1024f;

        float drawX = position.x - animations.originX * scaleX;
        float drawY = position.y - animations.originY * scaleY;

        float rotationOriginX = animations.originX * scaleX;
        float rotationOriginY = animations.originY * scaleY;
        float rotation = 0f;
        float renderScaleX = facingRight ? 1f : -1f;
        if(currentState == EnemyState.DRILL){
            rotation = drillRotation;
            renderScaleX = 1f;
        }

        batch.draw(frame, drawX, drawY,rotationOriginX, rotationOriginY, drawWidth, drawHeight, renderScaleX,1f, rotation);
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

public void reactToGravity(float gravity , float dt){
    if((type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY) ||
    (type == EnemyType.DRILL_CRAWLER && currentState!=EnemyState.DRILL)){
        if(!isGrounded)
            velocity.y+=gravity*dt;
        else
            velocity.y = 0;
        if(velocity.y < maxFallSpeed)
            velocity.y = maxFallSpeed;
    }
}

private void resolveHorizontalCollision(Vector2 movementDirection, Array<Rectangle> solidPlatforms){
    boolean overlapping = false;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if((worldHurtbox.x + worldHurtbox.width > rectangle.x && worldHurtbox.x < rectangle.x + rectangle.width)
            && worldHurtbox.y < rectangle.y+rectangle.height && worldHurtbox.y + worldHurtbox.height > rectangle.y){
                overlapping = true;
                if(movementDirection.x < 0){
                    if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING && isChasing)
                        currentState = EnemyState.DRILL;
                    if(currentState!=EnemyState.DRILL){
                        position.x = rectangle.x + rectangle.width - hurtbox.x;
                        velocity.x = 0;
                    }
                }
                else if(movementDirection.x>0){
                    if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING && isChasing)
                        currentState = EnemyState.DRILL;
                    if(currentState!=EnemyState.DRILL){
                        position.x = rectangle.x - hurtbox.width - hurtbox.x; 
                        velocity.x = 0;
                    }
                }
            }
        }
        if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.DRILL && !overlapping)
            currentState = EnemyState.WALKING;         
    }

    private void resolveVerticalCollision(Array<Rectangle> solidPlatforms){
        boolean overlapping = false;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if((worldHurtbox.y + worldHurtbox.height > rectangle.y &&  worldHurtbox.y < rectangle.y + rectangle.height)
            && (worldHurtbox.x + worldHurtbox.width > rectangle.x && worldHurtbox.x < rectangle.x + rectangle.width )){
                overlapping = true;
                if(velocity.y > 0){
                    if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING && isChasing)
                        currentState = EnemyState.DRILL;
                    if(currentState!=EnemyState.DRILL){
                        position.y = rectangle.y - hurtbox.height - hurtbox.y;
                        velocity.y = 0;
                    }
                }
                else if(velocity.y < 0){
                    if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING && isChasing)
                        currentState = EnemyState.DRILL;
                    if(currentState!=EnemyState.DRILL){
                        position.y = rectangle.y + rectangle.height - hurtbox.y;
                        velocity.y = 0;
                    }
                }
            }
        }
        if(type==EnemyType.DRILL_CRAWLER && currentState == EnemyState.DRILL && !overlapping)
            currentState = EnemyState.WALKING;
    }

    public void ensureGrounding(Rectangle worldHurtbox,Array<Rectangle> solidPlatforms){
        if(type != EnemyType.HOVER_DRONE || (type==EnemyType.DRILL_CRAWLER && isChasing)){
            isGrounded = false;
            float offset = 2.f;
            for(Rectangle rectangle: solidPlatforms){
                if((worldHurtbox.y >= rectangle.y + rectangle.height) && (worldHurtbox.y <= rectangle.y + rectangle.height + offset)
                && (rectangle.x < worldHurtbox.x + worldHurtbox.width) && (rectangle.x+rectangle.width > worldHurtbox.x)){
                    isGrounded = true;
                    break;
                }
            }
        }
    }


    public Rectangle getWorldHitRegion(Rectangle rect){
    float worldX;
    if(facingRight)
        worldX = position.x + rect.x;
    else
        worldX = position.x - rect.x - rect.width;
    return new Rectangle(worldX,position.y + rect.y,rect.width,rect.height);
}



public void updateEnemy(float dt, Vector2 playerPosition,Array<Rectangle> solidPlatforms){
    stateTime+= dt;
    if(currentState == EnemyState.DEAD){
        if(!readyTobeRemoved){
            if(stateTime >= DEATH_FRAME_DURATION * animations.deathFrames.length)
                readyTobeRemoved = true;
            return;
        }
    }
    
    float dx = playerPosition.x - position.x;
    float dy = playerPosition.y - position.y;
    float distanceSquared = dx*dx + dy*dy;
    boolean playerFoundInsideDetectionRadius =  distanceSquared <= (detectionRadius*detectionRadius);

    if(!playerFoundInsideDetectionRadius){
        isChasing = false;
        currentState = EnemyState.IDLE;
    }
    else{
        boolean playerFoundInsideAttackRadius = distanceSquared <= (attackRange * attackRange);
        if(playerFoundInsideAttackRadius){
            isChasing = false;
            currentState = EnemyState.ATTACK;
            attackPlayer(dt, playerPosition);
        }
        else{
            isChasing = true;
            if(type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY || type == EnemyType.DRILL_CRAWLER){
                if(type == EnemyType.DRILL_CRAWLER && currentState!=EnemyState.DRILL)
                    currentState = EnemyState.WALKING;
                if(type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY)
                    groundChase(dt,playerPosition);
                else 
                    drillChase(dt, playerPosition,solidPlatforms);
            }
             else if(type == EnemyType.HOVER_DRONE){
                currentState = EnemyState.FLYING;
                flyChase(dt, playerPosition);
             }
        }

    }

}

private void attackPlayer(float dt ,Vector2 playerPos){
    
}

private void groundChase(float dt,Vector2 playerPos ){

}

private void flyChase(float dt ,Vector2 playerPos){

}

private void drillChase(float dt ,Vector2 playerPos,Array<Rectangle> solidPlatforms){
    float dx = playerPos.x - position.x;
    float dy = playerPos.y - position.y;
    if(dx > 0)
        facingRight = true;
    else if(dx < 0)
        facingRight = false;

    float length = (float)Math.sqrt((dx*dx)+(dy*dy));
    float directionX = dx/length;
    float directionY = dy/length;
    velocity.x = directionX * moveSpeed;
    velocity.y = directionY * moveSpeed;

    position.x += velocity.x * dt;
    position.y += velocity.y * dt;

    drillRotation = MathUtils.atan2(dy,dx) * MathUtils.radiansToDegrees;
    resolveHorizontalCollision(playerPos,solidPlatforms);


}

public void jump(){
    if(isGrounded){
        velocity.y = maxRiseSpeed;
        isGrounded = false;
    }
}



public static void main(String[] args){

    }
}
