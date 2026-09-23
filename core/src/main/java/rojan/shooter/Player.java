package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation;


public class Player{
    private Vector2 position;
    private float width;
    private float height;
    private Vector2 gunHoldingPositionOffset;

    private Vector2 velocity;
    private float moveSpeed;

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

        switch(currentState){
        case IDLE:
            int idleIndex =(int)(stateTime / frameDuration) % idleFrames.length;
            drawFrame(batch, idleFrames[idleIndex], 1f, 1f);
            break;

        case IDLE_AIM:
            int idleAimIndex = (int)(stateTime / frameDuration) % idleAimFrames.length;
            drawFrame(batch, idleAimFrames[idleAimIndex], 1f, 1f);
            break;

        case WALK:
            int walkIndex = (int)(stateTime / frameDuration) % walkFrames.length;
            drawFrame(batch, walkFrames[walkIndex], 1f, 1f);
            break;

        case WALK_SHOOT:
            int walkShootIndex = (int)(stateTime / frameDuration) % walkShootFrames.length;
            drawFrame(batch, walkShootFrames[walkShootIndex], 1f, 1f);
            break;

        case RUNNING:
            int runningIndex = (int)(stateTime / frameDuration) % runningFrames.length;
            drawFrame(batch, runningFrames[runningIndex], 1f, 1f);
            break;

        case RUNNING_SHOOT:
            int runningShootIndex = (int)(stateTime / frameDuration) % runShootFrames.length;
            drawFrame(batch, runShootFrames[runningShootIndex], 1f, 1f);
            break;

        case CROUCH:
            int crouchIndex = (int)(stateTime / frameDuration) % crouchFrames.length;
            drawFrame(batch, crouchFrames[crouchIndex], 1f, 1f);
            break;

        case CROUCH_AIM:
            int crouchAimIndex = (int)(stateTime / frameDuration) % crouchAimFrames.length;
            drawFrame(batch, crouchAimFrames[crouchAimIndex], 1f, 1f);
            break;

        case CROUCH_SHOOT:
            int crouchShootIndex = (int)(stateTime / frameDuration) % crouchShootFrames.length;
            drawFrame(batch, crouchShootFrames[crouchShootIndex], 1f, 1f);
            break;

        case JUMP:
            int jumpIndex = (int)(stateTime / frameDuration) % jumpFrames.length;
            drawFrame(batch, jumpFrames[jumpIndex], 1f, 1f);
            break;

        case JUMPSHOOT:
            int jumpShootIndex = (int)(stateTime / frameDuration) % jumpShootFrames.length;
            drawFrame(batch, jumpShootFrames[jumpShootIndex], 1f, 1f);
            break;

        case SHOOT:
            int shootIndex = (int)(stateTime / frameDuration) % shootFrames.length;
            drawFrame(batch, shootFrames[shootIndex], 1f, 1f);
            break;

        case HURT:
            int hurtIndex = (int)(stateTime / frameDuration) % hurtFrames.length;
            drawFrame(batch, hurtFrames[hurtIndex], 1f, 1f);
            break;

        case DEAD:
            int deadIndex = (int)(stateTime / frameDuration) % deadFrames.length;
            drawFrame(batch, deadFrames[deadIndex], 1f, 1f);
            break;

        case CLIMB:
            int climbIndex = (int)(stateTime / frameDuration) % climbFrames.length;
            drawFrame(batch, climbFrames[climbIndex], 1f, 1f);
            break;

        case MELEE:
            int meleeIndex = (int)(stateTime / frameDuration) % meleeFrames.length;
            drawFrame(batch, meleeFrames[meleeIndex], 1f, 1f);
            break;

        case CROUCH_MELEE:
            int crouchMeleeIndex = (int)(stateTime / frameDuration) % crouchMeleeFrames.length;
            drawFrame(batch, crouchMeleeFrames[crouchMeleeIndex], 1f, 1f);
            break;

        case JUMP_MELEE:
            int jumpMeleeIndex = (int)(stateTime / frameDuration) % jumpMeleeFrames.length;
            drawFrame(batch, jumpMeleeFrames[jumpMeleeIndex], 1f, 1f);
            break;

        case THROW:
            int throwIndex = (int)(stateTime / frameDuration) % throwFrames.length;
            drawFrame(batch, throwFrames[throwIndex], 1f, 1f);
            break;

        case CROUCH_THROW:
            int crouchThrowIndex = (int)(stateTime / frameDuration) % crouchThrowFrames.length;
            drawFrame(batch, crouchThrowFrames[crouchThrowIndex], 1f, 1f);
            break;

        case JUMP_THROW:
            int jumpThrowIndex = (int)(stateTime / frameDuration) % jumpThrowFrames.length;
            drawFrame(batch, jumpThrowFrames[jumpThrowIndex], 1f, 1f);
            break;

        case HAPPY:
            int happyIndex = (int)(stateTime / frameDuration) % happyFrames.length;
            drawFrame(batch, happyFrames[happyIndex], 1f, 1f);
            break;
        }
    }


    private void updatePlayerState(){
        if(isCrouching)
            currentState = PlayerState.CROUCH;
        else if(!isGrounded)
            currentState = PlayerState.JUMP;
        else if(isGrounded && velocity.x==0)
            currentState = PlayerState.IDLE;
        else if(isGrounded && velocity.x!=0)
            currentState = PlayerState.RUNNING;
       
        

    }

    /*private void drawJetpackFlame(SpriteBatch batch, float dt){
        flameStateTime += dt;

        float flameOffsetX = width * 0.08f;
        int frameIndex = (int)(flameStateTime / 0.08f) % flameFrames.length;
        float flameWidth = width * 0.38f;
        float flameHeight = height * 0.32f;
        float flameX;

        float nozzleY = position.y + height * 0.635f;
        float flameY = nozzleY - flameHeight;

        float originX = flameWidth / 2f;
        float originY = flameHeight;
        float rotation;
        if(currentDirection == FacingDirection.RIGHT)
            rotation = -8f;
        else
            rotation = 8f;

        if(currentDirection == FacingDirection.RIGHT)
            flameX = position.x + flameOffsetX;
        else
            flameX = position.x + width - flameOffsetX - flameWidth;
    
        batch.draw(flameFrames[frameIndex], flameX, flameY, originX, originY,flameWidth,flameHeight,1.1f,1.1f,rotation);
    }*/


    public void disposePlayer(){;
        for(Texture texture : loadedTextures)
            texture.dispose();
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

        System.out.println("PLAYER X: " + position.x);
        System.out.println("PLAYER Y: " + position.y);

        System.out.println("OFFSET X: " + (moveBox.x - position.x));
        System.out.println("OFFSET Y: " + (moveBox.y - position.y));
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
        velocity.x= direction.x * moveSpeed;

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

        //updateWeaponDrawAdjustment(velocity.x,velocity.y); 
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
                System.out.println(
                    "PLAYER -> left:" + (position.x + activeCollider.x) +
                    " bottom:" + (position.y + activeCollider.y) +
                    " right:" + (position.x + activeCollider.x + activeCollider.width) +
                    " top:" + (position.y + activeCollider.y + activeCollider.height)
                );
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

    public void stopJetpack(){
        isJetPackActive = false;
        //flameStateTime = 0;
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
                break;
            }
        }
    }

    public void reactToGravity(float gravity , float dt){
        if(!isGrounded)
            velocity.y+= gravity * dt;
        else if(isGrounded)
            velocity.y = 0;

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


    /*private void updateWeaponDrawAdjustment(float velocity_x , float velocity_y){
        if(!isCrouching){
            if(velocity_y > -100 &&  velocity_y < 200)
                weaponDrawAdjustmentY = 9f;

            else if(velocity_y >= -400 &&  velocity_y < -100)
                weaponDrawAdjustmentY = 2.5f;
            
            else if(velocity_y < -400)
                weaponDrawAdjustmentY = -2;

            else if(velocity_y > 200 && velocity_y < 450)
                weaponDrawAdjustmentY = 12;

            else if(velocity_y > 450)
                weaponDrawAdjustmentY = 15;
        }

        else if(isCrouching && velocity_x == 0)
            weaponDrawAdjustmentY = -22;

        else if(isCrouching && velocity_x!=0)
            weaponDrawAdjustmentY = -14.5f;
    }*/

    public void setFacingDirectionLeft(){
        currentDirection = FacingDirection.LEFT;
    }

    public void setFacingDirectionRight(){
        currentDirection = FacingDirection.RIGHT;
    }



    public static void main(String[] args){

    }


}