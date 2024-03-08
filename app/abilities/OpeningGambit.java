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

	public void applyGloomChaserAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		TileWrapper leftTile = TileLocator.getTileLeftOfUnit(gameState, unit);

		if (leftTile != null) {
			SummonWraithling.createWraithling(out, gameState.getHumanPlayer(), leftTile);
		} else {
			System.out.println("Tile is occupied or out of bounds");
		}

	}

	public void applyNightSorrowAssassinAbility(ActorRef out, GameState gameState, UnitWrapper unit) {

		List<TileWrapper> validTiles = TileLocator.getAdjacentEnemyBelowMaxHealth(gameState, unit);

		if (!validTiles.isEmpty()) {

			for (int i = 0; i < validTiles.size(); i++) {
				if (!(validTiles.get(i).getUnit() instanceof Avatar)) {
					UnitController.destroyHumanPlayerEnemy(out, gameState, validTiles.get(i).getUnit().getTile());
					break; //exit loop once an enemy has been destroyed 
				}
			}
		} else {
			System.out.println("No valid tiles");
		}

	}

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
