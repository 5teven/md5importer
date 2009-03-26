package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Spatial;
import com.md5importer.control.BlendController;
import com.md5importer.control.MD5AnimController;
import com.md5importer.control.MD5NodeController;
import com.md5importer.interfaces.control.IBlendController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.util.ThreadedUpdater;

public class TestFading extends Test {

	private IMD5Node body;
	private IMD5Node head;
	
	private IMD5Anim walk;
	private IMD5Anim stand;
	
	private IMD5NodeController bodyController;
	private IMD5AnimController walkAnimController;
	private IMD5AnimController standAnimController;
	
	private ThreadedUpdater updater;
	
	private IBlendController blender;
	
	@Override
	protected IMD5Node loadModel() {
		try {
			// Load meshes.
			this.loadMeshes();
			// Load animations.
			URL walkAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5anim");
			this.walk = this.importer.loadAnim(walkAnimURL, "walk");
			this.importer.cleanup();
			URL standAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine_stand.md5anim");
			this.stand = this.importer.loadAnim(standAnimURL, "stand");
			this.importer.cleanup();
			URL headAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5anim");
			IMD5Anim headAnim = this.importer.loadAnim(headAnimURL, "headAnim");
			this.importer.cleanup();
			// Create controller for body and head.
			this.bodyController = new MD5NodeController(this.body);
			IMD5NodeController headController = new MD5NodeController(this.head);
			// Set active animations.
			this.bodyController.setActiveAnim(this.walk);
			headController.setActiveAnim(headAnim);
			// Create animation controllers.
			this.walkAnimController = new MD5AnimController(this.walk);
			this.standAnimController = new MD5AnimController(this.stand);
			this.standAnimController.setActive(false);
			IMD5AnimController headAnimController = new MD5AnimController(headAnim);
			// Create a blender.
			this.blender = new BlendController(this.body, this.bodyController);
			// Create a threaded updater to update animations in separate thread.
			this.updater = new ThreadedUpdater(60);
			this.updater.addController(this.walkAnimController);
			this.updater.addController(this.standAnimController);
			this.updater.addController(headAnimController);
			this.updater.addController(this.blender);
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
		Spatial node = this.rootNode.getChild("body");
		node.setLocalScale(1);
		KeyBindingManager.getKeyBindingManager().set("fade", KeyInput.KEY_O);
	}

	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("fade", false)) {
			if(this.bodyController.getActiveAnim().getName().equals("walk")) {
				this.walkAnimController.setActive(false);
				this.blender.blend(this.stand, this.standAnimController, 0.5f);
			} else if(this.bodyController.getActiveAnim().getName().equals("stand")) {
				this.standAnimController.setActive(false);
				this.blender.blend(this.walk, this.walkAnimController, 0.5f);
			}
		}
	}
	
	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
	}

	public static void main(String[] args) {
		new TestFading().start();
	}
}
