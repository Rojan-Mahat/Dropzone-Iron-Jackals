package rojan.shooter;

public class Powerup{
    public enum PowerupType{
        HEALTH,
        SHIELD,
        JETPACK,
        FIRE_RATE
    }
    public enum SpicyType{
        OVERDRIVE,
        RAPID_MOBILITY,
        BOTTOMLESS_MAG,
        DAMAGE_SURGE,
        GOLD_RUSH,
        SCORE_FRENZY
    }

    public enum MoreSpicyType{
        BERSERK,
        INFINITE_ASSAULT,
        HYPERDRIVE,
        FORTRESS,
        DOUBLE_FORTUNE
    }

    private PowerupType type;
    private SpicyType spicytype;
    private  MoreSpicyType moreSpicyType;

    Powerup(PowerupType type){
        this.type = type;
    }

    public PowerupType getType(){
        return type;
    }
    public SpicyType getSpicyType(){
        return spicytype;
    }
    public MoreSpicyType getMoreSpicyType(){
        return moreSpicyType;
    }

   
}