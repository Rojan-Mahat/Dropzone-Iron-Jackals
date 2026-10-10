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
        EXPLODING,
        CHARGING
    }
    private EnemyState currentState;
    private EnemyType type;
    private float hitPoints;
    private float maxHitPoints;
    private float pendingDamage;
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
        private TextureRegion[] chargeFrames;

        TextureRegion[] armIdleFrames;
        TextureRegion[] armWalkingFrames;
        TextureRegion[] armAttackFrames;
        TextureRegion[] armHurtFrames;
        EnemyAnimations(EnemyType type){
            if(type == EnemyType.RIFLEMAN){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.IDLE, AssetPaths.Enemies.IronJackals.Rifleman.IDLE_FRAMES);
                walkFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.WALK, AssetPaths.Enemies.IronJackals.Rifleman.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ATTACK, AssetPaths.Enemies.IronJackals.Rifleman.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.HURT, AssetPaths.Enemies.IronJackals.Rifleman.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.DEATH, AssetPaths.Enemies.IronJackals.Rifleman.DEATH_FRAMES);

                armIdleFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ARM_IDLE,AssetPaths.Enemies.IronJackals.Rifleman.ARM_IDLE_FRAMES);
                armWalkingFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ARM_WALKING,AssetPaths.Enemies.IronJackals.Rifleman.ARM_WALKING_FRAMES);
                armAttackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ARM_ATTACK,AssetPaths.Enemies.IronJackals.Rifleman.ARM_ATTACK_FRAMES);
                armHurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Rifleman.ARM_HURT,AssetPaths.Enemies.IronJackals.Rifleman.ARM_HURT_FRAMES);
            }

            else if(type == EnemyType.HEAVY){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.IDLE, AssetPaths.Enemies.IronJackals.Heavy.IDLE_FRAMES);
                walkFrames  = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.WALK, AssetPaths.Enemies.IronJackals.Heavy.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ATTACK, AssetPaths.Enemies.IronJackals.Heavy.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.HURT, AssetPaths.Enemies.IronJackals.Heavy.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.DEATH, AssetPaths.Enemies.IronJackals.Heavy.DEATH_FRAMES);
                armIdleFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ARM_IDLE,AssetPaths.Enemies.IronJackals.Heavy.ARM_IDLE_FRAMES);
                armWalkingFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ARM_WALKING,AssetPaths.Enemies.IronJackals.Heavy.ARM_WALKING_FRAMES);
                armAttackFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ARM_ATTACK,AssetPaths.Enemies.IronJackals.Heavy.ARM_ATTACK_FRAMES);
                armHurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.Heavy.ARM_HURT,AssetPaths.Enemies.IronJackals.Heavy.ARM_HURT_FRAMES);
            }

            else if(type == EnemyType.HOVER_DRONE){
                idleFrames  = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.IDLE, AssetPaths.Enemies.IronJackals.HoverDrone.IDLE_FRAMES);
                flyFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.WALK, AssetPaths.Enemies.IronJackals.HoverDrone.WALK_FRAMES);
                attackFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.ATTACK, AssetPaths.Enemies.IronJackals.HoverDrone.ATTACK_FRAMES);
                hurtFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.HURT, AssetPaths.Enemies.IronJackals.HoverDrone.HURT_FRAMES);
                deathFrames = loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.DEATH, AssetPaths.Enemies.IronJackals.HoverDrone.DEATH_FRAMES);
                chargeFrames =  loadFrames(AssetPaths.Enemies.IronJackals.HoverDrone.CHARGE, AssetPaths.Enemies.IronJackals.HoverDrone.CHARGE_FRAMES);
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
    private static final float DRILLER_DRAW_SCALE = 1.5f;
    private static final float DRONE_DRAW_SCALE = 0.69f;

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
    private boolean HEAVY_LOCK;
    private float HEAVY_CLOSE_ANGLE;
    private float RANDOM_LIMIT = MathUtils.random(2f,7f);
    private boolean HEAVY_CLOSE_SHOT_ONCE;
    private float HEAVY_CLOSE_TIMER = 0;
    private float HEAVY_CLOSE_COOLDOWN = 2f;

    private final float POISON_RADIUS = 80f;
    private final float STUN_RADIUS = 80f;
    private final float BOMBARDMENT_RADIUS = 50f;

    private final float POISON_AREA_LIFETIME = 12f;
    private final float STUN_AREA_LIFETIME = 3.5f;
    private final float BOMBARDMENT_AREA_LIFETIME = 3.5f;
  

    private float BOMBARDMENT_SPAWN_TIMER = 0;
    private int BOMBARDMENT_PROJECTILES_SPAWNED;
    private float BOMBARDMENT_SPEED = 350f;
    private float PROJECTILE_WIDTH = 25f;
    private float PROJECTILE_HEIGHT = 30f;
    private float BOMBARDMENT_Y;
    private float BOMBARDMENT_START_X;
    private float BOMBARDMENT_END_X;
    private Array<Rectangle> BOMBARDMENT_PROJECTILES = new Array<>();
    private Array<Vector2> BOMBARDMENT_IMPACT_EVENTS = new Array<>();

    private final float POISON_DAMAGE = 8f;
    private final float POISON_LINGERING_DAMAGE = 2.5f;
    private final float BOMBARDMENT_DAMAGE = 20f;

    private boolean PLAYER_POISIONED = false;
    private boolean PLAYER_STUNNED = false;

    private final float POISON_LINGERING_LIFETIME = 7f;
    private final float STUN_LINGERING_LIFETIME = 7f;
 
    private float POISON_LINGERING_DURATION;
    private float STUN_LINGERING_DURATION;

    private boolean HEAVY_RANGE_ATTACK_ACTIVE = false;
    private boolean HEAVY_RANGE_ATTACK_COMPLETED = false;

    private boolean PLAYER_INSIDE_POISON;
    
    private float HEAVY_LONG_RANGE_ATTACK_DURATION = 0f;
    private boolean HEAVY_LEFT_TARGET = false;
    private Vector2 HEAVY_PREDICTED_RAIN_FIRE_POSITION = new Vector2();
    private boolean HEAVY_PORTAL_ATTACK_CHOSEN = false;
    private int RANDOM_SELECT_ATTACK = (int) MathUtils.random(1,3);

    private boolean DRONE_LOCK_SENT = false;
    private boolean HEAVY_SHOT_READY = false;

    private static final float[] RIFLEMAN_ATTACK_SOCKET_X = {380.5f,378.606761f,376.430786f,378.793841f,378.656559f,380.5f};
    private static final float[] RIFLEMAN_ATTACK_SOCKET_Y = {475.92f,476.254529f,476.767235f,476.339072f,476.128497f,475.92f};
    private static final float[] RIFLEMAN_ATTACK_MUZZLE_X = {462.66f,457.352949f,452.525160f,459.307168f,456.033003f,462.66f};
    private static final float[] RIFLEMAN_ATTACK_MUZZLE_Y = {3.96f,6.381648f,15.262032f,7.997434f,6.381578f,3.96f};

    private static final float[] HEAVY_ATTACK_SOCKET_X = {236f,236f,231.344113f,232.5058f,233.66902f,234.60054f,235.533053f,236f};
    private static final float[] HEAVY_ATTACK_SOCKET_Y = {463f,463f,465.097084f,464.571043f,464.046175f,463.627069f,463.20878f,463f};
    private static final float[] HEAVY_ATTACK_MUZZLE_X = {568f,568f,548.000196f,555.000110f,561.000049f, 565.999881f,567.999797f, 568f};
    private static final float[] HEAVY_ATTACK_MUZZLE_Y = {-89f,-89f,-89.000039f, -89.000022f,-89.000010f, -88.999981f,-88.999968f, -89f};

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
    private float RIFLEMAN_AIM_ANGLE;

    private float DRILL_HEAD_OFFSET_X = 100f;
    private float DRONE_ATTACK_DURATION_LIMIT = 3;
    private float DRONE_ATTACK_TIMER = 0;
    private boolean DRONE_COOLDOWN;
    private float DRONE_COOLDOWN_TIMER = 0;
    private float HEAVY_LOCK_TIMER = 0;
    private float HEAVY_PORTAL_FX_TIMER = 0;
    private float RANDOM_LOCK_LIMIT = MathUtils.random(0.85f,(RANDOM_LIMIT-0.2f));

    private float droneBeamMagnitude;
    private float droneBeamAngleDegrees;
    private float DRONE_LOCK_TIME = MathUtils.random(0.5f,4f);

    private Vector2 RIFLEMAN_MUZZLE_POSITION = new Vector2();
    private Vector2 HEAVY_MUZZLE_POSITION = new Vector2();

    private final float RIFLEMAN_DAMAGE = 7;
    private final float HEAVY_CLOSE_RANGE_DAMAGE = 18;
    private final float DRONE_DAMAGE = 3;
    private final float DRILL_DAMAGE = 10;

    private float RIFLEMAN_SHOT_TIMER;
    private final float RIFLEMAN_FIRE_INTERVAL = 0.3f;
    private boolean RIFLEMAN_SHOT_READY;
    private Vector2 enemyDeathLocation;

    Enemy(EnemyType type, float x, float y, float drawWidth, float drawHeight, Rectangle hitbox,Array<Circle> drillCircles){
        this.type = type;
        position = new Vector2(x, y);
        this.drawWidth = drawWidth * ENEMY_SCALE;
        this.drawHeight = drawHeight * ENEMY_SCALE;
        this.hurtbox = new Rectangle(scaleRectangle(hitbox));
        velocity = new Vector2();
        this.enemyDeathLocation = new Vector2();
        if(type == EnemyType.RIFLEMAN){
            animations = riflemanAnimations;
            this.hitRegions = riflemanHitRegions;
            maxHitPoints = 120;
            hitPoints = maxHitPoints;
            this.detectionRadius = 900;
            this.attackRange = 750;
            this.moveSpeed = 165;
            this.maxRiseSpeed = 500;
        }
        else if(type == EnemyType.HEAVY){
            animations = heavyAnimations;
            this.hitRegions = heavyHitRegions;
            maxHitPoints = 350;
            hitPoints = maxHitPoints;
            this.detectionRadius = 1100;
            this.attackRange = 750;
            this.moveSpeed = 120;
        }
        else if(type == EnemyType.HOVER_DRONE){
            animations = hoverDroneAnimations;
            this.hitRegions = droneHitRegions;
            maxHitPoints = 70;
            hitPoints = maxHitPoints;
            this.detectionRadius = 1100;
            this.attackRange = 550;
            this.moveSpeed = 225;
            this.maxRiseSpeed = 500;
            this.drawWidth *= DRONE_DRAW_SCALE;
            this.drawHeight *= DRONE_DRAW_SCALE;
            float centerX = hurtbox.x + hurtbox.width / 2f;
            float centerY = hurtbox.y + hurtbox.height / 2f;
            hurtbox.width *= DRONE_DRAW_SCALE;
            hurtbox.height *= DRONE_DRAW_SCALE;
            hurtbox.x = centerX - hurtbox.width / 2f;
            hurtbox.y = centerY - hurtbox.height / 2f;
            DRONE_COOLDOWN = false;
        }
        else if(type == EnemyType.DRILL_CRAWLER){
            animations = drillCrawlerAnimations;
            this.hitRegions = drillerHitRegions;
            this.drawWidth *= DRILLER_DRAW_SCALE;
            this.drawHeight *= DRILLER_DRAW_SCALE;
            maxHitPoints = 180;
            hitPoints = maxHitPoints;
            this.detectionRadius = 1000;
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

    public void drawEnemy(SpriteBatch batch, float dt,Vector2 playerPosition,Array<Rectangle> solidPlatforms,Rectangle playerHurtbox, Vector2 playerVelocity,Rectangle heavyPortalBounds){
        if((type == EnemyType.RIFLEMAN || type == EnemyType.HEAVY )|| (type == EnemyType.DRILL_CRAWLER && currentState != EnemyState.DRILL)){
            resolveVerticalCollision(dt,solidPlatforms);
            ensureGrounding(getWorldHurtbox(),solidPlatforms);
        }
        updateEnemy(dt,playerPosition,solidPlatforms,playerHurtbox,playerVelocity,heavyPortalBounds);
        int frameIndex = 0;
        TextureRegion frame;
        if(currentState == EnemyState.DEAD){
            frameIndex = (int)(stateTime / DEATH_FRAME_DURATION);
            if(frameIndex >= animations.deathFrames.length)
                frameIndex = animations.deathFrames.length-1;
                frame = animations.deathFrames[frameIndex];
            }     
        else if(currentState == EnemyState.WALKING){
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.walkFrames.length;
            frame = animations.walkFrames[frameIndex];
        }
        else if(currentState == EnemyState.FLYING){
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.flyFrames.length;
            frame = animations.flyFrames[frameIndex];
        }
        else if(currentState == EnemyState.DRILL){
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.drillFrames.length;
            frame = animations.drillFrames[frameIndex];
        }

        else if(currentState == EnemyState.HURT){
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.hurtFrames.length;
            frame = animations.hurtFrames[frameIndex];
        }

        else if(currentState == EnemyState.ATTACK){
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.attackFrames.length;
            frame = animations.attackFrames[frameIndex];
        }

        else if(currentState == EnemyState.CHARGING){
            frameIndex =(int)(stateTime/0.06f) % animations.chargeFrames.length;
            frame = animations.chargeFrames[frameIndex];
        }

        else{
            frameIndex = (int)(stateTime / IDLE_FRAME_DURATION) % animations.idleFrames.length;
            frame = animations.idleFrames[frameIndex];
        }

        float scaleX = drawWidth / 1024f;
        float scaleY = drawHeight/1024f;

        float drawX = position.x - animations.originX * scaleX;
        float drawY = position.y - animations.originY * scaleY;

        float rotationOriginX = animations.originX * scaleX;
        float rotationOriginY = animations.originY * scaleY;
        float rotation = 0f;
        float renderScaleX;
        if(type == EnemyType.HOVER_DRONE)
            renderScaleX = 1f;
        else
            renderScaleX = facingRight ? 1f : -1f;
        if(currentState == EnemyState.DRILL){
            rotation = drillRotation;
            renderScaleX = 1f;
        }

        batch.draw(frame, drawX, drawY,rotationOriginX, rotationOriginY, drawWidth, drawHeight, renderScaleX,1f, rotation);
        drawHealthBar(batch);
        if(type == EnemyType.HEAVY && currentState != EnemyState.DEAD && currentState != EnemyState.EXPLODING)
            drawHeavyCloseRangeArm(batch,drawX,drawY,drawWidth,drawHeight,frameIndex);
        
        if(type == EnemyType.RIFLEMAN && currentState != EnemyState.DEAD && currentState != EnemyState.EXPLODING)
              drawRiflemanArm(batch,drawX,drawY,drawWidth,drawHeight,frameIndex);
    }
    

    public float getHitPoints(){
        return hitPoints;
    }

    public EnemyType getType(){
        return type;
    }

    public EnemyState getCurrentState(){
        return currentState;
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
        if(type == EnemyType.HOVER_DRONE)
            barWidth = 60f;
        float barHeight = barWidth * (48f / 256f);
        Rectangle worldHurtbox = getWorldHurtbox();

        float barX = worldHurtbox.x + worldHurtbox.width/2f - barWidth/2f;
        float barY = worldHurtbox.y + worldHurtbox.height + 10f;

        if(type == EnemyType.DRILL_CRAWLER && currentState == EnemyState.DRILL){
            float radians = drillRotation*MathUtils.degreesToRadians;
            float cos = MathUtils.cos(radians);
            float sin = MathUtils.sin(radians);

            float left = hurtbox.x;
            float right = hurtbox.x+hurtbox.width;
            float bottom = hurtbox.y;
            float top = hurtbox.y+hurtbox.height;

            float x1 = left*cos-bottom*sin;
            float y1 = left*sin+bottom*cos;

            float x2 = right*cos-bottom*sin;
            float y2 = right*sin+bottom*cos;

            float x3 = right*cos-top*sin;
            float y3 = right*sin+top*cos;

            float x4 = left*cos-top*sin;
            float y4 = left*sin+top*cos;

            float minX = Math.min(Math.min(x1,x2),Math.min(x3,x4));
            float maxX = Math.max(Math.max(x1,x2),Math.max(x3,x4));
            float maxY = Math.max(Math.max(y1,y2),Math.max(y3,y4));

            barX = position.x+(minX+maxX)/2f-barWidth/2f;
            barY = position.y+maxY+10f;
        }
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

    private boolean resolveHorizontalCollision(float dt,Array<Rectangle> solidPlatforms){
        float oldX = position.x;
        position.x += velocity.x * dt;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if(!worldHurtbox.overlaps(rectangle))
                continue;
            if(type == EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING){
                currentState = EnemyState.DRILL;
                position.x = oldX;
                return true;
            }
            position.x = oldX;
            velocity.x = 0;
            return true;
        }
        return false;
    }

    private boolean resolveVerticalCollision(float dt, Array<Rectangle> solidPlatforms){
        float oldY = position.y;
        position.y += velocity.y * dt;
        Rectangle worldHurtbox = getWorldHurtbox();
        for(Rectangle rectangle: solidPlatforms){
            if(!worldHurtbox.overlaps(rectangle))
                continue;
            if(type == EnemyType.DRILL_CRAWLER && currentState == EnemyState.WALKING){
                currentState = EnemyState.DRILL;
                position.y = oldY;
                return true;
            }
            position.y = oldY;
            velocity.y = 0;
            return true;
        }
        return false;
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



public void updateEnemy(float dt, Vector2 playerPosition,Array<Rectangle> solidPlatforms, Rectangle playerHurtbox,Vector2 playerVelocity,Rectangle heavyPortalBounds){
    updateLingeringDamages(dt);
    stateTime+= dt;
    if(currentState == EnemyState.DEAD){
        if(!readyTobeRemoved){
            if(stateTime >= DEATH_FRAME_DURATION * animations.deathFrames.length){
                Rectangle hurtbox = getWorldHurtbox();
                enemyDeathLocation.x = hurtbox.x + hurtbox.width/2;
                enemyDeathLocation.y = hurtbox.y + hurtbox.height/2;
                readyTobeRemoved = true;
                
            }
            return;
        }
        return;
    }
    boolean playerFoundInsideDetectionRadius;
    boolean playerFoundInsideAttackRadius;
    float dx,dy;
    float headX = position.x;
    if(type == EnemyType.DRILL_CRAWLER){
        if(facingRight)
            headX = position.x + DRILL_HEAD_OFFSET_X;
        else
            headX = position.x - DRILL_HEAD_OFFSET_X;

        dx = playerPosition.x - headX;
        dy = playerPosition.y - position.y;
        float playerCenterX = playerHurtbox.x + playerHurtbox.width / 2f;
        float distanceSquared = dx * dx + dy * dy;
        playerFoundInsideDetectionRadius = distanceSquared <= detectionRadius * detectionRadius;
        Rectangle drillerWorldHurtbox = getWorldHurtbox();
        float drillerCenterX = drillerWorldHurtbox.x + drillerWorldHurtbox.width/2f;
        boolean horizontalAttackRange = Math.abs(playerCenterX-drillerCenterX) <= drillerWorldHurtbox.width/2f + attackRange;
        boolean verticalOverlap = playerHurtbox.y < drillerWorldHurtbox.y + drillerWorldHurtbox.height && playerHurtbox.y + playerHurtbox.height > drillerWorldHurtbox.y;
        playerFoundInsideAttackRadius = horizontalAttackRange && verticalOverlap;
    }
    else{
        dx = playerPosition.x - headX;
        dy = playerPosition.y - position.y;
        float distanceSquared = dx * dx + dy * dy;
        playerFoundInsideDetectionRadius = distanceSquared <= detectionRadius * detectionRadius;
        playerFoundInsideAttackRadius = distanceSquared <= attackRange * attackRange;
    }
    
    if(type==EnemyType.HEAVY){
        updateHeavy(playerFoundInsideAttackRadius,dt,playerHurtbox, solidPlatforms, playerVelocity,heavyPortalBounds);
    }
    else if(type==EnemyType.RIFLEMAN)
        updateRifleman(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt,playerHurtbox, solidPlatforms);

    else if(type==EnemyType.DRILL_CRAWLER)
        updateDrillCrawler(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt,playerPosition, solidPlatforms);
    
    else if(type == EnemyType.HOVER_DRONE)
        updateHoverDrone(playerFoundInsideDetectionRadius,playerFoundInsideAttackRadius,dt ,playerPosition , playerHurtbox, solidPlatforms);
}


private void updateDrillCrawler(boolean playerFoundInsideDetectionRadius,boolean playerFoundInsideAttackRadius,float dt,Vector2 playerPos,Array<Rectangle> solidPlatforms){
    if(currentState == EnemyState.DRILL){
        finishDrilling(dt,playerPos,solidPlatforms);
        return;
    }

    if(playerFoundInsideAttackRadius){
        currentState = EnemyState.ATTACK;
        pendingDamage+=DRILL_DAMAGE * dt;
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
    float facingDeadZone = 2f;
    float dx = playerPos.x - position.x;
    if(dx > facingDeadZone)
        facingRight = true;
    else if(dx < -facingDeadZone)
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
        if(playerFoundInsideAttackRadius){
            Rectangle riflemanWorldHurtbox = getWorldHurtbox();
            Vector2 diffVector = new Vector2(playerHurtbox.x + playerHurtbox.width/2,playerHurtbox.y + playerHurtbox.height/2).sub(riflemanWorldHurtbox.x + riflemanWorldHurtbox.width/2,riflemanWorldHurtbox.y + riflemanWorldHurtbox.height/2);
            RIFLEMAN_AIM_ANGLE = diffVector.angleDeg();
            riflemanAttack(dt);
        }
        else
            riflemanMoveTowardsPlayer(playerHurtbox,dt);
    else if(isPlayerInLifeofSight(playerHurtbox,solidPlatforms) && !scouting)
        currentState = EnemyState.IDLE;
    else
        Scouting(dt);
}

private void riflemanMoveTowardsPlayer(Rectangle playerHurtbox, float dt){
    scouting = false;
    attacking = false;
    currentState = EnemyState.IDLE;
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
    else{
        position.x = newPos;
        currentState = EnemyState.WALKING;
    }
}

private void riflemanAttack(float dt){
    attacking = true;
    scouting = false;
    if(attacking){
        currentState = EnemyState.ATTACK;
        RIFLEMAN_SHOT_TIMER+=dt;

        if(RIFLEMAN_SHOT_TIMER >= RIFLEMAN_FIRE_INTERVAL && !RIFLEMAN_SHOT_READY){
            RIFLEMAN_SHOT_READY = true;
            RIFLEMAN_SHOT_TIMER = 0;
        }
    }
}

public boolean isRiflemanShotReady(){
    return RIFLEMAN_SHOT_READY;
}

public void consumeRiflemanShot(){
    RIFLEMAN_SHOT_READY = false;
}

public Vector2 getRiflemanNozzlePosition(){
    if(type == EnemyType.RIFLEMAN)
        return nozzlePosition;
    return null;
}

public Vector2 getHeavyNozzlePosition(){
     if(type == EnemyType.HEAVY)
        return nozzlePosition;
    return null;
}

private void Scouting(float dt){
    if(homePlatform == null){
        velocity.x = 0;
        currentState = EnemyState.IDLE;
        return;
    }
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

public float getStateTime(){
    return stateTime;
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

private void updateHoverDrone(boolean playerFoundInsideDetectionRadius,boolean playerFoundInsideAttackRadius,float dt,Vector2 playerPos, Rectangle playerHurtbox, Array<Rectangle> solidPlatforms){
    if(targetNeverSeen)
        currentState = EnemyState.IDLE;
    else 
        droneChase(dt,playerHurtbox,solidPlatforms);
}


private void droneChase(float dt,Rectangle playerHurtbox,Array<Rectangle> solidPlatforms){
    targetNeverSeen = false;
    Vector2 diffVector = new Vector2(playerHurtbox.x + playerHurtbox.width / 2,playerHurtbox.y + playerHurtbox.height / 2).sub(position);
    if(diffVector.len() <= attackRange){
        velocity.x = 0;
        velocity.y = 0;
        droneAttack(playerHurtbox,dt);
        return;
    }
    currentState = EnemyState.FLYING;
    Vector2 direction = diffVector.nor();
    velocity.x = direction.x * moveSpeed;
    velocity.y = direction.y * moveSpeed;

    resolveHorizontalCollision(dt, solidPlatforms);
    resolveVerticalCollision(dt, solidPlatforms);
}

private void droneAttack(Rectangle playerHurtbox, float dt){
    Rectangle hurtbox = getWorldHurtbox();
    Vector2 diffVector = new Vector2(((playerHurtbox.x + playerHurtbox.width/2)-(hurtbox.x + hurtbox.width/2)), ((playerHurtbox.y+playerHurtbox.height/2) - (hurtbox.y + hurtbox.height/2)));
    droneBeamMagnitude = diffVector.len();
    droneBeamAngleDegrees = diffVector.angleDeg(); 
    if(DRONE_ATTACK_TIMER > DRONE_ATTACK_DURATION_LIMIT){
        DRONE_COOLDOWN = true; 
        stateTime = 0;
    }
    if(!DRONE_COOLDOWN){
        DRONE_COOLDOWN_TIMER = 0;
        currentState = EnemyState.ATTACK;
        DRONE_ATTACK_TIMER+=dt;
        if(DRONE_ATTACK_TIMER >= DRONE_LOCK_TIME && !DRONE_LOCK_SENT){
            HEAVY_LOCK = true;
            DRONE_LOCK_SENT = true;
        }
        pendingDamage+=DRONE_DAMAGE * dt;
    }
    else{
        currentState = EnemyState.CHARGING;
        DRONE_ATTACK_TIMER=0;
        DRONE_COOLDOWN_TIMER += dt;
        if(DRONE_COOLDOWN_TIMER > 2.5f){
            DRONE_COOLDOWN = false;
            DRONE_LOCK_TIME = MathUtils.random(0.5f,4f);
            DRONE_LOCK_SENT = false;
            stateTime = 0;
        }
    }
}


public float getDroneBeamMagnitude(){
    return droneBeamMagnitude;
}

public float getDroneBeamAngleDegrees(){
    return droneBeamAngleDegrees;
}

public Vector2 getDroneCenter(){
    Rectangle hurtbox = getWorldHurtbox();
    return new Vector2(hurtbox.x + hurtbox.width / 2f,hurtbox.y + hurtbox.height / 2f);
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

private void updateHeavy(boolean playerFoundInsideAttackRadius,float dt,Rectangle playerHurtbox, Array<Rectangle> solidPlatforms,Vector2 playerVelocity,Rectangle heavyPortalBounds){
    if(HEAVY_RANGE_ATTACK_ACTIVE){
        heavyRangeAttack(playerHurtbox,dt,solidPlatforms);
        if(HEAVY_RANGE_ATTACK_COMPLETED){
            HEAVY_RANGE_ATTACK_ACTIVE = false;
            HEAVY_RANGE_ATTACK_COMPLETED = false;
            HEAVY_PORTAL_ATTACK_CHOSEN = false;
            HEAVY_PORTAL_FX_TIMER = 0;
        }
        return;
    }

    if(HEAVY_CLOSE_SHOT_ONCE){
        currentState = EnemyState.IDLE;
        HEAVY_CLOSE_TIMER += dt;

    if(HEAVY_CLOSE_TIMER < 0.8f)
        currentState = EnemyState.ATTACK;
    else
        currentState = EnemyState.IDLE;

        if(HEAVY_CLOSE_TIMER >= HEAVY_CLOSE_COOLDOWN){
            HEAVY_CLOSE_SHOT_ONCE = false;
            HEAVY_CLOSE_TIMER = 0;
        }
    }

    if(HEAVY_LOCK){
        openPortal(playerHurtbox,dt,playerVelocity,solidPlatforms,heavyPortalBounds);
        return;
    }

    if(playerFoundInsideAttackRadius){
        nozzlePosition = setNozzle(playerHurtbox.x + playerHurtbox.width/2);
        if(isPlayerInLifeofSight(playerHurtbox,solidPlatforms)){
            heavyCloseRangeAttack(playerHurtbox,dt);
            return;
        }
        Scouting(dt);
        currentState = EnemyState.WALKING;
        return;
    }
    currentState = EnemyState.IDLE;
}

private void heavyCloseRangeAttack(Rectangle playerHurtbox,float dt){
    Rectangle hurtbox = getWorldHurtbox();
    Vector2 diffVector = new Vector2(playerHurtbox.x + playerHurtbox.width / 2,playerHurtbox.y + playerHurtbox.height / 2).sub(hurtbox.x + hurtbox.width/2, hurtbox.y+hurtbox.height/2);
    HEAVY_CLOSE_ANGLE = diffVector.angleDeg(); 
    float playerCenterX = playerHurtbox.x + playerHurtbox.width/2;
    float heavyCenterX = hurtbox.x + hurtbox.width/2;
    facingRight = playerCenterX >= heavyCenterX;
    if(HEAVY_CLOSE_SHOT_ONCE){
        return;
    }
    currentState = EnemyState.ATTACK;
    stateTime = 0;
    HEAVY_SHOT_READY = true;
    HEAVY_CLOSE_SHOT_ONCE = true;

}


public boolean isHeavyShotReady(){
    return HEAVY_SHOT_READY;
}

public void consumeHeavyShot(){
    HEAVY_SHOT_READY = false;
}

private void openPortal(Rectangle playerHurtbox, float dt, Vector2 playerVelocity, Array<Rectangle> solidPlatforms,Rectangle heavyPortalBounds){
    HEAVY_LOCK_TIMER+=dt;
    if(HEAVY_LOCK_TIMER >= RANDOM_LIMIT){
        HEAVY_PREDICTED_RAIN_FIRE_POSITION.x =MathUtils.clamp(HEAVY_PREDICTED_RAIN_FIRE_POSITION.x,heavyPortalBounds.x,heavyPortalBounds.x + heavyPortalBounds.width);
        HEAVY_PREDICTED_RAIN_FIRE_POSITION.y =MathUtils.clamp(HEAVY_PREDICTED_RAIN_FIRE_POSITION.y,heavyPortalBounds.y,heavyPortalBounds.y + heavyPortalBounds.height);
        
        HEAVY_LOCK = false;
        HEAVY_LEFT_TARGET = false;
        HEAVY_LOCK_TIMER = 0;
        RANDOM_LIMIT = MathUtils.random(2f,7f);
        RANDOM_LOCK_LIMIT = MathUtils.random(0.85f,(RANDOM_LIMIT-0.2f));
        HEAVY_RANGE_ATTACK_ACTIVE = true;
    }

    if(HEAVY_RANGE_ATTACK_ACTIVE){
        heavyRangeAttack(playerHurtbox,dt,solidPlatforms);
        return;
    }

    if(HEAVY_LOCK_TIMER + RANDOM_LOCK_LIMIT >=  RANDOM_LIMIT && !HEAVY_LEFT_TARGET){
        HEAVY_LEFT_TARGET = true;
        float TargetX = playerHurtbox.x + playerHurtbox.width/2;
        float TargetY = playerHurtbox.y + playerHurtbox.height/2;

        float predictedX = TargetX + playerVelocity.x * (RANDOM_LIMIT-HEAVY_LOCK_TIMER);
        float predictedY =TargetY + playerVelocity.y * (RANDOM_LIMIT-HEAVY_LOCK_TIMER);

        predictedX = MathUtils.clamp(predictedX,heavyPortalBounds.x,heavyPortalBounds.x + heavyPortalBounds.width);
        predictedY = MathUtils.clamp(predictedY,heavyPortalBounds.y,heavyPortalBounds.y + heavyPortalBounds.height);
        HEAVY_PREDICTED_RAIN_FIRE_POSITION.set(predictedX,predictedY);
        return;
    }
}

private void heavyRangeAttack(Rectangle playerHurtbox, float dt, Array<Rectangle> solidPlatforms){
    HEAVY_PORTAL_FX_TIMER+=dt;
    if(!HEAVY_PORTAL_ATTACK_CHOSEN){
       RANDOM_SELECT_ATTACK = (int) MathUtils.random(1,3);
       HEAVY_PORTAL_ATTACK_CHOSEN = true;
    }

    if(!HEAVY_RANGE_ATTACK_COMPLETED){
        if(RANDOM_SELECT_ATTACK  == 1){
            HEAVY_POISION_ATTACK(playerHurtbox, dt);
        }
        else if(RANDOM_SELECT_ATTACK == 2){
            HEAVY_BOMBARDMENT_ATTACK(playerHurtbox, dt, solidPlatforms);
        }
        else if(RANDOM_SELECT_ATTACK == 3){
            HEAVY_STUN_ATTACK(playerHurtbox, dt);
        }
    }
}

private void HEAVY_POISION_ATTACK(Rectangle playerHurtbox, float dt){
    Circle posionCircle = new Circle(HEAVY_PREDICTED_RAIN_FIRE_POSITION.x,HEAVY_PREDICTED_RAIN_FIRE_POSITION.y,POISON_RADIUS);
    if(HEAVY_LONG_RANGE_ATTACK_DURATION < POISON_AREA_LIFETIME){
        HEAVY_LONG_RANGE_ATTACK_DURATION += dt;
        if(Intersector.overlaps(posionCircle,playerHurtbox)){
            PLAYER_INSIDE_POISON = true;
            PLAYER_POISIONED = true;
            POISON_LINGERING_DURATION = 0;
            pendingDamage+=POISON_DAMAGE * dt;
            return;
        }
        PLAYER_INSIDE_POISON = false;
    }
    else{
        HEAVY_LONG_RANGE_ATTACK_DURATION = 0;
        HEAVY_RANGE_ATTACK_COMPLETED = true;
        PLAYER_INSIDE_POISON = false;
    }
}

private void HEAVY_BOMBARDMENT_ATTACK(Rectangle playerHurtbox, float dt, Array<Rectangle> solidPlatforms){
    BOMBARDMENT_Y = HEAVY_PREDICTED_RAIN_FIRE_POSITION.y + 350f;
    BOMBARDMENT_START_X = HEAVY_PREDICTED_RAIN_FIRE_POSITION.x - BOMBARDMENT_RADIUS;
    BOMBARDMENT_END_X = HEAVY_PREDICTED_RAIN_FIRE_POSITION.x + BOMBARDMENT_RADIUS - PROJECTILE_WIDTH;
    HEAVY_LONG_RANGE_ATTACK_DURATION+=dt;
    BOMBARDMENT_SPAWN_TIMER += dt;
    if(BOMBARDMENT_PROJECTILES_SPAWNED < 8 && BOMBARDMENT_SPAWN_TIMER >= 0.35f){
        Rectangle projectile = new Rectangle(MathUtils.random(BOMBARDMENT_START_X , BOMBARDMENT_END_X),BOMBARDMENT_Y,PROJECTILE_WIDTH,PROJECTILE_HEIGHT);
        BOMBARDMENT_PROJECTILES.add(projectile);
        BOMBARDMENT_PROJECTILES_SPAWNED++;
        BOMBARDMENT_SPAWN_TIMER = 0;
    }
    
    for(int i = BOMBARDMENT_PROJECTILES.size-1;i>=0;i--){
        Rectangle object = BOMBARDMENT_PROJECTILES.get(i);
        object.y -= BOMBARDMENT_SPEED*dt;
        if(object.y + object.height < 0){
            BOMBARDMENT_PROJECTILES.removeIndex(i);
            continue;
        }
        boolean hitPlatform = false;

        for(Rectangle platform : solidPlatforms){
            if(object.overlaps(platform)){
                BOMBARDMENT_IMPACT_EVENTS.add(new Vector2(object.x+object.width/2f,object.y));
                hitPlatform = true;
                break;
            }
        }

        if(hitPlatform){
            BOMBARDMENT_PROJECTILES.removeIndex(i);
            continue;
        }

        if(object.overlaps(playerHurtbox)){
            BOMBARDMENT_IMPACT_EVENTS.add(new Vector2(object.x+object.width/2f,object.y));
            pendingDamage+=BOMBARDMENT_DAMAGE;
            BOMBARDMENT_PROJECTILES.removeIndex(i);

        }
    }
    
    if(BOMBARDMENT_PROJECTILES_SPAWNED >= 8 && BOMBARDMENT_PROJECTILES.size == 0){
        HEAVY_RANGE_ATTACK_COMPLETED = true;
        HEAVY_LONG_RANGE_ATTACK_DURATION = 0;
        BOMBARDMENT_PROJECTILES_SPAWNED = 0;
        BOMBARDMENT_SPAWN_TIMER = 0;
    }
}

public Vector2 consumeBombardmentImpact(){
    if(BOMBARDMENT_IMPACT_EVENTS.size == 0)
        return null;
    return BOMBARDMENT_IMPACT_EVENTS.removeIndex(0);
}

private void  HEAVY_STUN_ATTACK(Rectangle playerHurtbox, float dt){
    float portalOpenDuration = 0.64f;
    float chargeDuration = 0.40f;
    float pulseDuration = 0.60f;
    float portalCloseDuration = 0.56f;

    float pulseStart = portalOpenDuration+chargeDuration;
    float pulseEnd = pulseStart+pulseDuration;
    float attackEnd = pulseEnd+portalCloseDuration;

    HEAVY_LONG_RANGE_ATTACK_DURATION+=dt;

    if(HEAVY_LONG_RANGE_ATTACK_DURATION >= pulseStart && HEAVY_LONG_RANGE_ATTACK_DURATION < pulseEnd){
        Circle stunCircle = new Circle(HEAVY_PREDICTED_RAIN_FIRE_POSITION.x,HEAVY_PREDICTED_RAIN_FIRE_POSITION.y,STUN_RADIUS);
        if(Intersector.overlaps(stunCircle,playerHurtbox) && !PLAYER_STUNNED){
            PLAYER_STUNNED = true;
            STUN_LINGERING_DURATION = 0;
        }
    }
    if(HEAVY_LONG_RANGE_ATTACK_DURATION >= attackEnd){
        HEAVY_LONG_RANGE_ATTACK_DURATION = 0;
        HEAVY_RANGE_ATTACK_COMPLETED = true;
    }
}

private void updateLingeringDamages(float dt){
    if(!PLAYER_INSIDE_POISON  && PLAYER_POISIONED){
        if(POISON_LINGERING_DURATION < POISON_LINGERING_LIFETIME){
            pendingDamage += POISON_LINGERING_DAMAGE * dt;
            POISON_LINGERING_DURATION+=dt;
        }
        else{
            POISON_LINGERING_DURATION = 0;
            PLAYER_POISIONED= false;
        }
    }

    if(PLAYER_STUNNED){
        if(STUN_LINGERING_DURATION < STUN_LINGERING_LIFETIME){
            STUN_LINGERING_DURATION+= dt;
        }
        else{
            STUN_LINGERING_DURATION = 0;
            PLAYER_STUNNED = false;
        }
    }
}

private void drawHeavyCloseRangeArm(SpriteBatch batch,float bodyX,float bodyY,float bodyWidth,float bodyHeight,int frameIndex){
    TextureRegion armFrame;
    float[] socketX;
    float[] socketY;
    float[] baseRotation;

    if(currentState == EnemyState.ATTACK){
        frameIndex %= animations.armAttackFrames.length;
        armFrame = animations.armAttackFrames[frameIndex];

        socketX = new float[]{236f,236f,231.344113f,232.5058f,233.66902f,234.60054f,235.533053f,236f};
        socketY = new float[]{463f,463f,465.097084f,464.571043f,464.046175f,463.627069f,463.20878f,463f};
        baseRotation = new float[]{0f,0f,-0.649977f,-0.487478f,-0.324983f,-0.194978f,-0.064973f,0f};
        updateHeavyMuzzlePosition(frameIndex);
    }

    else if(currentState == EnemyState.WALKING){
        frameIndex %= animations.armWalkingFrames.length;
        armFrame = animations.armWalkingFrames[frameIndex];

        socketX = new float[]{238.722112f,239.35505f,239.820862f,239.991687f,239.820862f,239.35505f,238.722112f,238.092013f,237.633496f,237.466209f,237.633496f,238.092013f};
        socketY = new float[]{459.772695f,458.303841f,456.295018f,455.362951f,456.295018f,458.303841f,459.772695f,459.769945f,458.834151f,458.29484f,458.834151f,459.769945f};
        baseRotation = new float[]{1.199976f,1.475013f,1.676313f,1.750027f,1.676313f,1.475013f,1.199976f,0.925023f,0.723665f,0.649977f,0.723665f,0.925023f};
    }

    else if(currentState == EnemyState.HURT){
        frameIndex %= animations.armHurtFrames.length;
        armFrame = animations.armHurtFrames[frameIndex];

        socketX = new float[]{236f,233.618674f,228.333925f,224.162796f,224.162796f,228.333925f,233.618674f,236f};
        socketY = new float[]{463f,465.385339f,470.797151f,475.186846f,475.186846f,470.797151f,465.385339f,463f};
        baseRotation = new float[]{0f,-0.564774f,-1.833778f,-2.85147f,-2.85147f,-1.833778f,-0.564774f,0f};
    }

    else{
        frameIndex %= animations.armIdleFrames.length;
        armFrame = animations.armIdleFrames[frameIndex];

        socketX = new float[]{236f,236.168094f,236.291391f,236.336622f,236.291391f,236.168094f,236f,235.832104f,235.709205f,235.664645f,235.709205f,235.832104f};
        socketY = new float[]{463f,462.118061f,461.472432f,461.236673f,461.472432f,462.118061f,463f,463.882026f,464.527829f,464.764602f,464.527829f,463.882026f};
        baseRotation = new float[]{0f,0.075f,0.12989f,0.15f,0.12989f,0.075f,0f,-0.075f,-0.12989f,-0.15f,-0.12989f,-0.075f};
    }

    float scaleX = bodyWidth/1024f;
    float scaleY = bodyHeight/1024f;

    float socketOffsetX = (socketX[frameIndex]-animations.originX)*scaleX;
    float armSocketWorldX;

    if(facingRight)
        armSocketWorldX = position.x+socketOffsetX;
    else
        armSocketWorldX = position.x-socketOffsetX;

    float armSocketWorldY =
        position.y+
        ((1024f-socketY[frameIndex])-animations.originY)*scaleY;

    float armOriginX = 256f*scaleX;
    float armOriginY = 512f*scaleY;

    float armX = armSocketWorldX-armOriginX;
    float armY = armSocketWorldY-armOriginY;

    float armRotation;

    if(currentState == EnemyState.ATTACK || HEAVY_CLOSE_SHOT_ONCE)
        armRotation = HEAVY_CLOSE_ANGLE;
    else if(facingRight)
        armRotation = baseRotation[frameIndex];
    else
        armRotation = 180f-baseRotation[frameIndex];

    float armScaleY = facingRight ? 1f : -1f;
    batch.draw(armFrame, armX, armY, armOriginX, armOriginY, bodyWidth,bodyHeight,1f,armScaleY,armRotation);
}

private void drawRiflemanArm(SpriteBatch batch,float bodyX,float bodyY,float bodyWidth,float bodyHeight,int frameIndex){
    TextureRegion armFrame;
    float[] socketX;
    float[] socketY;
    float[] baseRotation;

    if(currentState == EnemyState.ATTACK){
        frameIndex %= animations.armAttackFrames.length;
        armFrame = animations.armAttackFrames[frameIndex];

        socketX = new float[]{380.50f,378.61f,376.43f,378.79f,378.66f,380.50f};
        socketY = new float[]{475.92f,476.25f,476.77f,476.34f,476.13f,475.92f};
        baseRotation = new float[]{0f,-0.40f,-1.00f,-0.50f,-0.25f,0f};

        updateRiflemanMuzzlePosition(frameIndex);
    }

    else if(currentState == EnemyState.WALKING){
        frameIndex %= animations.armWalkingFrames.length;
        armFrame = animations.armWalkingFrames[frameIndex];

        socketX = new float[]{383.03f,384.49f,384.49f,383.03f,381.57f,381.57f};
        socketY = new float[]{474.95f,471.94f,471.94f,474.95f,473.03f,473.03f};
        baseRotation = new float[]{1.20f,1.89f,1.89f,1.20f,0.51f,0.51f};
    }

    else if(currentState == EnemyState.HURT){
        frameIndex %= animations.armHurtFrames.length;
        armFrame = animations.armHurtFrames[frameIndex];

        socketX = new float[]{380.50f,372.23f,365.54f,370.73f,376.73f,380.50f};
        socketY = new float[]{475.92f,479.27f,482.20f,479.91f,477.41f,475.92f};
        baseRotation = new float[]{0f,-2.59f,-4.70f,-3.05f,-1.17f,0f};
    }

    else{
        frameIndex %= animations.armIdleFrames.length;
        armFrame = animations.armIdleFrames[frameIndex];

        socketX = new float[]{380.50f,380.77f,380.77f,380.50f,380.23f,380.23f};
        socketY = new float[]{475.92f,474.50f,474.50f,475.92f,477.34f,477.34f};
        baseRotation = new float[]{0f,0.13f,0.13f,0f,-0.13f,-0.13f};
    }

    float bodyScaleX = bodyWidth/1024f;
    float bodyScaleY = bodyHeight/1024f;

    float socketOffsetX = (socketX[frameIndex]-animations.originX)*bodyScaleX;

    float armSocketWorldX;

    if(facingRight)
        armSocketWorldX = position.x+socketOffsetX;
    else
        armSocketWorldX = position.x-socketOffsetX;

    float armSocketWorldY = bodyY+(1024f-socketY[frameIndex])*bodyScaleY;

    float armOriginX = 256f*bodyScaleX;
    float armOriginY = 512f*bodyScaleY;

    float armX = armSocketWorldX-armOriginX;
    float armY = armSocketWorldY-armOriginY;

    float armRotation;

    if(currentState == EnemyState.ATTACK)
        armRotation = RIFLEMAN_AIM_ANGLE;
    else if(facingRight)
        armRotation = baseRotation[frameIndex];
    else
        armRotation = 180f-baseRotation[frameIndex];

    float armScaleY = facingRight ? 1f : -1f;
    batch.draw(armFrame, armX, armY, armOriginX, armOriginY, bodyWidth, bodyHeight, 1f, armScaleY, armRotation);
}

private void calculateWeaponMuzzle(Vector2 output,float socketX,float socketY,float muzzleOffsetX,float muzzleOffsetY,float angleDegrees){
    float scaleX = drawWidth/1024f;
    float scaleY = drawHeight/1024f;
    float socketOffsetX = (socketX-animations.originX)*scaleX;
    float socketWorldX;

    if(facingRight)
        socketWorldX = position.x+socketOffsetX;
    else
        socketWorldX = position.x-socketOffsetX;

    float socketWorldY =position.y+((1024f-socketY)-animations.originY)*scaleY;
    float localX = muzzleOffsetX*scaleX;
    float localY = muzzleOffsetY*scaleY;

    if(!facingRight)
        localY = -localY;

    float cos = MathUtils.cosDeg(angleDegrees);
    float sin = MathUtils.sinDeg(angleDegrees);

    float rotatedX = localX*cos-localY*sin;
    float rotatedY = localX*sin+localY*cos;

    output.set(socketWorldX+rotatedX,socketWorldY+rotatedY);
}

private void updateRiflemanMuzzlePosition(int frameIndex){
    calculateWeaponMuzzle(
        RIFLEMAN_MUZZLE_POSITION,
        RIFLEMAN_ATTACK_SOCKET_X[frameIndex],
        RIFLEMAN_ATTACK_SOCKET_Y[frameIndex],
        RIFLEMAN_ATTACK_MUZZLE_X[frameIndex],
        RIFLEMAN_ATTACK_MUZZLE_Y[frameIndex],
        RIFLEMAN_AIM_ANGLE);
}

private void updateHeavyMuzzlePosition(int frameIndex){
    calculateWeaponMuzzle(
        HEAVY_MUZZLE_POSITION,
        HEAVY_ATTACK_SOCKET_X[frameIndex],
        HEAVY_ATTACK_SOCKET_Y[frameIndex],
        HEAVY_ATTACK_MUZZLE_X[frameIndex],
        HEAVY_ATTACK_MUZZLE_Y[frameIndex],
        HEAVY_CLOSE_ANGLE);
}

public Vector2 getRiflemanMuzzlePosition(){
    return RIFLEMAN_MUZZLE_POSITION;
}

public Vector2 getHeavyMuzzlePosition(){
    return HEAVY_MUZZLE_POSITION;
}

public boolean isHeavyLocking(){
    return HEAVY_LOCK;
}

public boolean isHeavyLeftTarget(){
    return HEAVY_LEFT_TARGET;
}

public boolean isHeavyRangeAttackActive(){
    return HEAVY_RANGE_ATTACK_ACTIVE;
}

public Vector2 getHeavyPredictedRainFirePosition(){
    return HEAVY_PREDICTED_RAIN_FIRE_POSITION;
}

public float getHeavyLockTimer(){
    return HEAVY_LOCK_TIMER;
}

public int getHeavySelectedAttack(){
    return RANDOM_SELECT_ATTACK;
}

public void setHeavyLock(boolean value){
    HEAVY_LOCK = value;
}

public float getPendingDamage(){
    return pendingDamage;
}

public void resetPendingDamage(){
    pendingDamage = 0;
}

public float getHeavyPortalFxTimer(){
    return HEAVY_PORTAL_FX_TIMER;
}

public float getHeavyCloseDamage(){
    return HEAVY_CLOSE_RANGE_DAMAGE;
}

public float getRiflemanDamage(){
    return RIFLEMAN_DAMAGE;
}

public static TextureRegion getHealthBarFrame(float healthRatio){
    healthRatio = MathUtils.clamp(healthRatio,0f,1f);
    int frameIndex = 11-(int)Math.ceil(11f*healthRatio);
    return healthBarFrames[frameIndex];
}


public boolean isPlayerPoisoned(){
    return PLAYER_POISIONED;
}

public boolean isPlayerStunned(){
    return PLAYER_STUNNED;
}

public Array<Rectangle> getBombardmentProjectiles(){
    return BOMBARDMENT_PROJECTILES;
}

public boolean isOnPlatform(Rectangle platform){
    Rectangle hurtbox = getWorldHurtbox();
    float enemyFeetY = hurtbox.y;
    float enemyCenterX = hurtbox.x + hurtbox.width/2f;
    boolean withinX = enemyCenterX >= platform.x && enemyCenterX <= platform.x + platform.width;
    boolean onTop = Math.abs(enemyFeetY - (platform.y + platform.height)) < 8f;

    return withinX && onTop;
}

public Vector2 getEnemyDeathLocation(){
    return enemyDeathLocation;
}


public static void main(String[] args){

    }
}

