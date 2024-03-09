package abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.TileWrapper;

/**
 * This is an interface which all of the spell ability classes extend.
 * It defines a method for the application of the ability to a tile wrapper in the game.
 * 
 * @author Rajib Malik
*/
public interface SpellAbility {
     /**
     * Executes the spell ability.
     * 
     * @param out reference to the actor for frontend communication
     * @param gameState current state of the game
     * @param targetTile the tileWrapper unto which the ability is applied
     */
    public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile);
}
