package structures;

import controllers.PlayerController;
import structures.basic.Board;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;

/**
 * This class can be used to hold information about the on-going game. Its
 * created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

	private Player humanPlayer;
	private Player aiPlayer;
	private Player currentPlayer;
	private PlayerController humanPlayerController;
	private PlayerController aiPlayerController;
	Board board = new Board();

	public GameState() {
		// Initialize human and AI players
		humanPlayer = new Player(new Deck("1"), new Hand());
		aiPlayer = new Player(new Deck("2"), new Hand());

		// Set the human player as the initial current player
		currentPlayer = humanPlayer;

		// Create a PlayerController for both human and AI players
		humanPlayerController = new PlayerController(humanPlayer);
		aiPlayerController = new PlayerController(aiPlayer);

	}

	public void initializeGame() {
		humanPlayerController.drawInitialHand();
		humanPlayerController.setTurnMana();

		aiPlayerController.drawInitialHand();
		aiPlayerController.setTurnMana();
	}

	public void playerDrawCard() {
		if (currentPlayer == humanPlayer) {
			humanPlayerController.drawCard();
		} else {
			aiPlayerController.drawCard();
		}

	}

	public Deck getPlayerDeck() {
		if (currentPlayer == humanPlayer) {
			return humanPlayerController.getPlayerDeck();
		} else {
			return aiPlayerController.getPlayerDeck();
		}
	}

	public Board getBoard() {
		return board;
	}

	public Hand getPlayerHand() {
		if (currentPlayer == humanPlayer) {
			return humanPlayerController.getPlayerHand();
		} else {
			return aiPlayerController.getPlayerHand();
		}
	}

	public void switchPlayer() {
		if (currentPlayer == humanPlayer) {
			currentPlayer = aiPlayer;
		} else {
			currentPlayer = humanPlayer;
		}
	}

	public PlayerController getHumanPlayerController() {
		return humanPlayerController;
	}

	public PlayerController getAIPlayerController() {
		return aiPlayerController;
	}

	public Player getCurrentPlayer() {
		return this.currentPlayer;
	}

	public Player getAIPlayer() {
		return this.aiPlayer;
	}

	public Player getHumanPlayer() {
		return this.humanPlayer;
	}
}
