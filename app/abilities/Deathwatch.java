package abilities;

import java.util.ArrayList;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;
import utils.TileLocator;

/**
 * This class is responsible for the Deathwatch unit ability, which is triggered any time a unit (friendly or enemy) dies. 
 * This ability is applied to four units: Bad Omen, Shadow Watcher, Bloodmoon Priestess, and Shadowdancer.
 * @author Darby Christy 
 */

public class Deathwatch implements UnitAbility {
	private int attackBonus;
	private int healthBonus;

	public Deathwatch(int attackBonus, int healthBonus) {
		this.attackBonus = attackBonus;
		this.healthBonus = healthBonus;
	}
	
	public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {

		if (unit.getName().equals("Bad Omen")) {
			applyBadOmenAbility(out, unit);
		} else if (unit.getName().equals("Shadow Watcher")) {
			applyShadowWatcherAbility(out, unit);
		} else if (unit.getName().equals("Bloodmoon Priestess")) {
			applyBloodmoonPriestessAbility(out, gameState, unit);
		}else if (unit.getName().equals("Shadowdancer")) {
			applyShadowdancerAbility(out, gameState, unit);
		}
	}
	
	/**
	 * Adds +1 attack permanently to the Bad Omen unit. 
	 * @param out
	 * @param unit
	 */
	public void applyBadOmenAbility(ActorRef out, UnitWrapper unit) {
		UnitController.increaseAttack(out, unit, attackBonus);
	}
	
	/**
	 * Adds +1 attack and +1 health permanently to the Shadow Watcher unit. 
	 * @param out
	 * @param unit
	 */
	public void applyShadowWatcherAbility(ActorRef out, UnitWrapper unit) {
		UnitController.increaseAttack(out, unit, attackBonus);
		UnitController.increaseHealth(out, unit, healthBonus);
	}
	
	/**
	 * Summons a Wraithling on a randomly selected unoccupied adjacent tile to Bloodmoon Priestess. 
	 * If there are no unoccupied tiles, then this ability has no effect.
	 * @param out
	 * @param unit
	 */
	public void applyBloodmoonPriestessAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		Player currentPlayer = gameState.getHumanPlayer();
		TileWrapper unitPosition = unit.getTile();
		// Check if the Bloodmoon Priestess has a valid position
		if (unitPosition != null) {
			ArrayList<TileWrapper> validTiles = TileLocator.getAdjacentTilesWithoutUnit(gameState, unitPosition.getUnit());
	
			// Check if there are valid adjacent tiles available
			if (!validTiles.isEmpty()) {
				TileWrapper tileWrapper = validTiles.get(0);
				SummonWraithling.createWraithling(out, currentPlayer, tileWrapper);
			} else {
				// Handle the case where no valid adjacent tiles are available
				System.out.println("No valid adjacent tiles available for summoning.");
			}
		} else {
			// Handle the case where Bloodmoon Priestess does not have a valid position
			System.out.println("Bloodmoon Priestess does not have a valid position.");
		}
	}
	
	/**
	 * Deals 1 damage to the enemy avatar and heals Shadow Dancer unit by 1.
	 * @param out
	 * @param unit
	 */
	public void applyShadowdancerAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		Player currentPlayer = gameState.getHumanPlayer();
		if (currentPlayer == gameState.getHumanPlayer()) {
			for (UnitWrapper enemyUnit : gameState.getAIPlayer().getUnits()) {
				if (enemyUnit instanceof Avatar) {
					enemyUnit.decreaseHealth(1);
					BasicCommands.setUnitHealth(out, enemyUnit.getUnit(), enemyUnit.getHealth());
					try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

					// Heal the Shadowdancer if health is less than max health
					if(unit.getHealth()<4){
						unit.increaseHealth(1);
						BasicCommands.setUnitHealth(out, unit.getUnit(), unit.getHealth());
						try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
					}
					
				}
			}
		} 
	}
}