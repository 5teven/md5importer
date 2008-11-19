package com.model.md5.resource.mesh;

import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
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
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IJoint;
import com.model.md5.interfaces.IMesh;
import com.model.md5.interfaces.ITriangle;
import com.model.md5.interfaces.IVertex;
import com.model.md5.interfaces.IWeight;

/**
 * <code>Mesh</code> defines the concrete implementation of a mesh. It does
 * not directly process any geometric information but delegates the process
 * down to the primitive elements it maintains.
 * <p>
 * <code>Mesh</code> cannot be cloned directly. The cloning process of a
 * <code>Mesh</code> can only be initiated by the cloning process of the
 * parent <code>IIMD5Node</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 11-18-2008 22:39 EST
 */
public class Mesh extends TriMesh implements IMesh {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -6431941710991131243L;
	/**
	 * The <code>String</code> texture file name without extension.
	 */
	private String texture;
	/**
	 * The array of <code>IVertex</code> in this mesh.
	 */
	private IVertex[] vertices;
	/**
	 * The array of <code>ITriangle</code> in this mesh.
	 */
	private ITriangle[] triangles;
	/**
	 * The array of <code>IWeight</code> in this mesh.
	 */
	private IWeight[] weights;

	/**
	 * Constructor of <code>Mesh</code>.
	 */
	public Mesh() {
		super();
	}
	
	/**
	 * Constructor of <code>Mesh</code>.
	 * @param texture The <code>String</code> texture file name without extension.
	 * @param vertices The array of <code>IVertex</code> in this mesh.
	 * @param triangles The array of <code>ITriangle</code> in this mesh.
	 * @param weights The array of <code>IWeight</code> in this mesh.
	 */
	public Mesh(String texture, IVertex[] vertices, ITriangle[] triangles, IWeight[] weights) {
		this.texture = texture;
		this.vertices = vertices;
		this.triangles = triangles;
		this.weights = weights;
	}

	@Override
	public void initialize() {
		this.setName("Mesh");
		this.setNormalsMode(Spatial.NormalsMode.AlwaysNormalize);
		this.processIndex();
		this.processVertex();
		this.processNormal();
		this.processTexture();
		this.processBounding();
	}

	@Override
	public void updateMesh() {
		for(IVertex vertex : this.vertices) {
			vertex.resetInformation();
		}
		this.processVertex();
		this.processNormal();
		this.updateModelBound();
	}

	/**
	 * Process and setup the index buffer.
	 */
	private void processIndex() {
		IntBuffer indexBuffer = BufferUtils.createIntBuffer(this.triangles.length*3);
		indexBuffer.clear();
		for(ITriangle triangle : this.triangles) {
			for(int j = 0; j < 3; j++) {
				indexBuffer.put(triangle.getVertex(j).getIndex());
			}
		}
		indexBuffer.flip();
		this.setIndexBuffer(indexBuffer);
	}

	/**
	 * Process and setup the vertex position buffer.
	 */
	private void processVertex() {
		FloatBuffer vertexBuffer = this.getVertexBuffer();
		if(vertexBuffer == null) {
			vertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
			this.setVertexBuffer(vertexBuffer);
		}
		vertexBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].processPosition();
			BufferUtils.setInBuffer(this.vertices[i].getPosition(), vertexBuffer, i);
		}
	}

	/**
	 * Process and setup the normal position buffer.
	 */
	private void processNormal() {
		// Triangles have to process the normal first in case the vertices are not in order.
		for(int i = 0; i < this.triangles.length; i++) {
			this.triangles[i].processNormal();
		}
		FloatBuffer normalBuffer = this.getNormalBuffer();
		if(normalBuffer == null) {
			normalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
			this.setNormalBuffer(normalBuffer);
		}
		normalBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getNormal(), normalBuffer, i);
		}
	}

	/**
	 * Process and setup the <code>TextureState</code> and texture UV buffer.
	 */
	private void processTexture() {
		MD5Importer instance = MD5Importer.getInstance();
		FloatBuffer textureBuffer = BufferUtils.createVector2Buffer(this.vertices.length);
		float maxU = 1; float maxV = 1; float minU = 0; float minV = 0;
		int index = 0;
		for(IVertex vertex : this.vertices) {
			BufferUtils.setInBuffer(vertex.getTextureCoords(), textureBuffer, index);
			if(vertex.getTextureCoords().x > maxU) maxU = vertex.getTextureCoords().x;
			else if(vertex.getTextureCoords().x < minU) minU = vertex.getTextureCoords().x;
			if(vertex.getTextureCoords().y > maxV) maxV = vertex.getTextureCoords().y;
			else if(vertex.getTextureCoords().y < minV) minV = vertex.getTextureCoords().y;
			index++;
		}
		this.setTextureCoords(new TexCoords(textureBuffer));
		URL url = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, this.texture);
		Texture color = TextureManager.loadTexture(url,instance.getMiniFilter(),instance.getMagFilter(),instance.getAnisotropic(),true);
		if(color != null) {
			if(maxU > 1) color.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
			else color.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Clamp);
			if(maxV > 1) color.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
			else color.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Clamp);
		}
		TextureState state = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		state.setTexture(color);
		this.setRenderState(state);
	}

	/**
	 * Process and setup the bounding volume of the <code>Mesh</code>.
	 */
	private void processBounding() {
		if(MD5Importer.getInstance().isOriented()) this.setModelBound(new OrientedBoundingBox());
		else this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.updateGeometricState(0, true);
	}

	@Override
	public IVertex getVertex(int index) {
		return this.vertices[index];
	}

	@Override
	public IWeight getWeight(int index) {
		return this.weights[index];
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return Mesh.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.texture, "Texture", null);
		oc.write(this.vertices, "Vertices", null);
		oc.write(this.triangles, "Triangles", null);
		oc.write(this.weights, "Weights", null);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		this.texture = ic.readString("Texture", null);
		temp = ic.readSavableArray("Vertices", null);
		this.vertices = new IVertex[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.vertices[i] = (IVertex)temp[i];
		}
		temp = ic.readSavableArray("Triangles", null);
		this.triangles = new ITriangle[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.triangles[i] = (ITriangle)temp[i];
		}
		temp = ic.readSavableArray("Weights", null);
		this.weights = new IWeight[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.weights[i] = (IWeight)temp[i];
		}
	}

	@Override
	public IMesh clone(IJoint[] clonedJoints) {
		// Weights need to be cloned first.
		IWeight[] clonedWeights = new IWeight[this.weights.length];
		for(int i = 0; i < clonedWeights.length; i++) clonedWeights[i] = this.weights[i].clone(clonedJoints);
		// Then pass cloned weights to clone vertices.
		IVertex[] clonedVertices = new IVertex[this.vertices.length];
		for(int i = 0; i < clonedVertices.length; i++) clonedVertices[i] = this.vertices[i].clone(clonedWeights);
		// Then pass cloned vertices to clone triangles.
		ITriangle[] clonedTriangles = new ITriangle[this.triangles.length];
		for(int i = 0; i < clonedTriangles.length; i++) clonedTriangles[i] = this.triangles[i].clone(clonedVertices);
		return new Mesh(new String(this.texture), clonedVertices, clonedTriangles, clonedWeights);
	}
}
