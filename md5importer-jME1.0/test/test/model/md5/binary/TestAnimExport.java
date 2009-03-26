package test.model.md5.binary;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import test.model.md5.TestAnim;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.util.export.binary.BinaryExporter;
import com.model.md5.JointAnimation;
import com.model.md5.ModelNode;
import com.model.md5.controller.JointController;
import com.model.md5.importer.MD5Importer;

/**
 * Demo shows how to export anim files.
 * Note that when exporting animations, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimExport extends TestMeshExport {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private JointAnimation bodyAnim;
	private JointAnimation headAnim;
	private File bodyanimFile;
	private File headanimFile;

	public static void main(String[] args) {
		new TestAnimExport().start();
	}
	
	@Override
	protected ModelNode loadModel() {
		super.loadModel();
		URL bodyURL = TestAnim.class.getClassLoader().getResource("test/model/md5/data/marine.md5anim");
		URL headURL = TestAnim.class.getClassLoader().getResource("test/model/md5/data/sarge.md5anim");
		try {
			MD5Importer.getInstance().loadAnim(bodyURL, "bodyanim");
			this.bodyAnim = MD5Importer.getInstance().getAnimation();
			MD5Importer.getInstance().cleanup();
			MD5Importer.getInstance().loadAnim(headURL, "heaedanim");
			this.headAnim = MD5Importer.getInstance().getAnimation();
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
		return this.bodyNode;
	}
	
	@Override
	protected void setupGame() {
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL url = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/");
		String raw = url.toString().replaceAll("%20", " ");
		String path = raw.substring(raw.indexOf("/") + 1, raw.length()).replaceFirst("bin", "test");
		this.bodyanimFile = new File(path + this.body);
		this.headanimFile = new File(path + this.head);
		try {
			this.bodyanimFile.createNewFile();
			this.headanimFile.createNewFile();
		} catch (IOException e) {
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
