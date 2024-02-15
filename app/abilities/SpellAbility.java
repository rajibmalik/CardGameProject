package abilities;
import structures.basic.Player;
import structures.basic.TileWrapper;

public interface SpellAbility {
    public void castSpell(Player player, TileWrapper targetTile);
}
