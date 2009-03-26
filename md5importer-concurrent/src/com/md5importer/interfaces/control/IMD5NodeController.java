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
 * is modified.
 * <p>
 * <code>IMD5NodeController</code> only provides primitive level of
 * thread safety meaning that it only guarantees the visibility of
 * the newly set active animation. There is no locking performed on
 * any of the methods to allow maximum concurrency. However it is
 * advised to only invoke <code>update</code> within a single thread.
 * The <code>getActiveAnim</code> is thread safe in the sense
 * that it always reflects the most recently set active animation.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:21 EST
 * @version Modified date: 03-25-2009 18:46 EST
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
