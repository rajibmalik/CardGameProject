package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import controllers.TileHighlightController;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Hand;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case somewhere that is not on a card tile or the end-turn button.
 * 
 * { messageType = “otherClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		Hand playerHand = gameState.getPlayerHand();

		// Unclick all cards in the players hand
		for (CardWrapper card : playerHand.getHand()) {
			card.setHasBeenClicked(false);
		}
		
		//remove any tile highlighting
		TileHighlightController.removeBoardHighlight(out, gameState);

	}

}
