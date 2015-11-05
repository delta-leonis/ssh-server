package org.ssh.ui.lua.console;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Custom OutputStream class, so that we don't bloat the entire Sytem.out
 * Use this stream to write to the {@link Console} instead of the System.out
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
public class ConsoleOutput extends OutputStream{
	private Console console;

	public ConsoleOutput(Console console){
		this.console = console;
	}

	@Override
	public void write(int b) throws IOException {
		console.print(String.valueOf((char)b));
	}
}
