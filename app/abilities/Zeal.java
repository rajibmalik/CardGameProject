package abilities;

import java.util.ArrayList;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.UnitWrapper;

public class Zeal implements UnitAbility{

    @Override
    public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        unit.setAttack(unit.getAttack() + 2);
    }

    public static boolean checkForZeal(GameState gameState) {
		ArrayList<UnitWrapper> units = gameState.getAIPlayerController().getUnits();

		for (UnitWrapper unitWrapper : units) {
			if (unitWrapper.getAbility() instanceof Zeal) {
				return true;
			}
		}
		return false;
	}
    
}
