package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.model.MD5Node;

/**
 * A simple test for loading a static model.
 * @author Yi Wang (Neakor)
 */
public class TestMesh extends Test {
	
	private IMD5Node body;
	private IMD5Node head;

	protected IMD5Node loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5mesh");
		try {
			this.body = this.importer.loadMesh(md5mesh, "ModelNode");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.importer.cleanup();
		URL head = TestMesh.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5mesh");
		try {
			this.head = this.importer.loadMesh(head, "Head");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.body.attachChild(this.head, "Shoulders");
		this.importer.cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
	}

	public static void main(String[] args) {
		new TestMesh().start();
	}
}
