package com.md5importer.test.util;

import com.md5importer.control.MD5AnimController;
import com.md5importer.interfaces.control.IMD5AnimController;
import com.md5importer.interfaces.model.IMD5Anim;

public class ThreadedController implements Runnable {

	private final IMD5AnimController animController;
	private final float time;
	private final Thread thread;
	private volatile boolean started;
	private float interpolation;

	public ThreadedController(IMD5Anim anim, int rate) {
		this.animController = new MD5AnimController(anim);
		this.time = 1000.0f/(float)rate;
		this.thread = new Thread(this);
	}

	public void start() {
		synchronized(this.thread) {
			if(this.started) return;
			this.started = true;
			this.thread.start();
		}
	}

	public void stop() {
		this.started = false;
	}

	@Override
	public void run() {
		while(this.started) {
			final long start = System.currentTimeMillis();
			this.animController.update(this.interpolation);
			final long end = System.currentTimeMillis();
			final long diff = end - start;
			final long sleep = (long)(this.time - diff);
			this.interpolation = (diff / 1000.0f) + (sleep <= 0 ? 0 : sleep/1000.0f);
			if(sleep <= 0) continue;
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public IMD5AnimController getAnimController() {
		return this.animController;
	}
}
