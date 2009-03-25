package com.md5importer.interfaces.control;

import com.md5importer.interfaces.IObserver;
import com.md5importer.interfaces.model.IMD5Anim;

/**
 * <code>IMD5NodeController</code> defines the interface of the
 * concurrent controller unit that is responsible for synchronizing
 * the <code>IMD5Node</code> with active <code>IMD5Anim</code> to
 * perform CPU skinning.
 * <p>
 * <code>IMD5NodeController</code> contains the logic to properly
 * update the <code>IJoint</code> of the <code>IMD5Node</code> based
 * on the active <code>IMD5Anim</code>. It manipulates the joint
 * positions based on the interpolated value between the current
 * positions and the animation frame positions. The interpolation is
 * performed using the current time value of the active animation.
 * The actual skinning logic is maintained by the mesh primitives.
 * <p>
 * <code>IMD5NodeController</code> is defined as an observer unit
 * that monitors the current active animation set on the node. It
 * is notified whenever the frames of the current active animation
 * are modified. This allows the controller to synchronize the
 * controlling node with the active animation.
 * <p>
 * <code>IMD5NodeController</code> is registered as an observer to
 * the given <code>IMD5Anim</code> when <code>setActiveAnim</code>
 * method is invoked.
 * <p>
 * <code>IMD5NodeController</code> uses <code>ReentrantLock</code>
 * for both the <code>update</code> and <code>setActiveAnim</code>
 * methods to guarantee the update is not performed partially with
 * old information and recording for blending information during set
 * does not record partially updated joint information. This also
 * guarantees the thread safety of <code>update</code> method if
 * more than one thread invokes <code>update</code>. However it is
 * advised to only invoke <code>update</code> within a single thread.
 * Since the <code>setActiveAnim</code> method is guarded by a
 * lock, the <code>getActiveAnim</code> is thread safe in the sense
 * that it always reflects the most recently set active animation.
 * However, there is no locking performed that method, which allows
 * maximum concurrency.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:21 EST
 * @version Modified date: 03-25-2009 10:47 EST
 */
public interface IMD5NodeController extends IController, IObserver {
	
	/**
	 * Set the active animation with given parameters. the duration
	 * and scale values are ignored if the blend flag is set to false.
	 * This invocation registers this controller as an observer to the
	 * given animation observable unit and unregisters itself from the
	 * previous active animation.
	 * @param anim The <code>IMD5Anim</code> to be set.
	 * @param blend The blending <code>Boolean</code> flag.
	 * @param duration The <code>Float</code> blending duration.
	 */
	public void setActiveAnim(IMD5Anim anim, boolean blend, float duration);
	
	/**
	 * Retrieve the current active animation.
	 * @return The current active <code>IMD5Anim</code>.
	 */
	public IMD5Anim getActiveAnim();
}
