package test.model.md5.clone;

import com.model.md5.ModelNode;

import test.model.md5.binary.TestMeshImport;

/**
 * Demo shows how fast mesh cloning is over loading binary files.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshClone extends TestMeshImport {
	protected ModelNode bodyclone;
	protected ModelNode headclone;
	private double headclonetime;
	private double bodyclonetime;

	public static void main(String[] args) {
		new TestMeshClone().start();
	}
	
	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		long start = System.nanoTime();
		this.bodyclone = this.bodyNode.clone();
		long end = System.nanoTime();
		this.bodyclonetime = (end - start)/1000000.0;
		start = System.nanoTime();
		this.headclone = this.headNode.clone();
		end = System.nanoTime();
		this.headclonetime = (end - start)/1000000.0;
		this.bodyclone.attachChild(this.headclone, "Shoulders");
		this.bodyclone.setLocalTranslation(35, 0, 0);
		this.rootNode.attachChild(this.bodyclone);
		this.printResult();
	}
	
	private void printResult() {
		System.out.println("Loading head mesh took: " + this.headtime + " millisecond\n");
		System.out.println("Cloning head mesh took: " + this.headclonetime + " millisecond\n");
		System.out.println("Loading body mesh took: " + this.bodytime + " millisecond\n");
		System.out.println("Cloning body mesh took: " + this.bodyclonetime + " millisecond\n");
	}
}
