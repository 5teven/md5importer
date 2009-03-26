package com.md5.viewer.selector.gui.enumn;

/**
 * <code>EButton</code> defines the enumerations of all the buttons utilized
 * by the <code>AnimationSelectorGUI</code>.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:55 EST
 * @version Modified date: 11-24-2008 13:03 EST
 */
public enum EButton {
	/**
	 * The button used to select the hierarchy file.
	 */
	SelectHierarchy("Open"),
	/**
	 * The button used to select the base animation file.
	 */
	SelectBaseAnimation("Open"),
	/**
	 * The button used to add animation into the chain.
	 */
	AddAnimation("Add"),
	/**
	 * The button used to remove animation from the chain.
	 */
	RemoveAnimation("Remove"),
	/**
	 * The OK button to start the player.
	 */
	OK("OK"),
	/**
	 * The cancel button to quit the application.
	 */
	Cancel("Cancel");
	
	/**
	 * Retrieve the button enumeration based on the given name.
	 * @param name The <code>String</code> name to check.
	 * @return The <code>EButton</code> enumeration.
	 */
	public static EButton getEnumn(String name) {
		for(EButton enumn : EButton.values()) {
			if(enumn.name().equals(name)) return enumn;
		}
		return null;
	}
	
	/**
	 * The <code>String</code> text to display.
	 */
	private final String text;
	
	/**
	 * Constructor of <code>EButton</code>.
	 * @param text The <code>String</code> text to display.
	 */
	private EButton(String text) {
		this.text = text;
	}
	
	/**
	 * Retrieve the text for the button to display.
	 * @return The <code>String</code> text to display.
	 */
	public String getText() {
		return this.text;
	}
}
