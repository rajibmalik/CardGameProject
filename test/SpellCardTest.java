import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import abilities.SpellAbility;
import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Card;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.SpellCard;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitWrapper;
import structures.basic.Player;

public class SpellCardTest {
    private SpellCard spellCard;
    private SpellAbility spellAbility;
    private GameState gameState;
    private TileWrapper targetTile;
    private Player player;

    @Before
    public void setUp() {
        player = new Player(new Deck("1"), new Hand());
        spellAbility = new TestSpellAbility();
        Card card = new Card();
        spellCard = new SpellCard("Test Spell", 5, spellAbility, card);
        gameState = new GameState();
        targetTile = new TileWrapper(null, 0, 0);
    }

    @Test
    public void testApplySpellAbility() {
        Unit unit = new Unit();
        UnitWrapper unitWrapper = new UnitWrapper(unit, "Test Unit", 10, 5, gameState.getHumanPlayer(), null, targetTile);
        targetTile.setUnitWrapper(unitWrapper);
        targetTile.setHasUnit(true);
        spellCard.applySpellAbility(null, gameState, targetTile);
        assertEquals(15, unitWrapper.getHealth());
    }

    @Test
    public void testGetSpellAbility() {
        SpellAbility result = spellCard.getSpellAbility();
        assertNotNull(result);
        assertEquals(spellAbility, result);
    }

    @Test
    public void testGetName() {
        String result = spellCard.getName();
        assertNotNull(result);
        assertEquals("Test Spell", result);
    }

    @Test
    public void testGetManaCost() {
        int result = spellCard.getManaCost();
        assertEquals(5, result);
    }

    @Test
    public void testSetSpellAbility() {
        SpellAbility newSpellAbility = new TestSpellAbility();
        spellCard.setSpellAbility(newSpellAbility);
        assertEquals(newSpellAbility, spellCard.getSpellAbility());
    }

    private class TestSpellAbility implements SpellAbility {
        @Override
        public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {
            if (targetTile.getHasUnit()) {
                UnitWrapper unitWrapper = targetTile.getUnit();
                unitWrapper.increaseHealth(5);
            }
        }
    }
}