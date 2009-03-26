package test.model.md5;

import java.net.URISyntaxException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.resource.SimpleResourceLocator;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IMD5Node;

public abstract class Test extends SimpleGame{
	
	protected final MD5Importer importer;

	protected Test() {
		this.importer = new MD5Importer();
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
			URL url = this.getClass().getClassLoader().getResource("test/model/md5/data/texture/");
			SimpleResourceLocator locator = new SimpleResourceLocator(url);
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	abstract protected IMD5Node loadModel();
	
	abstract protected void setupGame();
}
