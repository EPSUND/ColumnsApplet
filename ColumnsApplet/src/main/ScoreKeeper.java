package main;

import java.util.ArrayList;

public interface ScoreKeeper {

	//Clears bricks that generate score and return the score gained
	public int calcScore(Brick[][] brickGrid, ArrayList<Brick> fallingBricks);
	//Get the score for dropping a block to the floor
	public int calcDropToFloorScore();
	//Resets the score keeper and clears information stored about the scoring
	public void reset();
}
