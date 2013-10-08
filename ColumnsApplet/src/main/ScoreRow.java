package main;

import java.awt.geom.Line2D;

import utils.MathToolbox;

public class ScoreRow {
	private GridPos startPos, endPos;
	
	public ScoreRow(GridPos startPos, GridPos endPos)
	{
		this.startPos = startPos;
		this.endPos = endPos;
	}
	
	public int getNumBricks()
	{
		int i, j;
		int brickCount = 0;
		boolean reachedEndPos = false;
		int xDir, yDir;
		
		//Get the direction of the score row
		xDir = Math.abs(endPos.column - startPos.column) > 0 ? (endPos.column - startPos.column) / Math.abs(endPos.column - startPos.column) : 0;
		yDir = Math.abs(endPos.row - startPos.row) > 0 ? (endPos.row - startPos.row) / Math.abs(endPos.row - startPos.row) : 0;
		
		//Start looping through the score row at the start position
		i = startPos.row;
		j = startPos.column;
		
		do
		{	
			//If we have reached the end of the score row
			if(i == endPos.row && j == endPos.column)
			{
				reachedEndPos = true;
			}
			
			brickCount++;
			
			i += yDir;
			j += xDir;
		}
		while(!reachedEndPos);
		
		return brickCount;
	}
	
	public int clearScoreRow(Brick[][] brickGrid)
	{
		int i, j;
		int brickCount = 0;
		boolean reachedEndPos = false;
		int xDir, yDir;
		
		//Clear the score row from the brick grid
		
		//Get the direction of the score row
		xDir = Math.abs(endPos.column - startPos.column) > 0 ? (endPos.column - startPos.column) / Math.abs(endPos.column - startPos.column) : 0;
		yDir = Math.abs(endPos.row - startPos.row) > 0 ? (endPos.row - startPos.row) / Math.abs(endPos.row - startPos.row) : 0;
		
		//Start looping through the score row at the start position
		i = startPos.row;
		j = startPos.column;
		
		do
		{	
			//If we have reached the end of the score row
			if(i == endPos.row && j == endPos.column)
			{
				reachedEndPos = true;
			}
			
			brickGrid[i][j] = null;
			brickCount++;
			
			i += yDir;
			j += xDir;
		}
		while(!reachedEndPos);
		
		boolean diagonalRow = startPos.row != endPos.row && startPos.column != endPos.column;	
		
		//Play a sound to indicate that a score row has been cleared
		Columns.soundPlayer.playClip("cleared_brick_" + brickCount);
		
		//Return the score of the row
		if(brickCount == 3)
		{
			if(diagonalRow)
			{
				return ColumnsScoreKeeper.SCORE_DIAG_3_ROW;
			}
			else
			{
				return ColumnsScoreKeeper.SCORE_3_ROW;
			}
		}
		else if(brickCount == 4)
		{
			if(diagonalRow)
			{
				return ColumnsScoreKeeper.SCORE_DIAG_4_ROW;
			}
			else
			{
				return ColumnsScoreKeeper.SCORE_4_ROW;
			}
		}
		else
		{
			if(diagonalRow)
			{
				return ColumnsScoreKeeper.SCORE_DIAG_5_ROW;
			}
			else
			{
				return ColumnsScoreKeeper.SCORE_5_ROW;
			}
		}
	}

	/***
	 * Check if the score row intersects another score row
	 * @param otherScoreRow The other score row
	 * @return If the score rows intersect
	 */
	public boolean checkForIntersection(ScoreRow otherScoreRow)
	{	
		//Hanterar denna pararella linjesegment som �verlappar?
		return Line2D.linesIntersect(startPos.column, startPos.row, 
							  endPos.column, endPos.row, 
							  otherScoreRow.getStartPos().column, otherScoreRow.getStartPos().row, 
							  otherScoreRow.getEndPos().column, otherScoreRow.getEndPos().row);
	}
	
	public boolean isPartOf(ScoreRow otherScoreRow)
	{
		//If the score rows does not intersect they can not overlap
		if(!checkForIntersection(otherScoreRow))
		{
			return false;
		}
		
		//Create a 2D line with the other score row
		Line2D line = new Line2D.Float(otherScoreRow.getStartPos().column, otherScoreRow.getStartPos().row, 
				  						otherScoreRow.getEndPos().column, otherScoreRow.getEndPos().row);
		
		//Check if any of the score row's end points is on the line
		return MathToolbox.isZero(line.ptLineDist(startPos.column, startPos.row)) &&
			   MathToolbox.isZero(line.ptLineDist(endPos.column, endPos.row));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endPos == null) ? 0 : endPos.hashCode());
		result = prime * result
				+ ((startPos == null) ? 0 : startPos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScoreRow other = (ScoreRow) obj;
		if (endPos == null) {
			if (other.getEndPos() != null)
				return false;
		} else if (!endPos.equals(other.getEndPos()))
			return false;
		if (startPos == null) {
			if (other.getStartPos() != null)
				return false;
		} else if (!startPos.equals(other.getStartPos()))
			return false;
		return true;
	}

	/**
	 * @return the startPos
	 */
	public GridPos getStartPos() {
		return startPos;
	}

	/**
	 * @return the endPos
	 */
	public GridPos getEndPos() {
		return endPos;
	}
}
