package org.ssh.managers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
abstract public class Manager<M extends Manageable> {
                                 
    // a logger for good measure
    private static final Logger  LOG = Logger.getLogger();
  
}
