package me.jva.ccbr_events;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EventBuilder {
    
    private Player p;
    private String name;
    private Location eventIn, eventStart, eventOut, pos1, pos2;
    private ArrayList<Location> lossePoints;
    private int Stage = 0;
    
    private CCBR_Events main;

    public EventBuilder(Player p, String name, CCBR_Events main) {
        lossePoints = new ArrayList<>();
        this.main = main;
        this.name = name;
    }

    public int getStage() {
        return Stage;
    }

    public String getName() {
        return name;
    }
    
    public void setEventIn(Location eventIn) {
        this.eventIn = eventIn;
    }

    public void setEventOut(Location eventOut) {
        this.eventOut = eventOut;
    }

    public void setEventStart(Location eventStart) {
        this.eventStart = eventStart;
    }

    public void setStage(int Stage) {
        this.Stage = Stage;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }
    
    public boolean defRegion(){
        if(pos1!=null && pos2!=null) {
            lossePoints.add(pos1);
            lossePoints.add(pos2);
            pos1 = null;
            pos2 = null;
            return true;
        }
        return false;
    }
    
    public boolean canFinnalize(){
        return lossePoints.size()>1;
    }
    
    public Event make(){
        if(lossePoints.size()%2==1) lossePoints.remove(lossePoints.size()-1);
        return new Event(main, name, eventIn, eventOut, lossePoints, main.config.getBoolean("default.kickOnLoss")
                        , main.config.getBoolean("default.kickOnDeath"), main.config.getInt("default.timeToStart")*60
                        , main.config.getInt("default.timeToEnd")*60, main.config.getBoolean("default.lastPlayerWin")
                        , eventStart, main.config.getInt("default.minPlayers"));
    }
    
}
