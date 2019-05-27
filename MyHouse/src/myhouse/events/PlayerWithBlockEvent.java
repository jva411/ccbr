package myhouse.events;

import myhouse.com.region.Region;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerWithBlockEvent extends MyHouseEvent{

    public static enum Action {
        LEFT_CLICK, RIGHT_CLICK, PLACED, BROKEN;
    }
    
    private final Block Block;
    private final Action Action;
    
    public PlayerWithBlockEvent(Player who, Region region, Block block, Action action) {
        super(who, region);
        this.Block = block;
        this.Action = action;
    }

    public Block getBlock() {
        return Block;
    }

    public Action getAction() {
        return Action;
    }
    
    public boolean canDo(){
        if(player.hasPermission("MyHouse.adm") || player.isOp()) return true;
        if(player.getName().equalsIgnoreCase(Region.getOwner())) return true;
        for(String a:Region.getMembers()){
            if(player.getName().equalsIgnoreCase(a)){
                return true;
            }
        }
        return false;
    }
    
}
