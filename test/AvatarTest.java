import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import structures.basic.Avatar;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;

public class AvatarTest {

    private Avatar avatar;
    private Player player;
    
    @Before
    public void setUp() {
        Unit unit = new Unit();
        Player player = new Player(new Deck("1"), new Hand());
        TileWrapper tile = new TileWrapper(new Tile(), 2, 8);

        // Create instance of Avatar for testing
        avatar = new Avatar(unit, "TestAvatar", 20, 2, player, null, tile);
    }
    
    @Test
    public void initialisedWithTwoAttack() {
        assertEquals(2, avatar.getAttack());
    }

    @Test
    public void initialisedWithTwoHealth() {
        assertEquals(2, avatar.getHealth());
    }

    @Test
    public void initialsedWithPlayer() {
        assertEquals(player, avatar.getPlayer());
    }

    @Test
    public void testArtifactActivation() {
        // Artifact should not be active initially, so check to confirm this
        assertFalse(avatar.isArtifactActive());
		
		// Setting artifact to active
        avatar.setArtifactActive(true);
        assertTrue(avatar.isArtifactActive());
    }

    @Test
    public void testRobustness() {
        // Initial robustness should be zero
        assertEquals(0, avatar.getRobustness());

        // Set robustness, check to see if it's updated
        avatar.setRobustness(5);
        assertEquals(5, avatar.getRobustness());

        // Decrease robustness check
        avatar.decreaseRobustness();
        assertEquals(4, avatar.getRobustness());

        // Decrease robustness until artifact is destroyed
        avatar.setArtifactActive(true);
        avatar.decreaseRobustness();
        avatar.decreaseRobustness();
        assertFalse(avatar.isArtifactActive());
        assertEquals(2, avatar.getRobustness());
    }
}