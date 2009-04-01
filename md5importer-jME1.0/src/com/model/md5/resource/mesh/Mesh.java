package com.model.md5.resource.mesh;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.scene.SceneElement;
import com.jme.scene.batch.TriangleBatch;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;
import com.model.md5.resource.mesh.primitive.Triangle;
import com.model.md5.resource.mesh.primitive.Vertex;
import com.model.md5.resource.mesh.primitive.Weight;

/**
 * <code>Mesh</code> represents a mesh in md5mesh file.
 * <p>
 * <code>Mesh</code> maintains a number of <code>Vertex</code>,
 * <code>Weight</code> and <code>Triangle</code> to represent the basic
 * geometry of a <code>ModelNode</code>.
 * <p>
 * <code>Mesh</code> does not directly process any geometric information
 * but delegates the process down to the primitive elements it maintains.
 * <p>
 * <code>Mesh</code> cannot be cloned directly. The cloning process of a
 * <code>Mesh</code> can only be initiated by the cloning process of the
 * parent <code>ModelNode</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-10-2008 15:18 EST
 */
public class Mesh implements Serializable, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -6431941710991131243L;
	/**
	 * The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	private ModelNode modelNode;
	/**
	 * The texture file name without extension.
	 */
	private String texture;
	/**
	 * The array of <code>Vertex</code> in this <code>Mesh</code>.
	 */
	private Vertex[] vertices;
	/**
	 * The array of <code>Triangle</code> in this <code>Mesh</code>.
	 */
	private Triangle[] triangles;
	/**
	 * The array of <code>Weight</code> in this <code>Mesh</code>.
	 */
	private Weight[] weights;
	/**
	 * The generated <code>TriangleBatch</code> of this <code>Mesh</code>.
	 */
	private TriangleBatch triangleBatch;
	/**
	 * The temporary <code>List</code> of <code>IVertex</code>
	 * for averaging normal calculation.
	 */
	private final List<Vertex> tempVertices;

	/**
	 * Default constructor of <code>Mesh</code>.
	 */
	public Mesh() {
		this.tempVertices = new ArrayList<Vertex>();
	}

	/**
	 * Constructor of <code>Mesh</code>.
	 * @param modelNode The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	public Mesh(ModelNode modelNode) {
		this.modelNode = modelNode;
		this.tempVertices = new ArrayList<Vertex>();
	}

	/**
	 * Generate the <code>TriangleBatch</code> of this <code>Mesh</code>.
	 */
	public void generateBatch() {
		this.triangleBatch = new TriangleBatch();
		this.triangleBatch.setNormalsMode(SceneElement.NM_GL_NORMALIZE_PROVIDED);
		this.processIndex();
		this.processVertex();
		this.processNormal();
		this.processTexture();
		this.processBounding();
	}

	/**
	 * Update the <code>TriangleBatch</code> vertex and normal buffer.
	 */
	public void updateBatch() {
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].resetInformation();
		}
		this.processVertex();
		this.processNormal();
		this.triangleBatch.updateModelBound();
	}

	/**
	 * Process and setup the index buffer.
	 */
	private void processIndex() {
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(this.triangles.length*3);
		indexBuffer.clear();
		for(int i = 0; i < this.triangles.length; i++) {
			for(int j = 0; j < 3; j++) {
				indexBuffer.put(this.triangles[i].getVertexIndex(j));
			}
		}
		indexBuffer.flip();
		this.triangleBatch.setIndexBuffer(indexBuffer);
	}

	/**
	 * Process and setup the vertex position buffer.
	 */
	private void processVertex() {
		FloatBuffer vertexBuffer = this.triangleBatch.getVertexBuffer();
		if(vertexBuffer == null) vertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		vertexBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].processPosition();
			BufferUtils.setInBuffer(this.vertices[i].getPosition(), vertexBuffer, i);
		}
		this.triangleBatch.setVertexBuffer(vertexBuffer);
	}

	/**
	 * Process and setup the normal position buffer.
	 */
	private void processNormal() {
		for(int i = 0; i < this.triangles.length; i++) {
			this.triangles[i].processNormal();
		}
		// Average vertex normals with same vertex positions.
		this.tempVertices.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			final Vertex v1 = this.vertices[i];
			this.tempVertices.add(v1);
			// Find all vertices with same position.
			for(int j = 0; j < this.vertices.length; j++) {
				final Vertex v2 = this.vertices[j];
				if(v1 != v2 && v2.getPosition().equals(v1.getPosition())) {
					this.tempVertices.add(v2);
				}
			}
			// Average vertices in list.
			float x = 0;
			float y = 0;
			float z = 0;
			for(Vertex vertex : this.tempVertices) {
				x += vertex.getNormal().getX();
				y += vertex.getNormal().getY();
				z += vertex.getNormal().getZ();
			}
			final int size = this.tempVertices.size();
			x = x / size;
			y = y / size;
			z = z / size;
			for(Vertex vertex : this.tempVertices) {
				vertex.getNormal().set(x, y, z);
				vertex.getNormal().normalizeLocal();
			}
			// Clear out this group.
			this.tempVertices.clear();
		}
		// Put into buffer.
		FloatBuffer normalBuffer = this.triangleBatch.getNormalBuffer();
		if(normalBuffer == null) normalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		normalBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getNormal(), normalBuffer, i);
		}
		this.triangleBatch.setNormalBuffer(normalBuffer);
	}

	/**
	 * Process and setup the <code>TextureState</code> and texture UV buffer.
	 */
	private void processTexture() {
		MD5Importer instance = MD5Importer.getInstance();
		FloatBuffer textureBuffer = BufferUtils.createVector2Buffer(this.vertices.length);
		float maxU = 1; float maxV = 1; float minU = 0; float minV = 0;
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getTextureCoords(), textureBuffer, i);
			if(this.vertices[i].getTextureCoords().x > maxU) maxU = this.vertices[i].getTextureCoords().x;
			else if(this.vertices[i].getTextureCoords().x < minU) minU = this.vertices[i].getTextureCoords().x;
			if(this.vertices[i].getTextureCoords().y > maxV) maxV = this.vertices[i].getTextureCoords().y;
			else if(this.vertices[i].getTextureCoords().y < minV) minV = this.vertices[i].getTextureCoords().y;
		}
		this.triangleBatch.setTextureBuffer(textureBuffer, 0);
		URL url = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, this.texture);
		Texture color = TextureManager.loadTexture(url,instance.getMMFilter(),instance.getFMFilter(),instance.getAnisotropic(),true);
		if(color != null) {
			if(maxU > 1 || minU < 0) {
				if(maxV > 1 || minV < 0) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
				else {
					if(color.getWrap() != Texture.WM_WRAP_S_WRAP_T) {
						if(color.getWrap() == Texture.WM_CLAMP_S_WRAP_T) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
						else color.setWrap(Texture.WM_WRAP_S_CLAMP_T);
					}
				}
			} else if(maxV > 1 || minV < 0) {
				if(color.getWrap() != Texture.WM_WRAP_S_WRAP_T) {
					if(color.getWrap() == Texture.WM_WRAP_S_CLAMP_T) color.setWrap(Texture.WM_WRAP_S_WRAP_T);
					else color.setWrap(Texture.WM_CLAMP_S_WRAP_T);
				}
			}
		}
		TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		state.setTexture(color);
		this.triangleBatch.setRenderState(state);
	}

	/**
	 * Process and setup the bounding volume of the <code>Mesh</code>.
	 */
	private void processBounding() {
		if(MD5Importer.getInstance().isOriented()) this.triangleBatch.setModelBound(new OrientedBoundingBox());
		else this.triangleBatch.setModelBound(new BoundingBox());
		this.triangleBatch.updateModelBound();
		this.triangleBatch.updateGeometricState(0, true);
	}

	/**
	 * Set the texture file name of this <code>Mesh</code>.
	 * @param texture The texture file name without extension.
	 */
	public void setTexture(String texture) {
		this.texture = texture;
	}

	/**
	 * Setup the vertices array based on the given count.
	 * @param count The number of vertices in this <code>Mesh</code>.
	 */
	public void setVrticesCount(int count) {
		this.vertices = new Vertex[count];
	}

	/**
	 * Set the <code>Vertex</code> with given index number.
	 * @param index The index of the <code>Vertex</code>.
	 * @param vertex The <code>Vertex</code> to be set.
	 */
	public void setVertex(int index, Vertex vertex) {
		this.vertices[index] = vertex;
	}

	/**
	 * Setup the triangles array based on the given count.
	 * @param count The number of triangles in this <code>Mesh</code>.
	 */
	public void setTrianglesCount(int count) {
		this.triangles = new Triangle[count];
	}

	/**
	 * Set the <code>Triangle</code> with given index number.
	 * @param index The index of the <code>Triangle</code>.
	 * @param triangle The <code>Triangle</code> to be set.
	 */
	public void setTriangle(int index, Triangle triangle) {
		this.triangles[index] = triangle;
	}

	/**
	 * Setup the weights array based on the given count.
	 * @param count The number of weights in this <code>Mesh</code>.
	 */
	public void setWeightCount(int count) {
		this.weights = new Weight[count];
	}

	/**
	 * Set the <code>Weight</code> with given index number.
	 * @param index The index of the <code>Weight</code>.
	 * @param weight The <code>Weight</code> to be set.
	 */
	public void setWeight(int index, Weight weight) {
		this.weights[index] = weight;
	}

	/**
	 * Retrieve the <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 * @return The <code>ModelNode</code> this <code>Mesh</code> belongs to.
	 */
	public ModelNode getModelNode() {
		return this.modelNode;
	}

	/**
	 * Retrieve the <code>Vertex</code> with given index number.
	 * @param index The index number of the <code>Vertex</code>.
	 * @return The <code>Vertex</code> instance with given index number.
	 */
	public Vertex getVertex(int index) {
		return this.vertices[index];
	}

	/**
	 * Retrieve the <code>Weight</code> with given index number.
	 * @param index The index number of the <code>Weight</code>.
	 * @return The <code>Weight</code> instance with given index number.
	 */
	public Weight getWeight(int index) {
		return this.weights[index];
	}

	/**
	 * Retrieve the <code>TriangleBatch</code> generated by this <code>Mesh</code>.
	 * @return The generated <code>TriangleBatch</code> instance.
	 */
	public TriangleBatch getTriangleBatch() {
		return this.triangleBatch;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Mesh.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.modelNode, "ModelNode", null);
		oc.write(this.texture, "Texture", null);
		oc.write(this.vertices, "Vertices", null);
		oc.write(this.triangles, "Triangles", null);
		oc.write(this.weights, "Weights", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		this.modelNode = (ModelNode)ic.readSavable("ModelNode", null);
		this.texture = ic.readString("Texture", null);
		temp = ic.readSavableArray("Vertices", null);
		this.vertices = new Vertex[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.vertices[i] = (Vertex)temp[i];
		}
		temp = ic.readSavableArray("Triangles", null);
		this.triangles = new Triangle[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.triangles[i] = (Triangle)temp[i];
		}
		temp = ic.readSavableArray("Weights", null);
		this.weights = new Weight[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.weights[i] = (Weight)temp[i];
		}
	}

	/**
	 * Clone this mesh with given newly cloned <code>ModelNode</code> parent.
	 * @param mesh The cloned <code>ModelNode</code> parent.
	 * @return The cloned copy of this <code>Mesh</code>
	 */
	public Mesh clone(ModelNode modelNode) {
		Mesh clone = new Mesh();
		clone.modelNode = modelNode;
		clone.texture = new String(this.texture.toCharArray());
		clone.vertices = new Vertex[this.vertices.length];
		for(int i = 0; i < clone.vertices.length; i++) {
			clone.vertices[i] = this.vertices[i].clone(clone);
		}
		clone.triangles = new Triangle[this.triangles.length];
		for(int i = 0; i < clone.triangles.length; i++) {
			clone.triangles[i] = this.triangles[i].clone(clone);
		}
		clone.weights = new Weight[this.weights.length];
		for(int i = 0; i < clone.weights.length; i++) {
			clone.weights[i] = this.weights[i].clone();
		}
		return clone;
	}
}
