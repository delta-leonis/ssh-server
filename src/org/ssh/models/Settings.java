package org.ssh.models;

import java.net.URI;

public class Settings extends Model {
    
    // TODO define these constants while initializing this Model in mail org.ssh.managers class
    private static final transient String BASE_PATH      = "config";
    private static final transient String DEFAULT_FOLDER = "default";
    private static final transient String PROFILE_FOLDER = System.getProperty("user.name");
                                                         
    private static final transient String separator      = "/";
                                                         
    public Settings() {
        super("settings");
    }
    
    public URI getBasePath() {
        return URI.create(Settings.BASE_PATH);
    }
    
    public URI getDefaultPath() {
        return URI.create(Settings.BASE_PATH + Settings.separator + Settings.DEFAULT_FOLDER + Settings.separator);
    }
    
    public URI getProfilePath() {
        return URI.create(Settings.BASE_PATH + Settings.separator + Settings.PROFILE_FOLDER + Settings.separator);
    }
    
}
