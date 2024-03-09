package abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Utility class used for summoning wraithling when abilities are applied. 
 * @author Darby christy
 */

public class SummonWraithling {
	
	public static void createWraithling(ActorRef out,Player player, TileWrapper targetTile) {
		Unit unit = renderWraithlingFrontEnd(out, player, targetTile);
		UnitWrapper unitWrapper = new UnitWrapper(unit, "Wraithling", 1, 1, player, null, targetTile);
		targetTile.setUnitWrapper(unitWrapper);
		targetTile.setHasUnit(true);
		unitWrapper.setTile(targetTile);
		unitWrapper.setHasAttacked(true);
		unitWrapper.setHasMoved(true);
		player.addUnit(unitWrapper);		
	}
	
	public static Unit renderWraithlingFrontEnd(ActorRef out, Player player, TileWrapper targetTile) {
		Tile tile = targetTile.getTile();
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.wraithling, UnitWrapper.nextId, Unit.class);
		unit.setPositionByTile(tile); 
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, 1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, 1);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
		
	}


}
