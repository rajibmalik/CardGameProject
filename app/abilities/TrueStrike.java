package abilities;

import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class TrueStrike implements SpellAbility {

    @Override
    public void castSpell(Player player, TileWrapper targetTile) {

        if (targetTile.getHasUnit()) {
            UnitWrapper targetUnit = targetTile.getUnit();

            // Check to make sure the unit is an enemy unit
            if (!isEnemyUnit(player, targetUnit)) {
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
