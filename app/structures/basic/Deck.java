package structures.basic;
import java.util.ArrayList;
import java.util.List;

import abilities.BeamShock;
import abilities.DarkTerminus;
import abilities.Deathwatch;
import abilities.HornOfTheForsaken;
import abilities.OpeningGambit;
import abilities.SpellAbility;
import abilities.SundropElixir;
import abilities.TrueStrike;
import abilities.UnitAbility;
import abilities.WraithlingSwarm;
import abilities.Zeal;
import utils.OrderedCardLoader;

/**
 * This class represents the players deck which is made up of CardWrappers which can be either a UnitCard 
 * or a SpellCard
 * @author Rajib Malik
*/

public class Deck {
	private ArrayList<CardWrapper> deck;
	private int topCardIndex;

	public Deck(String deckNumber) {
		this.deck = new ArrayList<>();
		createDeck(deckNumber);
	}

	/**
     * This method is responsible for creating the deck. If "1" is passed , then the player 1 deck is created, 
	 * else if 2, the player 2 deck is created
	 * @param deckNumber represents the deck that is created 
    */
	public void createDeck(String deckNumber) {
		List<Card> cards = null;

		if (deckNumber == "1") {
			cards = OrderedCardLoader.getPlayer1Cards(2);

		} else {
			cards = OrderedCardLoader.getPlayer2Cards(2);
		}

		for (Card crd : cards) {
			if (crd.getIsCreature()) {
				createCardWrapper(crd, true);
			} else {
				createCardWrapper(crd, false);
			}
		}
	}

	/**
     * This method is responsible for creating CardWrappers from a Card, 
	 * if it is a creature, a UnitCard is created, else a SpellCard is created
	 * @card the card created from the OrderedCardLoader
	 * @isCreature is the card is a creature card
    */
	private void createCardWrapper(Card card, boolean isCreature) {
		if (isCreature) {
			int attack = card.bigCard.getAttack();
			int health = card.bigCard.getHealth();
			int manaCost = card.getManacost();
			String name = card.getCardname();
			UnitCard unitCard = new UnitCard(manaCost, name, card, attack, health, createUnitAbility(card));

			this.deck.add(unitCard);
		} else {
			int manaCost = card.getManacost();
			String name = card.getCardname();
			SpellCard spellCard = new SpellCard(name, manaCost, createSpellAbility(card), card);

			this.deck.add(spellCard);
		}
	}

	/**
     * This helper method is responsible for creating a UnitAbility from the card
	 * @card a reference to a card
    */
	private UnitAbility createUnitAbility(Card card) {
		String[] rulesTextRows = card.bigCard.rulesTextRows;
		UnitAbility ability = null;
		if (rulesTextRows != null) {
			ability = getUnitAbility(card.getCardname());
		}
		return ability;
	}

	/**
     * This method is responsible for returning the correct UnitAbility depending on the Cards name
	 * @name the name of the card
	 * @return the appropriate UnitAbility depending on the card name
    */
	private UnitAbility getUnitAbility(String name) {
		UnitAbility ability = null;
		if (name.equals("Bad Omen")) {
			return ability = new Deathwatch(1, 0);
		} else if (name.equals("Shadow Watcher")) {
			return ability = new Deathwatch(1, 1);
		} else if (name.equals("Bloodmoon Priestess")) {
			return ability = new Deathwatch(0, 0);
		} else if (name.equals("Shadowdancer")) {
			return ability = new Deathwatch(0, 1);
		} else if (name.equals("Silverguard Knight")) {
			return ability = new Zeal();
		} else if (name.equals("Gloom Chaser")) {
			return ability = new OpeningGambit();
		}else if (name.equals("Nightsorrow Assassin")) {
			return ability = new OpeningGambit();
		}else if (name.equals("Silverguard Squire")) {
			return ability = new OpeningGambit();
		}
		return ability;

	}

	/**
     * This helper method is responsible for creating a SpellAbility from a card
	 * @card a reference to a card
	 * @return an appropriate SpellAbility dependning on the Card name
    */
	private SpellAbility createSpellAbility(Card card) {
		String[] rulesTextRows = card.bigCard.rulesTextRows;
		SpellAbility ability = null;
		if (rulesTextRows != null) {
			ability = getSpellAbility(card.getCardname());
		}
		return ability;
	}

	/**
     * This method gets the correct SpellAbility depending on the SpellCard name
	 * @card a SpellAbility depending on the card name
    */
	private SpellAbility getSpellAbility(String name) {
		SpellAbility ability = null;
		if (name.equals("Dark Terminus")) {
			return ability = new DarkTerminus();
		} else if (name.equals("Wraithling Swarm")) {
			return ability = new  WraithlingSwarm();
		} else if (name.equals("Horn of the Forsaken")) {
			return ability = new  HornOfTheForsaken();
		} else if (name.equals("Sundrop Elixir")) {
			return ability = new SundropElixir();
		} else if (name.equals("Truestrike")) {
			return ability = new TrueStrike();
		} else if (name.equals("Beamshock")) {
			return ability = new BeamShock();
		}
			
		return null;
	}
	/**
     * This method returns the top card in the deck
	 * @return a CardWrapper representing the top card in the deck
    */
	public CardWrapper getTopCard() {
		CardWrapper cardWrapper = null;

		if (topCardIndex >= 0 && topCardIndex < 20) {
			cardWrapper = this.deck.get(this.topCardIndex);
			this.topCardIndex++;
		}

		return cardWrapper;
	}

	public ArrayList<CardWrapper> getDeck() {
		return this.deck;
	}

	public int getTopCardIndex() {
		return this.topCardIndex;
	}
}