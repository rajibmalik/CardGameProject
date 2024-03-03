package controllers;

import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.CardWrapper;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.TileLocator;

public class TileHighlightController {

	public static void setCardTileHighlight(ActorRef out, GameState gameState, CardWrapper clickedCard) {
		TileWrapper[][] board = gameState.getBoard().getBoard();

		if (clickedCard instanceof UnitCard) {
			List<UnitWrapper> units = gameState.getCurrentPlayer().getUnits();

			for (UnitWrapper unit : units) {
				highlightTilesAroundUnit(out, unit, board);
			}
		} else if (clickedCard instanceof SpellCard) {

			String name = clickedCard.getName();
			if (name.equals("Dark Terminus")) {
			    for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
			        if (!(unit instanceof Avatar)) {
			            System.out.println("Unit: " + unit);
			            System.out.println("Unit Tile: " + unit.getTile());
			            if (unit.getTile() != null) {
			                Tile tile = unit.getTile().getTile();
			                setTileHighlightStatus(tile, 2);
			                BasicCommands.drawTile(out, tile, 2);
			                try {
			                    Thread.sleep(10);
			                } catch (InterruptedException e) {
			                    e.printStackTrace();
			                }
			            }
			        }
			    }
			} else if (name.equals("Wraithling Swarm")) {
				List<UnitWrapper> units = gameState.getCurrentPlayer().getUnits();

				for (UnitWrapper unit : units) {
					highlightTilesAroundUnit(out, unit, board);
				}
				
			}else if (name.equals("Horn of the Forsaken")) {
				List<UnitWrapper> units = gameState.getCurrentPlayer().getUnits();

				for (UnitWrapper unit : units) {
					
					if (unit instanceof Avatar) {
						Tile tile = unit.getTile().getTile();
						setTileHighlightStatus(tile, 2);
						BasicCommands.drawTile(out, tile, 2);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}
			}

		}

	}

	public static void setUnitMovementTileHighlight(ActorRef out, GameState gameState, UnitWrapper unit) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		highlightTilesForUnitMovement(out, unit, board);
	}

	public static void highlightTilesAroundUnit(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {

		for (TileWrapper tile : TileLocator.getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is not already occupied by a unit
			if (!isTileOccupied(board, xpos, ypos)) {
				setTileHighlightStatus(tile.getTile(), 1);
				BasicCommands.drawTile(out, tile.getTile(), 1);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void highlightEnemyInRange(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {

		for (TileWrapper tile : TileLocator.getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is not already occupied by a unit
			if (isTileOccupied(board, xpos, ypos) && isEnemyUnit(tile, unit)) {
				setTileHighlightStatus(tile.getTile(), 2);
				BasicCommands.drawTile(out, tile.getTile(), 2);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void highlightTilesForUnitMovement(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {
		TileWrapper unitPosition = unit.getTile();

		highlightTilesAroundUnit(out, unit, board);

		// Highlight tiles 2 squares above and below, and 2 squares to the left and
		// right
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
						setTileHighlightStatus(tileWrapper.getTile(), 1);
						BasicCommands.drawTile(out, tileWrapper.getTile(), 1);
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	

	// Helper method to check if a tile contains an enemy unit
	private static boolean isEnemyUnit(TileWrapper tileWrapper, UnitWrapper unit) {
		return tileWrapper.getHasUnit() && !unit.getPlayer().equals(tileWrapper.getUnit().getPlayer());
	}

	private static boolean isTileOccupied(TileWrapper[][] board, int x, int y) {
		// Check if there is a unit on the specified tile
		return board[x][y].getUnit() != null;
	}

	public static void removeBoardHighlight(ActorRef out, GameState gameState) {

		TileWrapper[][] board = gameState.getBoard().getBoard();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board[i][j].getTile();
				setTileHighlightStatus(tile, 0);
				BasicCommands.drawTile(out, tile, 0);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void setTileHighlightStatus(Tile tile, int status) {
		tile.setHighlightStatus(status);
	}

}
