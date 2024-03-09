package structures.basic;
import abilities.SpellAbility;
import akka.actor.ActorRef;
import structures.GameState;

/**
 * The SpellCard class represents a spell card in the game.
 * Spell cards are special cards that have a mana cost and a spell ability associated with them.
 * When played, spell cards can apply their spell abilities to targeted tiles.
 * 
 * @author Eldhos Thomas
 */
public class SpellCard extends CardWrapper {
    private SpellAbility spellAbility;

    /**
     * Constructs a SpellCard with the specified name, mana cost, spell ability, and base card.
     * @param name         The name of the spell card.
     * @param manaCost     The mana cost required to play the spell card.
     * @param spellAbility The spell ability associated with the spell card.
     * @param card         The base card from which this spell card is derived.
     */
    public SpellCard(String name, int manaCost, SpellAbility spellAbility, Card card) {
        super(manaCost, name, card);
        this.spellAbility = spellAbility;
    }

    /**
     * Applies the spell ability of the spell card to a targeted tile in the game.
     * @param out         The reference to the actor to send messages to.
     * @param gameState   The current game state.
     * @param targetTile  The targeted tile where the spell ability is applied.
     */
    public void applySpellAbility(ActorRef out, GameState gameState, TileWrapper targetTile) {
        this.spellAbility.castSpell(out, gameState, targetTile);
    }

    /**
     * Retrieves the spell ability associated with the spell card.
     * @return The spell ability associated with the spell card.
     */
    public SpellAbility getSpellAbility() {
        return this.spellAbility;
    }

    /**
     * Retrieves the name of the spell card.
     * @return The name of the spell card.
     */
    public String getName() {
        return super.getName();
    }

    /**
     * Retrieves the mana cost required to play the spell card.
     * @return The mana cost of the spell card.
     */
    public int getManaCost() {
        return super.getManaCost();
    }

    /**
     * Sets the spell ability associated with the spell card.
     * @param spellAbility The spell ability to be associated with the spell card.
     */
    public void setSpellAbility(SpellAbility spellAbility) {
        this.spellAbility = spellAbility;
    }
}
