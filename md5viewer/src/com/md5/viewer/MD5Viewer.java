package com.md5.viewer;

import com.md5.viewer.player.AnimationPlayer;
import com.md5.viewer.selector.AnimationSelector;

/**
 * <code>MD5Viewer</code> defines the concrete implementation of a viewer
 * unit that allows user to selected one or more animations and view them
 * in a chain in manual or automatic mode.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:08 EST
 * @version Modified date: 11-24-2008 22:33 EST
 */
public class MD5Viewer {

	/**
	 * Main method.
	 */
	public static void main(String[] args) {
		AnimationSelector selector = new AnimationSelector();
		selector.initialize();
		selector.display();
		// Wait till the animation selection is completed.
		while(!selector.isCompleted()) try {Thread.sleep(300);} catch (InterruptedException e) {}
		// Start player.
		AnimationPlayer player = new AnimationPlayer(selector.getDirectory(), selector.getHierarchy(),
				selector.getBaseAnim(), selector.getAnimations(), selector.getPlayMode());
		player.start();
	}
}
