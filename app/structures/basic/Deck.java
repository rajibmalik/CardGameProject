package structures.basic;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import abilities.BeamShock;
import abilities.DarkTerminus;
import abilities.Deathwatch;
import abilities.HornOfTheForsaken;
import abilities.SpellAbility;
import abilities.SummonWraithling;
import abilities.SundropElixir;
import abilities.TrueStrike;
import abilities.UnitAbility;
import abilities.WraithlingSwarm;
import commands.BasicCommands;
import utils.OrderedCardLoader;

// This class represents a deck object which is an arraylist, containing cardWrapper objects
// which are generated from the card configuration files.

public class Deck {
	private ArrayList<CardWrapper> deck;
	private int topCardIndex;

	// "1" creates player deck
	// "2" creates AI deck
	public Deck(String deckNumber) {
		this.deck = new ArrayList<>();
		createDeck(deckNumber);
	}

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

	// This method has to check the rulesTextRows and depending on the ability
	// information
	// generate the correct UnitAbility to return
	private UnitAbility createUnitAbility(Card card) {
		String[] rulesTextRows = card.bigCard.rulesTextRows;
		UnitAbility ability = null;
		if (rulesTextRows != null) {
			ability = getUnitAbility(card.getCardname());
		}
		return ability;
	}
	
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
		}
		return ability;

	}

	// This method has to check the rulesTextRows and depending on the ability
	// information
	// generate the correct SpellAbility to return
	private SpellAbility createSpellAbility(Card card) {
		String[] rulesTextRows = card.bigCard.rulesTextRows;
		SpellAbility ability = null;
		if (rulesTextRows != null) {
			ability = getSpellAbility(card.getCardname());
		}
		return ability;
	}

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