package nl.saxion.robosim.model;

import java.io.*;
import java.util.Properties;

/**
 * Created by Damon on 4-6-2015.
 */
public class Settings {
    private boolean teamBlue = true;
    private boolean teamYellow = true;
    private String fileName;
    //incoming ip / incoming port / outgoing ip, outgoing port
    private String iip, iport, oip, oport;
    private String refIp, refPort;
    private String accel, speed;
    private int keeperId;
    private final String CONFIG = "config.properties";

    //singleton variable
    private static Settings settings;


    public static Settings getInstance() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    /**
     * reads the values from the config.properties file
     */
    private Settings() {
        /* default values
        this.iip = "224.5.23.2";
        this.iport = "10002";
        this.oip = "224.5.23.20";
        this.oport = "1337";
        */
        Properties prop = new Properties();
        InputStream input = null;

        File f = new File(CONFIG);
        if (!f.exists()) {
            try {
                reset(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {

            input = new FileInputStream(f);

            // load a properties file
            prop.load(input);

            // get the property value and set the variables
            this.iip = prop.getProperty("inputIp");
            this.iport = prop.getProperty("inputPort");
            this.oip = prop.getProperty("outputIp");
            this.oport = prop.getProperty("outputPort");
            this.refIp = prop.getProperty("refereeIp");
            this.refPort = prop.getProperty("refereePort");
            this.speed   = prop.getProperty("robotSpeed");
            this.accel   = prop.getProperty("robotAcceleration");
            if (iip == null || iport == null ||
                    oip == null || oport == null ||
                    refIp == null || refPort == null ||
                    speed == null || accel == null) {
                reset(f);
            }


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
     *
     * @param blue   simulate team blue?
     * @param yellow simulate team yellow?
     * @param file   what log to read.
     */
    public void setSimulationSettings(boolean blue, boolean yellow, String file, int keeperId) {
        this.teamBlue = blue;
        this.teamYellow = yellow;
        this.fileName = file;
        this.keeperId = keeperId;
    }



    /**
     * Set communication settings, input receives from ai, output sends to ai.
     *
     * @param iip   Input ip address
     * @param iport input port
     * @param oip   output ip address
     * @param oport output port
     */
    public void setCommunicationSettings(String iip, String iport, String oip, String oport) {
        FileInputStream in = null;
        this.iip = iip;
        this.iport = iport;
        this.oip = oip;
        this.oport = oport;
        try {
            in = new FileInputStream(CONFIG);
            Properties props = new Properties();
            props.load(in);
            in.close();
            //set properties to new values
            FileOutputStream out = new FileOutputStream(CONFIG);
            props.setProperty("refereeIp", refIp);
            props.setProperty("refereeIp", refPort);
            props.setProperty("inputIp", iip);
            props.setProperty("outputIp", oip);
            props.setProperty("inputPort", iport);
            props.setProperty("outputPort", oport);
            props.setProperty("robotSpeed", speed + "");
            props.setProperty("robotAcceleration", accel + "");
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRobotSettings(String accel, String speed) {
        this.accel = accel;
        this.speed = speed;
        FileInputStream in = null;
        try {
            in = new FileInputStream(CONFIG);
            FileOutputStream out = new FileOutputStream(CONFIG);

            Properties props = new Properties();
            props.load(in);
            in.close();
            props.setProperty("refereeIp", refIp);
            props.setProperty("refereePort", refPort);
            props.setProperty("inputIp", iip);
            props.setProperty("outputIp", oip);
            props.setProperty("inputPort", iport);
            props.setProperty("outputPort", oport);
            props.setProperty("robotSpeed", speed + "");
            props.setProperty("robotAcceleration" +
                    "", accel + "");
            props.store(out, null);
            out.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRefSettings(String ip, String port) {
        FileInputStream in = null;
        this.refIp = ip;
        this.refPort = port;
        try {
            in = new FileInputStream(CONFIG);
            FileOutputStream out = new FileOutputStream(CONFIG);

            Properties props = new Properties();
            props.load(in);
            in.close();
            props.setProperty("refereeIp", ip);
            props.setProperty("refereePort", port);
            props.setProperty("inputIp", iip);
            props.setProperty("outputIp", oip);
            props.setProperty("inputPort", iport);
            props.setProperty("outputPort", oport);
            props.setProperty("robotSpeed", speed + "");
            props.setProperty("robotAcceleration", accel + "");
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRefPort() {
        return refPort;
    }

    public String getRefIp() {
        return refIp;
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

    public int getKeeperId() {
        return keeperId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSpeed() {return Integer.parseInt(speed); }

    public int getAcceleration() { return Integer.parseInt(accel); }

    public boolean hasTeamYellow() {

        return teamYellow;
    }

    public boolean hasTeamBlue() {

        return teamBlue;
    }

    public static void destroy() {
        settings = null;
    }

    public void reset(File f) throws FileNotFoundException {
        PrintWriter w = new PrintWriter(f);
        w.write("outputIp=224.5.23.2\n" +
                "inputIp=224.5.23.20\n" +
                "inputPort=1337\n" +
                "refereePort=10003\n" +
                "outputPort=10002\n" +
                "refereeIp=224.5.23.1\n" +
                "robotSpeed=500\n" +
                "robotAcceleration=120\n");
        w.close();

    }

}
