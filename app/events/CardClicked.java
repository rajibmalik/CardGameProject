
package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Hand;

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
		int handPosition = message.get("position").asInt();

		setCardClicked(handPosition, gameState);
	}

	public void setCardClicked(int handPosition, GameState gameState) {
		Hand playerHand = gameState.getPlayerHand();
		// Check if the clicked card is still in the player's hand
		if (handPosition >= 1 && handPosition <= playerHand.getHand().size()) {
			CardWrapper clickedCard = gameState.getPlayerHand().getCard(handPosition - 1);

			// Unclick all cards except the clicked one
			for (CardWrapper card : playerHand.getHand()) {
				if (card != clickedCard) {
					card.setHasBeenClicked(false);
				}
			}

			// Update the hasBeenClicked status of the clicked card
			clickedCard.setHasBeenClicked(true);

		} else {
			// Handle invalid hand position (out of bounds)
			System.out.println("Invalid hand position: " + handPosition);
		}
	}

}
