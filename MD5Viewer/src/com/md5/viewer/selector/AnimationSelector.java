package com.md5.viewer.selector;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

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
 * @version Modified date: 11-24-2008 14:54 EST
 */
public class AnimationSelector {
	/**
	 * The <code>AnimationSelectorGUI</code> instance.
	 */
	private AnimationSelectorGUI gui; 
	/**
	 * The <code>HierarchyLoader</code> instance.
	 */
	private HierarchyLoader loader;
	/**
	 * The <code>String</code> directory of the hierarchy.
	 */
	private String dir;
	/**
	 * The <code>List</code> of <code>String</code> animated parts.
	 */
	private List<String> hierarchy;
	/**
	 * The <code>URL</code> link to the base animation.
	 */
	private URL baseAnimURL;
	/**
	 * The <code>Map</code> of <code>String</code> file name and file
	 * <code>URL</code> link to the animations.
	 */
	private Map<String, URL> animsURL;
	/**
	 * The flag indicates if the playback mode is manual.
	 */
	private boolean manual;

	/**
	 * Constructor of <code>AnimationSelector</code>.
	 */
	public AnimationSelector() {
		this.animsURL = new HashMap<String, URL>();
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
	 * Add the given URL animation to the chain list.
	 * @param url The <code>URL</code> links to the animation file.
	 */
	public void addAnimation(URL url) {
		if(url == null) return;
		String name = this.getFileName(url);
		if(this.animsURL.containsKey(name)) {
			JOptionPane.showMessageDialog(null, "Selected animation is already added.");
			return;
		}
		this.animsURL.put(name, url);
		this.gui.addAnimation(name);
	}
	
	/**
	 * Remove the animation file with given file name.
	 * @param fileName The <code>String</code> file name.
	 */
	public void removeAnimation(String fileName) {
		this.animsURL.remove(fileName);
		this.gui.removeAnimation(fileName);
	}
	
	/**
	 * Set the hierarchy URL.
	 * @param url The <code>URL</code> link to the hierarchy file.
	 */
	public void setHierarchyURL(URL url) {
		if(url == null) return;
		try {
			this.hierarchy = this.loader.load(url);
			this.dir = this.loader.getBaseDirectory();
			this.gui.setHierarchyText(this.getFileName(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the base animation URL.
	 * @param url The <code>URL</code> link to the base animation file.
	 */
	public void setBaseAnimURL(URL url) {
		if(url == null) return;
		this.baseAnimURL = url;
		this.gui.setBaseAnimText(this.getFileName(url));
	}
	
	/**
	 * Retrieve the selector GUI.
	 * @return The <code>AnimationSelectorGUI</code> instance.
	 */
	public AnimationSelectorGUI getGUI() {
		return this.gui;
	}
	
	/**
	 * Retrieve the name of the file linked by given URL.
	 * @param url The <code>URL</code> that links to the file.
	 * @return The <code>String</code> file name.
	 */
	private String getFileName(URL url) {
		String file = url.getFile();
		return file.substring(file.lastIndexOf("/")+1, file.length());
	}
}
