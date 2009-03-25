package com.md5importer.control;

import java.util.concurrent.locks.ReentrantLock;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.md5importer.interfaces.IObservable;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.interfaces.model.anim.IFrame;
import com.md5importer.interfaces.model.mesh.IJoint;

/**
 * <code>MD5NodeController</code> defines the concrete implementation
 * of a concurrent controller unit that is responsible for updating
 * the <code>IMD5Node</code> given at construction time with active
 * <code>IMD5Anim</code>.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:13 EST
 * @version Modified date: 03-24-2009 22:42 EST
 */
public class MD5NodeController extends AbstractController implements IMD5NodeController {
	/**
	 * The <code>IMD5Node</code> instance.
	 */
	private final IMD5Node node;
	/**
	 * The array of <code>IJoint</code> in the node.
	 */
	private final IJoint[] joints;
	/**
	 * The update <code>ReentrantLock</code>.
	 */
	private final ReentrantLock lock;
	/**
	 * The <code>Vector3f</code> temporary translation.
	 */
	private final Vector3f translation;
	/**
	 * The <code>Quaternion</code> temporary orientation.
	 */
	private final Quaternion orientation;
	/**
	 * The <code>Boolean</code> blending flag.
	 */
	private volatile boolean blending;
	/**
	 * The <code>Float</code> blending duration value in seconds.
	 */
	private volatile float duration;
	/**
	 * The array of <code>Vector3f</code> translations used for blending.
	 */
	private Vector3f[] translations;
	/**
	 * The array of <code>Quaternion</code> orientations used for blending.
	 */
	private Quaternion[] orientations;

	/**
	 * Constructor of <code>MD5NodeController</code>.
	 * @param node The <code>IMD5Node</code> to control.
	 */
	public MD5NodeController(IMD5Node node) {
		this.node = node;
		this.joints = this.node.getJoints();
		this.lock = new ReentrantLock();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}

	@Override
	public void update(IObservable observable) {
		if(observable == null || !this.active) return;
		IMD5Anim anim = (IMD5Anim)observable;
		// Lock to prevent setting invalid blending information by another thread.
		this.lock.lock();
		try {
			if(this.blending) this.updateBlending(anim.getTime()/this.duration, anim.getPreviousFrame());
			else this.updateJoints(this.interpolation(anim), anim.getPreviousFrame(), anim.getNextFrame());
		} finally {
			this.lock.unlock();
		}
	}

	/**
	 * Update the joints with blending based on given interpolation and frame.
	 * @param interpolation The <code>Float</code> update interpolation.
	 * @param prev The previous <code>IFrame</code> in the active animation.
	 */
	private void updateBlending(final float interpolation, final IFrame prev) {
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(this.translations[i], prev.getTranslation(i), interpolation);
			this.orientation.slerp(this.orientations[i], prev.getOrientation(i), interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
		if(interpolation >= 1) {
			// Lock is already acquired.
			this.blending = false;
			this.duration = 0;
		}
		this.node.flagUpdate();
	}

	/**
	 * Update the joints based on given interpolation and frame.
	 * @param interpolation The <code>Float</code> update interpolation.
	 * @param prev The previous <code>IFrame</code> in the active animation.
	 * @param next The next <code>IFrame</code> in the active animation.
	 */
	private void updateJoints(final float interpolation, final IFrame prev, final IFrame next) {
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(prev.getTranslation(i), next.getTranslation(i), interpolation);
			this.orientation.slerp(prev.getOrientation(i), next.getOrientation(i), interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
		this.node.flagUpdate();
	}

	/**
	 * Retrieve the update frame interpolation value based on the
	 * time value of given active animation.
	 * @param anim The active <code>IMD5Anim</code>.
	 * @return The <code>Float</code> interpolation value.
	 */
	private float interpolation(IMD5Anim anim) {
		// Calculate interpolation value.
		final float prev = anim.getPreviousTime();
		final float next = anim.getNextTime();
		if(prev == next) return 0.0f;
		float interpolation = (anim.getTime() - prev) / (next - prev);
		// Add 1 if it is playing backwards.
		if(anim.getNextIndex() < anim.getPreviousIndex()) interpolation = 1 + interpolation;
		// Return clamped result.
		if(interpolation < 0.0f) return 0.0f;
		else if (interpolation > 1.0f) return 1.0f;
		else return interpolation;
	}

	@Override
	public void setActiveAnim(IMD5Anim anim, boolean blend, float duration) {
		// Validate animation first.
		if(!this.validateAnim(anim)) throw new IllegalArgumentException("Invalid animation: " + anim.getName());
		// Lock if blending, since it needs to record concurrent joint formation.
		if(blend) {
			this.lock.lock();
			try {
				this.prepareBlending(duration);
			} finally {
				// Release lock before invoke external register method.
				this.lock.unlock();
			}
		}
		// Register this controller as observer.
		anim.register(this);
	}

	/**
	 * Validate the given animation with controlled skeleton.
	 * @param anim The <code>IMD5Anim</code> to be validated.
	 * @return The <code>Boolean</code> validity flag.
	 */
	private boolean validateAnim(IMD5Anim anim) {
		if(this.joints.length != anim.getJointIDs().length) return false;
		else {
			boolean result = true;
			for(int i = 0; i < this.joints.length && result; i++) {
				result = this.joints[i].getName().equals(anim.getJointIDs()[i]);
			}
			return result;
		}
	}

	/**
	 * Prepare the blending process by setting proper temporary blending values.
	 * @param duration The <code>Float</code> blending duration.
	 */
	private void prepareBlending(float duration) {
		// Store arguments.
		this.blending = true;
		this.duration = FastMath.abs(duration);
		// Prepare.
		if(this.translations == null && this.orientations == null) {
			this.translations = new Vector3f[this.joints.length];
			this.orientations = new Quaternion[this.joints.length];
			for(int i = 0; i < this.joints.length; i++) {
				this.translations[i] = this.joints[i].getTranslation().clone();
				this.orientations[i] = this.joints[i].getOrientation().clone();
			}
		} else {
			for(int i = 0; i < this.joints.length; i++) {
				this.translations[i].set(this.joints[i].getTranslation());
				this.orientations[i].set(this.joints[i].getOrientation());
			}
		}
	}
}
