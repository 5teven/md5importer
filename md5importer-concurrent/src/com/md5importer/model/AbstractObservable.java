package com.md5importer.model;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.md5importer.interfaces.IObservable;
import com.md5importer.interfaces.IObserver;

/**
 * <code>AbstractObservable</code> defines the basic abstraction of
 * an observable data unit that can be observed by various observers.
 * It only provides the most commonly shared implementations of all
 * observable units.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-24-2009 22:18 EST
 * @version Modified date: 03-24-2009 22:25 EST
 */
public abstract class AbstractObservable implements IObservable {
	/**
	 * The thread safe <code>Queue</code> of <code>IObserver</code>. 
	 */
	private final Queue<IObserver> observers;
	
	/**
	 * Constructor of <code>AbstractObservable</code>.
	 */
	protected AbstractObservable() {
		this.observers = new ConcurrentLinkedQueue<IObserver>();
	}

	@Override
	public void notifyUpdate() {
		for(IObserver observer : this.observers) {
			observer.update(this);
		}
	}

	@Override
	public void register(IObserver observer) {
		this.observers.add(observer);
	}
}
