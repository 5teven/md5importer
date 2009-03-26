package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.md5importer.control.MD5AnimController;
import com.md5importer.control.MD5NodeController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.model.MD5Node;
import com.md5importer.test.util.ThreadedUpdater;


/**
 * A simple test for loading an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnim extends Test {
	
	private IMD5Node body;
	private IMD5Node head;
	private ThreadedUpdater updater;
	
	@Override
	protected IMD5Node loadModel() {
		try {
			// Load meshes.
			this.loadMeshes();
			// Load animations.
			URL bodyAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5anim");
			IMD5Anim bodyAnim = this.importer.loadAnim(bodyAnimURL, "walking");
			this.importer.cleanup();
			URL headAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5anim");
			IMD5Anim headAnim = this.importer.loadAnim(headAnimURL, "headAnim");
			this.importer.cleanup();
			// Create controller for body and head.
			IMD5NodeController bodyController = new MD5NodeController(this.body);
			IMD5NodeController headController = new MD5NodeController(this.head);
			// Set active animations.
			bodyController.setActiveAnim(bodyAnim);
			headController.setActiveAnim(headAnim);
			// Create animation controllers.
			IMD5AnimController bodyAnimController = new MD5AnimController(bodyAnim);
			IMD5AnimController headAnimController = new MD5AnimController(headAnim);
			// Create a threaded updater to update animations in separate thread.
			this.updater = new ThreadedUpdater(60);
			this.updater.addController(bodyAnimController);
			this.updater.addController(headAnimController);
			this.updater.start();
			return this.body;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void loadMeshes() {
		try {
			URL bodyMeshURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5mesh");
			this.body = this.importer.loadMesh(bodyMeshURL, "body");
			this.importer.cleanup();
			URL headMeshURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5mesh");
			this.head = this.importer.loadMesh(headMeshURL, "head");
			this.body.attachChild(this.head, "Shoulders");
			this.importer.cleanup();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("body");
		node.setLocalScale(1);
	}

	public static void main(String[] args) {
		new TestAnim().start();
	}
	
	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
	}
}
