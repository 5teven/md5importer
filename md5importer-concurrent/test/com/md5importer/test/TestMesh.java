package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.md5importer.interfaces.model.IMD5Node;

/**
 * A simple test for loading a static model.
 * @author Yi Wang (Neakor)
 */
public class TestMesh extends Test {
	
	protected IMD5Node body;
	protected IMD5Node head;

	protected IMD5Node setupModel() {
		try {
			URL bodyURL = TestMesh.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5mesh");
			this.body = this.importer.loadMesh(bodyURL, "body");
			this.importer.cleanup();
			URL headURL = TestMesh.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5mesh");
			this.head = this.importer.loadMesh(headURL, "head");
			this.importer.cleanup();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.body.attachChild(this.head, "Shoulders");
		this.importer.cleanup();
		return body;
	}

	@Override
	protected void setupGame() {}

	public static void main(String[] args) {
		new TestMesh().start();
	}
}
