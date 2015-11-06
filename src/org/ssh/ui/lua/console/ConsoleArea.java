package org.ssh.ui.lua.console;

import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.Z;

import java.util.List;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;

import javafx.scene.input.KeyCombination;

/**
 * Class that extends the RTF {@link CodeArea} Used to block certain inputs.
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *        
 */
public class ConsoleArea extends ColoredCodeArea {
    
    /** The path to the stylesheet */
    private static final String styleSheet = "/css/java-keywords.css";
    /** The currentline of the console. Anything before this line can't be edited */
    private int                 currentLine;
                                
    /**
     * Constructor of the ConsoleArea. Disabled ctrl+Z and changes the behaviour of Backspace so
     * that it can't be used to wipe out the cursor.
     * 
     * @param cursor
     *            The cursor used by this console (Usually something like "> " )
     */
    public ConsoleArea(final List<String> objectHighlights, final List<String> functionHighlights) {
        super.setupColoredCodeArea(ConsoleArea.styleSheet, objectHighlights, functionHighlights);
        // TODO: Disable drag text
        // Custom key events
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(BACK_SPACE)).act(event -> this.backspace()).create());
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(Z, KeyCombination.CONTROL_DOWN)).act(event -> {
                }).create());
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(TAB)).act(event -> {
                }).create());
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(ENTER)).act(event -> {
                }).create());
                
    }
    
    /**
     * Custom backspace. Makes sure you can't use backspace in invalid positions.
     */
    private void backspace() {
        if (this.isValid()) {
            this.replaceText(this.getAnchor() - 1, this.getAnchor(), "");
        }
    }
    
    /**
     * Checks whether the current position is a valid place to use backspace.
     */
    private boolean isValid() {
        if (this.currentLine >= this.getCaretPosition()) return false;
        
        return true;
    }
    
    /**
     * Overriding replaceSelection so that stuff can't be written in any previous lines
     * 
     * @see {@link CodeArea#replaceSelection(String)}
     */
    @Override
    public void replaceSelection(final String text) {
        // Check whether it's a valid position
        if (this.currentLine >= this.getCaretPosition()) this.selectRange(this.getLength(), this.getLength());
        
        super.replaceSelection(text);
    }
    
    /**
     * Overriding replaceText so that stuff can't be written in any previous lines
     * 
     * @see {@link CodeArea#replaceText(int, int, String)}
     */
    @Override
    public void replaceText(final int start, final int end, final String text) {
        // Only replace text on valid positions (Any position below length is invalid)
        super.replaceText(start < this.currentLine ? this.currentLine : start,
                end < this.currentLine ? end : end,
                text);
    }
    
    /**
     * Sets the currentline for this class. Anything before this line can't be edited.
     * 
     * @param currentLine
     */
    public void setCurrentLine(final int currentLine) {
        this.currentLine = currentLine;
    }
}
