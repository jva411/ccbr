package me.jva.ccbr_events;

import org.bukkit.Location;

public class LosseCube {
    
    public Location Start, End;

    public LosseCube(Location start, Location end) {
        if(start.getBlockX() > end.getBlockX()){
            int deltaX = end.getBlockX() - start.getBlockX();
            start.add(-deltaX, 0, 0);
            end.add(deltaX, 0, 0);
        }
        if(start.getBlockY() > end.getBlockY()){
            int deltaY = end.getBlockY() - start.getBlockY();
            start.add(0, -deltaY, 0);
            end.add(0, deltaY, 0);
        }
        if(start.getBlockZ() > end.getBlockZ()){
            int deltaZ = end.getBlockZ() - start.getBlockZ();
            start.add(0, 0, -deltaZ);
            end.add(0, 0, deltaZ);
        }
        this.Start = start;
        this.End = end;
    }
    
    public boolean isLossePoint(Location point){
        return  point.getBlockX() >= Start.getBlockX() && 
                point.getBlockY() >= Start.getBlockY() &&
                point.getBlockZ() >= Start.getBlockZ() &&
                point.getBlockX() <= End.getBlockX() &&
                point.getBlockY() <= End.getBlockY() &&
                point.getBlockZ() <= End.getBlockZ();
    }
    
}