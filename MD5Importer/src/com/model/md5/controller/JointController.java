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
 * JointController controls a the skeleton of a ModelNode. It interpolates the
 * previous and the next Frame then updates the skeleton with interpolated
 * translation and orientation values.
 * 
 * @author Yi Wang (Neakor)
 */
public class JointController extends Controller{
	// Serial version.
	private static final long serialVersionUID = 1029065355427370006L;
	// The logger object.
	private static final Logger logger = Logger.getLogger(JointController.class.getName());
	// The total time elapsed.
	private float time;
	// The joints this controller controls.
	private Joint[] joints;
	// The active animation.
	private JointAnimation activeAnimation;
	// The array list of animations.
	private HashMap<String, JointAnimation> animations;
	// The interpolation value.
	private float interpolation;
	// The temporary translation.
	private Vector3f translation;
	// The temporary orientation.
	private Quaternion orientation;
	
	/**
	 * Default constructor of JointController.
	 */
	public JointController(){
		super();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}
	
	/**
	 * Constructor of JointController.
	 * @param joints The array of Joint to be controlled.
	 */
	public JointController(Joint[] joints) {
		this.joints = joints;
		this.animations = new HashMap<String, JointAnimation>();
		this.translation = new Vector3f();
		this.orientation = new Quaternion();
	}

	/**
	 * Update the active animation to obtain previous and next Frame then updates
	 * the skeleton with interpolated translation and orientation values.
	 * @param time The time between the last update and the current one.
	 */
	@Override
	public void update(float time) {
		this.time = this.time + time * this.getSpeed();
		if(this.activeAnimation != null)
		{
			this.activeAnimation.update(time, this.getRepeatType(), this.getSpeed());
		}
		this.interpolation = this.getInterpolation();
		for(int i = 0; i < this.joints.length; i++)
		{
			this.translation.interpolate(this.activeAnimation.getPreviousFrame().getTranslation(i),
					this.activeAnimation.getNextFrame().getTranslation(i), this.interpolation);
			this.orientation.slerp(this.activeAnimation.getPreviousFrame().getOrientation(i),
					this.activeAnimation.getNextFrame().getOrientation(i), this.interpolation);
			this.joints[i].updateTransform(this.translation, this.orientation);
		}
	}
	
	/**
	 * Retrieve the frame interpolation value.
	 * @return The frame interpolation value.
	 */
	private float getInterpolation() {
		float prev = this.activeAnimation.getPreviousTime();
		float next = this.activeAnimation.getNextTime();
		if(prev == next) return 0.0f;
		float interpolation = (this.time - prev) / (next - prev);
		if(interpolation < 0.0f) return 0.0f;
		else if (interpolation > 1.0f) return 1.0f;
		else return interpolation;
	}
	
	/**
	 * Validate the given animation with controlled skeleton.
	 * @param animation The JointAnimation to be validated.
	 * @return True if the given animation is useable with the skeleton. False otherwise.
	 */
	private boolean validateAnimation(JointAnimation animation) {
		if(this.joints.length != animation.getJointIDs().length) return false;
		else
		{
			boolean result = true;
			for(int i = 0; i < this.joints.length && result; i++)
			{
				result = this.joints[i].getName().equals(animation.getJointIDs()[i]);
			}
			return result;
		}
	}
	
	/**
	 * Add a new JointAnimation to this controller and set it to the active animation.
	 * @param animation The JointAnimation to be added.
	 */
	public void addAnimation(JointAnimation animation) {
		if(this.validateAnimation(animation))
		{
			this.animations.put(animation.getName(), animation);
			this.activeAnimation = animation;
		}
		else throw new InvalidAnimationException();
	}
	
	/**
	 * Set the animation with given name to be the active animation.
	 * @param name The name of the animation to be activated.
	 */
	public void setActiveAnimation(String name) {
		if(this.animations.containsKey(name)) this.activeAnimation = this.animations.get(name);
		else JointController.logger.info("Invalid animation name: " + name);
	}
	
	/**
	 * Set the given JointAnimation to the active animation.
	 * @param animation The JointAnimation to be set.
	 */
	public void setActiveAnimation(JointAnimation animation) {
		if(this.animations.containsValue(animation)) this.activeAnimation = animation;
		else this.addAnimation(animation);
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
		for(int i = 0; i < temp.length; i++)
		{
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
