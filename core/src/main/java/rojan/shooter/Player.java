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

    private Texture texture;
    private Sprite sprite;

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
        RUNNING,
        CROUCH_IDLE,
        CROUCH_WALKING,
        HIT,
        SHOOTING,
        JETPACK_THRUST,
        AIRBORNE,
        DEAD
    }
    private PlayerState currentState;
    private PlayerState previousState;


    private enum FacingDirection{
        LEFT,
        RIGHT
    }
    private FacingDirection currentDirection;

    private Texture idleTexture;
    private TextureRegion[] idleFrames;
    private float stateTime;

    private Texture crouchIdleTexture;
    private TextureRegion[] crouchIdleFrames;

    private Texture crouchWalkingTexture;
    private TextureRegion[] crouchWalkingFrames;

    private Texture runningTexture;
    private TextureRegion[] runningFrames;

    private Texture jetThrustTexture;
    private TextureRegion[] jetThrustFrames;

    private Texture airborneTexture;
    private TextureRegion[] airborneFrames;

    private Texture flameTexture;
    private TextureRegion[] flameFrames;
    private float flameStateTime;

    private Weapon currentWeapon;

    
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
        isCrouching = false;
        currentState = PlayerState.IDLE;
        previousState = PlayerState.IDLE;

        currentDirection = FacingDirection.RIGHT;


        stateTime = 0f;

        idleTexture = new Texture("sprites/player/idle.png");
        TextureRegion[][] idleRegions = TextureRegion.split(idleTexture,idleTexture.getWidth() / 4,idleTexture.getHeight()/2);
        idleFrames = new TextureRegion[8];
        int idleIndex = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++){
                idleFrames[idleIndex] = idleRegions[row][col];
                idleIndex++;
            }
        }

        runningTexture = new Texture("sprites/player/running.png");
        TextureRegion[][] runningRegions = TextureRegion.split(runningTexture,runningTexture.getWidth() / 4,runningTexture.getHeight()/2);
        runningFrames = new TextureRegion[8];
        int runningIndex = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++){
                runningFrames[runningIndex] = runningRegions[row][col];
                runningIndex++;
            }
        }

        crouchIdleTexture = new Texture("sprites/player/crouch_idle.png");
        TextureRegion[][] crouchRegions = TextureRegion.split(crouchIdleTexture,crouchIdleTexture.getWidth() / 4,crouchIdleTexture.getHeight());
        crouchIdleFrames = new TextureRegion[4];
        crouchIdleFrames = crouchRegions[0];

        crouchWalkingTexture = new Texture("sprites/player/crouch_walking.png");
        TextureRegion[][] crouchWalkingRegions = TextureRegion.split(crouchWalkingTexture,crouchWalkingTexture.getWidth() / 4,crouchWalkingTexture.getHeight());
        crouchWalkingFrames = new TextureRegion[4];
        crouchWalkingFrames = crouchWalkingRegions[0];

        jetThrustTexture = new Texture("sprites/player/jet_thrust.png");
        TextureRegion[][] jetThrustRegions = TextureRegion.split(jetThrustTexture,jetThrustTexture.getWidth() / 4,jetThrustTexture.getHeight());
        jetThrustFrames = new TextureRegion[4];
        jetThrustFrames = jetThrustRegions[0];

        airborneTexture = new Texture("sprites/player/airborne.png");
        TextureRegion[][] airborneRegions = TextureRegion.split(airborneTexture,airborneTexture.getWidth() / 4,airborneTexture.getHeight());
        airborneFrames = new TextureRegion[4];
        airborneFrames = airborneRegions[0];

        flameTexture = new Texture("sprites/effects/flame.png");
        TextureRegion[][] flameRegions = TextureRegion.split(flameTexture,flameTexture.getWidth() / 4,flameTexture.getHeight());
        flameFrames = new TextureRegion[4];
        flameFrames = flameRegions[0];
        flameStateTime = 0f;
    }

    public void drawPlayer(SpriteBatch batch,float dt){
        previousState = currentState;
        updatePlayerState();
        if(previousState!=currentState)
                stateTime = 0;
        else
            stateTime+=dt;

        if(isJetPackActive)
            drawJetpackFlame(batch, dt);

        switch(currentState){

            case JETPACK_THRUST:
                int jetThrustIndex = (int)(stateTime / 0.23f) % jetThrustFrames.length;
                drawFrame(batch,jetThrustFrames[jetThrustIndex]);
                batch.draw(jetThrustFrames[jetThrustIndex], position.x, position.y, width, height);
                break;

            case AIRBORNE:
                int airborneIndex = (int)(stateTime / 0.23f) % airborneFrames.length;
                drawFrame(batch,airborneFrames[airborneIndex]);
                batch.draw(airborneFrames[airborneIndex], position.x, position.y, width, height);
                break;


            case IDLE:
                int idleIndex = (int)(stateTime / 90f) % idleFrames.length;
                drawFrame(batch,idleFrames[idleIndex]);
                batch.draw(idleFrames[idleIndex], position.x, position.y, width, height);
                break;
            

            case RUNNING:
                int runningIndex = (int)(stateTime / 0.23f) % runningFrames.length;
                batch.draw(runningFrames[runningIndex], position.x, position.y, width, height);
                break;
            

            case CROUCH_IDLE:
                int crouchIdleIndex = (int)(stateTime / 0.23f) % crouchIdleFrames.length;
                drawFrame(batch,crouchIdleFrames[crouchIdleIndex]);
                batch.draw(crouchIdleFrames[crouchIdleIndex], position.x, position.y, width, height);
                break;

            case CROUCH_WALKING:
                int crouchWalkingIndex = (int)(stateTime / 0.23f) % crouchWalkingFrames.length;
                drawFrame(batch,crouchWalkingFrames[crouchWalkingIndex]);
                batch.draw(crouchWalkingFrames[crouchWalkingIndex], position.x, position.y, width, height);
                break;
        }
    }

    private void updatePlayerState(){
        if(isJetPackActive)
            currentState = PlayerState.JETPACK_THRUST;

        else if(!isGrounded)
            currentState = PlayerState.AIRBORNE;

        else if(isGrounded && !isCrouching && velocity.x == 0)
            currentState = PlayerState.IDLE;
        
        else if(isGrounded && !isCrouching && velocity.x != 0)
            currentState = PlayerState.RUNNING;
        
        else if(isGrounded && isCrouching && velocity.x == 0)
            currentState = PlayerState.CROUCH_IDLE;
        
        else if(isGrounded && isCrouching && velocity.x != 0)
            currentState = PlayerState.CROUCH_WALKING;    

        
        if(velocity.x < 0)
            currentDirection = FacingDirection.LEFT;
        else if(velocity.x>0)
            currentDirection = FacingDirection.RIGHT;

    }

    private void drawJetpackFlame(SpriteBatch batch, float dt){
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
    }


    public void disposePlayer(){
        texture.dispose();
        idleTexture.dispose();
        runningTexture.dispose(); 
        crouchIdleTexture.dispose(); 
        crouchWalkingTexture.dispose(); 
        jetThrustTexture.dispose();
        airborneTexture.dispose();
        flameTexture.dispose();

    }

    private void drawFrame(SpriteBatch batch, TextureRegion frame){
        boolean faceLeft = currentDirection == FacingDirection.LEFT;
        if(frame.isFlipX() != faceLeft)
            frame.flip(true,false);
        batch.draw(frame,position.x,position.y,width,height);
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

        sprite.setPosition(position.x , position.y);
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
        flameStateTime = 0;
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
        handPos.y = gunHoldingPositionOffset.y + position.y;
        if(currentDirection == FacingDirection.RIGHT)
            handPos.x = gunHoldingPositionOffset.x + position.x;
        else
            handPos.x = (width - gunHoldingPositionOffset.x) + position.x;
        return handPos;
    }



    public static void main(String[] args){

    }


}