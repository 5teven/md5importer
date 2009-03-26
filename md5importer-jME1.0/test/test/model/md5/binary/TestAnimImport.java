package test.model.md5.binary;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.model.md5.JointAnimation;
import com.model.md5.controller.JointController;

/**
 * Simple test to show how to load in the exported animations.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimImport extends TestMeshImport {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private JointAnimation bodyAnim;
	private JointAnimation headAnim;

	public static void main(String[] args) {
		new TestAnimImport().start();
	}
	
	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			this.bodyAnim = (JointAnimation)BinaryImporter.getInstance().load(bodyURL);
			this.headAnim = (JointAnimation)BinaryImporter.getInstance().load(headURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JointController bodycontroller = new JointController(this.bodyNode.getJoints());
		bodycontroller.addAnimation(this.bodyAnim);
		bodycontroller.setRepeatType(1);
		bodycontroller.setActive(true);
		this.bodyNode.addController(bodycontroller);
		JointController headcontroller = new JointController(this.headNode.getJoints());
		headcontroller.addAnimation(this.headAnim);
		headcontroller.setRepeatType(1);
		headcontroller.setActive(true);
		this.headNode.addController(headcontroller);	
	}
}
