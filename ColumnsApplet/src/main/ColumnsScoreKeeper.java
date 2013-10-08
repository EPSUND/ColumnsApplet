package main;

import java.util.ArrayList;
import java.util.Iterator;

public class ColumnsScoreKeeper implements ScoreKeeper {

	private static final int NUM_BRICKS_FOR_SCORE = 3;
	public static final int SCORE_3_ROW = 1;
	public static final int SCORE_4_ROW = 3;
	public static final int SCORE_5_ROW = 10;
	public static final int SCORE_DIAG_3_ROW = 2;
	public static final int SCORE_DIAG_4_ROW = 4;
	public static final int SCORE_DIAG_5_ROW = 11;
	public static final int BONUS_FOR_EXTRA_ROWS = 1;
	public static final int SCORE_FOR_DROP_TO_FLOOR = 1;
	
	private int numBricksCleared;
	
	public ColumnsScoreKeeper()
	{
		numBricksCleared = 0;
	}
	
	public int getNumBricksCleared()
	{
		return numBricksCleared;
	}
	
	public void reset()
	{
		numBricksCleared = 0;
	}
	
	private ArrayList<ScoreRow> getScoreRows(Brick[][] brickGrid)
	{
		int sameColorLength;
		int startRow, endRow;
		int startColumn, endColumn;
		
		ArrayList<ScoreRow> scoreRows = new ArrayList<ScoreRow>();
		
		//Search for horizontal score rows
		for(int i = 0; i < brickGrid.length; i++)
		{
			//Loop through the row
			for(int j = 0; j < brickGrid[i].length; j++)
			{
				//If we have a non-null brick
				if(brickGrid[i][j] != null)
				{
					sameColorLength = 1;
					startColumn = j;
					endColumn = j;
					//Check if there are bricks of the same color next to it
					for(int k = j + 1; k < brickGrid[i].length; k++)
					{
						//If we have a brick with the same color
						if(brickGrid[i][k] != null && brickGrid[i][j].color == brickGrid[i][k].color)
						{
							//Increment length of the score row
							sameColorLength++;
							//Update the end column
							endColumn = k;
						}
						else//We have either encountered a brick with another color or a null cell
						{
							//We have reached the end of the score row
							break;
						}
					}
					
					//If the score row is long enough
					if(sameColorLength >= NUM_BRICKS_FOR_SCORE)
					{
						//Add the score row
						scoreRows.add(new ScoreRow(new GridPos(i, startColumn), new GridPos(i, endColumn)));
					}
					
					//We can continue searching for score rows beyond the score row we have found
					j = endColumn;
				}
			}
		}
		
		//Search for vertical score rows
		for(int i = 0; i < brickGrid[0].length; i++)
		{
			//Loop through the column
			for(int j = 0; j < brickGrid.length; j++)
			{
				//If we have a non-null brick
				if(brickGrid[j][i] != null)
				{
					sameColorLength = 1;
					startRow = j;
					endRow = j;
					//Check if there are bricks of the same color next to it
					for(int k = j + 1; k < brickGrid.length; k++)
					{
						//If we have a brick with the same color
						if(brickGrid[k][i] != null && brickGrid[j][i].color == brickGrid[k][i].color)
						{
							//Increment length of the score row
							sameColorLength++;
							//Update the end row
							endRow = k;
						}
						else//We have either encountered a brick with another color or a null cell
						{
							//We have reached the end of the score row
							break;
						}
					}
					
					//If the score row is long enough
					if(sameColorLength >= NUM_BRICKS_FOR_SCORE)
					{
						//Add the score row
						scoreRows.add(new ScoreRow(new GridPos(startRow, i), new GridPos(endRow, i)));
					}
					
					//We can continue searching for score rows beyond the score row we have found
					j = endRow;
				}
			}
		}
		
		//Search for diagonal score rows
		scoreRows.addAll(getDiagonalScoreRows(brickGrid));
		
		return scoreRows;
	}
	
	private ArrayList<ScoreRow> getDiagonalScoreRows(Brick[][] brickGrid)
	{
		ArrayList<ScoreRow> scoreRows = new ArrayList<ScoreRow>();
		
		int sameColorLength, curCol, curRow;
		GridPos startPos = new GridPos(), endPos = new GridPos();
		boolean onScoreRow;
		
		for(int i = brickGrid.length - 1; i >= 0; i--)
		{
			for(int j = 0; j < brickGrid[0].length; j++)
			{
				if(brickGrid[i][j] != null)
				{	
					//Try to make a score row that goes up-left
					//  0  1  2  3 
					//0[x][x][x][x]
					//1[x][x][x][x]
					//2[i][x][x][x]
					//3[x][i][x][x]
					//4[x][x][i][x]
					
					onScoreRow = true;
					
					//Save the start point for the score row
					startPos.row = i;
					startPos.column = j;
					
					curRow = i;
					curCol = j;
					
					sameColorLength = 1;
					
					do
					{
						//Go up-left
						curRow--;
						curCol--;
	
						//If we have found a brick of the same color
						if(curRow >= 0 && curCol >= 0 && 
						   brickGrid[curRow][curCol] != null &&
						   brickGrid[i][j].color == brickGrid[curRow][curCol].color)
						{	
							//Increment length of the score row
							sameColorLength++;
							//Update the end point of the score row
							endPos.row = curRow;
							endPos.column = curCol;
						}
						else//We are either outside the grid, has moved to a cell that is null or to a brick that have another color 
						{
							onScoreRow = false;
						}
					}
					while(onScoreRow);
					
					//If the score row is long enough
					if(sameColorLength >= NUM_BRICKS_FOR_SCORE)
					{
						boolean overlapsOtherRows = false;
						
						ScoreRow newScoreRow = new ScoreRow(new GridPos(startPos), new GridPos(endPos));
						
						for(ScoreRow scoreRow : scoreRows)
						{
							if(newScoreRow.isPartOf(scoreRow))
							{
								overlapsOtherRows = true;
								break;
							}
						}
						
						//If the score row does not overlap any other score row, which would indicate that another longer score row containing this score row already exists 
						if(!overlapsOtherRows)
						{
							//Add the score row
							scoreRows.add(newScoreRow);
						}
					}
					
					//Try to make a score row that goes up-right
					//  0  1  2  3 
					//0[x][x][x][x]
					//1[x][x][x][x]
					//2[x][x][x][i]
					//3[x][x][i][x]
					//4[x][i][x][x]
					
					onScoreRow = true;
					
					//Save the start point for the score row
					startPos.row = i;
					startPos.column = j;
					
					curRow = i;
					curCol = j;
					
					sameColorLength = 1;
					
					do
					{
						//Go up-right
						curRow--;
						curCol++;
	
						//If we have found a brick of the same color
						if(curRow >= 0 && curCol < brickGrid[0].length && 
						   brickGrid[curRow][curCol] != null &&
						   brickGrid[i][j].color == brickGrid[curRow][curCol].color)
						{	
							//Increment length of the score row
							sameColorLength++;
							//Update the end point of the score row
							endPos.row = curRow;
							endPos.column = curCol;
						}
						else//We are either outside the grid, has moved to a cell that is null or to a brick that have another color 
						{
							onScoreRow = false;
						}
					}
					while(onScoreRow);
					
					//If the score row is long enough
					if(sameColorLength >= NUM_BRICKS_FOR_SCORE)
					{
						boolean overlapsOtherRows = false;
						
						ScoreRow newScoreRow = new ScoreRow(new GridPos(startPos), new GridPos(endPos));
						
						for(ScoreRow scoreRow : scoreRows)
						{
							if(newScoreRow.isPartOf(scoreRow))
							{
								overlapsOtherRows = true;
								break;
							}
						}
						
						//If the score row does not overlap any other score row, which would indicate that another longer score row containing this score row already exists 
						if(!overlapsOtherRows)
						{
							//Add the score row
							scoreRows.add(newScoreRow);
						}
					}
				}
			}
		}
		
		return scoreRows;
	}
	
	private void getFallingBricks(Brick[][] brickGrid, ArrayList<Brick> fallingBricks)
	{
		boolean firstEmptyCellFound;
		
		for(int i = 0; i < brickGrid[0].length; i++)
		{
			firstEmptyCellFound = false;
			
			for(int j = brickGrid.length - 1; j >= 0; j--)
			{
				if(brickGrid[j][i] == null)
				{
					firstEmptyCellFound = true;
				}
				else
				{
					//The brick we have found is not resting on anything!
					if(firstEmptyCellFound)
					{
						//Make the brick a falling brick
						fallingBricks.add(brickGrid[j][i]);
						//Remove it from the brick grid
						brickGrid[j][i] = null;
					}
				}
			}
		}
	}
	
	@Override
	public int calcScore(Brick[][] brickGrid, ArrayList<Brick> fallingBricks) 
	{
		int score = 0;
		
		//Get all current score rows
		ArrayList<ScoreRow> scoreRows = getScoreRows(brickGrid);
		
		//Clear all bricks that are part of score rows from the brick grid
		for(ScoreRow row : scoreRows)
		{
			//Get the score of the score row
			score += row.clearScoreRow(brickGrid);
			//Add the bricks in score row to the brick count
			numBricksCleared += row.getNumBricks();
		}
		
		//Add a bonus score depending on the number of score rows we have cleared
		score += ((scoreRows.size() - 1) < 0 ? 0 : (scoreRows.size() - 1)) * BONUS_FOR_EXTRA_ROWS;
		
		//Get all bricks that are not on top of anything now and make them falling bricks
		getFallingBricks(brickGrid, fallingBricks);
		
		//Return the accumulated score
		return score;
	}
	
	public int calcDropToFloorScore()
	{
		//Just give a constant amount of points
		return SCORE_FOR_DROP_TO_FLOOR;
	}

}
