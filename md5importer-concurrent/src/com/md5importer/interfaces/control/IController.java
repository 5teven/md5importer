package com.md5importer.interfaces.control;

/**
 * <code>IController</code> defines the common interface shared by
 * all controller units. It only defines the most basic shared
 * functionalities for all controllers.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 18:00 EST
 * @version Modified date: 03-23-2009 18:02 EST
 */
public interface IController {

	/**
	 * Set the activeness of this controller.
	 * @param active The <code>Boolean</code> activeness flag.
	 */
	public void setActive(boolean active);
	
	/**
	 * Check if this controller is active.
	 * @return The <code>Boolean</code> activeness flag.
	 */
	public boolean isActive();
}
