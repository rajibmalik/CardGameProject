package controllers;

import java.util.ArrayList;

import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class PlayerController {
    Player player;
    private int turn; 
    private static final int MAX_MANA = 9; // Maximum mana value

    public PlayerController(Player player) {
        this.player = player;
        this.turn=1;
    }

    public boolean drawCard() {
        if (player.getHand().getHand().size() < 5)  {
            CardWrapper cardWrapper = player.getDeck().getTopCard();
            player.getHand().addCard(cardWrapper);

            return true;
        }

        return false;
    }

    public void drawInitialHand() {
        for (int i = 0; i < 3; i++) {
            CardWrapper cardWrapper = player.getDeck().getTopCard();
            player.getHand().addCard(cardWrapper);
        }
    }
    public void setTurnMana() {
        
        int calculatedMana = 1 + turn;
        
        if (calculatedMana > MAX_MANA) {
            player.setMana(MAX_MANA);
        } else {
            player.setMana(calculatedMana);
        }
    }
    
    public void clearMana() {
    	player.setMana(0);
    }
    public int getMana() {
    	return player.getMana();
    }

    public Player getPlayer() {
        return this.player;
    }
    
    public void nextTurn() {
        turn++;
        setTurnMana();
    }

    public Deck getPlayerDeck() {
        return this.player.getDeck();
    } 
    
    public Hand getPlayerHand() {
        return this.player.getHand();
    }

    public int getTurn() {
        return this.turn;
    }

    public boolean canPlayCard(Player player, CardWrapper card) {
        int manaCost = card.getManaCost();
        int currentMana = player.getMana();

        if (currentMana >= manaCost) {
            return true;
        } else {
            System.out.println("Insufficient mana to play this card.");
        }

        return false;
    }
    
    public static void deductMana(Player player, CardWrapper card) {
        int manaCost = card.getManaCost();
        int currentMana = player.getMana();
        
        if (currentMana >= manaCost) {
            player.setMana(currentMana - manaCost);
        } else {
            System.out.println("Insufficient mana to play this card.");
        }
    }

    public void removeCardFromHand(int id) {
        ArrayList<CardWrapper> hand = player.getHand().getHand();
    
        // Remove cardWrapper if the cardWrapper id is equal to id
        hand.removeIf(cardWrapper -> cardWrapper.getId() == id);
    }

    public ArrayList<UnitWrapper> getUnits() {
        return player.getUnits();
    }
}
