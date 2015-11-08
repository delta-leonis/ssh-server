package org.ssh.ui.lua.console;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Class used to color code the given keywords. It colors the following symbols: ( ) { } [ ] "text"
 * // ; These colors can be edited in the java-keywords.css
 *
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com Most code originates from
 *         https://github.com/TomasMikula/RichTextFX
 */
public class ColoredCodeArea extends CodeArea {
    
    /** Lua keyword variables */
    private static final String[] KEYWORDS          = new String[] { "and", "break", "do", "else", "elseif", "end",
            "false", "finally", "float", "for", "function", "if", "in", "local", "nil", "not", "or", "repeat", "return",
            "then", "true", "until", "while" };
            
    /** Joins all keywords together into a pattern */
    private static final String   KEYWORD_PATTERN   = "\\b(" + String.join("|", ColoredCodeArea.KEYWORDS) + ")\\b";
                                                    
    /** Pattern for Parentheses: ( ) */
    private static final String   PAREN_PATTERN     = "\\(|\\)";
    /** Pattern for Braces: { } */
    private static final String   BRACE_PATTERN     = "\\{|\\}";
    /** Pattern for Brackets: [ ] */
    private static final String   BRACKET_PATTERN   = "\\[|\\]";
    /** Pattern for Semicolon: ; */
    private static final String   SEMICOLON_PATTERN = "\\;";
    /** Pattern for a String: "string" (Anything between quotes gets colored too) */
    private static final String   STRING_PATTERN    = "\"([^\"\\\\]|\\\\.)*\"";
    /** Pattern for comments: singleline: -- Comment Multiline: --[[ Comment ]]-- */
    private static final String   COMMENT_PATTERN   = "--\\[\\[" + "(.|\\R)*?" + "\\]\\]--" + "|" + "--[^\n]*";
    /** Default pattern. Works on anything */
    private static final String   DEFAULT           = ".";
    private Pattern        pattern;
                                  
    /**
     * Method used to highlight the text
     */
    private StyleSpans<Collection<String>> computeHighlighting(final String text) {
        // Create a matcher to look for patterns in the given text
        final Matcher matcher = pattern.matcher(text);
        int lastKwEnd = 0;
        // Create a StyleSpansBuilder to color the text
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        // Look for anything that matches the pattern
        while (matcher.find()) {
            // Switch what css block to use based on the group found by the matcher
            final String styleClass = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("OBJ") != null ? "obj"
                    : matcher.group("FUNC") != null ? "func"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : matcher.group("DEFAULT") != null ? "default" : null;
                    /* never happens */ assert styleClass != null;
            // Put the last found pattern indices in the spansBuilder
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            // Assign the appropiate style the the section highlighted in the previous line.
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            // Remember where we left off
            lastKwEnd = matcher.end();
        }
        // Don't forget the last bit of text
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        // Create the StyleSpans<Collection<String>>
        return spansBuilder.create();
    }

    private String styleSheet;
    
    /**
     * Returns the url of the given path. Also prints an error on the {@link ColoredCodeArea} if the
     * file couldn't be found
     *
     * @param path
     *            Of the css file
     * @return The path to the css file that works.
     */
    public String getCssSheet(final String path) {
        final URL url = this.getClass().getResource(path);
        if (url == null) {
            System.err.println("Resource " + path + " not found. Aborting.");
        }
        return url.toExternalForm();
    }
    
    /**
     * Makes sure everything is configured for the {@link ColoredCodeArea} The color coding works by
     * constantly calling the {@link #computeHighlighting(String)} whenever text is changed
     */
    public void setupColorCoding() {
        // Turn on paragraph numbers
        this.setParagraphGraphicFactory(LineNumberFactory.get(this));
        // Make sure any changes in textcoloring are being notified to this text area
        this.richChanges().subscribe(change -> 
            this.setStyleSpans(0, computeHighlighting(this.getText())));
        // Add stylesheets to this text area
        this.getStylesheets().add(this.getCssSheet(this.styleSheet));
        this.getStyleClass().add("background");
    }

    /**
     * Constructor for the ColorCodeArea.
     *
     * @param path
     *            The ccs to be used by this class. Leaving this empty will result in black text on
     *            a white background, without color coding
     * @param objectHighlights
     *            The Java Objects that need to be highlighted
     * @param functionHighlights
     *            The Java Functions that need to be highlighted
     */
    public void setupColoredCodeArea(final String path,
            List<String> objectHighlights,
            List<String> functionHighlights) {
        // Make sure we don't get any errors if null is passed
        if (objectHighlights == null) 
            objectHighlights = new ArrayList<String>();
        if (functionHighlights == null) 
            functionHighlights = new ArrayList<String>();
        // Make sure we don't get any errors if empty arraylists are passed
        if (objectHighlights.isEmpty()) 
            objectHighlights.add(" ");
        if (functionHighlights.isEmpty()) 
            functionHighlights.add(" ");
        
        // Creates a pattern for everything that needs to be highlighted
        pattern = Pattern.compile("(?<KEYWORD>" + ColoredCodeArea.KEYWORD_PATTERN + ")"
        // Add the Java Objects and Functions that need to be highlighted
                + "|(?<OBJ>" + "\\b(" + String.join("|", objectHighlights) + ")\\b" + ")" 
                + "|(?<FUNC>" + "\\b(" + String.join("|", functionHighlights) + ")\\b" + ")" 
                + "|(?<PAREN>" + ColoredCodeArea.PAREN_PATTERN + ")" 
                + "|(?<BRACE>" + ColoredCodeArea.BRACE_PATTERN + ")" 
                + "|(?<BRACKET>" + ColoredCodeArea.BRACKET_PATTERN + ")" 
                + "|(?<SEMICOLON>" + ColoredCodeArea.SEMICOLON_PATTERN + ")"
                + "|(?<STRING>" + ColoredCodeArea.STRING_PATTERN + ")" 
                + "|(?<COMMENT>" + ColoredCodeArea.COMMENT_PATTERN + ")" 
                + "|(?<DEFAULT>" + ColoredCodeArea.DEFAULT + ")");
        
        this.styleSheet = path;
        this.setupColorCoding();
    }
}