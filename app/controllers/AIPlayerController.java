package controllers;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
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

public class AIPlayerController extends PlayerController {
    
    public AIPlayerController(Player player) {
        super(player);
    }

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
        attackUnits(out, gameState);
        endTurn(gameState);
    }

    // public void playUnitCard(ActorRef out, GameState gameState) {
    //     while (canPlayCard(gameState, true)) {
    //         UnitCard unitCard = getLowestCostUnitCard();
          
    //         // ArrayList<TileWrapper> tileWrappers = getValidTiles(gameState);
    //         ArrayList<TileWrapper> tileWrappers = TileLocator.getValidSpawnTiles(gameState);
    //         TileWrapper tileWrapper = tileWrappers.get(0);
    //         Unit unit = UnitController.renderUnit(out, unitCard, tileWrapper.getTile());

    //         UnitController.createUnitWrapper(unit, unitCard, tileWrapper, gameState.getAIPlayer());
    //         super.removeCardFromHand(unitCard);
    //         super.deductAndRenderMana(gameState, out, unitCard);
    //     }
    // }

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
          
        
            super.removeCardFromHand(unitCard);
            super.deductAndRenderMana(gameState, out, unitCard);
        }
    }

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

    public void playAndRemoveSpell(ActorRef out, GameState gameState, SpellCard spellCard) {
        switch (spellCard.getName()) {
            case "Beamshock":
                System.out.println("Playing Beamshock");
                SpellController.playBeamShock(out, gameState, spellCard);
                break;
            case "Sundrop Elixir":
                System.out.println("Sundrop Elixir");
                SpellController.playSundropElixir(out, gameState, spellCard);
                break;  
            case "Truestrike":
                System.out.println("Truestrike");
                SpellController.playTrueStrike(out, gameState, spellCard);
                break;
        }
        super.removeCardFromHand(spellCard);
    }

    public void attackUnits(ActorRef out, GameState gameState) {
        System.out.println("Attacking human units");
        ArrayList<UnitWrapper> AIunits = new ArrayList<>(gameState.getAIPlayer().getUnits());

        // test 

        for (UnitWrapper unitWrapper:AIunits) {
            System.out.println(unitWrapper.getName() + ", " + "has attacked " + unitWrapper.getHasAttacked() + " has moved " + unitWrapper.getHasMoved());
        }
       
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

    public void endTurn(GameState gameState) {
        gameState.switchPlayer();
        super.drawCard();
    }
}
