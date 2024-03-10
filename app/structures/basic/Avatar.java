package structures.basic;

import abilities.UnitAbility;

/**
 * Represents a player's avatar, which extends from UnitWrapper.
 * The Avatar's health represents the players health. 
 * Additionally, an avatar can have robustness which relates to the 
 * SpellCard Horn of the Forsaken
 * 
 * @author Rajib Malik
 * @author Ashling Curran
*/

public class Avatar extends UnitWrapper {
	private int robustness;
	private boolean artifactActive;

	public Avatar(Unit unit, String name, int health, int attack, Player player, UnitAbility ability,
			TileWrapper tile) {
		super(unit, name, 20, 2, player, ability, tile); // Player avatar is initialised with 2 health and attack
		this.robustness = 0;
		this.artifactActive = false;
	}

	
	public boolean isArtifactActive() {
		return artifactActive;
	}

	public void setArtifactActive(boolean artifactActive) {
		this.artifactActive = artifactActive;
	}

	public int getRobustness() {
		return this.robustness;
	}

	public void setRobustness(int robustness) {
		this.robustness = robustness;
	}

	/**
     * Decreases the Avatar's robustness. If the robustness reaches 0,
 	 * the artifact is set to inactive.
    */
	public void decreaseRobustness() {
		this.robustness--;

		if (this.robustness <= 0) {
			setArtifactActive(false);
		}
	}

}