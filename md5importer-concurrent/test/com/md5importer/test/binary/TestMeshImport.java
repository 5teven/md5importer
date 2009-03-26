package com.md5importer.test.binary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.MultiFormatResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.md5importer.interfaces.model.IMD5Node;

/**
 * Simple test to show how to load in the exported mesh.
 * We cannot extend test because this requires loading jME default textures.
 * The texture key has to be different.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshImport extends SimpleGame {
	
	protected IMD5Node body;
	protected IMD5Node head;
	
	protected double bodytime;
	protected double headtime;
	
	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
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
		this.rootNode.attachChild((Spatial)this.body);
	}

	private void overrideTextureKey() {
		try {
			MultiFormatResourceLocator locator = new MultiFormatResourceLocator(this.getClass().getClassLoader().getResource("com/md5importer/test/data/texture/"), 
					new String[]{".tga"});
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestMeshImport().start();
	}
}
