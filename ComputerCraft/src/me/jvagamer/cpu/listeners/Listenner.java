package me.jvagamer.cpu.listeners;

import me.jvagamer.cpu.ComputerCraft;
import me.jvagamer.cpu.com.Filter;
import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.api.ItemConstructor;
import me.jvagamer.cpu.com.Strings;
import me.jvagamer.cpu.com.SuperStack;
import me.jvagamer.cpu.com.component.Component;
import me.jvagamer.cpu.com.component.HD;
import me.jvagamer.cpu.com.machine.Items;
import me.jvagamer.cpu.com.machine.CPU;
import me.jvagamer.cpu.com.machine.ComplexMachine;
import me.jvagamer.cpu.com.machine.MachineManager;
import java.util.ArrayList;
import java.util.HashMap;
import me.jvagamer.cpu.com.Sucker;
import me.jvagamer.cpu.com.VacuumHopper;
import me.jvagamer.cpu.com.machine.SimpleMachine;
import myhouse.events.PlayerWithBlockEvent;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Listenner implements Listener{
    
    public static HashMap<Player, ComplexMachine> Players = new HashMap<>();
    public static ArrayList<Player> Players2 = new ArrayList<>();
    private Location tempLoc = null;
    public ItemConstructor Icons = new ItemConstructor();
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getName().equals("§9CPU")){
            e.setCancelled(true);
            ItemStack CI = e.getCurrentItem();
            if(IsBuilder.isAir(CI)) return;
            Player p = (Player)e.getWhoClicked();
            if(e.getClickedInventory().equals(e.getInventory())){
                int s = e.getRawSlot();
                if(s<36){
                    int tira = 1;
                    SuperStack ss = new SuperStack(CI);
                    if(e.getClick()==ClickType.LEFT) tira = 64 > ss.getAmount() ? ss.getAmount() : 64;
                    else if(e.getClick()==ClickType.SHIFT_LEFT || e.getClick()==ClickType.SHIFT_RIGHT) tira = ss.getAmount();
                    ss.setAmount(tira);
                    CPU maq = (CPU)Players.get(p);
                    ss = darItem(p, ss, false);
                    ss.setAmount(tira-ss.getAmount());
                    maq.remItem(ss);
                }
            }else{
                int bota = 0;
                if(e.getClick()==ClickType.RIGHT) bota = 1;
                else if(e.getClick()==ClickType.LEFT) bota = CI.getAmount();
                else if(e.getClick()==ClickType.SHIFT_LEFT || e.getClick()==ClickType.SHIFT_RIGHT) {
                    for(int i=0;i<36;i++){
                        ItemStack is = p.getInventory().getItem(i);
                        if(!IsBuilder.isAir(is)){
                            if(IsBuilder.compareTo(CI, is)){
                                bota += is.getAmount();
                                p.getInventory().setItem(i, new ItemStack(Material.AIR));
                            }
                        }
                    }
                }
                SuperStack ss = new SuperStack(CI, bota);
                e.setCurrentItem(new IsBuilder(CI).separeInAmount(bota)[1]);
                CPU maq = (CPU)Players.get(p);
                ss = maq.addItem(ss);
                if(ss.getAmount()>0) darItem(p, ss, true);
            }
//        }else if(e.getInventory().getType()==InventoryType.HOPPER){
//            if(e.getClickedInventory().equals(e.getInventory())){
//                ItemStack is = e.getCursor();
//                if(Filter.isFilter(is)){
//                    int s = e.getRawSlot();
//                    is = is.clone();
//                    if(s!=4){
//                        ItemStack s4 = e.getInventory().getItem(4), ci = e.getCurrentItem();
//                        if(!IsBuilder.isAir(ci)) ci = ci.clone();
//                        if(!IsBuilder.isAir(s4)) s4 = s4.clone();
//                        if(e.getClick()==ClickType.LEFT){
//                            e.setCancelled(true);
//                            e.setCursor(ci);
//                            e.setCurrentItem(s4);
//                            e.getInventory().setItem(4, is);
//                        }else if(e.getClick()==ClickType.RIGHT){
//                            e.setCancelled(true);
//                            if(IsBuilder.isAir(ci)){
//                                ItemStack[] iss = IsBuilder.separeInAmount(is, 1);
//                                e.getInventory().setItem(4, iss[0]);
//                                e.setCursor(iss[1]);
//                                e.setCurrentItem(s4);
//                            }else{
//                                e.getInventory().setItem(4, is);
//                                e.setCursor(ci);
//                                e.setCurrentItem(s4);
//                            }
//                        }else if(!e.isShiftClick()) e.setCancelled(true);
//                    }
//                }
//            }
        }else if(e.getInventory().getName().equals("§eSeleção de Filtro")){
            if(e.getInventory().equals(e.getClickedInventory())){
                e.setCancelled(true);
                ItemStack is = e.getCursor(), ci = e.getCurrentItem();
                if(IsBuilder.isAir(ci)){
                    if(!IsBuilder.isAir(is)){
                        ItemStack[] iss = IsBuilder.separeInAmount(is, 1);
                        e.getInventory().setItem(e.getRawSlot(), iss[0]);
                    }
                }else{
                    e.setCurrentItem(null);
                }
            }
        }
    }
    
    public static SuperStack darItem(Player p, SuperStack ss, boolean dropRest){
        int max = ss.getItem().getMaxStackSize();
        while(ss.getAmount()>0){
            int da = max > ss.getAmount() ? ss.getAmount() : max;
            ItemStack is = ss.getItem().clone();
            is.setAmount(da);
            HashMap<Integer, ItemStack> itens = darItem(p, is, dropRest);
            for(int i:itens.keySet()) da -= i;
            ss.setAmount(ss.getAmount()-da);
            if(itens.size()>0){
                return ss;
            }
        }
        return ss;
    }
    
    public static HashMap<Integer, ItemStack> darItem(Player p, ItemStack is, boolean dropRest){
        if(dropRest){
            HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
            for(int i:itens.keySet()) {
                is.setAmount(i);
                Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
                Item.setPickupDelay(20);
            }
            return new HashMap<>();
        }else return p.getInventory().addItem(is);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!e.isCancelled()){
            if(!IsBuilder.isAir(e.getItemInHand())){
                if(IsBuilder.compareTo(Items.CPU.ItemStack, e.getItemInHand())){
                    CPU machine = new CPU(e.getBlockPlaced().getLocation());
                    MachineManager.Machines.add(machine);
                }else if(e.getItemInHand().getType()==Material.HOPPER){
                    if(tempLoc!=null){
                        for(SimpleMachine sm:MachineManager.Machines){
                            if(sm.getLoc().equals(tempLoc)){
                                if(sm instanceof ComplexMachine){
                                    ComplexMachine cm = (ComplexMachine)sm;
                                    cm.addHopper(tempLoc.clone());
                                    cm.saveMachineInFile();
                                    tempLoc = null;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onMyHouseInteract(PlayerWithBlockEvent e){
        if(e.getPlayer().hasPermission("ComputerCraft.adm")) return;
        for(SimpleMachine sm:MachineManager.Machines){
            if(sm.getLoc().equals(e.getBlock().getLocation())){
                e.setCancelled(!e.canDo());
                return;
            }
        }
    }
    
    @EventHandler
    public void onItemTransfer(InventoryMoveItemEvent e){
        ItemStack is = e.getItem();
        if(e.getSource().getName().equals(CPU.getNameS()) || e.getDestination().getName().equals(CPU.getNameS())){
            e.setCancelled(true);
        }
        if(e.getSource().getType()==InventoryType.HOPPER){
            if(!canTransferItem(is)){
                if(e.getDestination().getType()!=InventoryType.PLAYER){
                    e.setCancelled(true);
                    for(int i=0;i<5;i++){
                        ItemStack Is = e.getSource().getItem(i);
                        if(!IsBuilder.isAir(Is))
                        if(!Filter.isFilter(Is)){
                            e.setItem(Is);
                            break;
                        }
                    }
                }
            }
        }
        if(e.getDestination().getType()==InventoryType.HOPPER){
            ItemStack Is = e.getDestination().getItem(4);
            if(Filter.isFilter(Is)){
                NBTTagCompound tag = CraftItemStack.asNMSCopy(Is).getTag().getCompound(Strings.ComputerCraft),
                        items = tag.getCompound(Strings.filterItems);
                String type = tag.getString(Strings.filterType);
                boolean canPass = false;
                label1:
                for(int i=0;i<5;i++){
                    is = e.getSource().getItem(i);
                    if(!IsBuilder.isAir(is))
                    if(!Filter.isFilter(is)){
                        if(type.equals("SPECIFIC")){
                            for(String key:items.getKeys()){
                                ItemStack IS = Icons.getItem(items.getString(key));
                                if(IsBuilder.compareTo(is, IS)){
                                    canPass = true;
                                    break label1;
                                }
                            }
                        }else{
                            for(String key:items.getKeys()){
                                ItemStack IS = Icons.getItem(items.getString(key));
                                if(is.getType()==IS.getType()){
                                    canPass = true;
                                    break label1;
                                }
                            }
                        }
                    }
                }
                e.setCancelled(true);
                if(canPass){
                    if(e.getDestination().addItem(is).size()==0){
                        for(int j=0;j<5;j++){
                            ItemStack IS = e.getSource().getItem(j);
                            if(!IsBuilder.isAir(IS)){
                                if(IsBuilder.compareTo(is, IS)){
                                    e.getSource().setItem(j, IsBuilder.separeInAmount(IS, 1)[1]);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public static boolean canTransferItem(ItemStack is){
        if(!IsBuilder.isAir(is)){
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
            if(nmsIs.hasTag()){
                NBTTagCompound Tag = nmsIs.getTag();
                if(Tag.hasKey(Strings.ComputerCraft)){
                    NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
                    if(tag.hasKey(Strings.canBeTransefered)){
                        return tag.getBoolean(Strings.canBeTransefered);
                    }
                }
            }
        }
        return true;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!e.isCancelled()){
            Player p = e.getPlayer();
            if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
                for(SimpleMachine sm:MachineManager.Machines){
                    if(sm.getLoc().equals(e.getClickedBlock().getLocation())){
                        if(!e.getPlayer().isSneaking()){
                            if(sm instanceof CPU){
                                CPU cpu = (CPU)sm;
                                e.setCancelled(true);
                                if(IsBuilder.compareTo(p.getItemInHand(), Items.WRENCH.ItemStack)){
                                    cpu.openWrench(p);
                                    cpu.saveMachineInFile();
                                    return;
                                }
                                cpu.open(p);
                                return;
                            }
                        }else{
                            if(sm instanceof CPU){
                                tempLoc = sm.getLoc();
                                Bukkit.getScheduler().scheduleSyncDelayedTask(ComputerCraft.ComputerCraft, new Runnable(){
                                    
                                    @Override
                                    public void run(){
                                        tempLoc = null;
                                    }
                                    
                                }, 1);
                            }
                        }
                    }
                }
            }
            if(e.getAction()==Action.RIGHT_CLICK_AIR || e.getAction()==Action.RIGHT_CLICK_BLOCK){
                if(p.isSneaking()){
                    if(Filter.isFilter(p.getItemInHand())){
                        Filter.open(p, p.getItemInHand());
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        if(!e.isCancelled()){
            for(SimpleMachine sm:MachineManager.Machines){
                if(sm.getLoc().equals(e.getBlock().getLocation())){
                    sm.stop();
                    if(sm instanceof ComplexMachine){
                        e.setCancelled(true);
                        ComplexMachine cm = (ComplexMachine)sm;
                        for(Component comp:cm.getComponents()) {
                            HD hd = (HD)comp;
                            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), hd.getItemStack()).setPickupDelay(20);
                        }
                        e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), cm.getItem()).setPickupDelay(20);
                        e.getBlock().setType(Material.AIR);
                    }
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){
        Player p = (Player)e.getPlayer();
        if(e.getInventory().getName().equals("§9CPU")){
            if(Players2.contains(p)) Players2.remove(p);
            else {
                CPU maq = (CPU)Players.get(p);
                maq.getInventory().remove(p, maq);
                Players.remove(p);
            }
        }else if(e.getInventory().getName().equals("§9CPU §0Work")){
            CPU maq = (CPU)Players.get(p);
            maq.closeWork(p, e.getInventory());
            if(Players2.contains(p)) Players2.remove(p);
            Players.remove(p);
        }else if(e.getInventory().getName().equals("§eSeleção de Filtro")){
            if(e.getReason()!= InventoryCloseEvent.Reason.DEATH && e.getReason() != InventoryCloseEvent.Reason.DISCONNECT){
                if(Filter.isFilter(p.getItemInHand())){
                    p.setItemInHand(Filter.close(p, p.getItemInHand(), e.getInventory()));
                }
            }
        }else try{
            if(e.getInventory().getType()==InventoryType.HOPPER){
                for(int i=0;i<5;i++){
                    ItemStack is = e.getInventory().getItem(i);
                    if(Sucker.isSucker(is)){
                        Location loc = e.getInventory().getLocation();
                        for(SimpleMachine sm:MachineManager.Machines){
                            if(MachineManager.getLoc(sm.getLoc()).equals(MachineManager.getLoc(loc))) return;
                        }
                        VacuumHopper vh = new VacuumHopper(loc);
                        vh.saveMachineInFile();
                    }
                }
            }
        }catch(Exception ex){}
    }
}
