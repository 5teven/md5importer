package com.md5importer.interfaces.model.mesh;

import com.jme.util.export.Savable;
import com.md5importer.interfaces.model.mesh.primitive.IVertex;
import com.md5importer.interfaces.model.mesh.primitive.IWeight;

/**
 * <code>IMesh</code> defines the interface of a mesh in the model.
 * It maintains a number of vertices that compose a number of
 * triangles. And each vertex is affected with a weight.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 11-17-2008 20:12 EST
 * @version Modified date: 05-10-2009 17:21 EST
 */
public interface IMesh extends Savable {

	/**
	 * Initialize this mesh and its geometric data.
	 * @param name The parent MD5 node <code>String</code> name.
	 */
	public void initialize(String name);

	/**
	 * Update this mesh and its geometric data.
	 */
	public void updateMesh();
	
	/**
	 * Swap the vertex information buffers.
	 */
	public void swapBuffer();
	
	/**
	 * Set this mesh to use the given joints.
	 * @param joints The array of <code>IJoint</code> to be used.
	 */
	public void setJoints(IJoint[] joints);

	/**
	 * Retrieve the vertex with given index number.
	 * @param index The <code>Integer</code> index number of the <code>IVertex</code>.
	 * @return The <code>IVertex</code> instance with given index number.
	 */
	public IVertex getVertex(int index);

	/**
	 * Retrieve the weight with given index number.
	 * @param index The <code>Integer</code> index number of the <code>Weight</code>.
	 * @return The <code>Weight</code> instance with given index number.
	 */
	public IWeight getWeight(int index);

	/**
	 * Clone this mesh.
	 * @param clonedJoints The array of cloned <code>IJoint</code>.
	 * @return The cloned copy of this <code>IMesh</code>
	 */
	public IMesh clone(IJoint[] clonedJoints);
}