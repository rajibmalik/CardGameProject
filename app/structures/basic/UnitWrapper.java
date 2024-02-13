package structures.basic;
import abilities.UnitAbility;

public class UnitWrapper {
    private Unit unit; // Unit reference to backend representation of a Unit
    private String name; 
    private int health;
    private int attack;
    private boolean hasBeenClicked;
    private boolean hasMoved;
    private Player player; // Player reference to the owner of the unit
    private UnitAbility ability;

    // Default constructor, most rigid requiring all parameters 
    public UnitWrapper(Unit unit, String name, int health, int attack, Player player) {

        if (health < 0 || attack < 0) {
            throw new IllegalArgumentException("Health and attack values cannot be negative & Unit and Player cannot be null!");
        }

        this.unit = unit;
        this.name = name;
        this.health = health;
        this.attack = attack;
        this.hasBeenClicked = false;
        this.hasMoved = false;
        this.player = player;
    }

    // Overloaded constructor, Unit reference null, this can be set later
    public UnitWrapper(String name, int health, int attack, Player player) {
        this(null, name, health, attack, player);
    }

    // Overloaded constructor, Player & Unit reference null, these can be set later
    public UnitWrapper(String name, int health, int attack) {
        this(null, name, health, attack, null);
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

    public void setAbility(UnitAbility ability) {
        this.ability = ability;
    }

    public void useAbility() {
        this.ability.applyAbility(this);
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

}
