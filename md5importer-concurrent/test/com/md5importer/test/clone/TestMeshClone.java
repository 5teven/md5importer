package com.md5importer.test.clone;

import com.jme.scene.Spatial;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.binary.TestMeshImport;

/**
 * Demo shows how fast mesh cloning is over loading binary files.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshClone extends TestMeshImport {
	
	protected IMD5Node bodyclone;
	protected IMD5Node headclone;
	
	private double headclonetime;
	private double bodyclonetime;

	public static void main(String[] args) {
		new TestMeshClone().start();
	}
	
	@Override
	protected void simpleInitGame() {
		super.simpleInitGame();
		long start = System.nanoTime();
		this.bodyclone = this.body.clone();
		long end = System.nanoTime();
		this.bodyclonetime = (end - start)/1000000.0;
		start = System.nanoTime();
		this.headclone = this.head.clone();
		end = System.nanoTime();
		this.headclonetime = (end - start)/1000000.0;
		this.bodyclone.attachChild(this.headclone, "Shoulders");
		
		((Spatial)this.bodyclone).setLocalTranslation(50, -40, -300);
		this.rootNode.attachChild((Spatial)this.bodyclone);
		
		this.printResult();
	}
	
	private void printResult() {
		System.out.println("Loading head mesh took: " + this.headtime + " millisecond\n");
		System.out.println("Cloning head mesh took: " + this.headclonetime + " millisecond\n");
		System.out.println("Loading body mesh took: " + this.bodytime + " millisecond\n");
		System.out.println("Cloning body mesh took: " + this.bodyclonetime + " millisecond\n");
	}
}
