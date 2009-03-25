package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.md5importer.control.MD5NodeController;
import com.md5importer.enumn.ERepeatType;
import com.md5importer.interfaces.control.IMD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.model.MD5Node;
import com.md5importer.test.util.ThreadedController;


/**
 * A simple test for loading an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnim extends Test {
	
	private ThreadedController bController;
	private ThreadedController hController;
	
	@Override
	protected IMD5Node loadModel() {
		try {
			URL bodyMesh = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5mesh");
			URL bodyAnim = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine.md5anim");
			IMD5Node body = this.importer.loadMesh(bodyMesh, "body");
			IMD5Anim bAnim = this.importer.loadAnim(bodyAnim, "bodyAnim");
			this.importer.cleanup();
			URL headMesh = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5mesh");
			URL headAnim = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/sarge.md5anim");
			IMD5Node head = this.importer.loadMesh(headMesh, "head");
			IMD5Anim hAnim = this.importer.loadAnim(headAnim, "headAnim");
			body.attachChild(head, "Shoulders");
			this.importer.cleanup();
			// Create controllers.
			IMD5NodeController bodyNC = new MD5NodeController(body);
			bodyNC.setActiveAnim(bAnim, false, 0);
			IMD5NodeController headNC = new MD5NodeController(head);
			headNC.setActiveAnim(hAnim, false, 0);
			this.bController = new ThreadedController(bAnim, 700);
			this.bController.start();
			this.hController = new ThreadedController(hAnim, 700);
			this.hController.start();
			return body;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("body");
		node.setLocalScale(1);
		this.bController.getAnimController().setRepeatType(ERepeatType.Cycle);
		this.hController.getAnimController().setRepeatType(ERepeatType.Cycle);
	}

	public static void main(String[] args) {
		new TestAnim().start();
	}
	
	protected void cleanup() {
		super.cleanup();
		this.bController.stop();
		this.hController.stop();
	}
}
