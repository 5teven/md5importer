package test.model.md5;

import java.io.IOException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IMD5Controller;
import com.model.md5.interfaces.IMD5Node;

public class TestFading extends Test{
	private IMD5Controller controller;

	public static void main(String[] args) {
		new TestFading().start();	
	}

	@Override
	protected IMD5Node loadModel() {
		URL bodyMesh = TestFading.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		URL bodyAnim = TestFading.class.getClassLoader().getResource("test/model/md5/data/marine.md5anim");
		try {
			MD5Importer.getInstance().load(bodyMesh, "ModelNode", bodyAnim, "BodyAnimation", Controller.RT_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		IMD5Node body = MD5Importer.getInstance().getModelNode();
		MD5Importer.getInstance().cleanup();
		URL headMesh = TestFading.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		URL headAnim = TestFading.class.getClassLoader().getResource("test/model/md5/data/sarge.md5anim");
		try {
			MD5Importer.getInstance().load(headMesh, "Head", headAnim, "HeadAnimation", Controller.RT_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		body.attachChild(MD5Importer.getInstance().getModelNode(), "Shoulders");
		MD5Importer.getInstance().cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		Spatial node = this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
		node.getController(0).setSpeed(0.2f);
		this.controller = (IMD5Controller)node.getController(0);
		this.addFadingAnim();
		this.setupKey();
	}
	
	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("fade", false)) {
			if(this.controller.getActiveAnimation().getName() != "Stand") {
				this.controller.setFading("Stand", 3, false);
			} else {
				this.controller.setFading("BodyAnimation", 3, false);
			}
		}

	}
	
	private void addFadingAnim() {
		URL bodyAnim = TestFading.class.getClassLoader().getResource("test/model/md5/data/marine_stand.md5anim");
		try {
			MD5Importer.getInstance().loadAnim(bodyAnim, "Stand");
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.controller.addAnimation(MD5Importer.getInstance().getAnimation());
	}
	
	private void setupKey() {
		KeyBindingManager.getKeyBindingManager().set("fade", KeyInput.KEY_O);
	}
}
