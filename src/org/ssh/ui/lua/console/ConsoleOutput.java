package org.ssh.ui.lua.console;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Custom OutputStream class, so that we don't bloat the entire Sytem.out Use this stream to write
 * to the {@link Console} instead of the System.out
 *
 * @author Thomas Hakkers
 *         
 */
public class ConsoleOutput extends OutputStream {
    
    private final Console console;
    private String        currentLine = "";
                                      
    public ConsoleOutput(final Console console) {
        this.console = console;
    }
    
    /**
     * Adds a character to the currentLine buffer. The buffer gets pushed when a "\n" is passed
     */
    @Override
    public void write(final int b) throws IOException {
        String stringval = String.valueOf((char) b);
        currentLine += stringval;
        // Should work in both Windows and UNIX systems.
        if ("\n".equals(stringval)) {
            this.console.print(currentLine);
            currentLine = "";
        }
    }
}
