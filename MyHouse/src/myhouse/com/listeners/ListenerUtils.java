package myhouse.com.listeners;

import java.util.HashMap;
import myhouse.com.flags.Flag;
import myhouse.com.region.Region;
import myhouse.com.region.RegionManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ListenerUtils {
    
    public RegionManager rm;
    public HashMap<Player, Location> Loc1s, Loc2s;

    public ListenerUtils(RegionManager rm) {
        Loc1s = new HashMap<>();
        Loc2s = new HashMap<>();
        this.rm = rm;
    }
    
    public void setLocation1(Player p, Location loc){
        if(Loc1s.containsKey(p)){
            Location Loc = Loc1s.get(p);
            if(Loc.getBlockX()==loc.getBlockX() && Loc.getBlockY()==loc.getBlockY() && Loc.getBlockZ()==loc.getBlockZ()){
                return;
            }else{
                p.sendBlockChange(Loc, Loc.getBlock().getBlockData());
            }
        }
        Loc1s.put(p, loc.clone());
        rm.blocks.get(p).sendBlock(loc, Material.GLOWSTONE, (byte)0);
        p.sendMessage(rm.ConfigU.Lang.getString("localSelecionado").replace('&', '§').replace("{coords}", loc.getBlockX()+" "+loc.getBlockZ()).replace("{point}", "1"));
    }
    public void setLocation2(Player p, Location loc){
        if(Loc2s.containsKey(p)){
            Location Loc = Loc2s.get(p);
            if(Loc.getBlockX()==loc.getBlockX() && Loc.getBlockY()==loc.getBlockY() && Loc.getBlockZ()==loc.getBlockZ()){
                return;
            }else{
                p.sendBlockChange(Loc, Loc.getBlock().getBlockData());
            }
        }
        Loc2s.put(p, loc.clone());
        rm.blocks.get(p).sendBlock(loc, Material.GLOWSTONE, (byte)0);
        p.sendMessage(rm.ConfigU.Lang.getString("localSelecionado").replace('&', '§').replace("{coords}", loc.getBlockX()+" "+loc.getBlockZ()).replace("{point}", "2"));
    }
    
    public boolean canEndermanTeleport(Entity e, Region region){
        if(e instanceof Enderman){
            if(region.getFlagManager().getEnabledFlags().contains(Flag.NO_ENDERMAN_TELEPORT)){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }
    
    public boolean b(Player p, Region region){
        if(rm.isOp(p)) return true;
        if(region != null){
            if(rm.isOwner(p.getName(), region)){
                return true;
            }else if(rm.isMember(p.getName(), region)){
                return true;
            }else{
                return false;
            }
        }else{
            return true;
        }
    }
    
    private boolean a(Player p, Region region, Flag flag){
        if(rm.isOp(p)) return true;
        if(region != null){
            if(rm.isOwner(p.getName(), region)){
                return true;
            }else if(rm.isMember(p.getName(), region)){
                return true;
            }else{
                if(region.getFlagManager().getEnabledFlags().contains(flag)){
                    return false;
                }else {
                    return true;
                }
            }
        }else{
            return true;
        }
    }
    
    public boolean canPortal(Player p, Region region){
        return a(p, region, Flag.NO_PORTAL);
    }
    
    public boolean canInteract(Player p, Region region){
        return a(p, region, Flag.SAFE_INTERACT);
    }
    
    public boolean canChests(Player p, Region region){
        return a(p, region, Flag.SAFE_CHESTS);
    }
    
    public boolean canWork(Player p, Region region){
        return a(p, region, Flag.NO_WORKS);
    }
    
    public boolean canDoors(Player p, Region region){
        return a(p, region, Flag.SAFE_DOORS);
    }
    
    public boolean canBeacon(Player p, Region region){
        return b(p, region);
    }
    
    public boolean canMove(Player p, Region region){
        return a(p, region, Flag.NO_ENTRY);
    }
    
    public boolean canBuild(Player p, Region region){
        return a(p, region, Flag.NO_BUILD);
    }
    
    public boolean canMobDamage(Player p, Region region){
        return a(p, region, Flag.NO_ANIMALS_DAMAGE);
    }
    
    public boolean isAllowPVP(Region region){
        return region.getFlagManager().getEnabledFlags().contains(Flag.NO_PVP);
    }
    
    public boolean isAllowPVP(Location loc){
        Region region = rm.getRegion(loc, true);
        if(region==null){
            return true;
        }else{
            return region.getFlagManager().getEnabledFlags().contains(Flag.NO_PVP);
        }
    }
    
    
    
}