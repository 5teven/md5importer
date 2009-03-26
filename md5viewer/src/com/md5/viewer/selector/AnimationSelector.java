package com.md5.viewer.selector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
 * @version Modified date: 11-24-2008 23:15 EST
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
	 * The base animation <code>File</code>.
	 */
	private File baseAnimFile;
	/**
	 * The <code>Map</code> of <code>String</code> file name and animations <code>File</code>.
	 */
	private final Map<String, File> animMap;
	/**
	 * The <code>List</code> of chain animation <code>File</code>.
	 */
	private final List<File> animFiles;
	/**
	 * The flag indicates if all selection is completed.
	 */
	private boolean completed;

	/**
	 * Constructor of <code>AnimationSelector</code>.
	 */
	public AnimationSelector() {
		this.animMap = new HashMap<String, File>();
		this.animFiles = new ArrayList<File>();
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
	 * Add the given animation file to the chain list.
	 * @param file The animation <code>File</code>.
	 */
	public void addAnimation(File file) {
		if(file == null) return;
		String name = file.getName();
		if(this.animMap.containsKey(name)) {
			JOptionPane.showMessageDialog(null, "Selected animation is already added.");
			return;
		}
		this.animMap.put(name, file);
		this.animFiles.add(file);
		this.gui.addAnimation(name);
	}
	
	/**
	 * Remove the animation file with given file name.
	 * @param fileName The <code>String</code> file name.
	 */
	public void removeAnimation(String fileName) {
		File file = this.animMap.remove(fileName);
		this.animFiles.remove(file);
		this.gui.removeAnimation(fileName);
	}
	
	/**
	 * Set the hierarchy file.
	 * @param file The hierarchy <code>File</code>.
	 */
	public void setHierarchyFile(File file) {
		if(file == null) return;
		try {
			this.hierarchy = this.loader.load(file);
			this.dir = this.loader.getDirectory();
			this.gui.setHierarchyText(file.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the base animation file.
	 * @param file The base animation <code>File</code>.
	 */
	public void setBaseAnimFile(File file) {
		if(file == null) return;
		this.baseAnimFile = file;
		this.gui.setBaseAnimText(file.getName());
	}
	
	/**
	 * Set the selection process to be completed.
	 */
	public void setCompleted() {
		this.completed = true;
		this.gui.setVisible(false);
	}

	/**
	 * Retrieve the selector GUI.
	 * @return The <code>AnimationSelectorGUI</code> instance.
	 */
	public AnimationSelectorGUI getGUI() {
		return this.gui;
	}
	
	/**
	 * Retrieve the base directory for the hierarchy file and the mesh files.
	 * @return The <code>String</code> base directory.
	 */
	public String getDirectory() {
		return this.dir;
	}
	
	/**
	 * Retrieve the mesh hierarchy list.
	 * @return The <code>List</code> of <code>String</code> hierarchy.
	 */
	public List<String> getHierarchy() {
		return this.hierarchy;
	}
	
	/**
	 * Retrieve the base animation file.
	 * @return The base animation <code>File</code>.
	 */
	public File getBaseAnim() {
		return this.baseAnimFile;
	}
	
	/**
	 * Retrieve the list of chain animation file.
	 * @return The <code>List</code> of chain animation <code>File</code>.
	 */
	public List<File> getAnimations() {
		return this.animFiles;
	}
	
	/**
	 * Retrieve the play back mode.
	 * @return True if the play back mode is manual. False automatic.
	 */
	public boolean getPlayMode() {
		return this.gui.isManual();
	}
	
	/**
	 * Check if the selection process is completed.
	 * @return True if the process is completed. False otherwise.
	 */
	public boolean isCompleted() {
		return this.completed;
	}
}
