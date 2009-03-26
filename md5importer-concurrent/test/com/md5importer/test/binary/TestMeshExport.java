package com.md5importer.test.binary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryExporter;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.Test;

/**
 * Demo shows how to export mesh files.
 * Note that when exporting meshes, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshExport extends Test {
	
	protected IMD5Node body;
	protected IMD5Node head;
	
	private File bodyfile;
	private File headfile;

	@Override
	protected IMD5Node setupModel() {
		try {
			URL bodyURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/marine.md5mesh");
			this.body = this.importer.loadMesh(bodyURL, "body");
			this.importer.cleanup();
			
			URL headURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/sarge.md5mesh");
			this.head = this.importer.loadMesh(headURL, "head");
			this.importer.cleanup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rootNode.attachChild((Spatial)this.head);
		return this.body;
	}

	@Override
	protected void setupGame() {
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL bodyURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/bodymesh.jme");
		URL headURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/headmesh.jme");
		try {
			this.bodyfile = new File(bodyURL.toURI());
			this.headfile = new File(headURL.toURI());
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
			BinaryExporter.getInstance().save(this.body, this.bodyfile);
			BinaryExporter.getInstance().save(this.head, this.headfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestMeshExport().start();
	}
}
