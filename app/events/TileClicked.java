package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import abilities.HornOfTheForsaken;
import abilities.UnitAbility;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import controllers.TileHighlightController;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Card;
import structures.basic.CardWrapper;
import structures.basic.Hand;
import structures.basic.Player;
import structures.basic.SpellCard;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import structures.basic.UnitCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import abilities.UnitAbility;

/**
 * Indicates that the user has clicked an object on the game canvas, in this
 * case a tile. The event returns the x (horizontal) and y (vertical) indices of
 * the tile that was clicked. Tile indices start at 1.
 * 
 * { messageType = “tileClicked” tilex = <x index of the tile> tiley = <y index
 * of the tile> }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor {

	@Override

	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Player currentPlayer = gameState.getCurrentPlayer();
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		// Current tile clicked
		TileWrapper tileWrapper = gameState.getBoard().getBoard()[tilex][tiley];
		Tile tile = tileWrapper.getTile();

		if (currentPlayer == gameState.getHumanPlayer()) {

			// Will run if a card was clicked prior to tile click
			if (hasCardBeenClicked(gameState)) {
				handleCardClick(out, gameState, tileWrapper);
				// Will run if a tile containing a friendly unit was clicked
			} else if (tileWrapper.getHasUnit() == true && getTileHighlighted(tile) != 2) {
				handleUnitClick(out, gameState, currentPlayer, tileWrapper);
				// Will run if an empty white highlighted tile was clicked to move a unit
			} else if (getTileHighlighted(tile) == 1 && !tileWrapper.getHasUnit()) {
				handleTileClick(out, gameState, tileWrapper);
				// Will run if a tile containing an enemy unit in attack range was clicked
			} else if (getTileHighlighted(tile) == 2 && tileWrapper.getHasUnit()) {
				handleTileClickAttack(out, gameState, tileWrapper);
				// Resetting board highlight in all other instances
			} else {
				TileHighlightController.removeBoardHighlight(out, gameState);
				unclickAllUnits(gameState);
			}
		}
	}

	private void handleCardClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		CardWrapper clickedCard = getClickedCard(gameState);
		// If the tile is highlighted, and the card can be played, then play card
		if (getTileHighlighted(tile) == 1 || getTileHighlighted(tile) == 2 && canPlayCard(gameState, clickedCard)) {
			TileHighlightController.removeBoardHighlight(out, gameState);
			playCard(clickedCard, gameState, out, tileWrapper);
			deductAndRenderMana(gameState, out, clickedCard);
			removeCard(out, gameState, clickedCard);

			System.out.println("A card was played");
		} else {
			clickedCard.setHasBeenClicked(false);
			TileHighlightController.removeBoardHighlight(out, gameState);
			System.out.println("The card was unclicked");
		}

		unclickAllUnits(gameState);
	}

	private void handleUnitClick(ActorRef out, GameState gameState, Player currentPlayer, TileWrapper tileWrapper) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileHighlightController.removeBoardHighlight(out, gameState);
		UnitWrapper unitWrapper = tileWrapper.getUnit();
		//make sure not other units are clicked
		unclickAllUnits(gameState);
		// if friendly unit is clicked and it hasn't moved or attacked already,
		// highlight tiles for movement			
		if (unitWrapper != null && unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			System.out.println("Tiles highlighted for movement");
			TileHighlightController.setUnitMovementTileHighlight(out, gameState, unitWrapper);
			TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
			tileWrapper.getUnit().setHasBeenClicked(true);

			// testing avatar bug
			System.out.println("Here are the human players units:");
			for (UnitWrapper unit : gameState.getHumanPlayer().getUnits()) {
				System.out.println(unit.getName() + " " + unit.getHasBeenClicked());
			}

			System.out.println("Here are the ai players units:");
			for (UnitWrapper unit : gameState.getAIPlayer().getUnits()) {
				System.out.println(unit.getName() + " " + unit.getHasBeenClicked());
			}

			// If the unit has moved but not yet attacked, then highlight enemies in range
		} else if (unitWrapper != null && unitWrapper.getHasMoved() == true && unitWrapper.getHasAttacked() == false
				&& currentPlayer.getUnits().contains(unitWrapper)) {
			TileHighlightController.highlightEnemyInRange(out, unitWrapper, board);
			tileWrapper.getUnit().setHasBeenClicked(true);
		}
		
		//testing
		if(getClickedUnit(gameState)!=null) {
			System.out.println("The clicked unit is " + getClickedUnit(gameState).getName());
		}
		

	}

	private void handleTileClick(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		if (hasUnitBeenClicked(gameState)) {
			UnitWrapper unitWrapper = getClickedUnit(gameState);
			// If the unit has not moved or attacked, then move unit
			if (unitWrapper.getHasMoved() == false && unitWrapper.getHasAttacked() == false) {
				System.out.println("The unit" + unitWrapper.getName() + "has moved");
				UnitController.moveUnitBackend(unitWrapper, tileWrapper);
				UnitController.moveUnitFrontend(out, unitWrapper, tile);
				TileHighlightController.removeBoardHighlight(out, gameState);
				unclickAllUnits(gameState);

			}
		}
	}

	private void handleTileClickAttack(ActorRef out, GameState gameState, TileWrapper tileWrapper) {
		Player currentPlayer = gameState.getCurrentPlayer();
		UnitWrapper attackingUnitWrapper = getClickedUnit(gameState);
		Unit attackingUnit = attackingUnitWrapper.getUnit();

		UnitWrapper unitWrapperAttacked = tileWrapper.getUnit();
		Unit unitAttacked = unitWrapperAttacked.getUnit();

		// Attack unit
		UnitController.attackUnitBackend(out, gameState, attackingUnitWrapper, unitWrapperAttacked);
		UnitController.attackUnitFrontEnd(out, gameState, attackingUnit, unitAttacked, unitWrapperAttacked);

		// Attacked unit Dies
		if (unitWrapperAttacked.getHealth() <= 0) {
			UnitController.unitDeathBackend(out,gameState, currentPlayer, unitWrapperAttacked);
			UnitController.unitDeathFrontEnd(out, currentPlayer, unitAttacked);
		} else if (unitWrapperAttacked.getHealth() > 0) {
			// If attacked unit does not die, perform counter attack
			UnitController.attackUnitBackend(out, gameState, unitWrapperAttacked, attackingUnitWrapper);
			UnitController.attackUnitFrontEnd(out, gameState, unitAttacked, attackingUnit, attackingUnitWrapper);
			// Counter attack results in attacking unit death
			if (attackingUnitWrapper.getHealth() <= 0) {
				UnitController.unitDeathBackend(out,gameState, currentPlayer, attackingUnitWrapper);
				UnitController.unitDeathFrontEnd(out, currentPlayer, attackingUnit);

			}
		}

		unclickAllUnits(gameState);

		// Render idle movement
		BasicCommands.playUnitAnimation(out, attackingUnit, UnitAnimationType.idle);
		BasicCommands.playUnitAnimation(out, unitAttacked, UnitAnimationType.idle);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private boolean hasCardBeenClicked(GameState gameState) {
		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if (cardWrapper.hasBeenClicked() == true) {
				return true;
			}
		}

		return false;
	}

	private boolean hasUnitBeenClicked(GameState gameState) {
		for (UnitWrapper unitWrapper : gameState.getCurrentPlayer().getUnits()) {
			if (unitWrapper.getHasBeenClicked() == true) {
				return true;
			}
		}

		return false;
	}

	private CardWrapper getClickedCard(GameState gameState) {
		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if (cardWrapper.hasBeenClicked() == true) {
				return cardWrapper;
			}
		}
		return null;
	}

	private UnitWrapper getClickedUnit(GameState gameState) {
		for (UnitWrapper unit : gameState.getCurrentPlayer().getUnits()) {
			if (unit.getHasBeenClicked() == true) {
				return unit;
			}
		}
		return null;
	}
	
	private void unclickAllUnits(GameState gameState) {
		for (UnitWrapper unit : gameState.getCurrentPlayer().getUnits()) {
			unit.setHasBeenClicked(false);
		}
	}

	private int getTileHighlighted(Tile tile) {
		// Check if the clicked tile is highlighted
		int tileHighlightStatus = tile.getHighlightStatus();
		return tileHighlightStatus;
	}

	private boolean canPlayCard(GameState gameState, CardWrapper cardWrapper) {
		if (cardWrapper.getManaCost() <= gameState.getCurrentPlayer().getMana()) {
			return true;
		}

		return false;
	}

	public void playCard(CardWrapper cardWrapper, GameState gameState, ActorRef out, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();
		if (cardWrapper instanceof UnitCard) {
			UnitCard unitCard = (UnitCard) cardWrapper;
			// render front end of the unit
			Unit unit = renderUnit(out, unitCard, tile);
			// create unit in the backend
			createUnit(unit, unitCard, gameState, tileWrapper);
		} else if (cardWrapper instanceof SpellCard) {
			SpellCard spellCard = (SpellCard) cardWrapper;
			spellCard.applySpellAbility(out, gameState, tileWrapper);
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
		String config = unitCard.getCard().getUnitConfig();

		Unit unit = BasicObjectBuilders.loadUnit(config, UnitWrapper.nextId, Unit.class);
		unit.setPositionByTile(tile);

		BasicCommands.drawUnit(out, unit, tile);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		BasicCommands.setUnitAttack(out, unit, unitCard.getAttack());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		BasicCommands.setUnitHealth(out, unit, unitCard.getHealth());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return unit;
	}

	// creates backend representation of unit
	private void createUnit(Unit unit, CardWrapper cardWrapper, GameState gameState, TileWrapper tileWrapper) {
		Tile tile = tileWrapper.getTile();

		UnitCard unitCard = (UnitCard) cardWrapper;
		String name = unitCard.getName();
		int health = unitCard.getHealth();
		int attack = unitCard.getAttack();
		Player player = gameState.getCurrentPlayer();
		UnitAbility unitAbility = unitCard.getUnitAbility();

		UnitWrapper unitWrapper = new UnitWrapper(unit, name, health, attack, player, unitAbility, tileWrapper);
		tileWrapper.setUnitWrapper(unitWrapper);
		tileWrapper.setHasUnit(true);
		unitWrapper.setTile(tileWrapper);
		player.addUnit(unitWrapper);

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

	}

	private void clearRenderedHand(ActorRef out, GameState gameState) {
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

	private void renderHand(ActorRef out, GameState gameState) {
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
}
