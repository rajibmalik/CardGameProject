package structures.basic;
import abilities.SpellAbility;
import akka.actor.ActorRef;
import structures.GameState;

public class SpellCard extends CardWrapper {
    private SpellAbility spellAbility;

    public SpellCard(String name, int manaCost, SpellAbility spellAbility, Card card) {
        super(manaCost, name, card);
        this.spellAbility = spellAbility;
    }

    public void applySpellAbility(ActorRef out, GameState gameState, TileWrapper targetTile) {
        this.spellAbility.castSpell(out,gameState, targetTile);
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
