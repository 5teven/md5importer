package com.md5.viewer.selector.gui.handler;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.md5.viewer.selector.AnimationSelector;
import com.md5.viewer.selector.gui.enumn.EButton;

/**
 * <code>MD5MouseHandler</code> defines the mouse click handler for all
 * buttons in the <code>AnimationSelector</code>.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-24-2008 12:52 EST
 * @version Modified date: 11-24-2008 14:14 EST
 */
public class MD5MouseHandler extends MouseAdapter {
	/**
	 * The <code>AnimationSelector</code> instance.
	 */
	private final AnimationSelector seletor;
	/**
	 * The <code>JFileChooser</code> instance.
	 */
	private final JFileChooser chooser;
	/**
	 * The <code>MD5FileFilter</code> instance.
	 */
	private final MD5FileFilter filter;

	/**
	 * Constructor of <code>MD5MouseHandler</code>.
	 * @param selector The <code>AnimationSelector</code> instance.
	 */
	public MD5MouseHandler(AnimationSelector selector) {
		this.seletor = selector;
		this.chooser = new JFileChooser();
		this.filter = new MD5FileFilter();
		this.chooser.setFileFilter(this.filter);
	}

	public void mouseClicked(MouseEvent event) {
		String name = ((JButton)event.getSource()).getName();
		EButton enumn = EButton.getEnumn(name);
		try {
			switch(enumn) {
			case SelectHierarchy:
				this.seletor.setHierarchyURL(this.selectFile("Select Hierarchy file", null));
				break;
			case SelectBaseAnimation: break;
			case AddAnimation: break;
			case RemoveAnimation: break;
			case OK: break;
			case Cancel: break;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Select a file with given dialog title and file extension.
	 * @param title The <code>String</code> dialog title.
	 * @param extension The <code>String</code> file extension to filter.
	 * @return The selected file <code>URL</code>.
	 * @throws MalformedURLException If selection is interrupted.
	 */
	private URL selectFile(String title, final String extension) throws MalformedURLException {
		this.chooser.setDialogTitle(title);
		this.filter.setExtension(extension);
		this.chooser.showOpenDialog(null);
		File file = this.chooser.getSelectedFile();
		if(file != null) return this.chooser.getSelectedFile().toURI().toURL();
		else return null;
	}

	/**
	 * <code>MD5FileFilter</code> defines the concrete implementation of a
	 * file filter that allows dynamic filter extension modification.
	 *
	 * @author Yi Wang (Neakor)
	 * @author Tim Poliquin (Weenahmen)
	 * @version Creation date: 11-24-2008 13:35 EST
	 * @version Modified date: 11-24-2008 13:37 EST
	 */
	private class MD5FileFilter extends FileFilter {
		/**
		 * The <code>String</code> filter extension.
		 */
		private String extension;

		@Override
		public boolean accept(File f) {
			if(this.extension == null) return true;
			String name = f.getName();
			String ext = name.substring(name.lastIndexOf(".")+1, name.length());
			return ext.equalsIgnoreCase(this.extension);
		}
		@Override
		public String getDescription() {
			return this.extension + " files";
		}

		/**
		 * Set the extension to filter.
		 * @param extension The <code>String</code> file extension.
		 */
		private void setExtension(String extension) {
			this.extension = extension;
		}
	}
}
