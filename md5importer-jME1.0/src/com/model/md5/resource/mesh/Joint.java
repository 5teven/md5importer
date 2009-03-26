package com.model.md5.resource.mesh;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.ModelNode;

/**
 * <code>Joint</code> represents a joint in md5mesh file.
 * <p>
 * <code>Joint</code> maintains its own transform information and the index
 * number of its parent <code>Joint</code>.
 * <p>
 * <code>Joint</code> cannot be cloned directly. The cloning process of a
 * <code>Joint</code> can only be initiated by the cloning process of the
 * parent <code>ModelNode</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:16 EST
 */
public class Joint implements Serializable, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -926371530130383637L;
	/**
	 * The <code>ModelNode</code> this <code>Joint</code> belongs.
	 */
	private ModelNode modelNode;
	/**
	 * The name ID of the <code>Joint</code>.
	 */
	private String name;
	/**
	 * The parent index of this <code>Joint</code> in the local <code>ModelNode</code>.
	 */
	private int parent;
	/**
	 * The parent index of this <code>Joint</code> in the parent <code>ModelNode</code>.
	 */
	private int nodeParent;
	/**
	 * The translation value.
	 */
	private Vector3f translation;
	/**
	 * The orientation value.
	 */
	private Quaternion orientation;
	/**
	 * The relative <code>TransformMatrix</code> of this <code>Joint</code> to its parent.
	 */
	private TransformMatrix transform;

	/**
	 * Default constructor of <code>Joint</code>.
	 */
	public Joint() {
		this.nodeParent = -1;
	}

	/**
	 * Constructor of <code>Joint</code>.
	 * @param name The name ID of the <code>Joint</code>.
	 */
	public Joint(String name, ModelNode modelNode) {
		this.name = name;
		this.modelNode = modelNode;
		this.parent = -1;
		this.nodeParent = -1;
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
		this.transform = new TransformMatrix();
	}

	/**
	 * Update the translation and orientation of this <code>Joint</code>.
	 * @param translation The new <code>Vector3f</code> translation value.
	 * @param orientation The new </code>Quaternion</code> orientation value.
	 */
	public void updateTransform(Vector3f translation, Quaternion orientation) {
		this.translation.set(translation);
		this.orientation.set(orientation);
		this.modelNode.flagUpdate();
	}

	/**
	 * Process the translation and orientation of this <code>Joint</code> This process
	 * has to be started from the bottom of skeleton tree up to the root <code>Joint</code>.
	 * @param parentTrans The parent <code>Vector3f</code> translation value.
	 * @param parentOrien The parent <code>Quaternion</code> orientation value.
	 */
	public void processTransform(Vector3f parentTrans, Quaternion parentOrien) {
		if(parentTrans == null || parentOrien == null) {
			parentOrien = new Quaternion();
			parentTrans = new Vector3f();
		}
		this.orientation.set(parentOrien.inverse().multLocal(this.orientation));
		this.translation.subtractLocal(parentTrans);
		parentOrien.inverse().multLocal(this.translation);
	}

	/**
	 * Process the relative transforms of this <code>Joint</code>.
	 */
	public void processRelative() {
		this.transform.loadIdentity();
		if(this.parent >= 0) this.transform.set(this.modelNode.getJoint(this.parent).getTransform());
		else this.transform.set(this.getBaseTransform());
		this.transform.multLocal(new TransformMatrix(this.orientation, this.translation), new Vector3f());
	}

	/**
	 * Get the base transform of the parent <code>Joint</code> in either the parent
	 * <code>ModelNode</code> or the local <code>ModelNode</code>.
	 * @return The base <code>TransformMatrix</code>.
	 */
	private TransformMatrix getBaseTransform() {
		if(this.nodeParent < 0) return new TransformMatrix();
		else {
			TransformMatrix matrix = new TransformMatrix();
			matrix.combineWithParent(((ModelNode)this.modelNode.getParent()).getJoint(this.nodeParent).getTransform());
			return matrix;
		}
	}

	/**
	 * Set the parent index of this <code>Joint</code>.
	 * @param parent The index of the parent <code>Joint</code>.
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}

	/**
	 * Set the parent index of this <code>Joint</code> in the parent <code>ModelNode</code>.
	 * @param outerParent The index of the parent <code>Joint</code>.
	 */
	public void setNodeParent(int outerParent) {
		this.nodeParent = outerParent;
	}

	/**
	 * Set one of the 6 transform values.
	 * @param index The index of the transform values.
	 * @param value The actual value to be set.
	 */
	public void setTransform(int index, float value) {
		switch(index) {
		case 0: this.translation.setX(value); break;
		case 1: this.translation.setY(value); break;
		case 2: this.translation.setZ(value); break;
		case 3: this.orientation.x = value; break;
		case 4: this.orientation.y = value; break;
		case 5:
			this.orientation.z = value;
			this.processOrientation();
			break;
		default: break;
		}
	}

	/**
	 * Compute the w value of the orientation.
	 */
	private void processOrientation() {
		float t = 1.0f-(this.orientation.x*this.orientation.x)-(this.orientation.y*this.orientation.y)-(this.orientation.z*this.orientation.z);
		if (t < 0.0f) this.orientation.w = 0.0f;
		else this.orientation.w = -(FastMath.sqrt(t));
	}

	/**
	 * Retrieve the translation of this <code>Joint</code> read from MD5 file.
	 * @return The <code>Vector3f</code> translation read directly from MD5 file.
	 */
	public Vector3f getTranslation() {
		return this.translation;
	}

	/**
	 * Retrieve the orientation of this <code>Joint</code> read from MD5 file.
	 * @return The <code>Quaternion</code> orientation read directly from MD5 file.
	 */
	public Quaternion getOrientation() {
		return this.orientation;
	}

	/**
	 * Retrieve the relative <code>TransformMatrix</code> of this <code>Joint</code>.
	 * @return The relative <code>TransformMatrix</code> of this <code>Joint</code>.
	 */
	public TransformMatrix getTransform() {
		return this.transform;
	}

	/**
	 * Retrieve the index number of the parent <code>Joint</code>.
	 * @return The index number of the parent <code>Joint</code>.
	 */
	public int getParent() {
		return this.parent;
	}

	/**
	 * Retrieve the name ID of this <code>Joint</code>.
	 * @return The name ID of this <code>Joint</code>.
	 */
	public String getName() {
		return this.name;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Joint.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.modelNode, "ModelNode", null);
		oc.write(this.name, "Name", null);
		oc.write(this.parent, "Parent", -1);
		oc.write(this.translation, "Translation", null);
		oc.write(this.orientation, "Orientation", null);
		oc.write(this.transform, "Transform", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.modelNode = (ModelNode)ic.readSavable("ModelNode", null);
		this.name = ic.readString("Name", null);
		this.parent = ic.readInt("Parent", -1);
		this.translation = (Vector3f)ic.readSavable("Translation", null);
		this.orientation = (Quaternion)ic.readSavable("Orientation", null);
		this.transform = (TransformMatrix)ic.readSavable("Transform", null);
	}

	/**
	 * Clone this join with given newly cloned <code>ModelNode</code> parent.
	 * @param mesh The cloned <code>ModelNode</code> parent.
	 * @return The cloned copy of this <code>Joint</code>
	 */
	public Joint clone(ModelNode modelNode) {
		Joint clone = new Joint();
		clone.modelNode = modelNode;
		clone.name = new String(this.name.toCharArray());
		clone.parent = this.parent;
		clone.translation = this.translation.clone();
		clone.orientation = new Quaternion(this.orientation.x, this.orientation.y, this.orientation.z, this.orientation.w);
		clone.transform = new TransformMatrix();
		clone.transform.set(this.transform);
		return clone;
	}
}
