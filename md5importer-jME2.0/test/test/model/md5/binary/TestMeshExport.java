package test.model.md5.binary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import test.model.md5.Test;
import test.model.md5.TestMesh;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;
import com.model.md5.interfaces.IMD5Node;

/**
 * Demo shows how to export mesh files.
 * Note that when exporting meshes, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshExport extends Test {
	private final String body = "bodymesh.jme";
	private final String head = "headmesh.jme";
	protected IMD5Node bodyNode;
	protected IMD5Node headNode;
	private File bodyFile;
	private File headFile;

	public static void main(String[] args) {
		new TestMeshExport().start();
	}

	@Override
	protected IMD5Node loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		try {
			this.importer.loadMesh(md5mesh, "ModelNode");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bodyNode = this.importer.getMD5Node();
		this.importer.cleanup();
		URL head = TestMesh.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		try {
			this.importer.loadMesh(head, "Head");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.headNode = this.importer.getMD5Node();
		this.rootNode.attachChild((Spatial)this.headNode);
		return this.bodyNode;
	}

	@Override
	protected void setupGame() {
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			this.bodyFile = new File(bodyURL.toURI());
			this.headFile = new File(headURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("export", false)) {
			this.export();
		}
	}

	protected void export() {
		try {
			BinaryExporter.getInstance().save(this.bodyNode, this.bodyFile);
			BinaryExporter.getInstance().save(this.headNode, this.headFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
