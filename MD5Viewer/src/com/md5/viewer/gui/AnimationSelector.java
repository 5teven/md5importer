package com.md5.viewer.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.md5.viewer.player.AnimationPlayer;

/**
 * <code>AnimationSelector</code> defines the concrete implementation of a
 * selector unit that is responsible for allowing the user to select one
 * or more animations to play. It delegates the selected animation links
 * to the <code>AnimationPlayer</code> for actual rendering.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:09 EST
 * @version Modified date: 11-23-2008 23:09 EST
 */
public class AnimationSelector {
	/**
	 * The <code>List</code> of selected animation <code>URL</code>.
	 */
	private final List<URL> urls;
	/**
	 * The <code>HierarchyLoader</code> instance.
	 */
	private HierarchyLoader loader;
	/**
	 * The flag indicates if the playback mode should be manual.
	 */
	private boolean manual;
	
	/**
	 * Constructor of <code>AnimationSelector</code>.
	 */
	public AnimationSelector() {
		this.urls = new ArrayList<URL>();
	}

	/**
	 * Initialize the selector.
	 */
	public void initialize() {
		this.loader = new HierarchyLoader();
	}
	
	/**
	 * Display the GUI.
	 */
	public void display() {
		
	}
	
	// This method should be placed into the button handler.
	public void startPlayer() {
		// TODO Load actual hierarchy from file.
		AnimationPlayer player = new AnimationPlayer(this.urls, this.loader.load(null), this.manual);
		player.start();
	}
}
