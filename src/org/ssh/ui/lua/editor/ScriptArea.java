package org.ssh.ui.lua.editor;

import javafx.scene.input.KeyCode;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.ssh.managers.manager.Models;
import org.ssh.models.Model;
import org.ssh.models.Settings;
import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.ui.lua.console.ColoredCodeArea;
import org.ssh.util.Logger;
import org.ssh.util.LuaUtils;

import javax.script.ScriptEngine;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Thomas on 7-12-2015.
 */
public class ScriptArea extends ColoredCodeArea {

    // A logger for errorhandling
    private static final Logger LOG = Logger.getLogger();
    /**
     * All objects found with reflection that use the {@link AvailableInLua}
     */
    private final List<Object>   functionClasses;

    private Globals globals;

    public ScriptArea(){
        // Use reflection to obtain all classes annotated with AvailableInLua
        this.functionClasses = LuaUtils.getAllAvailableInLua();

        super.setupColoredCodeArea(LuaUtils.getLuaClasses(), LuaUtils.getLuaFunctions());
        // Keypress TAB for autocomplete
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> this.handleTab()).create());
    }

    /**
     * Runs a lua script
     *
     * @param command
     *            The script to run
     */
    public void runScript(final String command) {
        this.globals.load(command).call();
    }

    /**
     * Autocompletes the given command and returns the part that's missing
     *
     * @param command The command that needs to be completed, for example "Functions:ge" or "Func"
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
                return "";
            }
            else {
                // Map the available classes into Strings
                List<String> options = functionClasses.stream().map(LuaUtils::getSimpleName)
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
                        .map(Method::getName).collect(Collectors.toList()), prefix) + "(";
            }
        }
        return null;
    }


    /**
     * Autocompletes the given prefix based on a list of possible Strings (options)
     *
     * @param options The options the prefix can be
     * @param prefix  The prefix that needs to be completed, for example "Functions:ge" or "Func"
     * @return The part of the prefix that's missing, for example "tFunctions()" or "tions"
     */
    private String autocompleteBasedOnList(List<String> options, String prefix) {
        // Turn the options into a stream
        Optional<String> filteredString = options.stream()
                // Find the option that starts with `prefix` and isn't equal to `prefix`
                .filter(m -> m.startsWith(prefix) && !m.equals(prefix)).findAny();
        return filteredString.isPresent() ? filteredString.get().substring(prefix.length()) : null;
    }


    /**
     * Function that gets called when tab is pressed Autocompletes the current command
     */
    private void handleTab() {
        // Where the cursor is located (caret)
        final int caretPos = this.getCaretPosition();
        // The command we're currently auto completing is a substring of the
        // currentline till our
        // cursor
        final String command = this.getText(0, caretPos);

        // Handle the tab using this unfinished command
        final String result = this.autocomplete(command);
        // If the handleTab function returns anything useful
        if (result != null)
            // Use it
            this.replaceText(caretPos, caretPos, result);
    }


    /**
     * Sets up the {@link ScriptEngine}, making the classes annotated with {@link AvailableInLua}
     * available as well.
     */
    private void setupScriptEngine() {
        try {
            // Initialize Globals
            this.globals = JsePlatform.standardGlobals();

            // Add every @AvailableInLua class to the luaj
            if (this.functionClasses != null)
                for (final Object o : this.functionClasses)
                    globals.set(LuaUtils.getSimpleName(o), CoerceJavaToLua.coerce(o));

            initialize();
        }
        catch (final Exception exception) {
            ScriptArea.LOG.exception(exception);
        }
    }


    /**
     * Looks for all lua files in lua.d and calls them for initiliasation.
     * These files contain useful shorthands for functions that are used often.
     */
    private void initialize() {
        // Find init script based on Settings and execute it
        Optional<Model> oSettings = Models.get("settings");
        // If settings exists
        if (oSettings.isPresent()) {
            Settings settings = (Settings) oSettings.get();
            // Check whether it has a variable pointing to the lua folder
            if (settings.getLuaInitFolder() != null) {
                try {
                    // For each file in folder
                    Files.walk(Paths.get(settings.getLuaInitFolder()))
                            // Check whether it's a valid file
                            .filter(Files::isRegularFile)
                            .filter(file -> file.toFile().getAbsolutePath().endsWith(".lua"))
                            // Call the lua files one by one
                            .forEach(lua -> {
                                String path = lua.toAbsolutePath().toString();
                            });
                }
                catch (IOException exception) {
                    ScriptArea.LOG.exception(exception);
                }
            }
        }
    }
}
