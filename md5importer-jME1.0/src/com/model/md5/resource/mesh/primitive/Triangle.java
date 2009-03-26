package com.model.md5.resource.mesh.primitive;

import java.io.IOException;
import java.io.Serializable;

import com.jme.math.Vector3f;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.mesh.Mesh;

/**
 * <code>Triangle</code> represents a tri in md5mesh file.
 * <p>
 * <code>Triangle</code> maintains three indices of <code>Vertex</code> that
 * define this <code>Triangle</code>.
 * <p>
 * <code>Triangle</code> is responsible for calculating the normal vector for
 * each <code>Vertex</code>.
 * <p>
 * <code>Triangle</code> cannot be cloned directly. The cloning process of a
 * <code>Triangle</code> can only be initiated by the cloning process of the
 * parent <code>Mesh</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:15 EST
 */
public class Triangle implements Serializable, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -6234457193386375719L;
	/**
	 * The <code>Mesh</code> this <code>Triangle</code> belongs to.
	 */
	private Mesh mesh;
	/**
	 * The array of <code>Vertex</code> index.
	 */
	private int[] vertexIndices;
	/**
	 * The first temporary <code>Vector3f</code> for normal calculation.
	 */
	private final Vector3f temp1;
	/**
	 * The second temporary <code>Vector3f</code> for normal calculation.
	 */
	private final Vector3f temp2;

	/**
	 * Default constructor of <code>Triangle</code>.
	 */
	public Triangle() {
		this.temp1 = new Vector3f();
		this.temp2 = new Vector3f();
	}

	/**
	 * Constructor of <code>Triangle</code>.
	 * @param mesh The <code>Mesh</code> this <code>Triangle</code> belongs to.
	 */
	public Triangle(Mesh mesh) {
		this.mesh = mesh;
		this.vertexIndices = new int[3];
		this.temp1 = new Vector3f();
		this.temp2 = new Vector3f();
	}

	/**
	 * Process the normal of vertices and store the normal values in the
	 * <code>Vertex</code> instances.
	 */
	public void processNormal() {
		Vertex vert1 = this.mesh.getVertex(this.vertexIndices[0]);
		Vertex vert2 = this.mesh.getVertex(this.vertexIndices[1]);
		Vertex vert3 = this.mesh.getVertex(this.vertexIndices[2]);
		this.temp1.set(vert2.getPosition()).subtractLocal(vert1.getPosition());
		this.temp2.set(vert3.getPosition()).subtractLocal(vert2.getPosition());
		this.temp1.crossLocal(this.temp2);
		this.temp1.normalizeLocal();
		vert1.setNormal(this.temp2.set(this.temp1).multLocal(1.0f/(float)vert1.getUsedTimes()));
		vert2.setNormal(this.temp2.set(this.temp1).multLocal(1.0f/(float)vert2.getUsedTimes()));
		vert3.setNormal(this.temp1.multLocal(1.0f/(float)vert3.getUsedTimes()));
	}

	/**
	 * Set the index of one <code>Vertex</code> in this <code>Triangle</code>.
	 * @param index The index of the <code>Vertex</code> in the indices array.
	 * @param vertex The index of the <code>Vertex</code> to be set.
	 */
	public void setVertexIndex(int index, int vertex) {
		this.vertexIndices[index] = vertex;
	}

	/**
	 * Retrieve the <code>Vertex</code> index with given array index.
	 * @param index The array index number in the <code>Triangle</code>.
	 * @return The index number of the <code>Vertex</code> with given array index.
	 */
	public int getVertexIndex(int index) {
		return this.vertexIndices[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Triangle.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.mesh, "Mesh", null);
		oc.write(this.vertexIndices, "VertexIndices", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.mesh = (Mesh)ic.readSavable("Mesh", null);
		this.vertexIndices = ic.readIntArray("VertexIndices", null);
	}

	/**
	 * Clone this triangle with given newly cloned mesh parent.
	 * @param mesh The cloned <code>Mesh</code> parent.
	 * @return The cloned copy of this <code>Triangle</code>
	 */
	public Triangle clone(Mesh mesh) {
		Triangle clone = new Triangle();
		clone.mesh = mesh;
		clone.vertexIndices = new int[this.vertexIndices.length];
		System.arraycopy(this.vertexIndices, 0, clone.vertexIndices, 0, this.vertexIndices.length);
		return clone;
	}
}
