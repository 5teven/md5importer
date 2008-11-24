package com.md5.viewer.selector;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.md5.viewer.player.AnimationPlayer;
import com.md5.viewer.selector.gui.AnimationSelectorGUI;
import com.md5.viewer.selector.gui.handler.MD5MouseHandler;

/**
 * <code>AnimationSelector</code> defines the concrete implementation of a
 * selector unit that is responsible for allowing the user to select one
 * or more animations to play. It delegates the selected animation links
 * to the <code>AnimationPlayer</code> for actual rendering.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:09 EST
 * @version Modified date: 11-24-2008 14:15 EST
 */
public class AnimationSelector {
	/**
	 * The <code>List</code> of selected animation <code>URL</code>.
	 */
	private final List<URL> urls;
	/**
	 * The <code>AnimationSelectorGUI</code> instance.
	 */
	private AnimationSelectorGUI gui; 
	/**
	 * The <code>HierarchyLoader</code> instance.
	 */
	private HierarchyLoader loader;
	/**
	 * The flag indicates if the playback mode should be manual.
	 */
	private boolean manual;
	/**
	 * The <code>String</code> directory of the hierarchy.
	 */
	private String dir;
	/**
	 * The <code>List</code> of <code>String</code> animated parts.
	 */
	private List<String> hierarchy;

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
		this.gui = new AnimationSelectorGUI(new MD5MouseHandler(this));
		this.loader = new HierarchyLoader();
		this.gui.initComponents();
	}
	
	/**
	 * Display the GUI.
	 */
	public void display() {
		this.gui.setVisible(true);
	}
	
	/**
	 * Set the hierarchy URL.
	 * @param url The <code>URL</code> link to the hierarchy file.
	 */
	public void setHierarchyURL(URL url) {
		try {
			this.hierarchy = this.loader.load(url);
			this.dir = this.loader.getBaseDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// This method should be placed into the button handler.
	public void startPlayer() {
		// TODO Load actual hierarchy from file.
		try {
			AnimationPlayer player = new AnimationPlayer(null, this.loader.load(null), null, this.urls, this.manual);
			player.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
