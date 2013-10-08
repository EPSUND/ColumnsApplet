package usrinput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyControl extends KeyAdapter {
	private boolean moveLeft, moveRight, moveDown, dropToFloor, rotate;
	
	public KeyControl() {
		moveLeft = false;
		moveRight = false;
		moveDown = false;
		dropToFloor = false;
		rotate = false;
	}
	
	public boolean getRotate()
	{
		return rotate;
	}
	
	public void resetDropToFloor()
	{
		dropToFloor = false;
	}
	
	public boolean getDropToFloor()
	{
		return dropToFloor;
	}
	
	public boolean getMoveDown()
	{
		return moveDown;
	}
	
	public boolean getMoveLeft()
	{
		return moveLeft;
	}
	
	public boolean getMoveRight()
	{
		return moveRight;
	}
	
	public void keyReleased(KeyEvent ke)
	{
		//Get the released key and act accordingly
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			moveLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = false;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = false;
			break;
		case KeyEvent.VK_UP:
			rotate = false;
			break;
		//case KeyEvent.VK_SPACE:
		//	dropToFloor = false;
		//	break;
		}
	}
	
	/**
	 * keyPressed
	 * Handles the keyPressed event of the keyboard.
	 * @param ke KeyEvent that occured
	 */
	public void keyPressed(KeyEvent ke) {
		//Get the pressed key and act accordingly
		switch (ke.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			moveLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = true;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = true;
			break;
		case KeyEvent.VK_UP:
			rotate = true;
			break;
		case KeyEvent.VK_SPACE:
			dropToFloor = true;
			break;
		
			//case KeyEvent.VK_D :
			//	if(ke.isShiftDown())
			//		player.setDebugMode(!player.getDebugMode());
			//	break;
			//case KeyEvent.VK_S :
			//	if(player.getDebugMode())
			//		player.setStepFlag(true);
			//	break;
			//case KeyEvent.VK_L :
			//	if(player.getDebugMode())
			//		player.setLevelCompleted(true);
			//	break;
		}
	}
}
