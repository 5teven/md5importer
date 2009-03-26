package test.model.md5.clone;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.model.md5.JointAnimation;
import com.model.md5.controller.JointController;

/**
 * Test to show how fast animation cloning is over reading binary file.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimClone extends TestMeshClone {
	private final String body = "bodyanim.jme";
	private final String head = "headanim.jme";
	private JointAnimation bodyAnim;
	private JointAnimation headAnim;
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
			this.bodyAnim = (JointAnimation)BinaryImporter.getInstance().load(bodyURL);
			long end = System.nanoTime();
			this.bodyanimtime = (end - start)/1000000.0;
			start = System.nanoTime();
			this.headAnim = (JointAnimation)BinaryImporter.getInstance().load(headURL);
			end = System.nanoTime();
			this.headanimtime = (end - start)/1000000.0;
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

	private void cloneAnim() {
		long start = System.nanoTime();
		JointAnimation bodyclone = this.bodyAnim.clone();
		long end = System.nanoTime();
		this.bodyanimclonetime = (end - start)/1000000.0;
		start = System.nanoTime();
		JointAnimation headclone = this.headAnim.clone();
		end = System.nanoTime();
		this.headanimclonetime = (end - start)/1000000.0;
		JointController bodycontroller = new JointController(this.bodyclone.getJoints());
		bodycontroller.addAnimation(bodyclone);
		bodycontroller.setRepeatType(1);
		bodycontroller.setActive(true);
		bodycontroller.setSpeed(0.2f);
		this.bodyclone.addController(bodycontroller);
		JointController headcontroller = new JointController(this.headclone.getJoints());
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
