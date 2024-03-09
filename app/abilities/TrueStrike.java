package abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;
/**
 * This class is responsible for the TrueStrike spell ability.
 * @author Darby christy
 */


public class TrueStrike implements SpellAbility {

	/**
	 * Deals 2 damage to the targetTile
	 */
    @Override
    public void castSpell(ActorRef out,GameState gameState, TileWrapper targetTile) {
    	Player currentPlayer = gameState.getCurrentPlayer();

        if (targetTile.getHasUnit()) {
            UnitWrapper targetUnit = targetTile.getUnit();

            // Check to make sure the unit is an enemy unit
            if (!isEnemyUnit(currentPlayer, targetUnit)) {
                throw new IllegalArgumentException("You can only target enemy units with TrueStrike!");
            }

            targetUnit.decreaseHealth(2);
        } else {
            // Handle the case when there is no unit on the tile
            throw new IllegalArgumentException("There is no unit on the tile!");
        }
    }

    private boolean isEnemyUnit(Player player, UnitWrapper targetUnit) {
        // Check if the target unit belongs to the opposing player
        return !player.getUnits().contains(targetUnit);
    }
}


