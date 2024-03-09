package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.TileHighlightController;
import structures.GameState;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case the end-turn button.
 * 
 * { messageType = “endTurnClicked” }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		TileHighlightController.removeBoardHighlight(out, gameState);
		Player currentPlayer = gameState.getCurrentPlayer();

		if (currentPlayer == gameState.getHumanPlayer()) {
			gameState.getHumanPlayerController().clearMana();
			gameState.resetHumanUnitMovementAndAttack();
			BasicCommands.setPlayer1Mana(out, currentPlayer);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		} 
		// Switch to the next player
		gameState.endTurnPlayerSwitch(out, gameState, currentPlayer);
		
		if (currentPlayer == gameState.getHumanPlayer()) {
			gameState.AITakeTurn(out, gameState);
			gameState.getAIPlayerController().nextTurn();
		}

	}



}
