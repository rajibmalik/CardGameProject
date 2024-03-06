package controllers;

import java.util.ArrayList;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.SpellCard;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class SpellController {

    public static void playTrueStrike(ActorRef out, GameState gameState, SpellCard spellCard) {
        ArrayList<UnitWrapper> humanUnits = gameState.getHumanPlayer().getUnits();
        UnitWrapper targetUnit = null;

        for (UnitWrapper unitWrapper:humanUnits) {
            if (unitWrapper.getName() != "Human Avatar") {
                System.out.println("playing Truestrike");
                
                targetUnit = unitWrapper;
            }
        }

        if (targetUnit != null) {
            // BasicCommands.drawTile(out, targetUnit.getTile().getTile(), 2);
            // try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}

            // test 
            EffectAnimation summoning= BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
		    BasicCommands.playEffectAnimation(out, summoning, targetUnit.getTile().getTile());

            spellCard.applySpellAbility(out, gameState, targetUnit.getTile());
    
            if (targetUnit.getHealth() <= 0) {
                BasicCommands.deleteUnit(out, targetUnit.getUnit());
                try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
            } else {
                BasicCommands.setUnitHealth(out, targetUnit.getUnit(), targetUnit.getHealth());
                try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
            }
    
            BasicCommands.drawTile(out, targetUnit.getTile().getTile(), 0);
            try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
        }  
    }

    public static void playSundropElixir(ActorRef out, GameState gameState, SpellCard spellCard) {
        ArrayList<UnitWrapper> units = gameState.getAIPlayer().getUnits();
        int difference = 0; 
        UnitWrapper lowestHealthUnit = null;

        for (UnitWrapper unitWrapper:units) {
            if (unitWrapper.getMaxHealth() - unitWrapper.getHealth() > difference) {
                difference = unitWrapper.getMaxHealth() - unitWrapper.getHealth();
                lowestHealthUnit = unitWrapper;
            }
        }

        if (lowestHealthUnit != null) {
            EffectAnimation summoning= BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
		    BasicCommands.playEffectAnimation(out, summoning, lowestHealthUnit.getTile().getTile());
        
            spellCard.applySpellAbility(out, gameState, lowestHealthUnit.getTile());
            BasicCommands.setUnitHealth(out, lowestHealthUnit.getUnit(), lowestHealthUnit.getHealth());
            try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
            
            BasicCommands.drawTile(out, lowestHealthUnit.getTile().getTile(), 0);
            try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
        }
    }

    public static void playBeamShock(ActorRef out, GameState gameState, SpellCard spellCard) {
        ArrayList<UnitWrapper> humanUnits = gameState.getHumanPlayer().getUnits();
        UnitWrapper targetUnit = null;
 
        for (UnitWrapper unitWrapper:humanUnits) {
             if (!unitWrapper.getName().equals("Human Avatar")) {
                 targetUnit = unitWrapper;
             }
        }

        if (targetUnit != null) {
            BasicCommands.drawTile(out, targetUnit.getTile().getTile(), 2);
            try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("TILE WRAPPER: " + targetUnit.getTile());
            System.out.println("SPELL CARD: " + spellCard);
            spellCard.applySpellAbility(out, gameState, targetUnit.getTile());
            BasicCommands.drawTile(out, targetUnit.getTile().getTile(), 0);
            try {Thread.sleep(300);} catch (InterruptedException e) {e.printStackTrace();}
        }
     }

     public static boolean canPlaySundropElixir(GameState gameState) {
        System.out.println("Checking if can play SundropElixir");
        ArrayList<UnitWrapper> units = gameState.getAIPlayer().getUnits();
        for (UnitWrapper unitWrapper:units) {
            if(unitWrapper.getMaxHealth() > unitWrapper.getHealth() && unitWrapper.getHealth() > 0) {
                return true;
            }
        }

        return false;
    }

    public static boolean canPlayAttackingSpell(GameState gameState) {
        ArrayList<UnitWrapper> humanUnits = gameState.getHumanPlayer().getUnits();
        
        for(UnitWrapper unitWrapper: humanUnits) {
            if (!unitWrapper.getName().equals("Human Avatar") && unitWrapper.getHealth() > 0 && unitWrapper.getTile() != null) {
                System.out.println("CAN TARGET THIS WITH ATTACKING SPELL: " + unitWrapper.getName()); // test 
                return true;
            }
        }

        return false;
    }

     
    
}
