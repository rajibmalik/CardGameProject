package structures.basic;

import java.util.ArrayList;

/**
 * This class represents a player's hand, which is an array of CardWrapper objects.
 * It provides methods to retrieve the hand, adding a card to the hand, and getting a card
 * from a specific index in the hand.
 * @author Darby Christy, Matthew Wilson 
 */

public class Hand {
    private ArrayList<CardWrapper> hand;
 
    public Hand() {
        this.hand = new ArrayList<>();
    }

    public ArrayList<CardWrapper> getHand() {
        return this.hand;
    }

    public void addCard(CardWrapper card) {
        hand.add(card);
    }

    public CardWrapper getCard (int index) {
    	return hand.get(index);
    }
}  