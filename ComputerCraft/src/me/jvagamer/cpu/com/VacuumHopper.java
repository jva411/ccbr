package me.jvagamer.cpu.com;

import me.jvagamer.cpu.ComputerCraft;
import me.jvagamer.cpu.com.machine.MachineManager;
import me.jvagamer.cpu.com.machine.SimpleMachine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class VacuumHopper extends SimpleMachine{

    private final String Type = "VacuumHopper";
    
    public VacuumHopper(Location Location) {
        super(Location);
    }
    
    @Override
    public String getType(){
        return Type;
    }
    
    @Override
    public void stop(){
        Bukkit.getScheduler().cancelTask(Task);
        MachineManager.Machines.remove(this);
        MachineManager.delMachine(this);   
    }
    
    @Override
    protected void thread(){
        Task = Bukkit.getScheduler().scheduleSyncRepeatingTask(ComputerCraft.ComputerCraft, new Runnable(){
            @Override
            public void run(){
                if(Loc!=null){
                    if(Loc.getBlock().getType()==Material.HOPPER){
                        Hopper hp = (Hopper)Loc.getBlock().getState();
                        for(int i=0;i<5;i++){
                            ItemStack is = hp.getInventory().getItem(i);
                            if(Sucker.isSucker(is)){
                                byte b = Sucker.getSuckerPower(is);
                                Location loc = Loc.clone().add(0.5, 0.5, 0.5);
                                for(Entity e:loc.getNearbyEntities(((double)b+0.5), ((double)b+0.5), ((double)b+0.5))){
                                    if(e instanceof Item){
                                        Item I = (Item)e;
                                            int r = 0;
                                            try{
                                                r = hp.getInventory().addItem(I.getItemStack()).get(0).getAmount();
                                            }catch(Exception ex){}
                                            I.getItemStack().setAmount(r);
                                            if(r==0) I.remove();

                                    }
                                }
                                return;
                            }
                        }
                    }
                }
                stop();
                Loc = null;
            }
        }, 0, 1);
    }
    
}
