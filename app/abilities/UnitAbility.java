package abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.UnitWrapper;

/**
 * This is an interface which all of the unit ability classes extend.
 * It defines a method for the application of the ability to a unit in the game.
 * @author Rajib Malik
*/
public interface UnitAbility {
    /**
     * Executes the unit ability.
     * @param out reference to the actor for frontend communication
     * @param gameState current state of the game
     * @param unit the unit to which the ability is applied
    */
    public void applyAbility(ActorRef out, GameState gameState,UnitWrapper unit);
}
