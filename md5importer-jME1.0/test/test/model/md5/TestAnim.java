package test.model.md5;

import java.io.IOException;
import java.net.URL;

import com.jme.scene.Controller;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;


/**
 * A simple test for loading an animated model.
 * @author Yi Wang (Neakor)
 */
public class TestAnim extends Test{

	public static void main(String[] args) {
		new TestAnim().start();
	}

	@Override
	protected ModelNode loadModel() {
		URL bodyMesh = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		URL bodyAnim = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5anim");
		try {
			MD5Importer.getInstance().load(bodyMesh, "ModelNode", bodyAnim, "BodyAnimation", Controller.RT_CYCLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ModelNode body = MD5Importer.getInstance().getModelNode();
		MD5Importer.getInstance().cleanup();
		URL headMesh = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		URL headAnim = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5anim");
		try {
			MD5Importer.getInstance().load(headMesh, "Head", headAnim, "HeadAnimation", Controller.RT_CYCLE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		body.attachChild(MD5Importer.getInstance().getModelNode(), "Shoulders");
		MD5Importer.getInstance().cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		ModelNode node = (ModelNode)this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
		node.getController(0).setSpeed(2f);
	}
}
