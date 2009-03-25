package com.md5importer.interfaces.control;

import com.md5importer.enumn.ERepeatType;

/**
 * <code>IMD5AnimController</code> defines the interface of a logic
 * controller unit that is responsible for updating the given
 * <code>IMD5Anim</code> instance based on given time interpolation.
 * <p>
 * <code>IMD5AnimController</code> contains the logic to update the
 * frame indices of a <code>IMD5Anim</code> given at construction
 * time. It updates the next and previous frame indices based on the
 * given frame time interpolation value, the repeat type and the
 * speed value of the controller.
 * <p>
 * <code>IMD5AnimController</code> only provides primitive level of
 * thread safety. It guarantees the memory visibility of the most
 * recent set operations. However, there is no actual locking
 * performed on any of the set methods to allow maximum concurrency.
 * <code>IMD5NodeController</code> does not perform any locking on
 * the update operation either. It assumes the update operation is
 * only invoked in a single thread. This design decision is made to
 * reduce locking overhead. In almost all cases, it is always best
 * to invoke the update operation within a single thread.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 17:51 EST
 * @version Modified date: 03-24-2009 22:34 EST
 */
public interface IMD5AnimController extends IController {

	/**
	 * Update the animation controller to update the animation frames.
	 * @param interpolation The <code>Float</code> time interpolation.
	 */
	public void update(float interpolation);

	/**
	 * Set the repeat type of this controller.
	 * @param type The <code>ERepeatType</code> value.
	 */
	public void setRepeatType(ERepeatType type);

	/**
	 * Set the speed of this controller.
	 * @param speed The <code>Float</code> speed.
	 */
	public void setSpeed(float speed);

	/**
	 * Retrieve the current repeat type.
	 * @return The <code>ERepeatType</code> enumeration.
	 */
	public ERepeatType getRepeatType();

	/**
	 * Check if this animation is being played backward.
	 * @return True if the animation is being played backward. False forward.
	 */
	public boolean isBackward();

	/**
	 * Check if one cycle of this animation is complete, but the new one has not yet started.
	 * @return True if one cycle is complete. False otherwise.
	 */
	public boolean isCyleComplete();
}
