package me.jvagamer.cpu.com.machine;

import me.jvagamer.cpu.ComputerCraft;
import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.api.ItemConstructor;
import me.jvagamer.cpu.com.Strings;
import me.jvagamer.cpu.com.SuperStack;
import me.jvagamer.cpu.com.component.Component;
import me.jvagamer.cpu.com.component.HD;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.TileEntityFurnace;
import net.minecraft.server.v1_13_R2.TileEntityHopper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.block.Hopper;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class CPU extends ComplexMachine{
    
    private static ItemConstructor Icons = new ItemConstructor();
    
    public CPU(Location Loc){
        super(Loc);
        init();
    }
    
    @Override
    protected void init(){
        Furnace furnace = (Furnace)Loc.getBlock().getState();
        if(IsBuilder.isAir(furnace.getInventory().getSmelting()));
        ItemStack is = new ItemStack(Material.DIAMOND);
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        NBTTagCompound Tag = new NBTTagCompound(), tag = new NBTTagCompound();
        NBTTagCompound HDs = new NBTTagCompound();
        tag.set(Strings.machineHDs, HDs);
        Tag.set(Strings.ComputerCraft, tag);
        nmsIs.setTag(Tag);
        is = nmsIs.asBukkitCopy();
        furnace.getInventory().setSmelting(is);
        furnace.update();
        TileEntityFurnace tef = (TileEntityFurnace)((CraftWorld)furnace.getLocation().getWorld()).getHandle().getTileEntity(new BlockPosition(furnace.getLocation().getBlockX(), furnace.getLocation().getBlockY(), furnace.getLocation().getBlockZ()));
        tef.setItem(0, nmsIs);
        tef.update();
        thread();
    }
    
    public static CPU loadCPU(Location Loc){
        if(Loc.getBlock().getType()==Material.FURNACE){
            Furnace fnc = (Furnace)Loc.getBlock().getState();
            ItemStack is = fnc.getInventory().getSmelting();
            if(!IsBuilder.isAir(is)){
                if(is.getType()==Material.DIAMOND){
                    net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
                    if(nmsIs.hasTag()){
                        NBTTagCompound Tag = nmsIs.getTag();
                        if(Tag.hasKey(Strings.ComputerCraft)){
                            NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
                            if(tag.hasKey(Strings.machineHDs)){
                                NBTTagCompound HDs = tag.getCompound(Strings.machineHDs);
                                ArrayList<Component> Components = new ArrayList<>();
                                for(String key:HDs.getKeys()){
                                    HD hd = HD.loadHD(Icons.getItem(HDs.getString(key)));
                                    if(hd!=null) Components.add(hd);
                                }
                                CPU cpu = new CPU(Loc);
                                for(Component comp:Components) cpu.addComponent(comp);
                                return cpu;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void loadMachine(){
        Furnace furnace = (Furnace)Loc.getBlock().getState();
        ItemStack is = furnace.getInventory().getSmelting();
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        NBTTagCompound tag = nmsIs.getTag().getCompound(Strings.ComputerCraft);
        NBTTagCompound HDs = tag.getCompound(Strings.machineHDs);
        ArrayList<HD> Hds = new ArrayList<>();
        for(String a:HDs.getKeys()){
            Hds.add(HD.loadHD(Icons.getItem(HDs.getString(a))));
        }
        update();
    }
    
    public SuperStack addItem(SuperStack ss){
        int sa = ss.getAmount();
        for(int i=0;i<Components.size();i++){
            HD hd = (HD)Components.get(i);
            ss = hd.addItem(ss);
            if(ss.getAmount()<1) break;
        }
        if(ss.getAmount()!=sa) {
            update();
            saveMachine();
        }
        return ss;
    }
    
    public SuperStack remItem(SuperStack ss){
        int sa = ss.getAmount();
        for(int i=Components.size()-1;i>-1;i--){
            HD hd = (HD)Components.get(i);
            ss = hd.remItem(ss);
            if(ss.getAmount()<1) break;
        }
        if(ss.getAmount()!=sa) {
            update();
            saveMachine();
        }
        return ss;
    }
    
    @Override
    public void saveMachine(){
        Furnace furnace = (Furnace)Loc.getBlock().getState();
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(furnace.getInventory().getSmelting());
        NBTTagCompound Tag = nmsIs.getTag();
        NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
        NBTTagCompound HDs = new NBTTagCompound();
        for(int i=0;i<this.Components.size();i++){
            HDs.setString("HD"+i, Icons.getString(this.Components.get(i).getItemStack()));
        }
        tag.set(Strings.machineHDs, HDs);
        Tag.set(Strings.ComputerCraft, tag);
        nmsIs.setTag(Tag);
        furnace.getInventory().setSmelting(nmsIs.asBukkitCopy());
        furnace.update();
        TileEntityFurnace tef = (TileEntityFurnace)((CraftWorld)furnace.getLocation().getWorld()).getHandle().getTileEntity(new BlockPosition(furnace.getLocation().getBlockX(), furnace.getLocation().getBlockY(), furnace.getLocation().getBlockZ()));
        tef.setItem(0, nmsIs);
        tef.update();
    }
    
    public ArrayList<SuperStack> getAllSuperStacks(){
        ArrayList<SuperStack> SSs = new ArrayList<>();
        label1:
        for(Component comp:Components){
            HD hd = (HD)comp;
            label2:
            for(SuperStack ss:hd.getSuperStacks()){
                for(int i=0;i<SSs.size();i++){
                    SuperStack Ss = SSs.get(i);
                    if(IsBuilder.compareTo(ss.getItem(), Ss.getItem())){
                        Ss.setAmount(Ss.getAmount()+ss.getAmount());
                        continue label2;
                    }else{
                        SuperStack ss1 = ss.clone();
                        SSs.add(ss1);
                        continue label2;
                    }
                }
                SuperStack ss1 = ss.clone();
                if(SSs.isEmpty()) SSs.add(ss1);
            }
        }
        return SSs;
    }
    
    @Override
    public ItemStack getItem(){
        return Items.CPU.ItemStack;
    }
    
    @Override
    public String getType() {
        return "CPU";
    }
    
    @Override
    public String getName() {
        return "§9CPU";
    }
    
    public static String getNameS(){
        return "§9CPU";
    }
    
    @Override
    public void update(){
        Inventory.update(getAllSuperStacks(), Components, getName());
    }
    
    @Override
    protected void thread(){
        Task = Bukkit.getScheduler().scheduleSyncRepeatingTask(ComputerCraft.ComputerCraft, new Runnable(){
            
            @Override
            public void run(){
                suck();
                push();
            }
            
        }, 0, 4);
    }
    
    private void push(){
        if(HopperBottom.getBlock().getType()==Material.HOPPER){
            for(Component comp:Components){
                HD hd = (HD)comp;
                for(SuperStack ss:hd.getSuperStacks()){
                    Hopper hp = (Hopper)HopperBottom.getBlock().getState();
                    if(hp.getInventory().addItem(ss.getItem()).size()==0){
                        remItem(new SuperStack(ss.getItem(), 1));
                        saveMachine();
                        TileEntityHopper tep = (TileEntityHopper)((CraftWorld)hp.getLocation().getWorld()).getHandle().getTileEntity(new BlockPosition(hp.getLocation().getBlockX(), hp.getLocation().getBlockY(), hp.getLocation().getBlockZ()));
                        for(int i=0;i<5;i++) tep.setItem(i, CraftItemStack.asNMSCopy(hp.getInventory().getItem(i)));
                        tep.update();
                        return;
                    }
                }
            }
        }
    }
    
    private void suck(){
        ArrayList<Location> Locs = new ArrayList<>();
        Locs.add(HopperTop);
        for(int i=0;i<Hoppers.size();i++) {
            Location loc = Hoppers.get(i);
            if(loc.getBlock().getType()!=Material.HOPPER) {
                Hoppers.remove(loc);
                saveMachineInFile();
            }
        }
        Locs.addAll(Hoppers);
        HashMap<Location, ArrayList<Object>> SSs = new HashMap<>();
        for(Location Loc:Locs){
            if(HopperTop.getBlock().getType()==Material.HOPPER){
                Hopper hp = (Hopper)HopperTop.getBlock().getState();
                for(int i=0;i<5;i++){
                    IsBuilder ib = new IsBuilder(hp.getInventory().getItem(i));
                    if(!ib.isAir()){
                        ItemStack[] isa = ib.separeInAmount(1);
                        ArrayList<Object> objs = new ArrayList<>();
                        objs.add(addItem(new SuperStack(isa[0])));
                        objs.add(i);
                        objs.add(isa[1]);
                        SSs.put(Loc, objs);
                        break;
                    }
                }
            }
        }
        for(Location Loc:SSs.keySet()) {
            ArrayList<Object>objs = SSs.get(Loc);
            if(((SuperStack)objs.get(0)).getAmount()==0) {
                Hopper hp = (Hopper)Loc.getBlock().getState();
                ItemStack is = (ItemStack)objs.get(2);
                hp.getInventory().setItem((int)objs.get(1), is);
                hp.update();
                TileEntityHopper tep = (TileEntityHopper)((CraftWorld)hp.getLocation().getWorld()).getHandle().getTileEntity(new BlockPosition(hp.getLocation().getBlockX(), hp.getLocation().getBlockY(), hp.getLocation().getBlockZ()));
                tep.setItem((int)objs.get(1), CraftItemStack.asNMSCopy(is));
                tep.update();
            }
        }
    }
    
}