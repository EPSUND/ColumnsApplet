package main;

public class Level {
	private float scoreFactor;
	private int levelNum, dropDelay, brickThreshold;
	
	public Level(int levelNum, float scoreFactor, int dropDelay, int brickThreshold)
	{
		this.levelNum = levelNum;
		this.scoreFactor = scoreFactor;
		this.dropDelay = dropDelay;
		this.brickThreshold = brickThreshold;
	}
	
	public int getBrickThreshold()
	{
		return brickThreshold;
	}
	
	public float getScoreFactor()
	{
		return scoreFactor;
	}
	
	public int getDropDelay()
	{
		return dropDelay;
	}
	
	public int getLevelNum()
	{
		return levelNum;
	}
}
