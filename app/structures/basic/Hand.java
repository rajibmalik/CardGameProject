package structures.basic;

import java.util.ArrayList;

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

    public void removeCard(int index) { 
        hand.remove(index);
    }
    
    public CardWrapper getCard (int index) {
    	return hand.get(index);
    }
}  