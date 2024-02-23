package abilities;

import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class SundropElixir implements SpellAbility {
	
	public void castSpell(Player player, TileWrapper targetTile) {
		
		if (targetTile.getHasUnit()) {
            UnitWrapper targetUnit = targetTile.getUnit();
            if (!(targetUnit instanceof Avatar)) {
                targetUnit.increaseHealth(4);
            } else {
                // Handle the case when the target unit is an Avatar
                System.out.println("Cannot target the Avatar with Sundrop Elixir spell.");
            }
        } else {
            // Handle the case when there is no unit on the tile
            System.out.println("No unit on the target tile.");
        }
    }
}