package org.ssh.models.enums;

import org.ssh.ui.lua.console.AvailableInLua;

/**
 * Enum that describes different possible send methods. Not all are implemented, also the list is not final.
 * 
 * @author Jeroen
 *
 */
@AvailableInLua
public enum SendMethod {
	UDP, USB, WIFI_DIRECT, SERIAL, TCP, BLUETOOTH, HOMING_PIGEON, POST_NL, INFRARED, GPIO, DEBUG
}
