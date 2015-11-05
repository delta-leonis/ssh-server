package org.ssh.models;

import java.net.URI;

public class Settings extends Model {
	// TODO define these constants while initializing this Model in mail org.ssh.managers class 
	private static final transient String BASE_PATH =  "config";
	private static final transient String DEFAULT_FOLDER = "default";
	private static final transient String PROFILE_FOLDER = System.getProperty("user.name");
	
	private static final transient String separator = "/";
	
	public Settings() {
		super("settings");
	}

	public URI getDefaultPath(){
		return URI.create(BASE_PATH + separator  + DEFAULT_FOLDER + separator);
	}
	
	public URI getBasePath() {
		return URI.create(BASE_PATH);
	}

	public URI getProfilePath() {
		return URI.create(BASE_PATH + separator + PROFILE_FOLDER + separator );
	}

}
