package test.model.md5;

import java.io.IOException;
import java.net.URL;

import com.jme.scene.Controller;
import com.model.md5.MD5Node;


/**
 * A simple test for loading an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnim extends Test{

	public static void main(String[] args) {
		new TestAnim().start();
	}

	@Override
	protected MD5Node loadModel() {
		URL bodyMesh = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		URL bodyAnim = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5anim");
		try {
			this.importer.load(bodyMesh, "ModelNode", bodyAnim, "BodyAnimation", Controller.RT_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		MD5Node body = (MD5Node) this.importer.getMD5Node();
		this.importer.cleanup();
		URL headMesh = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		URL headAnim = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5anim");
		try {
			this.importer.load(headMesh, "Head", headAnim, "HeadAnimation", Controller.RT_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		body.attachChild(this.importer.getMD5Node(), "Shoulders");
		this.importer.cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
		node.getController(0).setSpeed(2f);
	}
}
