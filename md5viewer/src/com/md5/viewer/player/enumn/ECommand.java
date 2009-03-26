package com.md5.viewer.player.enumn;

import com.jme.input.KeyInput;

/**
 * <code>ECommand</code> defines the enumerations of all control commands that
 * can be performed by the player.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-25-2008 00:09 EST
 * @version Modified date: 11-25-2008 00:15 EST
 */
public enum ECommand {
	/**
	 * The command to increase the play back speed.
	 */
	IncreaseSpeed(KeyInput.KEY_EQUALS),
	/**
	 * The command to decrease the play back speed.
	 */
	DecreaseSpeed(KeyInput.KEY_MINUS),
	/**
	 * The command to set the camera view to front.
	 */
	CameraFont(KeyInput.KEY_1),
	/**
	 * The command to set the camera view to side.
	 */
	CameraSide(KeyInput.KEY_2),
	/**
	 * The command to set the camera view to perspective.
	 */
	CameraPerspective(KeyInput.KEY_3),
	/**
	 * The command to increment the manual animation play.
	 */
	IncrementAnimation(KeyInput.KEY_SPACE),
	/**
	 * The command to reset the manual animation play.
	 */
	ResetAnimation(KeyInput.KEY_BACK);
	
	/**
	 * The <code>Integer</code> key code used for the command.
	 */
	private final int keyCode;
	
	/**
	 * Constructor of <code>ECommand</code>.
	 * @param keyCode The <code>Integer</code> key code used for the command.
	 */
	private ECommand(int keyCode) {
		this.keyCode = keyCode;
	}
	
	/**
	 * Retrieve the key code of the command.
	 * @return The <code>Integer</code> key code used for the command.
	 */
	public int getKeyCode() {
		return this.keyCode;
	}
}
