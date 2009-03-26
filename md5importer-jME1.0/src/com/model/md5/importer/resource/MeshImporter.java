package com.model.md5.importer.resource;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.model.md5.ModelNode;
import com.model.md5.exception.InvalidVersionException;
import com.model.md5.importer.MD5Importer;
import com.model.md5.resource.mesh.Joint;
import com.model.md5.resource.mesh.Mesh;
import com.model.md5.resource.mesh.primitive.Triangle;
import com.model.md5.resource.mesh.primitive.Vertex;
import com.model.md5.resource.mesh.primitive.Weight;

/**
 * <code>MeshImporter</code> is responsible for importing MD5Mesh resources and
 * constructing the final <code>ModelNode</code> instance.
 * <p>
 * <code>MeshImporter</code> is used by <code>MD5Importer</code> internally only.
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 05-01-2008 18:41 EST
 */
public class MeshImporter {
	/**
	 * The <code>StreamTokenizer</code> instance.
	 */
	private StreamTokenizer reader;
	/**
	 * The array of <code>Joint</code> that form the skeleton.
	 */
	private Joint[] joints;
	/**
	 * The array of <code>Mesh</code> which represents the actual geometry.
	 */
	private Mesh[] meshes;
	/**
	 * The final <code>ModelNode</code> instance.
	 */
	private ModelNode modelNode;

	/**
	 * Constructor of <code>MeshImporter</code>.
	 * @param reader The <code>StreamTokenizer</code> instance setup for reading file.
	 */
	public MeshImporter(StreamTokenizer reader) {
		this.reader = reader;
	}

	/**
	 * Load the md5mesh file and construct the final <code>ModelNode</code>.
	 * @param name The name of the loaded <code>ModelNode</code>.
	 * @return The loaded <code>ModelNode</code> instance.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public ModelNode loadMesh(String name) throws IOException {
		this.modelNode = new ModelNode(name);
		this.processSkin();
		this.constructSkinMesh();
		return this.modelNode;
	}

	/**
	 * Process the information in md5mesh file.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processSkin() throws IOException {
		String sval = null;
		while(this.reader.nextToken() != StreamTokenizer.TT_EOF) {
			sval = this.reader.sval;
			if(sval != null) {
				if(sval.equals("MD5Version")) {
					this.reader.nextToken();
					if(this.reader.nval != MD5Importer.version) throw new InvalidVersionException((int)this.reader.nval);
				} else if(sval.equals("numJoints")) {
					this.reader.nextToken();
					this.joints = new Joint[(int)this.reader.nval];
				} else if(sval.equals("numMeshes")) {
					this.reader.nextToken();
					this.meshes = new Mesh[(int)this.reader.nval];
				} else if(sval.equals("joints")) {
					this.reader.nextToken();
					this.processJoints();
				} else if(sval.equals("mesh")) {
					this.reader.nextToken();
					this.processMesh();
				}
			}
		}
	}

	/**
	 * Process the information to construct all <code>Joint</code>.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processJoints() throws IOException {
		int jointIndex = 0;
		int type = -4;
		// The index of the 6 transform values.
		int transIndex = 0;
		while(this.reader.nextToken() != '}' && jointIndex < this.joints.length) {
			type = this.reader.ttype;
			switch(type) {
			case '"':
				this.joints[jointIndex] = new Joint(this.reader.sval, this.modelNode);
				break;
			case StreamTokenizer.TT_NUMBER:
				this.joints[jointIndex].setParent((int)this.reader.nval);
				break;
			case '(':
				while(this.reader.nextToken() != ')') {
					this.joints[jointIndex].setTransform(transIndex, (float)this.reader.nval);
					transIndex++;
				}
				break;
			case StreamTokenizer.TT_EOL:
				if(transIndex > 5) {
					transIndex = 0;
					jointIndex++;
				}
				break;
			}
		}
	}

	/**
	 * Process the information to construct a single <code>Mesh</code>.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processMesh() throws IOException {
		int meshIndex = -1;
		for(int i = 0; i < this.meshes.length && meshIndex == -1; i++) {
			if(this.meshes[i] == null) {
				this.meshes[i] = new Mesh(this.modelNode);
				meshIndex = i;
			}
		}
		while(this.reader.nextToken() != '}') {
			if(this.reader.ttype == StreamTokenizer.TT_WORD) {
				if(this.reader.sval.equals("shader")) {
					this.reader.nextToken();
					this.meshes[meshIndex].setTexture(this.reader.sval);
				} else if(this.reader.sval.equals("numverts")) {
					this.reader.nextToken();
					this.meshes[meshIndex].setVrticesCount((int)this.reader.nval);
				} else if(this.reader.sval.equals("vert")) {
					this.processVertex(this.meshes[meshIndex]);
				} else if(this.reader.sval.equals("numtris")) {
					this.reader.nextToken();
					this.meshes[meshIndex].setTrianglesCount((int)this.reader.nval);
				} else if(this.reader.sval.equals("tri")) {
					this.processTriangle(this.meshes[meshIndex]);
				} else if(this.reader.sval.equals("numweights")) {
					this.reader.nextToken();
					this.meshes[meshIndex].setWeightCount((int)this.reader.nval);
				} else if(this.reader.sval.equals("weight")) {
					this.processWeight(this.meshes[meshIndex]);
				}
			}
		}
	}

	/**
	 * Process the information to construct a single <code>Vertex</code>.
	 * @param mesh The <code>Mesh</code> that is being processed.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processVertex(Mesh mesh) throws IOException {
		int pointer = 0;
		Vertex vertex = new Vertex(mesh);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				switch(pointer) {
				case 0:
					mesh.setVertex((int)this.reader.nval, vertex);
					pointer++;
					break;
				case 1:
					float u = (float)this.reader.nval;
					pointer++;
					this.reader.nextToken();
					float v = (float)this.reader.nval;				
					vertex.setTextureCoords(u, v);
					pointer++;
					break;
				case 3:
					int start = (int)this.reader.nval;
					pointer++;
					this.reader.nextToken();
					int length = (int)this.reader.nval;
					vertex.setWeightIndices(start, length);
					pointer++;
					break;
				}
			}
		}
	}

	/**
	 * Process the information to construct in a single <code>Triangle</code>.
	 * @param mesh The <code>Mesh</code> that is being processed.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processTriangle(Mesh mesh) throws IOException {
		int pointer = 0;
		int index = -1;
		Triangle triangle = new Triangle(mesh);
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				if(pointer == 0) {
					mesh.setTriangle((int)this.reader.nval, triangle);
					pointer++;
				} else if(pointer >= 1 && pointer <= 3) {
					switch(pointer) {
					case 1:	index = 0; break;
					case 2: index = 2; break;
					case 3: index = 1; break;
					}
					triangle.setVertexIndex(index, (int)this.reader.nval);
					mesh.getVertex((int)this.reader.nval).incrementUsedTimes();
					pointer++;
				}
			}
		}
	}

	/**
	 * Process the information to construct in a single <code>Weight</code>.
	 * @param mesh The <code>Mesh</code> that is being processed.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	private void processWeight(Mesh mesh) throws IOException {
		int pointer = 0;
		Weight weight = new Weight();
		while(this.reader.nextToken() != StreamTokenizer.TT_EOL) {
			if(this.reader.ttype == StreamTokenizer.TT_NUMBER) {
				if(pointer == 0) {
					mesh.setWeight((int)this.reader.nval, weight);
					pointer++;
				} else if(pointer == 1) {
					weight.setJointIndex((int)this.reader.nval);
					pointer++;
				} else if(pointer == 2) {
					weight.setWeightValue((float)this.reader.nval);
					pointer++;
				} else if(pointer >=3 && pointer <= 5) {
					weight.setPosition(pointer - 3, (float)this.reader.nval);
					pointer++;
				}
			}
		}
	}

	/**
	 * Construct the <code>ModelNode</code> with information read in.
	 */
	private void constructSkinMesh() {
		Joint parent = null;
		for(int i = this.joints.length - 1; i >= 0; i--) {
			if(this.joints[i].getParent() < 0) this.joints[i].processTransform(null, null);
			else {
				parent = this.joints[this.joints[i].getParent()];
				this.joints[i].processTransform(parent.getTranslation(), parent.getOrientation());
			}
		}
		for(int i = 0; i < this.joints.length; i++) {
			if(this.joints[i].getParent() < 0) {
				this.joints[i].getOrientation().set(MD5Importer.base.mult(this.joints[i].getOrientation()));
			}
		}
		this.modelNode.setJoints(this.joints);
		this.modelNode.setMeshes(this.meshes);
		this.modelNode.initialize();
		this.joints = null;
		this.meshes = null;
	}
}
