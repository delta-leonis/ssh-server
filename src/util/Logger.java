package util;

import java.util.logging.LogManager;

/**
 * Expanded Logger with build in formatter
 * 
 * @author Jeroen
 *
 */
public class Logger extends java.util.logging.Logger {

	/**
	 * Creates a Logger instance
	 * @param name
	 * @param resourceBundleName
	 */
	protected Logger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}

	/**
	 * gets a util.logger that uses the classname of the class that called this method using the StackTrace
	 * 
	 * @return a suitable util.logger
	 */
	public static Logger getLogger() {
		//return getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
		// is this more better?
		return getLogger(new SecurityManager() { String className = getClassContext()[2].getName(); }.className);
		
	}

	/**
	 * get a java.util.logging.logger and cast it to util.logger for String.format functionality
	 * 
	 * @param name	 A name for the logger. This should be a dot-separated name and should normally be based on the package name or class name of the subsystem, such as java.net or javax.swing
	 * @return	a suitable Logger
	 */
    public static Logger getLogger(String name) {
        LogManager m = LogManager.getLogManager();
        Object l = m.getLogger(name);
        if (l == null) m.addLogger(new Logger(name, null));
        l = m.getLogger(name);
        return (Logger)l;
	}
	
    /**
     * 
     * Logs a SEVERE formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the SEVERE message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void severe(String format, Object... args){
		super.severe(String.format(format, args)); 
	}
	
    /**
     * 
     * Logs a INFO formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the INFO message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void info(String format, Object... args){
		super.info(String.format(format, args)); 
	}

    /**
     * 
     * Logs a FINE formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the FINE message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void fine(String format, Object... args){
		super.fine(String.format(format, args)); 
	}

    /**
     * 
     * Logs a FINER formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the FINER message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void finer(String format, Object... args){
		super.finer(String.format(format, args)); 
	}

    /**
     * 
     * Logs a FINEST formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the FINEST message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void finest(String format, Object... args){
		super.finest(String.format(format, args)); 
	}

    /**
     * 
     * Logs a CONFIG formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the CONFIG message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void config(String format, Object... args){
		super.config(String.format(format, args)); 
	}

    /**
     * 
     * Logs a WARNING formatted string using the specified format string and arguments.
     * 
     * If the logger is currently enabled for the WARNING message level then the given message is forwarded to all the registered output Handler objects.
	 * @param  format
     *         A format string</a>
     *
     * @param  args
     *         Arguments referenced by the format specifiers in the format
     *         string.  If there are more arguments than format specifiers, the
     *         extra arguments are ignored.  The number of arguments is
     *         variable and may be zero.  The maximum number of arguments is
     *         limited by the maximum dimension of a Java array as defined by
     *         <cite>The Java&trade; Virtual Machine Specification</cite>.
     *         The behaviour on a
     *         {@code null} argument depends on the conversion</a>.
     *
     * @see  java.util.Formatter
     */
	public void warning(String format, Object... args){
		super.warning(String.format(format, args)); 
	}
}
