package abilities;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.UnitController;
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
		
		Player humanPlayer = gameState.getHumanPlayer();

		destroyEnemy(out, gameState, targetTile);
		SummonWraithling.createWraithling(out, humanPlayer, targetTile);

	}

	public void destroyEnemy(ActorRef out, GameState gameState, TileWrapper targetTile) {
		Player aiPlayer = gameState.getAIPlayer();
		UnitWrapper unitDying = targetTile.getUnit();
		Unit unit = unitDying.getUnit();
		
		UnitController.unitDeathBackend(out, gameState, aiPlayer,  unitDying);
		UnitController.unitDeathFrontEnd( out,  aiPlayer,  unitDying);
	}
	


}

