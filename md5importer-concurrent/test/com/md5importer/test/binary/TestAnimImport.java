package com.md5importer.test.binary;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.md5importer.control.MD5AnimController;
import com.md5importer.control.MD5NodeController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.test.util.ThreadedUpdater;

/**
 * Simple test to show how to load in the exported animations.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimImport extends TestMeshImport {
	
	protected IMD5Anim walk;
	protected IMD5Anim headAnim;
	
	protected IMD5NodeController bodyController;
	protected IMD5AnimController walkAnimController;
	
	protected ThreadedUpdater updater;

	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		this.importAnims();
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
	}
	
	private void importAnims() {
		URL walkURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/bodyanim.jme");
		URL headURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/headanim.jme");
		try {
			this.walk = (IMD5Anim)BinaryImporter.getInstance().load(walkURL);
			this.headAnim = (IMD5Anim)BinaryImporter.getInstance().load(headURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		this.body.swapBuffers();
		this.head.swapBuffers();
	}
	
	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
	}
	
	public static void main(String[] args) {
		new TestAnimImport().start();
	}
}
