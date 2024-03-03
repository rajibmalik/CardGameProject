package abilities;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.UnitWrapper;

public interface UnitAbility {
    public void applyAbility(ActorRef out, GameState gameState,UnitWrapper unit);
}
