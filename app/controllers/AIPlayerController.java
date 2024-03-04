package controllers;

import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
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
        playUnitCard(out, gameState);
        // playSpellCard(out, gameState);
        attackUnits(out, gameState);
        endTurn(gameState);
    }

    public void playUnitCard(ActorRef out, GameState gameState) {
        if (canPlayCard(gameState, true)) {
            UnitCard unitCard = getLowestCostUnitCard();
          
            // ArrayList<TileWrapper> tileWrappers = getValidTiles(gameState);
            ArrayList<TileWrapper> tileWrappers = TileLocator.getValidSpawnTiles(gameState);
            TileWrapper tileWrapper = tileWrappers.get(0);
            Unit unit = UnitController.renderUnit(out, unitCard, tileWrapper.getTile());

            UnitController.createUnitWrapper(unit, unitCard, tileWrapper, gameState.getCurrentPlayer());
            removeCardFromHand(unitCard);
            deductAndRenderMana(gameState, out, unitCard);
        } 
    }

    // test 
    public void playSpellCard(ActorRef out, GameState gameState) {
        if (canPlayCard(gameState, false)) {
            SpellCard spellCard = getLowestCostSpellCard();
        }
    }

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
        setUnits(AIunits);
    }

    public Unit renderUnit(ActorRef out, UnitCard unitCard, Tile tile) {
        Unit unit = UnitController.renderUnit(out, unitCard, tile);
		return unit;
    }

    private TileWrapper getHumanAvatarTile(GameState gameState) {
        ArrayList<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
        for (UnitWrapper unitWrapper : units) {
            if (unitWrapper instanceof Avatar) {
                return unitWrapper.getTile();
            }
        }
        return null; // Return null if the avatar tile is not found
    }

    public boolean canPlayCard(GameState gameState, boolean isUnitCard) {
        if (isUnitCard) {
            return (super.getMana() >= getLowestCostUnitCardPrice() && (!TileLocator.getValidSpawnTiles(gameState).isEmpty()));
        } else {
            return (super.getMana() >= getLowestCostSpellCard().getManaCost());
        }
    }

    public void setUnits(GameState gameState, ArrayList<UnitWrapper> units) {
        super.setUnits(units);
    }

    private int getLowestCostUnitCardPrice() {
        ArrayList<UnitCard> unitCards = getUnitCards();
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
        ArrayList<UnitCard> unitCards = getUnitCards();
        UnitCard highestManaCost = unitCards.get(0);

        for (UnitCard unitCard: unitCards) {
            if (unitCard.getManaCost() > highestManaCost.getManaCost()) {
                highestManaCost = unitCard;
            }
        }

        return highestManaCost;
    }

    private UnitCard getLowestCostUnitCard() {
        ArrayList<UnitCard> unitCards = getUnitCards();
        UnitCard lowestManaCost = unitCards.get(0);

        for (UnitCard unitCard: unitCards) {
            if (unitCard.getManaCost() < lowestManaCost.getManaCost()) {
                lowestManaCost = unitCard;
            }
        }

        return lowestManaCost;
    }

    private ArrayList<UnitCard> getUnitCards() {
        ArrayList<UnitCard> unitCards = new ArrayList<>();

        for (CardWrapper cardWrapper: super.getPlayerHand().getHand()) {
            if (cardWrapper instanceof UnitCard) {
                UnitCard unitCard = (UnitCard) cardWrapper;
                unitCards.add(unitCard);
            }
        }

        return unitCards;
    }

    private ArrayList<SpellCard> getSpellCards() {
        ArrayList<SpellCard> spellCards = new ArrayList<>();

        for (CardWrapper cardWrapper: super.getPlayerHand().getHand()) {
            if (cardWrapper instanceof SpellCard) {
                SpellCard spellCard = (SpellCard) cardWrapper;
                spellCards.add(spellCard);
            }
        }

        return spellCards;
    }

    private SpellCard getLowestCostSpellCard() {
        ArrayList<SpellCard> spellCards = getSpellCards();
        SpellCard lowestManaCost = spellCards.get(0);

        for (SpellCard spellCard: spellCards) {
            if (spellCard.getManaCost() < lowestManaCost.getManaCost()) {
                lowestManaCost = spellCard;
            }
        }

        return lowestManaCost;
    }

    public void removeCardFromHand(CardWrapper cardWrapper) {
        ArrayList<CardWrapper> hand = super.getPlayerHand().getHand();

        if (hand.contains(cardWrapper)) {
            hand.remove(cardWrapper);
        }
    }

    public void endTurn(GameState gameState) {
        gameState.switchPlayer();
        super.drawCard();
    }

    private void deductAndRenderMana(GameState gameState, ActorRef out, CardWrapper cardWrapper) {
		deductManaFromBackEnd(gameState, cardWrapper);
		renderManaOnFrontEnd(out, gameState);
	}

	private void deductManaFromBackEnd(GameState gameState, CardWrapper cardWrapper) {
        AIPlayerController.deductMana(gameState.getCurrentPlayer(), cardWrapper);
	}

	private void renderManaOnFrontEnd(ActorRef out, GameState gameState) {
		BasicCommands.setPlayer2Mana(out, gameState.getCurrentPlayer());
	}
}
