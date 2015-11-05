package org.ssh.ui.lua.console;

import org.fxmisc.richtext.CodeArea;

import static javafx.scene.input.KeyCode.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import java.util.List;

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
	/** The path to the stylesheet */
	private static final String styleSheet = "/css/java-keywords.css";
	/** The currentline of the console. Anything before this line can't be edited */
	private int currentLine;
	
	/**
	 * Overriding replaceText so that stuff can't be written in any previous lines
	 * @see {@link CodeArea#replaceText(int, int, String)}
	 */
	@Override
    public void replaceText(int start, int end, String text) {
		// Only replace text on valid positions (Any position below length is invalid)
		super.replaceText( start < currentLine ? currentLine : start,
		                   end < currentLine ? end : end,
		                   text);
    }

	/**
	 * Overriding replaceSelection so that stuff can't be written in any previous lines
	 * @see {@link CodeArea#replaceSelection(String)}
	 */
	@Override
    public void replaceSelection(String text) {
		//Check whether it's a valid position
		if(currentLine >= getCaretPosition())
			selectRange(getLength(), getLength());

		super.replaceSelection(text);
    }
	
	/**
	 * Constructor of the ConsoleArea.
	 * Disabled ctrl+Z and changes the behaviour of Backspace so that it can't be used to wipe out the cursor.
	 * @param cursor The cursor used by this console (Usually something like "> " ) 
	 */
	public ConsoleArea(List<String> objectHighlights, List<String> functionHighlights){
	    super.setupColoredCodeArea(styleSheet, objectHighlights, functionHighlights);
        // TODO: Disable drag text
        // Custom key events
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(BACK_SPACE)).act(event -> backspace()).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(Z, CONTROL_DOWN)).act(event -> {}).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(TAB)).act(event -> {}).create());
        EventHandlerHelper.install(onKeyPressedProperty(), EventHandlerHelper.on(keyPressed(ENTER)).act(event -> {}).create());

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
	 * Sets the currentline for this class.
	 * Anything before this line can't be edited.
	 * @param currentLine
	 */
	public void setCurrentLine(int currentLine){
	    this.currentLine = currentLine;
	}

	/**
	 * Checks whether the current position is a valid place to use backspace.
	 */
	private boolean isValid(){
		if(currentLine >= getCaretPosition())
			return false;
		
		return true;
	}
}
