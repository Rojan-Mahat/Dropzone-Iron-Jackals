package rojan.shooter;

public class PlayerProfile{
    private Inventory inventory;
    private int bestScore;

    PlayerProfile(){
        inventory = new Inventory();
    }

    public Inventory getInventory(){
        return inventory;
    }

    public int getBestScore(){
        return bestScore;
    }

    public void updateBestScore(int score){
        if(score > bestScore)
            bestScore = score;
    }
}