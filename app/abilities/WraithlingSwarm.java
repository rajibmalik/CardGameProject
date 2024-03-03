package abilities;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import controllers.TileHighlightController;
import structures.GameState;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitWrapper;

public class WraithlingSwarm implements SpellAbility {

	@Override
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {
		// TODO Auto-generated method stub
		Player currentPlayer = gameState.getCurrentPlayer();
		TileWrapper[][] board = gameState.getBoard().getBoard();
		int i = 0;

		SummonWraithling.createWraithling(out, currentPlayer, targetTile);

		while (i < 2) {
			ArrayList<TileWrapper> validTiles = getValidTiles(gameState);

			// Check if there are valid tiles available
			if (!validTiles.isEmpty()) {
				TileWrapper tileWrapper = validTiles.get(0);
				SummonWraithling.createWraithling(out, currentPlayer, tileWrapper);
			} else {
				// Handle the case where no valid tiles are available
				System.out.println("No valid tiles available for summoning.");
			}
			i++;
		}

	}

	public ArrayList<TileWrapper> getValidTiles(GameState gameState) {
		ArrayList<TileWrapper> validTiles = new ArrayList<>();
		TileWrapper[][] board = gameState.getBoard().getBoard();
		List<UnitWrapper> units = gameState.getHumanPlayer().getUnits();

		for (UnitWrapper unit : units) {
			TileWrapper unitPosition = unit.getTile();

			if (unitPosition != null) { // temp solution to fix null pointer error
				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {
						int newX = unitPosition.getXpos() + i;
						int newY = unitPosition.getYpos() + j;

						// Check if the new coordinates are within the bounds of the board
						if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
							TileWrapper tileWrapper = board[newX][newY];

							// Check if the tile is not already occupied by a unit
							if (!isTileOccupied(board, newX, newY)) {
								validTiles.add(tileWrapper);
							}
						}
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

}
