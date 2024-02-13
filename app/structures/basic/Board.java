package structures.basic;

public class Board {

	private TileWrapper[][] board;
	
	public Board() {
		this.board = new TileWrapper[9][5];
	}
	
	public void createBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				this.board[i][j] = new TileWrapper(i, j);
			}
		}
	}

	public TileWrapper[][] getBoard() {
		return board;
	}
	
	public void setBoard(TileWrapper[][] board) {
		this.board = board;
	}


	
	
	
}







