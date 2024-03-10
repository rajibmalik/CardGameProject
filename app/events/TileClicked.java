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
				PlayerController.handleCardClick(out, gameState, tileWrapper);
			// Will run if a tile containing a friendly unit was clicked
			} else if (tileWrapper.getHasUnit() == true && TileHighlightController.getTileHighlighted(tile) != 2) {
				UnitController.handleUnitClick(out, gameState, currentPlayer, tileWrapper);
			// Will run if an empty white highlighted tile was clicked to move a unit
			} else if (TileHighlightController.getTileHighlighted(tile) == 1 && !tileWrapper.getHasUnit()) {
				UnitController.handleTileClick(out, gameState, tileWrapper);
			// Will run if a tile containing an enemy unit in attack range was clicked
			} else if (TileHighlightController.getTileHighlighted(tile) == 2 && tileWrapper.getHasUnit()) {
				System.out.println("HANDLING ATTACK");
				UnitController.handleTileClickAttack(out, gameState, tileWrapper);
			// Resetting board highlight in all other instances
			} else {
				TileHighlightController.removeBoardHighlight(out, gameState);
				gameState.unclickAllUnits(gameState);
			}
		}
	}



	

	

}
