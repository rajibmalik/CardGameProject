package events;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import demo.CommandDemo;
import demo.Loaders_2024_Check;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;

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
		initializeBackEnd(out, gameState);
		initializehumanPlayer(out, gameState);
		initalizeAiPlayer(out, gameState);
		// CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
	}

	public void initializeBackEnd(ActorRef out, GameState gameState) {
		initializeGameState(gameState);
		highlightBoard(out, gameState);
	}

	public void initializehumanPlayer(ActorRef out, GameState gameState) {
		renderHand(out, gameState);
		Unit playerAvatar = setPlayerAvatarFrontend(out, gameState);
		Player humanPlayer = setPlayerAvatarBackend(gameState, playerAvatar);
		setPlayerHealth(out, humanPlayer);
		setPlayerMana(out, gameState.getHumanPlayerController());
	}

	public void initalizeAiPlayer(ActorRef out, GameState gameState) {
		Unit  aiAvatar = setAiAvatarFrontend(out, gameState);
		Player aiPlayer = setAiAvatarBackend(gameState, aiAvatar);
		setAvatarHealth(out, aiPlayer);
	}

	public void initializeGameState(GameState gameState) {
		gameState.initializeGame();
	}

	public void setPlayerHealth(ActorRef out, Player player) {
		BasicCommands.setPlayer1Health(out, player);
	}

	// test 
	public void setPlayerMana(ActorRef out, PlayerController playerController) {
		int turn = playerController.getTurn();
		int mana = turn + 1;

		Player player = playerController.getPlayer();
		player.setMana(mana);
		
		BasicCommands.setPlayer1Mana(out, playerController.getPlayer());
	}

	public void setAvatarHealth(ActorRef out, Player player) {
		BasicCommands.setPlayer2Health(out, player);
	}


	public void renderHand(ActorRef out, GameState gameState) {
		System.out.println("Rendering hand");

		Hand hand = gameState.getPlayerHand();
		int handPosition = 1;

		for (CardWrapper cardWrapper:hand.getHand()) {
			BasicCommands.drawCard(out, cardWrapper.getCard(), handPosition, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			handPosition ++;

			if (handPosition == 4) {
				break;
			}
		}
	}

	public void highlightBoard(ActorRef out, GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				Tile tile = board[i][j].getTile();
				BasicCommands.drawTile(out, tile, 0);
				try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}

	public Player setPlayerAvatarBackend(GameState gameState, Unit unit) {
		Player player = gameState.getHumanPlayer();

		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileWrapper tileWrapper = board[1][2];
		Avatar avatar = new Avatar(unit, "Player", 20, 2, player, null, tileWrapper);

		tileWrapper.setUnitWrapper(avatar);
		player.addUnit(avatar);

		return player;
	}

	public Unit setPlayerAvatarFrontend(ActorRef out, GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		Tile tile = board[1][2].getTile();
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
		unit.setPositionByTile(tile); 
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, 20);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

	
		return unit;
	}

	public Player setAiAvatarBackend(GameState gameState, Unit unit) {
		Player player = gameState.getAIPlayer();

		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileWrapper tileWrapper = board[7][2];
		
		Avatar avatar = new Avatar(unit, "AI", 20, 2, player, null, tileWrapper);
		tileWrapper.setUnitWrapper(avatar);
		player.addUnit(avatar);

		return player;
	}

	public Unit setAiAvatarFrontend (ActorRef out, GameState gameState) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		Tile tile = board[7][2].getTile();
		Unit unit = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 0, Unit.class);
		unit.setPositionByTile(tile); 
		BasicCommands.drawUnit(out, unit, tile);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, 2);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, 20);
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
	}

}


