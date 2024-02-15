package structures.basic;
import abilities.SpellAbility;

public class SpellCard extends CardWrapper {
    private SpellAbility spellAbility;

    public SpellCard(String name, int manaCost, SpellAbility spellAbility) {
        super(manaCost, name);
        this.spellAbility = spellAbility;
    }

    public void applySpellAbility(Player player, TileWrapper targetTile) {
        this.spellAbility.castSpell(player, targetTile);
    }

    public SpellAbility getSpellAbility() {
        return this.spellAbility;
    }

    public String getName() {
        return super.getName();
    }

    public int getManaCost() {
        return super.getManaCost();
    }

    public void setSpellAbility(SpellAbility spellAbility) {
        this.spellAbility = spellAbility;
    }
}
