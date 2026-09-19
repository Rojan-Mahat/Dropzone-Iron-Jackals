package rojan.shooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;


public class Player{
    private Vector2 position;
    private float width;
    private float height;
    private Texture texture;
    private Sprite sprite;

    private Vector2 velocity;
    private float moveSpeed;

    private Rectangle movementOffset;
    private Rectangle crouchMovementOffset;

    private boolean isCrouching;
    private boolean isGrounded;

    private float maxRiseSpeed = 550;
    private float maxFallSpeed = -800;
    


    
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
    }

    public void drawPlayer(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void disposePlayer(){
        texture.dispose();
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



    public void move(Vector2 direction, float dt, Vector2 worldSize,Array<Rectangle> solidPlatforms){
        float velocityX = direction.x * moveSpeed;

        position.x = position.x + velocityX * dt;
        resolveHorizontalCollision(direction,solidPlatforms);
        position.y = position.y + velocity.y * dt;
        resolveVerticalCollision(solidPlatforms);

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
            System.out.println("CROUCHING");
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
        velocity.y += thrust * dt;
        if(velocity.y > maxRiseSpeed)
              velocity.y = maxRiseSpeed;
    }

    public void balanceIfPlayerOnVerticalExtreme(Vector2 worldSize){
        if(position.y + height  >= worldSize.y && velocity.y > 0) 
                velocity.y = 0;

        else if(position.y <= 0 && velocity.y < 0)
                velocity.y = 0;
    }


    public static void main(String[] args){

    }


}