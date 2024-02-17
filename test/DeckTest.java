import static org.junit.Assert.assertEquals;
import org.junit.Test;
import structures.basic.Deck;

public class DeckTest {
    @Test
    public void deckInitialisesCorrectNumberOfCards() {
        Deck deck = new Deck("1");

        assertEquals(20,deck.getDeck().size());
    }

    @Test
    public void playerDeckInitialisesCorrectNames() {
        // Test checks to see if the player cards are initialised with the correct names
        Deck deck = new Deck("1");

        assertEquals("Bad Omen",deck.getDeck().get(0).getName());
        assertEquals("Horn of the Forsaken",deck.getDeck().get(1).getName());
        assertEquals("Gloom Chaser",deck.getDeck().get(2).getName());
        assertEquals("Shadow Watcher",deck.getDeck().get(3).getName());
        assertEquals("Wraithling Swarm",deck.getDeck().get(4).getName());
        assertEquals("Nightsorrow Assassin",deck.getDeck().get(5).getName());
        assertEquals("Rock Pulveriser",deck.getDeck().get(6).getName());
        assertEquals("Dark Terminus",deck.getDeck().get(7).getName());
        assertEquals("Bloodmoon Priestess",deck.getDeck().get(8).getName());
        assertEquals("Shadowdancer",deck.getDeck().get(9).getName());
        assertEquals("Bad Omen",deck.getDeck().get(10).getName());
        assertEquals("Horn of the Forsaken",deck.getDeck().get(11).getName());
        assertEquals("Gloom Chaser",deck.getDeck().get(12).getName());
        assertEquals("Shadow Watcher",deck.getDeck().get(13).getName());
        assertEquals("Wraithling Swarm",deck.getDeck().get(14).getName());
        assertEquals("Nightsorrow Assassin",deck.getDeck().get(15).getName());
        assertEquals("Rock Pulveriser",deck.getDeck().get(16).getName());
        assertEquals("Dark Terminus",deck.getDeck().get(17).getName());
        assertEquals("Bloodmoon Priestess",deck.getDeck().get(18).getName());
        assertEquals("Shadowdancer",deck.getDeck().get(19).getName());
    }

    public void aiDeckInitialisesCorrectNames() {
        // Test checks to see if the ai cards are initialised with the correct names
        Deck deck = new Deck("2");

        assertEquals("Skyrock Golem",deck.getDeck().get(0).getName());
        assertEquals("Swamp Entangler",deck.getDeck().get(1).getName());
        assertEquals("Silverguard Knight",deck.getDeck().get(2).getName());
        assertEquals("Saberspine Tiger",deck.getDeck().get(3).getName());
        assertEquals("Beamshock",deck.getDeck().get(4).getName());
        assertEquals("Young Flamewing",deck.getDeck().get(5).getName());
        assertEquals("Silverguard Squire",deck.getDeck().get(6).getName());
        assertEquals("Ironcliff Guardian",deck.getDeck().get(7).getName());
        assertEquals("Sundrop Elixir",deck.getDeck().get(8).getName());
        assertEquals("Truestrike",deck.getDeck().get(9).getName());
    }


}
