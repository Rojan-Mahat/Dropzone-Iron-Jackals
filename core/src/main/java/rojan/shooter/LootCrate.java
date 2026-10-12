package rojan.shooter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LootCrate{
    private Rectangle crate;
    public enum CrateType{
        COMMON,
        RARE,
        SUPER_RARE,
        SPECIAL
    } 
    private boolean opened;
    private CrateType type;

    private static Texture commonSheet;
    private static Texture rareSheet;
    private static Texture superRareSheet;
    private static Texture specialSheet;

    private static Texture dashboard3;
    private static Texture dashboard4;
    private static Texture dashboard5;
    private static Texture dashboard6;
    private static Texture dashboardSpecial5;
    private static Texture dashboard5NoWeapon;
    private static Texture dashboard6NoWeapon;

    private static Animation<TextureRegion> commonOpening;
    private static Animation<TextureRegion> rareOpening;
    private static Animation<TextureRegion> superRareOpening;
    private static Animation<TextureRegion> specialOpening;

    private static final float FRAME_DURATION = 0.08f;
    private float openingTime = 0f;
    private float openedTimer = 0f;
    private final float OPENED_LIFETIME = 8f;
    private boolean readyToRemove = false;

    private boolean claimed = false;
    private float claimedTimer = 0f;
    private final float CLAIMED_LIFETIME = 8f;
    public class Contents{
        private int count;
        private int gold;
        private int ammoRewardPercent;
        private int score;
        private int hpBoost;

        private Array<Powerup.PowerupType> powerups;;
        private Array<Weapon.WeaponType> weapons;

        private Powerup.SpicyType spicy;
        private Powerup.MoreSpicyType moreSpicy;
        private boolean jackpot;
        private boolean biggerJackpot;
        private boolean godMode;
        Contents(CrateType type){
            powerups = new Array<>();
            weapons = new Array<>();
            if(type == CrateType.COMMON){
                int roll = MathUtils.random(1, 15);
                if(roll <= 5 || roll > 9){
                    count = 3;
                }
                if(roll > 5 && roll < 9){
                    count = 4;
                    addRandomPowerUp();
                }
                if(roll == 9){
                    count = 5;
                    addRandomPowerUp();
                    addRandomSpicy();
                }
                gold = MathUtils.random(5,15);
                score =  MathUtils.random(25,75);
                ammoRewardPercent = MathUtils.random(15,20);
                hpBoost = 15;
            }
            if(type == CrateType.RARE){
                addRandomPowerUp();
                hpBoost = 25;
                int roll = MathUtils.random(1, 20);
                if(roll <= 5 || roll > 9){
                    count = 4;
                }
                else if(roll > 5 && roll < 9){
                    count = 5;
                    addRandomSpicy();
                }
                else{
                    count = 5;
                    int chance = MathUtils.random(1,5);
                    if(chance<4){
                        addRandomMoreSpicy();
                    }
                    else if(chance == 4){
                        addRandomGun();
                    }
                    else{
                        jackpot = true;
                    }
                }
                gold = MathUtils.random(20,40);
                score =  MathUtils.random(120,250);
                ammoRewardPercent =  MathUtils.random(20,30);
            }
            if(type == CrateType.SUPER_RARE){
                hpBoost = 50;
                addRandomPowerUp();
                addRandomSpicy();
                int roll = MathUtils.random(1, 20);
                if(roll <= 5 || roll > 9){
                    count = 5;
                }
                else if(roll > 5 && roll < 9){
                    count = 6;
                    addRandomGun();
                }
                else{
                    count = 6;
                    int chance = MathUtils.random(1,5);
                    if(chance<4){
                        addRandomGun();
                    }
                    else if(chance ==4){
                       addRandomMoreSpicy();
                    }
                    else{
                        biggerJackpot = true;
                    }
                }
                gold = MathUtils.random(60,120);
                score =  MathUtils.random(400,800);
                ammoRewardPercent =  MathUtils.random(40,60);
            }

            if(type == CrateType.SPECIAL){
                hpBoost = 100;
                count = 5;
                addRandomGun();
                addRandomMoreSpicy();
                addRandomGun();
                biggerJackpot = true;
                godMode = true;
            }
        }
        private void addRandomPowerUp(){
            int roll = MathUtils.random(1,100);
            if(roll <= 40)
                powerups.add(Powerup.PowerupType.HEALTH);
            else if(roll <= 65)
                powerups.add(Powerup.PowerupType.SHIELD);
            else if(roll <= 85)
                powerups.add(Powerup.PowerupType.JETPACK);
            else
                powerups.add(Powerup.PowerupType.FIRE_RATE);
        }

        private void addRandomGun(){
            int roll = MathUtils.random(1,100);
            if(roll <= 35)
                weapons.add(Weapon.WeaponType.PISTOL);
            else if(roll <= 60)
                weapons.add(Weapon.WeaponType.SMG);
            else if(roll <= 80)
                weapons.add(Weapon.WeaponType.SHOTGUN);
            else if(roll <= 95)
                weapons.add(Weapon.WeaponType.SNIPER);
            else
                weapons.add(Weapon.WeaponType.ROCKET_LAUNCHER);
        }

        private void addRandomSpicy(){
            int roll = MathUtils.random(1,6);
            if(roll == 1)
                spicy = Powerup.SpicyType.OVERDRIVE;
            else if(roll == 2)
                spicy = Powerup.SpicyType.RAPID_MOBILITY;
            else if(roll == 3)
                spicy = Powerup.SpicyType.BOTTOMLESS_MAG;
            else if(roll == 4)
                spicy = Powerup.SpicyType.DAMAGE_SURGE;
            else if(roll == 5)
                spicy = Powerup.SpicyType.GOLD_RUSH;
            else
                spicy = Powerup.SpicyType.SCORE_FRENZY;
        }
        
        private void addRandomMoreSpicy(){
            int roll = MathUtils.random(1,5);
            if(roll == 1)
                moreSpicy = Powerup.MoreSpicyType.BERSERK;
            else if(roll == 2)
                moreSpicy = Powerup.MoreSpicyType.INFINITE_ASSAULT;
            else if(roll == 3)
                moreSpicy = Powerup.MoreSpicyType.HYPERDRIVE;
            else if(roll == 4)
                moreSpicy = Powerup.MoreSpicyType.FORTRESS;
            else
                moreSpicy = Powerup.MoreSpicyType.DOUBLE_FORTUNE;
        }

        public int getCount(){
            return count;
        }

        public int getGold(){
            return gold;
        }

        public int getScore(){
            return score;
        }

        public int getAmmoReward(){
            return ammoRewardPercent;
        }

        public Array<Powerup.PowerupType> getPowerups(){
            return powerups;
        }

        public Array<Weapon.WeaponType> getWeapons(){
            return weapons;
        }

        public boolean hasJackpot(){
            return jackpot;
        }

        public boolean hasBiggerJackpot(){
            return biggerJackpot;
        }

        public boolean hasGodMode(){
            return godMode;
        }

        public int getHpBoost(){
            return hpBoost;
        }

        public Powerup.SpicyType getSpicy(){
            return spicy;
        }

        public Powerup.MoreSpicyType getMoreSpicy(){
            return moreSpicy;
        }

  
    }
    private Contents contents;
    LootCrate(CrateType type, Rectangle crate ){
        this.crate = new Rectangle(crate);
        this.type = type;
        this.contents = new Contents(type);
        opened = false;
    }

    public CrateType getType(){
        return type;
    }

    public void openCrate(){
        if(!opened){
            opened = true;
            openingTime = 0f;
            openedTimer = 0f;
        }
    }

    public Contents getContents(){
        return contents;
    }
    public static void loadAssetsOnce(){
        if(commonSheet != null)
            return;
        commonSheet = new Texture("loot/common/opening_sheet.png");
        rareSheet = new Texture("loot/rare/opening_sheet.png");
        superRareSheet = new Texture("loot/super_rare/opening_sheet.png");
        specialSheet = new Texture("loot/special/opening_sheet.png");

        dashboard3 = new Texture("loot/dashboard/3slot.png");
        dashboard4 = new Texture("loot/dashboard/4slot.png");
        dashboard5 = new Texture("loot/dashboard/5slot.png");
        dashboard6 = new Texture("loot/dashboard/6slot.png");
        dashboardSpecial5 = new Texture("loot/dashboard/5slot_special.png");
        dashboard5NoWeapon = new Texture("loot/dashboard/5slot_no_weapon.png");
        dashboard6NoWeapon = new Texture("loot/dashboard/6slot_no_weapon.png");

        commonOpening = createAnimation(commonSheet);
        rareOpening = createAnimation(rareSheet);
        superRareOpening = createAnimation(superRareSheet);
        specialOpening = createAnimation(specialSheet);
    }

    private static Animation<TextureRegion> createAnimation(Texture sheet){
        TextureRegion[][] split = TextureRegion.split(sheet,512,512);
        Array<TextureRegion> frames = new Array<>();

        for(int row=0;row<2;row++){
            for(int column=0;column<4;column++){
                frames.add(split[row][column]);
            }
        }
        return new Animation<TextureRegion>(FRAME_DURATION,frames,Animation.PlayMode.NORMAL);
    }

    private Animation<TextureRegion> getOpeningAnimation(){
        if(type == CrateType.COMMON)
            return commonOpening;

        if(type == CrateType.RARE)
            return rareOpening;

        if(type == CrateType.SUPER_RARE)
            return superRareOpening;

        return specialOpening;
    }

    public void drawCrate(SpriteBatch batch,float dt){
        Animation<TextureRegion> animation = getOpeningAnimation();
        if(opened)
            openingTime+=dt;
        TextureRegion frame;
        if(opened)
            frame = animation.getKeyFrame(openingTime,false);
        else
            frame = animation.getKeyFrame(0f,false);
        float visibleBodyWidth;
        if(type == CrateType.SPECIAL)
            visibleBodyWidth = 432f;
        else
            visibleBodyWidth = 400f;
        float targetWidth = crate.width;
        if(type == CrateType.SPECIAL)
            targetWidth = crate.width * 1.4f;

        float scale = targetWidth/visibleBodyWidth;
        float drawSize = 512f*scale;
        float feetX = crate.x+crate.width/2f;
        float feetY = crate.y;
        float drawX = feetX-256f*scale;
        float drawY = feetY-32f*scale;
        batch.draw(frame,drawX,drawY,drawSize,drawSize);
}

    public boolean isOpened(){
        return opened;
    }

    public boolean openingFinished(){
        return opened && getOpeningAnimation().isAnimationFinished(openingTime);
    }

    public Rectangle getCrate(){
        return crate;
    }

    public void update(float dt){
        if(!claimed)
            return;
        claimedTimer+=dt;
        if(claimedTimer >= CLAIMED_LIFETIME)
            readyToRemove = true;
    }

    public boolean isReadyToRemove(){
        return readyToRemove;
    }

    public Texture getDashboardTexture(){
        if(type == CrateType.SPECIAL)
            return dashboardSpecial5;
        int count = contents.getCount();
        boolean hasWeapon = contents.getWeapons().size > 0;
        if(count == 3)
            return dashboard3;
        if(count == 4)
            return dashboard4;
        if(count == 5){
            if(hasWeapon)
                return dashboard5;
            else
                return dashboard5NoWeapon;
        }
        if(count == 6){
            if(hasWeapon)
                return dashboard6;
            else
                return dashboard6NoWeapon;
        }
        return dashboard6NoWeapon;
    }

    public Contents claim(){
        if(claimed)
            return null;

        claimed = true;
        claimedTimer = 0f;
        
        return contents;
       
    }

    public boolean isClaimed(){
        return claimed;
    }  





    public static void main(String[] args){

    }
}