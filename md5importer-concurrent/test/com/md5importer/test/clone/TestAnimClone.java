package com.md5importer.test.clone;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.model.md5.MD5Animation;
import com.model.md5.controller.MD5Controller;
import com.model.md5.interfaces.IMD5Animation;
import com.model.md5.interfaces.IMD5Controller;

/**
 * Test to show how fast animation cloning is over reading binary file.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimClone extends TestMeshClone {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private MD5Animation bodyAnim;
	private MD5Animation headAnim;
	private double headanimtime;
	private double bodyanimtime;
	private double headanimclonetime;
	private double bodyanimclonetime;

	public static void main(String[] args) {
		new TestAnimClone().start();		
	}
	
	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		this.loadAnim();
		this.cloneAnim();
		this.printResult();
	}
	
	private void loadAnim() {
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			long start = System.nanoTime();
			this.bodyAnim = (MD5Animation)BinaryImporter.getInstance().load(bodyURL);
			long end = System.nanoTime();
			this.bodyanimtime = (end - start)/1000000.0;
			start = System.nanoTime();
			this.headAnim = (MD5Animation)BinaryImporter.getInstance().load(headURL);
			end = System.nanoTime();
			this.headanimtime = (end - start)/1000000.0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		IMD5Controller bodycontroller = new MD5Controller(this.bodyNode);
		bodycontroller.addAnimation(this.bodyAnim);
		bodycontroller.setRepeatType(1);
		bodycontroller.setActive(true);
		this.bodyNode.addController(bodycontroller);
		IMD5Controller headcontroller = new MD5Controller(this.headNode);
		headcontroller.addAnimation(this.headAnim);
		headcontroller.setRepeatType(1);
		headcontroller.setActive(true);
		this.headNode.addController(headcontroller);	
	}

	private void cloneAnim() {
		long start = System.nanoTime();
		IMD5Animation bodyclone = this.bodyAnim.clone();
		long end = System.nanoTime();
		this.bodyanimclonetime = (end - start)/1000000.0;
		start = System.nanoTime();
		IMD5Animation headclone = this.headAnim.clone();
		end = System.nanoTime();
		this.headanimclonetime = (end - start)/1000000.0;
		IMD5Controller bodycontroller = new MD5Controller(this.bodyclone);
		bodycontroller.addAnimation(bodyclone);
		bodycontroller.setRepeatType(1);
		bodycontroller.setActive(true);
		bodycontroller.setSpeed(0.2f);
		this.bodyclone.addController(bodycontroller);
		IMD5Controller headcontroller = new MD5Controller(this.headclone);
		headcontroller.addAnimation(headclone);
		headcontroller.setRepeatType(1);
		headcontroller.setActive(true);
		this.headclone.addController(headcontroller);	
	}
	
	private void printResult() {
		System.out.println("Loading body animation took: " + this.bodyanimtime + " millisecond\n");
		System.out.println("Cloning body animation took: " + this.bodyanimclonetime + " millisecond\n");
		System.out.println("Loading head animation took: " + this.headanimtime + " millisecond\n");
		System.out.println("Cloning head animation took: " + this.headanimclonetime + " millisecond\n");
	}
}
