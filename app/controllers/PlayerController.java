package controllers;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.TileWrapper;
import structures.basic.UnitCard;
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

    public void removeCardFromHand(CardWrapper cardWrapper) {
        ArrayList<CardWrapper> hand = player.getHand().getHand();

        if (hand.contains(cardWrapper)) {
            hand.remove(cardWrapper);
        }
    }

    public ArrayList<UnitWrapper> getUnits() {
        return player.getUnits();
    }

    public void setUnits(ArrayList<UnitWrapper> units) {
        this.player.setUnits(getUnits());
    }

    public ArrayList<SpellCard> getSpellCards() {
        ArrayList<SpellCard> spellCards = new ArrayList<>();

        for (CardWrapper cardWrapper: this.player.getHand().getHand()) {
            if (cardWrapper instanceof SpellCard) {
                SpellCard spellCard = (SpellCard) cardWrapper;
                spellCards.add(spellCard);
            }
        }

        return spellCards;
    }

    public ArrayList<UnitCard> getUnitCards() {
        ArrayList<UnitCard> unitCards = new ArrayList<>();

        for (CardWrapper cardWrapper: this.player.getHand().getHand()) {
            if (cardWrapper instanceof UnitCard) {
                UnitCard unitCard = (UnitCard) cardWrapper;
                unitCards.add(unitCard);
            }
        }

        return unitCards;
    }

    public SpellCard getLowestCostSpellCard() {
        ArrayList<SpellCard> spellCards = getSpellCards();
        if (spellCards.isEmpty()) {
            return null;
        }
    
        SpellCard lowestManaCost = spellCards.get(0);

        for (SpellCard spellCard: spellCards) {
            if (spellCard.getManaCost() < lowestManaCost.getManaCost()) {
                lowestManaCost = spellCard;
            }
        }

        return lowestManaCost;
    }

     public void deductAndRenderMana(GameState gameState, ActorRef out, CardWrapper cardWrapper) {
        deductMana(gameState.getCurrentPlayer(), cardWrapper);
        BasicCommands.setPlayer2Mana(out, gameState.getCurrentPlayer());
        try {Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();} // these cause processing to wait for a number of milliseconds.
	}
    
}
