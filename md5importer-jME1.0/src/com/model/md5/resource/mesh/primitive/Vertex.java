package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Mesh;

/**
 * <code>Vertex</code> represents a vert in md5mesh file.
 * <p>
 * <code>Vertex</code> maintains its texture coordinates, normal vector and
 * position vector.
 * <p>
 * <code>Vertex</code> stores an array of <code>Weight</code> indices which
 * are used to calculate the position vector.
 * <p>
 * <code>Vertex</code> cannot be cloned directly. The cloning process of
 * <code>Vertex</code> can only be initiated by the cloning process of the
 * parent <code>Mesh</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:15 EST
 */
public class Vertex implements Serializable, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 6774812007144718188L;
	/**
	 * The <code>Mesh</code> this <code>Vertex</code> belongs to.
	 */
	private Mesh mesh;
	/**
	 * The texture coordinates of this <code>Vertex</code>.
	 */
	private Vector2f textureCoords;
	/**
	 * The array of <code>Weight</code> indices.
	 */
	private int[] weightIndices;
	/**
	 * The number of times this <code>Vertex</code> has been used by <code>Triangle</code>.
	 */
	private int usedTimes;
	/**
	 * The normal of this <code>Vertex</code>.
	 */
	private Vector3f normal;
	/**
	 * The position of this <code>Vertex</code>.
	 */
	private Vector3f position;
	/**
	 * The temporary <code>Vector3f</code> for position calculation.
	 */
	private final Vector3f temp;

	/**
	 * Default constructor of <code>Vertex</code>.
	 */
	public Vertex() {
		this.temp = new Vector3f();
	}

	/**
	 * Constructor of <code>Vertex</code>.
	 * @param mesh The <code>Mesh</code> this <code>Vertex</code> belongs to.
	 */
	public Vertex(Mesh mesh) {
		this.mesh = mesh;
		this.position = new Vector3f();
		this.temp = new Vector3f();
	}

	/**
	 * Process the <code>Vertex</code> position.
	 */
	public void processPosition() {
		this.position.zero();
		for(int i = 0; i < this.weightIndices.length; i++) {
			this.temp.set(this.mesh.getWeight(this.weightIndices[i]).getPosition());
			this.mesh.getModelNode().getJoint(this.mesh.getWeight(this.weightIndices[i]).getJointIndex()).getTransform().multPoint(this.temp);
			this.temp.multLocal(this.mesh.getWeight(this.weightIndices[i]).getWeightValue());
			this.position.addLocal(this.temp);
		}
	}

	/**
	 * Reset the normal and position information of this <code>Vertex</code>.
	 */
	public void resetInformation() {
		this.normal.zero();
		this.position.zero();
	}

	/**
	 * Increment the number of times this <code>Vertex</code> has been used by
	 * <code>Triangle</code>.
	 */
	public void incrementUsedTimes() {
		this.usedTimes++;
	}

	/**
	 * Set the texture coordinates of this <code>Vertex</code>.
	 * @param u The u value.
	 * @param v The un-inverted v value.
	 */
	public void setTextureCoords(float u, float v) {
		// Invert the v value.
		float invertV = 1.0f - v;
		this.textureCoords = new Vector2f(u, invertV);
	}

	/**
	 * Set the indices of <code>Weight</code> that affects this <code>Vertex</code>.
	 * @param start The starting index number.
	 * @param length The number of weights that affect this <code>Vertex</code>.
	 */
	public void setWeightIndices(int start, int length) {
		this.weightIndices = new int[length];
		for(int i = 0; i < this.weightIndices.length; i++) {
			this.weightIndices[i] = start + i;
		}
	}

	/**
	 * Set the normal vector of this <code>Vertex</code>.
	 * @param normal The normal <code>Vector3f</code> to be set.
	 */
	public void setNormal(Vector3f normal) {
		if(this.normal == null) this.normal = new Vector3f(normal);
		// If this vertex has been used, add the new value.
		else this.normal.addLocal(normal);
	}

	/**
	 * Retrieve the texture coordinates of this <code>Vertex</code>.
	 * @return The <code>Vector2f</code> texture coordinates of this <code>Vertex</code>.
	 */
	public Vector2f getTextureCoords() {
		return this.textureCoords;
	}

	/**
	 * Retrieve the number of times this <code>Vertex</code> has been used.
	 * @return The number of times this <code>Vertex</code> has been used.
	 */
	public int getUsedTimes() {
		return this.usedTimes;
	}

	/**
	 * Retrieve the position of this <code>Vertex</code>.
	 * @return The <code>Vector3f</code> position of this <code>Vertex</code>.
	 */
	public Vector3f getPosition() {
		return this.position;
	}

	/**
	 * Retrieve the normal of this <code>Vertex</code>.
	 * @return The <code>Vector3f</code> normal of this <code>Vertex</code>.
	 */
	public Vector3f getNormal() {
		return this.normal;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Vertex.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.mesh, "Mesh", null);
		oc.write(this.textureCoords, "TextureCoords", null);
		oc.write(this.weightIndices, "WeightIndices", null);
		oc.write(this.usedTimes, "UsedTimes", 0);
		oc.write(this.normal, "Normal", null);
		oc.write(this.position, "Position", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.mesh = (Mesh)ic.readSavable("Mesh", null);
		this.textureCoords = (Vector2f)ic.readSavable("TextureCoords", null);
		this.weightIndices = ic.readIntArray("WeightIndices", null);
		this.usedTimes = ic.readInt("UsedTimes", 0);
		this.normal = (Vector3f)ic.readSavable("Normal", null);
		this.position = (Vector3f)ic.readSavable("Position", null);
	}

	/**
	 * Clone this vertex with given newly cloned mesh parent.
	 * @param mesh The cloned <code>Mesh</code> parent.
	 * @return The cloned copy of this <code>Vertex</code>
	 */
	public Vertex clone(Mesh mesh) {
		Vertex clone = new Vertex();
		clone.mesh = mesh;
		clone.textureCoords = this.textureCoords.clone();
		clone.weightIndices = new int[this.weightIndices.length];
		System.arraycopy(this.weightIndices, 0, clone.weightIndices, 0, this.weightIndices.length);
		clone.usedTimes = this.usedTimes;
		clone.normal = this.normal.clone();
		clone.position = this.position.clone();
		return clone;
	}
}
