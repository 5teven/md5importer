package com.model.md5.interfaces;

import java.util.Iterator;

import com.jme.util.export.Savable;

/**
 * <code>IMD5Controller</code> defines the interface of a controller unit
 * that is responsible for controlling the <code>IMD5Node</code> with its
 * <code>IMD5Animation</code>.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-17-2008 23:01 EST
 * @version Modified date: 02-17-2009 11:13 EST
 */
public interface IMD5Controller extends Savable {
	
	/**
	 * Update the controller.
	 * @param interpolation The <code>Float</code> frame interpolation.
	 */
	public void update(float interpolation);

	/**
	 * Add a new animation to this controller. The new animation set it to be
	 * the active animation if currently there is no active animation.
	 * @param animation The <code>IMD5Animation</code> to be added.
	 */
	public void addAnimation(IMD5Animation animation);
	
	/**
	 * Fade from the current active animation to the given animation.
	 * @param name The name of the<code>IMD5Animation</code> to be faded into.
	 * @param duration The <code>Float</code> fading duration in seconds.
	 * @param scale True if fading duration should scale with controller speed.
	 */
	public void fadeTo(String name, float duration, boolean scale);
	
	/**
	 * Fade from the current active animation to the given animation.
	 * @param animation The <code>IMD5Animation</code> to be faded into.
	 * @param duration The <code>Float</code> fading duration in seconds.
	 * @param scale True if fading duration should scale with controller speed.
	 */
	public void fadeTo(IMD5Animation animation, float duration, boolean scale);
	
	/**
	 * Set the repeat type of the animations on this controller.
	 * @param type The <code>Integer</code> repeat type.
	 */
	public void setRepeatType(int type);
	
	/**
	 * Set the speed of this controller.
	 * @param speed The <code>Float</code> speed.
	 */
	public void setSpeed(float speed);
	
	/**
	 * Activate or deactivate this controller.
	 * @param active True if activate. False deactivate.
	 */
	public void setActive(boolean active);
	
	/**
	 * Retrieve the current active animation.
	 * @return The active <code>IMD5Animation</code>.
	 */
	public IMD5Animation getActiveAnimation();
	
	/**
	 * Retrieve the iterator of the maintained animations.
	 * @return The <code>Iterator</code> of <code>IMD5Animation</code>.
	 */
	public Iterator<IMD5Animation> getAnimations();
}
