package com.md5importer;

import java.io.IOException;
import java.net.URL;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.md5importer.interfaces.model.IMD5Anim;
import com.md5importer.interfaces.model.IMD5Node;
import com.md5importer.loader.AnimLoader;
import com.md5importer.loader.MeshLoader;
import com.md5importer.loader.ResourceLoader;

/**
 * <code>MD5Importer</code> defines a utility unit that contains
 * the logic of importing a MD5 formated skeletal animated model.
 * <p>
 * <code>MD5Importer</code> does not provide any thread safety.
 * It can be reused to repeatedly to import model files. However,
 * it requires external invocation to perform clean up before next
 * reuse.
 * <P>
 * For details on MD5 format, please go to official MD5 wiki at
 * {@link}http://www.modwiki.net/wiki/MD5_(file_format).
 *
 * @author Yi Wang (Neakor)
 * @version Modified date: 03-24-2009 14:56 EST
 */
public class MD5Importer {
	/**
	 * The mesh <code>ResourceImporter</code> instance.
	 */
	private final ResourceLoader<IMD5Node> meshImporter;
	/**
	 * The animation <code>ResourceImporter</code> instance.
	 */
	private final ResourceLoader<IMD5Anim> animImporter;

	/**
	 * Constructor of <code>MD5Importer</code>.
	 */
	public MD5Importer() {
		this.meshImporter = new MeshLoader();
		this.animImporter = new AnimLoader();
	}

	/**
	 * Load the given md5mesh file.
	 * @param md5mesh The <code>URL</code> points to the md5mesh file.
	 * @param name The <code>String</code> name of the loaded model.
	 * @return The loaded <code>IMD5Node</code> instance.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public IMD5Node loadMesh(URL md5mesh, String name) throws IOException {
		return this.meshImporter.load(md5mesh, name);
	}

	/**
	 * Load the given md5anim file.
	 * @param md5anim The <code>URL</code> points to the md5anim file.
	 * @param name The <code>String</code> name of the loaded animation.
	 * @return The loaded <code>IMD5Anim</code> instance.
	 * @throws IOException Thrown when errors occurred during file reading.
	 */
	public IMD5Anim loadAnim(URL md5anim, String name) throws IOException {
		return this.animImporter.load(md5anim, name);
	}

	/**
	 * Set the minification (MM) <code>Texture</code> filter.
	 * @param filter The minification (MM) <code>Texture</code> filter.
	 */
	public void setMiniFilter(Texture.MinificationFilter filter) {
		((MeshLoader)this.meshImporter).setMiniFilter(filter);
	}

	/**
	 * Set the magnification (FM) <code>Texture</code> filter.
	 * @param filter The magnification (FM) <code>Texture</code> filter.
	 */
	public void setMagFilter(Texture.MagnificationFilter filter) {
		((MeshLoader)this.meshImporter).setMagFilter(filter);
	}

	/**
	 * Set the texture anisotropic level.
	 * @param aniso The <code>Integer</code> anisotropic level value.
	 */
	public void setAnisotropic(int aniso) {
		((MeshLoader)this.meshImporter).setAnisotropic(aniso);
	}

	/**
	 * Set if oriented bounding should be used for the meshes.
	 * @param value The <code>Boolean</code> oriented bounding flag.
	 */
	public void setOrientedBounding(boolean value) {
		((MeshLoader)this.meshImporter).setOrientedBounding(value);
	}

	/**
	 * Retrieve the minification (MM) texture filter.
	 * @return The <code>MinificationFilter</code> enumeration.
	 */
	public MinificationFilter getMiniFilter() {
		return ((MeshLoader)this.meshImporter).getMiniFilter();
	}

	/**
	 * Retrieve the magnification (FM) texture filter.
	 * @return The <code>MagnificationFilter</code> enumeration.
	 */
	public MagnificationFilter getMagFilter() {
		return ((MeshLoader)this.meshImporter).getMagFilter();
	}

	/**
	 * Retrieve the anisotropic level.
	 * @return The <code>Integer</code> anisotropic level.
	 */
	public int getAnisotropic() {
		return ((MeshLoader)this.meshImporter).getAnisotropic();
	}

	/**
	 * Check if oriented bounding should be used.
	 * @return The <code>Boolean</code> oriented bounding flag.
	 */
	public boolean isOriented() {
		return ((MeshLoader)this.meshImporter).isOriented();
	}

	/**
	 * Cleanup the importer.
	 */
	public void cleanup() {
		this.meshImporter.cleanup();
		this.animImporter.cleanup();
	}
}
