package main;
import gui.ColumnsCanvas;

import java.util.ArrayList;


public class GameEngine {
	//Grid constants
	public static final int GRID_COLUMNS = 10;
	public static final int GRID_ROWS = 15;
	public static final int CELL_WIDTH = Columns.CANVAS_GRID_WIDTH / GRID_COLUMNS;
	public static final int CELL_HEIGHT = Columns.CANVAS_GRID_HEIGHT / GRID_ROWS;
	private static final int START_COLUMN = 5;
	private static final int START_ROW = 0;
	
	public static final int NEXT_BLOCK_X = CELL_WIDTH - Columns.STATUS_FIELD_SPACING / 2;
	public static final int NEXT_BLOCK_Y = CELL_HEIGHT * 8;
	
	private static final int FALLING_BRICK_DELAY = 10;
	
	public static final int STATUS_FIELD_WIDTH = 3 * CELL_WIDTH;
	
	private ColumnsCanvas canvas;
	private Player player;
	private Brick[][] brickGrid;
	private Block activeBlock, nextBlock;
	private ArrayList<Brick> fallingBricks;
	private ArrayList<Level> levels;
	private ScoreKeeper scoreKeeper;
	
	private int pf_width, pf_height;
	
	private volatile boolean restart;
	private boolean isGameOver, isPaused;
	private int fallingBricksFlag;
	
	public GameEngine(ColumnsCanvas canvas)
	{
		this.canvas = canvas;
		restart = false;
		isGameOver = false;
		isPaused = false;
		
		activeBlock = null;
		nextBlock = null;
		
		brickGrid = new Brick[GRID_ROWS][GRID_COLUMNS];
		
		//Set to null just in case
		for(int i = 0; i < GRID_ROWS; i++)
		{
			for(int j = 0; j < GRID_COLUMNS; j++)
			{
				brickGrid[i][j] = null;
			}
		}
		
		fallingBricksFlag = 0;
		
		fallingBricks = new ArrayList<Brick>();
		levels = new ArrayList<Level>();
		
		scoreKeeper = new ColumnsScoreKeeper();
	}
	
	/**
	 * initWorld
	 * Executes the initial creating of the world.
	 */
	public void initWorld()
	{
		//Initiera spel
		if(!restart)
		{
			//Make the player
			player = new Player(canvas);
			
			pf_width = Columns.CANVAS_GRID_WIDTH + STATUS_FIELD_WIDTH;
			pf_height = Columns.CANVAS_GRID_HEIGHT;
			
			setCanvasSize(pf_width, pf_height);
			canvas.addKeyListener(player.getKeyController());
		}
		else
		{
			//Reset the game over flag
			isGameOver = false;
			//Reset the paused flag
			isPaused = false;
			//Make the player ready for a new game
			player.restartGame();
			//Clear the brick grid
			for(int i = 0; i < GRID_ROWS; i++)
			{
				for(int j = 0; j < GRID_COLUMNS; j++)
				{
					brickGrid[i][j] = null;
				}
			}
			//Clear the falling bricks
			fallingBricks.clear();
			//Reset the score keeper
			scoreKeeper.reset();
		}
		
		//Get the first block
		getNextBlock();
		//Make the levels
		makeLevels();
		//Make the first level the active level
		player.setActiveLevel(levels.remove(0));//The levels should be removed from the level list as they are used
	}
	
	private void updateFallingBricks()
	{
		//We have no falling bricks to update
		if(fallingBricks.size() == 0)
		{
			return;
		}
		
		//The falling bricks should be updated
		if(fallingBricksFlag >= FALLING_BRICK_DELAY)
		{
			boolean fallingBricksUpdated = false;
			
			while(!fallingBricksUpdated)
			{
				fallingBricksUpdated = true;
				
				for(Brick brick : fallingBricks)
				{
					//Move brick towards floor
					brick.yMove(CELL_HEIGHT);
					//Check if the brick intersects the floor or a boundery
					if(brick.intersectsBounderies(pf_width, pf_height) || brick.checkForIntersection(brickGrid))
					{
						//Resolve the intersection
						brick.yMove(-CELL_HEIGHT);
						//Add the brick to the brick grid
						brick.addToBrickGrid(brickGrid);
						//Remove the brick from the falling brick list
						fallingBricks.remove(brick);
						//We need to restart the loop since we have removed an element from the list
						fallingBricksUpdated = false;
						break;
					}
				}
			}
			
			//Reset the falling bricks flag
			fallingBricksFlag = 0;
		}
		else//It is not time to update the falling bricks yet, increment the flag
		{
			fallingBricksFlag++;
		}
		
	}
	
	/**
	 * updateWorld
	 * Updates the game world
	 */
	public void updateWorld() 
	{
		boolean blockDroppedToFloor = false;
		int score;
		
		//Clear score giving rows and add the score to the player
		score = scoreKeeper.calcScore(brickGrid, fallingBricks);
		score *= player.getActiveLevel().getScoreFactor();
		player.addScore(score);
		//Update the number of bricks cleared
		player.setNumBricks(((ColumnsScoreKeeper)scoreKeeper).getNumBricksCleared());
		
		//Switch level if the player has cleared it
		updateLevel();
		
		//Update the falling bricks
		updateFallingBricks();
		
		//Check if the block should drop to the floor
		if(fallingBricks.size() == 0 && player.shouldDropToFloor())
		{
			//Move the active block to the floor
			activeBlock.moveToFloor(brickGrid, pf_width, pf_height);
			//Get the drop to floor and add the score to the player
			score = scoreKeeper.calcDropToFloorScore();
			score *= player.getActiveLevel().getScoreFactor();
			player.addScore(score);
			//Get the next block
			getNextBlock();
			//Indicate with this flag that this block should not be moved anymore
			blockDroppedToFloor = true;
			//Reset all block movement flags
			player.resetFlags();
		}
		
		//Check if the active block should move down a row
		if(fallingBricks.size() == 0 && !blockDroppedToFloor && player.shouldDrop())
		{
			activeBlock.moveY(CELL_HEIGHT);
			
			//If we have hit the floor or a brick
			if(activeBlock.intersectsBounderies(pf_width, pf_height) || activeBlock.checkForIntersection(brickGrid))
			{
				//Resolve the intersection
				activeBlock.moveY(-CELL_HEIGHT);
				//Add the block to the brick grid
				activeBlock.addToBrickGrid(brickGrid);
				//Get the next block
				getNextBlock();
			}
		}
		
		//Check if the block should be moved
		if(fallingBricks.size() == 0 && !blockDroppedToFloor && player.shouldMove())
		{
			int xMove = 0;
			int yMove = 0;
			
			int moveDir = player.getMoveDir();
			
			if(moveDir != Player.ROTATE)//If we should make a move that is not a rotation
			{
				if(moveDir == Player.MOVE_LEFT)
				{
					xMove = -CELL_WIDTH;
				}
				else if(moveDir == Player.MOVE_RIGHT)
				{
					xMove = CELL_WIDTH;
				}
				else if(moveDir == Player.MOVE_DOWN)
				{
					yMove = CELL_HEIGHT;
				}
				
				activeBlock.move(xMove, yMove);
				
				//If we have hit a boundary or a brick
				if(activeBlock.intersectsBounderies(pf_width, pf_height) || activeBlock.checkForIntersection(brickGrid))
				{
					//Resolve the intersection
					activeBlock.move(-xMove, -yMove);
				}
			}
			else//This is a rotation
			{
				activeBlock.rotate();
				//Om detta var tetris så skulle vi behöva kolla så rotationen inte medförde en kolision
			}
		}
	}
	
	/**
	 * paintWorld
	 * Paints all the background scenes and objects on canvas.
	 */
	public void paintWorld() 
	{
		/* Initiate the graphics context */
		canvas.initGraphicsContext();
		
		//Paint the background
		canvas.paintBackground();
		
		//Paint the status bar
		canvas.paintStatusField(player.getScore(), player.getNumBricks(), player.getLevelNum());
		
		//Paint the brick grid
		for(int i = 0; i < GameEngine.GRID_ROWS; i++)
		{
			for(int j = 0; j < GameEngine.GRID_COLUMNS; j++)
			{
				if(brickGrid[i][j] != null)
				{
					canvas.paint(brickGrid[i][j]);
				}
			}
		}
		
		//Paint the falling bricks
		for(Brick brick : fallingBricks)
		{
			canvas.paint(brick);
		}
		
		//Paint the active block
		canvas.paint(activeBlock);
		
		//Paint the next block
		canvas.paint(nextBlock);
		
		//If the game is over, paint a message
		if(isGameOver)
			canvas.paintGameOverMessage();
		
		//Show the graphics buffer
		canvas.showGraphicsBuffer();
	}
	
	/**
	 * game
	 * The game main loop. Calls the updating and painting of objects
	 * every FRAME_DELAY ms.
	 */
	public void game() {
		/* Update the game as long as it is running. */
		while (canvas.isVisible() && !restart) {
			if(!isGameOver && !isPaused)
				updateWorld();	// Update the player and NPCs.
			
			paintWorld();	// Paint the objects and background scenes.
			
			try { 
				Thread.sleep(ColumnsCanvas.FRAME_DELAY);		// Wait a given interval.
			} catch (InterruptedException e) {
				// Ignore.
			}
		}
		
		return;
	}
	
	private void setCanvasSize(int width, int height)
	{
		/*Set the canvas size*/
		canvas.setPlayingFieldWidth(width);
		canvas.setPlayingFieldHeight(height);
		canvas.setBounds(0, 0, width, height);
	}
	
	public void setRestart(boolean val) {
		restart = val;
	}
	
	public boolean getRestart() {
		return restart;
	}
	
	public void setPlayingFieldWidth(int width) {
		pf_width = width;
	}
	
	public int getPlayingFieldWidth() {
		return pf_width;
	}
	
	public void setPlayingFieldHeight(int height) {
		pf_height = height;
	}
	
	public int getPlayingFieldHeight() {
		return pf_height;
	}
	
	public void setIsPaused(boolean val)
	{
		isPaused = val;
	}
	
	public boolean getIsPaused()
	{
		return isPaused;
	}
	
	private void makeLevels()
	{
		//Must clear the level list before adding new levels, if any levels are left
		levels.clear();
		//Add levels
		levels.add(new Level(1, 1.0f, 100, 40));
		levels.add(new Level(2, 1.1f, 90, 80));
		levels.add(new Level(3, 1.2f, 80, 120));
		levels.add(new Level(4, 1.3f, 70, 160));
		levels.add(new Level(5, 1.4f, 60, 200));
		levels.add(new Level(6, 1.5f, 50, 240));
		levels.add(new Level(7, 1.6f, 45, 280));
		levels.add(new Level(8, 1.7f, 40, 320));
		levels.add(new Level(9, 1.8f, 35, 360));
		levels.add(new Level(10, 1.9f, 30, 400));
		levels.add(new Level(11, 2.0f, 25, 440));
		levels.add(new Level(12, 2.1f, 20, 480));
		levels.add(new Level(13, 2.2f, 17, 520));
		levels.add(new Level(14, 2.3f, 14, 560));
		levels.add(new Level(15, 2.4f, 12, 600));
		levels.add(new Level(16, 2.6f, 11, 640));
		levels.add(new Level(17, 2.8f, 10, 680));
		levels.add(new Level(18, 3.0f, 9, 720));
		levels.add(new Level(19, 3.2f, 8, 760));
		levels.add(new Level(20, 3.4f, 7, 800));
	}
	
	private void getNextBlock()
	{
		//If this is the first time we get a block
		if(nextBlock == null)
			nextBlock = new ColumnBlock(NEXT_BLOCK_X, NEXT_BLOCK_Y);
		
		//Make the next block the active block
		activeBlock = nextBlock;
		//Make a new next block
		nextBlock = new ColumnBlock(NEXT_BLOCK_X, NEXT_BLOCK_Y);
		//Move the block to the start position
		activeBlock.setPosition(GameEngine.getCanvasXPos(START_COLUMN), GameEngine.getCanvasYPos(START_ROW));
		//Check if the new block would intersect any brick and mark the game as over if that is the case
		if(activeBlock.checkForIntersection(brickGrid))
		{
			isGameOver = true;
			
			//Run the score registration in a seperate thread to prevent the rendering from being paused
			new Thread()
			{
				public void run()
				{
					Columns.highScoreSystem.registerScore(new Object[]{player.getScore(), player.getNumBricks(), player.getActiveLevel().getLevelNum(), player.getStartTime()});	
				}
			}.start();
		}
	}
	
	private void updateLevel()
	{
		//Switch level if the player has cleared the active level if there are more levels
		if(player.clearedLevel() && levels.size() > 0)
		{
			player.setActiveLevel(levels.remove(0));
		}
	}
	
	//Static methods
	
	public static int getCanvasXPos(int column)
	{	
		return STATUS_FIELD_WIDTH + column * CELL_WIDTH;
	}
	
	public static int getGridColumn(int xPos)
	{
		if((xPos - STATUS_FIELD_WIDTH) / CELL_WIDTH < 0)
		{
			return 0;
		}
		else
		{
			return (xPos - STATUS_FIELD_WIDTH) / CELL_WIDTH;
		}
	}
	
	public static int getCanvasYPos(int row)
	{	
		return row * CELL_HEIGHT;
	}
	
	public static int getGridRow(int yPos)
	{	
		if(yPos / CELL_HEIGHT < 0)
		{
			return 0;
		}
		else
		{
			return yPos / CELL_HEIGHT;
		}
	}
}
