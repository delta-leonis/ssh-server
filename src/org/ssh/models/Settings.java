package org.ssh.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
 * @TODO change profiles_folder to be settable, since Settings aren't only applicable to a user.name,
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
    private static final transient String DEFAULT_WORKSPACE = "default";
    /**
     * Folder used as standard workspace
     */
    private static final transient String TEMP_WORKSPACE = "temp";

    /**
     * Username of current user
     */
    private static final transient String USERNAME  = System.getProperty("user.name");

    /**
     * Separator for folders (should be {@link File#separator}, but URI can't play nice)
     */
    private static final transient String SEPARATOR       = "/";
                                                          
    private String defaultWorkspace;
                                                          
    private transient StringProperty      currentProfile;

    private transient String              currentWorkspace = "";

    /** The folder containing all init files that are run whenever the {@link org.ssh.ui.lua.console.Console} is initialized */
    private String luaInitFolder;
    /** Folder containing all other lua scripts */
    private String luaScriptFolder;
    /** The path to application.css */
    private String applicationCss;

    /**
     * Create a settings model
     */
    public Settings() {
        super("settings", "");
    }

    @Override
    public void initialize(){
        currentProfile = new SimpleStringProperty(USERNAME);
        luaInitFolder = "";
        luaScriptFolder = "";
        applicationCss = "";
        defaultWorkspace = "";
        resetWorkspace();
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
        return URI.create(Settings.BASE_PATH + Settings.SEPARATOR + Settings.DEFAULT_WORKSPACE + Settings.SEPARATOR);
    }
    
    /**
     * note: Settings.json is also stored here.
     * 
     * @return Path to folder containing all profiles for a specific user.
     */
    public URI getProfilesPath() {
        return URI.create((Settings.BASE_PATH + Settings.SEPARATOR + getProfile() + Settings.SEPARATOR).replace(" ", ""));
    }

    public StringProperty getCurrentProfileProperty(){
        return currentProfile;
    }
    public String getCurrentProfile(){
        return currentProfile.getValue();
    }

    /**
     * @return profile specific path for settings and modeldumps
     */
    public URI getCurrentWorkspacePath() {
        return URI.create(getProfilesPath() + getCurrentWorkspace() + Settings.SEPARATOR);
    }
    
    /**
     * @return Current (should be) loaded workspace.
     */
    public String getCurrentWorkspace() {
        return currentWorkspace;
    }

    public String getProfile(){
        return currentProfile.getValue().isEmpty() ?  Settings.DEFAULT_WORKSPACE :  currentProfile.getValue();
    }

    public void resetWorkspace(){
        // empty current workspace
        currentWorkspace = "";
        //if it isn't default, it should set a workspace
        if(!getDefaultPath().equals(getProfilesPath()))
            //which is either default, or a temp
            currentWorkspace = defaultWorkspace.isEmpty() ? Settings.TEMP_WORKSPACE : Settings.DEFAULT_WORKSPACE;
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
