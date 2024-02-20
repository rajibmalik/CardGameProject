import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import structures.basic.Board;
import structures.basic.TileWrapper;

public class BoardTest {

    // This test checks that TileWrapper and Tile objects are being 
    // created at construction of the board
    @Test
    public void testBoardCreation() {
        Board board = new Board();
        TileWrapper[][] boardArray = board.getBoard();

        assertNotNull(boardArray);

        assertEquals(9, boardArray.length);
        assertEquals(5, boardArray[0].length);

        for (int i = 0; i <boardArray.length; i++) {
            for (int j = 0; j <boardArray[0].length; j++) {
                assertNotNull(boardArray[i][j]);
                assertNotNull(boardArray[i][j].getTile());
            }
        }
    }
}

