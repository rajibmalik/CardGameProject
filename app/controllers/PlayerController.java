package controllers;

import java.util.ArrayList;
import java.util.List;

import abilities.Deathwatch;
import abilities.OpeningGambit;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
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

/**
 * This class is responsible for handling changes to the player in the game. It
 * acts as a controller class to manage player related operations without
 * directly updating the player. Operations include drawing cards, playing
 * cards, managing mana.
 * 
 * @author Darby Christy, Rajib Malik
 */

public class PlayerController {
	Player player;
	private int turn;
	private static final int MAX_MANA = 9; // Maximum mana value

	public PlayerController(Player player) {
		this.player = player;
		this.turn = 1;
	}

	public Player getPlayer() {
		return this.player;
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

	public ArrayList<UnitWrapper> getUnits() {
		return player.getUnits();
	}

	public void setUnits(ArrayList<UnitWrapper> units) {
		this.player.setUnits(getUnits());
	}

	public int getMana() {
		return player.getMana();
	}

	public void clearMana() {
		player.setMana(0);
	}

	public void setTurnMana() {
		int calculatedMana = 1 + turn;
		if (calculatedMana > MAX_MANA) {
			player.setMana(MAX_MANA);
		} else {
			player.setMana(calculatedMana);
		}
	}

	public void nextTurn() {
		turn++;
		setTurnMana();
	}

	/**
	 * Draws an initial hand with three cards for the player at the beginning of the
	 * game.
	 */
	public void drawInitialHand() {
		for (int i = 0; i < 3; i++) {
			CardWrapper cardWrapper = player.getDeck().getTopCard();
			player.getHand().addCard(cardWrapper);
		}
	}

	/**
	 * Renders the initial hand of the player on the front end.
	 * @param out
	 * @param gameState
	 */
	public static void renderInitialHand(ActorRef out, GameState gameState) {
		Hand hand = gameState.getPlayerHand();
		int handPosition = 1;
		for (CardWrapper cardWrapper : hand.getHand()) {
			BasicCommands.drawCard(out, cardWrapper.getCard(), handPosition, 1);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			handPosition++;

			if (handPosition == 4) {
				break;
			}
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
		playerController.removeCardFromHand(cardWrapper);
	}

	public void removeCardFromHand(CardWrapper cardWrapper) {
		int id = cardWrapper.getId();
		ArrayList<CardWrapper> hand = player.getHand().getHand();
		// Loop through the hand and remove the cardWrapper if its id is equal to the
		// specified id
		for (CardWrapper card : hand) {
			if (card.getId() == id) {
				hand.remove(cardWrapper);
				break;
			}
		}
	}

	/**
	 * Front end rendering of players current hand
	 * @param out
	 * @param gameState
	 */
	private static void renderHand(ActorRef out, GameState gameState) {
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

	/**
	 * Checks if the player can play a card based on available mana.
	 * @param player
	 * @param card
	 * @return True if the player has sufficient mana, otherwise false
	 */
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

	/**
	 * Helper method to get all the spell cards in the players hand
	 */
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

	/**
	 * Helper method to get all the unit cards in the players hand
	 */
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

	/**
	 * Sets the human players initial mana to 2 at the start of the game.
	 * @param out
	 * @parm playerController
	 */
	public static void setInitialMana(ActorRef out, PlayerController playerController) {
		int turn = playerController.getTurn();
		int mana = turn + 1;

		Player player = playerController.getPlayer();
		player.setMana(mana);

		BasicCommands.setPlayer1Mana(out, playerController.getPlayer());
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

	/**
	 * Helper method to find all the units on the board with Opening Gambit and apply their abilities.
	 */
	public static void applyOpeningGambit(ActorRef out, GameState gameState) {
		List<UnitWrapper> humanUnits = new ArrayList<>(gameState.getHumanPlayer().getUnits());
		for (UnitWrapper gambitUnit : humanUnits) {
			if (gambitUnit.getAbility() instanceof OpeningGambit) {
				gambitUnit.useAbility(out, gameState, gambitUnit);
			}
		}
		List<UnitWrapper> aiUnits = new ArrayList<>(gameState.getAIPlayer().getUnits());
		for (UnitWrapper gambitUnit : aiUnits) {
			if (gambitUnit.getAbility() instanceof OpeningGambit) {
				gambitUnit.useAbility(out, gameState, gambitUnit);
			}
		}
	}

	/**
	 * Helper method to find all the units on the board with Deathwatch and apply their abilities.
	 */
	public static void applyDeathWatch(ActorRef out, GameState gameState) {
		List<UnitWrapper> humanUnits = new ArrayList<>(gameState.getHumanPlayer().getUnits());
		for (UnitWrapper unit : humanUnits) {
			if (unit.getAbility() instanceof Deathwatch) {
				unit.useAbility(out, gameState, unit);
			}
		}
	}
	
	/**
	 * Plays the selected card on the specified tile, applying relevant actions such as rendering
	 * the unit on the front end, creating the unit in the backend, applying spell/unit abilities,
	 * deducting and rendering mana, and removing the card from the player's hand.
	 *
	 * @param out           ActorRef for communication with the front end
	 * @param gameState     Current state of the game
	 * @param clickedCard   The card to be played
	 * @param tileWrapper   The tile on which the card will be played
	 */
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
		applyOpeningGambit(out, gameState);
		deductAndRenderMana(gameState, out, clickedCard);
		removeCard(out, gameState, clickedCard);
	}
	
	/**
	 * Handles the action when a tile is clicked to play a card. If the tile is highlighted,
	 * and the card can be played, then it triggers the play of the card. After the card has been played, the card will be 
	 * marked as unclicked and tile highlights will be removed accordingly. 
	 * @param out          ActorRef for communication with the front end
	 * @param gameState    Current state of the game
	 * @param tileWrapper  The tile on which the card will be played
	 */
	public static void handleCardClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		CardWrapper clickedCard = gameState.getClickedCard(gameState);
		// If the tile is highlighted, and the card can be played, then play card
		if (TileHighlightController.getTileHighlighted(tile) == 1 || TileHighlightController.getTileHighlighted(tile) == 2 
				&& canPlayCard(gameState, clickedCard)) {
			if (clickedCard.getName().equals("Wraithling Swarm")) {
				SpellCard spellCard = (SpellCard) clickedCard;
				spellCard.applySpellAbility(out, gameState, tileWrapper);
		        } else {
		            // Play other cards
		       playCard(out,gameState,clickedCard,tileWrapper);
		        }
			System.out.println("A card was played");
		} else {
			clickedCard.setHasBeenClicked(false);
			TileHighlightController.removeBoardHighlight(out, gameState);
			System.out.println("The card was unclicked");
		}
		gameState.unclickAllUnits(gameState);
	}

	public static Player setPlayerAvatar(GameState gameState, Unit unit) {
		Player humanPlayer = gameState.getHumanPlayer();

		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileWrapper tileWrapper = board[1][2];
		Avatar avatar = new Avatar(unit, "Human Avatar", 20, 2, humanPlayer, null, tileWrapper);

		tileWrapper.setUnitWrapper(avatar);
		tileWrapper.setHasUnit(true);
		avatar.setTile(tileWrapper);
		humanPlayer.addUnit(avatar);

		return humanPlayer;
	}

}
