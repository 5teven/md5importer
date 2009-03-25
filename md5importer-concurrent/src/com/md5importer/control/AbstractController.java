package com.md5importer.control;

import com.md5importer.interfaces.control.IController;

/**
 * <code>AbstractController</code> defines the basic abstraction of
 * a controller implementation. It only provides the most basic
 * shared logic of all controllers.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 18:17 EST
 * @version Modified date: 03-24-2009 22:30 EST
 */
public abstract class AbstractController implements IController {
	/**
	 * The <code>Boolean</code> activeness flag.
	 */
	protected volatile boolean active;
	
	/**
	 * Constructor of <code>AbstractController</code>.
	 */
	protected AbstractController() {
		// Default is active.
		this.active = true;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isActive() {
		return this.active;
	}
}
