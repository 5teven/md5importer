package com.md5importer.interfaces;

/**
 * <code>IObserver</code> defines the interface of a logic unit
 * that is registered to monitor a single observable unit. It
 * is notified whenever the monitored observable is modified.
 * <p>
 * <code>IObserver</code> <code>update</code> method is invoked
 * when an update notification is sent by the monitored observable.
 * This notification is sent in the same thread as the invoking
 * thread of the notify method on the observable end.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-24-2009 22:08 EST
 * @version Modified date: 03-24-2009 22:14 EST
 */
public interface IObserver {

	/**
	 * Update the observer based on given modified observable.
	 * @param observable The modified <code>IObservable</code>.
	 */
	public void update(IObservable observable);
}
