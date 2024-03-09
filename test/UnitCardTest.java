import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import abilities.UnitAbility;
import structures.basic.Card;
import structures.basic.CardWrapper;
import structures.basic.UnitCard;

public class UnitCardTest {
    private UnitCard unitCard;
    private Card card; // Assume this is a valid Card object
    private UnitAbility ability; // Assume this is a valid UnitAbility object

    @Before
    public void setUp() {
        card = new Card();
        unitCard = new UnitCard(5, "Test UnitCard", card, 10, 20, ability);
    }

    @Test
    public void testGetAttack() {
        int result = unitCard.getAttack();
        assertEquals(10, result);
    }

    @Test
    public void testGetHealth() {
        int result = unitCard.getHealth();
        assertEquals(20, result);
    }

    @Test
    public void testGetUnitAbility() {
        UnitAbility result = unitCard.getUnitAbility();
        assertNotNull(result);
        assertEquals(ability, result);
    }

}

