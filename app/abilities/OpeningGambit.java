package abilities;

import java.util.List;

import akka.actor.ActorRef;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;
import utils.TileLocator;

public class OpeningGambit implements UnitAbility {
	/**
	 * This class is responsible for the OpeningGambit unit ability, which is triggered any time a unit is summoned 
	 * on the board. This ability is applied to three cards: Gloom Chaser, Nightsorrow Assassin, and Silverguard Squire. 
	 * @author Darby Christy, Matthew Wilson, Rajib Malik
	 */

	@Override
	public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		if (unit.getName().equals("Gloom Chaser")) {
			applyGloomChaserAbility(out, gameState, unit);
		} else if (unit.getName().equals("Nightsorrow Assassin")) {
			applyNightSorrowAssassinAbility(out, gameState, unit);
		} else if (unit.getName().equals("Silverguard Squire")) {
			applySilverguardSquireAbility(out, gameState, unit);
		}
	}
	
	/**
	 * Method to Summon a Wraithling directly behind the target unit (to its left for the human player). 
	 * If the space is occupied, then this method has no effect.
	 * @param out
	 * @param gameState
	 * @param unit
	 */
	public void applyGloomChaserAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		TileWrapper leftTile = TileLocator.getTileLeftOfUnit(gameState, unit);
		if (leftTile != null) {
			SummonWraithling.createWraithling(out, gameState.getHumanPlayer(), leftTile);
		} else {
			System.out.println("Tile is occupied or out of bounds");
		}
	}
	
	/**
	 * Method to destroy an enemy unit in an adjacent square that is below its maximum heath. 
	 * @param out
	 * @param gameState
	 * @param unit
	 */
	public void applyNightSorrowAssassinAbility(ActorRef out, GameState gameState, UnitWrapper unit) {

		List<TileWrapper> validTiles = TileLocator.getAdjacentEnemyBelowMaxHealth(gameState, unit);

		System.out.println("Here are the enemies below max health");
		for(TileWrapper enemy: validTiles) {
			System.out.println(enemy.getUnit().getName());
		}
		if (!validTiles.isEmpty()) {

			for (int i = 0; i < validTiles.size(); i++) {
				UnitWrapper enemy = validTiles.get(i).getUnit();
				if (!(enemy instanceof Avatar)) {
					UnitController.destroyHumanPlayerEnemy(out, gameState, enemy.getTile());
					break; //exit loop once an enemy has been destroyed 
				}
			}
		} else {
			System.out.println("No valid tiles");
		}

	}

	/**
	 * Method to give any adjacent allied unit that is directly left or right of
	 * the owning player’s avatar +1 attack and +1 health permanently.
	 * @param out
	 * @param gameState
	 * @param unit
	 */
	public void applySilverguardSquireAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		List<TileWrapper> aiAvatarLeftRightTiles = TileLocator.getAIAvatarLeftRightTiles(gameState);
	 
		if (!aiAvatarLeftRightTiles.isEmpty()) {
	        System.out.println("AI Avatar found. Left and right tiles exist.");

	        for (TileWrapper tile : aiAvatarLeftRightTiles) {
	            UnitWrapper unitOnTile = tile.getUnit();
	            if (unitOnTile != null) {
	                UnitController.increaseAttack(out, unitOnTile, 1);
	                UnitController.increaseHealth(out, unitOnTile, 1);
	                System.out.println("Unit health/attack was updated");
	            } else {
	                System.out.println("Error: Unit on tile is null.");
	            }
	        }
	    } else {
	        System.out.println("No valid tiles or AI Avatar not found.");
	    }
	}


}
