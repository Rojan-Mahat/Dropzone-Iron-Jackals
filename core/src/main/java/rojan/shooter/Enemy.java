package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
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
        private TextureRegion[] prepareFrames;
        private TextureRegion[] jumpFrames;
        private TextureRegion[] airborneFrames;
        private TextureRegion[] landFrames;
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
                prepareFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.PREPARE,AssetPaths.Enemies.IronJackals.DrillCrawler.PREPARE_FRAMES);
                jumpFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.JUMP,AssetPaths.Enemies.IronJackals.DrillCrawler.JUMP_FRAMES);
                airborneFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.AIRBORNE,AssetPaths.Enemies.IronJackals.DrillCrawler.AIRBORNE_FRAMES);
                landFrames = loadFrames(AssetPaths.Enemies.IronJackals.DrillCrawler.LAND,AssetPaths.Enemies.IronJackals.DrillCrawler.LAND_FRAMES);
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
    private static final float DRILLER_DRAW_SCALE = 1.5f;

    private float detectionRadius;
    private float attackRange;
    private final float maxFallSpeed = -800;
    private float maxRiseSpeed;
    private boolean isGrounded;
    private float drillRotation;
    private boolean facingRight;
    private Vector2 nozzlePosition;
    private Array<Circle> drillCircles;
    private boolean targetNeverSeen;
    
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
    private boolean scouting;
    private boolean isChasing;
    private boolean attacking;
    private Rectangle homePlatform;


    Enemy(EnemyType type, float x, float y, float drawWidth, float drawHeight, Rectangle hitbox,Array<Circle> drillCircles){
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
            this.moveSpeed = 165;
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
            this.drawWidth *= DRILLER_DRAW_SCALE;
            this.drawHeight *= DRILLER_DRAW_SCALE;
            maxHitPoints = 180;
            hitPoints = maxHitPoints;
            this.detectionRadius = 500;
            this.attackRange = 20;
            this.moveSpeed = 125;
            maxRiseSpeed = 0 ;
            this.drillCircles = new Array<>();
            float circleScale = ENEMY_SCALE * DRILLER_DRAW_SCALE;
            for(Circle circle : drillCircles)
                this.drillCircles.add(new Circle(circle.x * circleScale,circle.y * circleScale,circle.radius * circleScale));
            
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

    public void drawEnemy(SpriteBatch batch, float dt,Vector2 playerPosition,Array<Rectangle> solidPlatforms,Rectangle playerHurtbox){
        if((type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY )|| (type == EnemyType.DRILL_CRAWLER && currentState != EnemyState.DRILL)){
            resolveVerticalCollision(dt,solidPlatforms);
            ensureGrounding(getWorldHurtbox(),solidPlatforms);
        }
        updateEnemy(dt,playerPosition,solidPlatforms,playerHurtbox);
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

    public EnemyType getType(){
        return type;
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

    private void resolveHorizontalCollision(float dt,Array<Rectangle> solidPlatforms){
        float oldX = position.x;
        position.x += velocity.x * dt;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if(!worldHurtbox.overlaps(rectangle))
                continue;
            if(type == EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING){
                currentState = EnemyState.DRILL;
                position.x = oldX;
                return;
            }
            position.x = oldX;
            velocity.x = 0;
            return;
        }
    }

    private void resolveVerticalCollision(float dt, Array<Rectangle> solidPlatforms){
        float oldY = position.y;
        position.y += velocity.y * dt;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if(!worldHurtbox.overlaps(rectangle))
                continue;
            if(type == EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING){
                currentState = EnemyState.DRILL;
                position.y = oldY;
                return;
            }
            position.y = oldY;
            velocity.y = 0;
            return;
        }
    }

    public void ensureGrounding(Rectangle worldHurtbox,Array<Rectangle> solidPlatforms){
        if(type != EnemyType.HOVER_DRONE || (type==EnemyType.DRILL_CRAWLER && isChasing)){
            isGrounded = false;
            float offset = 2.f;
            for(Rectangle rectangle: solidPlatforms){
                if((worldHurtbox.y >= rectangle.y + rectangle.height) && (worldHurtbox.y <= rectangle.y + rectangle.height + offset)
                && (rectangle.x < worldHurtbox.x + worldHurtbox.width) && (rectangle.x+rectangle.width > worldHurtbox.x)){
                    isGrounded = true;
                    if((type == EnemyType.RIFLEMAN ||type == EnemyType.HEAVY) && (homePlatform == null))
                        homePlatform = rectangle;
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



public void updateEnemy(float dt, Vector2 playerPosition,Array<Rectangle> solidPlatforms, Rectangle playerHurtbox){
    stateTime+= dt;
    if(currentState == EnemyState.DEAD){
        if(!readyTobeRemoved){
            if(stateTime >= DEATH_FRAME_DURATION * animations.deathFrames.length)
                readyTobeRemoved = true;
            return;
        }
        return;
    }
    boolean playerFoundInsideDetectionRadius;
    boolean playerFoundInsideAttackRadius;
    if(!(type==EnemyType.HEAVY)){
        float dx = playerPosition.x - position.x;
        float dy = playerPosition.y - position.y;
        float distanceSquared = dx*dx + dy*dy;
        playerFoundInsideDetectionRadius =  distanceSquared <= (detectionRadius*detectionRadius);
        playerFoundInsideAttackRadius = distanceSquared <= (attackRange * attackRange);
    }
    else{
        playerFoundInsideDetectionRadius = false;
        playerFoundInsideAttackRadius = false;
    }
    if(type==EnemyType.HEAVY){
        //updateHeavy(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt,playerHurtbox, solidPlatforms);
        //need behavioral settings fixed
    }
    else if(type==EnemyType.RIFLEMAN)
        updateRifleman(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt,playerHurtbox, solidPlatforms);

    else if(type==EnemyType.DRILL_CRAWLER)
        updateDrillCrawler(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt,playerPosition, solidPlatforms);
    
    else if(type == EnemyType.HOVER_DRONE){}
        //updateHoverDrone(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt ,playerPosition , playerHurtbox, solidPlatforms);
}

    /*if(!playerFoundInsideDetectionRadius){
        currentState = EnemyState.IDLE;
    }
    else{
       
        if(playerFoundInsideAttackRadius){
            currentState = EnemyState.ATTACK;
            attackPlayer(dt, playerPosition);
        }
        else{
            
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
        }*/

private void attackPlayer(float dt ,Vector2 playerPos){
    
}

private void groundChase(float dt,Vector2 playerPos ){

}

private void flyChase(float dt ,Vector2 playerPos){

}

private void updateDrillCrawler(boolean playerFoundInsideDetectionRadius,boolean playerFoundInsideAttackRadius,float dt,Vector2 playerPos,Array<Rectangle> solidPlatforms){
    if(currentState == EnemyState.DRILL){
        finishDrilling(dt,playerPos,solidPlatforms);
        return;
    }

    if(playerFoundInsideAttackRadius){
        currentState = EnemyState.ATTACK;
        attackPlayer(dt,playerPos);
        return;
    }

    if(playerFoundInsideDetectionRadius){
        currentState = EnemyState.WALKING;
        drillChase(dt,playerPos);
        return;
    }

    currentState = EnemyState.IDLE;
    velocity.x = 0;
}
private void drillChase(float dt,Vector2 playerPos){
    float dx = playerPos.x - position.x;
    if(dx>0)
        facingRight = true;
    else if(dx<0)
        facingRight = false;
    float dy = playerPos.y - position.y;
    float length = (float)Math.sqrt(dx*dx + dy*dy);
    if(length == 0)
        return;
    velocity.x = (dx/length) * moveSpeed;
    velocity.y = (dy/length) * moveSpeed;
    position.x += velocity.x * dt;
    position.y += velocity.y * dt;
    drillRotation = MathUtils.atan2(dy,dx) * MathUtils.radiansToDegrees;
}

private Vector2 setNozzle(float playerX){
    updateFacingDirection(playerX);
    Rectangle hurtbox = getWorldHurtbox();
    float nozzleY = hurtbox.y + hurtbox.height * 0.65f;
    float nozzleX;
    if(facingRight)
        nozzleX = hurtbox.x + hurtbox.width;
    else
        nozzleX = hurtbox.x; 
    return new Vector2(nozzleX,nozzleY);
}

public void jump(){
    if(isGrounded){
        velocity.y = maxRiseSpeed;
        isGrounded = false;
    }
}

private void updateFacingDirection(float playerX){
    if(!scouting){
        if(playerX > position.x)
            facingRight = true;
        else if(playerX < position.x)
            facingRight = false;
    }
}

private void updateRifleman(boolean playerFoundInsideDetectionRadius,boolean playerFoundInsideAttackRadius,  float dt,Rectangle playerHurtbox,Array<Rectangle> solidPlatforms){
     if(homePlatform == null){
        velocity.x = 0;
        return;
    }
    nozzlePosition = setNozzle(playerHurtbox.x + playerHurtbox.width/2);
    if(playerFoundInsideDetectionRadius && isPlayerInLifeofSight(playerHurtbox,solidPlatforms))
        if(playerFoundInsideAttackRadius)
            riflemanAttack(dt);
        else
            riflemanMoveTowardsPlayer(playerHurtbox,dt);
    else if(isPlayerInLifeofSight(playerHurtbox,solidPlatforms) && !scouting)
        currentState = EnemyState.IDLE;
    else
        riflemanScout(dt);
}

private void riflemanMoveTowardsPlayer(Rectangle playerHurtbox, float dt){
    scouting = false;
    attacking = false;
    if(position.x + hurtbox.x < playerHurtbox.x){
        facingRight = true;
        velocity.x = moveSpeed;
    }
    else if(position.x + hurtbox.x > playerHurtbox.x){
        facingRight = false;
        velocity.x = -moveSpeed;
    }

    float oldPos = position.x;
    float newPos = oldPos + velocity.x * dt;
    if((newPos + hurtbox.x + hurtbox.width >= homePlatform.x + homePlatform.width) || (newPos + hurtbox.x <= homePlatform.x))
        position.x = oldPos;
    else 
        position.x = newPos;
}

private void riflemanAttack(float dt){
    attacking = true;
    scouting = false;
    if(attacking)
        currentState = EnemyState.ATTACK;
    
}

private void riflemanScout(float dt){
    attacking = false;
    scouting = true;
    if(scouting)
        currentState = EnemyState.WALKING;
    if(facingRight)
        velocity.x = moveSpeed;
    else
        velocity.x = -moveSpeed;
    float oldPos = position.x;
    float newPos = oldPos + velocity.x * dt;
    if(newPos + hurtbox.x + hurtbox.width >= homePlatform.x + homePlatform.width){
        position.x = oldPos;
        facingRight = false;
        velocity.x = -moveSpeed;
    }
    
    else if(newPos + hurtbox.x <= homePlatform.x){
        position.x = oldPos;
        facingRight = true;
        velocity.x = moveSpeed;

    }
    else 
        position.x = newPos;
}

private boolean isPlayerInLifeofSight(Rectangle playerHurtbox, Array<Rectangle>solidPlatforms){
    boolean first = true;
    boolean second = true;
    boolean third = true;
    Vector2 playerBottom = new Vector2( playerHurtbox.x + playerHurtbox.width/2, playerHurtbox.y);
    Vector2 playerCentre = new Vector2(playerHurtbox.x + playerHurtbox.width/2,playerHurtbox.y+ playerHurtbox.height/2);
    Vector2 playerHead = new Vector2(playerHurtbox.x + playerHurtbox.width/2,playerHurtbox.y+ playerHurtbox.height);
    for(Rectangle platform : solidPlatforms){
        if(Intersector.intersectSegmentRectangle(nozzlePosition,playerBottom,platform))
            first = false;
    }
    for(Rectangle platform : solidPlatforms){
        if(Intersector.intersectSegmentRectangle(nozzlePosition,playerCentre,platform))
            second = false;
    }
    for(Rectangle platform : solidPlatforms){
        if(Intersector.intersectSegmentRectangle(nozzlePosition,playerHead,platform))
            third = false;
    }
    return first || second || third;
}

private void finishDrilling(float dt,Vector2 playerPos,Array<Rectangle> solidPlatforms){
    drillChase(dt,playerPos);
    if(!overlapsAnySolid(solidPlatforms) && isStanding(solidPlatforms)){
        currentState = EnemyState.WALKING;
        isGrounded = true;
        velocity.y = 0;
    }
}

private void  updateHoverDrone(boolean playerFoundInsideDetectionRadius,boolean playerFoundInsideAttackRadius,float dt,Vector2 playerPos, Rectangle playerHurtbox, Array<Rectangle> solidPlatforms){
    nozzlePosition = setNozzle(playerHurtbox.x + playerHurtbox.width/2);
    if(playerFoundInsideAttackRadius){
        droneAttack();
        return;
    }
    if(targetNeverSeen)
        currentState = EnemyState.IDLE;
    else 
        droneChase(dt, playerPos,playerHurtbox,solidPlatforms);
}


private void droneChase(float dt, Vector2 playerPos,Rectangle playerHurtbox,Array<Rectangle> solidPlatforms){
    targetNeverSeen = false;
    if(position.x < playerHurtbox.x)
        velocity.x = moveSpeed;
    else if(position.x > playerHurtbox.x)
        velocity.x = -moveSpeed;
    resolveHorizontalCollision(dt, solidPlatforms);
    resolveVerticalCollision(dt, solidPlatforms);
}

private void sendPlayerLocationToHeavy(){

}

private void droneAttack(){

}


private boolean overlapsAnySolid(Array<Rectangle> solidPlatforms){
    Rectangle worldHurtbox = getWorldHurtbox();
    for(Rectangle rectangle : solidPlatforms){
        if(worldHurtbox.overlaps(rectangle))
            return true;
    }
    return false;
}

private boolean isStanding(Array<Rectangle> solidPlatforms){
    Rectangle worldHurtbox = getWorldHurtbox();
    float offset = 2f;
    for(Rectangle rectangle : solidPlatforms){
        float platformTop = rectangle.y + rectangle.height;
        boolean feetOnTop = worldHurtbox.y >= platformTop && worldHurtbox.y <= platformTop + offset;
        boolean horizontalOverlap = worldHurtbox.x + worldHurtbox.width > rectangle.x && worldHurtbox.x < rectangle.x + rectangle.width;
        if(feetOnTop && horizontalOverlap)
            return true;
    }
    return false;
}

public Array<Circle> getWorldDrillCircles(){
    Array<Circle> worldCircles = new Array<>();
    float rotation = currentState == EnemyState.DRILL ? drillRotation : 0f;
    float radians = rotation * MathUtils.degreesToRadians;
    float cos = MathUtils.cos(radians);
    float sin = MathUtils.sin(radians);
    for(Circle circle : drillCircles){
        float localX = facingRight || currentState == EnemyState.DRILL ? circle.x: -circle.x;
        float localY = circle.y - 10f;
        float rotatedX = localX * cos - localY * sin;
        float rotatedY = localX * sin + localY * cos;
        worldCircles.add(new Circle(position.x + rotatedX,position.y + rotatedY,circle.radius));
    }
    return worldCircles;
}



public static void main(String[] args){

    }
}
