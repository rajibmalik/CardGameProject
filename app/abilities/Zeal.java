package abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.UnitWrapper;

public class Zeal implements UnitAbility{

    @Override
    public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        unit.setAttack(unit.getAttack() + 2);
    }
    
}
