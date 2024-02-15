package structures.basic;

import abilities.UnitAbility;

// This class represents the player avatar and extends the UnitWrapper class

public class Avatar extends UnitWrapper {
    private int robustness;

    public Avatar(Unit unit, String name, Player player, UnitAbility ability) {
        super(unit, name, 2, 2, player, ability); // Player avatar is initialised with 2 health and attack
        this.robustness = 0;
    }

    public int getRobustness() {
        return this.robustness;
    }

    public void setRobustness(int robustness) {
        this.robustness = robustness;
    }
}
