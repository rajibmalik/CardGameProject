package abilities;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class Deathwatch implements UnitAbility {
	private int attackBonus;
	private int healthBonus;

	public Deathwatch(int attackBonus, int healthBonus) {
		this.attackBonus = attackBonus;
		this.healthBonus = healthBonus;
	}

	public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		Player currentPlayer = gameState.getHumanPlayer();

		if (unit.getName().equals("Bad Omen")) {
			int attack = unit.getAttack() + this.attackBonus;
			unit.setAttack(attack);
			BasicCommands.setUnitAttack(out, unit.getUnit(), attack);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			
		} else if (unit.getName().equals("Shadow Watcher")) {
			int attack = unit.getAttack() + this.attackBonus;
			int health = unit.getHealth() + this.healthBonus;
			unit.setAttack(attack);
			unit.setHealth(unit.getHealth() + this.healthBonus);
			BasicCommands.setUnitHealth(out, unit.getUnit(), health);
			BasicCommands.setUnitAttack(out, unit.getUnit(), attack);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			
		} else if (unit.getName().equals("Bloodmoon Priestess")) {
			TileWrapper unitPosition = unit.getTile();

			// Check if the Bloodmoon Priestess has a valid position
			if (unitPosition != null) {
				ArrayList<TileWrapper> validTiles = getValidAdjacentTiles(gameState, unitPosition);

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
		} else if (unit.getName().equals("Shadowdancer")) {
	
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
			} else if (currentPlayer == gameState.getAIPlayer()) {
				// Similar logic for AI player
			}

		}

	}

	private ArrayList<TileWrapper> getValidAdjacentTiles(GameState gameState, TileWrapper unitPosition) {
		ArrayList<TileWrapper> validAdjacentTiles = new ArrayList<>();
		TileWrapper[][] board = gameState.getBoard().getBoard();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				int newX = unitPosition.getXpos() + i;
				int newY = unitPosition.getYpos() + j;

				// Check if the new coordinates are within the bounds of the board
				if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
					TileWrapper tileWrapper = board[newX][newY];

					// Check if the tile is not already occupied by a unit
					if (!isTileOccupied(board, newX, newY)) {
						validAdjacentTiles.add(tileWrapper);
					}
				}
			}
		}

		return validAdjacentTiles;
	}

	private static boolean isTileOccupied(TileWrapper[][] board, int x, int y) {
		// Check if there is a unit on the specified tile
		return board[x][y].getUnit() != null;
	}

}
