package abilities;
import structures.basic.UnitWrapper;

public class Deathwatch implements UnitAbility {
	private int attackBonus;
	private int healthBonus;
	
	public Deathwatch(int attackBonus, int healthBonus) {
		this.attackBonus = attackBonus;
		this.healthBonus = healthBonus;
	}
	
	public void applyAbility(UnitWrapper unit) {
        unit.setAttack(unit.getAttack() + this.attackBonus);
        unit.setHealth(unit.getHealth() + this.healthBonus);
	}
}
