package structures.basic;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

import abilities.Deathwatch;
import abilities.SpellAbility;
import abilities.UnitAbility;

// This class represents a deck object which is an arraylist, containing cardWrapper objects
// which are generated from the card configuration files.

public class Deck{
    private ArrayList<CardWrapper> deck;
    int topCardIndex;

    // "1" creates player deck 
    // "2" creates AI deck
    public Deck(String deckNumber) {
        this.deck = new ArrayList<>();
        createDeck(createFilePattern(deckNumber));
    }

    public void createDeck(String deckNumber) {
        // Specifies the directory containing JSON files of the cards
        String cardConfigPath = "conf\\gameconfs\\cards";

        // Creates an ObjectMapper to parse JSON files
        ObjectMapper objectMapper = new ObjectMapper();

        // Creates a path object, representsing the cardConfig Directory
        Path directory = Paths.get(cardConfigPath);

        // Checks if the directory exists and is a directory
        if (Files.exists(directory) && Files.isDirectory(directory)) {
            // Iterates through the card configuration files for player or AI
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, deckNumber)) {
                for (Path file:stream) {
                    // reads and parses card JSON file into a Card object
                    Card card = objectMapper.readValue(file.toFile(), Card.class);
                    if (card.getIsCreature()) {
                        // Instantiates a UnitCard object using Card object, then added to deck using createUnitFromCard()
                         createCardWrapper(card, true);
                    } else {
                        // Instantiates a SpellCard object using Card object, then added to deck using createSpellFromCard()
                        createCardWrapper(card, false);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error while reading or parsing JSON card configuration files" + e.getMessage());
            }

            // duplicates the deck, maintaining the correct order of cards
            this.deck.addAll(this.deck);

        } else {
            System.err.println("Directory does not exist or is not a directory " + cardConfigPath);
        }
    }

    // file pattern to find player ("1") or AI ("2") cards
    private String createFilePattern(String number) {
        return number + "*.json";
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
            SpellCard spellCard = new SpellCard(name, manaCost, createSpellAbility(card),card);
    
            this.deck.add(spellCard);
        }
    }

    // This method has to check the rulesTextRows and depending on the ability information 
    // generate the correct UnitAbility to return
    private UnitAbility createUnitAbility(Card card) { 
        String[] rulesTextRows = card.bigCard.rulesTextRows;
        UnitAbility ability = null;
        if (rulesTextRows != null) {
            ability = new Deathwatch(0,0) {
            };
        }
        return ability;
    }

    // This method has to check the rulesTextRows and depending on the ability information 
    // generate the correct SpellAbility to return
    private SpellAbility createSpellAbility(Card card) {
        String[] rulesTextRows = card.bigCard.rulesTextRows;
        SpellAbility ability = null;
        if (rulesTextRows != null) {
          
        }
        return ability;
    }

    public CardWrapper getTopCard() {
        CardWrapper cardWrapper = null;

        if (topCardIndex >= 0 && topCardIndex < 20) {
            cardWrapper = this.deck.get(this.topCardIndex);
            this.topCardIndex ++;
        }

        return cardWrapper;
    }

    public ArrayList<CardWrapper> getDeck() {
        return this.deck;
    }
}