package abilities;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.UnitController;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.Unit;
import structures.basic.UnitWrapper;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;
import utils.TileLocator;

public class OpeningGambit implements UnitAbility{

    @Override
    public void applyAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        if(Unit.getName().equals("Gloom Chaser")) {
            applyGloomChaserAbility();
        } else if(Unit.getName().equals("Nightsorrow Assassin")) {
            applyNightSorrowAssassinAbility();
        } else if(Unit.getName().equals("Silverguard Squire")) {
            applySilverguardSquireAbility();
        }
    }

    public void applyGloomChaserAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        if(TileLocator.spaceBehind()) {     //check if the tilewrapper behind the unit is empty
            SummonWraithling.createWraithling(out, gameState.getHumanPlayer(), null);
        }
       
    }

    public void applyNightSorrowAssassinAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        if(TileLocator.enemyUnitisAdjcent()) {     //check if an enemy unit is in an adjacent tile
           UnitWrapper unitWrapper = TileLocator.getAdjacentEnemyBelowMaxHealth();       //gets an enemy unit that is in an adjacent tile and is below max health
           UnitController.destroyEnemy(out, gameState, unit); 
        }
    }

    public void applySilverguardSquireAbility(ActorRef out, GameState gameState, UnitWrapper unit) {
        if(TileLocator.aiPlayerLeftRightAdjacentToAvatar()) {   //checks whether the AI has an ajdacent allied unit either directly in front or behind the AI avatar
            UnitController.increaseAttack(unit, 1);
            UnitController.increaseHealth(unit, 1);
        }
    }
}