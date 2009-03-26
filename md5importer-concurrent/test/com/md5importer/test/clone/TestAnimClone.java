package com.md5importer.test.clone;

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
 * Test to show how fast animation cloning is over reading binary file.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimClone extends TestMeshClone {

	private IMD5Anim bodyAnim;
	private IMD5Anim headAnim;

	protected ThreadedUpdater updater;
	protected ThreadedUpdater cloneUpdater;
	
	private double headanimtime;
	private double bodyanimtime;
	private double headanimclonetime;
	private double bodyanimclonetime;

	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		this.loadAnim();
		this.cloneAnim();
		this.printResult();
	}

	private void loadAnim() {
		URL bodyURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/bodyanim.jme");
		URL headURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/headanim.jme");
		try {
			long start = System.nanoTime();
			this.bodyAnim = (IMD5Anim)BinaryImporter.getInstance().load(bodyURL);
			long end = System.nanoTime();
			this.bodyanimtime = (end - start)/1000000.0;
			start = System.nanoTime();
			this.headAnim = (IMD5Anim)BinaryImporter.getInstance().load(headURL);
			end = System.nanoTime();
			this.headanimtime = (end - start)/1000000.0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Create controller for body and head.
		IMD5NodeController bodyController = new MD5NodeController(this.body);
		IMD5NodeController headController = new MD5NodeController(this.head);
		// Set active animations.
		bodyController.setActiveAnim(this.bodyAnim);
		headController.setActiveAnim(this.headAnim);
		// Create animation controllers.
		IMD5AnimController walkAnimController = new MD5AnimController(this.bodyAnim);
		IMD5AnimController headAnimController = new MD5AnimController(this.headAnim);
		// Create a threaded updater to update animations in separate thread.
		this.updater = new ThreadedUpdater(60);
		this.updater.addController(walkAnimController);
		this.updater.addController(headAnimController);
		this.updater.start();
	}

	private void cloneAnim() {
		long start = System.nanoTime();
		IMD5Anim bodyanimclone = this.bodyAnim.clone();
		long end = System.nanoTime();
		this.bodyanimclonetime = (end - start)/1000000.0;
		start = System.nanoTime();
		IMD5Anim headanimclone = this.headAnim.clone();
		end = System.nanoTime();
		this.headanimclonetime = (end - start)/1000000.0;
		
		// Create controller for body and head.
		IMD5NodeController bodyController = new MD5NodeController(this.bodyclone);
		IMD5NodeController headController = new MD5NodeController(this.headclone);
		// Set active animations.
		bodyController.setActiveAnim(bodyanimclone);
		headController.setActiveAnim(headanimclone);
		// Create animation controllers.
		IMD5AnimController walkAnimController = new MD5AnimController(bodyanimclone);
		IMD5AnimController headAnimController = new MD5AnimController(headanimclone);
		// Create a threaded updater to update animations in separate thread.
		this.cloneUpdater = new ThreadedUpdater(60);
		this.cloneUpdater.addController(walkAnimController);
		this.cloneUpdater.addController(headAnimController);
		this.cloneUpdater.start();
	}

	private void printResult() {
		System.out.println("Loading body animation took: " + this.bodyanimtime + " millisecond\n");
		System.out.println("Cloning body animation took: " + this.bodyanimclonetime + " millisecond\n");
		System.out.println("Loading head animation took: " + this.headanimtime + " millisecond\n");
		System.out.println("Cloning head animation took: " + this.headanimclonetime + " millisecond\n");
	}
	
	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
		this.cloneUpdater.stop();
	}

	public static void main(String[] args) {
		new TestAnimClone().start();		
	}
}
