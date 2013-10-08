package hscore;

public class ColumnsHighScoreTableModel extends HighScoreTableModel {

	public ColumnsHighScoreTableModel()
	{
		super();
		columnNames = new String[]{"Name", "Score", "Bricks", "Level", "Time", "Date"};
	}
}
