package structures.basic;
import abilities.UnitAbility;

public class UnitWrapper {
    private Unit unit; // Unit reference to backend representation of a Unit
    private String name; 
    private int health;
    private int attack;
    private boolean hasBeenClicked;
    private boolean hasMoved;
    private boolean hasAttacked;
    private Player player; // Player reference to the owner of the unit
    private UnitAbility ability;
    private final int id; // Unique identifier for each UnitWrapper, cannot be changed
    private static int nextId = 1; // By setting this as static, each instance of UnitWrapper shares this variable
                                   // allowing us to keep track of the counter

    // Default constructor
    public UnitWrapper(Unit unit, String name, int health, int attack, Player player, UnitAbility ability) {

        if (health < 0 || attack < 0) {
            throw new IllegalArgumentException("Health and attack values cannot be negative & Unit and Player cannot be null!");
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
    }

    public int getId() {
        return this.id;
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
    
    public void setHasAttacked(Boolean attacked) {
        this.hasAttacked = attacked;
    }

    public boolean getHasAttacked() {
        return this.hasAttacked;
    }


}
