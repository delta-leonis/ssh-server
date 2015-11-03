package ui.lua.console;

import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.luaj.vm2.LuaError;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import examples.CommunicatorExample;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import ui.UIComponent;
import ui.lua.utils.LuaUtils;

/**
 * Simple LuaJava console. The {@link Console} has access to every class that
 * uses the {@link AvailableInLua} annotation It features the following:
 * <ul>
 *  <li>Autocomplete on every class annotated with {@link AvailableInLua}</li>
 *  <li>Function and Object highlights</li>
 *  <li>Command history (Use up and down keys)</li>
 * </ul>
 * 
 * Example Command stolen from {@link CommunicatorExample}:
 * Communicator:register(SendMethod.UDP, luajava.newInstance("output.UDPSender"
 * , "127.0.0.1", 9292)) To make a new instance of something, call:
 * luajava.newInstance(Object.class, Arguments...) To access a static variable
 * in an object, use a period instead of a colon. So SendMethod.UDP, not
 * SendMethod:UDP
 * 
 * Remember: It's a lua console, so if you wanna call an object's function, it's
 * called like object:function() (not object.function())
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 * @see {@link Functions}
 * @see java-keyords.css for the stylesheet
 */
public class Console extends UIComponent {
    /*
     * TODO: Add globals like "luajava.newInstance" to autocomplete
     */
    /** The actual area we type in */
    private ConsoleArea consoleArea;
    /** Custom outputstream */
    private ConsoleOutput out;
    /** The cursor used by the console */
    private static final String cursor = "> ";
    /** The title that shows when starting up the console */
    private static final String title = "Lua Console";
    /** The line we're currently typing on (Used to figure out which part of the
        text is the command) */
    private int currentLine = 0;
    /** All objects found with reflection that use the {@link AvailableInLua} */
    private List<Object> functionClasses;
    /** ScriptEngine for handling scripts in lua */
    private ScriptEngine scriptEngine;

    /**
     * The constructor of the {@link Console}. 
     * After that it looks for all classes for auto complete and sets up the {@link ConsoleArea}
     * And last of all, it starts a thread for reading out commands.
     */
    public Console(String name) {
        super(name, "console.fxml");
        // Use reflection to obtain all classes annotated with {@link AvailableInLua}
        functionClasses = LuaUtils.getAllAvailableInLua();
        // Create an outputstream that use the {@link Console} to write in the {@link ConsoleArea}
        out = new ConsoleOutput(this);

        // Create TextArea using the classes and functions found using reflection
        consoleArea = new ConsoleArea();
        consoleArea.setupConsoleArea(this, getClasses(), getFunctions());
        consoleArea.setWrapText(true);
//        consoleArea.prefWidthProperty().bind(widthProperty());
//        consoleArea.prefHeightProperty().bind(heightProperty());
        this.add(consoleArea);

        // Make sure keypresses like tab and enter are handled
        addKeyListener();
        
        // Sets up the script engine
        setupScriptEngine();
    }

    /**
     * Collects all Class names and puts them in an ArrayList<String>
     * 
     * @return an ArrayList<String> containing every class in the
     *         functionClasses variable
     */
    private List<String> getClasses() {
        if(functionClasses == null)
            return null;
        // Create empty arraylist
        List<String> strings = functionClasses.stream().map(o -> getSimpleName(o)).collect(Collectors.toList());
        // Add String to the list
        strings.add("String");
        return strings;
    }

    /**
     * Collects all Function names and puts them in an ArrayList<String>
     * 
     * @return an ArrayList<String> containing every Function in the
     *         functionClasses variable
     */
    private List<String> getFunctions() {
        if(functionClasses == null)
            return null;
        // Turn into stream
        ArrayList<String> strings = (ArrayList<String>) functionClasses.stream()
                // Get all declared methods as Method[]
                .map(o -> getClass(o).getDeclaredMethods())
                // Turn Method[] into multiple streams
                .map(me -> Arrays.stream(me)
                        // Retrieve names from methods
                        .map(m -> m.getName())
                        // Collect into a list of List<List<String>>
                        .collect(Collectors.toList()))
                // Turn List<List<String>> into a stream
                .flatMap(l -> l.stream())
                // Collect everything back into a List<String>
                .collect(Collectors.toList());
        return strings;
    }

    /**
     * Makes sure code gets executed when enter or tab is pressed. <br/>
     * If enter is pressed, the command will get executed <br/>
     * If alt-enter is pressed, a new line will be printed <br/>
     * If tab is pressed, the word you're currently typing will autocomplete.
     */
    private void addKeyListener() {
        consoleArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            // Save the last line, since it's used often
            int lastLine = consoleArea.getText().length();
            // When enter is pressed
            if (event.getCode() == KeyCode.ENTER) {
                // Check whether it's alt+enter. (Only println in this case)
                if (event.isAltDown()) {
                    println("");
                } 
                // Else, retrieve the current command and execute it
                else {
                    String currentCommand = consoleArea.getText(currentLine, lastLine);
                    executeCommand(currentCommand);
                }
            // When Tab is pressed
            } else if (event.getCode() == KeyCode.TAB) {
                // Where the cursor is located (caret)
                int caretPos = consoleArea.getCaretPosition();
                // The command we're currently auto completing is a substring of the currentline till our cursor
                String command = consoleArea.getText(currentLine, caretPos);
                // Handle the tab using this unfinished command
                String result = handleTab(command);
                // If the handleTab function returns anything useful
                if (result != null)
                    // Use it
                    consoleArea.replaceText(caretPos, caretPos, result);
            }
        });
    }

    /**
     * Autocompletes the given command and returns the part that's missing
     * 
     * @param command
     *            The command that needs to be completed, for example
     *            "Functions:ge" or "Func"
     * @return The part of the command that's missing, for example
     *         "tFunctions()" or "tions"
     */
    private String handleTab(String command) {
        // Split whatever was before it
        String[] split = command.split("\\(|\\)|(\\r?\\n)|\\s+");

        // If it's a function, the split has to be at least a size of 2
        if (split.length == 0)
            return null;

        // Split the last record in split.
        String[] splitObjectsAndFunctions = split[split.length - 1].split(":");
        if (splitObjectsAndFunctions.length == 1) {
            // Autocomplete Class
            String object = splitObjectsAndFunctions[0];
            for (Object o : functionClasses) {
                String simpleName = getSimpleName(o);
                if (simpleName.startsWith(object) && !simpleName.equals(object))
                    return simpleName.substring(object.length());
            }
        } else {
            // Autocomplete Function
            // Split into Object and function (prefix)
            String object = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 2];
            String prefix = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 1];

            // Turn into stream
            Method method = functionClasses.stream()
            // Retrieve the Class this function belongs to
            .filter(o -> getSimpleName(o).equals(object))
            .map(o -> getClass(o).getDeclaredMethods())
            // Turn Method[] into multiple streams
            .map(me -> Arrays.stream(me)
                    // Collect into a list of List<List<String>>
                    .collect(Collectors.toList()))
            // Turn these lists into one stream
            .flatMap(l -> l.stream())
            // Return the function that starts with the prefix
            .filter(m -> m.getName().startsWith(prefix) && !m.getName().equals(prefix))
            .findAny()
            .get();
            
            // If we have a method
            if(method != null)
                // Return the part that's missing from the prefix
                return method.getName().substring(prefix.length()) + "("
                + (method.getParameterCount() == 0 ? ")" : "");
        }
        return null;
    }

    /**
     * Appends the given String to the consoleArea Don't use this function for
     * debugging
     * 
     * @param s
     *            String that you want to print
     */
    public void print(final String s) {
        Platform.runLater(() -> {
            int i = consoleArea.getLength();
            consoleArea.replaceText(i, i, s);
        });
    }

    /**
     * Same as {@link #print(String)}, only with an added '\n'
     * 
     * @param s
     *            String that you want to print.
     */
    public void println(String s) {
        print(s + "\n");
    }

    /**
     * Prints the cursor and sets the currentLine
     */
    public void printCursor() {
        Platform.runLater(() -> {
            int i = consoleArea.getLength();
            consoleArea.replaceText(i, i, '\n' + cursor);
            currentLine = consoleArea.getText().length();
        });
    }

    /**
     * @return the currentLine (Where the current command starts)
     */
    public int getCurrentLine() {
        return currentLine;
    }

    /**
     * @param o
     *            The object we need the simple name of
     * @return The simple name of the object. If an object has been turned into
     *         a {@link Class}, it won't return Class as simpleName
     */
    private String getSimpleName(Object o) {
        return o instanceof Class ? ((Class<?>) o).getSimpleName() : o.getClass().getSimpleName();
    }

    /**
     * Function used to get the {@link Class} of a certain object properly.
     * Motivation: The classes retrieved by LuaUtils are a mix of Class
     * instances and normal instances. When you call getClass() on each of these
     * objects, you won't get what you want when when it's called on something
     * that's a Class already. For example: You'd get
     * Chicken.getClass().getClass() which would return `Class` rather than
     * `Chicken`.
     * 
     * @param o
     *            The object to (maybe) call `getClass()` on
     * @return The valid {@link Class} of o.
     */
    @SuppressWarnings("rawtypes")
    private Class getClass(Object o) {
        return o instanceof Class ? (Class) o : o.getClass();
    }

    /**
     * Sets up the {@link ScriptEngine}, making the classes annotated with {@link AvailableInLua} available as well.
     */
    private void setupScriptEngine() {
        try {
            // Initialize ScriptEngine
            ScriptEngineManager sem = new ScriptEngineManager();
            scriptEngine = sem.getEngineByName("luaj");
            scriptEngine.getContext().setWriter(new OutputStreamWriter(out));

            // Add every @AvailableInLua class to the luaj
            if(functionClasses != null)
                for (Object o : functionClasses) 
                    scriptEngine.put(getSimpleName(o), o);
            
            // Add a useful sleep script
            scriptEngine.eval("local clock = os.clock function sleep(n) local t0 = clock() * 1000 while clock() * 1000 - t0 <= n do end end");

            // Important piece of code that fixed all bugs. Do not decode to
            // check its contents. (If this gives errors, see:
            // http://www.digizol.com/2008/09/eclipse-access-restriction-on-library.html)
            scriptEngine.eval(new String(Base64.decode(
                    "bG9jYWwgY293ID0gewpbWyAKICBcICAgICAgICAgICAsfi4KICAgIFwgICAgICwtJ19fIGAtLAogICAgICAgXCAgeywtJyAgYC4gfSAgICAgICAgICAgICAgLCcpCiAgICAgICAgICAsKCBhICkgICBgLS5fXyAgICAgICAgICwnLCcpfiwKICAgICAgICAgPD0uKSAoICAgICAgICAgYC0uX18sPT0nICcgJyAnfQogICAgICAgICAgICggICApICAgICAgICAgICAgICAgICAgICAgIC8pCiAgICAgICAgICAgIGAtJ1wgICAgLCAgICAgICAgICAgICAgICAgICAgKQoJICAgICAgIHwgIFwgICAgICAgICBgfi4gICAgICAgIC8KICAgICAgICAgICAgICAgXCAgICBgLl8gICAgICAgIFwgICAgICAgLwogICAgICAgICAgICAgICAgIFwgICAgICBgLl9fX19fLCcgICAgLCcKICAgICAgICAgICAgICAgICAgYC0uICAgICAgICAgICAgICwnCiAgICAgICAgICAgICAgICAgICAgIGAtLl8gICAgIF8sLScKICAgICAgICAgICAgICAgICAgICAgICAgIDc3amonCiAgICAgICAgICAgICAgICAgICAgICAgIC8vX3x8CiAgICAgICAgICAgICAgICAgICAgIF9fLy8tLScvYAoJICAgICAgICAgICAgLC0tJy9gICAnCl1dCn0KZnVuY3Rpb24gY2hpY2tlbnNheSh0ZXh0KQpsID0gdGV4dDpsZW4oKQphID0gbCAvIDEwCmZvciBpPTAsYSBkbwoJaW8ud3JpdGUoIlsiIC4uIHRleHQ6c3ViKGkqMTArMSwgKChpKzEpKjEwID4gbCkgYW5kIGwgb3IgKGkrMSkqMTAgKSAuLiAgIl1cbiIpCmVuZAoJcHJpbnQoY293WzFdKQplbmQK")));

            println(title);
            printCursor();
            
            // Set the line from where we need to start reading commands.
            currentLine = consoleArea.getText().length();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Executes the given command using the lua {@link ScriptEngine}
     * @param command The command to be executed.
     */
    private void executeCommand(String command){
        try {
            println("");
            consoleArea.addCommand(command);
            // Leave console if exit
            if (command.equals("exit"))
                System.exit(0);

            scriptEngine.eval(command);

            // Gotta catch it while we're still in the while loop.
        } catch (ScriptException | LuaError exc) {
            println(exc.getClass().getSimpleName() + " in line: " + command);
        }

        // TODO: Block input until here?
        printCursor();
        currentLine = consoleArea.getText().length();
    }
}