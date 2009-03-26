package com.model.md5.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.JointAnimation;
import com.model.md5.exception.InvalidAnimationException;
import com.model.md5.resource.mesh.Joint;

/**
 * <code>JointController</code> controls the skeleton of a <code>ModelNode</code>.
 * <p>
 * <code>JointController</code> interpolates the previous and next
 * <code>Frame</code> then updates the skeleton with interpolated translation
 * and orientation values.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 07-24-2008 11:15 EST
 */
public class JointController extends Controller {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 1029065355427370006L;
	/**
	 * The <code>Logger</code> instance.
	 */
	private static final Logger logger = Logger.getLogger(JointController.class.getName());
	/**
	 * The total time elapsed in the current cycle.
	 */
	private float time;
	/**
	 * The array of <code>Joint</code> this controller controls.
	 */
	private Joint[] joints;
	/**
	 * The current active <code>JointAnimation</code>.
	 */
	private JointAnimation activeAnimation;
	/**
	 * The <code>HashMap</code> of controlled <code>JointAnimation</code>.
	 */
	private HashMap<String, JointAnimation> animations;
	/**
	 * The temporary interpolation value.
	 */
	private float interpolation;
	/**
	 * The temporary translation.
	 */
	private final Vector3f translation;
	/**
	 * The temporary orientation.
	 */
	private final Quaternion orientation;
	/**
	 * The flag indicates if fading is in process.
	 */
	private boolean fading;
	/**
	 * The fading duration value in seconds.
	 */
	private float duration;
	/**
	 * The array of <code>Vector3f</code> translations used for fading.
	 */
	private Vector3f[] translations;
	/**
	 * The array of <code>Quaternion</code> orientations used for fading.
	 */
	private Quaternion[] orientations;
	/**
	 * The flag indicates if fading should scale with controller speed.
	 */
	private boolean scale;
	
	/**
	 * Default constructor of <code>JointController</code>.
	 */
	public JointController(){
		super();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}

	/**
	 * Constructor of <code>JointController</code>.
	 * @param joints The array of <code>Joint</code> to be controlled.
	 */
	public JointController(Joint[] joints) {
		this.joints = joints;
		this.animations = new HashMap<String, JointAnimation>();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}

	/**
	 * Update the current active <code>JointAnimation</code> to obtain previous
	 * and next <code>Frame</code>. Then updates the skeleton with interpolated
	 * translation and orientation values.
	 * @param time The time between the last update and the current one.
	 */
	@Override
	public void update(float time) {
		if(this.activeAnimation == null) return;
		this.updateTime(time);
		if(!this.fading) {
			this.activeAnimation.update(time, this.getRepeatType(), this.getSpeed());
			this.updateJoints();
		}
		else this.updateFading();
	}

	/**
	 * Update the total time elapsed with given value based on the repeat type. The
	 * time is reseted after one cycle of the animation is completed.
	 * @param time The time between the last update and the current one.
	 */
	private void updateTime(float time) {
		if(this.activeAnimation != null) {
			if(this.fading) {
				if(this.scale) this.time += time * this.getSpeed();
				else this.time += time;
				return;
			}
			switch(this.getRepeatType()) {
			case Controller.RT_WRAP:
				this.time += time * this.getSpeed();
				if(this.activeAnimation.isCyleComplete()) this.time = 0.0f;
				break;
			case Controller.RT_CLAMP:
				this.time += time * this.getSpeed();
				if(this.activeAnimation.isCyleComplete()) this.time = 0.0f;
				break;
			case Controller.RT_CYCLE:
				if(!this.activeAnimation.isBackward()) this.time += time * this.getSpeed();
				else this.time -= time * this.getSpeed();
				if(this.activeAnimation.isCyleComplete()) {
					if(!this.activeAnimation.isBackward()) this.time = 0;
					else this.time = this.activeAnimation.getAnimationTime();
				}
				break;
			}
		}
	}

	/**
	 * Update the skeleton during normal animating process.
	 */
	private void updateJoints() {
		this.interpolation = this.getInterpolation();
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(this.activeAnimation.getPreviousFrame().getTranslation(i),
					this.activeAnimation.getNextFrame().getTranslation(i), this.interpolation);
			this.orientation.slerp(this.activeAnimation.getPreviousFrame().getOrientation(i),
					this.activeAnimation.getNextFrame().getOrientation(i), this.interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
	}

	/**
	 * Update the fading process.
	 */
	private void updateFading() {
		this.interpolation = this.time/this.duration;
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(this.translations[i], this.activeAnimation.getPreviousFrame().getTranslation(i), this.interpolation);
			this.orientation.slerp(this.orientations[i], this.activeAnimation.getPreviousFrame().getOrientation(i), this.interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
		if(this.interpolation >= 1) {
			this.fading = false;
			this.time = 0;
		}
	}

	/**
	 * Retrieve the <code>Frame</code> interpolation value.
	 * @return The <code>Frame</code> interpolation value.
	 */
	private float getInterpolation() {
		float prev = this.activeAnimation.getPreviousTime();
		float next = this.activeAnimation.getNextTime();
		if(prev == next) return 0.0f;
		float interpolation = (this.time - prev) / (next - prev);
		// Add 1 if it is playing backwards.
		if(this.activeAnimation.isBackward()) interpolation = 1 + interpolation;
		if(interpolation < 0.0f) return 0.0f;
		else if (interpolation > 1.0f) return 1.0f;
		else return interpolation;
	}

	/**
	 * Validate the given <code>JointAnimation</code> with controlled skeleton.
	 * @param animation The <code>JointAnimation</code> to be validated.
	 * @return True if the given <code>JointAnimation</code> is useable with the skeleton. False otherwise.
	 */
	private boolean validateAnimation(JointAnimation animation) {
		if(this.joints.length != animation.getJointIDs().length) return false;
		else {
			boolean result = true;
			for(int i = 0; i < this.joints.length && result; i++) {
				result = this.joints[i].getName().equals(animation.getJointIDs()[i]);
			}
			return result;
		}
	}

	/**
	 * Add a new <code>JointAnimation</code> to this <code>JointController</code>.
	 * The new animation set it to be the active animation if currently there is no
	 * active animation.
	 * @param animation The <code>JointAnimation</code> to be added.
	 */
	public void addAnimation(JointAnimation animation) {
		if(this.validateAnimation(animation)) {
			this.animations.put(animation.getName(), animation);
			if(this.activeAnimation == null) this.setFading(animation, 0, true);
		}
		else throw new InvalidAnimationException();
	}

	/**
	 * Fade from the current active animation to the given animation.
	 * @param name The name of the<code>JointAnimation</code> to be faded into.
	 * @param duration The fading duration in seconds.
	 * @param scale True if fading duration should scale with controller speed.
	 */
	public void setFading(String name, float duration, boolean scale) {
		this.setActiveAnimation(this.animations.get(name));
		this.enabledFading(duration, scale);
	}

	/**
	 * Fade from the current active animation to the given animation.
	 * @param animation The <code>JointAnimation</code> to be faded into.
	 * @param duration The fading duration in seconds.
	 * @param scale True if fading duration should scale with controller speed.
	 */
	public void setFading(JointAnimation animation, float duration, boolean scale) {
		this.setActiveAnimation(animation);
		this.enabledFading(duration, scale);
	}

	/**
	 * Enable fading between the current <code>Frame</code> and the new active animation.
	 * @param duration The fading duration in seconds.
	 */
	private void enabledFading(float duration, boolean scale) {
		this.activeAnimation.reset();
		this.fading = true;
		this.duration = FastMath.abs(duration);
		this.scale = scale;
		this.time = 0;
		if(this.translations == null && this.orientations == null) {
			this.translations = new Vector3f[this.joints.length];
			this.orientations = new Quaternion[this.joints.length];
			for(int i = 0; i < this.joints.length; i++) {
				this.translations[i] = this.joints[i].getTranslation().clone();
				this.orientations[i] = new Quaternion();
				this.orientations[i].set(this.joints[i].getOrientation());
			}
		} else {
			for(int i = 0; i < this.joints.length; i++) {
				this.translations[i].set(this.joints[i].getTranslation());
				this.orientations[i].set(this.joints[i].getOrientation());
			}
		}
	}

	/**
	 * Set the given <code>JointAnimation</code> to the be active animation.
	 * @param animation The <code>JointAnimation</code> to be set.
	 */
	private void setActiveAnimation(JointAnimation animation) {
		if(animation == null) JointController.logger.info("Given animation is null.");
		else if(this.animations.containsValue(animation)) this.activeAnimation = animation;
		else this.addAnimation(animation);
	}

	/**
	 * Retrieve the current active <code>JointAnimation</code>.
	 * @return The current active <code>JointAnimation</code>.
	 */
	public JointAnimation getActiveAnimation() {
		return this.activeAnimation;
	}

	/**
	 * Retrieve all the <code>JointAnimation</code> assigned to this <code>JointController</code>.
	 * @return Unmodifiable <code>Collection</code> of <code>JointAnimation</code>.
	 */
	public Collection<JointAnimation> getAnimations() {
		return Collections.unmodifiableCollection(this.animations.values());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return JointController.class;
	}

	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule oc = e.getCapsule(this);
		oc.write(this.joints, "Joints", null);
		oc.write(this.activeAnimation, "ActiveAnimation", null);
		oc.writeStringSavableMap(this.animations, "Animations", null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(JMEImporter e) throws IOException {
		super.read(e);
		InputCapsule ic = e.getCapsule(this);
		Savable[] temp = ic.readSavableArray("Joints", null);
		this.joints = new Joint[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.joints[i] = (Joint)temp[i];
		}
		this.activeAnimation = (JointAnimation)ic.readSavable("ActiveAnimation", null);
		this.animations = (HashMap<String, JointAnimation>)ic.readStringSavableMap("Animations", null);
	}
}
