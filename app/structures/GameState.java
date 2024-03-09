package structures;

import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.AIPlayerController;
import controllers.PlayerController;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.UnitWrapper;

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
	private AIPlayerController aiPlayerController;
	Board board = new Board();

	public GameState() {
		// Initialize human and AI players
		humanPlayer = new Player(new Deck("1"), new Hand());
		aiPlayer = new Player(new Deck("2"), new Hand());

		// Set the human player as the initial current player
		currentPlayer = humanPlayer;

		// Create a PlayerController for both human and AI players
		humanPlayerController = new PlayerController(humanPlayer);
		aiPlayerController = new AIPlayerController(aiPlayer);
	}

	public void AITakeTurn(ActorRef out, GameState gameState) {
		if (currentPlayer == getAIPlayer()) {
		aiPlayerController.takeTurn(out, gameState);
		System.out.println("Ending turn");
			
		gameState.getAIPlayerController().clearMana();
		resetAIUnitMovementAndAttack();
		BasicCommands.setPlayer2Mana(out, gameState.getCurrentPlayer());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		playerGainMain(out, gameState);
		}
	}
	
	public void resetHumanUnitMovementAndAttack() {
		
		List<UnitWrapper> units = getHumanPlayer().getUnits();

		for (UnitWrapper unit : units) {
			unit.setHasAttacked(false);
			unit.setHasMoved(false);
		}
	}
	
public void resetAIUnitMovementAndAttack() {
		
		List<UnitWrapper> units =getAIPlayer().getUnits();

		for (UnitWrapper unit : units) {
			unit.setHasAttacked(false);
			unit.setHasMoved(false);
		}
	}

	public void playerGainMain(ActorRef out, GameState gameState) {
		gameState.getHumanPlayerController().clearMana();
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getHumanPlayerController().nextTurn();
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
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
		return this.board;
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
	
	/**
	 * Helper method to un-click all units in the players array of units  
	 * @param gameState
	 */
	public  void unclickAllUnits(GameState gameState) {
		Player currentPlayer =  gameState.getCurrentPlayer();
		for (UnitWrapper unit : currentPlayer.getUnits()) {
			unit.setHasBeenClicked(false);
		}
	}
	
	public UnitWrapper getClickedUnit(GameState gameState) {
		for (UnitWrapper unit : gameState.getCurrentPlayer().getUnits()) {
			if (unit.getHasBeenClicked() == true) {
				return unit;
			}
		}
		return null;
	}
	
	public boolean hasUnitBeenClicked(GameState gameState) {
		for (UnitWrapper unitWrapper : gameState.getCurrentPlayer().getUnits()) {
			if (unitWrapper.getHasBeenClicked() == true) {
				return true;
			}
		}

		return false;
	}

	public CardWrapper getClickedCard(GameState gameState) {
		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if (cardWrapper.hasBeenClicked() == true) {
				return cardWrapper;
			}
		}
		return null;
	}
	
	public boolean hasCardBeenClicked(GameState gameState) {
		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if (cardWrapper.hasBeenClicked() == true) {
				return true;
			}
		}

		return false;
	}

	// announcing game result
	public void announceResult(ActorRef out, GameState gameState, Player player) {
		Player aiPlayer = gameState.getAIPlayer();
		Player humanPlayer = gameState.getHumanPlayer();
		String result;
		if (isHealthZero(aiPlayer)) {
			result = "VICTORY"; // if AI Player's avatar health is zero then the human player has won
		} else if (isHealthZero(humanPlayer)) {
			result = "DEFEAT"; // if human Player's avatar health is zero then they lost the game
		} else { // no outcome yet
			return;
		}

		// send notification to player interface
		BasicCommands.addPlayer1Notification(out, "Game Result: " + result, 10); // Display for 10 seconds
	}

	// checks if either avatar reaches zero health, if so it returns true
	private static boolean isHealthZero(Player player) {
		return player.getUnits().stream().anyMatch(unit -> unit instanceof Avatar && unit.getHealth() <= 0);
	}
}
