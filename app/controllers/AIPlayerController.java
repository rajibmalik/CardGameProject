package controllers;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.CardWrapper;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;

public class AIPlayerController extends PlayerController {
    UnitController unitController = new UnitController();
   
    public AIPlayerController(Player player) {
        super(player);
    }

    public void playUnitCard(ActorRef out, GameState gameState) {
        if (canPlayUnitCard(gameState)) {
            UnitCard unitCard = getLowestCostUnitCard();
            ArrayList<TileWrapper> tileWrappers = getValidTiles(gameState);
            TileWrapper tileWrapper = tileWrappers.get(0);
            Unit unit = unitController.renderUnit(out, unitCard, tileWrapper.getTile());

            unitController.createUnitWrapper(unit, unitCard, tileWrapper, gameState.getCurrentPlayer());
            removeCardFromHand(unitCard);
            deductAndRenderMana(gameState, out, unitCard);
        } 
    }

    public Unit renderUnit(ActorRef out, UnitCard unitCard, Tile tile) {
        String config = unitCard.getCard().getUnitConfig();

		Unit unit = BasicObjectBuilders.loadUnit(config, UnitWrapper.nextId, Unit.class);
		unit.setPositionByTile(tile);
		BasicCommands.drawUnit(out, unit, tile);

		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, unitCard.getAttack());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, unitCard.getHealth());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

        // for time between spawning units
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
    }

    public ArrayList<TileWrapper> getValidTiles(GameState gameState) {
        ArrayList<TileWrapper> validTiles = new ArrayList<>();
        TileWrapper[][] board = gameState.getBoard().getBoard();
        List<UnitWrapper> units = gameState.getAIPlayer().getUnits();

        for (UnitWrapper unit : units) {
            TileWrapper unitPosition = unit.getTile();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int newX = unitPosition.getXpos() + i;
                    int newY = unitPosition.getYpos() + j;

                    // Check if the new coordinates are within the bounds of the board
                    if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                        TileWrapper tileWrapper = board[newX][newY];

                        // Check if the tile is not already occupied by a unit
                        if (!isTileOccupied(board, newX, newY)) {
                            validTiles.add(tileWrapper);
                        }
                    }
                }
            }
        }

        return validTiles;
    }

    private static boolean isTileOccupied(TileWrapper[][] board, int x, int y) {
		// Check if there is a unit on the specified tile
		return board[x][y].getUnit() != null;
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

    public boolean canPlayUnitCard(GameState gameState) {
        if (super.getMana() >= getLowestCostUnitCardPrice() && (!getValidTiles(gameState).isEmpty())) {
            return true;
        }

        return false;
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
