package com.md5importer.control;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.md5importer.interfaces.IObservable;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.interfaces.model.anim.IFrame;
import com.md5importer.interfaces.model.mesh.IJoint;
import com.md5importer.interfaces.model.mesh.IMesh;

/**
 * <code>MD5NodeController</code> defines the concrete implementation
 * of a concurrent controller unit that is responsible for updating
 * the <code>IMD5Node</code> given at construction time with active
 * <code>IMD5Anim</code>.
 * <p>
 * <code>MD5NodeController</code> uses lazy initialization on the
 * temporary blending records since the blending utility may never
 * be used.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:13 EST
 * @version Modified date: 03-25-2009 18:44 EST
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
	 * The array of <code>IMesh</code> in the node.
	 */
	private final IMesh[] meshes;
	/**
	 * The <code>Vector3f</code> temporary translation.
	 */
	private final Vector3f translation;
	/**
	 * The <code>Quaternion</code> temporary orientation.
	 */
	private final Quaternion orientation;
	/**
	 * The current active <code>IMD5Anim</code>.
	 */
	private volatile IMD5Anim activeAnim;

	/**
	 * Constructor of <code>MD5NodeController</code>.
	 * @param node The <code>IMD5Node</code> to control.
	 */
	public MD5NodeController(IMD5Node node) {
		this.node = node;
		this.joints = node.getJoints();
		this.meshes = node.getMeshes();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}

	@Override
	public void update(float interpolation) {}

	@Override
	public void update(IObservable observable) {
		if(observable == null || !this.active) return;
		IMD5Anim anim = (IMD5Anim)observable;
		this.updateJoints(this.interpolation(anim), anim.getPreviousFrame(), anim.getNextFrame());
		this.updateMeshes();
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
			this.joints[i].processRelative();
		}
	}

	/**
	 * Update the geometric information of all meshes in the node.
	 */
	private void updateMeshes() {
		// Update mesh geometric information.
		for(IMesh mesh : this.meshes) mesh.updateMesh();
		// Update dependent children.
		final Iterable<IMD5Node> children = this.node.getDependents();
		for(IMD5Node child : children) {
			final IMesh[] meshes = child.getMeshes();
			for(final IMesh mesh : meshes) mesh.updateMesh();
		}
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
	public void setActiveAnim(IMD5Anim anim) {
		if(anim == null) return;
		// Validate animation first.
		if(!this.validateAnim(anim)) throw new IllegalArgumentException("Invalid animation: " + anim.getName());
		// Unregister from the previous animation.
		if(this.activeAnim != null) this.activeAnim.unregister(this);
		// Record active animation does not require lock.
		this.activeAnim = anim;
		// Register this controller as observer.
		this.activeAnim.register(this);
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

	@Override
	public IMD5Anim getActiveAnim() {
		return this.activeAnim;
	}
}
