package com.md5importer.interfaces.model;

import com.md5importer.interfaces.IObservable;
import com.md5importer.interfaces.model.anim.IFrame;

/**
 * <code>IMD5Anim</code> defines the interface of a data structure
 * that represents a completely loaded MD5 animation unit. It
 * internally utilizes <code>IFrame</code> instances to actually
 * maintain the joint position data.
 * <p>
 * <code>IMD5Anim</code> operates on the previous and next frame
 * indices set by external controller unit. It provide various
 * retrieval methods related to frames based on current set indices.
 * <p>
 * <code>IMD5Anim</code> is defined as an observable unit that can
 * be monitored by various <code>IObserver</code> instances. These
 * observers should be notified when the frame indices of the
 * animation are modified through the <code>setIndices</code> method.
 * This implies that the <code>notifyUpdate</code> method should be
 * invoked after the indices have been set completely in an update
 * cycle.
 * <p>
 * <code>IMD5Anim</code> does not perform any locking on any of the
 * retrieval methods. However, it does provide thread safety on the
 * <code>setIndices</code> method by using <code>ReentrantLock</code>.
 * This guarantees the compound action for setting the previous and
 * next frame indices is performed atomically. 
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 16:17 EST
 * @version Modified date: 03-24-2009 22:15 EST
 */
public interface IMD5Anim extends IObservable {
	
	/**
	 * Set the frame indices.
	 * @param prev The <code>Integer</code> previous frame index.
	 * @param next The <code>Integer</code> next frame index.
	 * @param time The The <code>Float</code> elapsed time since last set.
	 */
	public void setIndices(int prev, int next, float time);
	
	/**
	 * Retrieve the total time of a complete cycle of this animation.
	 * @return The <code>Float</code> complete cycle time.
	 */
	public float getAnimationTime();
	
	/**
	 * Retrieve the total number of frames.
	 * @return The <code>Integer</code> number of frames.
	 */
	public int getFrameCount();
	
	/**
	 * Get the complete percentage of the current cycle.
	 * @return The <code>Float</code> percentage value.
	 */
	public float getPercentage();

	/**
	 * Retrieve the previous frame.
	 * @return The previous <code>IFrame</code>.
	 */
	public IFrame getPreviousFrame();
	
	/**
	 * Retrieve the previous frame index number.
	 * @return The <code>Integer</code> index number.
	 */
	public int getPreviousIndex();
	
	/**
	 * Retrieve the starting time of the previous frame.
	 * @return The <code>Float</code> starting time.
	 */
	public float getPreviousTime();
	
	/**
	 * Retrieve the next frame.
	 * @return The next <code>IFrame</code>.
	 */
	public IFrame getNextFrame();
	
	/**
	 * Retrieve the next frame index number.
	 * @return The <code>Integer</code> index number.
	 */
	public int getNextIndex();
	
	/**
	 * Retrieve the starting time of the next frame.
	 * @return The <code>Float</code> starting time.
	 */
	public float getNextTime();
	
	/**
	 * Retrieve the IDs of joints of this animation.
	 * @return The array of <code>String</code> IDs.
	 */
	public String[] getJointIDs();
	
	/**
	 * Retrieve the name of this animation.
	 * @return The <code>String</code> name.
	 */
	public String getName();
	
	/**
	 * Retrieve the elapsed time since last frame change.
	 * @return The <code>Float</code> time value.
	 */
	public float getTime();
	
	/**
	 * Clone this animation.
	 * @return The cloned <code>IMD5Anim</code> instance.
	 */
	public IMD5Anim clone();
}
