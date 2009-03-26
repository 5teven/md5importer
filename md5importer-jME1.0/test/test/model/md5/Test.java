package test.model.md5;

import java.net.URISyntaxException;

import com.jme.app.SimpleGame;
import com.jme.scene.Node;
import com.jme.util.resource.MultiFormatResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.model.md5.ModelNode;
import com.model.md5.importer.MD5Importer;

public abstract class Test extends SimpleGame{

	@Override
	protected void simpleInitGame() {
		this.overrideTextureKey();
		Node node = this.loadModel();
		node.setLocalTranslation(0, -40, -300);
		this.rootNode.attachChild(node);
		this.setupGame();
		MD5Importer.getInstance().cleanup();
	}
	
	protected void overrideTextureKey() {
		try {
			MultiFormatResourceLocator locator = new MultiFormatResourceLocator(this.getClass().getClassLoader().getResource("test/model/md5/data/texture/"), 
					new String[]{".tga", ".bmp", ".png", ".jpg", ".texture", ".jme"});
			ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, locator);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	abstract protected ModelNode loadModel();
	
	abstract protected void setupGame();
}
