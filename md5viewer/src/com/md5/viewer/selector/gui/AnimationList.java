package com.md5.viewer.selector.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

/**
 * <code>AnimationList</code> defines the list model for displaying the
 * selected animations.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:46 EST
 * @version Modified date: 11-24-2008 14:38 EST
 */
public class AnimationList extends AbstractListModel {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -882927363570210321L;
	/**
	 * The <code>List</code> of <code>String</code> elements.
	 */
	private final List<String> list;
	
	/**
	 * Constructor of <code>AnimationList</code>.
	 */
	public AnimationList() {
		this.list = new ArrayList<String>();
	}
	
	/**
	 * Add the given element to this list.
	 * @param element The <code>String</code> element to be added.
	 */
	public void addElement(String element) {
		this.list.add(element);
	}

	@Override
	public Object getElementAt(int index) {
		return this.list.get(index);
	}

	@Override
	public int getSize() {
		return this.list.size();
	}
}
