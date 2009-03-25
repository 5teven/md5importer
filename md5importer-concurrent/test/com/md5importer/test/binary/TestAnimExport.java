package com.md5importer.test.binary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.util.export.binary.BinaryExporter;
import com.md5importer.test.TestAnim;
import com.model.md5.controller.MD5Controller;
import com.model.md5.interfaces.IMD5Animation;
import com.model.md5.interfaces.IMD5Controller;
import com.model.md5.interfaces.IMD5Node;

/**
 * Demo shows how to export anim files.
 * Note that when exporting animations, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimExport extends TestMeshExport {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private IMD5Animation bodyAnim;
	private IMD5Animation headAnim;
	private File bodyanimFile;
	private File headanimFile;

	public static void main(String[] args) {
		new TestAnimExport().start();
	}
	
	@Override
	protected IMD5Node loadModel() {
		super.loadModel();
		URL bodyURL = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5anim");
		URL headURL = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5anim");
		try {
			this.importer.loadAnim(bodyURL, "bodyanim");
			this.bodyAnim = this.importer.getAnimation();
			this.importer.cleanup();
			this.importer.loadAnim(headURL, "heaedanim");
			this.headAnim = this.importer.getAnimation();
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
		return this.bodyNode;
	}
	
	@Override
	protected void setupGame() {
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			this.bodyanimFile = new File(bodyURL.toURI());
			this.headanimFile = new File(headURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void export() {
		try {
			BinaryExporter.getInstance().save(this.bodyAnim, this.bodyanimFile);
			BinaryExporter.getInstance().save(this.headAnim, this.headanimFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
