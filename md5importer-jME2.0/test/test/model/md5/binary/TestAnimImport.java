package test.model.md5.binary;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.model.md5.MD5Animation;
import com.model.md5.controller.MD5Controller;
import com.model.md5.interfaces.IMD5Controller;

/**
 * Simple test to show how to load in the exported animations.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimImport extends TestMeshImport {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private MD5Animation bodyAnim;
	private MD5Animation headAnim;

	public static void main(String[] args) {
		new TestAnimImport().start();
	}
	
	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			this.bodyAnim = (MD5Animation)BinaryImporter.getInstance().load(bodyURL);
			this.headAnim = (MD5Animation)BinaryImporter.getInstance().load(headURL);
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
}
