package myhouse.com.listeners;

import myhouse.com.region.Region;
import myhouse.events.PlayerWithBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class ProtectListener implements Listener{
    
    public ListenerUtils listU;

    public void setListU(ListenerUtils listU) {
        this.listU = listU;
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPortalEvent(PlayerPortalEvent e){
        Player p = e.getPlayer();
        Region region = listU.rm.getRegion(p.getLocation(), true);
        if(region!=null){
            if(!listU.canPortal(p, region)){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction()==Action.LEFT_CLICK_BLOCK || e.getAction()==Action.RIGHT_CLICK_BLOCK){
            Region region = listU.rm.getRegion(e.getClickedBlock().getLocation(), true);
            if(region!=null){
                Material mtrl = e.getClickedBlock().getType();
                if(mtrl.toString().contains("PRESSURE_PLATE") || mtrl.toString().contains("BUTTON")){
                    if(!listU.canInteract(p, region)){
                        e.setCancelled(true);
                    }
                }else if(mtrl.toString().contains("CHEST") || mtrl.toString().contains("FURNACE") || mtrl.toString().contains("BOX") || mtrl.toString().contains("CART") || mtrl.toString().contains("HOPPER")){
                    if(!listU.canChests(p, region)){
                        e.setCancelled(true);
                    }
                }else if(mtrl.toString().contains("ANVIL") || mtrl.toString().contains("TABLE")){
                    if(!listU.canWork(p, region)){
                        System.out.println("Cant");
                        e.setCancelled(true);
                    }
                }else if(mtrl.toString().contains("DOOR") || mtrl.toString().contains("GATE") || mtrl.toString().contains("TRAP")){
                    if(!listU.canDoors(p, region)){
                        e.setCancelled(true);
                    }
                }else if(mtrl.toString().contains("GRASS") || mtrl.toString().contains("SEED") || mtrl.toString().contains("SAPPLING")){
                    if(p.getItemInHand().getType().toString().contains("BONE")){
                        if(!listU.b(p, region)){
                            e.setCancelled(true);
                        }
                    }
                }else if(mtrl.toString().contains("BEACON")){
                    if(!listU.canBeacon(p, region)){
                        e.setCancelled(true);
                    }
                }else if(mtrl==Material.FARMLAND){
                    if(!listU.b(p, region)){
                        e.setCancelled(true);
                    }
                }
                if(p.getItemInHand().getType()==Material.FLINT_AND_STEEL){
                    if(!listU.b(p, region)){
                        e.setCancelled(true);
                    }
                }
                PlayerWithBlockEvent.Action Act = null;
                if(e.getAction()==Action.LEFT_CLICK_BLOCK) Act = PlayerWithBlockEvent.Action.LEFT_CLICK;
                else Act = PlayerWithBlockEvent.Action.RIGHT_CLICK;
                PlayerWithBlockEvent ev = new PlayerWithBlockEvent(p, region, e.getClickedBlock(), Act);
                Bukkit.getPluginManager().callEvent(ev);
                if(ev.isCancelled()) e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        if(!listU.rm.isOp(e.getPlayer())){
            Region region = listU.rm.getRegion(e.getRightClicked().getLocation(), true);
            if(region!=null){
                Player p = e.getPlayer();
                if(!(listU.rm.isOwner(p.getName(), region) || listU.rm.isMember(p.getName(), region))) {
                    if(!e.getPlayer().getItemInHand().getType().toString().contains("AIR")){
                        e.setCancelled(true);
                    }
                    if(e.getRightClicked().getType()==EntityType.ARMOR_STAND || e.getRightClicked().getType()==EntityType.ITEM_FRAME || e.getRightClicked() instanceof Vehicle){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTeleport(EntityTeleportEvent e){
        if(e.getEntity() instanceof Enderman){
            Region region = listU.rm.getRegion(e.getFrom(), true);
            if(region!=null){
                if(!listU.canEndermanTeleport(e.getEntity(), region)){
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerMove(PlayerMoveEvent e){
        Region region = listU.rm.getRegion(e.getTo(), true);
        if(region!=null){
            Player p = e.getPlayer();
            if(!listU.canMove(p, region)){
                p.teleport(e.getFrom());
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent e) {
        Region region = listU.rm.getRegion(e.getBlock().getLocation(), true);
        if(region!=null){
            Player p = e.getPlayer();
            if(!listU.canBuild(p, region)){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onBlockPlace2(BlockPlaceEvent e) {
        Region region = listU.rm.getRegion(e.getBlock().getLocation(), true);
        if(region!=null){
            Player p = e.getPlayer();
            if(!listU.canBuild(p, region)){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamageEntity(EntityDamageByEntityEvent e){
        Player p = null;
        if(e.getDamager() instanceof Player){
            p = (Player)e.getDamager();
        }else if(e.getDamager() instanceof Projectile){
            Projectile proj = (Projectile)e.getDamager();
            if(proj.getShooter() instanceof Player){
                p = (Player)proj.getShooter();
            }
        }
        if(p!=null){
            if(e.getEntity() instanceof Player){
                if(!listU.isAllowPVP(p.getLocation())){
                    e.setCancelled(true);
                }else if(!listU.isAllowPVP(e.getEntity().getLocation())){
                    e.setCancelled(true);
                }
            }else {
                Region region = listU.rm.getRegion(e.getEntity().getLocation(), true);
                if(region!=null){
                    if(e.getEntity() instanceof Animals){
                        if(!listU.canMobDamage(p, region)){
                            e.setCancelled(true);
                        }
                    }else if(e.getEntityType()==EntityType.ARMOR_STAND || e.getEntityType()==EntityType.ITEM_FRAME || e.getEntityType().toString().contains("CART")){
                        if(!listU.b(p, region)){
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }else{
            if(e.getCause().toString().contains("EXPLOSION")) {
                if(listU.rm.hasRegion(e.getEntity().getLocation())){
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onExplosion(EntityExplodeEvent e){
        for(Block block:e.blockList()){
            if(listU.rm.hasRegion(block.getLocation())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPistonExtend(BlockPistonExtendEvent e){
        for(Block block:e.getBlocks()) if(listU.rm.hasRegion(block.getLocation())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPistonRetract(BlockPistonRetractEvent e){
        for(Block block:e.getBlocks()) if(listU.rm.hasRegion(block.getLocation())) {
            e.setCancelled(true);
        }
    }
    
//    @EventHandler(priority = EventPriority.LOW)
//    public void onEntityEvent(EntityEvent e){
//        if(!(e.getEntity() instanceof Player)){
//            e.getEntity().getType().
//        }
//    }
    
}
