package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Player;
import utils.OrderedCardLoader;

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

		Player currentPlayer = gameState.getCurrentPlayer();

		if (currentPlayer == gameState.getAIPlayer()) {
			gameState.getAIPlayerController().clearMana();
			BasicCommands.setPlayer2Mana(out, currentPlayer);
			gameState.getAIPlayerController().nextTurn();
			// need logic to add card to hand
			// need logic to set all units to be able to move and attack again 
		} else if (currentPlayer == gameState.getHumanPlayer()) {
			gameState.getHumanPlayerController().clearMana();
			BasicCommands.setPlayer1Mana(out, currentPlayer);
			gameState.getHumanPlayerController().nextTurn();
			// need logic to add card to hand
			// need logic to set all units to be able to move and attack again 
		}
		// Switch to the next player
		switchPlayer(out, gameState, currentPlayer);
	}

	public void setPlayerMana(ActorRef out, Player player) {
		BasicCommands.setPlayer1Mana(out, player);
	}

	public void setPlayer2Mana(ActorRef out, Player player) {
		BasicCommands.setPlayer2Mana(out, player);
	}

	public void switchPlayer(ActorRef out, GameState gameState, Player currentPlayer) {

		gameState.switchPlayer();

		Player nextPlayer = gameState.getCurrentPlayer();

		if (nextPlayer == gameState.getAIPlayer()) {
			gameState.getAIPlayerController().setTurnMana();
			BasicCommands.setPlayer2Mana(out, nextPlayer);		
			if (gameState.getAIPlayerController().getTurn()==1) {
				gameState.getAIPlayerController().drawInitialHand();
			}	

			// draws and renders a card for the human
			drawCard(gameState.getHumanPlayerController(), out); 
			
		} else if (nextPlayer == gameState.getHumanPlayer()) {
			gameState.getHumanPlayerController().setTurnMana();
			BasicCommands.setPlayer1Mana(out, nextPlayer);

			// draws a card for the AI in the backend
			gameState.getAIPlayerController().drawCard(); 
		}
	}

	// draws a card in back end for player & renders the card
	private void drawCard(PlayerController playerController, ActorRef out) {

		if (playerController.drawCard()) {
			List<Card> cards = OrderedCardLoader.getPlayer1Cards(1);
			int topCardIndex  = playerController.getPlayerDeck().getTopCardIndex();
			Card topCard = cards.get(topCardIndex);

			BasicCommands.drawCard(out, topCard, playerController.getPlayerHand().getHand().size(), 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
