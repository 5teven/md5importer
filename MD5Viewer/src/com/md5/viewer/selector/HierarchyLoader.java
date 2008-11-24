package com.md5.viewer.selector;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * <code>HierarchyLoader</code> defines a file loading unit that is responsible
 * for loading in the animated parts hierarchy information.
 *
 * @author Yi Wang (Neakor)
 * @author Tim Poliquin (Weenahmen)
 * @version Creation date: 11-23-2008 23:19 EST
 * @version Modified date: 11-24-2008 14:14 EST
 */
public class HierarchyLoader {
	/**
	 * The <code>String</code> of loaded base directory.
	 */
	private String dir;

	/**
	 * Constructor of <code>HierarchyLoader</code>.
	 */
	public HierarchyLoader() {}
	
	/**
	 * Load the hierarchy information from the file linked by given URL.
	 * @param file The <code>URL</code> links to the file to be loaded.
	 * @return The loaded <code>List</code> of <code>String</code> hierarchy.
	 * @throws IOException If input scan is interrupted.
	 */
	public List<String> load(URL file) throws IOException {
		ArrayList<String> list = new ArrayList<String>();
		Scanner scanner = new Scanner(file.openStream());
		while(scanner.hasNextLine()) {
			list.add(scanner.nextLine().trim());
		}
		String raw = file.getPath().replaceAll("%20", " ");
		this.dir = raw.substring(0, raw.lastIndexOf("/") + 1);
		return list;
	}
	
	/**
	 * Retrieve the base directory for the hierarchy file and the mesh files.
	 * @return The <code>String</code> base directory.
	 */
	public String getBaseDirectory() {
		return this.dir;
	}
}
