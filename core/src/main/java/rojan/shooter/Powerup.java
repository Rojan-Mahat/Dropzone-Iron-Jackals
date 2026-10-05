package rojan.shooter;

public class Powerup{
    public enum PowerupType{
        HEALTH,
        SHIELD,
        JETPACK,
        FIRE_RATE
    }

    private PowerupType type;

    Powerup(PowerupType type){
        this.type = type;
    }

    public PowerupType getType(){
        return type;
    }
}