package org.ssh.models;


public class NetworkSettings extends Model {
    private String IP;
    private int port;

    private String suffix;
    
    public NetworkSettings(String suffix) {
        super("NetworkSettings");
    }
    
    @Override
    public String getSuffix(){
        return suffix;
    }
    
    @Override
    public String getConfigName(){
        return String.format("%s_%s.json", getName(), getSuffix());
    }

}
