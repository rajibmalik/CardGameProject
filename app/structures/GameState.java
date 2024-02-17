package structures;

import java.util.ArrayList;

import controllers.PlayerController;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;

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

	public void initializeGame() {
		playerController.drawInitialHand();
	}

	public Deck getPlayerDeck() {
		return playerController.getPlayerDeck();
	}

	
	public boolean gameInitalised = false;
	public boolean something = false;
}
