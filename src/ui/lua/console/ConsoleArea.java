package ui.lua.console;

import org.fxmisc.richtext.CodeArea;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import java.util.ArrayList;
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
	// The cursor used by the console, usually "> "
	private String cursor;
	private ArrayList<String> recentCommands;
	private boolean selecting = false;
	private ListIterator<String> iterator;
	private Console console;
	private static final String styleSheet = "/css/java-keywords.css";
	
	/**
	 * Overriding replaceText so that stuff can't be written in any previous lines
	 * @see {@link CodeArea#replaceText(int, int, String)}
	 */
	@Override
    public void replaceText(int start, int end, String text) {
		int s = start;
		int e = end;
		int length = console.getCurrentLine()+cursor.length();
		if(e < length)
			e = length;

		if(s < length)
			s = length;
		
		super.replaceText(s, e, text);
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
	public ConsoleArea(String cursor, Console console, ArrayList<String> objectHighlights, ArrayList<String> functionHighlights){
		super(styleSheet, objectHighlights, functionHighlights);

		this.cursor = cursor;
		this.console = console;
		recentCommands = new ArrayList<String>();
		// TODO: Disable drag text
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
		if(!recentCommands.isEmpty()){
			if(!selecting){
				selecting = true;
				iterator = recentCommands.listIterator(recentCommands.size());
			}
			if(!iterator.hasPrevious())
				iterator = recentCommands.listIterator(recentCommands.size());
			
			replaceText(console.getCurrentLine() + cursor.length(), getLength(), iterator.previous());

		}
	}
	
	/**
	 * Function that gets called when the down arrow is pressed
	 * Goes to the next command in the array
	 */
	private void down(){
		if(selecting){
			if(!iterator.hasNext())
				iterator = recentCommands.listIterator(0);

			replaceText(console.getCurrentLine() + cursor.length(), getLength(), iterator.next());

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
