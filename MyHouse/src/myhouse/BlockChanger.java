package myhouse;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockChanger {
    
    private Player player;
    private JavaPlugin plugin;
    
    public BlockChanger(Player p, JavaPlugin pl){
        player = p;
        plugin = pl;
    }
    
    public void sendBlock(Location Loc, BlockData bd){
        sendBlock(Loc, bd, null, (byte)-1);
    }
    
    public void sendBlock(Location Loc, Material mtrl, byte b){
        sendBlock(Loc, null, mtrl, b);
    }
    
    private void sendBlock(Location Loc, BlockData bd, Material mtrl, byte b){
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                if(mtrl==null || b==-1){
                    player.sendBlockChange(Loc, bd);
                }else if(bd==null){
                    player.sendBlockChange(Loc, mtrl, b);
                }else return;
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {

                    @Override
                    public void run() {
                        player.sendBlockChange(new Location(Loc.getWorld(), Loc.getBlockX(), Loc.getBlockY(), Loc.getBlockZ()), Loc.getBlock().getBlockData());
                    }
                    
                }, 600);
            }
            
        }, 1);
    }
    
}
