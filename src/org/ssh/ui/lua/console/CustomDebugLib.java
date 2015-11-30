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
    /**
     * If true, the current function will be stopped.
     */
    private boolean interrupted = false;
    /**
     * True when a function is running, false otherwise. Prevents functions from being cancelled
     * before they've even started yet
     */
    private boolean running = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onInstruction(int pc, Varargs v, int top) {
        if (interrupted) {
            interrupted = false;
            running = false;
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
    /**
     * Overriding this to set running to false.
     * {@see {@link CustomDebugLib#running}}
     */
    @Override
    public void onReturn(){
        super.onReturn();
        this.running = false;
    }

    /**
     * Interrupts the current thread
     */
    public void setInterrupt(boolean interrupt){
        if(running && interrupt)
            interrupted = true;
        else
            interrupted = false;
    }

    /**
     * The exception that gets thrown when a lua thread is interrupted
     */
    @SuppressWarnings ("serial")
    public static class ScriptInterruptException extends RuntimeException {
        
    }
}
