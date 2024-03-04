import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import structures.basic.CardWrapper;
import structures.basic.Deck;

public class DeckTest {
    private Deck deck;

    @Before
    public void setUp() {
        deck = new Deck("1");
    }

    @Test
    public void testDeckInitialisation() {
        assertEquals(20, deck.getDeck().size());

        assertEquals("Bad Omen", deck.getDeck().get(0).getName());
        assertEquals("Horn of the Forsaken", deck.getDeck().get(1).getName());
        assertEquals("Gloom Chaser", deck.getDeck().get(2).getName());
        assertEquals("Shadow Watcher", deck.getDeck().get(3).getName());
        assertEquals("Wraithling Swarm", deck.getDeck().get(4).getName());
        assertEquals("Nightsorrow Assassin", deck.getDeck().get(5).getName());
        assertEquals("Rock Pulveriser", deck.getDeck().get(6).getName());
        assertEquals("Dark Terminus", deck.getDeck().get(7).getName());
        assertEquals("Bloodmoon Priestess", deck.getDeck().get(8).getName());
        assertEquals("Shadowdancer", deck.getDeck().get(9).getName());
        assertEquals("Bad Omen", deck.getDeck().get(10).getName());
        assertEquals("Horn of the Forsaken", deck.getDeck().get(11).getName());
        assertEquals("Gloom Chaser", deck.getDeck().get(12).getName());
        assertEquals("Shadow Watcher", deck.getDeck().get(13).getName());
        assertEquals("Wraithling Swarm", deck.getDeck().get(14).getName());
        assertEquals("Nightsorrow Assassin", deck.getDeck().get(15).getName());
        assertEquals("Rock Pulveriser", deck.getDeck().get(16).getName());
        assertEquals("Dark Terminus", deck.getDeck().get(17).getName());
        assertEquals("Bloodmoon Priestess", deck.getDeck().get(18).getName());
        assertEquals("Shadowdancer", deck.getDeck().get(19).getName());
    }

    @Test
    public void testGetTopCard() {
        CardWrapper topCard = deck.getTopCard();
        assertNotNull("Top card should not be null", topCard);
        assertTrue("Top card should be instance of CardWrapper", topCard instanceof CardWrapper);
    }

    @Test
    public void testGetTopCardIndex() {
        int topCardIndex = deck.getTopCardIndex();
        assertTrue("Top card index should be between 0 and 19", topCardIndex >= 0 && topCardIndex < 20);
    }

    @Test
    public void testGetDeck() {
        ArrayList<CardWrapper> deckList = deck.getDeck();
        assertNotNull("Deck list should not be null", deckList);
        assertFalse("Deck list should not be empty", deckList.isEmpty());
    }

    @Test
    public void testExceptionHandling() {
        try {
            new Deck("3"); // This should throw an exception as there is no deck "3"
            fail("Expected an RuntimeException to be thrown");
        } catch (RuntimeException e) {
            String expectedMessage = "Invalid deck number";
            String actualMessage = e.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }
}
