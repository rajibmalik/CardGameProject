package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.Card;
import structures.basic.CardWrapper;
import utils.OrderedCardLoader;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		initializeGame(gameState);
		initializeFrontEnd(out, gameState);


		
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		// User 1 makes a change
		// CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//Loaders_2024_Check.test(out);
	}

	public void initializeGame(GameState gameState) {
		gameState.initializeGame();
	}

	public void initializeFrontEnd(ActorRef out, GameState gameState) {
		for (int iteration = 0; iteration < 3; iteration++) {
			int handPosition = 1;
			for (Card card : OrderedCardLoader.getPlayer1Cards(1)) {
				BasicCommands.drawCard(out, card, handPosition, 1);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handPosition++;
	
				if (handPosition == 4) {
					break;
				}
			}
		}

	}
}


