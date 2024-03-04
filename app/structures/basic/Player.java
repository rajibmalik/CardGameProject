package structures.basic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

// A basic representation of the Player. A player has health and mana.
 
public class Player {

	private int health;
	private int mana;
	@JsonIgnore
	private Deck deck;
	@JsonIgnore
	private Hand hand;
	@JsonIgnore
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

	public void addUnit(UnitWrapper unit) {
		this.units.add(unit);
	}

    // public void removeUnit(UnitWrapper unit) {	// this should be in a controller Class
	// 	Iterator<UnitWrapper> iterator = this.units.iterator();

	// 	while (iterator.hasNext()) {
	// 		UnitWrapper unitInstance = iterator.next();
	// 		if (unitInstance.getId() == unit.getId()) {
	// 			iterator.remove();
	// 			break;
	// 		}
	// 	}
    // }

	public ArrayList<UnitWrapper> getUnits() {
		return this.units;
	}

	public void setUnits(ArrayList<UnitWrapper> units) {
		this.units = units;
	}
	
}
