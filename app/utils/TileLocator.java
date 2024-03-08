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

	public static List<TileWrapper> getAdjacentTiles(TileWrapper[][] board, TileWrapper tile) {
	    List<TileWrapper> tilesWithinRange = new ArrayList<>();
	
	    for (int i = -1; i <= 1; i++) {
			if (tile != null) {
				for (int j = -1; j <= 1; j++) {
					int newX = tile.getXpos() + i;
					int newY = tile.getYpos() + j;
	
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

	public static ArrayList<TileWrapper> getAdjacentTilesWithAIEnemyUnit(GameState gameState, List<TileWrapper> validTiles) {
		ArrayList<TileWrapper> tiles = new ArrayList<>();
		ArrayList<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
		TileWrapper[][] board = gameState.getBoard().getBoard();
		ArrayList<TileWrapper> tilesWithAttackableUnit = new ArrayList<>();

		for (TileWrapper tile:validTiles) {
			tiles.addAll(TileLocator.getAdjacentTiles(board, tile));
		}

		for (TileWrapper tile:tiles) {
			if (tile.getHasUnit()) {
				if (!units.contains(tile.getUnit())) {
					tilesWithAttackableUnit.add(tile);
				}
			}
		}

		return tilesWithAttackableUnit;
	}

	
	public static ArrayList<TileWrapper> getAdjacentTilesWithoutUnit(GameState gameState, UnitWrapper unitWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		List<TileWrapper> adjacentTiles = TileLocator.getAdjacentTiles(board, unitWrapper);
		ArrayList<TileWrapper> validTiles = new ArrayList<>();

		for (TileWrapper tileWrapper: adjacentTiles) {
			if (!tileWrapper.getHasUnit()) {
				validTiles.add(tileWrapper);
			}
		}

		return validTiles;
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

	public static boolean isAdjacentToHumanUnit(GameState gameState, UnitWrapper unitWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		ArrayList<UnitWrapper> units = gameState.getHumanPlayer().getUnits();

		List<TileWrapper> adjacentTiles = TileLocator.getAdjacentTiles(board, unitWrapper);

		for (TileWrapper tileWrapper: adjacentTiles) {
			if (tileWrapper.getHasUnit() ) {
				UnitWrapper unit = tileWrapper.getUnit();
				if (units.contains(unit)) {
					return true;
				}
				
			}
		}

		return false;
	}

	public static TileWrapper getClosestTileToHumanAvatar(GameState gameState, List<TileWrapper> validTiles) {
		TileWrapper humanTileWrapper = TileLocator.getHumanAvatarTile(gameState);
		int minDistance = Integer.MAX_VALUE;
		TileWrapper closestTile = null;

		for (TileWrapper tileWrapper : validTiles) {
			int distance = calculateDistance(tileWrapper, humanTileWrapper);
			if (distance < minDistance) {
				minDistance = distance;
				closestTile = tileWrapper;
			}
		}
	
		return closestTile;
	}

	private static int calculateDistance(TileWrapper tile1, TileWrapper tile2) {
		int x1 = tile1.getTile().getXpos();
		int y1 = tile1.getTile().getYpos();
		int x2 = tile2.getTile().getXpos();
		int y2 = tile2.getTile().getYpos();
	
		// Euclidean distance formula: sqrt((x2 - x1)^2 + (y2 - y1)^2)
		return Math.abs(x2 - x1) + Math.abs(y2 - y1);
	}

	public static List<TileWrapper> getTilesForUnitMovement (UnitWrapper unit, TileWrapper[][] board){
		TileWrapper unitPosition = unit.getTile();
		List<TileWrapper> allTiles = TileLocator.getAdjacentTiles(board, unitPosition.getUnit());
		List<TileWrapper> validTiles = new ArrayList<>();

		allTiles.addAll(getCardinalDirectionTiles(unitPosition.getUnit(), board));

		for (TileWrapper tile: allTiles) {
			if (!tile.getHasUnit()) {
				validTiles.add(tile);
			}
		}
		
		return validTiles;
	}

	public static List<TileWrapper> getCardinalDirectionTiles(UnitWrapper unit, TileWrapper[][] board) {
		TileWrapper unitPosition = unit.getTile();
		List<TileWrapper> validTiles = new ArrayList<>();
		int[] directions = { -2, 0, 2 };
		for (int i : directions) {
			for (int j : directions) {
				int newX = unitPosition.getXpos() + i;
				int newY = unitPosition.getYpos() + j;

				// Check if the new coordinates are within the bounds of the board
				if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
					TileWrapper tileWrapper = board[newX][newY];

					// Check if the tile is not already occupied by a unit
					if (!isTileOccupied(board, newX, newY) && (i == 0 || j == 0)) { // Only highlight if the movement is horizontal or vertical
						validTiles.add(tileWrapper);
					}
				}
			}
		}

		return validTiles;
	}

	private static boolean isTileOccupied(TileWrapper[][] board, int x, int y) {
		// Check if there is a unit on the specified tile
		return board[x][y].getUnit() != null;
	}

	public static boolean areAdjacent(Tile tile1, Tile tile2) {
		int row1 = tile1.getTilex();
		int col1 = tile1.getTiley();
		int row2 = tile2.getTilex();
		int col2 = tile2.getTiley();
	
		// Tiles are adjacent if the difference in row and column indices is at most 1
		return Math.abs(row1 - row2) <= 1 && Math.abs(col1 - col2) <= 1;
	}
	
	




	


	
}








