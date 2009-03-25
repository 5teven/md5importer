package com.md5importer.enumn;

/**
 * <code>ERepeatType</code> defines the enumeration values of all
 * possible controller repeat types.
 *
 * @author Yi Wang (Neakor)
 * @version Creation date: 03-23-2009 15:26 EST
 * @version Modified date: 03-23-2009 15:28 EST
 */
public enum ERepeatType {
	/**
	 * The clamp type that stops at the last frame.
	 */
	Clamp,
	/**
	 * The cycle type that updates backward after a complete cycle
	 * is finished.
	 */
	Cycle,
	/**
	 * The wrap type that updates back from the beginning after a
	 * complete cycle is finished.
	 */
	Wrap
}
