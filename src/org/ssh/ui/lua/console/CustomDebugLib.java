package org.ssh.ui.lua.console;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

/**
 * A custom class that extends {@link DebugLib} to be able to interrupt threads in lua
 * @author Thomas Hakkers
 * Credits go to this guy: http://stackoverflow.com/questions/17496868/lua-java-luaj-handling-or-interrupting-infinite-loops-and-threads
 */
public class CustomDebugLib extends DebugLib {
    private boolean interrupted = false;
    private boolean running = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInstruction(int pc, Varargs v, int top) {
        if (interrupted) {
            interrupted = false;
            throw new ScriptInterruptException();
        }
        super.onInstruction(pc, v, top);
    }
    
    /**
     * Overriding this to set running to true.
     * {@see {@link CustomDebugLib#running}}
     */
    @Override
    public void onCall(LuaClosure c, Varargs varargs, LuaValue[] stack){
        super.onCall(c,varargs, stack);
        this.running = true;
    }
    /**
     * Overriding this to set running to true.
     * {@see {@link CustomDebugLib#running}}
     */
    @Override
    public void onCall(LuaFunction f){
        super.onCall(f);
        this.running = true;
    }
    
    @Override
    public void onReturn(){
        super.onReturn();
        this.running = false;
    }

    /**
     * Interrupts the current thread
     */
    public void interrupt(){
        if(running)
            interrupted = true;
    }

    /**
     * The exception that gets thrown when a lua thread is interrupted
     */
    @SuppressWarnings ("serial")
    public static class ScriptInterruptException extends RuntimeException {
        
    }
}
