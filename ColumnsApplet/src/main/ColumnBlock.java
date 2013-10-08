package main;

import java.util.ArrayList;

public class ColumnBlock extends Block {

	public ColumnBlock(int x, int y)
	{
		super();
		
		//Add the bricks. Let them make up a downward rectangle.
		bricks.add(new Brick(x, y));
		bricks.add(new Brick(x, y + GameEngine.CELL_HEIGHT));
		bricks.add(new Brick(x, y + 2 * GameEngine.CELL_HEIGHT));
	}
	
	public void setPosition(int x, int y)
	{
		bricks.get(0).setPosition(x, y);
		bricks.get(1).setPosition(x, y + GameEngine.CELL_HEIGHT);
		bricks.get(2).setPosition(x, y + 2 * GameEngine.CELL_HEIGHT);
	}
	
	@Override
	public void rotate() 
	{
		//Rearrange the bricks in the brick list and move them
		Brick brick;
		ArrayList<Brick> newBricks = new ArrayList<>();
		//Add the first brick
		brick = bricks.remove(2);
		brick.yMove(-2 * GameEngine.CELL_HEIGHT);
		newBricks.add(brick);
		//Add the second brick
		brick = bricks.remove(0);
		brick.yMove(GameEngine.CELL_HEIGHT);
		newBricks.add(brick);
		//Add the third brick
		brick = bricks.remove(0);
		brick.yMove(GameEngine.CELL_HEIGHT);
		newBricks.add(brick);
		//Use the new rearranged brick list
		bricks = newBricks;
	}
}
