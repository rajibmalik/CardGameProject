package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import controllers.TileHighlightController;
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
		System.out.println("HAND SIZE: " + gameState.getHumanPlayerController().getPlayerHand().getHand().size()); // test
		TileHighlightController.removeBoardHighlight(out, gameState);
		Player currentPlayer = gameState.getCurrentPlayer();

		if (currentPlayer == gameState.getHumanPlayer()) {
			gameState.getHumanPlayerController().clearMana();
			BasicCommands.setPlayer1Mana(out, gameState.getCurrentPlayer());
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.setPlayer1Mana(out, currentPlayer);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		} 
		// else {
		// 	gameState.getAIPlayerController().clearMana();
		// 	BasicCommands.setPlayer2Mana(out, gameState.getCurrentPlayer());
		// 	try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		// 	gameState.getHumanPlayerController().nextTurn();
		// 	BasicCommands.setPlayer2Mana(out, currentPlayer);
		// 	try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		// }

		// Switch to the next player
		switchPlayer(out, gameState, currentPlayer);


		if (currentPlayer == gameState.getHumanPlayer()) {
			gameState.AITakeTurn(out, gameState);
			gameState.getAIPlayerController().nextTurn();
		}

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
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			if (gameState.getAIPlayerController().getTurn()==1) {
				gameState.getAIPlayerController().drawInitialHand();
			}	

			// draws and renders a card for the human
			drawCard(gameState.getHumanPlayerController(), out); 
			
		} else if (nextPlayer == gameState.getHumanPlayer()) {
			gameState.getHumanPlayerController().setTurnMana();
			BasicCommands.setPlayer1Mana(out, nextPlayer);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			// draws a card for the AI in the backend
			gameState.getAIPlayerController().drawCard(); 
		}
	}

	// draws a card in back end for player & renders the card
	private void drawCard(PlayerController playerController, ActorRef out) {

		if (playerController.drawCard()) {
			List<Card> cards = OrderedCardLoader.getPlayer1Cards(2);
			int topCardIndex  = playerController.getPlayerDeck().getTopCardIndex() - 1;
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
