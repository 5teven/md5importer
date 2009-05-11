package com.md5importer.model.mesh;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.OrientedBoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.BufferUtils;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.md5importer.interfaces.model.mesh.IJoint;
import com.md5importer.interfaces.model.mesh.IMesh;
import com.md5importer.interfaces.model.mesh.primitive.ITriangle;
import com.md5importer.interfaces.model.mesh.primitive.IVertex;
import com.md5importer.interfaces.model.mesh.primitive.IWeight;

/**
 * <code>Mesh</code> defines the concrete implementation of a mesh.
 * It does not directly process any geometric information but
 * delegates the process down to the primitive elements it maintains.
 * <p>
 * <code>Mesh</code> cannot be cloned directly. The cloning process
 * of a <code>Mesh</code> can only be initiated by the cloning process
 * of the parent <code>IIMD5Node</code>.
 * <p>
 * This class is used internally by <code>MD5Importer</code> only.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 05-11-2009 18:29 EST
 */
public class Mesh extends TriMesh implements IMesh {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -6431941710991131243L;
	/**
	 * The special <code>String</code> texture extension.
	 */
	private static String extension;
	/**
	 * The <code>String</code> color map file name.
	 */
	private String color;
	/**
	 * The <code>String</code> normal map file name.
	 */
	private String normal;
	/**
	 * The <code>String</code> specular map file name.
	 */
	private String specular;
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
	 * The <code>Integer</code> anisotropic level value.
	 */
	private int anisotropic;
	/**
	 * The <code>MinificationFilter</code> enumeration.
	 */
	private MinificationFilter miniFilter;
	/**
	 * The <code>MagnificationFilter</code> enumeration.
	 */
	private MagnificationFilter magFilter;
	/**
	 * The flag indicates if oriented bounding should be used.
	 */
	private boolean orientedBounding;
	/**
	 * The temporary <code>List</code> of <code>IVertex</code>
	 * with same positions to average normal.
	 */
	private final List<IVertex> tempVertices;
	/**
	 * The back vertex <code>FloatBuffer</code> for updating.
	 */
	private FloatBuffer backVertexBuffer;
	/**
	 * The back normal <code>FloatBuffer</code> for updating.
	 */
	private FloatBuffer backNormalBuffer;

	/**
	 * Constructor of <code>Mesh</code>.
	 */
	public Mesh() {
		super();
		this.tempVertices = new ArrayList<IVertex>(32);
	}

	/**
	 * Constructor of <code>Mesh</code>.
	 * @param texture The <code>String</code> texture file name without extension.
	 * @param vertices The array of <code>IVertex</code> in this mesh.
	 * @param triangles The array of <code>ITriangle</code> in this mesh.
	 * @param weights The array of <code>IWeight</code> in this mesh.
	 */
	public Mesh(String texture, IVertex[] vertices, ITriangle[] triangles, IWeight[] weights, int anisotropic, MinificationFilter miniFilter,
			MagnificationFilter magFilter, boolean orientedBounding) {
		this();
		this.color = texture;
		this.vertices = vertices;
		this.triangles = triangles;
		this.weights = weights;
		this.anisotropic = anisotropic;
		this.miniFilter = miniFilter;
		this.magFilter = magFilter;
		this.orientedBounding = orientedBounding;
	}

	@Override
	public void initialize(String name) {
		this.setName(name + "Mesh");
		this.setNormalsMode(Spatial.NormalsMode.AlwaysNormalize);
		this.processIndex();
		this.processVertex();
		this.processNormal(true);
		this.processTexture();
		this.processBounding();
	}

	@Override
	public void updateMesh() {
		this.processVertex();
		this.processNormal(false);
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
		if(this.getVertexBuffer() == null) {
			final FloatBuffer frontBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
			this.setVertexBuffer(frontBuffer);
			this.backVertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		}
		this.backVertexBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			this.vertices[i].resetInformation();
			this.vertices[i].processPosition();
			BufferUtils.setInBuffer(this.vertices[i].getPosition(), this.backVertexBuffer, i);
		}
	}

	/**
	 * Process and setup the normal position buffer.
	 * @param init The <code>Boolean</code> initialization flag.
	 */
	private void processNormal(boolean init) {
		// Triangles have to process the normal first in case the vertices are not in order.
		for(int i = 0; i < this.triangles.length; i++) {
			this.triangles[i].processNormal();
		}
		// Average vertex normals with same vertex positions.
		if(init) this.averageNormal();
		// Put into buffer.
		if(this.getNormalBuffer() == null) {
			FloatBuffer frontBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
			this.setNormalBuffer(frontBuffer);
			this.backNormalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		}
		this.backNormalBuffer.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			BufferUtils.setInBuffer(this.vertices[i].getNormal(), this.backNormalBuffer, i);
		}
	}

	/**
	 * Average normals for vertices with same position.
	 */
	private void averageNormal() {
		this.tempVertices.clear();
		for(int i = 0; i < this.vertices.length; i++) {
			final IVertex v1 = this.vertices[i];
			this.tempVertices.add(v1);
			// Find all vertices with same position.
			for(int j = 0; j < this.vertices.length; j++) {
				final IVertex v2 = this.vertices[j];
				if(v1 != v2 && v2.getPosition().equals(v1.getPosition())) {
					this.tempVertices.add(v2);
				}
			}
			// Average vertices in list.
			float x = 0;
			float y = 0;
			float z = 0;
			for(IVertex vertex : this.tempVertices) {
				x += vertex.getNormal().getX();
				y += vertex.getNormal().getY();
				z += vertex.getNormal().getZ();
			}
			final int size = this.tempVertices.size();
			x = x / size;
			y = y / size;
			z = z / size;
			final Vector3f sharedNormal = new Vector3f(x, y, z);
			sharedNormal.normalizeLocal();
			for(IVertex vertex : this.tempVertices) {
				vertex.setNormalReference(sharedNormal);
			}
			// Clear out this group.
			this.tempVertices.clear();
		}
	}

	/**
	 * Process and setup the <code>TextureState</code> and texture UV buffer.
	 */
	private void processTexture() {
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

		// Get texture state.
		TextureState state = (TextureState)this.getRenderState(StateType.Texture);
		if(state == null) {
			state= DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
			this.setRenderState(state);
		}
		// Set color map.
		if(this.color != null) this.loadSetMap(this.color, state, maxU, maxV, 0);
		// Set normal map.
		if(this.normal != null) this.loadSetMap(this.normal, state, maxU, maxV, 1);
		// Set specular map.
		if(this.specular != null) this.loadSetMap(this.specular, state, maxU, maxV, 2);
	}
	
	/**
	 * Load and set the texture map at given unit to given state.
	 * @param map The <code>String</code> map path.
	 * @param state The <code>TextureState</code> to be set to.
	 * @param maxU The <code>Float</code> maximum U coordinate.
	 * @param maxV The <code>Float</code> maximum V coordinate.
	 * @param unit The <code>Integer</code> texture unit to set to.
	 */
	private void loadSetMap(String map, TextureState state, float maxU, float maxV, int unit) {
		if(Mesh.extension != null && Mesh.extension.length() > 0) state.setTexture(this.loadTexture(this.buildPath(map), maxU, maxV), unit);
		else state.setTexture(this.loadTexture(map, maxU, maxV), unit);
	}

	/**
	 * Load the texture linked by given file and set its wrap modes based on given values.
	 * @param file The <code>String</code> file location.
	 * @param maxU The <code>Float</code> maximum u value.
	 * @param maxV The <code>Float</code> maximum v value.
	 * @return The loaded <code>Texture</code> instance.
	 */
	private Texture loadTexture(String file, float maxU, float maxV) {
		// Add a locator according to the texture string.
		int last = file.lastIndexOf("/") + 1;
		if(last < 0) last = file.length();
		File path = new File(file.substring(0, last));
		try {
			if(path != null) {
				SimpleResourceLocator locator = new SimpleResourceLocator(path.toURI().toURL());
				ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		// Load URL.
		URL url = ResourceLocatorTool.locateResource(ResourceLocatorTool.TYPE_TEXTURE, file);
		// Load the texture and set the wrap mode.
		Texture map = TextureManager.loadTexture(url, this.miniFilter, this.magFilter, this.anisotropic, true);
		if(map != null) {
			if(maxU > 1) map.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
			else map.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Clamp);
			if(maxV > 1) map.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Repeat);
			else map.setWrap(Texture.WrapAxis.T, Texture.WrapMode.Clamp);
		}
		return map;
	}
	
	/**
	 * Build a texture path with specified extension and original path.
	 * @param original The <code>String</code> original texture path.
	 * @return The <code>String</code> texture path with proper extension.
	 */
	private String buildPath(String original) {
		final StringBuilder builder = new StringBuilder();
		return builder.append(original.substring(0, original.lastIndexOf("."))).append(Mesh.extension).toString();
	}

	/**
	 * Process and setup the bounding volume of the <code>Mesh</code>.
	 */
	private void processBounding() {
		if(this.orientedBounding) this.setModelBound(new OrientedBoundingBox());
		else this.setModelBound(new BoundingBox());
		this.updateModelBound();
		this.updateGeometricState(0, true);
	}

	@Override
	public void swapBuffer() {
		// Retrieve current front buffer.
		final FloatBuffer oldFrontVertexBuffer = this.vertBuf;
		final FloatBuffer oldFrontNormalBuffer = this.normBuf;
		// Set back buffer to be front buffer.
		this.vertBuf = this.backVertexBuffer;
		this.normBuf = this.backNormalBuffer;
		// Store old front buffer as back buffer.
		this.backVertexBuffer = oldFrontVertexBuffer;
		this.backNormalBuffer = oldFrontNormalBuffer;
	}

	@Override
	public void setJoints(IJoint[] joints) {
		for(IWeight weight : this.weights) {
			weight.setJoint(joints[weight.getJoint().getIndex()]);
		}
	}
	
	/**
	 * Set the texture extension to use.
	 * @param extension The <code>String</code> extension.
	 */
	public static void setExtension(String extension) {
		Mesh.extension = extension;
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
		// Save all texture locations.
		TextureState state = (TextureState)this.getRenderState(StateType.Texture);
		Texture colorMap = state.getTexture(0);
		Texture normalMap = state.getTexture(1);
		Texture specularMap = state.getTexture(2);
		if(colorMap != null) {
			String colorRaw = colorMap.getImageLocation();
			oc.write(colorRaw.substring(colorRaw.indexOf("/"), colorRaw.length()), "ColorMap", null);
		}
		if(normalMap != null) {
			String normalRaw = normalMap.getImageLocation();
			oc.write(normalRaw.substring(normalRaw.indexOf("/"), normalRaw.length()), "NormalMap", null);
		}
		if(specularMap != null) {
			String specularRaw = specularMap.getImageLocation();
			oc.write(specularRaw.substring(specularRaw.indexOf("/"), specularRaw.length()), "SpecularMap", null);
		}
		// Write out primitives.
		oc.write(this.vertices, "Vertices", null);
		oc.write(this.triangles, "Triangles", null);
		oc.write(this.weights, "Weights", null);
		// Write out settings.
		oc.write(this.anisotropic, "Anisotropic", 0);
		oc.write(this.miniFilter.name(), "MinFilter", null);
		oc.write(this.magFilter.name(), "MagFilter", null);
		oc.write(this.orientedBounding, "OrientedBounding", false);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		super.read(im);
		Savable[] temp = null;
		InputCapsule ic = im.getCapsule(this);
		// Read in texture map locations.
		this.color = ic.readString("ColorMap", null);
		this.normal = ic.readString("NormalMap", null);
		this.specular = ic.readString("SpecularMap", null);
		// Read in primitives.
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
		// Read in settings.
		this.anisotropic = ic.readInt("Anisotropic", 0);
		this.miniFilter = MinificationFilter.valueOf(ic.readString("MinFilter", null));
		this.magFilter = MagnificationFilter.valueOf(ic.readString("MagFilter", null));
		this.orientedBounding = ic.readBoolean("OrientedBounding", false);
		// Create back buffers.
		this.backVertexBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
		this.backNormalBuffer = BufferUtils.createVector3Buffer(this.vertices.length);
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
		return new Mesh(new String(this.color), clonedVertices, clonedTriangles, clonedWeights, this.anisotropic, this.miniFilter, this.magFilter, this.orientedBounding);
	}
}
