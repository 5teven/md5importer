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
 * method is invoked. This implied that the controller itself does
 * not require any external update invocation. It automatically
 * performs the synchronization operation whenever the animation
 * is modified. In fact <code>IMD5NodeController</code> does not
 * respond to <code>update</code> invocation.
 * <p>
 * <code>IMD5NodeController</code> uses <code>ReentrantLock</code>
 * to guard the observer update method to prevent interleaving
 * invocations from multiple animation updating threads. Although,
 * it is advised to update all the animations associated with the
 * controlled node in a single thread. This lock guarantees that
 * the mesh is updated based on a single animation at once. There
 * is no locking performed on <code>setActiveAnim</code> to allow
 * maximum concurrency. It does provide memory visibility. 
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:21 EST
 * @version Modified date: 03-27-2009 17:55 EST
 */
public interface IMD5NodeController extends IController, IObserver {
	
	/**
	 * Set the active animation.
	 * <p>
	 * This invocation registers this controller as an observer to the
	 * given animation observable unit and unregisters itself from the
	 * previous active animation.
	 * @param anim The <code>IMD5Anim</code> to be set.
	 */
	public void setActiveAnim(IMD5Anim anim);
	
	/**
	 * Retrieve the current active animation.
	 * @return The current active <code>IMD5Anim</code>.
	 */
	public IMD5Anim getActiveAnim();
}
