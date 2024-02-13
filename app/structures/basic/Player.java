package structures.basic;
import java.util.ArrayList;
import java.util.List;

// A basic representation of the Player. A player has health and mana.
 
public class Player {

	private int health;
	private int mana;
	private Deck deck;
	private Hand hand;
	private ArrayList<UnitWrapper> units;
	
	public Player(Deck deck, Hand hand) {
		this.health = 20;
		this.mana = 0;
		this.deck = deck;
		this.hand = hand;
		this.units = new ArrayList<>();
	}
	public int getHealth() {
		return health;
	}
	public void setHealth(int health) {
		this.health = health;
	}
	public int getMana() {
		return mana;
	}
	public void setMana(int mana) {
		this.mana = mana;
	}
	public Deck getDeck() {
		return deck;
	}
	public void setDeck(Deck deck) {
        this.deck = deck;
    }
    public Hand getHand() {
        return hand;
    }
    public void setHand(Hand hand) {
        this.hand = hand;
    }


    public void addUnit(ArrayList<UnitWrapper> units) {
        this.units.add(units);
    }
    public void removeUnit(UnitWrapper units) {	//would need to override compareTo for this to work
    	this.units.remove(units);	//not saying what unit to remove, will add functionality later
    }
	
}
