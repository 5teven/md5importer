package com.md5.viewer.player;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.KeyBindingManager;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Spatial;
import com.md5.viewer.player.enumn.ECommand;
import com.model.md5.controller.MD5Controller;
import com.model.md5.importer.MD5Importer;
import com.model.md5.interfaces.IMD5Animation;
import com.model.md5.interfaces.IMD5Node;

/**
 * <code>AnimationPlayer</code> defines the concrete implementation of a
 * animation player unit that is responsible for the actual rendering and
 * updating of the animations. It allows two different playback modes.
 * <p>
 * <code>AnimationPlayer</code> takes in a list of links to the selected
 * animations and the animated parts hierarchy from the
 * <code>AnimationSelector</code>.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:12 EST
 * @version Modified date: 11-25-2008 00:46 EST
 */
public class AnimationPlayer extends SimpleGame {
	/**
	 * The <code>String</code> directory of the hierarchy.
	 */
	private String dir;
	/**
	 * The <code>List</code> of <code>String</code> animated parts.
	 */
	private final List<String> hierarchy;
	/**
	 * The base animation <code>File</code>.
	 */
	private final File baseAnimFile;
	/**
	 * The <code>List</code> of chain animation <code>File</code>.
	 */
	private final List<File> animFiles;
	/**
	 * The flag indicates if the playback mode is manual.
	 */
	private final boolean manual;
	/**
	 * The <code>KeyBindingManager</code> instance.
	 */
	private final KeyBindingManager keyBinding;
	/**
	 * The <code>MD5Importer</code> instance.
	 */
	private final MD5Importer importer;
	/**
	 * The loaded model mesh <code>IMD5Node</code>.
	 */
	private IMD5Node modelMesh;
	/**
	 * The base <code>IMD5Animation</code>.
	 */
	private IMD5Animation baseAnimation;
	/**
	 * The array of loaded <code>IMD5Animation</code>.
	 */
	private IMD5Animation[] animations;
	/**
	 * The <code>MD5Controller</code> instance set on the model mesh.
	 */
	private MD5Controller controller;
	/**
	 * The current <code>Integer</code> animation index.
	 */
	private int index;
	/**
	 * The elapsed time since last fading to a new animation.
	 */
	private float count;
	/**
	 * The temporary <code>Vector3f</code> used for updating camera.
	 */
	private final Vector3f tempVector;

	/**
	 * Constructor of <code>AnimationPlayer</code>.
	 * @param dir The <code>String</code> directory of the hierarchy.
	 * @param hierarchy The <code>List</code> of <code>String</code> animated parts.
	 * @param baseAnimFile The base animation <code>File</code>.
	 * @param animFiles The <code>List</code> of chain animation <code>File</code>.
	 * @param manual True if the playback mode is manual. False automatic.
	 */
	public AnimationPlayer(String dir, List<String> hierarchy, File baseAnimFile, List<File> animFiles, boolean manual) {
		if(dir == null || hierarchy == null || hierarchy .size() <= 0) throw new RuntimeException("Unable to load basic information.");
		this.dir = dir;
		this.hierarchy = hierarchy;
		this.baseAnimFile = baseAnimFile;
		this.animFiles = animFiles;
		this.manual = manual;
		this.keyBinding = KeyBindingManager.getKeyBindingManager();
		this.importer = MD5Importer.getInstance();
		this.tempVector = new Vector3f();
		this.setConfigShowMode(ConfigShowMode.AlwaysShow);
	}

	@Override
	protected void simpleInitGame() {
		try {
			this.loadModelMesh();
			this.loadAnimations();
			this.setupController();
			this.setupUtilityKey();
			if(this.manual) this.setupManualKey();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.rootNode.updateGeometricState(0, true);
		this.updateCameraPosition(ECommand.CameraPerspective);
	}

	@Override
	protected void simpleUpdate() {
		this.updateUtility();
		if(this.manual) this.updateManual();
		else this.updateAutomatic();
	}

	/**
	 * Update the utility control.
	 */
	private void updateUtility() {
		if(this.keyBinding.isValidCommand(ECommand.IncreaseSpeed.name(), false)) {
			this.controller.setSpeed(this.controller.getSpeed() * 1.2f);
		} else if(this.keyBinding.isValidCommand(ECommand.DecreaseSpeed.name(), false)) {
			this.controller.setSpeed(this.controller.getSpeed() * 0.8f);
		}
		if(this.keyBinding.isValidCommand(ECommand.CameraFont.name(), false)) {
			this.updateCameraPosition(ECommand.CameraFont);
		} else if(this.keyBinding.isValidCommand(ECommand.CameraSide.name(), false)) {
			this.updateCameraPosition(ECommand.CameraSide);
		} else if(this.keyBinding.isValidCommand(ECommand.CameraPerspective.name(), false)) {
			this.updateCameraPosition(ECommand.CameraPerspective);
		}
	}

	/**
	 * Update the manual control.
	 */
	private void updateManual() {
		IMD5Animation active = this.controller.getActiveAnimation();
		if(this.keyBinding.isValidCommand(ECommand.IncrementAnimation.name(), false)) {
			if(active != null) {
				if(active == this.baseAnimation) this.incrementAnimation();
				else if(active.getPercentage() >= 0.7f) this.incrementAnimation();
			}
		} else if(this.keyBinding.isValidCommand(ECommand.ResetAnimation.name(), false)) {
			this.resetAnimation();
		}
		if(active != this.baseAnimation && active.isCyleComplete()) {
			this.count += this.tpf;
			if(this.count >= 0.5f) {
				this.resetAnimation();
				this.count = 0;
			}
		}
	}

	/**
	 * Update the automatic control.
	 */
	private void updateAutomatic() {
		if(this.controller.getActiveAnimation() != null && this.animations.length > 0 && this.controller.getActiveAnimation().isCyleComplete()) {
			this.controller.setFading(this.animations[this.index], 0, false);
			this.index++;
			if(this.index >= this.animations.length) this.index = 0;
		}
	}

	/**
	 * Update the camera position based on the camera command.
	 * @param command The <code>ECommand</code> enumeration.
	 */
	private void updateCameraPosition(ECommand command) {
		float x = ((BoundingBox)((Spatial)this.modelMesh).getWorldBound()).xExtent;
		float y = ((BoundingBox)((Spatial)this.modelMesh).getWorldBound()).yExtent;
		float z = ((BoundingBox)((Spatial)this.modelMesh).getWorldBound()).zExtent;
		float posx = x*2.0f;
		if(posx < 15) posx = 15;
		float posy = y;
		float posz = z*2.0f;
		if(posz < 15) posz = 15;
		this.tempVector.set(((Spatial)this.modelMesh).getWorldTranslation());
		switch(command) {
		case CameraFont: this.cam.setLocation(this.tempVector.addLocal(0, posy, posz)); break;
		case CameraSide: this.cam.setLocation(this.tempVector.addLocal(posx, posy, 0)); break;
		case CameraPerspective: this.cam.setLocation(this.tempVector.addLocal(posx, posy, posz)); break;
		}
		this.cam.lookAt(((Spatial)this.modelMesh).getWorldTranslation().addLocal(0, posy, 0), Vector3f.UNIT_Y);
	}

	/**
	 * Increment the animation chain.
	 */
	private void incrementAnimation() {
		this.controller.setRepeatType(Controller.RT_CLAMP);
		this.controller.setFading(this.animations[this.index], 0.2f, false);
		this.index++;
		if(this.index >= this.animations.length) this.index = 0;
	}

	/**
	 * Reset the animation back to the base animation.
	 */
	private void resetAnimation() {
		this.controller.setRepeatType(Controller.RT_WRAP);
		this.controller.setFading(this.baseAnimation, 0.2f, false);
		this.index = 0;
	}

	/**
	 * Load the mesh of the model based on the hierarchy.
	 * @throws IOException If the loading process is interrupted.
	 */
	private void loadModelMesh() throws IOException {
		// Load the base model mesh.
		File file = new File(this.dir + this.hierarchy.get(0) + ".md5mesh");
		URL url = file.toURI().toURL();
		this.importer.loadMesh(url, "Mesh0");
		this.modelMesh = this.importer.getModelNode();
		this.importer.cleanup();
		// Load the dependent children.
		for(int i = 1; i < this.hierarchy.size(); i++) {
			file = new File(this.dir + this.hierarchy.get(i) + ".md5mesh");
			url = file.toURI().toURL();
			this.importer.loadMesh(url, "Mesh"+i);
			this.modelMesh.attachDependent(this.importer.getModelNode());
			this.importer.cleanup();
		}
		// Attach to root node.
		this.rootNode.attachChild((Spatial)this.modelMesh);
	}

	/**
	 * Load the animations from the URL links.
	 * @throws IOException If the loading process is interrupted.
	 */
	private void loadAnimations() throws IOException {
		// Load the base animation.
		this.importer.loadAnim(this.baseAnimFile.toURI().toURL(), "BaseAnimation");
		this.baseAnimation = this.importer.getAnimation();
		this.importer.cleanup();
		// Load the animation chain.
		this.animations = new IMD5Animation[this.animFiles.size()];
		for(int i = 0; i < this.animations.length; i++) {
			this.importer.loadAnim(this.animFiles.get(i).toURI().toURL(), "Animation"+i);
			this.animations[i] = this.importer.getAnimation();
			this.importer.cleanup();
		}
	}

	/**
	 * Set the controller on the loaded model mesh.
	 */
	private void setupController() {
		this.controller = new MD5Controller(this.modelMesh);
		this.controller.setActive(true);
		this.modelMesh.addController(this.controller);
		// Set proper starting animation.
		if(this.manual) this.resetAnimation();
		else if(!this.manual){
			if(this.animations.length > 0) {
				this.controller.setFading(this.animations[0], 0, false);
				this.controller.setRepeatType(Controller.RT_CLAMP);
				this.index = 1;
			} else if(this.baseAnimation != null) this.resetAnimation();
		}
	}

	/**
	 * Setup the utility control hot keys.
	 */
	private void setupUtilityKey() {
		this.keyBinding.set(ECommand.IncreaseSpeed.name(), ECommand.IncreaseSpeed.getKeyCode());
		this.keyBinding.set(ECommand.DecreaseSpeed.name(), ECommand.DecreaseSpeed.getKeyCode());
		this.keyBinding.set(ECommand.CameraFont.name(), ECommand.CameraFont.getKeyCode());
		this.keyBinding.set(ECommand.CameraSide.name(), ECommand.CameraSide.getKeyCode());
		this.keyBinding.set(ECommand.CameraPerspective.name(), ECommand.CameraPerspective.getKeyCode());
	}

	/**
	 * Setup the control hot keys for manual mode.
	 */
	private void setupManualKey() {
		this.keyBinding.set(ECommand.IncrementAnimation.name(), ECommand.IncrementAnimation.getKeyCode());
		this.keyBinding.set(ECommand.ResetAnimation.name(), ECommand.ResetAnimation.getKeyCode());
	}
}
