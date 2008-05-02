package com.model.md5.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;


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
 * <code>JointController</code> interpolates the previous and next <code>Frame</code>
 * then updates the skeleton with interpolated translation and orientation values.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 05-01-2008 16:02 EST
 * @version 1.0.1
 */
public class JointController extends Controller{
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
	private Vector3f translation;
	/**
	 * The temporary orientation.
	 */
	private Quaternion orientation;
	/**
	 * The flag indicates if fading is in process.
	 */
	private boolean fading;
	/**
	 * The fading duration.
	 */
	private float fadingTime;
	
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
		this.updateTime(time);
		if(!this.fading) this.updateJoints(time);
		else this.updateFading();
	}
	
	/**
	 * Update the total time elapsed with given value based on the repeat type. The
	 * time is reseted after one cycle of the animation is completed.
	 * @param time The time between the last update and the current one.
	 */
	private void updateTime(float time) {
		if(this.activeAnimation != null) {
			switch(this.getRepeatType()) {
				case Controller.RT_WRAP:
					this.time = this.time + (time * this.getSpeed());
					if(this.activeAnimation.isCyleComplete()) this.time = 0.0f;
					break;
				case Controller.RT_CLAMP:
					this.time = this.time + (time * this.getSpeed());
					if(this.activeAnimation.isCyleComplete()) this.time = 0.0f;
					break;
				case Controller.RT_CYCLE:
					if(!this.activeAnimation.isBackward()) this.time = this.time + (time * this.getSpeed());
					else this.time = this.time - (time * this.getSpeed());
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
	 * @param time The time between the last update and the current one.
	 */
	private void updateJoints(float time) {
		if(this.activeAnimation != null) {
			this.activeAnimation.update(time, this.getRepeatType(), this.getSpeed());
		}
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
		this.interpolation = this.getInterpolation();
		for(int i = 0; i < this.joints.length; i++) {
			this.translation.interpolate(this.joints[i].getTranslation(),
					this.activeAnimation.getPreviousFrame().getTranslation(i), this.interpolation);
			this.orientation.slerp(this.joints[i].getOrientation(),
					this.activeAnimation.getPreviousFrame().getOrientation(i), this.interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
		if(this.interpolation >= 1) {
			this.fading = false;
		}
	}
	
	/**
	 * Retrieve the <code>Frame</code> interpolation value.
	 * @return The <code>Frame</code> interpolation value.
	 */
	private float getInterpolation() {
		if(!this.fading) {
			float prev = this.activeAnimation.getPreviousTime();
			float next = this.activeAnimation.getNextTime();
			if(prev == next) return 0.0f;
			float interpolation = (this.time - prev) / (next - prev);
			// Add 1 if it is playing backwards.
			if(this.activeAnimation.isBackward()) interpolation = 1 + interpolation;
			if(interpolation < 0.0f) return 0.0f;
			else if (interpolation > 1.0f) return 1.0f;
			else return interpolation;
		} else {
			return (this.time/this.fadingTime);
		}
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
	 * Add a new <code>JointAnimation</code> to this <code>JointController</code> and
	 * set it to be the active <code>JointAnimation</code>.
	 * @param animation The <code>JointAnimation</code> to be added.
	 */
	public void addAnimation(JointAnimation animation) {
		if(this.validateAnimation(animation)) {
			this.animations.put(animation.getName(), animation);
			if(this.activeAnimation == null) this.activeAnimation = animation;
		}
		else throw new InvalidAnimationException();
	}
	
	/**
	 * Set the <code>JointAnimation</code> with given name to be the active animation.
	 * @param name The name of the <code>JointAnimation</code> to be activated.
	 */
	public void setActiveAnimation(String name) {
		if(this.animations.containsKey(name)) this.activeAnimation = this.animations.get(name);
		else JointController.logger.info("Invalid animation name: " + name);
	}
	
	/**
	 * Set the given <code>JointAnimation</code> to the be active animation.
	 * @param animation The <code>JointAnimation</code> to be set.
	 */
	public void setActiveAnimation(JointAnimation animation) {
		if(this.animations.containsValue(animation)) this.activeAnimation = animation;
		else this.addAnimation(animation);
	}
	
	/**
	 * Set the <code>JointAnimation</code> with given name to be the active animation.
	 * @param name The name of the <code>JointAnimation</code> to be activated.
	 * @param fadingTime The time in seconds it takes to fade into the new active animation.
	 */
	public void setActiveAnimation(String name, float fadingTime) {
		this.enabledFading(fadingTime);
		this.setActiveAnimation(name);
	}
	
	/**
	 * Set the given <code>JointAnimation</code> to be the active animation.
	 * @param animation The <code>JointAnimation</code> to be set.
	 * @param fadingTime The time in seconds it takes to fade into the new active animation.
	 */
	public void setActiveAnimation(JointAnimation animation, float fadingTime) {
		this.enabledFading(fadingTime);
		this.setActiveAnimation(animation);
	}
	
	/**
	 * Enable fading between the current <code>Frame</code> and the new active animation.
	 * @param fadingTime The time in seconds it takes to fade into the new active animation.
	 */
	private void enabledFading(float fadingTime) {
		this.fading = true;
		this.fadingTime = fadingTime;
		this.time = 0;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return JointController.class;
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

	@Override
	public void write(JMEExporter e) throws IOException {
		super.write(e);
		OutputCapsule oc = e.getCapsule(this);
		oc.write(this.joints, "Joints", null);
		oc.write(this.activeAnimation, "ActiveAnimation", null);
		oc.writeStringSavableMap(this.animations, "Animations", null);
	}
}