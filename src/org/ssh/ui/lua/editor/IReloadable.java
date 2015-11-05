package org.ssh.ui.lua.editor;

/**
 * Interface used to specify which objects can be reloaded in the {@link ScriptEditor} 
 * 
 * If you, for example, want the {@link Robot} class to be reloaded
 * after its lua script has been edited, make sure it implements this interface.
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public interface IReloadable {
	/**
	 * Reloads the object, reinitializing it using its lua script
	 */
	void reload();

	/**
	 * Classes implementing this interface should use a lua script to initialize themselves
	 * @return The path to the lua script used to initialize this object.
	 */
	String getPath();
}
