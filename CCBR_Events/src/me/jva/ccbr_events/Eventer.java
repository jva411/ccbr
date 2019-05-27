package me.jva.ccbr_events;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Eventer {
    
    private final Player player;
    private final Event event;
    private Location spawnPoint;
    
    public Eventer(Player Player, Event Event){
        player = Player;
        event = Event;
        spawnPoint = event.getEventStart();
    }

    public Player getPlayer() {
        return player;
    }

    public Event getEvent() {
        return event;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Location spawnPoint) {
        this.spawnPoint = spawnPoint;
    }
    
}
