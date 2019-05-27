package me.jvagamer.cpu.com.machine;

import org.bukkit.Location;

public abstract class SimpleMachine {
    
    protected Location Loc;
    protected int Task;

    public SimpleMachine(Location Loc) {
        this.Loc = Loc;
        thread();
    }

    public Location getLoc() {
        return Loc;
    }
    
    public void saveMachineInFile(){
        MachineManager.saveMachine(this);
    }
    
    protected abstract void thread();
    public abstract void stop();
    public abstract String getType();
    
}
