package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.md5importer.control.MD5AnimController;
import com.md5importer.control.MD5NodeController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.util.ThreadedUpdater;

/**
 * A simple test for loading an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnim extends TestMesh {
	
	protected IMD5Anim walk;
	protected IMD5Anim headAnim;
	
	protected IMD5NodeController bodyController;
	protected IMD5AnimController walkAnimController;
	
	protected ThreadedUpdater updater;
	
	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		this.body.swapBuffers();
		this.head.swapBuffers();
	}
	
	@Override
	protected IMD5Node setupModel() {
		try {
			super.setupModel();
			// Load animations.
			URL bodyAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5anim");
			this.walk = this.importer.loadAnim(bodyAnimURL, "walk");
			this.importer.cleanup();
			URL headAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5anim");
			this.headAnim = this.importer.loadAnim(headAnimURL, "headAnim");
			this.importer.cleanup();
			// Create controller for body and head.
			this.bodyController = new MD5NodeController(this.body);
			IMD5NodeController headController = new MD5NodeController(this.head);
			// Set active animations.
			this.bodyController.setActiveAnim(this.walk);
			headController.setActiveAnim(this.headAnim);
			// Create animation controllers.
			this.walkAnimController = new MD5AnimController(this.walk);
			IMD5AnimController headAnimController = new MD5AnimController(this.headAnim);
			// Create a threaded updater to update animations in separate thread.
			this.updater = new ThreadedUpdater(60);
			this.updater.addController(this.walkAnimController);
			this.updater.addController(headAnimController);
			this.updater.start();
			return this.body;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
	}

	public static void main(String[] args) {
		new TestAnim().start();
	}
}
