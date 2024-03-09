package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.AIPlayerController;
import controllers.PlayerController;
import controllers.TileHighlightController;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Unit;
import structures.basic.UnitWrapper;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * Upon recieving the initalize message, the backend and frontend gamestate is initialized 
 * in relation to the board, players avatars, health, mana, deck and hand
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 * @author Rajib Malik
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		initializeBackEnd(out, gameState);
		initializehumanPlayer(out, gameState);
		initalizeAiPlayer(out, gameState);
		
		// CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		
		for(UnitWrapper unit : gameState.getHumanPlayer().getUnits()) {
			System.out.println("Here are the human players units:");
			System.out.println(unit.getName() + " on tile" + unit.getTile().getXpos());
		}
		
		for(UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
			System.out.println("Here are the ai players units:");
			System.out.println(unit.getName() + " on tile" + unit.getTile().getXpos());
		}
	}

	 /**
     * Prepares the backend game state and the board
    */
	public void initializeBackEnd(ActorRef out, GameState gameState) {
		gameState.initializeGame();
		TileHighlightController.initialHighlightBoard(out, gameState);
	}

	 /**
     * Prepares the human players hand, Avatar and health and mana 
    */
	public void initializehumanPlayer(ActorRef out, GameState gameState) {
		PlayerController.renderInitialHand(out, gameState);
		Unit playerAvatar = UnitController.renderPlayerAvatar(out, gameState);
		Player humanPlayer = PlayerController.setPlayerAvatar(gameState, playerAvatar);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		PlayerController.setInitialMana(out, gameState.getHumanPlayerController());
	}

	 /**
     * Prepares the AI Players avatar and health
    */
	public void initalizeAiPlayer(ActorRef out, GameState gameState) {
		Unit  aiAvatar = UnitController.renderAIAvatar(out, gameState);
		Player aiPlayer = AIPlayerController.setAIPlayerAvatar(gameState, aiAvatar);
		BasicCommands.setPlayer2Health(out, aiPlayer);
	}

}


