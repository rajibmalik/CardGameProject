package abilities;

import akka.actor.ActorRef;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Player;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class SundropElixir implements SpellAbility {
	/**
	 * The SundropElixir class represents a spell ability called Sundrop Elixir.
	 * This spell ability allows the player to cast a healing effect on a targeted unit.
	 * If the targeted unit is not the player's Avatar, it will be healed by 4 health points.
	 * If the unit's health plus 4 exceeds its maximum health, it will be set to its maximum health.
	 * Avatar units cannot be targeted by this spell ability.
	 * If the targeted tile does not have a unit, a message indicating no unit on the target tile will be printed.
	 * If the targeted unit is an Avatar, a message indicating inability to target Avatar units will be printed.
	 * 
	 * @author Eldhos Thomas
	 */
	
	public void castSpell(ActorRef out, GameState gameState, TileWrapper targetTile) {
		/**
	     * Casts the Sundrop Elixir spell ability on a targeted tile.
	     * If the targeted tile contains a unit, the unit will be healed.
	     * @param out         The reference to the actor to send messages to.
	     * @param gameState   The current game state.
	     * @param targetTile  The targeted tile where the spell is cast.
	     */
		if (targetTile.getHasUnit()) {
            UnitWrapper targetUnit = targetTile.getUnit();
            if (!(targetUnit instanceof Avatar)) {
                if (targetUnit.getHealth() + 4 >= targetUnit.getMaxHealth()) {
                    targetUnit.setHealth(targetUnit.getMaxHealth());
                } else {
                    targetUnit.increaseHealth(4);
                }
            } else {
                // Handle the case when the target unit is an Avatar
                System.out.println("Cannot target the Avatar with Sundrop Elixir spell.");
            }
        } else {
            // Handle the case when there is no unit on the tile
            System.out.println("No unit on the target tile.");
        }
    }
}