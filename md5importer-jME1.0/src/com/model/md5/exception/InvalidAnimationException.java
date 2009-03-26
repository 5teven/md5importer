package com.model.md5.exception;

/**
 * <code>InvalidAnimationException</code> is thrown when an invalid
 * <code>JointAnimation</code> is being set to a previously loaded skeletal
 * system.
 * 
 * @author Yi Wang (Neakor)
 * @version Modified date: 05-01-2008 17:36 EST
 */
public class InvalidAnimationException extends RuntimeException {
	/**
	 * Serial version.
	 */
	private static final long serialVersionUID = -7115839715066462208L;
	/**
	 * Text message.
	 */
	private static final String message = "Animation does not match skeleton system.";

	/**
	 * Constructor of <code>InvalidAnimationException</code>.
	 */
	public InvalidAnimationException() {
		super(InvalidAnimationException.message);
	}
}
