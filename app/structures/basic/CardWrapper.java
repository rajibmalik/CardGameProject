package structures.basic;

public class CardWrapper {
    private int manaCost;
    private String name;
    private boolean hasBeenClicked;
    private Card card;

    public CardWrapper(int manaCost, String name, Card card) {
    	this.card = card;
        this.manaCost = manaCost;
        this.name = name;
        this.hasBeenClicked = false;
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

