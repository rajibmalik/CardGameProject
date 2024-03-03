package abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Player;
import structures.basic.TileWrapper;

public interface SpellAbility {
    public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile);
}
