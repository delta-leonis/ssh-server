package org.ssh.util;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;

/**
 * Expanded Logger with build in formatter. This logger will also
 * auto subscribe any loggger to a new instance of {@link LoggerMemoryHandler} if it starts
 * with "org.ssh".
 *
 * @author Jeroen de Jong
 * @author Rimon Oz
 */
public class Logger extends java.util.logging.Logger {

    /**
     * Maximum amount of LogRows that will be stored in cache
     */
    private static final int MAX_CACHE_SIZE = 5000;

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
     * gets a logger that uses the classname of the class that called this method using
     * the StackTrace
     *
     * @return a suitable logger
     */
    public static Logger getLogger() {
        // Retrieve the calling class name by :
        // 1. creating an anonymous SecurityManager this way we can call the private method #getClassContext()
        String callingClassName = new SecurityManager() {
            // 2. create a public field in which we store the callers name.
            String className = this.getClassContext()[2].getName();
        }// 3. access the classname outside this scope
                .className;
        // 4. ???

        // 5. profit!
        return Logger.getLogger(callingClassName);
    }

    /**
     * get a {@link java.util.logging.Logger} and cast it to {@link Logger} for String.format
     * functionality. If the loggername starts with "org.ssh", a new {@link LoggerMemoryHandler} will be
     * attached for future use ({@link org.ssh.ui.components.overlay.LoggerConsole} for example)
     *
     * @param name A name for the logger. This should be a dot-separated name and should normally be
     *             based on the package name or class name of the subsystem, such as java.net or
     *             javax.swing
     * @return a suitable Logger
     */
    public static Logger getLogger(String name) {
        //retrieve the manager
        final LogManager logManager = LogManager.getLogManager();
        //retrieve the logger with given name
        Logger logger = (Logger) logManager.getLogger(name);
        //if it doesn't work
        if (logger == null) {
            // create the logger
            logManager.addLogger(new Logger(name, null));
            //retrieve the logger
            logger = (Logger) logManager.getLogger(name);
        }

        // by default every loglevel should be accepted
        logger.setLevel(Level.ALL);

        // if it is a logger for starting with org.ssh,
        // we probably want a LoggerMemoryHandler attached.
        if (name.matches("^org\\.ssh(.*?)$"))
            logger.attachMemoryHandler();

        // oh, we're finished... let's return the logger then.
        return logger;
    }

    /**
     * Attaches a new {@link LoggerMemoryHandler} as long
     */
    private void attachMemoryHandler() {
        //make sure the name contains the right format
        if (!getName().matches("^org\\.ssh(.*?)$"))
            return;

        String packageName = getName();

        // org.ssh is an exception that will not need formatting
        if (!"org.ssh".equals(getName())) {
            // split at the dots (org[.]ssh[.]package[.]class
            String[] nameParts = getName().split("\\.", 4);
            // it should contain multiple dots
            if (nameParts.length < 3)
                return;
            //construct the name
            packageName = "org.ssh." + nameParts[2];
        }

        //retrieve the manager
        final LogManager logManager = LogManager.getLogManager();

        // get the logger of the package
        java.util.logging.Logger logger = logManager.getLogger(packageName);

        //create if it doesn't exist yet
        if (logger == null) {
            logManager.addLogger(new Logger(packageName, null));
            //retrieve
            logger = logManager.getLogger(packageName);
        }
        //attach a LoggerMemoryHandler
        logger.addHandler(new LoggerMemoryHandler(MAX_CACHE_SIZE));
    }

    /**
     * Logs a CONFIG formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the CONFIG message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void config(final String format, final Object... args) {
        super.config(String.format(format, args));
    }

    /**
     * Logs a FINE formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the FINE message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void fine(final String format, final Object... args) {
        super.fine(String.format(format, args));
    }

    /**
     * Logs a FINER formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the FINER message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void finer(final String format, final Object... args) {
        super.finer(String.format(format, args));
    }

    /**
     * Logs a FINEST formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the FINEST message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void finest(final String format, final Object... args) {
        super.finest(String.format(format, args));
    }

    /**
     * Logs a INFO formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the INFO message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void info(final String format, final Object... args) {
        super.info(String.format(format, args));
    }

    /**
     * Logs a SEVERE formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the SEVERE message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void severe(final String format, final Object... args) {
        super.severe(String.format(format, args));
    }

    /**
     * Logs a WARNING formatted string using the specified format string and arguments.
     * <p>
     * If the logger is currently enabled for the WARNING message level then the given message is
     * forwarded to all the registered output Handler objects.
     *
     * @param format A format string</a>
     * @param args   Arguments referenced by the format specifiers in the format string. If there are
     *               more arguments than format specifiers, the extra arguments are ignored. The number
     *               of arguments is variable and may be zero. The maximum number of arguments is
     *               limited by the maximum dimension of a Java array as defined by <cite>The
     *               Java&trade; Virtual Machine Specification</cite>. The behaviour on a {@code null}
     *               argument depends on the conversion</a>.
     * @see java.util.Formatter
     */
    public void warning(final String format, final Object... args) {
        super.warning(String.format(format, args));
    }

    /**
     * Logs an EXCEPTION.
     * <p>
     * If the logger is currently enabled for the EXCEPTION message level then the given message is
     * forwarded to log level FINEST
     *
     * @param exception The exception that occured, this will be formatted
     * @see java.util.Formatter
     */
    public void exception(final Exception exception) {
        super.warning(Stream.of(exception.getStackTrace())
                .reduce("",
                        (result, curRule) -> String.format("%s%s%n", result, curRule.toString()),
                        (left, right) -> left + right)
                .toString());
    }
}
