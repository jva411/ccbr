package me.jvagamer.cpu.com.machine;

import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.com.component.Component;
import me.jvagamer.cpu.com.component.HD;
import me.jvagamer.cpu.com.inventory.Inventore;
import me.jvagamer.cpu.listeners.Listenner;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ComplexMachine extends SimpleMachine {
    
    protected Location HopperTop, HopperBottom;
    protected Inventore Inventory;
    protected ArrayList<Component> Components;
    protected ArrayList<Location> Hoppers;
    protected int Task;
    
    public ComplexMachine(Location Loc) {
        super(Loc);
        this.Inventory = new Inventore(getName());
        this.Components = new ArrayList<>();
        this.Hoppers = new ArrayList<>();
        this.HopperTop = new Location(Loc.getWorld(), Loc.getBlockX(), Loc.getBlockY()+1, Loc.getBlockZ());
        this.HopperBottom = new Location(Loc.getWorld(), Loc.getBlockX(), Loc.getBlockY()-1, Loc.getBlockZ());
    }

    public Location getHopperBottom() {
        return HopperBottom;
    }

    public Location getHopperTop() {
        return HopperTop;
    }

    public ArrayList<Location> getHoppers() {
        return Hoppers;
    }

    public Inventore getInventory() {
        return Inventory;
    }
    
    @Override
    public void stop(){
        close();
        Bukkit.getScheduler().cancelTask(Task);
        MachineManager.Machines.remove(this);
        MachineManager.delMachine(this);
    }
    
    public void close(){
        Inventory.closeAll(this);
    }
    
    public void open(Player p){
        Inventory.open(p, this);
    }
    
    public void openWrench(Player p){
        Inventory.openWrench(p, this);
    }

    public ArrayList<Component> getComponents() {
        return (ArrayList<Component>)Components.clone();
    }
    
    public Component addComponent(Component comp){
        if(Components.size()==8) return comp;
        Components.add(comp);
        saveMachine();
        update();
        return null;
    }
    
    public Component remComponent(Component comp){
        if(Components.contains(comp)) {
            Components.remove(comp);
            saveMachine();
            update();
            return null;
        }
        return comp;
    }
    
    public void closeWork(Player p, Inventory inv){
        Inventory.closeWork(p, this);
        Components = new ArrayList<>();
        for(int i=0;i<9;i++) {
            ItemStack is = inv.getItem(i);
            if(!IsBuilder.isAir(is)){
                ItemStack[] iss = IsBuilder.separeInAmount(is, 1);
                HD hd = HD.loadHD(iss[0]);
                if(hd==null) Listenner.darItem(p, iss[0], true);
                else Components.add(HD.loadHD(iss[0]));
                if(iss[1]!=null)Listenner.darItem(p, iss[1], true);
            }
        }
        saveMachine();
    }
    
    public void addHopper(Location Loc){
        Hoppers.add(Loc);
    }
    
    public abstract ItemStack getItem();
    public abstract String getName();
    protected abstract void init();
    public abstract void loadMachine();
    public abstract void saveMachine();
    public abstract void update();
    
}
