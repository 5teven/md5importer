package com.md5importer.control;

import com.md5importer.enumn.ERepeatType;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.model.IMD5Anim;

/**
 * <code>MD5AnimController</code> defines the concrete implementation
 * of a logic controller unit that is responsible for updating the
 * <code>IMD5Anim</code> instance given at construction time.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 18:14 EST
 * @version Modified date: 04-01-2009 18:38 EST
 */
public class MD5AnimController extends AbstractController implements IMD5AnimController {
	/**
	 * The <code>IMD5Anim</code> instance.
	 */
	private final IMD5Anim anim;
	/**
	 * The <code>ERepeatType</code> enumeration.
	 */
	private volatile ERepeatType repeat;
	/**
	 * The <code>Float</code> speed.
	 */
	private volatile float speed;
	/**
	 * The time elapsed since last change in key frame.
	 */
	private volatile float time;
	/**
	 * The flag indicates if this animation is being played backwards.
	 */
	private volatile boolean backward;
	/**
	 * The flag indicates if this cycle is completed but the new cycle has not yet started.
	 */
	private volatile boolean complete;

	/**
	 * Constructor of <code>MD5AnimController</code>.
	 * @param anim The <code>IMD5Anim</code> instance.
	 */
	public MD5AnimController(IMD5Anim anim) {
		this.anim = anim;
		// Default values.
		this.repeat = ERepeatType.Wrap;
		this.speed = 1;
		this.time = 0;
		this.backward = false;
		this.complete = false;
	}

	@Override
	public void update(float interpolation) {
		if(!this.active) return;
		// Reset complete flag if repeat type is not clamp.
		if(this.complete && this.repeat != ERepeatType.Clamp) this.complete = false;
		if(this.complete) return;
		// Record last frame.
		final int lastPrev = this.anim.getPreviousIndex();
		final int lastNext = this.anim.getNextIndex();
		// Update frames.
		switch(this.repeat) {
		case Clamp: this.updateClamp(interpolation); break;
		case Cycle: this.updateCycle(interpolation); break;
		case Wrap: this.updateWrap(interpolation); break;
		}
		// Notify update if frame changed.
		if(lastPrev != this.anim.getPreviousIndex() || lastNext != this.anim.getNextIndex()) this.anim.notifyUpdate();
	}

	/**
	 * Update as the repeat mode is set to clamp.
	 * @param interpolation The <code>Float</code> time interpolation.
	 */
	private void updateClamp(float interpolation) {
		this.time = this.time + (interpolation * this.speed);
		while(this.time >= this.anim.getNextTime()) {
			this.anim.setIndices(this.anim.getPreviousIndex()+1, this.anim.getNextIndex()+1, this.time);
			if(this.anim.getNextIndex() == this.anim.getFrameCount() - 1) {
				this.anim.setIndices(this.anim.getFrameCount()-2, this.anim.getFrameCount()-1, this.time);
				this.complete = true;
				this.time = 0.0f;
				break;
			}
		}
	}

	/**
	 * Update as the repeat mode is set to cycle.
	 * @param interpolation The <code>Float</code> time interpolation.
	 */
	private void updateCycle(float interpolation) {
		if(!this.backward) {
			this.time = this.time + (interpolation * this.speed);
			while(this.time >= this.anim.getNextTime()) {
				this.anim.setIndices(this.anim.getPreviousIndex()+1, this.anim.getNextIndex()+1, this.time);
				if(this.anim.getNextIndex() == this.anim.getFrameCount() - 1) {
					this.backward = true;
					this.anim.setIndices(this.anim.getFrameCount()-1, this.anim.getFrameCount()-2, this.time);
					this.complete = true;
					this.time = this.anim.getPreviousTime();
					break;
				}
			}
		} else {
			this.time = this.time - (interpolation * this.speed);
			while(this.time <= this.anim.getNextTime()) {
				this.anim.setIndices(this.anim.getNextIndex(), this.anim.getNextIndex()-1, this.time);
				if(this.anim.getNextIndex() == 0) {
					this.backward = false;
					this.anim.setIndices(0, 1, this.time);
					this.complete = true;
					this.time = 0.0f;
					break;
				}
			}
		}
	}

	/**
	 * Update as the repeat mode is set to cycle.
	 * @param interpolation The <code>Float</code> time interpolation.
	 */
	private void updateWrap(float interpolation) {
		this.time = this.time + (interpolation * this.speed);
		while(this.time >= this.anim.getNextTime()) {
			this.anim.setIndices(this.anim.getPreviousIndex()+1, this.anim.getNextIndex()+1, this.time);
			if(this.anim.getNextIndex() == this.anim.getFrameCount() - 1) {
				this.anim.setIndices(0, 1, this.time);
				this.complete = true;
				this.time = 0.0f;
				break;
			}
		}
	}

	@Override
	public void setRepeatType(ERepeatType type) {
		this.repeat = type;
	}

	@Override
	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public ERepeatType getRepeatType() {
		return this.repeat;
	}

	@Override
	public IMD5Anim getAnim() {
		return this.anim;
	}

	@Override
	public void reset() {
		this.time = 0;
		this.complete = false;
		this.anim.setIndices(0, 1, 0);
	}

	@Override
	public boolean isBackward() {
		return this.backward;
	}

	@Override
	public boolean isCyleComplete() {
		return this.complete;
	}
}
