package structures;

import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.AIPlayerController;
import controllers.PlayerController;
import structures.basic.Board;
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
			while (aiPlayerController.canPlayUnitCard(gameState) && (!aiPlayerController.getValidTiles(gameState).isEmpty())) {
				System.out.println("AI TURN");
				// play unit card with the highest mana
				aiPlayerController.playUnitCard(out, gameState);
			}
			System.out.println("Ending turn");
			
			
		aiPlayerController.endTurn(gameState);
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
	
	public void unitDealth(Player currentPlayer, UnitWrapper unitWrapper) {
		if(unitWrapper.getHealth()<=0){
			unitWrapper.getTile().setHasUnit(false);
			unitWrapper.getTile().setUnitWrapper(null);
			unitWrapper.setTile(null);
			removeUnit(unitWrapper);
		}
	}
	
	  public void removeUnit(UnitWrapper unitWrapper){
	    	currentPlayer.getUnits().remove(unitWrapper);
	    }
}
