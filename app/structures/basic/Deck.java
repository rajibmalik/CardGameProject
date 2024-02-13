package structures.basic;

public class Deck{
    private String player; 
    private CardWrapper[] HumanDeck;
    private CardWrapper[] AIDeck; 

    public Deck(String playerName) {
        this.player=playerName;
        
        if (playerName.equals("human")) {    //decide whether to change 'human' & 'AI' to 0 and 1
            CreateHumanDeck();
        }
        if (playerName.equals("AI")) {
            CreatePlayerDeck();   
        }
    }
    public void CreateHumanDeck() {
        //need to decide how we are creating the human and AI decks
    }
    public void CreatePlayerDeck() {
    }

    public CardWrapper[] getHumanDeck() {
        return this.HumanDeck;
    }
    public void setHumanDeck() {
        //add implementation
    }
    public CardWrapper[] getAIDeck() {
        return this.AIDeck;
    }
    public void setAIDeck() {
        //add implementation
    }
}