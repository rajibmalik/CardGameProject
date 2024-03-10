package structures.basic;

import abilities.UnitAbility;
import akka.actor.ActorRef;
import structures.GameState;
/**
 * Represents a summoned unit on the game board.
 * This class encapsulates the attributes and behavior of a unit, including its health, attack,
 * position on the board, and abilities.
 * Units are associated with a player and have a unique identifier.
 * @author Rajib Malik
*/

public class UnitWrapper {
	private Unit unit; 
	private String name;
	private final int maxHealth;
	private int health;
	private int attack;
	private TileWrapper tile;
	private boolean hasBeenClicked;
	private boolean hasMoved;
	private boolean hasAttacked;
	private Player player; 
	private UnitAbility ability;
	private final int id; 
	public static int nextId = 1; 

	// Default constructor
	public UnitWrapper(Unit unit, String name, int health, int attack, Player player, UnitAbility ability,
			TileWrapper tile) {

		if (health < 0 || attack < 0) {
			throw new IllegalArgumentException(
					"Health and attack values cannot be negative & Unit and Player cannot be null!");
		}
		this.player = player;
		this.unit = unit;
		this.name = name;
		this.health = health;
		this.attack = attack;
		this.hasBeenClicked = false;
		this.hasMoved = false;
		this.hasAttacked = false;
		this.id = nextId++; // id is assigned to value of nextId, then nextId incremented
		this.ability = ability;
		this.tile = tile;
		this.maxHealth = health;
	}

	public int getId() {
		return this.id;
	}

	public static int getNextId() {
		return nextId;
	}

	public TileWrapper getTile() {
		return tile;
	}

	public void setTile(TileWrapper tile) {
		this.tile = tile;
	}

	public void setHealth(int health) {
		if (health < 0) {
			throw new IllegalArgumentException("Health value cannot be negative!");
		}
		this.health = health;
	}

	public int getHealth() {
		return this.health;
	}

	public int getMaxHealth() {
		return this.maxHealth;
	}

	public void setAttack(int attack) {
		if (attack < 0) {
			throw new IllegalArgumentException("Attack value cannot be negative!");
		}
		this.attack = attack;
	}

	public int getAttack() {
		return this.attack;
	}

	public void setHasBeenClicked(Boolean clicked) {
		this.hasBeenClicked = clicked;
	}

	public boolean getHasBeenClicked() {
		return this.hasBeenClicked;
	}

	public void setPlayer(Player player) {
		if (player == null) {
			throw new IllegalArgumentException("Player reference cannot be set to null!");
		}
		this.player = player;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setUnit(Unit unit) {
		if (unit == null) {
			throw new IllegalArgumentException("Unit reference cannot be set to null!");
		}
		this.unit = unit;
	}

	public Unit getUnit() {
		return this.unit;
	}

	public String getName() {
		return this.name;
	}

	public void useAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
		this.ability.applyAbility(out, gameState, unit);
	}

	public UnitAbility getAbility() {
		return this.ability;
	}

	public void setHasMoved(Boolean moved) {
		this.hasMoved = moved;
	}

	public boolean getHasMoved() {
		return this.hasMoved;
	}

	public void setHasAttacked(Boolean attacked) {
		this.hasAttacked = attacked;
	}

	public boolean getHasAttacked() {
		return this.hasAttacked;
	}

	public void decreaseHealth(int damage) {

		int newHealth = this.health - damage;

		if (newHealth > 0) {
			this.health -= damage;
		} else {
			this.health = 0;
		}

	}

	public void increaseHealth(int healthIncrease) {
		this.health += healthIncrease;
	}

	public boolean equals(Object comparedObject) {
		if (this == comparedObject) {
			return true;
		}

		if (!(comparedObject instanceof UnitWrapper)) {
			return false;
		}

		UnitWrapper comparedUnitWrapper = (UnitWrapper) comparedObject;

		// if the instance variables of the objects are the same, so are the objects
		return this.name.equals(comparedUnitWrapper.getName()) && this.id == comparedUnitWrapper.id;
	}

	public String toString() {
		return (this.name + ": attack " + this.attack + ", " + "health " + this.health);
	}

}
