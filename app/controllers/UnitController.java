package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import abilities.Deathwatch;
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

public class UnitController {

	public UnitController() {
	}

	// creates a Unit object from the config
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

	// creates a backend UnitWrapper object
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

	// logic for carrying out an attack on the front and backend, uses various helper classes
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

	// checks and applies zeal if applicable
	private static void applyZeal(ActorRef out, GameState gameState, UnitWrapper attackingUnit, UnitWrapper attackedUnit) {
		ArrayList<UnitWrapper> units = gameState.getAIPlayer().getUnits();

		if (attackedUnit instanceof Avatar && attackedUnit.getName().equals("AI")) {
			if (attackingUnit.getAttack() > 0 && checkForZeal(gameState)) {
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

	// helper method to check if zeal can be applied
	private static boolean checkForZeal(GameState gameState) {
		ArrayList<UnitWrapper> units = gameState.getAIPlayerController().getUnits();

		for (UnitWrapper unitWrapper : units) {
			if (unitWrapper.getAbility() instanceof Zeal) {
				return true;
			}
		}
		return false;
	}

	
	private static void unclickAllUnits(GameState gameState) {
		for (UnitWrapper unit : gameState.getCurrentPlayer().getUnits()) {
			unit.setHasBeenClicked(false);
		}
	}

	// logic for carrying out an attack on the backend
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

	// same as normal attack, expect "has attacked" won't be set to true so player can attack on next turn
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

	// logic for carrying out an attack on the frontend
	public static void attackUnitFrontEnd(ActorRef out, GameState gameState, UnitWrapper attackingUnit, UnitWrapper unitAttacked,
			UnitWrapper unitWrapperAttacked) {

		TileHighlightController.removeBoardHighlight(out, gameState);
		BasicCommands.playUnitAnimation(out, attackingUnit.getUnit(), UnitAnimationType.attack);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unitAttacked.getUnit(), unitWrapperAttacked.getHealth());
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		// applies zeal if applicable after inital frontend unit updated 

		if (checkForZeal(gameState)) {
			applyZeal(out, gameState, attackingUnit, unitWrapperAttacked);
		}

	}

	public static void unitDeathBackend(ActorRef out, GameState gameState, Player currentPlayer, UnitWrapper unitDying) {
		unitDealth(gameState, unitDying);

		// Create a copy of the units list to avoid ConcurrentModificationException
		List<UnitWrapper> unitsCopy = new ArrayList<>(gameState.getHumanPlayer().getUnits());

		for (UnitWrapper unit : unitsCopy) {
			if (unit.getAbility() instanceof Deathwatch) {
				unit.useAbility(out, gameState, unit);
			}
		}

	}

	public static void unitDealth(GameState gameState, UnitWrapper unitWrapper) {
		if (unitWrapper.getHealth() < 1) {
			unitWrapper.getTile().setHasUnit(false);
			unitWrapper.getTile().setUnitWrapper(null);
			unitWrapper.setTile(null);
			removeUnit(gameState, unitWrapper);
		}
	}

	// removes a unit from a Players list of UnitWrappers
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


	public static void unitDeathFrontEnd(ActorRef out, Player currentPlayer, UnitWrapper unitDying) {
		BasicCommands.playUnitAnimation(out, unitDying.getUnit(), UnitAnimationType.death);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.deleteUnit(out, unitDying.getUnit());
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
	}

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
			}
		}
		// Find ai avatar and update player health to match
		for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
			if (unit instanceof Avatar) {
				gameState.getAIPlayer().setHealth(unit.getHealth());
				BasicCommands.setPlayer2Health(out, gameState.getAIPlayer());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void moveUnit(ActorRef out, GameState gameState, UnitWrapper unitWrapper, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		moveUnitBackend(unitWrapper, tileWrapper);
		moveUnitFrontend(out, unitWrapper, tile);
		TileHighlightController.removeBoardHighlight(out, gameState);
		gameState.unclickAllUnits(gameState);
	}

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

	public static void moveUnitFrontend(ActorRef out, UnitWrapper unitWrapper, Tile tile) {
		Unit unit = unitWrapper.getUnit();
		BasicCommands.moveUnitToTile(out, unit, tile);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);
	}
}
