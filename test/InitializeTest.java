import static org.junit.Assert.assertEquals;

import org.junit.Test;

import events.Initalize;
import structures.GameState;
import structures.basic.TileWrapper;

public class InitializeTest {

    @Test
    public void testPlayerAvatarInitialisedInBackend() {
        GameState gameState = new GameState();
        Initalize initialize = new Initalize();
        initialize.initializeGameState(gameState);
        initialize.processEvent(null, gameState, null);

        TileWrapper[][] board = gameState.getBoard().getBoard();
        TileWrapper tileWrapper = board[1][2];

        assertEquals("Player", tileWrapper.getUnit().getName());
        assertEquals(20, tileWrapper.getUnit().getHealth());
        assertEquals(2, tileWrapper.getUnit().getAttack());
    }
    
}
