package com.md5importer.interfaces;

/**
 * <code>IObservable</code> defines the interface of a mutable data
 * unit that can be monitored by other observers for notifications
 * when the mutable data is modified.
 * <p>
 * <code>IObservable</code> provides the external invocation method
 * to notify the registered observers when the mutable state is
 * modified. This notification is sent sequentially to all registered
 * observers.
 * <p>
 * <code>IObservable</code> provides thread safety on both the
 * register and notify methods by delegating thread safety to the
 * underlying thread safe data structures.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-24-2009 22:04 EST
 * @version Modified date: 03-24-2009 22:13 EST
 */
public interface IObservable {

	/**
	 * Notify the registered observers for update.
	 */
	public void notifyUpdate();

	/**
	 * Register the given observer to be notified when the state changes.
	 * @param observer The <code>IObserver</code> to be registered.
	 */
	public void register(IObserver observer);
}
