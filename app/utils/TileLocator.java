package utils;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Tile;
import structures.basic.TileWrapper;
import structures.basic.UnitWrapper;

public class TileLocator {

	

	public static List<TileWrapper> getAdjacentTiles(TileWrapper[][] board, UnitWrapper unit) {
	    List<TileWrapper> tilesWithinRange = new ArrayList<>();
	    TileWrapper unitPosition = unit.getTile();

	    for (int i = -1; i <= 1; i++) {
	        for (int j = -1; j <= 1; j++) {
	            int newX = unitPosition.getXpos() + i;
	            int newY = unitPosition.getYpos() + j;

	            // Check if the new coordinates are within the bounds of the board
	            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
	                TileWrapper tileWrapper = board[newX][newY];
	                tilesWithinRange.add(tileWrapper);
	            }
	        }
	    }
	    return tilesWithinRange;
		
	}
	


	
}


