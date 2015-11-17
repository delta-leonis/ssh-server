package org.ssh.ui.lua.console;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

public class CustomDebugLib extends DebugLib {
    private boolean interrupted = false;

    @Override
    public void onInstruction(int pc, Varargs v, int top) {
        System.out.println("On instruction" + interrupted);
        if (interrupted) {
            System.out.println("WOW HET WERKT");
            interrupted = false;
            throw new ScriptInterruptException();
        }
        super.onInstruction(pc, v, top);
    }
    
    public void interrupt(){
        interrupted = true;
    }

    public static class ScriptInterruptException extends RuntimeException {}
}
