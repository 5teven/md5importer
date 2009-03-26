package com.md5importer.interfaces.control;

import com.md5importer.interfaces.model.IMD5Anim;

/**
 * <code>IBlendController</code> defines the interface of a logic
 * control unit that is responsible for blending its maintained
 * <code>IMD5Node</code> from its current skeleton position to
 * the first frame of the given target <code>IMD5Anim</code>.
 * <p>
 * <code>IBlendController</code> requires external invocations
 * to perform the update operation that blends maintained node
 * toward the given animation.
 * <p>
 * <code>IBlendController</code> takes in an <code>IMD5Anim</code>
 * to blend to, the <code>IController</code> that controls the
 * given target animation and a blend duration. It internally
 * sets the given animation as the active animation for the node
 * and deactivates the animation controller to prevent the node
 * being updated by the animation during the blending process.
 * It then activates the controller back up again after the process
 * is completed.
 * <p>
 * <code>IBlendController</code> does not operate on the previous
 * active animation of the maintained node. However, it is advised
 * to deactivate the <code>IMD5AnimController</code> of the previous
 * active animation to save CPU time if the animation is not shared
 * by other <code>IMD5Node</code> instances.
 * <p>
 * <code>IBlendController</code> uses <code>ReentrantLock</code>
 * to provide thread safety on the <code>blend</code> method. This
 * prevents the recording of the current skeleton position before
 * blending produce incorrect information. The same lock is also
 * used for the <code>update</code> method to prevent interleave
 * thread invocations when the data is being recorded. However, it
 * is advised to only invoke <code>update</code> method within a
 * single thread.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-25-2009 17:16 EST
 * @version Modified date: 03-25-2009 21:15 EST
 */
public interface IBlendController extends IController {
	
	/**
	 * Blend the maintained node from its current skeleton position
	 * to the first frame of the given animation in given duration.
	 * @param anim The <code>IMD5Anim</code> to blend to.
	 * @param animController The <code>IMD5AnimController</code> of the target animation.
	 * @param duration The <code>Float</code> blending duration.
	 */
	public void blend(IMD5Anim anim, IMD5AnimController animController, float duration);
}
