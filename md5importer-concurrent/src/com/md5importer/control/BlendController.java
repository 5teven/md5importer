package com.md5importer.control;

import java.util.concurrent.locks.ReentrantLock;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.md5importer.interfaces.control.IBlendController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.interfaces.model.anim.IFrame;
import com.md5importer.interfaces.model.mesh.IJoint;

/**
 * <code>BlendController</code> defines the concrete implementation
 * of a controller unit that provides the functionality to blend
 * the maintained <code>IMD5Node</code> from its current skeleton
 * position to the first frame of a given <code>IMD5Anim</code>.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-25-2009 18:18 EST
 * @version Modified date: 05-10-2009 21:24 EST
 */
public class BlendController extends AbstractController implements IBlendController {
	/**
	 * The <code>IMD5Node</code> instance.
	 */
	private final IMD5Node node;
	/**
	 * The array of <code>IJoint</code> in the node.
	 */
	private final IJoint[] joints;
	/**
	 * The <code>IMD5NodeController</code> for the node.
	 */
	private final IMD5NodeController nodeController;
	/**
	 * The update <code>ReentrantLock</code>.
	 */
	private final ReentrantLock lock;
	/**
	 * The recorded array of <code>Vector3f</code> of joint translations.
	 */
	private final Vector3f[] recordTrans;
	/**
	 * The recorded array of <code>Quaternion</code> of joint orientations.
	 */
	private final Quaternion[] recordOriens;
	/**
	 * The <code>Vector3f</code> temporary translation.
	 */
	private final Vector3f translation;
	/**
	 * The <code>Quaternion</code> temporary orientation.
	 */
	private final Quaternion orientation;
	/**
	 * The target <code>IFrame</code>.
	 */
	private volatile IFrame frame;
	/**
	 * The <code>IMD5AnimController</code> for target animation.
	 */
	private volatile IMD5AnimController animController;
	/**
	 * The <code>Float</code> blending duration value in seconds.
	 */
	private volatile float duration;
	/**
	 * The <code>Float</code> elapsed time.
	 */
	private volatile float time;
	/**
	 * The complete <code>Boolean</code> flag.
	 */
	private volatile boolean completed;
	
	/**
	 * Constructor of <code>BlendController</code>.
	 * @param node The <code>IMD5Node</code> to blend.
	 * @param controller The <code>IMD5NodeController</code> for the node.
	 */
	public BlendController(IMD5Node node, IMD5NodeController controller) {
		this.node = node;
		this.joints = node.getJoints();
		this.nodeController = controller;
		this.lock = new ReentrantLock();
		this.recordTrans = new Vector3f[node.getJoints().length];
		for(int i = 0; i < this.recordTrans.length; i++) this.recordTrans[i] = new Vector3f();
		this.recordOriens = new Quaternion[node.getJoints().length];
		for(int i = 0; i < this.recordOriens.length; i++) this.recordOriens[i] = new Quaternion();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
		// Default to be completed.
		this.completed = true;
	}

	@Override
	public void update(float interpolation) {
		if(this.completed) return;
		// Lock before update.
		this.lock.lock();
		try {
			// Update interpolation based on elapsed time.
			this.time += interpolation;
			interpolation = this.time / this.duration;
			// Update joints and meshes.
			this.updateJoints(interpolation);
			this.node.updateMeshes();
			// Check for completion.
			if(interpolation >= 1) {
				this.completed = true;
				this.animController.setActive(true);
			}
		} finally  {
			this.lock.unlock();
		}
	}
	
	/**
	 * Update the skeleton joints.
	 * @param interpolation The <code>Float</code> interpolation.
	 */
	private void updateJoints(float interpolation) {
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(this.recordTrans[i], this.frame.getTranslation(i), interpolation);
			this.orientation.slerp(this.recordOriens[i], this.frame.getOrientation(i), interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
			this.joints[i].processRelative();
		}
	}
	
	@Override
	public void blend(IMD5Anim anim, IMD5AnimController animController, float duration) {
		// Lock before set to block update thread.
		this.lock.lock();
		try {
			// Reset animation controller.
			animController.reset();
			// Store arguments.
			this.frame = anim.getPreviousFrame();
			this.animController = animController;
			this.duration = Math.abs(duration);
			// Set given animation as active animation on node.
			this.nodeController.setActiveAnim(anim);
			// Shut off animation controller.
			this.animController.setActive(false);
			// Record current skeleton position.
			for(int i = 0; i < this.joints.length; i++) {
				this.recordTrans[i].set(this.joints[i].getTranslation());
				this.recordOriens[i].set(this.joints[i].getOrientation());
			}
			// Reset variables.
			this.time = 0;
			this.completed = false;
		} finally {
			this.lock.unlock();
		}
	}
}
