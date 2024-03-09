package abilities;

import akka.actor.ActorRef;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Player;
import structures.basic.TileWrapper;
/**
 * This class is responsible for the DarkTerminus spell ability. 
 * @author Darby Christy
 * @author Rajib Malik
 */
public class DarkTerminus implements SpellAbility {
	
	/**
	 * Given a target tile, this method will destroy the enemy unit on the tile and replace it with a wraithling 
	 * that belongs to the human player.
	 * @param out     
	 * @param gameState   
	 * @param targetTile 
	 */
	@Override
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {
		// TODO Auto-generated method stub
		
		Player humanPlayer = gameState.getHumanPlayer();
		UnitController.destroyHumanPlayerEnemy(out, gameState, targetTile);
		SummonWraithling.createWraithling(out, humanPlayer, targetTile);
	}

	
	
}
