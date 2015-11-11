package org.ssh.util;

import java.util.logging.LogManager;
import java.util.stream.Stream;

/**
 * Expanded Logger with build in formatter
 *
 * @author Jeroen de Jong
 * @author Rimon Oz
 *        
 */
public class Logger extends java.util.logging.Logger {
    
    /**
     * gets a org.ssh.util.logger that uses the classname of the class that called this method using
     * the StackTrace
     * 
     * @return a suitable org.ssh.util.logger
     */
    public static Logger getLogger() {
        // return getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
        // is this more better?
        return Logger.getLogger(new SecurityManager() {
            
            String className = this.getClassContext()[2].getName();
        }.className);
        
    }
    
    /**
     * get a java.util.logging.logger and cast it to org.ssh.util.logger for String.format
     * functionality
     * 
     * @param name
     *            A name for the logger. This should be a dot-separated name and should normally be
     *            based on the package name or class name of the subsystem, such as java.net or
     *            javax.swing
     * @return a suitable Logger
     */
    public static Logger getLogger(final String name) {
        final LogManager m = LogManager.getLogManager();
        Object l = m.getLogger(name);
        if (l == null) m.addLogger(new Logger(name, null));
        l = m.getLogger(name);
        return (Logger) l;
    }
    
    /**
     * Creates a Logger instance
     * 
     * @param name
     * @param resourceBundleName
     */
    protected Logger(final String name, final String resourceBundleName) {
        super(name, resourceBundleName);
    }
    
    /**
     *
     * Logs a CONFIG formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the CONFIG message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void config(final String format, final Object... args) {
        super.config(String.format(format, args));
    }
    
    /**
     *
     * Logs a FINE formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the FINE message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void fine(final String format, final Object... args) {
        super.fine(String.format(format, args));
    }
    
    /**
     *
     * Logs a FINER formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the FINER message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void finer(final String format, final Object... args) {
        super.finer(String.format(format, args));
    }
    
    /**
     *
     * Logs a FINEST formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the FINEST message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void finest(final String format, final Object... args) {
        super.finest(String.format(format, args));
    }
    
    /**
     *
     * Logs a INFO formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the INFO message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void info(final String format, final Object... args) {
        super.info(String.format(format, args));
    }
    
    /**
     *
     * Logs a SEVERE formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the SEVERE message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void severe(final String format, final Object... args) {
        super.severe(String.format(format, args));
    }
    
    /**
     *
     * Logs a WARNING formatted string using the specified format string and arguments.
     *
     * If the logger is currently enabled for the WARNING message level then the given message is
     * forwarded to all the registered org.ssh.senders Handler objects.
     * 
     * @param format
     *            A format string</a>
     *           
     * @param args
     *            Arguments referenced by the format specifiers in the format string. If there are
     *            more arguments than format specifiers, the extra arguments are ignored. The number
     *            of arguments is variable and may be zero. The maximum number of arguments is
     *            limited by the maximum dimension of a Java array as defined by <cite>The
     *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *            argument depends on the conversion</a>.
     *           
     * @see java.util.Formatter
     */
    public void warning(final String format, final Object... args) {
        super.warning(String.format(format, args));
    }
    
    /**
    *
    * Logs an EXCEPTION.
    *
    * If the logger is currently enabled for the EXCEPTION message level then the given message is
    * forwarded to log level FINEST
    * 
    * @param format
    *            A format string</a>
    *           
    * @param args
    *            Arguments referenced by the format specifiers in the format string. If there are
    *            more arguments than format specifiers, the extra arguments are ignored. The number
    *            of arguments is variable and may be zero. The maximum number of arguments is
    *            limited by the maximum dimension of a Java array as defined by <cite>The
    *            Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
    *            argument depends on the conversion</a>.
    *           
    * @see java.util.Formatter
    */
   public void exception(final Exception exception) {
       super.finest(Stream.of(exception.getStackTrace()).reduce("", (result, curRule) -> String.format("%s%s%n", result, curRule.toString()), (left, right) -> left + right).toString());
   }
}
