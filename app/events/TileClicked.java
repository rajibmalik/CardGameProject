package events;


import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import abilities.UnitAbility;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import structures.GameState;
import structures.basic.Card;
import structures.basic.CardWrapper;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import abilities.UnitAbility;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{
	private TileWrapper tileWrapper;

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		setTileClicked(gameState, message);

		// finds and sets if a card has been clicked
		CardWrapper cardPlayed = null;

		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if(cardWrapper.hasBeenClicked()==true) {
				cardPlayed = cardWrapper;
			}
		}

		if (cardPlayed != null && canPlayCard(gameState, cardPlayed)) {
			playCard(cardPlayed, gameState, out);
			deductAndRenderMana(gameState, out, cardPlayed);
			removeCard(out, gameState, cardPlayed);
		}

	}

	private void setTileClicked(GameState gameState, JsonNode message) {
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		this.tileWrapper = gameState.getBoard().getBoard()[tilex][tiley];
	}

	private boolean canPlayCard(GameState gameState, CardWrapper cardWrapper) {
		if (cardWrapper.getManaCost() <= gameState.getCurrentPlayer().getMana()) {
			return true;
		}

		return false;
	}

	public void playCard(CardWrapper cardWrapper, GameState gameState, ActorRef out) {
			if (cardWrapper instanceof UnitCard) {
				UnitCard unitCard = (UnitCard) cardWrapper;
				// render front end of the unit
				Unit unit = renderUnit(out, unitCard, this.tileWrapper.getTile() );
				// create unit in the backend
				createUnit(unit, unitCard, gameState);
			} else if (cardWrapper instanceof SpellCard) {
				SpellCard spellCard = (SpellCard) cardWrapper;
				spellCard.applySpellAbility(gameState.getHumanPlayer(), this.tileWrapper);
			}
	}

	private void deductAndRenderMana(GameState gameState, ActorRef out, CardWrapper cardWrapper) {
		deductManaFromBackEnd(gameState, cardWrapper);
		renderManaOnFrontEnd(out, gameState);
	}

	private void deductManaFromBackEnd(GameState gameState, CardWrapper cardWrapper) {
		if (gameState.getCurrentPlayer() == gameState.getHumanPlayer()) {
			PlayerController.deductMana(gameState.getCurrentPlayer(), cardWrapper);
		} 
	}

	private void renderManaOnFrontEnd(ActorRef out, GameState gameState) {
		if (gameState.getCurrentPlayer() == gameState.getHumanPlayer()) {
			BasicCommands.setPlayer1Mana(out, gameState.getCurrentPlayer());
		} else {
			BasicCommands.setPlayer1Mana(out, gameState.getCurrentPlayer());
		}
	}
	
	// renders frontend representation of unit
	public Unit renderUnit(ActorRef out, UnitCard unitCard, Tile tile) {
		String config= unitCard.getCard().getUnitConfig();
		
		Unit unit = BasicObjectBuilders.loadUnit(config, UnitWrapper.nextId, Unit.class);
		unit.setPositionByTile(tile);
		BasicCommands.drawUnit(out, unit, tile);
		
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitAttack(out, unit, unitCard.getAttack());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		BasicCommands.setUnitHealth(out, unit, unitCard.getHealth());
		try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}

		return unit;
	}

	// creates backend representation of unit
	private void createUnit(Unit unit, CardWrapper cardWrapper, GameState gameState) {
		UnitCard unitCard = (UnitCard) cardWrapper;
		String name = unitCard.getName();
		int health = unitCard.getHealth();
		int attack = unitCard.getAttack();
		Player player = gameState.getCurrentPlayer();
		UnitAbility  unitAbility = unitCard.getUnitAbility();

		UnitWrapper unitWrapper = new UnitWrapper(unit, name, health, attack, player, unitAbility);
		this.tileWrapper.setUnitWrapper(unitWrapper);

		System.out.println(unitWrapper);
	}
	
	private void removeCard(ActorRef out, GameState gameState, CardWrapper cardPlayed) {
		clearRenderedHand(out, gameState);
		removeCardFromBackEnd(gameState, cardPlayed);
		renderHand(out, gameState);
	}

	private void removeCardFromBackEnd(GameState gameState, CardWrapper cardWrapper) {
		PlayerController playerController = gameState.getHumanPlayerController();
		playerController.removeCardFromHand(cardWrapper.getId());
		// if (gameState.getCurrentPlayer() == gameState.getHumanPlayer()) {
			
		// } 
	}

	private void clearRenderedHand(ActorRef out, GameState gameState) {
		Hand hand = gameState.getPlayerHand();

		for (int i = 0; i < hand.getHand().size(); i++) {
			BasicCommands.deleteCard(out, i + 1);
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	private void renderHand(ActorRef out, GameState gameState) {
		if (gameState.getCurrentPlayer() == gameState.getHumanPlayer()) {
			System.out.println("CURRENT PLAYER: HUMAN"); 
		} else {
			System.out.println("CURRENT PLAYER: AI"); 
		}
		
		Hand hand = gameState.getPlayerHand();
		int handPosition = 1;

		for (CardWrapper cardWrapper:hand.getHand()) {
			BasicCommands.drawCard(out, cardWrapper.getCard(), handPosition, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			handPosition ++;

			if (handPosition > hand.getHand().size() + 1) {
				break;
			}
		}
	}
}
	
		
	
