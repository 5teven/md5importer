package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * <code>Weight</code> represents a weight in md5mesh file.
 * <p>
 * <code>Weight</code> maintains the index number of the <code>Joint</code>
 * which it affects, a fixed weight value and a fixed position vector.
 * <p>
 * <code>Weight</code> should not be cloned directly. The cloning process of a
 * <code>Weight</code> should be initiated by the cloning process of the parent
 * <code>Mesh</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:15 EST
 */
public class Weight implements Serializable, Savable, Cloneable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 4719214599414606855L;
	/**
	 * The index of the <code>Joint</code> this <code>Weight</code> affects.
	 */
	private int jointIndex;
	/**
	 * The fixed weight value of this <code>Weight</code>.
	 */
	private float value;
	/**
	 * The position vector of this <code>Weight</code>.
	 */
	private Vector3f position;

	/**
	 * Default constructor of <code>Weight</code>.
	 */
	public Weight() {
		this.position = new Vector3f();
	}

	/**
	 * Set the index of the <code>Joint</code> which this <code>Weight</code> affects.
	 * @param index The index of the <code>Joint</code>.
	 */
	public void setJointIndex(int index) {
		this.jointIndex = index;
	}

	/**
	 * Set the weight value of this <code>Weight</code>.
	 * @param value The weight value of this <code>Weight</code>.
	 */
	public void setWeightValue(float value) {
		this.value = value;
	}

	/**
	 * Set the fixed position of this <code>Weight</code>.
	 * @param index The index of position value to set.
	 * @param value The float value to be set.
	 */
	public void setPosition(int index, float value) {
		switch(index) {
		case 0:	this.position.setX(value); break;
		case 1: this.position.setY(value); break;
		case 2: this.position.setZ(value); break;
		default: break;
		}
	}

	/**
	 * Retrieve the index of the <code>Joint</code> this <code>Weight</code> affects.
	 * @return The index number of the <code>Joint</code>.
	 */
	public int getJointIndex() {
		return this.jointIndex;
	}

	/**
	 * Retrieve the fixed weight value of this <code>Weight</code>.
	 * @return The float weight value.
	 */
	public float getWeightValue() {
		return this.value;
	}

	/**
	 * Retrieve the fixed position of this <code>Weight</code>.
	 * @return The <code>Vector3f</code> position of this <code>Weight</code>.
	 */
	public Vector3f getPosition() {
		return this.position;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Weight.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.jointIndex, "JointIndex", -1);
		oc.write(this.value, "Value", 0);
		oc.write(this.position, "Position", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.jointIndex = ic.readInt("JointIndex", -1);
		this.value = ic.readFloat("Value", 0);
		this.position = (Vector3f)ic.readSavable("Position", null);
	}

	@Override
	public Weight clone() {
		Weight clone = new Weight();
		clone.jointIndex = this.jointIndex;
		clone.value = this.value;
		clone.position = this.position.clone();
		return clone;
	}
}
