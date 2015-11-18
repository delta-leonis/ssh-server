package org.ssh.ui.lua.console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.ssh.managers.manager.Services;
import org.ssh.services.PipelinePacket;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;
import org.ssh.util.LuaUtils;

import com.google.common.util.concurrent.ListenableFuture;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

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
 * @author Thomas Hakkers
 * @see java-keyords.css for the stylesheet
 */
public class Console extends UIComponent {
    
    // A logger for errorhandling
    private static final Logger LOG = Logger.getLogger();
    
    /** The cursor used by the console */
    private static final String              CURSOR      = "> ";
    /** The title that shows when starting up the console */
    private static final String              TITLE       = "Lua Console";
    /*
     * TODO: Add globals like "luajava.newInstance" to autocomplete
     */
    /** The actual area we type in */
    private final ConsoleArea                consoleArea;
    /** Custom outputstream */
    private final ConsoleOutput              out;
    /**
     * The line we're currently typing on (Used to figure out which part of the text is the command)
     */
    private int                              currentLine = 0;
    /** All objects found with reflection that use the {@link AvailableInLua} */
    private final List<Object>               functionClasses;
    /** Lua globals used by this {@link Console} */
    private Globals                          globals;
    /** Magic debuglibrary that is used to interrupt functions */
    private CustomDebugLib                   customDebug;
    /* Variables for handling command history */
    /** A list containing all previous commands */
    private final List<String>               recentCommands;
    /**
     * selecting = true when the user is selecting commands using the up and down keys
     */
    private boolean                          selecting   = false;
    /** Used to iterate through the recentCommands */
    private ListIterator<String>             iterator;
    /** Get created whenever a command is executed. Can be cancelled by calling close() */
    private ListenableFuture<PipelinePacket> currentFuture;
    
    /**
     * The constructor of the {@link Console}. After that it looks for all classes for auto complete
     * and sets up the {@link ConsoleArea} And last of all, it starts a thread for reading out
     * commands.
     */
    public Console(final String name) {
        super(name, "console.fxml");
        // Use reflection to obtain all classes annotated with AvailableInLua
        this.functionClasses = LuaUtils.getAllAvailableInLua();
        // Create an outputstream that use the Console to write in the
        // ConsoleArea
        this.out = new ConsoleOutput(this);
        // Initialize the command history
        this.recentCommands = new ArrayList<String>();
        
        // Create TextArea using the classes and functions found using
        // reflection
        this.consoleArea = new ConsoleArea(LuaUtils.getLuaClasses(), LuaUtils.getLuaFunctions());
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
        // Keypress UP for command history
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.UP)).act(event -> this.up()).create());
        // Keypress DOWN for command history
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.DOWN)).act(event -> this.down()).create());
        // Keypress ENTER for running command
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.ENTER)).act(event -> this.handleEnter())
                        .create());
        // Keycombination ENTER + ALT for printlns
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.ENTER, KeyCombination.ALT_DOWN))
                        .act(event -> this.println("")).create());
        // Keypress TAB for autocomplete
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> this.handleTab()).create());
        // Keycombination Control + C for cancel command
        EventHandlerHelper.install(this.consoleArea.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.C, KeyCombination.CONTROL_DOWN))
                        .act(event -> this.cancel()).create());
                        
    }
    
    /** ************************ */
    /* AUTOCOMPLETE FUNCTIONS */
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
            final String stringObject = splitObjectsAndFunctions[0];
            // If the Class is already autocompleted
            Object object = LuaUtils.getObjectBasedOnString(stringObject);
            if (object != null) {
                this.addCommand(command);
                println("");
                // Show all the functions is has
                LuaUtils.getPrettyFunctions(LuaUtils.getObjectBasedOnString(separator[separator.length - 1]))
                        .forEach(method -> println(method));
                printCursor();
                return "";
            }
            else {
                // Map the available classes into Strings
                List<String> options = functionClasses.stream().map(clazz -> LuaUtils.getSimpleName(clazz))
                        .collect(Collectors.toList());
                // Use those for autocompletion
                return autocompleteBasedOnList(options, stringObject);
            }
        }
        else {
            // Autocomplete Function
            // Split into Object and function (prefix)
            final String object = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 2];
            final String prefix = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 1];
            
            // Turn into stream
            final Object clazz = this.functionClasses.stream()
                    // Retrieve the Class this function belongs to
                    .filter(o -> LuaUtils.getSimpleName(o).equals(object)).findAny().get();
            if (clazz != null) {
                return autocompleteBasedOnList(Arrays.asList(LuaUtils.getClass(clazz).getDeclaredMethods()).stream()
                        .map(method -> method.getName()).collect(Collectors.toList()), prefix) + "(";
            }
        }
        return null;
    }
    
    /**
     * Autocompletes the given prefix based on a list of possible Strings (options)
     * 
     * @param options
     *            The options the prefix can be
     * @param prefix
     *            The prefix that needs to be completed, for example "Functions:ge" or "Func"
     * @return The part of the prefix that's missing, for example "tFunctions()" or "tions"
     */
    private String autocompleteBasedOnList(List<String> options, String prefix) {
        // Turn the options into a stream
        Optional<String> filteredString = options.stream()
                // Find the option that starts with `prefix` and isn't equal to
                // `prefix`
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
        consoleArea.setDisable(true);
        currentFuture = Services.submitTask(this.getName(), () -> {
            try {
                this.println("");
                // Add command to the command history
                this.addCommand(command);
                // Execute the command
                this.globals.load(command).call();
            }
            catch (LuaError exception) {
                Console.LOG.exception(exception);
                this.println(exception.getClass().getSimpleName() + " in line: " + command);
            }
            
            this.printCursor();
            this.currentLine = this.consoleArea.getText().length();
            this.consoleArea.setCurrentLine(this.currentLine);
            consoleArea.setDisable(false);
            return null;
        });
    }
    
    /**
     * Cancels the {@link ListenableFuture} obtained from {@link #executeCommand(String)}
     */
    public void cancel() {
        if (currentFuture != null) 
            currentFuture.cancel(true);
        
        customDebug.interrupt();
        requestFocus();
    }
    
    /**
     * Requests focus for the underlying {@link ConsoleArea}
     */
    @Override
    public void requestFocus() {
        Platform.runLater(() -> consoleArea.requestFocus());
    }
    
    /**
     * Eventhandler when enter is pressed Either executes the current command, or creates a new line
     * when alt is held at the same time
     * 
     * @param event
     *            The event generated by a keyevent
     */
    private void handleEnter() {
        final String currentCommand = this.consoleArea.getText(this.currentLine, this.consoleArea.getText().length());
        this.executeCommand(currentCommand);
    }
    
    /**
     * Function that gets called when tab is pressed Autocompletes the current command
     * 
     * @param event
     *            
     * @param event
     *            The KeyEvent generated by the event
     */
    private void handleTab() {
        // Where the cursor is located (caret)
        final int caretPos = this.consoleArea.getCaretPosition();
        // The command we're currently auto completing is a substring of the
        // currentline till our
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
        Platform.runLater(() -> this.consoleArea.insertText(this.consoleArea.getLength(), s));
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
            // Initialize Globals
            this.globals = JsePlatform.standardGlobals();
            // Initialize the CustomDebugLib
            customDebug = new CustomDebugLib();
            this.globals.load(customDebug);
            // Set outputstream so that it streams to the console
            this.globals.STDOUT = new PrintStream(this.out);
            
            // Add every @AvailableInLua class to the luaj
            if (this.functionClasses != null) 
                for (final Object o : this.functionClasses)
                    globals.set(LuaUtils.getSimpleName(o), CoerceJavaToLua.coerce(o));
            // Add a useful sleep script
            this.globals
                    .load("local clock = os.clock function sleep(n) local t0 = clock() * 1000 while clock() * 1000 - t0 <= n do end end")
                    .call();
            // Important piece of code that fixed all bugs. Do not decode to
            // check its contents.
            this.globals
                    .load(new String(Base64.getDecoder()
                            .decode("bG9jYWwgY293ID0gewpbWyAKICBcICAgICAgICAgICAsfi4KICAgIFwgICAgICwtJ19fIGAtLAogICAgICAgXCAgeywtJyAgYC4gfSAgICAgICAgICAgICAgLCcpCiAgICAgICAgICAsKCBhICkgICBgLS5fXyAgICAgICAgICwnLCcpfiwKICAgICAgICAgPD0uKSAoICAgICAgICAgYC0uX18sPT0nICcgJyAnfQogICAgICAgICAgICggICApICAgICAgICAgICAgICAgICAgICAgIC8pCiAgICAgICAgICAgIGAtJ1wgICAgLCAgICAgICAgICAgICAgICAgICAgKQoJICAgICAgIHwgIFwgICAgICAgICBgfi4gICAgICAgIC8KICAgICAgICAgICAgICAgXCAgICBgLl8gICAgICAgIFwgICAgICAgLwogICAgICAgICAgICAgICAgIFwgICAgICBgLl9fX19fLCcgICAgLCcKICAgICAgICAgICAgICAgICAgYC0uICAgICAgICAgICAgICwnCiAgICAgICAgICAgICAgICAgICAgIGAtLl8gICAgIF8sLScKICAgICAgICAgICAgICAgICAgICAgICAgIDc3amonCiAgICAgICAgICAgICAgICAgICAgICAgIC8vX3x8CiAgICAgICAgICAgICAgICAgICAgIF9fLy8tLScvYAoJICAgICAgICAgICAgLC0tJy9gICAnCl1dCn0KZnVuY3Rpb24gY2hpY2tlbnNheSh0ZXh0KQpsID0gdGV4dDpsZW4oKQphID0gbCAvIDEwCmZvciBpPTAsYSBkbwoJaW8ud3JpdGUoIlsiIC4uIHRleHQ6c3ViKGkqMTArMSwgKChpKzEpKjEwID4gbCkgYW5kIGwgb3IgKGkrMSkqMTAgKSAuLiAgIl1cbiIpCmVuZAoJcHJpbnQoY293WzFdKQplbmQK")))
                    .call();
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