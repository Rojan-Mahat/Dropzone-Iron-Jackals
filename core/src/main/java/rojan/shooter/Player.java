package rojan.shooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;



public class Player{
    private Vector2 position;
    private float width;
    private float height;
    private Vector2 gunHoldingPositionOffset;

    private Vector2 velocity;
    private float moveSpeed;

    private boolean isRunning;


    private Rectangle movementOffset;
    private Rectangle crouchMovementOffset;

    private boolean isCrouching;
    private boolean isGrounded;
    private boolean isJetPackActive;

    private float maxRiseSpeed = 550;
    private float maxFallSpeed = -800;

    private enum PlayerState{
                    IDLE,
                    IDLE_AIM,
                    WALK,
                    WALK_SHOOT,
                    RUNNING,
                    RUNNING_SHOOT,
                    CROUCH,
                    CROUCH_AIM,
                    CROUCH_SHOOT,
                    JUMP,
                    JUMPSHOOT,
                    SHOOT,
                    HURT,
                    DEAD,
                    CLIMB,
                    MELEE,
                    CROUCH_MELEE,
                    JUMP_MELEE,
                    THROW,
                    CROUCH_THROW,
                    JUMP_THROW,
                    HAPPY
                }

    private PlayerState currentState;
    private PlayerState previousState;


    private enum FacingDirection{
        LEFT,
        RIGHT
    }
    private FacingDirection currentDirection;
    private float stateTime;
    private int currentFrameIndex;

    private Array<Texture> loadedTextures = new Array<>();
    private TextureRegion[] idleFrames;
    private TextureRegion[] runningFrames;
    private TextureRegion[] idleAimFrames;
    private TextureRegion[] walkFrames;
    private TextureRegion[] walkShootFrames;
    private TextureRegion[] runShootFrames;
    private TextureRegion[] crouchFrames;
    private TextureRegion[] crouchAimFrames;
    private TextureRegion[] crouchShootFrames;
    private TextureRegion[] jumpFrames;
    private TextureRegion[] jumpShootFrames;
    private TextureRegion[] shootFrames;
    private TextureRegion[] hurtFrames;
    private TextureRegion[] deadFrames;
    private TextureRegion[] climbFrames;
    private TextureRegion[] meleeFrames;
    private TextureRegion[] crouchMeleeFrames;
    private TextureRegion[] jumpMeleeFrames;
    private TextureRegion[] throwFrames;
    private TextureRegion[] crouchThrowFrames;
    private TextureRegion[] jumpThrowFrames;
    private TextureRegion[] happyFrames;

    private Weapon currentWeapon;
    private float weaponDrawAdjustmentY = 0f;

    private int jumpCount;
    private static final int MAX_JUMP_PRESSES = 3;

    private static class GripPose{
        Vector2 position;
        float angle;

        GripPose(float x, float y, float angle){
            position = new Vector2(x, y);
            this.angle = angle;
        }
    }
    private GripPose[] idleGripPoses;
    private GripPose[] idleAimGripPoses;
    private GripPose[] walkGripPoses;
    private GripPose[] walkShootGripPoses;
    private GripPose[] runningGripPoses;
    private GripPose[] runningShootGripPoses;
    private GripPose[] crouchGripPoses;
    private GripPose[] crouchAimGripPoses;
    private GripPose[] crouchShootGripPoses;
    private GripPose[] jumpGripPoses;
    private GripPose[] jumpShootGripPoses;
    private GripPose[] shootGripPoses;
    private GripPose[] hurtGripPoses;
    private GripPose[] deadGripPoses;
    private GripPose[] climbGripPoses;
    private GripPose[] meleeGripPoses;
    private GripPose[] crouchMeleeGripPoses;
    private GripPose[] jumpMeleeGripPoses;
    private GripPose[] throwGripPoses;
    private GripPose[] crouchThrowGripPoses;
    private GripPose[] jumpThrowGripPoses;
    private GripPose[] happyGripPoses;

    private EnumMap<PlayerState, GripPose[]> gripPoseMap;
    private Rectangle jetPackDrawData;
    private Texture jetpackTexture;
    private Sprite jetpackSprite; 

    private Texture jetpackFlameTexture;
    private TextureRegion[] jetpackFlameFrames;
    private float jetpackFlameStateTime;

    private float airTime;
    private boolean isShooting;

    Player(float spawnX, float spawnY, float widthfromMap, float heightfromMap){
        position = new Vector2(spawnX,spawnY);
        width = widthfromMap;
        height = heightfromMap;

        velocity = new Vector2(0,0);
        moveSpeed = 300f;
        isCrouching = false;
        currentState = PlayerState.IDLE;
        previousState = PlayerState.IDLE;

        currentDirection = FacingDirection.RIGHT;

        stateTime = 0f;

        idleFrames = loadFrames(AssetPaths.Player.Soldier1.IDLE,AssetPaths.Player.Soldier1.IDLE_FRAMES);
        idleAimFrames = loadFrames(AssetPaths.Player.Soldier1.IDLE_AIM,AssetPaths.Player.Soldier1.IDLE_AIM_FRAMES);

        walkFrames = loadFrames(AssetPaths.Player.Soldier1.WALK,AssetPaths.Player.Soldier1.WALK_FRAMES);
        walkShootFrames = loadFrames(AssetPaths.Player.Soldier1.WALK_SHOOT, AssetPaths.Player.Soldier1.WALK_SHOOT_FRAMES);

        runningFrames = loadFrames(AssetPaths.Player.Soldier1.RUN,AssetPaths.Player.Soldier1.RUN_FRAMES);
        runShootFrames = loadFrames(AssetPaths.Player.Soldier1.RUN_SHOOT,AssetPaths.Player.Soldier1.RUN_SHOOT_FRAMES);

        crouchFrames = loadFrames(AssetPaths.Player.Soldier1.CROUCH,AssetPaths.Player.Soldier1.CROUCH_FRAMES);
        crouchAimFrames = loadFrames(AssetPaths.Player.Soldier1.CROUCH_AIM,AssetPaths.Player.Soldier1.CROUCH_AIM_FRAMES);
        crouchShootFrames = loadFrames(AssetPaths.Player.Soldier1.CROUCH_SHOOT,AssetPaths.Player.Soldier1.CROUCH_SHOOT_FRAMES);

        jumpFrames = loadFrames(AssetPaths.Player.Soldier1.JUMP,AssetPaths.Player.Soldier1.JUMP_FRAMES);
        jumpShootFrames = loadFrames(AssetPaths.Player.Soldier1.JUMP_SHOOT,AssetPaths.Player.Soldier1.JUMP_SHOOT_FRAMES);

        shootFrames = loadFrames(AssetPaths.Player.Soldier1.SHOOT,AssetPaths.Player.Soldier1.SHOOT_FRAMES);

        hurtFrames = loadFrames(AssetPaths.Player.Soldier1.HURT,AssetPaths.Player.Soldier1.HURT_FRAMES);

        deadFrames = loadFrames(AssetPaths.Player.Soldier1.DEAD,AssetPaths.Player.Soldier1.DEAD_FRAMES);

        climbFrames = loadFrames(AssetPaths.Player.Soldier1.CLIMB,AssetPaths.Player.Soldier1.CLIMB_FRAMES);

        meleeFrames = loadFrames(AssetPaths.Player.Soldier1.MELEE,AssetPaths.Player.Soldier1.MELEE_FRAMES);
        crouchMeleeFrames = loadFrames(AssetPaths.Player.Soldier1.CROUCH_MELEE,AssetPaths.Player.Soldier1.CROUCH_MELEE_FRAMES);
        jumpMeleeFrames = loadFrames(AssetPaths.Player.Soldier1.JUMP_MELEE,AssetPaths.Player.Soldier1.JUMP_MELEE_FRAMES);

        throwFrames = loadFrames(AssetPaths.Player.Soldier1.THROW,AssetPaths.Player.Soldier1.THROW_FRAMES);
        crouchThrowFrames = loadFrames(AssetPaths.Player.Soldier1.CROUCH_THROW,AssetPaths.Player.Soldier1.CROUCH_THROW_FRAMES);
        jumpThrowFrames = loadFrames(AssetPaths.Player.Soldier1.JUMP_THROW,AssetPaths.Player.Soldier1.JUMP_THROW_FRAMES);

        happyFrames = loadFrames(AssetPaths.Player.Soldier1.HAPPY,AssetPaths.Player.Soldier1.HAPPY_FRAMES);

        idleGripPoses = new GripPose[idleFrames.length];
        idleAimGripPoses = new GripPose[idleAimFrames.length];

        walkGripPoses = new GripPose[walkFrames.length];
        walkShootGripPoses = new GripPose[walkShootFrames.length];

        runningGripPoses = new GripPose[runningFrames.length];
        runningShootGripPoses = new GripPose[runShootFrames.length];

        crouchGripPoses = new GripPose[crouchFrames.length];
        crouchAimGripPoses = new GripPose[crouchAimFrames.length];
        crouchShootGripPoses = new GripPose[crouchShootFrames.length];

        jumpGripPoses = new GripPose[jumpFrames.length];
        jumpShootGripPoses = new GripPose[jumpShootFrames.length];

        shootGripPoses = new GripPose[shootFrames.length];

        hurtGripPoses = new GripPose[hurtFrames.length];

        deadGripPoses = new GripPose[deadFrames.length];

        meleeGripPoses = new GripPose[meleeFrames.length];
        crouchMeleeGripPoses = new GripPose[crouchMeleeFrames.length];
        jumpMeleeGripPoses = new GripPose[jumpMeleeFrames.length];

        throwGripPoses = new GripPose[throwFrames.length];
        crouchThrowGripPoses = new GripPose[crouchThrowFrames.length];
        jumpThrowGripPoses = new GripPose[jumpThrowFrames.length];

        happyGripPoses = new GripPose[happyFrames.length];

        loadAllGripPoses();

        gripPoseMap = new EnumMap<>(PlayerState.class);
        linkGripPoseMap();

        jetpackTexture = new Texture(AssetPaths.JetPack.jetpack);
        jetpackSprite = new Sprite(jetpackTexture);

        jetpackFlameTexture = new Texture(AssetPaths.JetPack.jetpackFlames);
        TextureRegion[][] flameSheet = TextureRegion.split(jetpackFlameTexture, jetpackFlameTexture.getWidth() / 4, jetpackFlameTexture.getHeight());
        jetpackFlameFrames = flameSheet[0];
    }

    private TextureRegion[] loadFrames(String prefix, int frameCount){
        TextureRegion[] frames = new TextureRegion[frameCount];
        for(int i = 0; i < frameCount; i++){
            String frameNumber = String.format("%03d", i);
            String path = prefix + frameNumber + ".png";
            Texture texture = new Texture(path);
            loadedTextures.add(texture);
            frames[i] = new TextureRegion(texture);
        }
        return frames;
    }

    public void drawPlayer(SpriteBatch batch,float dt){
        float frameDuration = 0.083f;
        previousState = currentState;
        updatePlayerState();
        if(previousState!=currentState)
                stateTime = 0;
        else
            stateTime+=dt;

        boolean faceLeft = currentDirection == FacingDirection.LEFT;
        if(jetpackSprite.isFlipX() != faceLeft)
            jetpackSprite.flip(true, false);

        float jetPackX,jetPackY;
        
        if(currentDirection == FacingDirection.RIGHT)
            jetPackX = position.x + jetPackDrawData.x;
        else
            jetPackX =position.x + width - jetPackDrawData.x - jetPackDrawData.width;
        jetPackY  = position.y + jetPackDrawData.y;

        if(isJetPackActive)
            drawJetpackFlame(batch,dt,jetPackX,jetPackY);
        batch.draw(jetpackSprite,jetPackX,jetPackY,jetPackDrawData.width,jetPackDrawData.height);
    

        switch(currentState){
        case IDLE:
            currentFrameIndex =(int)(stateTime / frameDuration) % idleFrames.length;
            drawFrame(batch, idleFrames[currentFrameIndex], 1f, 1f);
            break;

        case IDLE_AIM:
            currentFrameIndex = (int)(stateTime / frameDuration) % idleAimFrames.length;
            drawFrame(batch, idleAimFrames[currentFrameIndex], 1f, 1f);
            break;

        case WALK:
            currentFrameIndex = (int)(stateTime / frameDuration) % walkFrames.length;
            drawFrame(batch, walkFrames[currentFrameIndex], 1f, 1f);
            break;

        case WALK_SHOOT:
            currentFrameIndex = (int)(stateTime / frameDuration) % walkShootFrames.length;
            drawFrame(batch, walkShootFrames[currentFrameIndex], 1f, 1f);
            break;

        case RUNNING:
            currentFrameIndex = (int)(stateTime / frameDuration) % runningFrames.length;
            drawFrame(batch, runningFrames[currentFrameIndex], 1f, 1f);
            break;

        case RUNNING_SHOOT:
            currentFrameIndex = (int)(stateTime / frameDuration) % runShootFrames.length;
            drawFrame(batch, runShootFrames[currentFrameIndex], 1f, 1f);
            break;

        case CROUCH:
            currentFrameIndex = (int)(stateTime / frameDuration) % crouchFrames.length;
            drawFrame(batch, crouchFrames[currentFrameIndex], 1f, 1f);
            break;

        case CROUCH_AIM:
            currentFrameIndex = (int)(stateTime / frameDuration) % crouchAimFrames.length;
            drawFrame(batch, crouchAimFrames[currentFrameIndex], 1f, 1f);
            break;

        case CROUCH_SHOOT:
            currentFrameIndex = (int)(stateTime / frameDuration) % crouchShootFrames.length;
            drawFrame(batch, crouchShootFrames[currentFrameIndex], 1f, 1f);
            break;

        case JUMP:
            currentFrameIndex = (int)(stateTime / frameDuration) % jumpFrames.length;
            drawFrame(batch, jumpFrames[currentFrameIndex], 1f, 1f);
            break;

        case JUMPSHOOT:
            currentFrameIndex = (int)(stateTime / frameDuration) % jumpShootFrames.length;
            drawFrame(batch, jumpShootFrames[currentFrameIndex], 1f, 1f);
            break;

        case SHOOT:
            currentFrameIndex = (int)(stateTime / frameDuration) % shootFrames.length;
            drawFrame(batch, shootFrames[currentFrameIndex], 1f, 1f);
            break;

        case HURT:
            currentFrameIndex= (int)(stateTime / frameDuration) % hurtFrames.length;
            drawFrame(batch, hurtFrames[currentFrameIndex], 1f, 1f);
            break;

        case DEAD:
            currentFrameIndex = (int)(stateTime / frameDuration) % deadFrames.length;
            drawFrame(batch, deadFrames[currentFrameIndex], 1f, 1f);
            break;

        case CLIMB:
            currentFrameIndex = (int)(stateTime / frameDuration) % climbFrames.length;
            drawFrame(batch, climbFrames[currentFrameIndex], 1f, 1f);
            break;

        case MELEE:
            currentFrameIndex = (int)(stateTime / frameDuration) % meleeFrames.length;
            drawFrame(batch, meleeFrames[currentFrameIndex], 1f, 1f);
            break;

        case CROUCH_MELEE:
            currentFrameIndex = (int)(stateTime / frameDuration) % crouchMeleeFrames.length;
            drawFrame(batch, crouchMeleeFrames[currentFrameIndex], 1f, 1f);
            break;

        case JUMP_MELEE:
            currentFrameIndex = (int)(stateTime / frameDuration) % jumpMeleeFrames.length;
            drawFrame(batch, jumpMeleeFrames[currentFrameIndex], 1f, 1f);
            break;

        case THROW:
            currentFrameIndex = (int)(stateTime / frameDuration) % throwFrames.length;
            drawFrame(batch, throwFrames[currentFrameIndex], 1f, 1f);
            break;

        case CROUCH_THROW:
            currentFrameIndex = (int)(stateTime / frameDuration) % crouchThrowFrames.length;
            drawFrame(batch, crouchThrowFrames[currentFrameIndex], 1f, 1f);
            break;

        case JUMP_THROW:
            currentFrameIndex = (int)(stateTime / frameDuration) % jumpThrowFrames.length;
            drawFrame(batch, jumpThrowFrames[currentFrameIndex], 1f, 1f);
            break;

        case HAPPY:
            currentFrameIndex = (int)(stateTime / frameDuration) % happyFrames.length;
            drawFrame(batch, happyFrames[currentFrameIndex], 1f, 1f);
            break;
        }

    }


    private void updatePlayerState(){

        if(isCrouching && isShooting)
            currentState = PlayerState.CROUCH_SHOOT;
        else if(isCrouching)
            currentState = PlayerState.CROUCH;

        else if(!isGrounded && isShooting)
            currentState = PlayerState.JUMPSHOOT;

        else if(!isGrounded && !isJetPackActive)
            currentState = PlayerState.JUMP;
    
        else if((!isGrounded && isJetPackActive) || (isGrounded && velocity.x==0))
            currentState = PlayerState.IDLE;

        else if(isGrounded && isRunning && isShooting)
            currentState = PlayerState.RUNNING_SHOOT;

        else if(isGrounded && isRunning)
            currentState = PlayerState.RUNNING;

        else if(isGrounded && !isRunning && isShooting)
            currentState = PlayerState.WALK_SHOOT;

        else if(isGrounded && !isRunning)
            currentState = PlayerState.WALK;
    }

    private GripPose getCurrentGripPose(){
        GripPose[] poses = gripPoseMap.get(currentState);
        return poses[currentFrameIndex];
    }

    public void drawJetpackFlame(SpriteBatch batch, float dt, float x , float y){
        jetpackFlameStateTime += dt;

        int frameIndex = (int)(jetpackFlameStateTime / 0.07f) % jetpackFlameFrames.length;
        TextureRegion currentFlame = jetpackFlameFrames[frameIndex];
        float flameWidth = jetPackDrawData.width;

        float flameHeight = flameWidth * ((float)currentFlame.getRegionHeight()/ currentFlame.getRegionWidth());
        float flameX = x;
        float flameY = y - flameHeight/2;
    
        batch.draw(currentFlame, flameX, flameY,flameWidth,flameHeight);
    }


    public void disposePlayer(){;
        for(Texture texture : loadedTextures)
            texture.dispose();
        jetpackTexture.dispose();
    }

    private void drawFrame(SpriteBatch batch, TextureRegion frame, float scaleX, float scaleY){
        boolean faceLeft = currentDirection == FacingDirection.LEFT;
        if(frame.isFlipX() != faceLeft)
            frame.flip(true,false);

        float drawWidth = width * scaleX;
        float drawHeight = height * scaleY;

        float drawX = position.x + (width - drawWidth) / 2f;
        float drawY = position.y;

         batch.draw(frame, drawX, drawY, drawWidth, drawHeight);
    }

    public void receivePlayerMoveBox(Rectangle moveBox){
        movementOffset = new Rectangle();
        movementOffset.x = moveBox.x - position.x;
        movementOffset.y = moveBox.y - position.y;
        movementOffset.width = moveBox.width;
        movementOffset.height = moveBox.height;
    }

    public void receivePlayerCrouchMoveBox(Rectangle crouchMoveBox){
        crouchMovementOffset = new Rectangle();
        crouchMovementOffset.x = crouchMoveBox.x - position.x;
        crouchMovementOffset.y = crouchMoveBox.y - position.y;
        crouchMovementOffset.width = crouchMoveBox.width;
        crouchMovementOffset.height = crouchMoveBox.height;
    }
    
    public void receivePlayerGunHoldingOffset(Vector2 gunPosOffset){
        gunHoldingPositionOffset = gunPosOffset;
    }

    public void move(Vector2 direction, float dt, Vector2 worldSize,Array<Rectangle> solidPlatforms){
        float speedMultiplier;
        if(isRunning)
            speedMultiplier = 1f;
        else
            speedMultiplier = 0.5f;
        velocity.x= direction.x * moveSpeed * speedMultiplier;
        position.x = position.x + velocity.x * dt;
        resolveHorizontalCollision(direction,solidPlatforms);
        position.y = position.y + velocity.y * dt;
        resolveVerticalCollision(solidPlatforms);

        if(position.x <= 0)
            position.x = 0;
        
        else if(position.x >= worldSize.x - width)
            position.x = worldSize.x - width;
        
        if(position.y <= 0){
            position.y = 0;
            velocity.y = 0;
        }
        else if(position.y >= worldSize.y - height){
            position.y = worldSize.y - height;
            velocity.y = 0;

        }
    }

    public Vector2 getPlayerPosition(){
        return position;
    }

    public Vector2 getPlayerCentrePosition(){
        Vector2 centrePosition = new Vector2(position.x + width/2 , position.y + height/2);
        return centrePosition;
    }

    private void resolveHorizontalCollision(Vector2 direction,Array<Rectangle> solidPlatforms){
        Rectangle activeCollider;
        if(isCrouching)
            activeCollider = crouchMovementOffset;
        else
            activeCollider = movementOffset;

        for(Rectangle rectangle: solidPlatforms){
            if((position.x + activeCollider.x + activeCollider.width > rectangle.x && position.x + activeCollider.x < rectangle.x + rectangle.width)
            && position.y + activeCollider.y < rectangle.y+rectangle.height && position.y + activeCollider.y + activeCollider.height > rectangle.y){
                if(direction.x < 0)
                    position.x = rectangle.x + rectangle.width - activeCollider.x;
                else if(direction.x>0)
                    position.x = rectangle.x - activeCollider.width - activeCollider.x; 
            }
        }         
    }

    private void resolveVerticalCollision(Array<Rectangle> solidPlatforms){
        Rectangle activeCollider;
        if(isCrouching)
            activeCollider = crouchMovementOffset;
        else
            activeCollider = movementOffset;

        for(Rectangle rectangle: solidPlatforms){
            if((position.y + activeCollider.y + activeCollider.height > rectangle.y && position.y + activeCollider.y < rectangle.y + rectangle.height)
            && (position.x + activeCollider.x + activeCollider.width > rectangle.x && position.x + activeCollider.x < rectangle.x + rectangle.width )){
                if(velocity.y > 0){
                    position.y = rectangle.y - activeCollider.height - activeCollider.y;
                    velocity.y = 0;
                }
                else if(velocity.y < 0){
                    position.y = rectangle.y + rectangle.height - activeCollider.y;
                    velocity.y = 0;
                }
            }
        }
    }
    private void linkGripPoseMap() {
        gripPoseMap.put(PlayerState.IDLE, idleGripPoses);
        gripPoseMap.put(PlayerState.IDLE_AIM, idleAimGripPoses);

        gripPoseMap.put(PlayerState.WALK, walkGripPoses);
        gripPoseMap.put(PlayerState.WALK_SHOOT, walkShootGripPoses);

        gripPoseMap.put(PlayerState.RUNNING, runningGripPoses);
        gripPoseMap.put(PlayerState.RUNNING_SHOOT, runningShootGripPoses);

        gripPoseMap.put(PlayerState.CROUCH, crouchGripPoses);
        gripPoseMap.put(PlayerState.CROUCH_AIM, crouchAimGripPoses);
        gripPoseMap.put(PlayerState.CROUCH_SHOOT, crouchShootGripPoses);

        gripPoseMap.put(PlayerState.JUMP, jumpGripPoses);
        gripPoseMap.put(PlayerState.JUMPSHOOT, jumpShootGripPoses);

        gripPoseMap.put(PlayerState.SHOOT, shootGripPoses);

        gripPoseMap.put(PlayerState.HURT, hurtGripPoses);
        gripPoseMap.put(PlayerState.DEAD, deadGripPoses);

        gripPoseMap.put(PlayerState.MELEE, meleeGripPoses);
        gripPoseMap.put(PlayerState.CROUCH_MELEE, crouchMeleeGripPoses);
        gripPoseMap.put(PlayerState.JUMP_MELEE, jumpMeleeGripPoses);

        gripPoseMap.put(PlayerState.THROW, throwGripPoses);
        gripPoseMap.put(PlayerState.CROUCH_THROW, crouchThrowGripPoses);
        gripPoseMap.put(PlayerState.JUMP_THROW, jumpThrowGripPoses);

        gripPoseMap.put(PlayerState.HAPPY, happyGripPoses);
    }


    public boolean isFacingLeft(){
        return currentDirection == FacingDirection.LEFT;
    }


    public void crouch(){
        if(isGrounded){
            isCrouching = true;
        }

    }

    public void stand(Array<Rectangle> solidPlatforms){
        if (isCrouching){
            boolean canStand = true;
            float playerLeft,playerRight, playerBottom, playerTop;
            playerLeft = position.x + movementOffset.x;
            playerRight = playerLeft + movementOffset.width;
            playerBottom = position.y + movementOffset.y;
            playerTop = playerBottom + movementOffset.height;

            for (Rectangle rectangle : solidPlatforms) {
                if((playerRight > rectangle.x && playerLeft < rectangle.x + rectangle.width)
                &&(playerTop > rectangle.y && playerBottom < rectangle.y + rectangle.height)){
                    canStand = false;
                    break;
                }
            }
            if (canStand){
                isCrouching = false;
                System.out.println("STANDING");
            }
        }
    }

    public boolean getCrouchingState(){
        return isCrouching;
    }

    public boolean getGroundedStatus(){
        return isGrounded;
    }

    public boolean getJetPackStatus(){
        return isJetPackActive;
    }

    public void receiveJetPackDrawData(Rectangle data){
        jetPackDrawData = new Rectangle(data);  
    }

    public void stopJetpack(){
        isJetPackActive = false;
        jetpackFlameStateTime = 0;
    }

    public void ensureGrounding(Array<Rectangle> solidPlatforms){
        isGrounded = false;
        Rectangle activeCollider;
        if(isCrouching)
            activeCollider = crouchMovementOffset;
        else
            activeCollider = movementOffset;

        float offset = 2.f;
        for(Rectangle rectangle: solidPlatforms){
            if((position.y + activeCollider.y >= rectangle.y + rectangle.height) && (position.y + activeCollider.y <= rectangle.y + rectangle.height + offset)
            && (rectangle.x < position.x + activeCollider.x + activeCollider.width) && (rectangle.x+rectangle.width > position.x + activeCollider.x)){
                isGrounded = true;
                airTime = 0;
                jumpCount = 0;
                break;
            }
        }
    }

    public void reactToGravity(float gravity , float dt){
        if(!isGrounded)
            velocity.y+= gravity * dt;
        else if(isGrounded){
            velocity.y = 0;
            airTime = 0;
            jumpCount = 0;
        }

        if(velocity.y < maxFallSpeed)
            velocity.y = maxFallSpeed;
    }

    public void reactToJetpack(float thrust, float dt){
        isJetPackActive = true;
        velocity.y += thrust * dt;
        if(velocity.y > maxRiseSpeed)
              velocity.y = maxRiseSpeed;
    }

    public Vector2 getHandPosition(){
        Vector2 handPos = new Vector2();
        handPos.y = gunHoldingPositionOffset.y + position.y + weaponDrawAdjustmentY;
        if(currentDirection == FacingDirection.RIGHT)
            handPos.x = gunHoldingPositionOffset.x + position.x;
        else
            handPos.x = (width - gunHoldingPositionOffset.x) + position.x;
        return handPos;
    }

    public Vector2 getCurrentGripWorldPosition() {
        GripPose pose = getCurrentGripPose();
        return new Vector2(position.x+pose.position.x , position.y+pose.position.y);
    }


    private void loadAllGripPoses(){
        JsonValue root = new JsonReader().parse(Gdx.files.internal(AssetPaths.Data.SOLDIER1_GRIPS));
        loadGripPoseArray(root, "IDLE", idleGripPoses);
        loadGripPoseArray(root, "IDLE_AIM", idleAimGripPoses);

        loadGripPoseArray(root, "WALK", walkGripPoses);
        loadGripPoseArray(root, "WALK_SHOOT", walkShootGripPoses);

        loadGripPoseArray(root, "RUNNING", runningGripPoses);
        loadGripPoseArray(root, "RUNNING_SHOOT", runningShootGripPoses);

        loadGripPoseArray(root, "CROUCH", crouchGripPoses);
        loadGripPoseArray(root, "CROUCH_AIM", crouchAimGripPoses);
        loadGripPoseArray(root, "CROUCH_SHOOT", crouchShootGripPoses);

        loadGripPoseArray(root, "JUMP", jumpGripPoses);
        loadGripPoseArray(root, "JUMPSHOOT", jumpShootGripPoses);

        loadGripPoseArray(root, "SHOOT", shootGripPoses);

        loadGripPoseArray(root, "HURT", hurtGripPoses);
        loadGripPoseArray(root, "DEAD", deadGripPoses);

        loadGripPoseArray(root, "MELEE", meleeGripPoses);
        loadGripPoseArray(root, "CROUCH_MELEE", crouchMeleeGripPoses);
        loadGripPoseArray(root, "JUMP_MELEE", jumpMeleeGripPoses);

        loadGripPoseArray(root, "THROW", throwGripPoses);
        loadGripPoseArray(root, "CROUCH_THROW", crouchThrowGripPoses);
        loadGripPoseArray(root, "JUMP_THROW", jumpThrowGripPoses);

        loadGripPoseArray(root, "HAPPY", happyGripPoses);
    }

    

    private void loadGripPoseArray(JsonValue root, String stateName, GripPose[] targetArray) {
        JsonValue stateData = root.get(stateName);
        if(targetArray.length != stateData.size){
            System.out.println("Grip data does not match animation frame count!");
            return;
        }
        int m = 0;
        for(JsonValue pose = stateData.child; pose!=null ; pose = pose.next){
            float x = pose.getFloat("x");
            float y = pose.getFloat("y");
            float angle = pose.getFloat("angle");
            targetArray[m] = new GripPose(x,y,angle);
            m++;
        }
    }           


    public void setFacingDirectionLeft(){
        currentDirection = FacingDirection.LEFT;
    }

    public void setFacingDirectionRight(){
        currentDirection = FacingDirection.RIGHT;
    }

    public void startJetpack(){
        isJetPackActive = true;
    }

    public void jump(){
        currentState = PlayerState.JUMP;
        velocity.y = 625f;
        jumpCount++;
    }

    public float getAirTime(){
        return airTime;
    }

    public void setAirTime(float aT){
        airTime = aT;
    }

    public boolean jumpsAvailable(){
        return jumpCount < MAX_JUMP_PRESSES;
    }


    public void setRunningTrue(){
        isRunning = true;
    }

    public void setRunningFalse(){
        isRunning = false;
    }

    public void setShootingTrue(){
        isShooting = true;
    }

    public void setShootingFalse(){
        isShooting = false;
    }



 

    public static void main(String[] args){

    }


}