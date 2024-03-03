import org.junit.Before;
import org.junit.Test;
import structures.basic.Board;
import structures.basic.TileWrapper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertArrayEquals;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardCreation() {
        TileWrapper[][] boardArray = board.getBoard();

        assertNotNull(boardArray);
        assertEquals(9, boardArray.length);
        assertEquals(5, boardArray[0].length);

        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[0].length; j++) {
                assertNotNull(boardArray[i][j]);
                assertNotNull(boardArray[i][j].getTile());
            }
        }
    }

    @Test
    public void testSetBoard() {
        TileWrapper[][] newBoard = new TileWrapper[9][5];
        board.setBoard(newBoard);
        assertArrayEquals(newBoard, board.getBoard());
    }

    @Test
    public void testBoardInitialization() {
        TileWrapper[][] boardArray = board.getBoard();

        for (int i = 0; i < boardArray.length; i++) {
            for (int j = 0; j < boardArray[0].length; j++) {
                assertEquals(i, boardArray[i][j].getXpos());
                assertEquals(j, boardArray[i][j].getYpos());
            }
        }
    }
}

