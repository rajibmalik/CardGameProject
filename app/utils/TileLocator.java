package utils;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
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
	


	
}


