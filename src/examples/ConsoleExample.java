package examples;

import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.ui.lua.console.Console;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Example that runs a {@link Console} The {@link Console} has access to every class that uses the
 * {@link AvailableInLua} annotation It features the following:
 * <ul>
 * <li>Autocomplete</li>
 * <li>Function and Object highlights</li>
 * <li>Command history (Use up and down keys)</li>
 * </ul>
 *
 * Example Command stolen from {@link CommunicatorExample}: Communicator:register(SendMethod.UDP,
 * luajava.newInstance("org.ssh.senders.UDPSender" , "127.0.0.1", 9292)) To make a new instance of
 * something, call: luajava.newInstance(Object.class, Arguments...) To access a static variable in
 * an object, use a period instead of a colon. So SendMethod.UDP, not SendMethod:UDP
 *
 * Remember: It's a lua console, so if you want to call an object's function, it's called like
 * object:function() (not object.function())
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *        
 */
public class ConsoleExample extends Application {
    
    public static void main(final String[] args) {
        Application.launch(args);
    }
    
    private final int    width  = 600;
    private final int    height = 400;
                                
    private final String title  = "Lua Console";
                                
    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.setTitle(this.title);
        primaryStage.setOnCloseRequest(e -> System.exit(0));

        // Create a new {@link Console} used to manipulate a {@link ConsoleArea}
        final Console console = new Console("bottom-console");

        // Create base
        final Scene scene = new Scene(console, this.width, this.height, Color.WHITE);

        // Add TextArea
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
