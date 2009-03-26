package test.model.md5.binary;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import test.model.md5.Test;
import test.model.md5.TestMesh;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.util.export.binary.BinaryExporter;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

/**
 * Demo shows how to export mesh files.
 * Note that when exporting meshes, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshExport extends Test {
	private final String body = "bodymesh.jme";
	private final String head = "headmesh.jme";
	protected ModelNode bodyNode;
	protected ModelNode headNode;
	private File bodyFile;
	private File headFile;

	public static void main(String[] args) {
		new TestMeshExport().start();
	}

	@Override
	protected ModelNode loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		try {
			MD5Importer.getInstance().loadMesh(md5mesh, "ModelNode");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bodyNode = MD5Importer.getInstance().getModelNode();
		MD5Importer.getInstance().cleanup();
		URL head = TestMesh.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		try {
			MD5Importer.getInstance().loadMesh(head, "Head");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.headNode = MD5Importer.getInstance().getModelNode();
		this.rootNode.attachChild(this.headNode);
		return this.bodyNode;
	}

	@Override
	protected void setupGame() {
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL url = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/");
		String raw = url.toString().replaceAll("%20", " ");
		String path = raw.substring(raw.indexOf("/") + 1, raw.length()).replaceFirst("bin", "test");
		this.bodyFile = new File(path + this.body);
		this.headFile = new File(path + this.head);
		try {
			this.bodyFile.createNewFile();
			this.headFile.createNewFile();
		} catch (IOException e) {
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
