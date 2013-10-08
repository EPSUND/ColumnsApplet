package hscore;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import utils.Helpers;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;


public class ColumnsHighScoreSystem implements HighScoreSystem {
	
	private static final String USERNAME = "shmiagames@gmail.com";
	private static final String PASSWORD = "holmsund19";
	
	private HighScoreListDialog highScoreDialog;
	
	private SpreadsheetService highScoreService;
	private URL readWSListFeedURL, writeWSListFeedURL;
	
	public ColumnsHighScoreSystem(URL unsortedListFeed, URL sortedListFeed, HighScoreListDialog highScoreListDialog)
	{	
		readWSListFeedURL = sortedListFeed;
		writeWSListFeedURL = unsortedListFeed;
		this.highScoreDialog = highScoreListDialog;
		
		highScoreService = new SpreadsheetService("HighScoreService");
		
		//Make a spreadsheet service
		try 
		{
			highScoreService.setUserCredentials(USERNAME, PASSWORD);
		} catch (AuthenticationException e) {
			System.err.println("Columns: Could not authenicate user");
			e.printStackTrace();
		}
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
			registerScore(score, bricks, level, startTime, name);
			showHighScoreList();
		}
	}
	
	private void registerScore(int score, int bricks, int level, Date startTime, String name)
	{	
		try 
		{
			ListEntry newEntry = new ListEntry();
			newEntry.getCustomElements().setValueLocal("name", name);
			newEntry.getCustomElements().setValueLocal("score", Integer.toString(score));
			newEntry.getCustomElements().setValueLocal("bricks", Integer.toString(bricks));
			newEntry.getCustomElements().setValueLocal("level", Integer.toString(level));
			newEntry.getCustomElements().setValueLocal("time", Long.toString(Helpers.getTimeSpan(startTime, new Date())));
			newEntry.getCustomElements().setValueLocal("date", Helpers.getCurrentTimeUTC());
			highScoreService.insert(writeWSListFeedURL, newEntry);
		} catch (IOException e) {
			System.err.println("Columns: Could not add score entry");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.err.println("Columns: Could not add score entry");
			e.printStackTrace();
		}
	}
	
	public void showHighScoreList()
	{
		try {
			ListFeed scoreRowFeed = highScoreService.getFeed(readWSListFeedURL, ListFeed.class);
			
			ArrayList<Object[]> highScoreList = new ArrayList<Object[]>();
			
			for(ListEntry row : scoreRowFeed.getEntries())
			{
				Object[] objRow = new Object[6];
				objRow[0] = row.getCustomElements().getValue("name");
				objRow[1] = row.getCustomElements().getValue("score");
				objRow[2] = row.getCustomElements().getValue("bricks");
				objRow[3] = row.getCustomElements().getValue("level");
				
				objRow[4] = row.getCustomElements().getValue("time");
				objRow[4] = Helpers.getTimeSpanString(Long.parseLong((String)objRow[4]));
				
				objRow[5] = row.getCustomElements().getValue("date");
				objRow[5] = Helpers.utcToLocalTime((String)objRow[5]);
				
				highScoreList.add(objRow);
			}
			
			highScoreDialog.setHighScoreList(highScoreList);
			highScoreDialog.setVisible(true);
		} catch (IOException e) {
			System.err.println("Columns: Could not load score rows");
			e.printStackTrace();
		} catch (ServiceException e) {
			System.err.println("Columns: Could not load score rows");
			e.printStackTrace();
		}
	}
}
