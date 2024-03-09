package abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;
/**
 * This class is responsible for the BeamShock spell ability.
 * @author Darby christy
 */

public class BeamShock implements SpellAbility {

	/**
	 * When this method is called the targetTile will temporarily stunned, preventing the unit from moving or attacking
	 * on the players next turn. The ability will not target avatar units. 
	 */
	@Override
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {

		if (targetTile.getHasUnit()) {
			UnitWrapper targetUnit = targetTile.getUnit();

			// Check if the target unit is not an instance of Avatar
			if (!(targetUnit instanceof Avatar)) {

				// Make it look like the unit has already moved and attacked, thus preventing it
				// from doing either on the players next turn.
				targetUnit.setHasMoved(true);
				targetUnit.setHasAttacked(true);

			} else {
				// Handle the case when the target unit is an Avatar
				System.out.println("Cannot target the Avatar with BeamShock spell.");
			}
		} else {
			// Handle the case when there is no unit on the tile
			throw new IllegalArgumentException("There is no unit on the tile!");
		}

	}

}