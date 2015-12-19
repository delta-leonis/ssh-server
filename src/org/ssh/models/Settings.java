package org.ssh.models;

import java.io.File;
import java.net.URI;

/**
 * This class contains all settings regarding the models and the configpaths.<br />
 * Folder structure: <br />
 * 
 * <pre>
 *   config/               This folder contains configuration files for the project
    |-  default/        This folder contains all default settings
    \-  customProfile   This folder contains settings for profile 'customProfile'
      \-  lastSession   This folder contains settings which were active in the last session in this profile
 * </pre>
 * 
 * @TODO change profile_folder to be settable, since Settings aren't only applicable to a user.name,
 *       but also a custom name
 * @TODO define these constants while initializing this Model in main manager class
 *       
 * @author Jeroen de Jong
 *         
 */
public class Settings extends Model {

    /**
     * Base path for all config files
     */
    private static final transient String BASE_PATH       = "config";
    /**
     * Folder for the default config files
     */
    private static final transient String DEFAULT_FOLDER  = "default";
                                                          
    private static final transient String TEMP_FOLDER     = "temp";
    /**
     * Folder for settings which override default settings
     */
    private static final transient String USER_PROFILES   = System.getProperty("user.name");
                                                          
    /**
     * Separator for folders (should be {@link File#separator}, but URI can't play nice)
     */
    private static final transient String SEPARATOR       = "/";
                                                          
    private String                        default_profile = "";
                                                          
    private transient String              current_profile = "";

    /** The folder containing all init files that are run whenever the {@link org.ssh.ui.lua.console.Console} is initialized */
    private String luaInitFolder = "";
    /** Folder containing all other lua scripts */
    private String luaScriptFolder = "";
    /** The path to application.css */
    private String applicationCss = "";

    /**
     * Create a settings model
     */
    public Settings() {
        super("settings", "");
    }
    
    /**
     * @return base path for all configfiles
     */
    public URI getBasePath() {
        return URI.create(Settings.BASE_PATH);
    }
    
    /**
     * if profilepath (and thus lastsession) are both empty, the default path should be used.
     * 
     * @return default path for settings and modeldumps.
     */
    public URI getDefaultPath() {
        return URI.create(Settings.BASE_PATH + Settings.SEPARATOR + Settings.DEFAULT_FOLDER + Settings.SEPARATOR);
    }
    
    /**
     * note: Settings.json is also stored here.
     * 
     * @return Path to folder containing all profiles for a specific user.
     */
    public URI getUserProfilePath() {
        return URI.create((Settings.BASE_PATH + Settings.SEPARATOR + Settings.USER_PROFILES + Settings.SEPARATOR).replace(" ", ""));
    }
    
    /**
     * if lastSessionpath is empty, the profile path should be read.
     * 
     * @return profile specific path for settings and modeldumps
     */
    public URI getProfilePath() {
        return URI.create(getUserProfilePath() + getProfile() + Settings.SEPARATOR);
    }
    
    /**
     * @return Current (should be) loaded profile.
     */
    public String getProfile() {
        return current_profile.isEmpty() ? default_profile.isEmpty() ? Settings.TEMP_FOLDER : default_profile
                : current_profile;
    }

    /**
     * @return The path to the folder containing all lua init scripts
     */
    public String getLuaInitFolder(){
        return luaInitFolder;
    }

    /**
     * @return The path to the folder containing all scripts
     */
    public String getScriptFolder(){
        return luaScriptFolder;
    }

    /**
     * @return The path to the css file of the application
     */
    public String getApplicationCss(){
        return applicationCss;
    }
}
