package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import abilities.HornOfTheForsaken;
import abilities.UnitAbility;
import abilities.Zeal;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.TileLocator;

/**
 * The UnitController class handles the logic for units' actions and interactions during the game.
 * This includes rendering units, moving units, attacking units, and unit-specific abilities.
 * 
 * This class requires the following parameters:
 * - out: a reference to the actor for frontend communication
 * - gameState: the current state of the game
 * 
 * @author Rajib Malik
 * @author Darby Christy
 */
public class UnitController {

	public UnitController() {
	}
		public static Unit renderUnit(ActorRef out, UnitCard unitCard, Tile tile) {
		String config = unitCard.getCard().getUnitConfig();

		Unit unit = BasicObjectBuilders.loadUnit(config, UnitWrapper.nextId, Unit.class);
		unit.setPositionByTile(tile);
		
		EffectAnimation summoning= BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon.toString());
		BasicCommands.playEffectAnimation(out, summoning, tile);

		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, unitCard.getAttack());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, unitCard.getHealth());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		// for time between spawning units
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
	}

	 /**
     * Renders the AI player's avatar unit on the specified tile,
     * on the game board at the beginning of the game.
     * 
     * @return The rendered AI player's avatar Unit object.
     */

	public static Unit renderAIAvatar(ActorRef out, GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		Tile tile = board[7][2].getTile();
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
		unit.setPositionByTile(tile); 
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, 20);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
	}

	/**
     * Renders the human player's avatar unit on the specified tile,
     * on the game board at the beginning of the game.
     * 
     * @return The rendered human player's avatar Unit object.
     */
	public static Unit renderPlayerAvatar(ActorRef out, GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		Tile tile = board[1][2].getTile();
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		unit.setPositionByTile(tile); 
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, 20);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
	}

	 /**
     * Creates a UnitWrapper object and adds it to the player's list of units.
     * 
     * @param unit          The backend Unit object to be wrapped.
     * @param unitCard      The UnitCard containing unit information.
     * @param tileWrapper   The TileWrapper representing the unit's position.
     * @param player        The Player object owning the unit.
     */
	public static void createUnitWrapper(Unit unit, UnitCard unitCard, TileWrapper tileWrapper, Player player) {
		String name = unitCard.getName();
		int health = unitCard.getHealth();
		int attack = unitCard.getAttack();
		UnitAbility unitAbility = unitCard.getUnitAbility();

		UnitWrapper unitWrapper = new UnitWrapper(unit, name, health, attack, player, unitAbility, tileWrapper);
		if (unitWrapper.getName().equals("Saberspine Tiger")) {
			unitWrapper.setHasMoved(false);
			unitWrapper.setHasAttacked(false);
		} else {
			unitWrapper.setHasMoved(true);
			unitWrapper.setHasAttacked(true);
		}

		tileWrapper.setUnitWrapper(unitWrapper);
		tileWrapper.setHasUnit(true);
		unitWrapper.setTile(tileWrapper);
		player.addUnit(unitWrapper);
	}
	
	/**
	 * Handles the logic when a unit is clicked, including highlighting tiles for unit movement
	 * and potential enemy targets within range. This method considers various conditions, such as
	 * whether the clicked unit has already moved or attacked, and whether it belongs to the current player.
	 * @param out            
	 * @param gameState      
	 * @param currentPlayer 
	 * @param tileWrapper    
	 */
	public static void handleUnitClick(ActorRef out, GameState gameState, Player currentPlayer, TileWrapper tileWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileHighlightController.removeBoardHighlight(out, gameState);
		UnitWrapper unitWrapper = tileWrapper.getUnit();
		//make sure not other units are clicked
		gameState.unclickAllUnits(gameState);
		// if friendly unit is clicked and it hasn't moved or attacked already,
		// highlight tiles for movement			
		if (unitWrapper != null && unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			
			if (TileLocator.getAdjacentTilesWithProvoke(board, unitWrapper).isEmpty()) {
				System.out.println("Tiles highlighted for movement");
				TileHighlightController.setUnitMovementTileHighlight(out, gameState, unitWrapper);
				TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			} else {
				System.out.println("Tiles highlighted for movement");
				TileHighlightController.highlightProvokeEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			}

			// If the unit has moved but not yet attacked, then highlight enemies in range
		} else if (unitWrapper != null && unitWrapper.getHasMoved() == true && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			if (TileLocator.getAdjacentTilesWithProvoke(board, unitWrapper).isEmpty()) {
				TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			} else {
				TileHighlightController.highlightProvokeEnemyInRange(out, unitWrapper, board);
				tileWrapper.getUnit().setHasBeenClicked(true);
			}
		}
		
		if(gameState.getClickedUnit(gameState)!=null) {
			System.out.println("The clicked unit is " + gameState.getClickedUnit(gameState).getName());
		}
	}
	
	/**
	 * Handles the logic when a tile is clicked to move a unit. If a unit has been clicked,
	 * and the unit has not moved or attacked, this method triggers the movement of the unit
	 * to the clicked tile. The method checks the unit's status before initiating the move.
	 * @param out          
	 * @param gameState   
	 * @param tileWrapper  
	 */
	public static void handleTileClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		if (gameState.hasUnitBeenClicked(gameState)) {
			UnitWrapper unitWrapper = gameState.getClickedUnit(gameState);
			// If the unit has not moved or attacked, then move unit
			if (unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false) {
				System.out.println("The unit" + unitWrapper.getName() + "has moved");
				moveUnit(out,gameState,unitWrapper,tileWrapper);
			}
		}
	}

	/**
     * Handles the logic when a tile is clicked to initiate an attack or movement and attack.
     * 
     * If the clicked tile is within attack range, the unit will attack the enemy unit on that tile.
     * If the clicked tile is not within attack range, but is within movement range, the unit will move to that tile and then attack.
     * 
     * @param tileWrapper   The TileWrapper representing the clicked tile.
     */
	public static void handleTileClickAttack(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		List<TileWrapper> tiles = TileLocator.getAdjacentTiles(gameState.getBoard().getBoard(), gameState.getClickedUnit(gameState));
		UnitWrapper attackingUnitWrapper = gameState.getClickedUnit(gameState);
	
		if (tiles.contains(tileWrapper)) {
			UnitWrapper unitWrapperAttacked = tileWrapper.getUnit();
			// Attack unit
			attackUnit(out, gameState, attackingUnitWrapper, unitWrapperAttacked);
		} else {
			TileWrapper[][] board = gameState.getBoard().getBoard();
			UnitWrapper clickedUnit = gameState.getClickedUnit(gameState);
			TileWrapper tileWrapperToMove = null;

			List<TileWrapper> validTiles = TileLocator.getTilesForUnitMovement(clickedUnit, board);
			List<TileWrapper> tilesAdjacentToAttackedUnit = TileLocator.getAdjacentTiles(board, tileWrapper);

			for (TileWrapper tile:validTiles) {
				if (tilesAdjacentToAttackedUnit.contains(tile)) {
					tileWrapperToMove = tile;
				}
			}
	
			// If a valid tile to move to, move and attack 
			if (tileWrapperToMove != null) {
				moveUnit(out, gameState, attackingUnitWrapper, tileWrapperToMove);
				attackUnit(out, gameState, attackingUnitWrapper, tileWrapper.getUnit());
			} else {
				System.out.println("No valid tile to move to that is also attackable.");
			}
		}
	}

	/**
     * Handles the attack logic between two units, including both backend and frontend actions.
     * 
     * If the attacked unit dies as a result of the attack, it will be removed from the game.
     * 
     * @param attackingUnitWrapper   The attacking unit.
     * @param unitWrapperAttacked    The unit being attacked.
     */
	public static void attackUnit(ActorRef out, GameState gameState, UnitWrapper attackingUnitWrapper,
			UnitWrapper unitWrapperAttacked) {
		Player currentPlayer = gameState.getCurrentPlayer();
		attackUnitBackend(out, gameState, attackingUnitWrapper, unitWrapperAttacked);
		attackUnitFrontEnd(out, gameState, attackingUnitWrapper, unitWrapperAttacked, unitWrapperAttacked);
		updatePlayerHealth(out, gameState);

		// Attacked unit Dies
		if (unitWrapperAttacked.getHealth() <= 0) {
			unitDeathBackend(out, gameState, currentPlayer, unitWrapperAttacked);
			unitDeathFrontEnd(out, currentPlayer, unitWrapperAttacked);
		} else if (unitWrapperAttacked.getHealth() > 0) {
			// If attacked unit does not die, perform counter attack
			counterAttackUnitBackend(out, gameState, unitWrapperAttacked, attackingUnitWrapper);
			attackUnitFrontEnd(out, gameState, unitWrapperAttacked, attackingUnitWrapper, attackingUnitWrapper);
			updatePlayerHealth(out, gameState);
			// Counter attack results in attacking unit death
			if (attackingUnitWrapper.getHealth() <= 0) {
				UnitController.unitDeathBackend(out, gameState, currentPlayer, attackingUnitWrapper);
				UnitController.unitDeathFrontEnd(out, currentPlayer, attackingUnitWrapper);
			}
		}

		unclickAllUnits(gameState);

		// Render idle movement
		BasicCommands.playUnitAnimation(out, attackingUnitWrapper.getUnit(), UnitAnimationType.idle);
		BasicCommands.playUnitAnimation(out, unitWrapperAttacked.getUnit(), UnitAnimationType.idle);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}

	/**
     * Applies the Zeal ability to friendly units if applicable after an attack.
     * 
     * @param attackingUnit    The unit initiating the attack.
     * @param attackedUnit     The unit being attacked.
     */
	private static void applyZeal(ActorRef out, GameState gameState, UnitWrapper attackingUnit, UnitWrapper attackedUnit) {
		ArrayList<UnitWrapper> units = gameState.getAIPlayer().getUnits();

		if (attackedUnit instanceof Avatar && attackedUnit.getName().equals("AI")) {
			if (attackingUnit.getAttack() > 0 && Zeal.checkForZeal(gameState)) {
				for (UnitWrapper unitWrapper : units) {
					if (unitWrapper.getAbility() instanceof Zeal) {
						unitWrapper.useAbility(out, gameState, unitWrapper);

						BasicCommands.drawTile(out, unitWrapper.getTile().getTile(), 2);
						try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}

						BasicCommands.setUnitAttack(out, unitWrapper.getUnit(), unitWrapper.getAttack());
						try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}

						BasicCommands.drawTile(out, unitWrapper.getTile().getTile(), 0);
						try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
		}
	}

	 /**
     * Unclicks all units by setting their "hasBeenClicked" flag to false.
     */
	private static void unclickAllUnits(GameState gameState) {
		for (UnitWrapper unit : gameState.getCurrentPlayer().getUnits()) {
			unit.setHasBeenClicked(false);
		}
	}

	 /**
     * Updates the backend state after an attack between two units.
     * 
     * @param attackingUnitWrapper   The unit initiating the attack.
     * @param unitWrapperAttacked     The unit being attacked.
     */
	public static void attackUnitBackend(ActorRef out, GameState gameState, UnitWrapper attackingUnitWrapper,
			UnitWrapper unitWrapperAttacked) {attackingUnitWrapper.setHasAttacked(true);
   			unitWrapperAttacked.decreaseHealth(attackingUnitWrapper.getAttack());

		// Apply horn of forsaken logic if applicable
		if (unitWrapperAttacked instanceof Avatar && ((Avatar) unitWrapperAttacked).isArtifactActive() == true) {
			((Avatar) unitWrapperAttacked).decreaseRobustness();
		} else if (attackingUnitWrapper instanceof Avatar
				&& ((Avatar) attackingUnitWrapper).isArtifactActive() == true) {
			HornOfTheForsaken.summonWraithling(out, gameState, unitWrapperAttacked.getTile());
		}
	}

	 /**
     * Updates the backend state after a counter-attack from the attacked unit.
     * 
     * @param attackingUnitWrapper    The unit initiating the counter-attack.
     * @param unitWrapperAttacked     The unit that originally initiated the attack.
     */
	public static void counterAttackUnitBackend(ActorRef out, GameState gameState, UnitWrapper attackingUnitWrapper, UnitWrapper unitWrapperAttacked) {
		unitWrapperAttacked.decreaseHealth(attackingUnitWrapper.getAttack());
		
		// Apply horn of forsaken logic if applicable
		if (unitWrapperAttacked instanceof Avatar && ((Avatar) unitWrapperAttacked).isArtifactActive() == true) {
			((Avatar) unitWrapperAttacked).decreaseRobustness();
		} else if (attackingUnitWrapper instanceof Avatar
				&& ((Avatar) attackingUnitWrapper).isArtifactActive() == true) {
			HornOfTheForsaken.summonWraithling(out, gameState, unitWrapperAttacked.getTile());
		}

	}

	/**
     * Executes the frontend animations and updates after an attack between two units.
     * 
     * @param attackingUnit           The unit initiating the attack.
     * @param unitAttacked            The unit being attacked.
     * @param unitWrapperAttacked     The backend representation of the unit being attacked.
     */
	public static void attackUnitFrontEnd(ActorRef out, GameState gameState, UnitWrapper attackingUnit, UnitWrapper unitAttacked,
			UnitWrapper unitWrapperAttacked) {

		TileHighlightController.removeBoardHighlight(out, gameState);
		BasicCommands.playUnitAnimation(out, attackingUnit.getUnit(), UnitAnimationType.attack);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unitAttacked.getUnit(), unitWrapperAttacked.getHealth());
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		// applies zeal if applicable after inital frontend unit updated 

		if (Zeal.checkForZeal(gameState)) {
			applyZeal(out, gameState, attackingUnit, unitWrapperAttacked);
		}

	}

	 /**
     * Handles the backend logic for a unit's death, including updating the game state and player lists.
     * 
     * @param currentPlayer    The player to whom the unit belongs.
     * @param unitDying        The unit that is dying.
     */
	public static void unitDeathBackend(ActorRef out, GameState gameState, Player currentPlayer, UnitWrapper unitDying) {
		unitDealth(gameState, unitDying);
		
		PlayerController.applyDeathWatch(out, gameState);
	}

	 /**
     * Handles the backend logic for a unit's death, including removing it from the game state and player lists.
     * 
     * @param unitWrapper  The unit that is dying.
     */
	public static void unitDealth(GameState gameState, UnitWrapper unitWrapper) {
		if (unitWrapper.getHealth() < 1) {
			unitWrapper.getTile().setHasUnit(false);
			unitWrapper.getTile().setUnitWrapper(null);
			unitWrapper.setTile(null);
			removeUnit(gameState, unitWrapper);
		}
	}
	
 	 /**
     *  removes a unit from a Players list of UnitWrappers
     * 
     * @param unitWrapper  The unit that is being removed
     */
	public static void removeUnit(GameState gameState, UnitWrapper unitWrapper) {
	    // Look for dead unit in human unit list
	    Iterator<UnitWrapper> humanIterator = gameState.getHumanPlayer().getUnits().iterator();
	    while (humanIterator.hasNext()) {
	        UnitWrapper unit = humanIterator.next();
	        if (unit.getId() == unitWrapper.getId()) {
	            humanIterator.remove();
	            System.out.println("Comparing: " + unit.getName() + " (ID: " + unit.getId() + ") with "
	                    + unitWrapper.getName() + " (ID: " + unitWrapper.getId() + ")");
	            System.out.println("Unit removed successfully.");
	            break;
	        }
	    }

	    // Look for dead unit in ai unit list
	    Iterator<UnitWrapper> aiIterator = gameState.getAIPlayer().getUnits().iterator();
	    while (aiIterator.hasNext()) {
	        UnitWrapper unit = aiIterator.next();
	        if (unit.equals(unitWrapper)) {
	            aiIterator.remove();
	            System.out.println("Comparing: " + unit.getName() + " (ID: " + unit.getId() + ") with "
	                    + unitWrapper.getName() + " (ID: " + unitWrapper.getId() + ")");
	            System.out.println("Unit removed successfully.");
	            break;
	        }
	    }

	    // Debugging statements
	    System.out.println("Here are the human players units:");
	    for (UnitWrapper unit : gameState.getHumanPlayer().getUnits()) {
	        System.out.println(unit.getName());
	    }
	    System.out.println("Here are the ai players units:");
	    for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
	        System.out.println(unit.getName());
	    }
	}

	/**
     * Handles the frontend visual effects and removal of a unit upon its death.
     * 
     * @param currentPlayer The player whose unit is dying.
     * @param unitDying     The unit that is dying.
     */
	public static void unitDeathFrontEnd(ActorRef out, Player currentPlayer, UnitWrapper unitDying) {
		BasicCommands.playUnitAnimation(out, unitDying.getUnit(), UnitAnimationType.death);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.deleteUnit(out, unitDying.getUnit());
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
	}

	 /**
     * Updates the health of players based on their avatars' health, and checks for
     * victory or defeat conditions.
     */
	private static void updatePlayerHealth(ActorRef out, GameState gameState) {
		// Find human avatar and update player health to match
		for (UnitWrapper unit : gameState.getHumanPlayer().getUnits()) {
			if (unit instanceof Avatar) {
				gameState.getHumanPlayer().setHealth(unit.getHealth());
				BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (gameState.getHumanPlayer().getHealth()<1) {
					gameState.announceResult(out, gameState, gameState.getHumanPlayer());
					gameState.credits(out, gameState, gameState.getHumanPlayer());
				}
			}
		}
		// Find AI avatar and update player health to match
		for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
			if (unit instanceof Avatar) {
				gameState.getAIPlayer().setHealth(unit.getHealth());
				BasicCommands.setPlayer2Health(out, gameState.getAIPlayer());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (gameState.getAIPlayer().getHealth()<1) {
					gameState.announceResult(out, gameState, gameState.getAIPlayer());
					gameState.credits(out, gameState, gameState.getAIPlayer());
				}
			}
		}
	}
	
	 /**
     * Destroys a unit belonging to the AI player.
     * 
     * @param targetTile  The TileWrapper containing the unit to be destroyed.
     */
	public static void destroyHumanPlayerEnemy(ActorRef out, GameState gameState, TileWrapper targetTile) {
		Player aiPlayer = gameState.getAIPlayer();
		UnitWrapper unitDying = targetTile.getUnit();

		targetTile.getUnit().setHealth(0);
		UnitController.unitDeathBackend(out, gameState, aiPlayer,  unitDying);
		UnitController.unitDeathFrontEnd( out,  aiPlayer,  unitDying);
	}

	 /**
     * Moves a unit to a new tile.
     * 
     * @param unitWrapper  The UnitWrapper representing the unit to be moved.
     * @param tileWrapper  The target TileWrapper representing the destination tile.
     */
	public static void moveUnit(ActorRef out, GameState gameState, UnitWrapper unitWrapper, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		moveUnitBackend(unitWrapper, tileWrapper);
		moveUnitFrontend(out, unitWrapper, tile);
		TileHighlightController.removeBoardHighlight(out, gameState);
		gameState.unclickAllUnits(gameState);
	}

	/**
     * Moves a unit to a new tile on the backend.
     * 
     * @param unitWrapper  The UnitWrapper representing the unit to be moved.
     * @param targetTile   The TileWrapper representing the target tile to move the unit to.
     */
	public static void moveUnitBackend(UnitWrapper unitWrapper, TileWrapper targetTile) {
		TileWrapper oldTile = unitWrapper.getTile();

		// Remove unit from old tile
		oldTile.setHasUnit(false);
		oldTile.setUnitWrapper(null);

		// Add unit to new tile
		targetTile.setHasUnit(true);
		targetTile.setUnitWrapper(unitWrapper);
		unitWrapper.setTile(targetTile);
		unitWrapper.setHasMoved(true);

	}

	/**
	 * Moves a game unit to a specified tile on the game board and plays the unit's movement animation.
	 * This method is intended to be used as part of the game's frontend logic to visually represent unit movement.
	 * 
	 * @param unitWrapper An object that encapsulates the game unit to be moved. The actual {@code Unit} object is retrieved from this wrapper.
	 * @param tile An object that represents the destination tile on the game board to which the unit will be moved.
	*/
	public static void moveUnitFrontend(ActorRef out, UnitWrapper unitWrapper, Tile tile) {
		Unit unit = unitWrapper.getUnit();
		BasicCommands.moveUnitToTile(out, unit, tile);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
	}

	/**
	 * Increases the attack value of a game unit by a specified amount and updates the frontend.
	 * 
	 * @param unit unit to increase the attack of
	 * @param i The amount to increase the unit's attack by.
	 */
	public static void increaseAttack(ActorRef out, UnitWrapper unit, int i) {
		if(unit!=null) {
			unit.setAttack(unit.getAttack() + i);
			increaseAttackFrontend(out,unit);
		}
	}
	
	/**
	 * Updates the frontend to reflect the current attack value of a game unit.
	 * 
	 * @param unit The UnitWRapper containing the game unit whose attack value has been updated.
	 */
	public static void increaseAttackFrontend(ActorRef out, UnitWrapper unit) {
		int attack = unit.getAttack();
		BasicCommands.setUnitAttack(out, unit.getUnit(), attack);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}

	/**
	 * Increases the health value of a specified game unit by a given amount and updates the frontend to reflect the change.
	 * 
	 * @param unit A UniWrapper containing the game unit whose health is to be increased.
	 * @param i The amount to adjust the unit's health by
	 */
	public static void increaseHealth(ActorRef out, UnitWrapper unit, int i) {
		if(unit!=null) {
			unit.setHealth(unit.getHealth() + i);
			increaseHealthFrontend(out,unit);
		}
	}
	
	
	/**
	 * Updates the frontend to display the current health value of a specified game unit.
	 * 
	 * @param unit The UnitWrapper containing the game unit whose health has been updated.
	 */
	public static void increaseHealthFrontend(ActorRef out, UnitWrapper unit) {
		int health = unit.getHealth();
		BasicCommands.setUnitHealth(out, unit.getUnit(), health);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
	}
}
