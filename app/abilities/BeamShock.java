package abilities;

import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class BeamShock implements SpellAbility {

	@Override
	public void castSpell(Player player, TileWrapper targetTile) {

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
