package test.model.md5;

import java.io.IOException;
import java.net.URL;

import com.model.md5.MD5Node;
import com.model.md5.interfaces.IMD5Node;


/**
 * A simple test for loading a static model.
 * @author Yi Wang (Neakor)
 */
public class TestMesh extends Test{

	public static void main(String[] args) {
		new TestMesh().start();
	}
	
	protected IMD5Node loadModel() {
		URL md5mesh = TestMesh.class.getClassLoader().getResource("test/model/md5/data/marine.md5mesh");
		try {
			this.importer.loadMesh(md5mesh, "ModelNode");
		} catch (IOException e) {
			e.printStackTrace();
		}
		IMD5Node body = this.importer.getMD5Node();
		this.importer.cleanup();
		URL head = TestMesh.class.getClassLoader().getResource("test/model/md5/data/sarge.md5mesh");
		try {
			this.importer.loadMesh(head, "Head");
		} catch (IOException e) {
			e.printStackTrace();
		}
		body.attachChild(this.importer.getMD5Node(), "Shoulders");
		body.flagUpdate();
		this.importer.cleanup();
		return body;
	}

	@Override
	protected void setupGame() {
		MD5Node node = (MD5Node)this.rootNode.getChild("ModelNode");
		node.setLocalScale(1);
	}
}
