package ui.lua.console;

import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.luaj.vm2.LuaError;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import examples.CommunicatorExample;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import ui.lua.utils.LuaUtils;

/**
 * Simple LuaJava console.
 * The {@link Console} has access to every class that uses the {@link AvailableInLua} annotation
 * It features the following:
 * <ul>
 * 	<li> Autocomplete </li>
 *  <li> Function and Object highlights </li>
 *  <li> Command history (Use up and down keys) </li>
 * </ul>
 * 
 * Example Command stolen from {@link CommunicatorExample}: Communicator:register(SendMethod.UDP, luajava.newInstance("output.UDPSender" , "127.0.0.1", 9292))
 * To make a new instance of something, call: luajava.newInstance(Object.class, Arguments...)
 * To access a static variable in an object, use a period instead of a colon. So SendMethod.UDP, not SendMethod:UDP
 * 
 * Remember: It's a lua console, so if you wanna call an object's function, it's called like object:function() (not object.function())
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 * @see {@link Functions}
 * @see java-keyords.css for the stylesheet
 */
public class Console extends Pane
{
	/*
	 * TODO: Add globals to autocomplete
	 */
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
    	functionClasses = LuaUtils.getAllAvailableInLua();
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
     * Collects all Class names and puts them in an ArrayList<String>
     * @return an ArrayList<String> containing every class in the functionClasses variable
     */
    public ArrayList<String> getClasses(){
    	ArrayList<String> strings = new ArrayList<String>();
    	functionClasses.forEach(o -> strings.add(getSimpleName(o)));
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
    							new ArrayList<Method>(Arrays.asList(getClass(o).getDeclaredMethods())).
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
				int lastLine = consoleArea.getText().length();
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
    			String simpleName = getSimpleName(o);
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
        		if(getSimpleName(o).equals(object))
        			for(Method m : getClass(o).getDeclaredMethods())
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
	    		currentLine = consoleArea.getText().length()-cursor.length();
    		}
    	});
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
     * @param o The object we need the simple name of
     * @return The simple name of the object. If an object has been turned into a {@link Class}, it won't return Class as simpleName
     */
    private String getSimpleName(Object o){
    	return o instanceof Class ? ((Class<?>) o).getSimpleName() : o.getClass().getSimpleName();
    }
    
    /**
     * Function used to get the {@link Class} of a certain object properly.
     * Motivation:
     * 	The classes retrieved by LuaUtils are a mix of Class instances and normal instances.
     * 	When you call getClass() on each of these objects, you won't get what you want when when it's called on something that's a Class already.
     *  For example: You'd get Chicken.getClass().getClass() which would return `Class` rather than `Chicken`.
     * @param o The object to (maybe) call `getClass()` on
     * @return The valid {@link Class} of o.
     */
    @SuppressWarnings("rawtypes")
	private Class getClass(Object o){
    	return o instanceof Class ? (Class)o : o.getClass();
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
    			e.put(getSimpleName(o), o);
    		}
    		e.eval("local clock = os.clock function sleep(n) local t0 = clock() * 1000 while clock() * 1000 - t0 <= n do end end");

    		// Important piece of code that fixed all bugs. Do not decode to check its contents.  (If this gives errors, see: http://www.digizol.com/2008/09/eclipse-access-restriction-on-library.html) 
    		e.eval(new String(Base64.decode("bG9jYWwgY293ID0gewpbWyAKICBcICAgICAgICAgICAsfi4KICAgIFwgICAgICwtJ19fIGAtLAogICAgICAgXCAgeywtJyAgYC4gfSAgICAgICAgICAgICAgLCcpCiAgICAgICAgICAsKCBhICkgICBgLS5fXyAgICAgICAgICwnLCcpfiwKICAgICAgICAgPD0uKSAoICAgICAgICAgYC0uX18sPT0nICcgJyAnfQogICAgICAgICAgICggICApICAgICAgICAgICAgICAgICAgICAgIC8pCiAgICAgICAgICAgIGAtJ1wgICAgLCAgICAgICAgICAgICAgICAgICAgKQoJICAgICAgIHwgIFwgICAgICAgICBgfi4gICAgICAgIC8KICAgICAgICAgICAgICAgXCAgICBgLl8gICAgICAgIFwgICAgICAgLwogICAgICAgICAgICAgICAgIFwgICAgICBgLl9fX19fLCcgICAgLCcKICAgICAgICAgICAgICAgICAgYC0uICAgICAgICAgICAgICwnCiAgICAgICAgICAgICAgICAgICAgIGAtLl8gICAgIF8sLScKICAgICAgICAgICAgICAgICAgICAgICAgIDc3amonCiAgICAgICAgICAgICAgICAgICAgICAgIC8vX3x8CiAgICAgICAgICAgICAgICAgICAgIF9fLy8tLScvYAoJICAgICAgICAgICAgLC0tJy9gICAnCl1dCn0KZnVuY3Rpb24gY2hpY2tlbnNheSh0ZXh0KQpsID0gdGV4dDpsZW4oKQphID0gbCAvIDEwCmZvciBpPTAsYSBkbwoJaW8ud3JpdGUoIlsiIC4uIHRleHQ6c3ViKGkqMTArMSwgKChpKzEpKjEwID4gbCkgYW5kIGwgb3IgKGkrMSkqMTAgKSAuLiAgIl1cbiIpCmVuZAoJcHJpbnQoY293WzFdKQplbmQK")));

    		e.createBindings().put("wow", Console.class);
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
    	        	 println(exc.getClass().getSimpleName() + " in line: " + line);
    			}
    			
    			// TODO: Block input until here?
    			printCursor();
    		}
         } catch (Exception e) {
        	 e.printStackTrace();
         }
    	System.exit(0);
    }
}