package com.md5importer.test;

import java.io.IOException;
import java.net.URL;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.md5importer.control.BlendController;
import com.md5importer.control.MD5AnimController;
import com.md5importer.interfaces.control.IBlendController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;

public class TestFading extends TestAnim {

	protected IMD5Anim stand;
	
	protected IMD5AnimController standAnimController;
	
	protected IBlendController blender;
	
	@Override
	protected IMD5Node setupModel() {
		try {
			super.setupModel();
			// Load second animation to blend.
			URL standAnimURL = TestAnim.class.getClassLoader().getResource("com/md5importer/test/data/marine_stand.md5anim");
			this.stand = this.importer.loadAnim(standAnimURL, "stand");
			this.importer.cleanup();
			// Create stand controller.
			this.standAnimController = new MD5AnimController(this.stand);
			this.standAnimController.setActive(false);
			// Create a blender.
			this.blender = new BlendController(this.body, this.bodyController);
			// Create a threaded updater to update animations in separate thread.
			this.updater.addController(this.standAnimController);
			this.updater.addController(this.blender);
			this.updater.start();
			return this.body;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void setupGame() {
		super.setupGame();
		KeyBindingManager.getKeyBindingManager().set("fade", KeyInput.KEY_O);
	}

	protected void simpleUpdate() {
		super.simpleUpdate();
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("fade", false)) {
			if(this.bodyController.getActiveAnim().getName().equals("walk")) {
				this.walkAnimController.setActive(false);
				this.blender.blend(this.stand, this.standAnimController, 0.5f);
			} else if(this.bodyController.getActiveAnim().getName().equals("stand")) {
				this.standAnimController.setActive(false);
				this.blender.blend(this.walk, this.walkAnimController, 0.5f);
			}
		}
	}
	
	public static void main(String[] args) {
		new TestFading().start();
	}
}
