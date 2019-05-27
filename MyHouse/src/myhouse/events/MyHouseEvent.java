package myhouse.events;

import myhouse.com.region.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class MyHouseEvent extends PlayerEvent implements Cancellable{
    
    protected boolean isCancelled;
    protected final Region Region;
    protected static final HandlerList handlers = new HandlerList();

    public MyHouseEvent(Player who, Region region) {
        super(who);
        Region = region;
        isCancelled = false;
    }

    @Override
    public boolean isCancelled(){
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean bln){
        this.isCancelled = bln;
    }
    
    public Region getRegion() {
        return Region;
    }
    
    @Override
    public HandlerList getHandlers(){
        return handlers;
    }
    
    public static HandlerList getHandlerList(){
        return handlers;
    }
    
}
