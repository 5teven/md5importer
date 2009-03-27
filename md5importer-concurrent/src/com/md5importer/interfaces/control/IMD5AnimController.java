package com.md5importer.interfaces.control;

import com.md5importer.enumn.ERepeatType;
import com.md5importer.interfaces.model.IMD5Anim;

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
 * <code>IMD5AnimController</code> requires external invocations
 * to the <code>update</code> method to perform animation update
 * operation. This operation updates the maintained animation.
 * <p>
 * <code>IMD5AnimController</code> only provides primitive thread
 * safety. It guarantees the memory visibility of the most recent
 * set operation results on both update and retrieval methods.
 * However, there is no actual locking performed to allow maximum
 * concurrency. This implies that the <code>update</code> method
 * has to be confined within a single updating thread. In reality,
 * it does not make any sense to update the same animation with
 * multiple threads anyways.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 17:51 EST
 * @version Modified date: 03-27-2009 19:20 EST
 */
public interface IMD5AnimController extends IController {

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
	 * Retrieve the animation this controller controls.
	 * @return The controlled <code>IMD5Anim</code>.
	 */
	public IMD5Anim getAnim();
	
	/**
	 * Reset the controller by resetting the accumulated time to
	 * zero and resetting the animation to the first frame.
	 */
	public void reset();

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
