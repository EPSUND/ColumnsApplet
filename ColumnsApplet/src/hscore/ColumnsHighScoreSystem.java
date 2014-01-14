package hscore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import utils.Helpers;

public class ColumnsHighScoreSystem implements HighScoreSystem {
	
	/**
	 * The URL to the high score service
	 */
	private static final String HIGH_SCORE_SERVICE_URL = "http://highscoresystemes86.appspot.com/highscoresystem";
	
	private HighScoreListDialog highScoreDialog;
	
	public ColumnsHighScoreSystem(HighScoreListDialog highScoreListDialog)
	{
		this.highScoreDialog = highScoreListDialog;
	}
	
	public void registerScore(Object[] args)
	{
		int score = (Integer)args[0];
		int bricks = (Integer)args[1];
		int level = (Integer)args[2];
		Date startTime = (Date)args[3];
		
		String name = (String)JOptionPane.showInputDialog(
                null,
                "Please enter your name for the highscore list:",
                "High score",
                JOptionPane.INFORMATION_MESSAGE,
                null,
                null,
                "");
		
		if(name != null)
		{
			try
			{
				URLConnection hscoreConn = getHighScoreConnection(score, bricks, level, startTime, name);
				
				if(hscoreConn != null)
				{
					hscoreConn.connect();
					showHighScoreList(hscoreConn);
				}
			}
			catch(IOException e)
			{
				System.err.println("Columns: Could not connect to the highscore service");
				e.printStackTrace();
			}
		}
	}
	
	private URLConnection getHighScoreConnection(int score, int bricks, int level, Date startTime, String name)
	{	
		try 
		{
			URL registerHighScoreURL = new URL(HIGH_SCORE_SERVICE_URL + "?highScoreList=columns" + 
											   "&name=" + name + 
											   "&score=" + Integer.toString(score) + 
											   "&bricks=" + Integer.toString(bricks) +
											   "&level=" + Integer.toString(level) +
											   "&time=" + Long.toString(Helpers.getTimeSpan(startTime, new Date())) +
											   "&date=" + Helpers.getCurrentTimeUTC());
			
			return registerHighScoreURL.openConnection();
			
		} catch (IOException e) {
			System.err.println("Columns: Could not get a connection to the high score service");
			e.printStackTrace();
			return null;
		}
	}
	
	private URLConnection getHighScoreConnection()
	{	
		try 
		{
			URL registerHighScoreURL = new URL(HIGH_SCORE_SERVICE_URL + "?highScoreList=columns");	
			return registerHighScoreURL.openConnection();	
		} catch (IOException e) {
			System.err.println("Columns: Could not get a connection to the high score service");
			e.printStackTrace();
			return null;
		} 
	}
	
	public void showHighScoreList(URLConnection highScoreServiceConn)
	{
		InputStream highScoreStream = null;
		
		try {
			highScoreStream = highScoreServiceConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(highScoreStream));
			
			String line;
			ArrayList<Object[]> highScoreList = new ArrayList<Object[]>();
			
			while((line = reader.readLine()) != null)
			{
				String[] highScoreData = line.split(",");
				Object[] objRow = new Object[6];
				
				for(String highScoreItem : highScoreData)
				{
					String[] components = highScoreItem.split("=");
					
					if(components[0].equals("name"))
					{
						objRow[0] = components.length < 2 ? "" : components[1];//Must handle if the user didn't enter a name
					}
					else if(components[0].equals("score"))
					{
						objRow[1] = components[1];
					}
					else if(components[0].equals("bricks"))
					{
						objRow[2] = components[1];
					}
					else if(components[0].equals("level"))
					{
						objRow[3] = components[1];
					}
					else if(components[0].equals("time"))
					{
						objRow[4] = Helpers.getTimeSpanString(Long.parseLong(components[1]));
					}
					else if(components[0].equals("date"))
					{
						objRow[5] = Helpers.utcToLocalTime(components[1]);
					}
					
				}
				
				highScoreList.add(objRow);
			}
			
			highScoreDialog.setHighScoreList(highScoreList);
			highScoreDialog.setVisible(true);
		} catch (IOException e) {
			System.err.println("Columns: Could not show high score list");
			e.printStackTrace();
		}
		finally
		{
			try {
				highScoreStream.close();
			} catch (IOException e) {
				System.err.println("Columns: Could not close highscore stream");
				e.printStackTrace();
			}
		}
	}
	
	public void showHighScoreList()
	{
		URLConnection highScoreConn = getHighScoreConnection();
		
		if(highScoreConn != null)
			showHighScoreList(highScoreConn);
	}
}
