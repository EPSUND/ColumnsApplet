package main;
import java.util.Date;

import usrinput.KeyControl;
import gui.ColumnsCanvas;


public class Player {

	//Block movement constants
	private static final int MOVE_DELAY = 3;
	private static final int DROP_TO_FLOOR_DELAY = 4;
	public static final int NO_MOVE = 0;
	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int MOVE_DOWN = 3;
	public static final int ROTATE = 4;
	
	private int score, numBricks, dropFlag, moveFlag, dropToFloorFlag;
	private Level activeLevel;
	private KeyControl keyController;
	private Date startTime;
	
	public Player(ColumnsCanvas canvas)
	{	
		dropFlag = 0;
		moveFlag = 0;
		dropToFloorFlag = 0;
		score = 0;
		numBricks = 0;
		startTime = new Date();
		//The active level is set later
		activeLevel = null;
		
		keyController = new KeyControl();
	}
	
	public void resetFlags()
	{
		dropFlag = 0;
		moveFlag = 0;
		dropToFloorFlag = 0;
	}
	
	public void restartGame()
	{
		resetFlags();
		score = 0;
		numBricks = 0;
		startTime = new Date();
		activeLevel = null;
	}
	
	public void addScore(int newScore)
	{
		score += newScore;
	}
	
	public int getScore() 
	{
		return score;
	}
	
	public int getLevelNum()
	{
		return activeLevel.getLevelNum();
	}
	
	public boolean shouldDropToFloor()
	{
		if(!keyController.getDropToFloor() && dropToFloorFlag == 0)
		{
			return false;
		}
		
		if(dropToFloorFlag >= DROP_TO_FLOOR_DELAY)
		{
			dropToFloorFlag = 0;
			keyController.resetDropToFloor();
			return true;
		}
		else
		{
			dropToFloorFlag++;
			return false;
		}
	}
	
	public boolean shouldMove()
	{
		//Should not move if we are trying to move both left and right at the same time or if neither the left or right buttons are pressed
		if(!keyController.getRotate() &&
		   !keyController.getMoveDown() &&
		   ((keyController.getMoveLeft() && keyController.getMoveRight()) ||
		   (!keyController.getMoveLeft() && !keyController.getMoveRight()))
		   )
		{
			moveFlag = 0;
			return false;
		}
		
		if(moveFlag >= MOVE_DELAY)
		{
			moveFlag = 0;
			return true;
		}
		else
		{
			moveFlag++;
			return false;
		}
	}
	
	public int getMoveDir()
	{
		if(keyController.getMoveLeft() && !keyController.getMoveRight())
		{
			return MOVE_LEFT;
		}
		else if(!keyController.getMoveLeft() && keyController.getMoveRight())
		{
			return MOVE_RIGHT;
		}
		else if(keyController.getMoveDown())
		{
			return MOVE_DOWN;
		}
		else if(keyController.getRotate())
		{
			return ROTATE;
		}
		else//Should not move if we are trying to move both left and right at the same time or if neither the left or right buttons are pressed
		{
			return NO_MOVE;
		}
	}
	
	public boolean shouldDrop()
	{
		if(dropFlag >= activeLevel.getDropDelay())
		{
			dropFlag = 0;
			return true;
		}
		else
		{
			dropFlag++;
			return false;
		}
	}
	
	public void setActiveLevel(Level level)
	{
		activeLevel = level;
	}
	
	public Level getActiveLevel()
	{
		return activeLevel;
	}
	
	public KeyControl getKeyController()
	{
		return keyController;
	}
	
	public boolean clearedLevel()
	{
		return numBricks >= activeLevel.getBrickThreshold();
	}
	
	public void setNumBricks(int numBricks)
	{
		this.numBricks = numBricks;
	}
	
	public int getNumBricks()
	{
		return numBricks;
	}
	
	public Date getStartTime()
	{
		return startTime;
	}
}
