package controllers;

import java.util.ArrayList;
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

/**
 * This class is responsible for all tile highlighting on the board. 
 * This includes highlighting for unit placement and spell casts,
 * highlighting enemy units, and highlighting tiles for movement.
 * 
 * @author Darby Christy 
 */

public class TileHighlightController {

	/**
	 * Highlights valid locations on the game board where a card can be played.
	 * If the clicked card is a unit card, the responsibility for highlighting is delegated to the 
	 * highlightUnitsForUnitCard method. If the clicked card is a spell card, the responsibility for tile
	 * highlighting is delegated to the handleSpellCard method.
	 * @param out     
	 * @param gameState   
	 * @param clickedCard 
	 */
	public static void setCardTileHighlight(ActorRef out, GameState gameState, CardWrapper clickedCard) {
		TileWrapper[][] board = gameState.getBoard().getBoard();

		if (clickedCard instanceof UnitCard) {
			highlightUnitsForUnitCard(out, gameState, board);
		} else if (clickedCard instanceof SpellCard) {
			handleSpellCard(out, gameState, clickedCard);
		}
	}

	/**
	 * Loops through all of the units belonging to the human player and highlights
	 * tiles around them using the highlightTilesAroundUnit method.
	 * @param out
	 * @param gameState
	 * @param board     
	 */
	private static void highlightUnitsForUnitCard(ActorRef out, GameState gameState, TileWrapper[][] board) {
		List<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
		for (UnitWrapper unit : units) {
			highlightTilesAroundUnit(out, unit, board);
		}
	}
	
	/**
	  * Highlights tiles based on the specific spell card that is clicked.
	  * Calls specific methods for each spell card to handle highlighting rules.
	  * @param out  
	  * @param gameState  
	  * @param clickedCard 
	 */
	private static void handleSpellCard(ActorRef out, GameState gameState, CardWrapper clickedCard) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		String name = clickedCard.getName();
		if ("Dark Terminus".equals(name)) {
			highlightDarkTerminus(out, gameState);
		} else if ("Wraithling Swarm".equals(name)) {
			highlightUnitsForUnitCard(out, gameState, board);
		} else if ("Horn of the Forsaken".equals(name)) {
			highlightHornOfTheForsaken(out, gameState);
		}
	}

	/**
	 * Highlights tiles for the Dark Terminus spell card.
	 * Non-Avatar enemy units on the AI player's side are highlighted red on the board.
	 * @param out      
	 * @param gameState 
	 */
	private static void highlightDarkTerminus(ActorRef out, GameState gameState) {
		for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
			if (!(unit instanceof Avatar) && unit.getTile() != null) {
				Tile tile = unit.getTile().getTile();	
				highlightTileAttacking(out, tile);
			}
		}
	}

	/**
	 * Highlights tiles for the Horn of the Forsaken spell card.
	 * Only the human Avatar is highlighted red on the board.
	 * @param out      
	 * @param gameState 
	 */
	private static void highlightHornOfTheForsaken(ActorRef out, GameState gameState) {
		List<UnitWrapper> units = gameState.getHumanPlayer().getUnits();
		for (UnitWrapper unit : units) {
			if (unit instanceof Avatar && unit.getTile() != null) {
				Tile tile = unit.getTile().getTile();
				highlightTileAttacking(out, tile); //highlight tile red 
				break; // exit loop once avatar is found
			}
		}
	}

	public static void setUnitMovementTileHighlight(ActorRef out, GameState gameState, UnitWrapper unit) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		highlightTilesForUnitMovement(out, gameState, unit, board);
	}

	/**
	 * Locates adjacent tiles around a given unit that are unoccupied and highlights them white. 
	 * Makes use of the getAdjacentTiles method from the TileLocator class.
	 * @param out
	 * @param unit
	 * @param board
	 */
	public static void highlightTilesAroundUnit(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {

		for (TileWrapper tile : TileLocator.getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is not already occupied by a unit
			if (!isTileOccupied(board, xpos, ypos)) {
				highlightTileWhite(out,tile.getTile());
			}
		}

	}

	/**
	 * Locates adjacent tiles around a given unit that are occupied by an enemy unit
	 * and highlights them red. Makes use of the getAdjacentTiles method from the
	 * TileLocator class.
	 * @param out
	 * @param unit
	 * @param board
	 */
	public static void highlightEnemyInRange(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {

		for (TileWrapper tile : TileLocator.getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is not already occupied by a unit
			if (isTileOccupied(board, xpos, ypos) && isEnemyUnit(tile, unit)) {
				highlightTileAttacking(out, tile.getTile());
			}
		}
	}

	public static void highlightProvokeEnemyInRange(ActorRef out, UnitWrapper unit, TileWrapper[][] board) {

		for (TileWrapper tile : TileLocator.getAdjacentTiles(board, unit)) {
			int xpos = tile.getXpos();
			int ypos = tile.getYpos();
			// Check if the tile is not already occupied by a unit
			if (isTileOccupied(board, xpos, ypos) && isEnemyUnit(tile, unit)) {
				if (tile.getUnit().getName().equals("Swamp Entangler") || tile.getUnit().getName().equals("Silverguard Knight")) {
					highlightTileAttacking(out, tile.getTile());
				}
			}
		}
	}
	
	/**
	 * Highlights tiles for unit movement, including those directly adjacent to the
	 * unit and two squares above, below, left, and right of the unit. Highlighted
	 * tiles indicate valid locations for the unit to move.
	 * @param out
	 * @param unit
	 * @param board
	 */

	private static void highlightTilesForUnitMovement(ActorRef out, GameState gameState, UnitWrapper unit, TileWrapper[][] board) {
		TileWrapper unitPosition = unit.getTile();
		List<TileWrapper> validTiles = TileLocator.getTilesForUnitMovement(unitPosition.getUnit(), board);

		for (TileWrapper tileWrapper:validTiles) {
			if (tileWrapper != unitPosition && !tileWrapper.getHasUnit()) {
				highlightTileWhite(out, tileWrapper.getTile());
			}
			
		}

		highlightTilesForUnitMovementAndAttack(out, gameState, validTiles);
	}

	private static void highlightTilesForUnitMovementAndAttack(ActorRef out, GameState gameState, List<TileWrapper> validTiles) {
		ArrayList<TileWrapper> attackableTiles = TileLocator.getAdjacentTilesWithAIEnemyUnit(gameState, validTiles);

		for (TileWrapper tileWrapper:attackableTiles) {
			tileWrapper.getTile().setHighlightStatus(2);
			
			BasicCommands.drawTile(out, tileWrapper.getTile(), 2);
			try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

		}
	}
	
	/**
	 * Helper method to check if a tile contains an enemy unit.
	 * @param tileWrapper
	 * @param unit
	 */
	private static boolean isEnemyUnit(TileWrapper tileWrapper, UnitWrapper unit) {
		return tileWrapper.getHasUnit() && !unit.getPlayer().equals(tileWrapper.getUnit().getPlayer());
	}

	/**
	 * Helper method to check if a tile contains a unit. 
	 * @param board
	 * @param x
	 * @param y
	 */
	private static boolean isTileOccupied(TileWrapper[][] board, int x, int y) {
		// Check if there is a unit on the specified tile
		return board[x][y].getUnit() != null;
	}

	
	/**
	 * Method to reset the board to an un-highlighted state. 
	 * @param out
	 * @param gameState
	 */
	public static void removeBoardHighlight(ActorRef out, GameState gameState) {

		TileWrapper[][] board = gameState.getBoard().getBoard();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board[i][j].getTile();
				highlightTileNormal(out,tile);
			}
		}
	}
	
	/**
	 * Helper method to set the highlight status of a given tile. 
	 * @param tile
	 * @param status
	 */
	public static void setTileHighlightStatus(Tile tile, int status) {
		tile.setHighlightStatus(status);
	}
	
	/**
	 * Helper method to get the highlight status of a given tile. 
	 * @param tile
	 * @param status
	 */
	public static int getTileHighlighted(Tile tile) {
		// Check if the clicked tile is highlighted
		int tileHighlightStatus = tile.getHighlightStatus();
		return tileHighlightStatus;
	}
	
	/**
	 * Method for front end rendering of red tile 
	 * @param out
	 * @param tile
	 */
	public static void highlightTileAttacking(ActorRef out, Tile tile) {
		setTileHighlightStatus(tile, 2);
		BasicCommands.drawTile(out, tile, 2);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for front end rendering of white tile 
	 * @param out
	 * @param tile
	 */
	public static void highlightTileWhite(ActorRef out, Tile tile) {
		setTileHighlightStatus(tile, 1);
		BasicCommands.drawTile(out, tile, 1);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method for front end rendering of normal tile 
	 * @param out
	 * @param tile
	 */
	public static void highlightTileNormal(ActorRef out, Tile tile) {
		setTileHighlightStatus(tile, 0);
		BasicCommands.drawTile(out, tile, 0);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	
	

	

}
