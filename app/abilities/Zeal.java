package abilities;

import java.util.ArrayList;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.UnitWrapper;

/**
 * This class implements UnitAbility. When applied, it increases the unit's attack by 2.
 * It uses the following parameters: 
 * - out: reference to the actor for frontend communication 
 * - gameState: current state o the game
 * 
 * @author Rajib Malik
*/

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
