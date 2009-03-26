package com.md5importer.test.binary;

import java.io.IOException;
import java.net.URL;

import com.jme.util.export.binary.BinaryImporter;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.Test;

/**
 * Simple test to show how to load in the exported mesh.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshImport extends Test {
	
	protected IMD5Node body;
	protected IMD5Node head;
	
	protected double bodytime;
	protected double headtime;
	
	@Override
	protected IMD5Node setupModel() {
		URL bodyurl = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/bodymesh.jme");
		URL headurl = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/headmesh.jme");
		try {
			long start = System.nanoTime();
			this.body = (IMD5Node)BinaryImporter.getInstance().load(bodyurl);
			long end = System.nanoTime();
			this.bodytime = (end - start)/1000000.0;
			System.out.println("Import body mesh took: " + this.bodytime);
			
			start = System.nanoTime();
			this.head = (IMD5Node)BinaryImporter.getInstance().load(headurl);
			end = System.nanoTime();
			this.headtime = (end - start)/1000000.0;
			System.out.println("Import head mesh took: " + this.headtime);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.body.attachChild(this.head, "Shoulders");
		return this.body;
	}

	@Override
	protected void setupGame() {}

	public static void main(String[] args) {
		new TestMeshImport().start();
	}
}
