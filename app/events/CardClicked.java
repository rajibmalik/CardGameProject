
package events;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.PlayerController;
import controllers.TileHighlightController;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.UnitWrapper;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a card. The event returns the position in the player's hand the card
 * resides within.
 * 
 * { messageType = “cardClicked” position = <hand index position [1-6]> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Player currentPlayer = gameState.getCurrentPlayer();
		if (currentPlayer == gameState.getHumanPlayer()) {
			TileHighlightController.removeBoardHighlight(out, gameState);
			int handPosition = message.get("position").asInt();
			CardWrapper clickedCard = gameState.getPlayerHand().getCard(handPosition - 1);
			System.out.println(clickedCard.getName());
			
			if (PlayerController.canPlayCard(gameState, clickedCard)) {
				setCardClicked(handPosition, gameState, clickedCard);
				TileHighlightController.setCardTileHighlight(out, gameState, clickedCard);
			}
		}
	}

	public void setCardClicked(int handPosition, GameState gameState, CardWrapper clickedCard) {
	    Hand playerHand = gameState.getPlayerHand();
	    
	    // Check if the clicked card is still in the player's hand
	    if (handPosition >= 1 && handPosition <= playerHand.getHand().size()) {
	        // Unclick all cards except the clicked one
	        for (CardWrapper card : playerHand.getHand()) {
	            if (card != null && card != clickedCard) {
	                card.setHasBeenClicked(false);
	            }
	        }
	        // make sure all units are unclicked
	        gameState.unclickAllUnits(gameState);
	
	        // Update the hasBeenClicked status of the clicked card
	        if (clickedCard != null) {
	            clickedCard.setHasBeenClicked(true);
	        } else {
	            // Handle the case where clickedCard is null
	            System.out.println("Clicked card is null.");
	        }
	    } else {
	        // Handle invalid hand position (out of bounds)
	        System.out.println("Invalid hand position: " + handPosition);
	    }
	}


	
	

}
