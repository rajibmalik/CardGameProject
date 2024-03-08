package controllers;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.TileLocator;

/**
 * This class is responsible for the AI decision making during the AI players turn. This includes
 * playing unit cards, spell cards, moving units, attacking units and ending turn.
 * @author Rajib Malik
*/

public class AIPlayerController extends PlayerController {
    
    public AIPlayerController(Player player) {
        super(player);
    }

   /**
     * This method executes the AI player's turn, performing actions such as playing a unit card, 
     * playing a spell card, moving units, attacking units and ending the turn.
    */
    public void takeTurn(ActorRef out, GameState gameState) {
        ArrayList<CardWrapper> hand = super.getPlayerHand().getHand();
        // test 
        System.out.println("AI Player Hand:");
        for (CardWrapper cardWrapper:hand) {
            System.out.print(cardWrapper.getName() + ", " );
        }
        System.out.println();

        playUnitCard(out, gameState);
        playSpellCard(out, gameState);
        moveUnits(out, gameState);
        attackUnits(out, gameState);
        endTurn(gameState);
        BasicCommands.addPlayer1Notification(out, "Player 1 turn", 1);
    }

    /**
     * This method executes logic for playing a UnitCard. 
     * Checks for valid tile for placement using TileLocator, 
     * if there is a valid tile, UnitController updates the front and backend representing unit creation. 
     * If played, card is removed from the hand alongisde appropriate amount of mana from the player.
    */
    public void playUnitCard(ActorRef out, GameState gameState) {
        while (canPlayCard(gameState, true)) {
            UnitCard unitCard = getLowestCostUnitCard();

            List<TileWrapper> tileWrappers = new ArrayList<>();

            if (unitCard.getName().equals("Young Flamewing") && !TileLocator.getValidTilesAdjacentToHumanAvatar(gameState).isEmpty()) {
                tileWrappers = TileLocator.getValidTilesAdjacentToHumanAvatar(gameState);
                TileWrapper tileWrapper = tileWrappers.get(0);
                Unit unit = UnitController.renderUnit(out, unitCard, tileWrapper.getTile());
                UnitController.createUnitWrapper(unit, unitCard, tileWrapper, gameState.getAIPlayer());
            } else {
                tileWrappers = TileLocator.getValidSpawnTiles(gameState);
                TileWrapper tileWrapper = tileWrappers.get(0);
                Unit unit = UnitController.renderUnit(out, unitCard, tileWrapper.getTile());
                UnitController.createUnitWrapper(unit, unitCard, tileWrapper, gameState.getAIPlayer());
            }
            super.applyOpeningGambit(out, gameState); 
            super.removeCardFromHand(unitCard);
            super.deductAndRenderMana(gameState, out, unitCard);
        }
    }

    /**
     * Checks if SpellCards in the players hand can be played, 
     * static method from SpellController.
     * If it can be played, plays the spell updating the front and backend, 
     * removes the card from the hand and the approrpiate mana from the player
    */
    public void playSpellCard(ActorRef out, GameState gameState) {
        System.out.println("Checking if can play spell card");
        ArrayList<SpellCard> spellCards = getSpellCards();

        for (SpellCard spellCard: spellCards) {
            String spellName = spellCard.getName();
           
            if (spellName.equals("Beamshock") && SpellController.canPlayAttackingSpell(gameState)){
                playAndRemoveSpell(out, gameState, spellCard);
            } else if (spellName.equals("Sundrop Elixir") && SpellController.canPlaySundropElixir(gameState)) {
                playAndRemoveSpell(out, gameState, spellCard);
            } else if (spellName.equals("Truestrike") && SpellController.canPlayAttackingSpell(gameState)) {
                playAndRemoveSpell(out, gameState, spellCard);
            } 
        }       
    }

    /**
      * Executes logic for playing a SpellCard.
      * If able to play a SpellCard, applys it to both frontend and backend using static method from SpellController.
      * Then deducts mana before removing it from the hand.
     * @param spelLCard reference to a SpellCard object 
    */

    public void playAndRemoveSpell(ActorRef out, GameState gameState, SpellCard spellCard) {
        switch (spellCard.getName()) {
            case "Beamshock":
                BasicCommands.addPlayer1Notification(out, "Playing Beamshock", 1);
                System.out.println("Playing Beamshock");
                SpellController.playBeamShock(out, gameState, spellCard);
                break;
            case "Sundrop Elixir":
                BasicCommands.addPlayer1Notification(out, "Playing Sundrop Elixir", 1);
                System.out.println("Sundrop Elixir");
                SpellController.playSundropElixir(out, gameState, spellCard);
                break;  
            case "Truestrike":
                BasicCommands.addPlayer1Notification(out, "Playing Truestrike", 1);
                System.out.println("Truestrike");
                SpellController.playTrueStrike(out, gameState, spellCard);
                break;
        }
        super.removeCardFromHand(spellCard);
        super.deductAndRenderMana(gameState, out, spellCard);
    }

    /**
     * Executes logic for unit attacks.
     * Iterates through the player's units to check if they can attack.
     * If they can attack, retrieves adjacent enemy units using TileLocator.
     * Highlights the attacking unit's tile and updates front and backends
     * after damage calculation if an enemy unit is found.
    */
    public void attackUnits(ActorRef out, GameState gameState) {
        System.out.println("Attacking human units");
        ArrayList<UnitWrapper> AIunits = new ArrayList<>(gameState.getAIPlayer().getUnits());

        for(UnitWrapper AIUnit: AIunits) {
            System.out.println(AIUnit.getName() + ", " + AIUnit.getHasAttacked()); // test 
            if (!AIUnit.getHasAttacked()) {
                ArrayList<TileWrapper> adjacentEnemyUnits = TileLocator.getAdjacentTilesWithEnemyUnit(gameState, AIUnit);
                
                if (!adjacentEnemyUnits.isEmpty()) {
                    Tile tile = AIUnit.getTile().getTile();
                    TileHighlightController.highlightTileAttacking(out, tile);
                    UnitWrapper playerUnit = adjacentEnemyUnits.get(0).getUnit();
                    UnitController.attackUnit(out, gameState, AIUnit, playerUnit);
                    TileHighlightController.highlightTileNormal(out, tile);
                }
            }
        }
        super.setUnits(AIunits);
    }

    /**
     * Checks if the player has enough mana to play a card and if there is a valid tile to play a UnitCard.
     * @return
    */
    public boolean canPlayCard(GameState gameState, boolean isUnitCard) {
        if (isUnitCard) {
            return (super.getMana() >= getLowestCostUnitCardPrice() && (!TileLocator.getValidSpawnTiles(gameState).isEmpty()));
        } else {
            if (super.getLowestCostSpellCard() != null) {
                return (super.getMana() >= getLowestCostSpellCard().getManaCost());
            }
            return false;
        }
    }

    /**
     * This class is a helper method to return the lowest cost of a UnitCard from the players hand
     * @return
    */
    private int getLowestCostUnitCardPrice() {
        ArrayList<UnitCard> unitCards = super.getUnitCards();
        int lowestManaCost = Integer.MAX_VALUE;
        if (!unitCards.isEmpty()) {
            lowestManaCost = unitCards.get(0).getManaCost();
        }
        
        for (UnitCard unitCard: unitCards) {
            if (unitCard.getManaCost() < lowestManaCost) {
                lowestManaCost = unitCard.getManaCost();
            }
        }
        return lowestManaCost;
    }

    /**
     * This class is a helper method to retrieve the lowest cost UnitCard from the players hand
     * @return
    */
    private UnitCard getLowestCostUnitCard() {
        ArrayList<UnitCard> unitCards = super.getUnitCards();
        UnitCard lowestManaCost = unitCards.get(0);

        for (UnitCard unitCard: unitCards) {
            if (unitCard.getManaCost() < lowestManaCost.getManaCost()) {
                lowestManaCost = unitCard;
            }
        }
        return lowestManaCost;
    }

    /**
     * This class is a helper method to retrieve the highest cost UnitCard from the players hand
     * @return
    */
    private UnitCard getHighestCostUnitCard() {
        ArrayList<UnitCard> unitCards = super.getUnitCards();
        UnitCard highestManaCost = unitCards.get(0);

        for (UnitCard unitCard: unitCards) {
            if (unitCard.getManaCost() > highestManaCost.getManaCost()) {
                highestManaCost = unitCard;
            }
        }
        return highestManaCost;
    }

    /**
     * Executes the logic for moving the player's units.
     * Iterates through the player's units and moves them to the nearest unoccupied tile
     * to the enemy avatar if they have not moved and are not adjacent to an enemy unit.
    */
    public void moveUnits(ActorRef out, GameState gameState) {
        TileWrapper[][] board = gameState.getBoard().getBoard();
        ArrayList<UnitWrapper> units = super.player.getUnits();

        for (UnitWrapper unitWrapper: units) {
            if (!unitWrapper.getHasMoved() && TileLocator.isAdjacentToHumanUnit(gameState, unitWrapper) == false) {
                ArrayList<TileWrapper> validTiles = TileLocator.getAdjacentTilesWithoutUnit(gameState, unitWrapper);
                TileWrapper tileWrapper = TileLocator.getClosestTileToHumanAvatar(gameState, validTiles);

                UnitController.moveUnitBackend(unitWrapper, tileWrapper);
                // gives time for backend to process before rendering frontend
                try {Thread.sleep(2500);} catch (InterruptedException e) {e.printStackTrace();} 
                UnitController.moveUnitFrontend(out, unitWrapper, tileWrapper.getTile());
            }
        }
    }

    /**
     * This class is responsible for ending the players turn and drawing a card into their deck from their hand
    */
    public void endTurn(GameState gameState) {
        gameState.switchPlayer();
        super.drawCard();
    }

}
