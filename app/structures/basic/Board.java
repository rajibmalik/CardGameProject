package structures.basic;

import utils.BasicObjectBuilders;

public class Board {

	private TileWrapper[][] board;
	
	public Board() {
		this.board = new TileWrapper[9][5];
		createBoard();
	}
	
	public void createBoard() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = BasicObjectBuilders.loadTile(i, j);
				this.board[i][j] = new TileWrapper(tile, i, j);
			}
		}
	}

	public TileWrapper[][] getBoard() {
		return this.board;
	}
	
	public void setBoard(TileWrapper[][] board) {
		this.board = board;
	}
}







