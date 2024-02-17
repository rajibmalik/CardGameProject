package controllers;

import java.util.ArrayList;

import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;

public class PlayerController {
    Player player;

    public PlayerController(Player player) {
        this.player = player;
    }
    
    public void drawInitialHand() {
        for (int i = 0; i < 3; i++) {
            CardWrapper cardWrapper = player.getDeck().getTopCard();
            player.getHand().addCard(cardWrapper);
        }
    }

    public Deck getPlayerDeck() {
        return this.player.getDeck();
    } 

}
