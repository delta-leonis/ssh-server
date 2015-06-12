package nl.saxion.robosim.model;

import java.io.*;
import java.util.Properties;

/**
 * Created by Damon on 4-6-2015.
 */
public class Settings {
    boolean teamBlue = true;
    boolean teamYellow = true;
    String fileName;
    //incoming ip / incoming port / outgoing ip, outgoing port
    String iip, iport, oip, oport;
    //singleton variable
    private static Settings settings;


    public static Settings getInstance() {
        if(settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * reads the values from the config.properties file
     */
    private Settings(){
        /* default values
        this.iip = "224.5.23.2";
        this.iport = "10002";
        this.oip = "224.5.23.20";
        this.oport = "1337";
        */
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            // load a properties file
            prop.load(input);

            // get the property value and set the variables
            this.iip = prop.getProperty("inputIp");
            this.iport = prop.getProperty("inputPort");
            this.oip   = prop.getProperty("outputIp");
            this.oport = prop.getProperty("outputPort");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Set simulation settings
     * @param blue simulate team blue?
     * @param yellow simulate team yellow?
     * @param file what log to read.
     */
    public void setSimulationSettings(boolean blue, boolean yellow, String file) {
        this.teamBlue     = blue;
        this.teamYellow   = yellow;
        this.fileName     = file;
    }

    /**
     * Set communication settings, input receives from ai, output sends to ai.
     * @param iip Input ip address
     * @param iport input port
     * @param oip output ip address
     * @param oport output port
     */
    public void setCommunicationSettings(String iip, String iport, String oip, String oport) {
        FileInputStream in = null;
        try {
            in = new FileInputStream("config.properties");
            Properties props = new Properties();
            props.load(in);
            in.close();
            //set properties to new values
            FileOutputStream out = new FileOutputStream("config.properties");
            props.setProperty("inputIp", iip);
            props.setProperty("outputIp", oip);
            props.setProperty("inputPort", iport);
            props.setProperty("outputPort", oport);
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIip() {
        return iip;
    }

    public String getIport() {
        return iport;
    }

    public String getOip() {
        return oip;
    }

    public String getOport() {
        return oport;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean hasTeamYellow() {

        return teamYellow;
    }

    public boolean hasTeamBlue() {

        return teamBlue;
    }
}
