package ui.lua.console;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 * Class used to color code the given keywords.
 * It colors the following symbols: ( ) { } [ ] "text" // ;
 * These colors can be edited in the java-keywords.css
 * 
 * 
 * @Author Thomas Hakkers
 * Most code originates from https://github.com/TomasMikula/RichTextFX
 */
public class ColoredCodeArea extends CodeArea{
	private String styleSheet;

	// Java Keywords Variables
	private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "function"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String DEFAULT = ".";

    private static Pattern PATTERN;
    
    /**
     * Constructor for the ColorCodeArea.
     * 
     * @param path The ccs to be used by this class. Leaving this empty will result in black text on a white background, without color coding
     * @param objectHighlights The Java Objects that need to be highlighted
     * @param functionHighlights The Java Functions that need to be highlighted
     */
    public ColoredCodeArea(String path, ArrayList<String> objectHighlights, ArrayList<String> functionHighlights){
    	super();
    	
    	if(objectHighlights == null)
    		objectHighlights = new ArrayList<String>();
    	if(functionHighlights == null)
    		functionHighlights = new ArrayList<String>();
    	if(objectHighlights.isEmpty())
    		objectHighlights.add(" ");
    	if(functionHighlights.isEmpty())
    		functionHighlights.add(" ");
    	
    	PATTERN = Pattern.compile(
                "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                // Add the Java Objects and Functions that need to be highlighted
                + "|(?<OBJ>" + "\\b(" + String.join("|", objectHighlights) + ")\\b" + ")"
                + "|(?<FUNC>" + "\\b(" + String.join("|", functionHighlights) + ")\\b" + ")"
                + "|(?<PAREN>" + PAREN_PATTERN + ")"
                + "|(?<BRACE>" + BRACE_PATTERN + ")"
                + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                + "|(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                + "|(?<DEFAULT>" + DEFAULT + ")"
        );
    	styleSheet = path;
    	setupColorCoding();
    }

    /**
     * Method used to highlight the text
     */
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("OBJ") != null ? "obj" :
                    matcher.group("FUNC") != null ? "func" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("BRACE") != null ? "brace" :
                    matcher.group("BRACKET") != null ? "bracket" :
                    matcher.group("SEMICOLON") != null ? "semicolon" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    matcher.group("DEFAULT") != null ? "default" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
    
    /**
     * Makes sure everything is configured for the {@link ColoredCodeArea}
     * The color coding works by constantly calling the {@link #computeHighlighting(String)} whenever text is changed
     */
    public void setupColorCoding(){
    	// Set line numbers on
    	setParagraphGraphicFactory(LineNumberFactory.get(this));

    	richChanges().subscribe(change -> {
            setStyleSpans(0, computeHighlighting(getText()));
        });
        
        getStylesheets().add(getCssSheet(styleSheet));
        getStyleClass().add("background");
    }
    
    /**
     * Returns the url of the given path. Also prints an error on the {@link ColoredCodeArea} if the file couldn't be found
     * 
     * @param path Of the css file
     * @return The path to the css file that works.
     */
    public String getCssSheet(String path){
    	URL url = this.getClass().getResource(path);
        if (url == null) {
            System.err.println("Resource " + path + " not found. Aborting.");
            replaceText(0, 0, "Resource " + path + " not found. Aborting.");
        }
        return url.toExternalForm(); 
    }
}
