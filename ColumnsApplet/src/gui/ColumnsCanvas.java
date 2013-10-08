package gui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferStrategy;

import main.Block;
import main.Brick;
import main.Columns;
import main.GameEngine;

import utils.Helpers;


public class ColumnsCanvas extends Canvas {
	
	private static final long serialVersionUID = 1L;
	
	public static final int FRAME_DELAY = 10;		// Update interval.
	
	//Colors
	public static final Color BACKGROUND_COLOR = Color.BLACK;
	private static final Color STATUS_TEXT_COLOR = Color.WHITE;
	private static final Color GAMEOVER_TEXT_COLOR = Color.RED; 
	
	//Textures
	private static final String BACKGROUND_IMAGE = "steel_texture.jpg";
	private static final String STATUS_FIELD_IMAGE = "steel_texture2.jpg";
	private static final String NEXT_BLOCK_BOX_IMAGE = "glass_texture.jpg";
	
	//Block colors
	public static final int NUM_BLOCK_COLORS = 5;
	private static final int BLUE_BLOCK = 0;
	private static final int GREEN_BLOCK = 1;
	private static final int RED_BLOCK = 2;
	private static final int YELLOW_BLOCK = 3;
	private static final int MAGENTA_BLOCK = 4;
	
	/* Size of a single pane (Standard size of objects). */
	public final int PANE_SIZE = 30;
	
	private ImageCache imageCache;	// The image cache for the canvas.	
	
	private Graphics2D graphicsContext;
	
	public BufferStrategy strategy; // Double buffered strategy.
	
	/*Fonts*/
	private Font statusFieldFont; //The font of the text in the status field
	private Font gameOverFont; //The font to write the game over message
	
	private int pf_width, pf_height;
	
	public ColumnsCanvas()
	{
		// Create an image cache.
		imageCache = new ImageCache();
		
		//Make the font for the text in the status field
		statusFieldFont = new Font("Serif", Font.BOLD, 20);
		//Make the font for the text in the win/lose messages
		gameOverFont = new Font("SansSerif", Font.BOLD, 40);
		
		//Set the size of the canvas.
		this.setBounds(0, 0, Columns.DEFAULT_WINDOW_WIDTH + GameEngine.STATUS_FIELD_WIDTH, Columns.DEFAULT_WINDOW_HEIGHT);
	}
	
	/**
	 * createStrategy
	 * Creates a double buffering strategy for this canvas.
	 */
	public void createStrategy() {
		this.setVisible(true);	// Show the canvas.

		/* Make sure that the canvas is visible. */
		if (this.isDisplayable()) {
			/* Create double buffering. */
			this.createBufferStrategy(2);
			this.strategy = this.getBufferStrategy();

			/* Set the focus. */
			this.requestFocus();
		} else {
			System.err.println("Columns: Could not enable double buffering.");
			System.exit(1);	// Exit Columns.
		}
	}
	
	/**
	 * imageChache
	 * Gets the canvas-specific image cache.
	 * return image cache
	 */
	public ImageCache getImageCache() {
		return imageCache;
	}
	
	/**
	 * initGraphicsContext
	 * Initiates the graphics context
	 */
	public void initGraphicsContext() {
		graphicsContext = (Graphics2D) strategy.getDrawGraphics();
	}
	
	/**
	 * showGraphicsBuffer
	 * Makes the next available graphics buffer visible
	 */
	public void showGraphicsBuffer() {
		/*Dispose the graphics object.*/
		graphicsContext.dispose();
		/*Show buffer.*/
		strategy.show();
	}
	
	/**
	 * paintBackground
	 * Paints the background
	 */
	public void paintBackground() {
		/* Set up the background */
		graphicsContext.setBackground(BACKGROUND_COLOR);
		/*Clear the canvas*/
		paint(graphicsContext);
		/*Draw the background image*/
		graphicsContext.drawImage(imageCache.getImage(Helpers.getResourceURIString(this, "images/" + BACKGROUND_IMAGE)), 0, 0, getWidth(), getHeight(), this);
		
		//Draw the grid
		
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.setStroke(new BasicStroke(1));
		
		for(int i = 0; i <= Columns.CANVAS_GRID_WIDTH; i += GameEngine.CELL_WIDTH)
		{
			graphicsContext.drawLine(GameEngine.STATUS_FIELD_WIDTH + i, 0, GameEngine.STATUS_FIELD_WIDTH + i, getHeight());
		}
		
		for(int i = 0; i <= Columns.CANVAS_GRID_HEIGHT; i += GameEngine.CELL_HEIGHT)
		{
			graphicsContext.drawLine(GameEngine.STATUS_FIELD_WIDTH, i, getWidth(), i);
		}
	}
	
	/**
	 * paintStatusField
	 * Paints the status field
	 */
	public void paintStatusField(int score, int numBricks, int level) 
	{
		//Draw background texture for the status field
		graphicsContext.drawImage(imageCache.getImage(Helpers.getResourceURIString(this, "images/" + STATUS_FIELD_IMAGE)), 0, 0, GameEngine.STATUS_FIELD_WIDTH, getHeight(), this);
		//Make sure the graphics context uses the status field font
		graphicsContext.setFont(statusFieldFont);
		//Draw the level
		drawShadowedText(GameEngine.CELL_WIDTH - Columns.COLUMNS_SPACING, GameEngine.CELL_HEIGHT - 2, "Level", statusFieldFont, STATUS_TEXT_COLOR);
		drawShadowedText(GameEngine.CELL_WIDTH, GameEngine.CELL_HEIGHT + graphicsContext.getFontMetrics().getHeight() - 2, Integer.toString(level), statusFieldFont, STATUS_TEXT_COLOR);
		//Draw the score
		drawShadowedText(GameEngine.CELL_WIDTH - Columns.COLUMNS_SPACING, GameEngine.CELL_HEIGHT * 3 - 2, "Score", statusFieldFont, STATUS_TEXT_COLOR);
		drawShadowedText(GameEngine.CELL_WIDTH, GameEngine.CELL_HEIGHT * 3 + graphicsContext.getFontMetrics().getHeight() - 2, Integer.toString(score), statusFieldFont, STATUS_TEXT_COLOR);
		//Draw the number of bricks
		drawShadowedText(GameEngine.CELL_WIDTH - Columns.COLUMNS_SPACING, GameEngine.CELL_HEIGHT * 5 - 2, "Bricks", statusFieldFont, STATUS_TEXT_COLOR);
		drawShadowedText(GameEngine.CELL_WIDTH, GameEngine.CELL_HEIGHT * 5 + graphicsContext.getFontMetrics().getHeight() - 2, Integer.toString(numBricks), statusFieldFont, STATUS_TEXT_COLOR);
		//Draw the box containing the next block
		graphicsContext.setStroke(new BasicStroke(4));//Make the line thicker
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawRect(GameEngine.NEXT_BLOCK_X - Columns.COLUMNS_SPACING, GameEngine.NEXT_BLOCK_Y - GameEngine.CELL_HEIGHT, GameEngine.CELL_WIDTH + Columns.COLUMNS_SPACING * 2, GameEngine.CELL_HEIGHT * 5);
		graphicsContext.drawImage(imageCache.getImage(Helpers.getResourceURIString(this, "images/" + NEXT_BLOCK_BOX_IMAGE)), GameEngine.NEXT_BLOCK_X - Columns.COLUMNS_SPACING, GameEngine.NEXT_BLOCK_Y - GameEngine.CELL_HEIGHT, GameEngine.CELL_WIDTH + Columns.COLUMNS_SPACING * 2, GameEngine.CELL_HEIGHT * 5, this);
		drawShadowedText(GameEngine.CELL_WIDTH - Columns.COLUMNS_SPACING, GameEngine.NEXT_BLOCK_Y - Columns.COLUMNS_SPACING, "Next", statusFieldFont, STATUS_TEXT_COLOR);
		//Draw the line separating the status field from the playing field
		Stroke oldStroke = graphicsContext.getStroke();
		graphicsContext.setStroke(new BasicStroke(2));//Make the line thicker
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawLine(GameEngine.STATUS_FIELD_WIDTH, 0, GameEngine.STATUS_FIELD_WIDTH, pf_height);
		graphicsContext.setStroke(oldStroke);
	}
	
	/**
	 * paintGameOverMessage
	 * Paints the game over message
	 */
	public void paintGameOverMessage() {
		drawShadowedText((pf_width / 2) - 80, pf_height / 2, "GAME OVER", gameOverFont, GAMEOVER_TEXT_COLOR);
	}
	
	/**
	 * @param width the width to set
	 */
	public void setPlayingFieldWidth(int width) {
		pf_width = width;
	}

	/**
	 * @return the playing field width
	 */
	public int getPlayingFieldWidth() {
		return pf_width;
	}

	/**
	 * @param height the height to set
	 */
	public void setPlayingFieldHeight(int height) {
		pf_height = height;
	}

	/**
	 * @return the playing field height
	 */
	public int getPlayingFieldHeight() {
		return pf_height;
	}
	
	private void drawShadowedText(int x, int y, String text, Font font, Color color)
	{
		//Set the font
		graphicsContext.setFont(font);
		//Draw the shadow
		graphicsContext.setColor(Color.BLACK);
		graphicsContext.drawString(text, x + 2, y + 2);
		//Draw the text
		graphicsContext.setColor(color);
		graphicsContext.drawString(text, x, y);
	}
	
	public void paint (Block block)
	{
		for(Brick brick : block.getBricks())
		{
			paint(brick);
		}
	}
	
	public void paint(Brick rect)
	{
		String blockTexture = "";
		
		switch(rect.getColor())
		{
		case BLUE_BLOCK:
			blockTexture = "Blue.bmp";
			break;
		case GREEN_BLOCK:
			blockTexture = "Green.bmp";
			break;
		case RED_BLOCK:
			blockTexture = "Red.bmp";
			break;
		case YELLOW_BLOCK:
			blockTexture = "Yellow.bmp";
			break;
		case MAGENTA_BLOCK:
			blockTexture = "Magenta.bmp";
			break;	
		}
		
		graphicsContext.drawImage(imageCache.getImage(Helpers.getResourceURIString(this, "images/brick/" + blockTexture)), (int)rect.x, (int)rect.y, (int)rect.width, (int)rect.height, this);
	}
}
