package com.md5importer.test.binary;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.resource.MultiFormatResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.model.md5.MD5Node;
import com.model.md5.interfaces.IMD5Node;

/**
 * Simple test to show how to load in the exported mesh.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestMeshImport extends SimpleGame{
	private final String body = "bodymesh.jme";
	private final String head = "headmesh.jme";
	protected IMD5Node bodyNode;
	protected IMD5Node headNode;
	protected double bodytime;
	protected double headtime;
	
	public static void main(String[] args) {
		new TestMeshImport().start();
	}

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		URL bodyURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.body);
		URL headURL = this.getClass().getClassLoader().getResource("test/model/md5/data/binary/" + this.head);
		try {
			long start = System.nanoTime();
			this.bodyNode = (MD5Node)BinaryImporter.getInstance().load(bodyURL);
			long end = System.nanoTime();
			this.bodytime = (end - start)/1000000.0;
			start = System.nanoTime();
			this.headNode = (MD5Node)BinaryImporter.getInstance().load(headURL);
			end = System.nanoTime();
			this.headtime = (end - start)/1000000.0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bodyNode.attachChild(this.headNode, "Shoulders");
		this.rootNode.attachChild((Spatial)this.bodyNode);
	}
	
	private void overrideTextureKey() {
		try {
			MultiFormatResourceLocator locator = new MultiFormatResourceLocator(this.getClass().getClassLoader().getResource("test/model/md5/data/texture/"), 
					new String[]{".tga"});
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
