package structures.basic;

/**
 * This class is the backend representation of a card.
 * It holds information about the card's mana cost, name, click status,
 * associated card object, and a unique identifier.
 * @author Darby Christy, Eldhos Thomas
 */

public class CardWrapper {
    private int manaCost;
    private String name;
    private boolean hasBeenClicked;
    private Card card;
    private final int id;
    public static int nextId = 1;

    public CardWrapper(int manaCost, String name, Card card) {
    	this.card = card;
        this.manaCost = manaCost;
        this.name = name;
        this.hasBeenClicked = false;
        this.id = nextId++;
    }

    public int getId() {
        return this.id;
    }
    
    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public int getManaCost() {
        return this.manaCost;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasBeenClicked() {
        return hasBeenClicked;
    }

    public void setHasBeenClicked(boolean hasBeenClicked) {
        this.hasBeenClicked = hasBeenClicked;
    }
}



