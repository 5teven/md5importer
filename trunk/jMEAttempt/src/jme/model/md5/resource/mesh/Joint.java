package jme.model.md5.resource.mesh;


import jme.model.md5.MD5Importer;

import com.jme.animation.Bone;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;

/**
 * Joint maintains the information of a joint in md5mesh file. This class is
 * used internally by MD5Importer only.
 * 
 * @author Yi Wang (Neakor)
 */
public class Joint {
	// The name of the joint.
	private String name;
	// The parent joint index.
	private int parent;
	// The translation transform.
	private Vector3f translation;
	// The orientation transform.
	private Quaternion orientation;
	// The relative transform matrix of this joint.
	private TransformMatrix relativeTransform;
	// The generated bone of this joint.
	private Bone bone;

	/**
	 * Constructor of Joint.
	 * @param name
	 */
	public Joint(String name) {
		this.name = name;
		this.parent = -1;
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
		this.relativeTransform = new TransformMatrix();
		this.bone = new Bone(this.name);
	}
	
	/**
	 * Process the translation and orientation of this Joint into local space.
	 * This process has to be started from the bottom of Joint tree up to the root Joint.
	 */
	public void processTransform() {
		MD5Importer instance = MD5Importer.getInstance();
		Quaternion parentOrien = null;
		Vector3f parentTrans = null;
		if(this.parent >= 0)
		{
			parentOrien = instance.getJoint(this.parent).getOrientation();
			parentTrans = instance.getJoint(this.parent).getTranslation();
		}
		else
		{
			parentOrien = new Quaternion();
			parentTrans = new Vector3f();
		}
		this.orientation.set(parentOrien.inverse().multLocal(this.orientation));
		this.translation.subtractLocal(parentTrans);
		parentOrien.inverse().multLocal(this.translation);
		if(this.parent < 0)
		{
			this.orientation.set(MD5Importer.base.mult(this.orientation));
		}
	}

	/**
	 * Generate a Bone based on loaded information.
	 */
	public void generateBone() {
		this.processRelative();
		if(this.parent == -1) this.bone.setSkinRoot(true);
		else this.bone.setSkinRoot(false);
	}
	
	/**
	 * Process the relative transform of this joint.
	 */
	private void processRelative() {
		if(this.parent >= 0) this.relativeTransform.set(MD5Importer.getInstance().getJoint(this.parent).getRelativeTransform());
		this.relativeTransform.multLocal(new TransformMatrix(this.orientation, this.translation), new Vector3f());
	}
	
	/**
	 * Set the parent Joint index of this Joint.
	 * @param parent The index of the parent Joint.
	 */
	public void setParent(int parent) {
		this.parent = parent;
	}
	
	/**
	 * Set one of the 6 transform values.
	 * @param index The index of the transform values.
	 * @param value The actual value to be set.
	 */
	public void setTransform(int index, float value) {
		switch(index)
		{
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
	 * Retrieve the translation of this Joint read from MD5 file.
	 * @return The Vector3f translation read directly from MD5 file.
	 */
	public Vector3f getTranslation() {
		return this.translation;
	}
	
	/**
	 * Retrieve the orientation of this Joint read from MD5 file.
	 * @return The Quaternion orientation read directly from MD5 file.
	 */
	public Quaternion getOrientation() {
		return this.orientation;
	}

	/**
	 * Retrieve the relative TransformMatrix of this Joint.
	 * @return The relative TransformMatrix of this Joint.
	 */
	public TransformMatrix getRelativeTransform() {
		return this.relativeTransform;
	}
	
	/**
	 * Retrieve the index of the parent Joint.
	 * @return The index of the parent Joint.
	 */
	public int getParent() {
		return this.parent;
	}
	
	/**
	 * Retrieve the Bone generated by this Joint.
	 * @return The generated Bone object.
	 */
	public Bone getBone() {
		return this.bone;
	}
}
