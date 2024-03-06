package utils;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class TileLocator {

	

	public static List<TileWrapper> getAdjacentTiles(TileWrapper[][] board, UnitWrapper unit) {
	    List<TileWrapper> tilesWithinRange = new ArrayList<>();
		if (unit == null) {
			return tilesWithinRange;

		}
	  
	    for (int i = -1; i <= 1; i++) {
			TileWrapper unitPosition = unit.getTile();
			if (unitPosition != null) {
				for (int j = -1; j <= 1; j++) {
					int newX = unitPosition.getXpos() + i;
					int newY = unitPosition.getYpos() + j;
	
					// Check if the new coordinates are within the bounds of the board
					if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
						TileWrapper tileWrapper = board[newX][newY];
						tilesWithinRange.add(tileWrapper);
					}
				}

			}
	    }
	    return tilesWithinRange;
	}

	public static List<TileWrapper> getAdjacentTilesWithProvoke(TileWrapper[][] board, UnitWrapper unit) {
		List<TileWrapper> tileWrappers = getAdjacentTiles(board, unit);
		List<TileWrapper> adjacentTiles = new ArrayList<TileWrapper>();

		for (TileWrapper tileWrapper:tileWrappers) {
			if(tileWrapper.getHasUnit()) {
				if (tileWrapper.getUnit().getName().equals("Swamp Entangler")
					   ||tileWrapper.getUnit().getName().equals("Silverguard Knight") && tileWrapper.getUnit().getHealth() > 0) {
						adjacentTiles.add(tileWrapper);
					}
			}
		}

		return adjacentTiles;
	}

	public static ArrayList<TileWrapper> getAdjacentTilesToPlayerUnits(GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
        ArrayList<UnitWrapper> units = gameState.getAIPlayer().getUnits();
        ArrayList<TileWrapper> adjacentTiles = new ArrayList<>();

        for(UnitWrapper unit:units) {
            adjacentTiles.addAll(TileLocator.getAdjacentTiles(board, unit))  ;
        }

        return adjacentTiles;
	}

	public static ArrayList<TileWrapper> getValidSpawnTiles(GameState gameState) {
		ArrayList<TileWrapper> validTiles = new ArrayList<>();
		ArrayList<TileWrapper> adjacentTiles = getAdjacentTilesToPlayerUnits(gameState);

		for (TileWrapper tileWrapper:adjacentTiles) {
			if (tileWrapper.getHasUnit() == false) {
				validTiles.add(tileWrapper);
			}
		}

		return validTiles;
	}

	public static ArrayList<TileWrapper> getAdjacentTilesWithEnemyUnit(GameState gameState, UnitWrapper unitWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		List<TileWrapper> tiles = getAdjacentTiles(board, unitWrapper);
		ArrayList<UnitWrapper> AIUnits = gameState.getAIPlayer().getUnits();
		ArrayList<TileWrapper> enemyTileWrappersWithUnit = new ArrayList<>();

		for (TileWrapper tile: tiles) {
			if (tile.getHasUnit()) {
				UnitWrapper unit = tile.getUnit();
				if (!AIUnits.contains(unit)) {
					enemyTileWrappersWithUnit.add(tile);
				}
			}
		}

		return enemyTileWrappersWithUnit;
	}

	public static ArrayList<TileWrapper> getAdjacentTilesWithEnemyUnit(GameState gameState) {
		ArrayList<TileWrapper> adjacentTiles = getAdjacentTilesToPlayerUnits(gameState);
		ArrayList<TileWrapper> enemyUnits = new ArrayList<>();

		for (TileWrapper tileWrapper:adjacentTiles) {
			if (tileWrapper.getHasUnit()) {
				enemyUnits.add(tileWrapper);
			}
		}

		return enemyUnits;
	}

	public static TileWrapper getHumanAvatarTile(GameState gameState) {
        ArrayList<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
        for (UnitWrapper unitWrapper : units) {
            if (unitWrapper instanceof Avatar) {
                return unitWrapper.getTile();
            }
        }
        return null; // Return null if the avatar tile is not found
    }

	public static List<TileWrapper> getValidTilesAdjacentToHumanAvatar(GameState gameState) {
		TileWrapper humanAvatar = TileLocator.getHumanAvatarTile(gameState);
		TileWrapper [][] board = gameState.getBoard().getBoard();

		List<TileWrapper> validTiles = TileLocator.getAdjacentTiles(board, humanAvatar.getUnit());
		ArrayList<TileWrapper> tilesWithoutUnits = new ArrayList<>();

		for (TileWrapper tileWrapper : validTiles) {
			if (!tileWrapper.getHasUnit()) {
				tilesWithoutUnits.add(tileWrapper);
			}
		}

		System.out.println("NUMBER OF VALID TILES: " + validTiles.size()); // test

		return tilesWithoutUnits;
	}


	


	
}








