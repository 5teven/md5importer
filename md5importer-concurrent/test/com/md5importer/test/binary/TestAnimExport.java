package com.md5importer.test.binary;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.util.export.binary.BinaryExporter;
import com.md5importer.test.TestAnim;

/**
 * Demo shows how to export anim files.
 * Note that when exporting animations, all meshes need to be separated.
 * 
 * @author Yi Wang (Neakor)
 */
public class TestAnimExport extends TestAnim {
	
	private File bodyanimFile;
	private File headanimFile;

	@Override
	protected void setupGame() {
		super.setupGame();
		KeyBindingManager.getKeyBindingManager().set("export", KeyInput.KEY_O);
		URL bodyURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/bodyanim.jme");
		URL headURL = this.getClass().getClassLoader().getResource("com/md5importer/test/data/binary/headanim.jme");
		try {
			this.bodyanimFile = new File(bodyURL.toURI());
			this.headanimFile = new File(headURL.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void simpleUpdate() {
		if(KeyBindingManager.getKeyBindingManager().isValidCommand("export", false)) {
			this.export();
		}
	}
	
	private void export() {
		try {
			BinaryExporter.getInstance().save(this.walk, this.bodyanimFile);
			BinaryExporter.getInstance().save(this.headAnim, this.headanimFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestAnimExport().start();
	}
}
