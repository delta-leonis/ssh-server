package org.ssh.ui.lua.console;

import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.UP;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.luaj.vm2.LuaError;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;
import org.ssh.util.LuaUtils;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Simple LuaJava console. The {@link Console} has access to every class that uses the
 * {@link AvailableInLua} annotation It features the following:
 * <ul>
 * <li>Autocomplete on every class annotated with {@link AvailableInLua}</li>
 * <li>Function and Object highlights</li>
 * <li>Command history (Use up and down keys)</li>
 * </ul>
 *
 * Example Command stolen from {@link CommunicatorExample}: Communicator:register(SendMethod.UDP,
 * luajava.newInstance("org.ssh.senders.UDPSender" , "127.0.0.1", 9292)) To make a new instance of
 * something, call: luajava.newInstance(Object.class, Arguments...) To access a static variable in
 * an object, use a period instead of a colon. So SendMethod.UDP, not SendMethod:UDP
 *
 * Remember: It's a lua console, so if you want to call an object's function, it's called like
 * object:function() (not object.function())
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 * @see {@link Functions}
 * @see java-keyords.css for the stylesheet
 */
public class Console extends UIComponent {
    
    // A logger for errorhandling
    private static final Logger              LOG                 = Logger.getLogger();

    /** The cursor used by the console */
    private static final String  CURSOR      = "> ";
    /** The title that shows when starting up the console */
    private static final String  TITLE       = "Lua Console";
    /*
     * TODO: Add globals like "luajava.newInstance" to autocomplete
     */
    /** The actual area we type in */
    private final ConsoleArea    consoleArea;
    /** Custom outputstream */
    private final ConsoleOutput  out;
    /**
     * The line we're currently typing on (Used to figure out which part of the text is the command)
     */
    private int                  currentLine = 0;
    /** All objects found with reflection that use the {@link AvailableInLua} */
    private final List<Object>   functionClasses;
    /** ScriptEngine for handling scripts in lua */
    private ScriptEngine         scriptEngine;
    /* Variables for handling command history */
    /** A list containing all previous commands */
    private final List<String>   recentCommands;
    /** selecting = true when the user is selecting commands using the up and down keys */
    private boolean              selecting   = false;
    /** Used to iterate through the recentCommands */
    private ListIterator<String> iterator;
                                 
    /**
     * The constructor of the {@link Console}. After that it looks for all classes for auto complete
     * and sets up the {@link ConsoleArea} And last of all, it starts a thread for reading out
     * commands.
     */
    public Console(final String name) {
        super(name, "console.fxml");
        // Use reflection to obtain all classes annotated with AvailableInLua
        this.functionClasses = LuaUtils.getAllAvailableInLua();
        // Create an outputstream that use the Console to write in the ConsoleArea
        this.out = new ConsoleOutput(this);
        // Initialize the command history
        this.recentCommands = new ArrayList<String>();

        // Create TextArea using the classes and functions found using reflection
        this.consoleArea = new ConsoleArea(this.getClasses(), this.getFunctions());
        consoleArea.minHeightProperty().bind(this.heightProperty());
        consoleArea.maxHeightProperty().bind(this.heightProperty());
        consoleArea.minWidthProperty().bind(this.widthProperty());
        consoleArea.maxWidthProperty().bind(this.widthProperty());
        
        // Make the area resizable
        this.consoleArea.setWrapText(true);
        this.add(this.consoleArea);
        
        // Make sure keypresses like tab and enter are handled
        this.addKeyListeners();

        // Sets up the script engine
        this.setupScriptEngine();
    }
    
    /**
     * Call this after every function. Saves the command in a list which can be accessed by using
     * the up and down keys.
     * 
     * @param command
     *            The commands that'll be saved
     */
    private void addCommand(final String command) {
        // Move existing commands up
        if (this.recentCommands.contains(command))
            this.recentCommands.remove(command);
        
        this.recentCommands.add(command);
        // We're not selecting anymore
        this.selecting = false;
    }
    
    /**
     * Makes sure code gets executed when enter or tab is pressed. <br/>
     * If enter is pressed, the command will get executed <br/>
     * If alt-enter is pressed, a new line will be printed <br/>
     * If tab is pressed, the word you're currently typing will autocomplete. <br/>
     * If Up is pressed, the last command will show. Also used to scroll through commands <br/>
     * If Down is pressed, you can scroll through the commands.
     */
    private void addKeyListeners() {
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(UP)).act(event -> this.up()).create());
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(DOWN)).act(event -> this.down()).create());
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.ENTER)).act(event -> this.handleEnter(event))
                        .create());
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> this.handleTab())
                        .create());
    }
    
    
    /** ************************ */
    /*   AUTOCOMPLETE FUNCTIONS  */
    /** ************************ */
    
    /**
     * Autocompletes the given command and returns the part that's missing
     *
     * @param command
     *            The command that needs to be completed, for example "Functions:ge" or "Func"
     * @return The part of the command that's missing, for example "tFunctions()" or "tions"
     */
    private String autocomplete(final String command) {
        // Split whatever was before it
        final String[] separator = command.split("\\(|\\)|(\\r?\\n)|\\s+");
        
        // Split the last record in split.
        final String[] splitObjectsAndFunctions = separator[separator.length - 1].split(":");
        if (splitObjectsAndFunctions.length == 1) {
            // Autocomplete Class
            final String object = splitObjectsAndFunctions[0];
            // Map the available classes into Strings
            List<String> options = functionClasses.stream().map(clazz -> getSimpleName(clazz))
                    .collect(Collectors.toList());
            // Use those for autocompletion
            return autocompleteBasedOnList(options, object);
        }
        else {
            // Autocomplete Function
            // Split into Object and function (prefix)
            final String object = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 2];
            final String prefix = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 1];
            
            // Turn into stream
            final Object clazz = this.functionClasses.stream()
                    // Retrieve the Class this function belongs to
                    .filter(o -> this.getSimpleName(o).equals(object)).findAny().get();
            if(clazz != null){
                return autocompleteBasedOnList(Arrays.asList(getClass(clazz).getDeclaredMethods()).stream()
                        .map(method -> method.getName()).collect(Collectors.toList()), prefix);
            }
        }
        return null;
    }
    
    /**
     * Autocompletes the given prefix based on a list of possible Strings (options)
     * @param options The options the prefix can be
     * @param prefix The prefix that needs to be completed, for example "Functions:ge" or "Func"
     * @return The part of the prefix that's missing, for example "tFunctions()" or "tions"
     */
    private String autocompleteBasedOnList(List<String> options, String prefix){
        // Turn the options into a stream
        Optional<String> filteredString = options.stream()
                // Find the option that starts with `prefix` and isn't equal to `prefix`
                .filter(m -> m.startsWith(prefix) && !m.equals(prefix)).findAny();
        return filteredString.isPresent() ? filteredString.get().substring(prefix.length()) : null;
    }
    
    /**
     * Function that gets called when the down arrow is pressed Goes to the next command in the
     * array
     */
    private void down() {
        // If we're busy scrolling through commands
        if (this.selecting) {
            // Scroll through the commands
            if (!this.iterator.hasNext())
                this.iterator = this.recentCommands.listIterator(0);
            // Display the command
            this.consoleArea.replaceText(this.currentLine, this.consoleArea.getLength(), this.iterator.next());
        }
    }
    
    /**
     * Executes the given command using the lua {@link ScriptEngine}
     * 
     * @param command
     *            The command to be executed.
     */
    private void executeCommand(final String command) {
        try {
            this.println("");
            // Add command to the command history
            this.addCommand(command);
            // Execute the command
            this.scriptEngine.eval(command);
        }
        catch (ScriptException | LuaError exception) {
            LOG.exception(exception);
            this.println(exception.getClass().getSimpleName() + " in line: " + command);
        }
        
        // TODO: Block input until here?
        this.printCursor();
        this.currentLine = this.consoleArea.getText().length();
        this.consoleArea.setCurrentLine(this.currentLine);
    }
    
    /**
     * Function used to get the {@link Class} of a certain object properly. Motivation: The classes
     * retrieved by LuaUtils are a mix of Class instances and normal instances. When you call
     * getClass() on each of these objects, you won't get what you want when when it's called on
     * something that's a Class already. For example: You'd get Chicken.getClass().getClass() which
     * would return `Class` rather than `Chicken`.
     *
     * @param o
     *            The object to (maybe) call `getClass()` on
     * @return The valid {@link Class} of o.
     */
    @SuppressWarnings ("rawtypes")
    private Class getClass(final Object o) {
        return o instanceof Class ? (Class) o : o.getClass();
    }
    
    /**
     * Collects all Class names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every class in the functionClasses variable
     */
    private List<String> getClasses() {
        if (this.functionClasses == null) 
            return new ArrayList<String>();
        // Turn everything into a stream
        return this.functionClasses.stream().map(o ->
            // and get the simple name of each class
            this.getSimpleName(o)).collect(Collectors.toList());
    }
    
    /**
     * Collects all Function names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every Function in the functionClasses variable
     */
    private List<String> getFunctions() {
        if (this.functionClasses == null) 
            return new ArrayList<String>();
        // Turn into stream
        return this.functionClasses.stream()
                // Get all declared methods as Method[]
                .map(o -> this.getClass(o).getDeclaredMethods())
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
    }
    
    /**
     * @param o
     *            The object we need the simple name of
     * @return The simple name of the object. If an object has been turned into a {@link Class}, it
     *         won't return Class as simpleName
     */
    private String getSimpleName(final Object o) {
        return o instanceof Class ? ((Class<?>) o).getSimpleName() : o.getClass().getSimpleName();
    }
    
    /**
     * Eventhandler when enter is pressed Either executes the current command, or creates a new line
     * when alt is held at the same time
     * 
     * @param event
     *            The event generated by a keyevent
     */
    private void handleEnter(final KeyEvent event) {
        // Save the last line, since it's used often
        final int lastLine = this.consoleArea.getText().length();
        // When enter is pressed
        if (event.getCode() == KeyCode.ENTER) {
            // Check whether it's alt+enter. (Only println in this case)
            if (event.isAltDown()) {
                this.println("");
            }
            // Else, retrieve the current command and execute it
            else {
                final String currentCommand = this.consoleArea.getText(this.currentLine, lastLine);
                this.executeCommand(currentCommand);
            }
        }
    }
    
    /**
     * Function that gets called when tab is pressed Autocompletes the current command
     * 
     * @param event
     *            The KeyEvent generated by the event
     */
    private void handleTab() {
        // Where the cursor is located (caret)
        final int caretPos = this.consoleArea.getCaretPosition();
        // The command we're currently auto completing is a substring of the currentline till our
        // cursor
        final String command = this.consoleArea.getText(this.currentLine, caretPos);
        // Handle the tab using this unfinished command
        final String result = this.autocomplete(command);
        // If the handleTab function returns anything useful
        if (result != null)
            // Use it
            this.consoleArea.replaceText(caretPos, caretPos, result);
    }
    
    /**
     * Appends the given String to the consoleArea Don't use this function for debugging
     *
     * @param s
     *            String that you want to print
     */
    public void print(final String s) {
        Platform.runLater(() -> {
            final int i = this.consoleArea.getLength();
            this.consoleArea.replaceText(i, i, s);
        });
    }

    /**
     * Prints the cursor and sets the currentLine
     */
    public void printCursor() {
        Platform.runLater(() -> {
            final int i = this.consoleArea.getLength();
            this.consoleArea.replaceText(i, i, '\n' + Console.CURSOR);
            this.currentLine = this.consoleArea.getText().length();
            consoleArea.setCurrentLine(currentLine);
        });
    }
    
    /** *************************************** **/
    /// Functions that handle command history ///
    /** *************************************** **/
    
    /**
     * Same as {@link #print(String)}, only with an added '\n'
     *
     * @param s
     *            String that you want to print.
     */
    public void println(final String s) {
        this.print(s + "\n");
    }
    
    /**
     * Sets up the {@link ScriptEngine}, making the classes annotated with {@link AvailableInLua}
     * available as well.
     */
    private void setupScriptEngine() {
        try {
            // Initialize ScriptEngine
            final ScriptEngineManager sem = new ScriptEngineManager();
            this.scriptEngine = sem.getEngineByName("luaj");
            this.scriptEngine.getContext().setWriter(new OutputStreamWriter(this.out));
            
            // Add every @AvailableInLua class to the luaj
            if (this.functionClasses != null)
                for (final Object o : this.functionClasses)
                    this.scriptEngine.put(this.getSimpleName(o), o);
                
            // Add a useful sleep script
            this.scriptEngine.eval(
                    "local clock = os.clock function sleep(n) local t0 = clock() * 1000 while clock() * 1000 - t0 <= n do end end");
                    
            // Important piece of code that fixed all bugs. Do not decode to
            // check its contents.
            this.scriptEngine.eval(new String(Base64.getDecoder().decode(
                    "bG9jYWwgY293ID0gewpbWyAKICBcICAgICAgICAgICAsfi4KICAgIFwgICAgICwtJ19fIGAtLAogICAgICAgXCAgeywtJyAgYC4gfSAgICAgICAgICAgICAgLCcpCiAgICAgICAgICAsKCBhICkgICBgLS5fXyAgICAgICAgICwnLCcpfiwKICAgICAgICAgPD0uKSAoICAgICAgICAgYC0uX18sPT0nICcgJyAnfQogICAgICAgICAgICggICApICAgICAgICAgICAgICAgICAgICAgIC8pCiAgICAgICAgICAgIGAtJ1wgICAgLCAgICAgICAgICAgICAgICAgICAgKQoJICAgICAgIHwgIFwgICAgICAgICBgfi4gICAgICAgIC8KICAgICAgICAgICAgICAgXCAgICBgLl8gICAgICAgIFwgICAgICAgLwogICAgICAgICAgICAgICAgIFwgICAgICBgLl9fX19fLCcgICAgLCcKICAgICAgICAgICAgICAgICAgYC0uICAgICAgICAgICAgICwnCiAgICAgICAgICAgICAgICAgICAgIGAtLl8gICAgIF8sLScKICAgICAgICAgICAgICAgICAgICAgICAgIDc3amonCiAgICAgICAgICAgICAgICAgICAgICAgIC8vX3x8CiAgICAgICAgICAgICAgICAgICAgIF9fLy8tLScvYAoJICAgICAgICAgICAgLC0tJy9gICAnCl1dCn0KZnVuY3Rpb24gY2hpY2tlbnNheSh0ZXh0KQpsID0gdGV4dDpsZW4oKQphID0gbCAvIDEwCmZvciBpPTAsYSBkbwoJaW8ud3JpdGUoIlsiIC4uIHRleHQ6c3ViKGkqMTArMSwgKChpKzEpKjEwID4gbCkgYW5kIGwgb3IgKGkrMSkqMTAgKSAuLiAgIl1cbiIpCmVuZAoJcHJpbnQoY293WzFdKQplbmQK")));
                    
            this.println(Console.TITLE);
            this.printCursor();

            // Set the line from where we need to start reading commands.
            this.currentLine = this.consoleArea.getText().length();
            consoleArea.setCurrentLine(currentLine);
        }
        catch (final Exception exception) {
            LOG.exception(exception);
            this.println("Exception occured whilst setting up console");
        }
    }
    
    /**
     * Function that gets called when the up arrow is pressed Goes to the previous command in the
     * array
     */
    private void up() {
        // If there are any previous commands
        if (!this.recentCommands.isEmpty()) {
            // If we're not scrolling through commands yet
            if (!this.selecting) {
                // Start selecting
                this.selecting = true;
                // Make a new iterator starting at the last command
                this.iterator = this.recentCommands.listIterator(this.recentCommands.size());
            }
            // Scroll through list, make sure we don't go too far
            if (!this.iterator.hasPrevious())
                this.iterator = this.recentCommands.listIterator(this.recentCommands.size());
            // Display the command
            this.consoleArea.replaceText(this.currentLine, this.consoleArea.getLength(), this.iterator.previous());
        }
    }
}