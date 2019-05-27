package maquinas3;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
    
    API api = Maquinas3.api;
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent e){
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        ItemStack item = p.getItemInHand().clone();
        item.setAmount(1);
        for(MachineType machineType:api.Mtypes.values()) if(machineType.getBlock().equals(item)){
            Location loc = e.getBlock().getLocation();
            api.addMachine(p, loc, new Machine(loc, machineType, p.getName(), 1, 0, 10, true, true, true, 0));
            return;
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        if(api.machines.containsKey(loc)){
            Machine maq = api.machines.get(loc);
            if(p.getName().equalsIgnoreCase(maq.getOwn()) || p.hasPermission("maquinas.bypass")){
                e.setCancelled(true);
                api.remMachine(p, loc, maq);
            }
        }
    }
    
    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
            Location loc = e.getClickedBlock().getLocation();
            if(api.machines.containsKey(loc)){
                e.setCancelled(true);
                Player p = e.getPlayer();
                Machine maq = api.machines.get(loc);
                if(p.getName().equalsIgnoreCase(maq.getOwn()) || p.hasPermission("maquinas.bypass")){
                    ItemStack item = p.getItemInHand();
                    for(Combustivel comb:api.Combs.values()) {
                        if(!api.isAir(item)) {
                            if(api.isCombOf(item, comb)) {
                                if(api.isCombOf(comb, maq)){
                                    if(maq.getTime()<=0){
                                        maq.setTime(comb.getTime());
                                        maq.setMaxTime(comb.getTime());
                                        maq.setMultiplier(maq.getType().getMultiplier()*comb.getMultiplier());
                                        maq.enableHd();
                                        api.setMachine(maq);
                                        if(!p.getGameMode().equals(GameMode.CREATIVE)){
                                            item.setAmount(item.getAmount()-1);
                                            if(item.getAmount()<=0) p.setItemInHand(null);
                                        }
                                        p.sendMessage(api.abasteceu);
                                    }else p.sendMessage(api.jaAbastecida);
                                    return;
                                }
                                p.sendMessage(api.combInv);
                                return;
                            }
                        }
                    }
                    if(p.isSneaking()) {
                        e.setCancelled(false);
                        return;
                    }
                    api.openSettings(p, maq);
                }else p.sendMessage(api.noOwn);
            }
        }
    }
    
    @EventHandler
    public void onEntityClick(PlayerInteractAtEntityEvent e){
        if(e.getRightClicked() instanceof ArmorStand) {
            if(((ArmorStand)e.getRightClicked()).isCustomNameVisible()) e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof ArmorStand) if(((ArmorStand)e.getEntity()).isCustomNameVisible()) e.setCancelled(true);
    }
    
    
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        ItemStack CI = e.getCurrentItem();
        if(api.isAir(CI)) return;
        CI = CI.clone();
        if(e.getInventory().getName().equals(api.settings)){
            e.setCancelled(true);
            if(api.players.containsKey((Player)e.getWhoClicked())){
                Player p = (Player)e.getWhoClicked();
                int page = api.pages.get(p);
                Machine maq = api.players.get(p);
                int s = e.getRawSlot();
                if(page==0){
                    if(e.getClickedInventory().equals(e.getInventory())){
                        api.fechou.put(p, false);
                        if(s==10) maq.setEnabled(!maq.isEnabled());
                        else if(s==12) if(e.isRightClick()) maq.setChestEnable(!maq.isChestEnable()); else{api.openChest(p, maq);return;}
                        else if(s==14) {
                            maq.setHdEnable(!maq.isHdEnable());
                            if(maq.isHdEnable()) maq.enableHd();
                            else maq.getHd().disable();
                        }else if(s==16) {
                            api.openUpgrade(p, maq);
                            return;
                        }
                        api.openSettings(p, maq);
                    }
                }else if(page==1){
                    if(!e.getClickedInventory().equals(p.getInventory())){
                        ItemStack Item = CI.clone();
                        Item.setAmount(1);
                        boolean ok = api.isDropOf(Item, maq);
                        if(ok){
                            int amount = 0;
                            if(e.isLeftClick()) amount = CI.getAmount();
                            else if(e.isRightClick()) amount = 1;
                            maq.remDrops(amount);
                            Item.setAmount(amount);
                            api.darItem(p, Item);
                            api.openChest(p, maq);
                        }
                    }
                }else if(page==2){
                    if(s==2) api.openSettings(p, maq);
                    else if(s==6) api.upgrade(p, maq);
                }
                api.setMachine(maq);
            }else e.getWhoClicked().closeInventory();
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player)e.getPlayer();
        if(!api.fechou.containsKey(p)) return;
        if(e.getInventory().getName().equals(api.settings)){
            Machine maq = api.players.get(p);
            if(api.fechou.get(p)){
                api.players.remove(p);
                api.fechou.remove(p);
                api.pages.put(p, -1);
                maq.setPlayer(p, false);
            }else api.fechou.put(p, true);
            if(api.pages.get(p)==1) {
                for(int i=0;i<54;i++){
                    ItemStack item = e.getInventory().getItem(i);
                    if(!(item==null || item.getType()==Material.AIR)){
                        ItemStack Item = item.clone();
                        Item.setAmount(1);
                        if(!api.isDropOf(Item, maq)) api.darItem(p, item);
                    }
                }
            }
        }
    }
    
    
    
    
    
    
    
}
