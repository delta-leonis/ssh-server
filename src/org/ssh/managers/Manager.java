package org.ssh.managers;


import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.managers.manager.UI;
import org.ssh.util.Logger;

/**
 * The Class Manager.
 * 
 * A Manager is a DAO that handles a specific type of {@link Manageable}. Examples of Managers are
 * {@link Services}, {@link Models}, and {@link UI}.
 *
 * @param <M>
 *            the generic type of {@link Manageable} the Manager can handle.
 *            
 * @author Rimon Oz
 */
public interface Manager<M extends Manageable> {

    // a logger for good measure
    static final Logger  LOG = Logger.getLogger();
 
}
