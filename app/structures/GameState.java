package structures;

import java.util.ArrayList;

import controllers.PlayerController;
import structures.basic.Board;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import utils.BoardHighlighter;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {
	Deck playerDeck = new Deck("1");
	Hand playerHand = new Hand();
	Player humanPlayer = new Player(playerDeck, playerHand);
	PlayerController playerController = new PlayerController(humanPlayer);
	Board board = new Board();
	// BoardHighlighter boardHighlighter = new BoardHighlighter(board);

	public void initializeGame() {
		playerController.drawInitialHand();
	}

	public void playerDrawCard() {
		playerController.drawCard();
	}

	public Deck getPlayerDeck() {
		return playerController.getPlayerDeck();
	}
	
	public Board getBoard() {
		return board;
	}

	public Player getPlayer() {
		return this.humanPlayer;
	}

	// public void highlightBoard() {
	// 	boardHighlighter.setBoardToHighlight();
	// }
}
