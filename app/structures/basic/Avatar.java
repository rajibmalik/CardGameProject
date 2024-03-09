package structures.basic;

import abilities.UnitAbility;

// This class represents the player avatar and extends the UnitWrapper class

public class Avatar extends UnitWrapper {
	private int robustness;
	private boolean artifactActive;

	public Avatar(Unit unit, String name, int health, int attack, Player player, UnitAbility ability,
			TileWrapper tile) {
		super(unit, name, 20, 2, player, ability, tile); // Player avatar is initialised with 20 health and 2 attack
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

	public void decreaseRobustness() {
		this.robustness--;

		if (this.robustness <= 0) {
			setArtifactActive(false);
		}
	}
}