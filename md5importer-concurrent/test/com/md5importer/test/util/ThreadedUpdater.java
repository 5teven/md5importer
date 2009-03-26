package com.md5importer.test.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.md5importer.interfaces.control.IController;

public class ThreadedUpdater implements Runnable {

	private final Queue<IController> controllers;
	private final float time;
	private final Thread thread;
	private volatile boolean started;
	private float interpolation;

	public ThreadedUpdater(int rate) {
		this.controllers = new ConcurrentLinkedQueue<IController>();
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
	
	public void addController(IController controller) {
		this.controllers.add(controller);
	}
	
	public void removeController(IController controller) {
		this.controllers.remove(controller);
	}

	@Override
	public void run() {
		while(this.started) {
			final long start = System.currentTimeMillis();
			for(IController controller : this.controllers) controller.update(this.interpolation);
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
}
