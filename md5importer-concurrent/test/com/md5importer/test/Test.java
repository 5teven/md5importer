package com.md5importer.test;

import java.net.URISyntaxException;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.resource.MultiFormatResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.md5importer.MD5Importer;
import com.md5importer.interfaces.model.IMD5Node;

public abstract class Test extends SimpleGame{
	
	protected final MD5Importer importer;

	protected Test() {
		this.importer = new MD5Importer();
		this.setConfigShowMode(ConfigShowMode.AlwaysShow);
	}
	
	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		Node node = (Node)this.loadModel();
		node.setLocalTranslation(0, -40, -300);
		this.rootNode.attachChild(node);
		this.setupGame();
		this.importer.cleanup();
	}
	
	protected void overrideTextureKey() {
		try {
			MultiFormatResourceLocator locator = new MultiFormatResourceLocator(this.getClass().getClassLoader().getResource("com/md5importer/test/data/texture/"), 
					new String[]{".tga", ".bmp", ".png", ".jpg", ".texture", ".jme"});
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	abstract protected IMD5Node loadModel();
	
	abstract protected void setupGame();
}
