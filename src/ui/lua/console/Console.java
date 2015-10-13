package ui.lua.console;

import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.luaj.vm2.LuaError;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * Simple LuaJava console.
 * Use the {@link Functions} class to test functions like functions:hello()
 * 
 * @see {@link Functions}
 * @see java-keyords.css for the stylesheet
 */
public class Console extends Pane
{

	private ConsoleArea consoleArea;
	private ConsoleOutput out;
	private String currentCommand;
	private String cursor = "> ";
	private String title = "Lua Console";
	private int currentLine = 0;
	private ArrayList<Object> functionClasses;
	// For Singleton purposes
	private static Console console;
	
	public Console(){
		console = this;
    	functionClasses = getAllAvailableInLua();
        out = new ConsoleOutput(this);

        // Create TextArea
        consoleArea = new ConsoleArea(cursor, this, getClasses(), getFunctions());
        consoleArea.setWrapText(true);
        consoleArea.prefWidthProperty().bind(widthProperty());
        consoleArea.prefHeightProperty().bind(heightProperty());
        getChildren().add(consoleArea);
        
        // Make sure keypresses like tab and enter are handled
        addKeyListener();
        
        // Start the console loop
        new Thread(){
        	public void run(){
                consoleLoop();
        	}
        }.start();
	}
    
    /**
     * Singleton
     * @return The only console instance
     */
    public static Console getInstance(){
    	if(console == null)
    		console = new Console();
    	return console;
    }
    
    /**
     * Function that collects every class that has the {@link AvailableInLua} annotation
     * and returns it as an ArrayList<Object>
     */
    public ArrayList<Object> getAllAvailableInLua(){
    	Reflections reflections = new Reflections(
        	    new ConfigurationBuilder()
        	        .setUrls(ClasspathHelper.forJavaClassPath())
        	);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(AvailableInLua.class);
        ArrayList<Object> objectArrayList = new ArrayList<Object>();
        
        types.forEach(c -> {
        	try {
        		for(Method m : c.getDeclaredMethods())
        			// Make sure every class has a getInstance function
        			if(m.getName().equals("getInstance") && m.getReturnType().getSimpleName().equals(c.getSimpleName()) && m.getParameterCount() == 0)
        				objectArrayList.add(m.invoke(c));
			} catch (Exception e) {
				e.printStackTrace();
			}
        });
        return objectArrayList;
    }
    
    /**
     * Collects all Class names and puts them in an ArrayList<String>
     * @return an ArrayList<String> containing every class in the functionClasses variable
     */
    public ArrayList<String> getClasses(){
    	ArrayList<String> strings = new ArrayList<String>();
    	functionClasses.forEach(o -> strings.add(o.getClass().getSimpleName()));
    	strings.add("String");
    	return strings;
    }
    
    /**
     * Collects all Function names and puts them in an ArrayList<String>
     * @return an ArrayList<String> containing every Function in the functionClasses variable
     */
    public ArrayList<String> getFunctions(){
    	ArrayList<String> strings = new ArrayList<String>();
    	functionClasses.forEach(o -> 
    							new ArrayList<Method>(Arrays.asList(o.getClass().getDeclaredMethods())).
    							forEach(m -> 
    									strings.add(m.getName())));
    	return strings;
    }
    
    /**
     * Makes sure code gets executed when enter or tab is pressed.
     * <br/> If enter is pressed, the command will get executed
     * <br/> If alt-enter is pressed, a new line will be printed
     * <br/> If tab is pressed, the word you're currently typing will autocomplete.
     */
    public void addKeyListener(){
    	consoleArea.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){

			@Override
			public void handle(KeyEvent event) {
				int lastLine = consoleArea.getText().length()+1;
				if(event.getCode() == KeyCode.ENTER){
					if(event.isAltDown()){
						println("");
					}
					else if(currentCommand == null && consoleArea.getText(currentLine, lastLine).length() > cursor.length() - 1){
						currentCommand = consoleArea.getText(currentLine, lastLine).substring(cursor.length());
						println("");
					}
				}
				else if(event.getCode() == KeyCode.TAB){
					int caretPos = consoleArea.getCaretPosition();
					String command = consoleArea.getText(currentLine, caretPos).substring(cursor.length());
					String result = handleTab(command);
					if(result != null)
						consoleArea.replaceText(caretPos, caretPos, result);
				}
			}
    		
    	});
    }
    
    /**
     * Autocompletes the given command and returns the part that's missing
     * @param command The command that needs to be completed, for example "Functions:ge" or "Func"
     * @return The part of the command that's missing, for example "tFunctions()" or "tions"
     */
    private String handleTab(String command){
    	// Split whatever was before it
    	String[] split = command.split("\\(|\\)|(\\r?\\n)|\\s+");

    	// If it's a function, the split has to be at least a size of 2
    	if(split.length == 0)
    		return null;
    	
    	// Split the last record in split. 
    	String[] splitObjectsAndFunctions = split[split.length -1].split(":");
    	if(splitObjectsAndFunctions.length == 1){
    		// Autocomplete Class
    		String object = splitObjectsAndFunctions[0];
    		for(Object o : functionClasses){
    			String simpleName = o.getClass().getSimpleName();
        		if(simpleName.startsWith(object) && !simpleName.equals(object))
        			return simpleName.substring(object.length());
    		}
    	}
    	else{
    		// Autocomplete Function
    		// Split into Object and function (prefix)
        	String object = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 2];
        	String prefix = splitObjectsAndFunctions[splitObjectsAndFunctions.length - 1];
        		
        	for(Object o : functionClasses)
        		if(o.getClass().getSimpleName().equals(object))
        			for(Method m : o.getClass().getDeclaredMethods())
        				if(m.getName().startsWith(prefix) && !m.getName().equals(prefix))
        					return m.getName().substring(prefix.length()) + "(" + (m.getParameterCount() == 0 ? ")" : "");
    	}
    	return null;
    }
    
    /**
     * Appends the given String to the consoleArea
     * Don't use this function for debugging
     * @param s String that you want to print
     */
    public void print(final String s){
    	Platform.runLater(new Runnable(){
    		public void run(){
    			int i = consoleArea.getLength();
	    	    consoleArea.replaceText(i, i, s);
    		}
    	});
    }
    
    /**
     * Same as {@link #print(String)}, only with an added '\n'
     * @param s String that you want to print.
     */
    public void println(String s){
    	print(s + "\n");
    }
    
    /**
     * Prints the cursor and sets the currentLine
     */
    public void printCursor(){
    	Platform.runLater(new Runnable(){
    		public void run(){
	    	   	int i = consoleArea.getLength();
	    	    consoleArea.replaceText(i, i, '\n' + cursor);
	    		currentLine = consoleArea.getText().length()-2;
    		}
    	});
    }
    
    /**
     * Prints a line and moves the command cursor down.
     * Please use this function for logging.
     * @param s The String you want to print, without messing up any commands
     */
    public void printlnSafely(String s){
    	print(s);
    	printCursor();
    }
    
    /**
     * @return the currentLine (Where the current command starts)
     */
    public int getCurrentLine(){
    	return currentLine;
    }
    
    /**
     * @return 	The current command, and puts it back to null, 
     * 			so that the {@link #consoleLoop()} waits for the next command
     */
    private String readLine(){
    	if(currentCommand != null){
	    	String stringBuf = currentCommand;
	 		currentCommand = null;
	    	return stringBuf;
    	}
    	return null;
    }
    
    /**
     * Loop that constantly checks whether a command is available and executes it.
     */
    private void consoleLoop(){
    	try{
        	String line = "";

    		// Initialize ScriptEngine
    		ScriptEngineManager sem = new ScriptEngineManager();
    		ScriptEngine e = sem.getEngineByName("luaj");
    		e.getContext().setWriter(new OutputStreamWriter(out));
    		
    		// Add every @AvailableInLua class to the luaj
    		for(Object o : functionClasses){
    			e.put(o.getClass().getSimpleName(), o);
    		}
    		// Important piece of code that fixed all bugs. Do not decode to check its contents.
    		e.eval(new String(Base64.decode("bG9jYWwgY293ID0gewpbWyAKICBcICAgICAgICAgICAsfi4KICAgIFwgICAgICwtJ19fIGAtLAogICAgICAgXCAgeywtJyAgYC4gfSAgICAgICAgICAgICAgLCcpCiAgICAgICAgICAsKCBhICkgICBgLS5fXyAgICAgICAgICwnLCcpfiwKICAgICAgICAgPD0uKSAoICAgICAgICAgYC0uX18sPT0nICcgJyAnfQogICAgICAgICAgICggICApICAgICAgICAgICAgICAgICAgICAgIC8pCiAgICAgICAgICAgIGAtJ1wgICAgLCAgICAgICAgICAgICAgICAgICAgKQoJICAgICAgIHwgIFwgICAgICAgICBgfi4gICAgICAgIC8KICAgICAgICAgICAgICAgXCAgICBgLl8gICAgICAgIFwgICAgICAgLwogICAgICAgICAgICAgICAgIFwgICAgICBgLl9fX19fLCcgICAgLCcKICAgICAgICAgICAgICAgICAgYC0uICAgICAgICAgICAgICwnCiAgICAgICAgICAgICAgICAgICAgIGAtLl8gICAgIF8sLScKICAgICAgICAgICAgICAgICAgICAgICAgIDc3amonCiAgICAgICAgICAgICAgICAgICAgICAgIC8vX3x8CiAgICAgICAgICAgICAgICAgICAgIF9fLy8tLScvYAoJICAgICAgICAgICAgLC0tJy9gICAnCl1dCn0KZnVuY3Rpb24gY2hpY2tlbnNheSh0ZXh0KQpsID0gdGV4dDpsZW4oKQphID0gbCAvIDEwCmZvciBpPTAsYSBkbwoJaW8ud3JpdGUoIlsiIC4uIHRleHQ6c3ViKGkqMTArMSwgKChpKzEpKjEwID4gbCkgYW5kIGwgb3IgKGkrMSkqMTAgKSAuLiAgIl1cbiIpCmVuZAoJcHJpbnQoY293WzFdKQplbmQK")));

    		println(title);
    		printCursor();

			currentLine = consoleArea.getText().length();
			
			// Console Loop
    		while (true){
    			// Block until we have a line.
    			while((line = readLine()) == null);
    			try{
	    			consoleArea.addCommand(line);
	        		// Leave console if exit
	    			if(line.equals("exit"))
	    				System.exit(0);

		    		e.eval(line);
		    			
	    			// Gotta catch it while we're still in the while loop.
    			} catch(ScriptException | LuaError exc){
    	        	 printlnSafely(exc.getClass().getSimpleName() + " in line: " + line);
    			}
    			printCursor();
    		}
         } catch (Exception e) {
        	 e.printStackTrace();
         }
    	System.exit(0);
    }
}