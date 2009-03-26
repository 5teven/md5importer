package com.model.md5.resource.anim;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Frame</code> represents a frame in the md5anim file.
 * <p>
 * <code>Frame</code> maintains translation and orientation information of each
 * <code>Joint</code> in the animation of the current frame.
 * <p>
 * <code>Frame</code> should not be cloned directly. The cloning process of a
 * <code>Frame</code> should be initiated by the cloning process of the parent
 * <code>JointAnimation</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:14 EST
 */
public class Frame implements Serializable, Savable, Cloneable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 8891271219195292580L;
	/**
	 * The flag indicates if this <code>Frame</code> is a baseframe.
	 */
	private boolean baseframe;
	/**
	 * The array of parent indices for each <code>Joint</code>.
	 */
	private int[] parents;
	/**
	 * The translations of <code>Joint</code> of this <code>Frame</code>.
	 */
	private Vector3f[] translations;
	/**
	 * The orientations of <code>Joint</code> of this <code>Frame</code>.
	 */
	private Quaternion[] orientations;

	/**
	 * Default constructor of <code>Frame</code>.
	 */
	public Frame() {}

	/**
	 * Constructor of <code>Frame</code>.
	 * @param baseframe True if this <code>Frame</code> is a base frame. False otherwise.
	 * @param numJoints The total number of <code>Joint</code> in the animation.
	 */
	public Frame(boolean baseframe, int numJoints) {
		this.baseframe = baseframe;
		this.translations = new Vector3f[numJoints];
		this.orientations = new Quaternion[numJoints];
		for(int i = 0; i < this.translations.length && i < this.orientations.length; i++) {
			this.translations[i] = new Vector3f();
			this.orientations[i] = new Quaternion();
		}
	}

	/**
	 * Set the <code>Frame</code> hierarchy of the <code>Frame</code>.
	 * @param parents The integer array of <code>Joint</code> hierarchy.
	 */
	public void setParents(int[] parents) {
		this.parents = parents;
	}

	/**
	 * Set the transform of this <code>Frame</code>.
	 * @param jointIndex The index of the <code>Joint</code>.
	 * @param index The transform index number.
	 * @param value The transform value to be set.
	 */
	public void setTransform(int jointIndex, int index, float value) {
		switch(index) {
		case 0: this.translations[jointIndex].x = value; break;
		case 1: this.translations[jointIndex].y = value; break;
		case 2: this.translations[jointIndex].z = value; break;
		case 3: this.orientations[jointIndex].x = value; break;
		case 4: this.orientations[jointIndex].y = value; break;
		case 5:
			this.orientations[jointIndex].z = value;
			this.processOrientation(jointIndex, this.orientations[jointIndex]);
			break;
		}
	}

	/**
	 * Process the <code>Quaternion</code> orientation to finalize it.
	 * @param jointIndex The index of the <code>Joint</code>.
	 * @param raw The raw orientation value.
	 */
	private void processOrientation(int jointIndex, Quaternion raw) {
		float t = 1.0f - (raw.x * raw.x) - (raw.y * raw.y) - (raw.z * raw.z);
		if (t < 0.0f) raw.w = 0.0f;
		else raw.w = -(FastMath.sqrt(t));
	}

	/**
	 * Retrieve the parent index of the <code>Joint</code> with given index.
	 * @return The index of the parent <code>Joint</code>.
	 */
	public int getParent(int index) {
		return this.parents[index];
	}

	/**
	 * Retrieve the transform value with given indices.
	 * @param jointIndex The <code>Joint</code> index.
	 * @param transIndex The transform index.
	 * @return The transform value.
	 */
	public float getTransformValue(int jointIndex, int transIndex) {
		switch(transIndex) {
		case 0: return this.translations[jointIndex].x;
		case 1: return this.translations[jointIndex].y;
		case 2: return this.translations[jointIndex].z;
		case 3: return this.orientations[jointIndex].x;
		case 4: return this.orientations[jointIndex].y;
		case 5: return this.orientations[jointIndex].z;
		default: return 0;
		}
	}

	/**
	 * Retrieve the translation of the <code>Joint</code> with given index.
	 * @param jointIndex The <code>Joint</code> index number.
	 * @return The <code>Vector3f</code> translation value.
	 */
	public Vector3f getTranslation(int jointIndex) {
		return this.translations[jointIndex];
	}

	/**
	 * Retrieve the orientation of the <code>Joint</code> with given index.
	 * @param jointIndex The <code>Joint</code> number.
	 * @return The <code>Quaternion</code> orientation value.
	 */
	public Quaternion getOrientation(int jointIndex) {
		return this.orientations[jointIndex];
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Frame.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.baseframe, "Baseframe", false);
		oc.write(this.parents, "Parents", null);
		oc.write(this.translations, "Translations", null);
		oc.write(this.orientations, "Orientations", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		this.baseframe = ic.readBoolean("Baseframe", false);
		this.parents = ic.readIntArray("Parents", null);
		temp = ic.readSavableArray("Translations", null);
		this.translations = new Vector3f[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.translations[i] = (Vector3f)temp[i];
		}
		temp = ic.readSavableArray("Orientations", null);
		this.orientations = new Quaternion[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.orientations[i] = (Quaternion)temp[i];
		}
	}

	@Override
	public Frame clone() {
		Frame clone = new Frame();
		clone.baseframe = this.baseframe;
		clone.parents = new int[this.parents.length];
		System.arraycopy(this.parents, 0, clone.parents, 0, this.parents.length);
		clone.translations = new Vector3f[this.translations.length];
		for(int i = 0; i < clone.translations.length; i++) {
			clone.translations[i] = this.translations[i].clone();
		}
		clone.orientations = new Quaternion[this.orientations.length];
		for(int i = 0; i < clone.orientations.length; i++) {
			clone.orientations[i] = new Quaternion(this.orientations[i].x, this.orientations[i].y, this.orientations[i].z, this.orientations[i].w);
		}
		return clone;
	}
}
