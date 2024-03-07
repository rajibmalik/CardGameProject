package controllers;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.CardWrapper;
import structures.basic.Deck;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;

public class PlayerController {
	Player player;
	private int turn;
	private static final int MAX_MANA = 9; // Maximum mana value

	public PlayerController(Player player) {
		this.player = player;
		this.turn = 1;
	}

	public void drawInitialHand() {
		for (int i = 0; i < 3; i++) {
			CardWrapper cardWrapper = player.getDeck().getTopCard();
			player.getHand().addCard(cardWrapper);
		}
	}

	public boolean drawCard() {
		if (player.getHand().getHand().size() < 5) {
			CardWrapper cardWrapper = player.getDeck().getTopCard();
			player.getHand().addCard(cardWrapper);

			return true;
		}

		return false;
	}

	public static void removeCard(ActorRef out, GameState gameState, CardWrapper cardPlayed) {
		clearRenderedHand(out, gameState);
		removeCardFromBackEnd(gameState, cardPlayed);
		renderHand(out, gameState);
	}

	private static void removeCardFromBackEnd(GameState gameState, CardWrapper cardWrapper) {
		PlayerController playerController = gameState.getHumanPlayerController();
		playerController.removeCardFromHand(cardWrapper.getId());

	}
	
	public void removeCardFromHand(int id) {
		ArrayList<CardWrapper> hand = player.getHand().getHand();

		// Remove cardWrapper if the cardWrapper id is equal to id
		hand.removeIf(cardWrapper -> cardWrapper.getId() == id);
	}

	public void removeCardFromHand(CardWrapper cardWrapper) {
		ArrayList<CardWrapper> hand = player.getHand().getHand();

		if (hand.contains(cardWrapper)) {
			hand.remove(cardWrapper);
		}
	}

	// front end rendering of human players hand
	private static void renderHand(ActorRef out, GameState gameState) {
		if (gameState.getCurrentPlayer() == gameState.getHumanPlayer()) {
			System.out.println("CURRENT PLAYER: HUMAN");
		} else {
			System.out.println("CURRENT PLAYER: AI");
		}

		Hand hand = gameState.getPlayerHand();
		int handPosition = 1;

		for (CardWrapper cardWrapper : hand.getHand()) {
			BasicCommands.drawCard(out, cardWrapper.getCard(), handPosition, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			handPosition++;

			if (handPosition > hand.getHand().size() + 1) {
				break;
			}
		}
	}

	// front end remove card from hand
	private static void clearRenderedHand(ActorRef out, GameState gameState) {
		Hand hand = gameState.getPlayerHand();

		for (int i = 0; i < hand.getHand().size(); i++) {
			BasicCommands.deleteCard(out, i + 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setTurnMana() {

		int calculatedMana = 1 + turn;

		if (calculatedMana > MAX_MANA) {
			player.setMana(MAX_MANA);
		} else {
			player.setMana(calculatedMana);
		}
	}

	public void clearMana() {
		player.setMana(0);
	}

	public int getMana() {
		return player.getMana();
	}

	public Player getPlayer() {
		return this.player;
	}

	public void nextTurn() {
		turn++;
		setTurnMana();
	}

	public Deck getPlayerDeck() {
		return this.player.getDeck();
	}

	public Hand getPlayerHand() {
		return this.player.getHand();
	}

	public int getTurn() {
		return this.turn;
	}

	public boolean canPlayCard(Player player, CardWrapper card) {
		int manaCost = card.getManaCost();
		int currentMana = player.getMana();

		if (currentMana >= manaCost) {
			return true;
		} else {
			System.out.println("Insufficient mana to play this card.");
		}

		return false;
	}

	public static void deductMana(Player player, CardWrapper card) {
		int manaCost = card.getManaCost();
		int currentMana = player.getMana();

		if (currentMana >= manaCost) {
			player.setMana(currentMana - manaCost);
		} else {
			System.out.println("Insufficient mana to play this card.");
		}
	}



	public ArrayList<UnitWrapper> getUnits() {
		return player.getUnits();
	}

	public void setUnits(ArrayList<UnitWrapper> units) {
		this.player.setUnits(getUnits());
	}

	public ArrayList<SpellCard> getSpellCards() {
		ArrayList<SpellCard> spellCards = new ArrayList<>();

		for (CardWrapper cardWrapper : this.player.getHand().getHand()) {
			if (cardWrapper instanceof SpellCard) {
				SpellCard spellCard = (SpellCard) cardWrapper;
				spellCards.add(spellCard);
			}
		}

		return spellCards;
	}

	public ArrayList<UnitCard> getUnitCards() {
		ArrayList<UnitCard> unitCards = new ArrayList<>();

		for (CardWrapper cardWrapper : this.player.getHand().getHand()) {
			if (cardWrapper instanceof UnitCard) {
				UnitCard unitCard = (UnitCard) cardWrapper;
				unitCards.add(unitCard);
			}
		}

		return unitCards;
	}

	public SpellCard getLowestCostSpellCard() {
		ArrayList<SpellCard> spellCards = getSpellCards();
		if (spellCards.isEmpty()) {
			return null;
		}

		SpellCard lowestManaCost = spellCards.get(0);

		for (SpellCard spellCard : spellCards) {
			if (spellCard.getManaCost() < lowestManaCost.getManaCost()) {
				lowestManaCost = spellCard;
			}
		}

		return lowestManaCost;
	}

	public static void deductAndRenderMana(GameState gameState, ActorRef out, CardWrapper cardWrapper) {
		Player currentPlayer = gameState.getCurrentPlayer();
		
		if (currentPlayer == gameState.getAIPlayer()) {
			deductMana(gameState.getAIPlayer(), cardWrapper);
			BasicCommands.setPlayer2Mana(out, gameState.getCurrentPlayer());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} // these cause processing to wait for a number of milliseconds.
		} else if (currentPlayer == gameState.getHumanPlayer()) {
			deductManaFromBackEnd(gameState, cardWrapper);
			renderManaOnFrontEnd(out, gameState);
		}	
	}
	private static void deductManaFromBackEnd(GameState gameState, CardWrapper cardWrapper) {
		deductMana(gameState.getHumanPlayer(), cardWrapper);
	}

	private static void renderManaOnFrontEnd(ActorRef out, GameState gameState) {
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	


	public static boolean canPlayCard(GameState gameState, CardWrapper cardWrapper) {
		if (cardWrapper.getManaCost() <= gameState.getCurrentPlayer().getMana()) {
			return true;
		}

		return false;
	}

	public static void playCard(ActorRef out, GameState gameState, CardWrapper clickedCard, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		TileHighlightController.removeBoardHighlight(out, gameState);
		if (clickedCard instanceof UnitCard) {
			UnitCard unitCard = (UnitCard) clickedCard;
			// render front end of the unit
			Unit unit = UnitController.renderUnit(out, unitCard, tile);
			// create unit in the backend
			UnitController.createUnitWrapper(unit, (UnitCard) clickedCard, tileWrapper, gameState.getHumanPlayer());
		} else if (clickedCard instanceof SpellCard) {
			SpellCard spellCard = (SpellCard) clickedCard;
			spellCard.applySpellAbility(out, gameState, tileWrapper);
		}
		deductAndRenderMana(gameState, out, clickedCard);
		removeCard(out, gameState, clickedCard);
		
	}


}
