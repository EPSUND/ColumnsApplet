package main;
import gui.ColumnsCanvas;
import hscore.ColumnsHighScoreTableModel;
import hscore.HighScoreListDialog;
import hscore.HighScoreSystem;
import hscore.ColumnsHighScoreSystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sound.SoundPlayer;
import utils.Helpers;


public class Columns extends JApplet implements Runnable {
	
	public static final int DEFAULT_WINDOW_WIDTH = 545;
	public static final int DEFAULT_WINDOW_HEIGHT = 540;
	
	public static final int CANVAS_GRID_WIDTH = 320;//560
	public static final int CANVAS_GRID_HEIGHT = 480;//560
	
	public static final int COLUMNS_SPACING = 15;
	
	//GUI items
	
	public JPanel columnsPanel;
	public JFrame columnsFrame;
	public JMenuBar menuBar;
	public JMenu gameMenu;
	public JMenu optionsMenu;
	public JMenu helpMenu;
	public JMenuItem restartItem;
	public JMenuItem pauseItem;
	public JMenuItem resumeItem;
	public JMenuItem highScoreItem;
	public JMenuItem muteSoundEffectsItem;
	public JMenuItem howToPlayItem;
	public JMenuItem controlsItem;
	public JMenuItem aboutItem;
	
	public ColumnsCanvas columnsCanvas;
	
	//Game resources
	public static HighScoreSystem highScoreSystem;
	public static SoundPlayer soundPlayer;
	
	public GameEngine gameEngine;
	
	private Thread gameThread;
	
	public void destroy()
	{
		//Dispose sound resources
		soundPlayer.disposePlayer();
	}
	
	public void stop()
	{
		//G�r n�got vettigt
	}
	
	public void start()
	{
		//G�r n�got vettigt
	}
	
	public void run()
	{
		try
		{
			do 
			{
				//Initiate the game
				gameEngine.initWorld();
				
				if(!gameEngine.getRestart())//Only do this once
				{
					/* Set the applet size. Extend the height somewhat for the status field and the menu bar*/
					setSize(gameEngine.getPlayingFieldWidth(), gameEngine.getPlayingFieldHeight() + menuBar.getHeight());
				}
				else//We have restarted and should set the restart flag to false
				{
					gameEngine.setRestart(false);
					
				}
				
				/*Start the game. */
				gameEngine.game();
			}
			while(gameEngine.getRestart());
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, Helpers.getStackTraceString(e), "Word on Word: An exception has occured", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	public void init()
	{	
		try 
		{
			//Create the applet's GUI on the event-dispatching thread
	        SwingUtilities.invokeAndWait(new Runnable() {
	            public void run() {
	                createGUI();
	            }
	        });
	        
	        //Create game resources
	        createGameResources();
	        //Start a new game
	        newGame();
		}
		catch(Exception e)
		{
			System.err.println("Columns: The game could not initialised properly");
	        e.printStackTrace();
	        return;
		}
	}
	
	private void newGame() 
	{
		//Start the game thread
	    gameThread = new Thread(this);
	    gameThread.start();
	}
	
	private void createGameResources()
	{
		gameEngine = new GameEngine(columnsCanvas);
		soundPlayer = makeColumnsSoundPlayer();
		highScoreSystem = new ColumnsHighScoreSystem(makeUnsortedListFeedURL(),
													 makeSortedListFeedURL(),
													 new HighScoreListDialog(new ColumnsHighScoreTableModel())
													 );
	}
	
	private void createGUI()
	{
		/*Get the frame's panel*/
		columnsPanel = (JPanel)getContentPane();

		/* Create the menu bar. */
		menuBar = new JMenuBar();

		/* Create the game menu */
		gameMenu = new JMenu("Game");
		
		/*Add game menu to menu bar*/
		menuBar.add(gameMenu);
		
		/* Create the game menu items */
		restartItem = new JMenuItem("Restart");
		pauseItem = new JMenuItem("Pause");
		resumeItem = new JMenuItem("Resume");
		highScoreItem = new JMenuItem("High score");
		
		/*Add the game menu items to the menu*/
		gameMenu.add(restartItem);
		gameMenu.add(pauseItem);
		gameMenu.add(highScoreItem);
		
		/*Create the options menu*/
		optionsMenu = new JMenu("Options");
		
		/*Add the options menu to the menu bar*/
		menuBar.add(optionsMenu);
		
		/*Create the options menu items*/
		muteSoundEffectsItem = new JMenuItem("Mute sound effects");
		
		/*Add the options menu items to the menu*/
		optionsMenu.add(muteSoundEffectsItem);
		
		//Make the help menu
		helpMenu = new JMenu("Help");
		
		//Add the help menu to the menu bar
		menuBar.add(helpMenu);
		
		//Create the "how to play" menu item
		howToPlayItem = new JMenuItem("How to play");
		//Create the controls menu item
		controlsItem = new JMenuItem("Controls");
		//Create the about menu item
		aboutItem = new JMenuItem("About");
		
		//Add the help menu items
		helpMenu.add(howToPlayItem);
		helpMenu.add(controlsItem);
		helpMenu.add(aboutItem);
		
		/* Install the menu bar in the frame. */
		gameMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		
		/*Set the menu bar*/
		setJMenuBar(menuBar);
		
		/*Make the canvas*/
		columnsCanvas = new ColumnsCanvas();
		
		/* Add the canvas to the panel. */
		columnsPanel.add(columnsCanvas);
		
		/* Enable double buffering. */
		columnsCanvas.createStrategy();
		
		//G�r dialoger
		
		/*Add listeners*/
		
		restartItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				//The game will be unpaused when the game is restarted so the pause menu item should be added to the menu again
				if(gameEngine.getIsPaused())
				{
					gameMenu.remove(resumeItem);
					gameMenu.add(pauseItem, 1);
				}
				gameEngine.setRestart(true);
			}
		});
		
		pauseItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(true);
				gameMenu.remove(pauseItem);
				gameMenu.add(resumeItem, 1);
			}
		});
		
		resumeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(false);
				gameMenu.remove(resumeItem);
				gameMenu.add(pauseItem, 1);
			}
		});
		
		highScoreItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(true);//Pause the game while viewing the high score list
				highScoreSystem.showHighScoreList();
				gameEngine.setIsPaused(false);
				//The game window need to regain focus
				columnsCanvas.requestFocus();
			}
		});
		
		muteSoundEffectsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if(soundPlayer.isMuted())
				{
					soundPlayer.unmute();
					muteSoundEffectsItem.setText("Mute sound effects");
				}
				else
				{
					soundPlayer.mute();
					muteSoundEffectsItem.setText("Unmute sound effects");
				}
			}
		});
		
		howToPlayItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(true);//Pause the game while viewing help
				JOptionPane.showMessageDialog(null,
						"GAMEPLAY:\n" +
						"The objective of the game is to arrange the blocks that appear at the top of the screen so that the bricks that make up the blocks are grouped with bricks of the same colour.\nWhen there is a horizontal, vertical or diagonal row of bricks(at least 3 bricks long) of the same colour are the bricks eliminated.\nThe player must eliminate as many bricks as possible and prevent bricks from stacking all the way to the top. If that happens is the game over.\nBlocks automatically fall towards the bottom and the more bricks that are eliminated the faster the blocks fall.\nScore is awarded for each row of bricks eliminated. Longer rows give more points, and bonus points are awarded if the row is diagonal or if more than one row is eliminated at the same time.\n\n" +
						"SCORING:\n" +
						"The player is awarded score when a row of bricks are eliminated, according to this system:\n\nNormal row:\n\n3 bricks = 1 point\n4 bricks = 3 points\n5 bricks = 10 points\n\nDiagonal row:\n\n3 bricks = 2 point\n4 bricks = 4 points\n5 bricks = 11 points\n\n 1 extra point is awarded for each extra row if multiple rows are eliminated at the same time.", 
						"How to play", JOptionPane.INFORMATION_MESSAGE, null);
				gameEngine.setIsPaused(false);
			}
		});
		
		controlsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(true);//Pause the game while viewing help
				JOptionPane.showMessageDialog(null, 
						"CONTROLLING THE BLOCK:\n" +
						"The block is controlled using the movement keys on the keyboard.\n \"Left\" and \"Right\" moves the block left and right.\n\"Up\" rotates the bricks in the block.\n\"Down\" moves the block downwards.\n\"Space\" drops the block to the bottom instantly.\n" +
						"PAUSING THE GAME:\n" +
						"The game can be paused by clicking the pause menu item in the game menu.\n" +
						"TURNING ON AND OFF SOUNDS:\n" +
						"The game's sound effects can be turned on and off in the options menu.\n" +
						"VIEW HIGH SCORE LIST:\n" +
						"The game has a high score list which can viewed by clicking the high score menu item in the game menu.\n" +
						"RESTART GAME:\n" +
						"The game can be restarted by clicking the restart menu item in the game menu.",
						"Controls", JOptionPane.INFORMATION_MESSAGE, null);
				gameEngine.setIsPaused(false);
			}
		});
		
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				gameEngine.setIsPaused(true);//Pause the game while viewing help
				JOptionPane.showMessageDialog(null,
						"DEVELOPED BY:\n" +
						"Erik Sundholm 2013",
						"About", JOptionPane.INFORMATION_MESSAGE, null);
				gameEngine.setIsPaused(false);
			}
		});
	}
	
	private SoundPlayer makeColumnsSoundPlayer()
	{
		SoundPlayer sp = new SoundPlayer();
		
		sp.loadClip("sounds/cleared_brick_3.wav", "cleared_brick_3");
		sp.loadClip("sounds/cleared_brick_4.wav", "cleared_brick_4");
		sp.loadClip("sounds/cleared_brick_5.wav", "cleared_brick_5");
		
		return sp;
	}
	
	private URL makeUnsortedListFeedURL()
	{
		URL url;
		
		try
		{
			url = new URL("https://spreadsheets.google.com/feeds/list/tWyj1F413cTMy3of06WmGzw/od6/private/full");
		}
		catch(MalformedURLException e)
		{
			System.err.println("Columns: The provided URL is malformed");
			e.printStackTrace();
			//Make the url null just in case
			url = null;
		}
		
		return url;
	}
	
	private URL makeSortedListFeedURL()
	{
		URL url;
		
		try
		{
			url = new URL("https://spreadsheets.google.com/feeds/list/tWyj1F413cTMy3of06WmGzw/od7/public/values");
		}
		catch(MalformedURLException e)
		{
			System.err.println("Columns: The provided URL is malformed");
			e.printStackTrace();
			//Make the url null just in case
			url = null;
		}
		
		return url;
	}
}
