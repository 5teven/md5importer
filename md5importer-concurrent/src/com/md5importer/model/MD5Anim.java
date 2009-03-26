package com.md5importer.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.anim.IFrame;

/**
 * <code>MD5Anim</code> defines the concrete implementation of
 * the MD5 animation data structure.
 * <p>
 * <code>MD5Anim</code> defines the hash code of an instance to
 * be the hash code of the <code>String</code> name. Two instances
 * of <code>MD5Anim</code> are considered as equal if both of
 * them have the same name.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 17:36 EST
 * @version Modified date: 03-24-2009 22:25 EST
 */
public class MD5Anim extends AbstractObservable implements Serializable, IMD5Anim, Savable {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = 3116414498115764623L;
	/**
	 * The <code>ReentrantLock</code> for setting indices.
	 */
	private final ReentrantLock lock;
	/**
	 * The <code>String</code> name of this animation.
	 */
	private String name;
	/**
	 * The <code>String</code> joint IDs.
	 */
	private String[] jointIDs;
	/**
	 * The array of key <code>IFrame</code>.
	 */
	private IFrame[] frames;
	/**
	 * The <code>Float</code> frame rate.
	 */
	private float frameRate;
	/**
	 * The array of <code>Float</code> starting time of each frame.
	 */
	private float[] frameTimes;
	/**
	 * The <code>Float</code> total animation time.
	 */
	private float animationTime;
	/**
	 * The time elapsed since last change in key frame.
	 */
	private volatile float time;
	/**
	 * The index of the previous frame.
	 */
	private volatile int prev;
	/**
	 * The index of the next frame.
	 */
	private volatile int next;
	
	/**
	 * Constructor of <code>MD5Anim</code>.
	 */
	public MD5Anim() {
		this.lock = new ReentrantLock();
		// Default values.
		this.time = 0;
		this.prev = 0;
		this.next = 1;
	}

	/**
	 * Constructor of <code>MD5Anim</code>.
	 * @param name The <code>String</code> name of this animation.
	 * @param IDs The <code>String</code> joint IDs.
	 * @param frames The array of key <code>IFrame</code>.
	 * @param framerate The <code>Float</code> frame rate.
	 */
	public MD5Anim(String name, String[] IDs, IFrame[] frames, float framerate) {
		this.lock = new ReentrantLock();
		this.name = name;
		this.jointIDs = IDs;
		this.frames = frames;
		this.frameRate = framerate;
		final float timeperframe = 1.0f/this.frameRate;
		this.frameTimes = new float[this.frames.length];
		for(int i = 0; i < this.frameTimes.length; i++) {
			this.frameTimes[i] = (float)i * timeperframe;
		}
		this.animationTime = (1.0f/this.frameRate)*(float)this.frames.length;
		// Default values.
		this.time = 0;
		this.prev = 0;
		this.next = 1;
	}

	@Override
	public void setIndices(int prev, int next, float time) {
		this.lock.lock();
		try {
			this.prev = prev;
			this.next = next;
			this.time = time;
		} finally {
			// Release lock before invoke external method for notification.
			this.lock.unlock();
		}
	}

	@Override
	public float getAnimationTime() {
		return this.animationTime;
	}

	@Override
	public int getFrameCount() {
		return this.frames.length;
	}

	@Override
	public float getPercentage() {
		return (float)this.next / (float)this.getFrameCount();
	}

	@Override
	public IFrame getPreviousFrame() {
		return this.frames[this.prev];
	}

	@Override
	public int getPreviousIndex() {
		return this.prev;
	}

	@Override
	public float getPreviousTime() {
		if(this.frameTimes != null) return this.frameTimes[this.prev];
		return ((float)this.prev) * (1.0f/this.frameRate);
	}

	@Override
	public IFrame getNextFrame() {
		return this.frames[this.next];
	}

	@Override
	public int getNextIndex() {
		return this.next;
	}

	@Override
	public float getNextTime() {
		if(this.frameTimes != null) return this.frameTimes[this.next];
		return ((float)this.next) * (1.0f/this.frameRate);
	}

	@Override
	public String[] getJointIDs() {
		return this.jointIDs;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public float getTime() {
		return this.time;
	}

	@Override
	public Class<MD5Anim> getClassTag() {
		return MD5Anim.class;
	}

	@Override
	public void write(JMEExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(this.name, "Name", null);
		oc.write(this.jointIDs, "JointIDs", null);
		oc.write(this.frames, "Frames", null);
		oc.write(this.frameRate, "FrameRate", 0);
		oc.write(this.frameTimes, "FrameTimes", null);
		oc.write(this.animationTime, "AnimationTime", 0);
	}

	@Override
	public void read(JMEImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		this.name = ic.readString("Name", null);
		this.jointIDs = ic.readStringArray("JointIDs", null);
		Savable[] temp = ic.readSavableArray("Frames", null);
		this.frames = new IFrame[temp.length];
		for(int i = 0; i < temp.length; i++) {
			this.frames[i] = (IFrame)temp[i];
		}
		this.frameRate = ic.readFloat("FrameRate", 0);
		this.frameTimes = ic.readFloatArray("FrameTimes", null);
		this.animationTime = ic.readFloat("AnimationTime", 0);
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object object) {
		if(object == this) return true;
		if(object instanceof MD5Anim) {
			MD5Anim given = (MD5Anim)object;
			return given.name.equals(this.name);
		}
		return false;
	}

	@Override
	public IMD5Anim clone() {
		String[] clonedIDs = new String[this.jointIDs.length];
		for(int i = 0; i < clonedIDs.length; i++) clonedIDs[i] = new String(this.jointIDs[i]);
		IFrame[] clonedFrames = new IFrame[this.frames.length];
		for(int i = 0; i < clonedFrames.length; i++) clonedFrames[i] = this.frames[i].clone();
		MD5Anim clone = new MD5Anim(new String(this.name), clonedIDs, clonedFrames, this.frameRate);
		return clone;
	}
}
