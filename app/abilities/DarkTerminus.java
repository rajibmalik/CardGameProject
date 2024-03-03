

package abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.UnitWrapper;

public class DarkTerminus implements SpellAbility {

	@Override
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {
		// TODO Auto-generated method stub
		
		Player currentPlayer = gameState.getCurrentPlayer();

		destroyEnemy(out, currentPlayer, targetTile);
		SummonWraithling.createWraithling(out, currentPlayer, targetTile);

	}

	public void destroyEnemy(ActorRef out, Player player, TileWrapper targetTile) {
		UnitWrapper unitWrapper = targetTile.getUnit();
		Unit unit = unitWrapper.getUnit();
		unitWrapper.getTile().setHasUnit(false);
		unitWrapper.getTile().setUnitWrapper(null);
		player.getUnits().remove(unitWrapper);

		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
		BasicCommands.deleteUnit(out, unit);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
