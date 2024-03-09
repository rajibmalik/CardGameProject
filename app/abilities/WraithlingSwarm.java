package abilities;

import akka.actor.ActorRef;
import controllers.PlayerController;
import controllers.TileHighlightController;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.TileWrapper;

/**
 * This class is responsible for the Wraithling Swarm spell ability.
 * @author Darby christy
 */

public class WraithlingSwarm implements SpellAbility {
	private int wraithlingSwarmClicks = 0;

	/**
	 * Method to summon three wraithlings with a series of clicks. When the first wraithling is placed, tiles will be
	 * re-highlighted to indicate that another unit can be summoned. This will repeat until the third wriathling is placed,
	 * which will then remove the card from the players hand and drain mana. 
	 * @param out
	 * @param gameState
	 * @param targetTile
	 */
	@Override
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {

		CardWrapper clickedCard = gameState.getClickedCard(gameState);
		if (wraithlingSwarmClicks < 2) {
			SummonWraithling.createWraithling(out, gameState.getHumanPlayer(), targetTile);
			TileHighlightController.removeBoardHighlight(out, gameState);
			TileHighlightController.setCardTileHighlight(out, gameState, clickedCard);
			wraithlingSwarmClicks++;
		} else if (wraithlingSwarmClicks == 2) {
			wraithlingSwarmClicks = 0;
			clickedCard.setHasBeenClicked(false);
			SummonWraithling.createWraithling(out, gameState.getHumanPlayer(), targetTile);
			TileHighlightController.removeBoardHighlight(out, gameState);
			PlayerController.applyOpeningGambit(out, gameState);
			PlayerController.deductAndRenderMana(gameState, out, clickedCard);
			PlayerController.removeCard(out, gameState, clickedCard);
		}
	}

}
