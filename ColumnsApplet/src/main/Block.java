package main;
import java.util.ArrayList;


public abstract class Block {
	protected ArrayList<Brick> bricks;
	
	public Block()
	{
		bricks = new ArrayList<Brick>();
	}
	
	public abstract void rotate();
	public abstract void setPosition(int x, int y);
	
	public void addToBrickGrid(Brick[][] brickGrid)
	{	
		for(Brick brick : bricks)
		{
			brick.addToBrickGrid(brickGrid);
		}
	}
	
	public boolean checkForIntersection(Brick[][] brickGrid)
	{
		boolean intersectsBrick = false;
		
		for(int i = 0; i < brickGrid.length; i++)
		{
			for(int j = 0; j < brickGrid[i].length; j++)
			{
				if(brickGrid[i][j] != null && checkForIntersection(brickGrid[i][j]))
				{
					intersectsBrick = true;
					break;
				}
			}
		}
		
		return intersectsBrick;
	}
	
	private boolean checkForIntersection(Brick brick)
	{
		boolean intersectsBrick = false;
		
		for(Brick blockBrick : bricks)
		{
			if(blockBrick.checkForIntersection(brick))
			{
				intersectsBrick = true;
				break;
			}
		}
		
		return intersectsBrick;
	}
	
	public boolean intersectsBounderies(int pf_width, int pf_height)
	{
		boolean intersectsBorder = false;
		
		for(Brick brick : bricks)
		{
			if(brick.intersectsBounderies(pf_width, pf_height))
			{
				intersectsBorder = true;
				break;
			}
		}
		
		return intersectsBorder;
	}
	
	public void moveToFloor(Brick[][] brickGrid, int pf_width, int pf_height)
	{
		boolean reachedFloor = false;
		
		//Move the block down until one of its bricks intersects either the floor or a brick
		while(!reachedFloor)
		{
			//Move the block down a step
			moveY(GameEngine.CELL_HEIGHT);
			//Check if the block intersects anything
			if(intersectsBounderies(pf_width, pf_height) || checkForIntersection(brickGrid))
			{
				//Resolve the intersection
				moveY(-GameEngine.CELL_HEIGHT);
				//Add the block to the brick grid
				addToBrickGrid(brickGrid);
				reachedFloor = true;
			}
		}
	}
	
	public void move(int x, int y)
	{
		for(Brick brick : bricks)
		{
			brick.move(x, y);
		}
	}
	
	public void moveX(int x)
	{
		for(Brick brick : bricks)
		{
			brick.xMove(x);
		}
	}
	
	public void moveY(int y)
	{
		for(Brick brick : bricks)
		{
			brick.yMove(y);
		}
	}
	
	public ArrayList<Brick> getBricks()
	{
		return bricks;
	}
	
	public void addToGrid(Brick[][] brickGrid)
	{
		int row, column;
		
		for(Brick brick : bricks)
		{
			row = GameEngine.getGridRow(brick.y);
			column = GameEngine.getGridColumn(brick.x);
		}
	}
}
