package main;
import gui.ColumnsCanvas;

import java.util.Random;


public class Brick {
	public int x, y, width, height, left, right, top, bottom, color;
	
	public Brick(int x, int y) 
	{
		this.x = x;
		this.y = y;
		width = GameEngine.CELL_WIDTH;
		height = GameEngine.CELL_HEIGHT;
		this.left = x;
		this.right = x + width;
		this.bottom = y;
		this.top = y + height;
		
		//Pick a random color
		Random r = new Random();
		this.color = r.nextInt(ColumnsCanvas.NUM_BLOCK_COLORS);
	}
	
	public boolean intersectsBounderies(int pf_width, int pf_height) {
		boolean intersectBoundery = false;
		
		/*Check if the rectangle is outside the canvas bounderies and move it inside if that is the case*/
		if(x < GameEngine.STATUS_FIELD_WIDTH) {
			intersectBoundery = true;
		}
		else if((x + width) > pf_width) {
			intersectBoundery = true;
		}
		if(y < 0) {
			intersectBoundery = true;
		}
		else if((y + height) > pf_height) {
			intersectBoundery = true;
		}
		
		return intersectBoundery;
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
	
	public boolean checkForIntersection(Brick rect)
	{	
		return left < rect.right && right > rect.left && 
			   bottom < rect.top && top > rect.bottom;
	}
	
	/**
	 * move
	 * Moves the rectangle
	 * @param xMove The amount to move the rectangle along x
	 * @param yMove The amount to move the rectangle along y
	 */
	public void move(int xMove, int yMove) {
		x += xMove;
		left += xMove;
		right += xMove;
		y += yMove;
		bottom += yMove;
		top += yMove;
	}
	
	public void xMove(int xMove)
	{
		x += xMove;
		left += xMove;
		right += xMove;
	}
	
	public void yMove(int yMove)
	{
		y += yMove;
		bottom += yMove;
		top += yMove;
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.left = x;
		this.right = x + width;
		this.y = y;
		this.bottom = y;
		this.top = y + height;
	}
	
	public void setXPos(int x)
	{
		this.x = x;
		this.left = x;
		this.right = x + width;
	}
	
	public void setYPos(int y)
	{
		this.y = y;
		this.bottom = y;
		this.top = y + height;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public void addToBrickGrid(Brick[][] brickGrid)
	{	
		int row = GameEngine.getGridRow(y);
		int column = GameEngine.getGridColumn(x);
		
		brickGrid[row][column] = this;
	}
}
