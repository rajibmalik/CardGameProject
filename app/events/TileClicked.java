package events;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;

import abilities.UnitAbility;
import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.PlayerController;
import controllers.TileHighlightController;
import structures.GameState;
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
	private TileWrapper tileWrapper;
	private Tile tile;
	private TileWrapper lastTileClicked;

	@Override

	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		Player currentPlayer = gameState.getCurrentPlayer();
		if (currentPlayer == gameState.getHumanPlayer()) {
			setTileClicked(gameState, message);
			
			//Will run if a card was clicked prior to tile click
			if (hasCardBeenClicked(gameState)) {
				handleCardClick(out, gameState);
			//Will run if a tile containing a unit was clicked
			} else if (tileWrapper.getHasUnit() && getTileHighlighted(this.tile)!=2) {
				handleUnitClick(out, gameState,currentPlayer);
			//Will run if an empty highlighted tile was clicked to move a unit
			} else if (getTileHighlighted(this.tile)==1 && !tileWrapper.getHasUnit()) {
				handleTileClick(out,gameState);
			//Will run if a tile containing an enemy unit in attack range was clicked
			} else if (getTileHighlighted(this.tile)==2 && tileWrapper.getHasUnit()) {
				handleTileClickAttack(out,gameState);
			//Resetting board highlight in all other instances
			} else {
				TileHighlightController.removeBoardHighlight(out, gameState);
			}
		}
	}

	private void handleCardClick(ActorRef out, GameState gameState) {
		CardWrapper clickedCard = getClickedCard(gameState);
		if (getTileHighlighted(this.tile)==1 && canPlayCard(gameState, clickedCard)) {
			System.out.println("A card was played");
			playCard(clickedCard, gameState, out);
			TileHighlightController.removeBoardHighlight(out, gameState);
			deductAndRenderMana(gameState, out, clickedCard);
			removeCard(out, gameState, clickedCard);
		} else {
			System.out.println("The card was unclicked");
			clickedCard.setHasBeenClicked(false);
			TileHighlightController.removeBoardHighlight(out, gameState);
		}
	}

	private void handleUnitClick(ActorRef out, GameState gameState,Player currentPlayer) {
		TileWrapper[][] board = gameState.getBoard().getBoard();
		TileHighlightController.removeBoardHighlight(out, gameState);
		UnitWrapper unit = tileWrapper.getUnit();
		lastTileClicked = tileWrapper;
		//if friendly unit is clicked and it hasn't moved or attacked already, highlight tiles for movement
		if (unit != null && unit.getHasMoved() == false && unit.getHasAttacked() == false && currentPlayer.getUnits().contains(unit)) {
			System.out.println("Tiles highlighted for movement");
			TileHighlightController.setUnitMovementTileHighlight(out, gameState, unit);
			TileHighlightController.highlightEnemyInRange(out,unit,board);
		} else if (unit != null && unit.getHasMoved() == true && unit.getHasAttacked() == false ) {
			TileHighlightController.highlightEnemyInRange(out,unit,board);
		}
		
	}

	private void handleTileClick(ActorRef out,GameState gameState) {
		if(lastTileClicked.getUnit().getHasMoved()==false && lastTileClicked.getUnit().getHasAttacked() == false) {
			System.out.println("The unit should move");
			System.out.println(lastTileClicked.getUnit().getName());
			moveUnitBackend(lastTileClicked);
			moveUnitFrontend(out);
			TileHighlightController.removeBoardHighlight(out, gameState);
			lastTileClicked.getUnit().setHasMoved(true);
			lastTileClicked.setHasUnit(false);
			lastTileClicked.setUnitWrapper(null);
			lastTileClicked = null;	
		}
		
	}
	
	private void handleTileClickAttack(ActorRef out,GameState gameState) {
		Player currentPlayer =gameState.getCurrentPlayer();
		UnitWrapper attackingUnit = lastTileClicked.getUnit();
		Unit unit = attackingUnit.getUnit();
		
		UnitWrapper unitUnderAttack = tileWrapper.getUnit();
		Unit unitAttacked = unitUnderAttack.getUnit();
		
		if (attackingUnit.getHasAttacked()==false) {
			System.out.println("Attack the unit");
			attackingUnit.setHasAttacked(true);
			System.out.println("Before Decrease - Unit Health: " + unitUnderAttack.getHealth());
			unitUnderAttack.decreaseHealth(attackingUnit.getAttack());
			System.out.println("After Decrease - Unit Health: " + unitUnderAttack.getHealth());

			TileHighlightController.removeBoardHighlight(out, gameState);
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.attack);
			
			try { Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			
			BasicCommands.setUnitHealth(out, unitAttacked, unitUnderAttack.getHealth());
			
			try { Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			
			if(unitUnderAttack.getHealth()<=0) {
				gameState.unitDealth(currentPlayer, unitUnderAttack);
				BasicCommands.playUnitAnimation(out, unitAttacked, UnitAnimationType.death);
				BasicCommands.deleteUnit(out, unitAttacked);
				try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			} else {
				//counter attack
				BasicCommands.playUnitAnimation(out, unitAttacked, UnitAnimationType.attack);
				try { Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				attackingUnit.decreaseHealth(unitUnderAttack.getAttack());
				BasicCommands.setUnitHealth(out, unit, attackingUnit.getHealth());
				try { Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
				

				if(attackingUnit.getHealth()<=0) {
					gameState.unitDealth(currentPlayer, attackingUnit);
					BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.death);
					BasicCommands.deleteUnit(out, unit);
					try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			
			BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.idle);
			BasicCommands.playUnitAnimation(out, unitAttacked, UnitAnimationType.idle);
			try { Thread.sleep(200);} catch (InterruptedException e) {e.printStackTrace();}
			
			
	

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

	private CardWrapper getClickedCard(GameState gameState) {
		for (CardWrapper cardWrapper : gameState.getPlayerHand().getHand()) {
			if (cardWrapper.hasBeenClicked() == true) {
				return cardWrapper;
			}
		}
		return null;
	}

	private void moveUnitBackend(TileWrapper lastTileClicked) {
		UnitWrapper unitWrapper = lastTileClicked.getUnit();
		if (unitWrapper != null && unitWrapper.getHasMoved() == false) {
			this.tileWrapper.setHasUnit(true); // set tile to have unit on it
			this.tileWrapper.setUnitWrapper(unitWrapper);
			unitWrapper.setTile(tileWrapper); // Set unit to the new tile
			unitWrapper.setHasMoved(true);
		}
	}

	private void moveUnitFrontend(ActorRef out) {
		UnitWrapper unitWrapper = lastTileClicked.getUnit();
		Unit unit = unitWrapper.getUnit();
		BasicCommands.moveUnitToTile(out, unit, tile);
		BasicCommands.playUnitAnimation(out, unit, UnitAnimationType.move);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setTileClicked(GameState gameState, JsonNode message) {
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		this.tileWrapper = gameState.getBoard().getBoard()[tilex][tiley];
		this.tile = gameState.getBoard().getBoard()[tilex][tiley].getTile();
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

	public void playCard(CardWrapper cardWrapper, GameState gameState, ActorRef out) {
		if (cardWrapper instanceof UnitCard) {
			UnitCard unitCard = (UnitCard) cardWrapper;
			// render front end of the unit
			Unit unit = renderUnit(out, unitCard, this.tileWrapper.getTile());
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
	private void createUnit(Unit unit, CardWrapper cardWrapper, GameState gameState) {

		UnitCard unitCard = (UnitCard) cardWrapper;
		String name = unitCard.getName();
		int health = unitCard.getHealth();
		int attack = unitCard.getAttack();
		Player player = gameState.getCurrentPlayer();
		UnitAbility unitAbility = unitCard.getUnitAbility();

		UnitWrapper unitWrapper = new UnitWrapper(unit, name, health, attack, player, unitAbility, tileWrapper);
		this.tileWrapper.setUnitWrapper(unitWrapper);
		this.tileWrapper.setHasUnit(true);
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
			}
			handPosition++;

			if (handPosition > hand.getHand().size() + 1) {
				break;
			}
		}
	}
}

