package com.md5.viewer.selector.gui.handler;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import com.md5.viewer.selector.gui.enumn.EButton;

/**
 * <code>MD5MouseHandler</code> defines the mouse click handler for all
 * buttons in the <code>AnimationSelector</code>.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:52 EST
 * @version Modified date: 11-24-2008 13:09 EST
 */
public class MD5MouseHandler extends MouseAdapter {
	
	public void mouseClicked(MouseEvent event) {
		String name = ((JButton)event.getSource()).getName();
		EButton enumn = EButton.getEnumn(name);
		switch(enumn) {
		case SelectHierarchy: break;
		case SelectBaseAnimation: break;
		case AddAnimation: break;
		case RemoveAnimation: break;
		case OK: break;
		case Cancel: break;
		}
	}
}
