package com.undeadscythes.udsplugin.exceptions;

/**
 * @author UndeadScythes
 */
@SuppressWarnings("serial")
public class PlayerNotOnlineException extends PlayerException {
    public PlayerNotOnlineException(String name) {
        super(name + " is not currently online.");
    }
}