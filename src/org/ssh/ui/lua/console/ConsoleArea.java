package org.ssh.ui.lua.console;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.ssh.util.LuaUtils;


/**
 * Class that extends the RTF {@link CodeArea} Used to block certain inputs.
 *
 * @author Thomas Hakkers
 *
 */
public class ConsoleArea extends ColoredCodeArea {

    /** The currentline of the console. Anything before this line can't be edited */
    private int                 currentLine;

    /**
     * Constructor of the ConsoleArea. Disabled ctrl+Z and changes the behaviour of Backspace so
     * that it can't be used to wipe out the cursor.
     *
     */
    public ConsoleArea() {
        super.setupColoredCodeArea(LuaUtils.getLuaClasses(), LuaUtils.getLuaFunctions());
        // On Backspace, use a custom handler
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.BACK_SPACE)).act(event -> this.backspace()).create());
        // On ctrl + Z, do nothing to avoid bugs
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.Z, KeyCombination.CONTROL_DOWN))
                        .act(care -> {
                            // ignore when control+Z is pressed down
                            return;
                        }).create());
        // Keycombination Control + shift + C for copy
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN))
                        .act(event -> this.copy()).create());
        // Keycombination Control + shift + V for paste
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.V, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN))
                        .act(event -> this.paste()).create());
        // On Home, go to the first line
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.HOME)).act(event -> this.selectRange(this.currentLine, this.currentLine)).create());
        // Keycombination ENTER + ALT for printlns
        EventHandlerHelper.install(this.onKeyPressedProperty(),
                EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.ENTER, KeyCombination.ALT_DOWN))
                        .act(event ->
                                Platform.runLater(() ->
                                    this.insertText(this.getCaretPosition(), "\n"))
                                ).create());
    }

    /**
     * Custom backspace. Makes sure you can't use backspace in invalid positions.
     */
    private void backspace() {
        if(!"".equals(this.getSelectedText())){
            this.replaceText(this.getSelection().getStart(), this.getSelection().getEnd(), "");
        }
        else if(this.isValid()) {
            this.replaceText(this.getAnchor() - 1, this.getAnchor(), "");
        }
    }

    /**
     * Checks whether the current position is a valid place to use backspace.
     */
    private boolean isValid() {
        return this.currentLine < this.getCaretPosition();

    }

    /**
     * Overriding replaceSelection so that stuff can't be written in any previous lines
     *
     * @see {@link CodeArea#replaceSelection(String)}
     */
    @Override
    public void replaceSelection(final String text) {
        // Check whether it's a valid position
        if (this.currentLine > this.getCaretPosition())
            this.selectRange(this.getLength(), this.getLength());

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
     * @param currentLine the current line to set
     */
    public void setCurrentLine(final int currentLine) {
        this.currentLine = currentLine;
    }
}
