package com.md5importer.test.clone;

import java.io.IOException;
import java.net.URL;

import com.jme.app.SimpleGame;
import com.jme.image.Texture;
import com.jme.scene.Spatial;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.md5importer.MD5Importer;
import com.md5importer.control.MD5AnimController;
import com.md5importer.control.MD5NodeController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.test.util.ThreadedUpdater;

public class TestMassClone extends SimpleGame {

	private final MD5Importer importer;
	private final ThreadedUpdater updater;

	private final int size;
	private final IMD5Node[] nodes;

	private IMD5Node originNode;
	private IMD5Anim originAnim;

	public TestMassClone() {
		this.importer = new MD5Importer();
		this.updater = new ThreadedUpdater(24);
		this.size = 20;
		this.nodes = new IMD5Node[this.size];
	}

	@Override
	protected void simpleInitGame() {
		this.createSet();
		for(int i = 0; i < this.size; i++) this.createClone(i);
		this.updater.start();
	}

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		for(int i = 0; i < this.size; i++) this.nodes[i].swapBuffers();
	}

	private void createSet() {
		try {
			this.originNode = this.loadMesh();
			this.originAnim = this.loadAnim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createClone(int index) {
		final IMD5Node node = this.originNode.clone();
		final IMD5Anim anim = this.originAnim.clone();
		this.initControllers(node, anim);
		((Spatial)node).setLocalTranslation(5*index, 0, 0);
		this.nodes[index] = node;
		this.rootNode.attachChild((Spatial)node);
		
		final TextureState state = this.display.getRenderer().createTextureState();
		final URL url = this.getClass().getClassLoader().getResource("com/md5importer/test/data/texture.png");
		final Texture texture = TextureManager.loadTexture(url);
		state.setTexture(texture);
		((Spatial)node).setRenderState(state);
	}

	private IMD5Node loadMesh() throws IOException {
		try {
			final URL url = this.getClass().getClassLoader().getResource("com/md5importer/test/data/body.md5mesh");
			final IMD5Node body = this.importer.loadMesh(url, "Body");
			this.importer.cleanup();
			final URL url2 = this.getClass().getClassLoader().getResource("com/md5importer/test/data/head.md5mesh");
			body.attachDependent(this.importer.loadMesh(url2, "Head"));
			this.importer.cleanup();
			final URL url3 = this.getClass().getClassLoader().getResource("com/md5importer/test/data/legs.md5mesh");
			body.attachDependent(this.importer.loadMesh(url3, "Legs"));
			return body;
		} finally {
			this.importer.cleanup();
		}
	}

	private IMD5Anim loadAnim() throws IOException {
		try {
			final URL url = this.getClass().getClassLoader().getResource("com/md5importer/test/data/walk.md5anim");
			return this.importer.loadAnim(url, "Anim");
		} finally {
			this.importer.cleanup();
		}
	}

	private void initControllers(final IMD5Node node, final IMD5Anim anim) {
		final MD5NodeController nodeController = new MD5NodeController(node);
		final MD5AnimController animController = new MD5AnimController(anim);
		anim.register(nodeController);
		nodeController.setActiveAnim(anim);
		this.updater.addController(animController);
	}

	protected void cleanup() {
		super.cleanup();
		this.updater.stop();
	}

	public static void main(String[] args) {
		new TestMassClone().start();
	}
}
