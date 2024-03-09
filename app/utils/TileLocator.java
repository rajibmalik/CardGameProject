package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

/**
 * The TileLoacator class provides utility methods for location of specific
 * TileWrapper objects on the board, this faciliates gameplay mechanics
 * involving tiles and units.
 * 
 * @author Rajib Malik
 * @author Darby Christy
 */

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

	/**
	 * Retrieves the adjacent tiles with units which have the provoke ability
	 * 
	 * @param board
	 * @param unit  reference to the unit, their TileWrappers adjacent tiles are
	 *              checked
	 * @return adjacent tiles with provoke
	 */
	public static List<TileWrapper> getAdjacentTilesWithProvoke(TileWrapper[][] board, UnitWrapper unit) {
		List<TileWrapper> tileWrappers = getAdjacentTiles(board, unit);
		List<TileWrapper> adjacentTiles = new ArrayList<TileWrapper>();

		for (TileWrapper tileWrapper : tileWrappers) {
			if (tileWrapper.getHasUnit()) {
				if (tileWrapper.getUnit().getName().equals("Swamp Entangler")
						|| tileWrapper.getUnit().getName().equals("Silverguard Knight")
								&& tileWrapper.getUnit().getHealth() > 0) {
					adjacentTiles.add(tileWrapper);
				}
			}
		}

		return adjacentTiles;
	}

	/**
	 * Retrieves suitable tiles for spawning which do not have a unit set on it
	 * 
	 * @param gameState
	 * @return a list of TileWrappers which do not have an existing unit
	 */
	public static List<TileWrapper> getValidSpawnTiles(GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		List<UnitWrapper> units = gameState.getAIPlayer().getUnits();

		return units.stream().flatMap(unit -> TileLocator.getAdjacentTiles(board, unit).stream())
				.filter(tileWrapper -> !tileWrapper.getHasUnit()).collect(Collectors.toList());
	}

	/**
	 * Retrieves TileWrappers which contain an enemy unit adjacent to the unit being
	 * checked
	 * 
	 * @param gameState
	 * @return a list of TileWrappers are adjacent to the unit not belonging to the
	 *         AI player
	 */
	public static ArrayList<TileWrapper> getAdjacentTilesWithEnemyUnit(GameState gameState, UnitWrapper unitWrapper) {
		return getAdjacentTiles(gameState.getBoard().getBoard(), unitWrapper).stream()
				.filter(tile -> tile.getHasUnit() && !gameState.getCurrentPlayer().getUnits().contains(tile.getUnit()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Retrieves TileWrappers which contain a unit belonging to the AI player
	 * 
	 * @param gameState
	 * @param validTiles reference to tiles on the board which do not have a unit
	 *                   set
	 * @return a list of TileWrappers which belong to the AI player
	 */
	public static ArrayList<TileWrapper> getAdjacentTilesWithEnemyUnit(GameState gameState,
			List<TileWrapper> validTiles) {
		return validTiles.stream().flatMap(tile -> getAdjacentTiles(gameState.getBoard().getBoard(), tile).stream())
				.filter(tile -> tile.getHasUnit() && !gameState.getCurrentPlayer().getUnits().contains(tile.getUnit()))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * Retrieves TileWrappers which do not contain a unit belonging to the AI player
	 * 
	 * @param gameState
	 * @param unitWraper reference to tiles on the board which do not have a unit
	 *                   set
	 * @return a list of TileWrappers which belong to the AI player
	 */
	public static ArrayList<TileWrapper> getAdjacentTilesWithoutUnit(GameState gameState, UnitWrapper unitWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		List<TileWrapper> adjacentTiles = TileLocator.getAdjacentTiles(board, unitWrapper);

		return adjacentTiles.stream().filter(tile -> !tile.getHasUnit())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	public static TileWrapper getHumanAvatarTile(GameState gameState) {
		return gameState.getHumanPlayer().getUnits().stream().filter(unit -> unit instanceof Avatar).findFirst()
				.map(UnitWrapper::getTile).orElse(null);
	}

	public static TileWrapper getAIAvatarTile(GameState gameState) {
		return gameState.getAIPlayer().getUnits().stream().filter(unit -> unit instanceof Avatar).findFirst()
				.map(UnitWrapper::getTile).orElse(null);
	}

	public static List<TileWrapper> getValidTilesAdjacentToHumanAvatar(GameState gameState) {
		TileWrapper humanAvatar = TileLocator.getHumanAvatarTile(gameState);
		TileWrapper[][] board = gameState.getBoard().getBoard();

		List<TileWrapper> validTiles = TileLocator.getAdjacentTiles(board, humanAvatar.getUnit());

		System.out.println("NUMBER OF VALID TILES: " + validTiles.size()); // test

		return validTiles.stream().filter(tileWrapper -> !tileWrapper.getHasUnit()).collect(Collectors.toList());
	}

	public static boolean isAdjacentToHumanUnit(GameState gameState, UnitWrapper unitWrapper) {
		ArrayList<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
		TileWrapper[][] board = gameState.getBoard().getBoard();

		return TileLocator.getAdjacentTiles(board, unitWrapper).stream()
				.anyMatch(tileWrapper -> tileWrapper.getHasUnit() && units.contains(tileWrapper.getUnit()));
	}

	public static TileWrapper getClosestTileToHumanAvatar(GameState gameState, List<TileWrapper> validTiles) {
		TileWrapper humanTileWrapper = TileLocator.getHumanAvatarTile(gameState);

		return validTiles.stream().min((tile1, tile2) -> Integer.compare(calculateDistance(tile1, humanTileWrapper),
				calculateDistance(tile2, humanTileWrapper))).orElse(null);
	}

	private static int calculateDistance(TileWrapper tile1, TileWrapper tile2) {
		int x1 = tile1.getTile().getXpos();
		int y1 = tile1.getTile().getYpos();
		int x2 = tile2.getTile().getXpos();
		int y2 = tile2.getTile().getYpos();

		// Euclidean distance formula: sqrt((x2 - x1)^2 + (y2 - y1)^2)
		return Math.abs(x2 - x1) + Math.abs(y2 - y1);
	}

	public static List<TileWrapper> getTilesForUnitMovement(UnitWrapper unit, TileWrapper[][] board) {
		TileWrapper unitPosition = unit.getTile();
		List<TileWrapper> allTiles = TileLocator.getAdjacentTiles(board, unitPosition.getUnit());
		List<TileWrapper> validTiles = new ArrayList<>();

		allTiles.addAll(getCardinalDirectionTiles(unitPosition.getUnit(), board));

		for (TileWrapper tile : allTiles) {
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
					if (!isTileOccupied(board, newX, newY) && (i == 0 || j == 0)) { // Only highlight if the movement is
																					// horizontal or vertical
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

	// Method to get the unoccupied tile to the left of a unit
	public static TileWrapper getTileLeftOfUnit(GameState gameState, UnitWrapper unitWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileWrapper tile = unitWrapper.getTile();
		int x = tile.getXpos();
		int y = tile.getYpos();
		int newX = x - 1;
		if (!isTileOccupied(board, newX, y) && newX >= 0) {
			return board[newX][y];
		} else {
			System.out.println("Tile is occupied or out of bounds");
		}

		return null;
	}


	public static ArrayList<TileWrapper> getAdjacentEnemyBelowMaxHealth(GameState gameState, UnitWrapper unit) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		ArrayList<TileWrapper> enemiesBelowMaxHealth = new ArrayList<>();

		for (TileWrapper tile : getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is occupied by a unit
			if (isTileOccupied(board, xpos, ypos)) {
				UnitWrapper enemyUnit = board[xpos][ypos].getUnit();
				if(!gameState.getHumanPlayer().getUnits().contains(enemyUnit) 
						&& enemyUnit.getHealth() < enemyUnit.getMaxHealth()){
					enemiesBelowMaxHealth.add(enemyUnit.getTile());
				}
			}
		}
		

		return enemiesBelowMaxHealth;
	}
	
	/**
	 * Finds the AI Avatar and creates an ArrayList of TileWrapper objects.
	 * One represents the tile to the left, and the other to the right of the Avatar.
	 * Both tiles need to be occupied by a unit that belongs to the AI player.
	 * @param gameState
	 * @return An ArrayList of TileWrapper objects (left tile, right tile) if conditions are met, otherwise an empty list.
	 */
	public static ArrayList<TileWrapper> getAIAvatarLeftRightTiles(GameState gameState) {
	    TileWrapper[][] board = gameState.getBoard().getBoard();
	    TileWrapper aiAvatarTile = getAIAvatarTile(gameState);
	    ArrayList<TileWrapper> aiAvatarLeftRightTiles = new ArrayList<>();

	    if (aiAvatarTile != null) {
	        int x = aiAvatarTile.getXpos();
	        int y = aiAvatarTile.getYpos();

	        // Make sure left and right tiles are within bounds of the board
	        if (x - 1 >= 0 && x + 1 <9) {
	            TileWrapper leftTile = board[x - 1][y];
	            TileWrapper rightTile = board[x + 1][y];

	            // Check if either left or right tile is occupied by an AI unit
	            for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
	                if (unit.getTile().equals(leftTile) || unit.getTile().equals(rightTile)) {
	                    aiAvatarLeftRightTiles.add(unit.getTile());
	                }
	            }
	        }
	    }

	    // Return an ArrayList of TileWrapper objects or an empty list if conditions are met or not
	    return aiAvatarLeftRightTiles;
	}



}
