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
    private static final transient String BASE_PATH          = "config";
    /**
     * Folder for the default config files
     */
    private static final transient String DEFAULT_FOLDER     = "default";
    /**
     * Folder for settings which override default settings
     */
    private static final transient String PROFILE_FOLDER     = System.getProperty("user.name");
    /**
     * Folder for settings that should be set when the application closes, and overrides both
     * default, and profile settings
     */
    private static final transient String LASTSESSION_FOLDER = "lastsession";
    /**
     * Separator for folders (should be {@link File.separator}, but URI can't play nice)
     */
    private static final transient String SEPARATOR          = "/";
                                                             
    /**
     * Create a settings model
     */
    public Settings() {
        super("settings");
    }
    
    /**
     * @return base path for all configfiles
     */
    public URI getBasePath() {
        return URI.create(Settings.BASE_PATH);
    }
    
    /**
     * if profilepath (and thus lastsession) are both empty, the default path should be used
     * 
     * @return default path for settings and modeldumps
     */
    public URI getDefaultPath() {
        return URI.create(Settings.BASE_PATH + Settings.SEPARATOR + Settings.DEFAULT_FOLDER + Settings.SEPARATOR);
    }
    
    /**
     * if lastSessionpath is empty, the profile path should be read
     * 
     * @return profile specific path for settings and modeldumps
     */
    public URI getProfilePath() {
        return URI.create(Settings.BASE_PATH + Settings.SEPARATOR + Settings.PROFILE_FOLDER + Settings.SEPARATOR);
    }
    
    /**
     * @return path for settings and modeldumps that were loaded since last shutdown
     */
    public URI getLastSessionPath() {
        return URI.create(getProfilePath() + Settings.SEPARATOR + Settings.LASTSESSION_FOLDER + Settings.SEPARATOR);
    }
    
    @Override
    public String getSuffix(){
        return "";
    }
    
}
