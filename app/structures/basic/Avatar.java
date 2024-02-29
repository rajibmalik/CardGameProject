package structures.basic;

import abilities.UnitAbility;

// This class represents the player avatar and extends the UnitWrapper class

public class Avatar extends UnitWrapper {
    private int robustness;

    public Avatar(Unit unit, String name, int health, int attack, Player player, UnitAbility ability, TileWrapper tile) {
        super(unit, name, 20, 2, player, ability,tile); // Player avatar is initialised with 2 health and attack
        this.robustness = 0;
    }

    public int getRobustness() {
        return this.robustness;
    }

    public void setRobustness(int robustness) {
        this.robustness = robustness;
    }
}