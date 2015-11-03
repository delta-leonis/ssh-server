package ui.lua.console;

import org.fxmisc.richtext.CodeArea;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static javafx.scene.input.KeyCombination.*;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;


/**
 * Class that extends the RTF {@link CodeArea}
 * Used to block certain inputs.
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class ConsoleArea extends ColoredCodeArea{
    /** A list containing all previous commands */
	private List<String> recentCommands;
	/** selecting = true when the user is selecting commands using the up and down keys */
	private boolean selecting = false;
	/** Used to iterate through the recentCommands */
	private ListIterator<String> iterator;
	/** Mostly used to retrieve at what line we're currently allowed to type */
	private Console console;
	/** The path to the stylesheet */
	private static final String styleSheet = "/css/java-keywords.css";
	
	/**
	 * Overriding replaceText so that stuff can't be written in any previous lines
	 * @see {@link CodeArea#replaceText(int, int, String)}
	 */
	@Override
    public void replaceText(int start, int end, String text) {
	    // Save the line we're currently writing on for further use
		int length = console.getCurrentLine();
		
		// Only replace text on valid positions (Any position below length is invalid)
		super.replaceText( start < length ? length : start,
		                   end < length ? end : end,
		                   text);
    }

	/**
	 * Overriding replaceSelection so that stuff can't be written in any previous lines
	 * @see {@link CodeArea#replaceSelection(String)}
	 */
	@Override
    public void replaceSelection(String text) {
		//Check whether it's a valid position
		if(console.getCurrentLine() >= getCaretPosition())
			selectRange(getLength(), getLength());

		super.replaceSelection(text);
    }
	
	/**
	 * Constructor of the ConsoleArea.
	 * Disabled ctrl+Z and changes the behaviour of Backspace so that it can't be used to wipe out the cursor.
	 * @param cursor The cursor used by this console (Usually something like "> " ) 
	 */
	public void setupConsoleArea(Console console, List<String> objectHighlights, List<String> functionHighlights){
	    super.setupColoredCodeArea(styleSheet, objectHighlights, functionHighlights);
        this.console = console;
        recentCommands = new ArrayList<String>();
        // TODO: Disable drag text
        // Custom key events
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(BACK_SPACE)).act(event -> backspace()).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(Z, CONTROL_DOWN)).act(event -> {}).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(TAB)).act(event -> {}).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(ENTER)).act(event -> {}).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(UP)).act(event -> up()).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(DOWN)).act(event -> down()).create());
	}
	
	/**
	 * Function that gets called when the up arrow is pressed
	 * Goes to the previous command in the array
	 */
	private void up(){
	    // If there are any previous commands
		if(!recentCommands.isEmpty()){
		    // If we're not scrolling through commands yet
			if(!selecting){
			    // Start selecting
				selecting = true;
				// Make a new iterator starting at the last command
				iterator = recentCommands.listIterator(recentCommands.size());
			}
			// Scroll through list, make sure we don't go too far
			if(!iterator.hasPrevious())
				iterator = recentCommands.listIterator(recentCommands.size());
			// Display the command
			replaceText(console.getCurrentLine(), getLength(), iterator.previous());
		}
	}
	
	/**
	 * Function that gets called when the down arrow is pressed
	 * Goes to the next command in the array
	 */
	private void down(){
	    // If we're busy scrolling through commands
		if(selecting){
		    // Scroll through the commands
			if(!iterator.hasNext())
				iterator = recentCommands.listIterator(0);
			// Display the command
			replaceText(console.getCurrentLine(), getLength(), iterator.next());
		}
	}
	
	/**
	 * Call this after every function. Saves the command in a list which can be accessed by using the up and down keys.
	 * @param command The commands that'll be saved
	 */
	public void addCommand(String command){
		// Move existing commands up
		if(recentCommands.contains(command))
			recentCommands.remove(command);
		
		recentCommands.add(command);
		// We're not selecting anymore
		selecting = false;
	}
	
	/**
	 * Custom backspace. Makes sure you can't use backspace in invalid positions.
	 */
	private void backspace(){
		if(isValid()){
			replaceText(getAnchor()-1, getAnchor(), "");
		}
	}
	
	/**
	 * Checks whether the current position is a valid place to use backspace.
	 */
	private boolean isValid(){
		if(console.getCurrentLine() >= getCaretPosition())
			return false;
		
		return true;
	}
}
