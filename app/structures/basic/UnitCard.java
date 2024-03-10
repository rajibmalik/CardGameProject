package structures.basic;

import abilities.UnitAbility;

/**
 * This class represents a UnitCard which extends the CardWrapper.
 * It has variables for attack, health and a UnitAbility
 * @author Rajib Malik
*/

public class UnitCard extends CardWrapper{
    private int attack;
    private int health;
    private UnitAbility ability;

    public UnitCard(int manaCost, String name, Card card,int attack, int health, UnitAbility ability) {
        super(manaCost, name, card);
        this.attack = attack;
        this.health = health;
        this.ability = ability;
    }

    public int getAttack() {
        return this.attack;
    }

    public int getHealth() {
        return this.health;
    }

    public UnitAbility getUnitAbility() {
        return this.ability;
    }
}
