package com.model.md5;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import com.jme.scene.Controller;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.model.md5.resource.anim.Frame;

/**
 * <code>JointAnimation</code> is the final product of MD5 animation.
 * <p>
 * <code>JointAnimation</code> is added to a <code>JointControlle</code> for animating
 * the skeletal <code>ModelNode</code>.
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 06-09-2008 17:44 EST
 */
public class JointAnimation implements Serializable, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 3646737896444759738L;
	/**
	 * The name of this <code>JointAnimation</code>.
	 */
	private String name;
	/**
	 * The joint IDs of this <code>JointAnimation</code>.
	 */
	private String[] jointIDs;
	/**
	 * The array of key <code>Frame</code> of this <code>JointAnimation</code>.
	 */
	private Frame[] frames;
	/**
	 * The frame rate.
	 */
	private float frameRate;
	/**
	 * The array of starting time of each <code>Frame</code>.
	 */
	private float[] frameTimes;
	/**
	 * The flag indicates if this <code>JointAnimation</code> is being played backwards.
	 */
	private boolean backward;
	/**
	 * The time elapsed since last change in <code>Frame</code>.
	 */
	private float time;
	/**
	 * The index of the next <code>Frame</code>.
	 */
	private int prevFrame;
	/**
	 * The index of the previous <code>Frame</code>.
	 */
	private int nextFrame;
	/**
	 * The flag indicates if this cycle is completed but the new cycle has not yet started.
	 */
	private boolean complete;
	/**
	 * The children <code>JointAnimation</code>.
	 */
	private ArrayList<JointAnimation> animations;
	
	/**
	 * Default constructor of <code>JointAnimation</code>.
	 */
	public JointAnimation() {
		this.prevFrame = 0;
		this.nextFrame = 1;
	}
	
	/**
	 * Constructor of <code>JointAnimation</code>.
	 * @param name The name of this <code>JointAnimation</code>.
	 * @param IDs The joint IDs of this <code>JointAnimation</code>.
	 * @param frames The array of <code>Frame</code>.
	 * @param framerate The frame rate of this <code>JointAnimation</code>.
	 */
	public JointAnimation(String name, String[] IDs, Frame[] frames, float framerate) {
		this.name = name;
		this.setJointIDs(IDs);
		this.setFrames(frames);
		this.setFrameRate(framerate);
		this.frameTimes = new float[this.frames.length];
		for(int i = 0; i < this.frameTimes.length; i++) {
			this.frameTimes[i] = (float)i * (1.0f/this.frameRate);
		}
		this.prevFrame = 0;
		this.nextFrame = 1;
	}

	/**
	 * Update the <code>Frame</code> index based on given values.
	 * @param time The time between last update and the current one.
	 * @param repeat The <code>Controller</code> repeat type.
	 * @param speed The speed of the <code>Controller</code>.
	 */
	public void update(float time, int repeat, float speed) {
		this.time = this.time + (time * speed);
		if(this.complete) this.complete = false;
		switch(repeat) {
			case Controller.RT_CLAMP:
				this.updateClamp();
				break;
			case Controller.RT_CYCLE:
				this.updateCycle();
				break;
			case Controller.RT_WRAP:
				this.updateWrap();
				break;
		}
		if(this.animations != null) {
			for(JointAnimation anim : this.animations) {
				anim.update(time, repeat, speed);
			}
		}
	}
	
	/**
	 * Update <code>Frame</code> index when the wrap mode is set to clamp.
	 */
	private void updateClamp() {
		if(this.time >= 1.0f/this.frameRate) {
			this.nextFrame++;
			this.prevFrame = this.nextFrame - 1;
			if(this.nextFrame > this.frames.length - 1) {
				this.nextFrame = this.frames.length - 1;
				this.prevFrame = this.nextFrame;
				this.complete = true;
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Update <code>Frame</code> index when the wrap mode is set to cycle.
	 */
	private void updateCycle() {
		if(this.time >= 1.0f/this.frameRate) {
			if(!this.backward) {
				this.nextFrame++;
				this.prevFrame = this.nextFrame - 1;
				if(this.nextFrame > this.frames.length - 1) {
					this.backward = true;
					this.prevFrame = this.frames.length - 1;
					this.nextFrame = this.prevFrame - 1;
					this.complete = true;
				}
			} else {
				this.nextFrame--;
				this.prevFrame = this.nextFrame + 1;
				if(this.nextFrame < 0) {
					this.backward = false;
					this.prevFrame = 0;
					this.nextFrame = this.prevFrame + 1;
					this.complete = true;
				}
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Update <code>Frame</code> index when the wrap mode is set to wrap.
	 */
	private void updateWrap() {
		if(this.time >= 1.0f/this.frameRate) {
			this.nextFrame++;
			this.prevFrame = this.nextFrame - 1;
			if(this.nextFrame > this.frames.length - 1) {
				this.prevFrame = 0;
				this.nextFrame = this.prevFrame + 1;
				this.complete = true;
			}
			this.time = 0.0f;
		}
	}
	
	/**
	 * Add a child <code>JointAnimation</code>.
	 * @param animation The child <code>JointAnimation</code> to be added.
	 */
	public void addAnimation(JointAnimation animation) {
		if(this.animations == null) this.animations = new ArrayList<JointAnimation>();
		this.animations.add(animation);
	}
	
	/**
	 * Set the IDs of <code>Joint</code> of this <code>JointAnimation</code>.
	 * @param IDs The array of IDs of <code>Joint</code>.
	 */
	public void setJointIDs(String[] IDs) {
		this.jointIDs = IDs;
	}
	
	/**
	 * Set the <code>Frame</code> of this <code>JointAnimation</code>.
	 * @param frames The array of <code>Frame</code>.
	 */
	public void setFrames(Frame[] frames) {
		this.frames = frames;
	}
	
	/**
	 * Set the framerate of this <code>JointAnimation</code>.
	 * @param frameRate The float framerate.
	 */
	public void setFrameRate(float frameRate) {
		this.frameRate = frameRate;
	}
	
	/**
	 * Retrieve the total time of one cycle of this <code>JointAnimation</code>.
	 * @return The total time of one cycle of this <code>JointAnimation</code>.
	 */
	public float getAnimationTime() {
		return (1.0f/this.frameRate)*(float)this.frames.length;
	}

	/**
	 * Retrieve the previous <code>Frame</code>.
	 * @return The previous <code>Frame</code>.
	 */
	public Frame getPreviousFrame() {
		return this.frames[this.prevFrame];
	}
	
	/**
	 * Retrieve the starting time of the previous <code>Frame</code>.
	 * @return The starting time of the previous <code>Frame</code>.
	 */
	public float getPreviousTime() {
		if(this.frameTimes != null) return this.frameTimes[this.prevFrame];
		return ((float)this.prevFrame) * (1.0f/this.frameRate);
	}

	/**
	 * Retrieve the next <code>Frame</code>.
	 * @return The next <code>Frame</code>.
	 */
	public Frame getNextFrame() {
		return this.frames[this.nextFrame];
	}
	
	/**
	 * Retrieve the starting time of the next <code>Frame</code>.
	 * @return The starting time of the next <code>Frame</code>.
	 */
	public float getNextTime() {
		if(this.frameTimes != null) return this.frameTimes[this.nextFrame];
		return ((float)this.nextFrame) * (1.0f/this.frameRate);
	}
	
	/**
	 * Retrieve the IDs of <code>Joint</code> of this <code>JointAnimation</code>.
	 * @return The array of IDs of <code>Joint</code>.
	 */
	public String[] getJointIDs() {
		return this.jointIDs;
	}

	/**
	 * Retrieve the name of this <code>JointAnimation</code>.
	 * @return The name of this <code>JointAnimation</code>.
	 */
	public String getName() {
		return this.name;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Class getClassTag() {
		return JointAnimation.class;
	}
	
	/**
	 * Check if this <code>JointAnimation</code> is being played backward.
	 * @return True if the <code>JointAnimation</code> is being played backward. False forward.
	 */
	public boolean isBackward() {
		return this.backward;
	}

	/**
	 * Check if one cycle of this <code>JointAnimation</code> is complete, but the
	 * new one has not yet started.
	 * @return True if one cycle is complete. False otherwise.
	 */
	public boolean isCyleComplete() {
		return this.complete;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.name = ic.readString("Name", null);
		this.jointIDs = ic.readStringArray("JointIDs", null);
		Savable[] temp = ic.readSavableArray("Frames", null);
		this.frames = new Frame[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.frames[i] = (Frame)temp[i];
		}
		this.frameRate = ic.readFloat("FrameRate", 0);
		this.frameTimes = ic.readFloatArray("FrameTimes", null);
		this.animations = (ArrayList<JointAnimation>)ic.readSavableArrayList("Animations", null);
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.name, "Name", null);
		oc.write(this.jointIDs, "JointIDs", null);
		oc.write(this.frames, "Frames", null);
		oc.write(this.frameRate, "FrameRate", 0);
		oc.write(this.frameTimes, "FrameTimes", null);
		oc.writeSavableArrayList(this.animations, "Animations", null);
	}
}
