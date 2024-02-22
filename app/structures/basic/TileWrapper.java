package structures.basic;

public class TileWrapper {
	
	private Tile tile; 
	private UnitWrapper unit;
	private boolean hasUnit; 
	private int xpos; 
	private int ypos;
	
	public TileWrapper(Tile tile, int xpos, int ypos) {
		this.tile=tile;
		this.xpos = xpos;
		this.ypos = ypos;
		this.hasUnit = false;
	}

	public boolean getHasUnit() {
		return hasUnit;
	}

	public void setHasUnit(boolean hasUnit) {
		this.hasUnit = hasUnit;
	}

	public int getXpos() {
		return xpos;
	}

	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	public int getYpos() {
		return ypos;
	}

	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public UnitWrapper getUnit() {
		return unit;
	}

	public void setUnitWrapper(UnitWrapper unit) {
		this.unit = unit;
	}
	
	

	
	
	
	

}
