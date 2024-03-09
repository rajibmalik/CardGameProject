package events;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import abilities.HornOfTheForsaken;
import abilities.UnitAbility;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import controllers.TileHighlightController;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.CardWrapper;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.TileLocator;
import abilities.UnitAbility;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {

	@Override

	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Player currentPlayer = gameState.getCurrentPlayer();
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		// Current tile clicked
		TileWrapper tileWrapper = gameState.getBoard().getBoard()[tilex][tiley];
		Tile tile = tileWrapper.getTile();

		if (currentPlayer == gameState.getHumanPlayer()) {

			// Will run if a card was clicked prior to tile click
			if (gameState.hasCardBeenClicked(gameState)) {
				handleCardClick(out, gameState, tileWrapper);
			// Will run if a tile containing a friendly unit was clicked
			} else if (tileWrapper.getHasUnit() == true && TileHighlightController.getTileHighlighted(tile) != 2) {
				handleUnitClick(out, gameState, currentPlayer, tileWrapper);
			// Will run if an empty white highlighted tile was clicked to move a unit
			} else if (TileHighlightController.getTileHighlighted(tile) == 1 && !tileWrapper.getHasUnit()) {
				handleTileClick(out, gameState, tileWrapper);
			// Will run if a tile containing an enemy unit in attack range was clicked
			} else if (TileHighlightController.getTileHighlighted(tile) == 2 && tileWrapper.getHasUnit()) {
				System.out.println("HANDLING ATTACK");
				handleTileClickAttack(out, gameState, tileWrapper);
			// Resetting board highlight in all other instances
			} else {
				TileHighlightController.removeBoardHighlight(out, gameState);
				gameState.unclickAllUnits(gameState);
			}
		}
	}

	private void handleCardClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		CardWrapper clickedCard = gameState.getClickedCard(gameState);
		// If the tile is highlighted, and the card can be played, then play card
		if (TileHighlightController.getTileHighlighted(tile) == 1 || TileHighlightController.getTileHighlighted(tile) == 2 
				&& PlayerController.canPlayCard(gameState, clickedCard)) {
			if (clickedCard.getName().equals("Wraithling Swarm")) {
				SpellCard spellCard = (SpellCard) clickedCard;
				spellCard.applySpellAbility(out, gameState, tileWrapper);
		        } else {
		            // Play other cards
		        PlayerController.playCard(out,gameState,clickedCard,tileWrapper);
		        }
			System.out.println("A card was played");
		} else {
			clickedCard.setHasBeenClicked(false);
			TileHighlightController.removeBoardHighlight(out, gameState);
			System.out.println("The card was unclicked");
		}
		gameState.unclickAllUnits(gameState);
	}

	private void handleUnitClick(ActorRef out, GameState gameState, Player currentPlayer, TileWrapper tileWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileHighlightController.removeBoardHighlight(out, gameState);
		UnitWrapper unitWrapper = tileWrapper.getUnit();
		//make sure not other units are clicked
		gameState.unclickAllUnits(gameState);
		// if friendly unit is clicked and it hasn't moved or attacked already,
		// highlight tiles for movement			
		if (unitWrapper != null && unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			
			if (TileLocator.getAdjacentTilesWithProvoke(board, unitWrapper).isEmpty()) {
				System.out.println("Tiles highlighted for movement");
				TileHighlightController.setUnitMovementTileHighlight(out, gameState, unitWrapper);
				TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			} else {
				System.out.println("Tiles highlighted for movement");
				TileHighlightController.highlightProvokeEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			}
			// testing avatar bug
			System.out.println("Here are the human players units:");
			for (UnitWrapper unit : gameState.getHumanPlayer().getUnits()) {
				System.out.println(unit.getName() + " " + unit.getHasBeenClicked());
			}

			System.out.println("Here are the ai players units:");
			for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
				System.out.println(unit.getName() + " " + unit.getHasBeenClicked());
			}

			// If the unit has moved but not yet attacked, then highlight enemies in range
		} else if (unitWrapper != null && unitWrapper.getHasMoved() == true && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			if (TileLocator.getAdjacentTilesWithProvoke(board, unitWrapper).isEmpty()) {
				TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			} else {
				TileHighlightController.highlightProvokeEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			}
		}
		
		//testing
		if(gameState.getClickedUnit(gameState)!=null) {
			System.out.println("The clicked unit is " + gameState.getClickedUnit(gameState).getName());
		}
	}

	private void handleTileClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		if (gameState.hasUnitBeenClicked(gameState)) {
			UnitWrapper unitWrapper = gameState.getClickedUnit(gameState);
			// If the unit has not moved or attacked, then move unit
			if (unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false) {
				System.out.println("The unit" + unitWrapper.getName() + "has moved");
				UnitController.moveUnit(out,gameState,unitWrapper,tileWrapper);
			}
		}
	}
	
	private void handleTileClickAttack(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		List<TileWrapper> tiles = TileLocator.getAdjacentTiles(gameState.getBoard().getBoard(), gameState.getClickedUnit(gameState));
		UnitWrapper attackingUnitWrapper = gameState.getClickedUnit(gameState);
	
		if (tiles.contains(tileWrapper)) {
			UnitWrapper unitWrapperAttacked = tileWrapper.getUnit();
			// Attack unit
			UnitController.attackUnit(out, gameState, attackingUnitWrapper, unitWrapperAttacked);
		} else {
			TileWrapper[][] board = gameState.getBoard().getBoard();
			UnitWrapper clickedUnit = gameState.getClickedUnit(gameState);
			TileWrapper tileWrapperToMove = null;

			List<TileWrapper> validTiles = TileLocator.getTilesForUnitMovement(clickedUnit, board);
			List<TileWrapper> tilesAdjacentToAttackedUnit = TileLocator.getAdjacentTiles(board, tileWrapper);

			for (TileWrapper tile:validTiles) {
				if (tilesAdjacentToAttackedUnit.contains(tile)) {
					tileWrapperToMove = tile;
				}
			}
	
			// If a valid tile to move to, move and attack 
			if (tileWrapperToMove != null) {
				UnitController.moveUnit(out, gameState, attackingUnitWrapper, tileWrapperToMove);
				UnitController.attackUnit(out, gameState, attackingUnitWrapper, tileWrapper.getUnit());
			} else {
				System.out.println("No valid tile to move to that is also attackable.");
			}
		}
	}
	

}
