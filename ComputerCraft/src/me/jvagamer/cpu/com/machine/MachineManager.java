package me.jvagamer.cpu.com.machine;

import me.jvagamer.cpu.ComputerCraft;
import me.jvagamer.cpu.api.Config;
import java.util.ArrayList;
import me.jvagamer.cpu.com.VacuumHopper;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class MachineManager {
    
    public static Config cfg = new Config(ComputerCraft.ComputerCraft, "machines.yml");
    
    public static ArrayList<SimpleMachine> Machines = new ArrayList<>();
    
    public static void delMachine(SimpleMachine sm){
        cfg.set(getLoc(sm.getLoc()), null);
        cfg.saveConfig();
    }
    
    public static void saveMachine(SimpleMachine sm){
        String a = getLoc(sm.getLoc())+".";
        cfg.set(a+"Type", sm.getType());
        ArrayList<String> arr = new ArrayList<>();
        if(sm instanceof ComplexMachine){
            ComplexMachine cm = (ComplexMachine)sm;
            for(Location Loc:cm.getHoppers()) arr.add(getLoc(Loc));
            cfg.set(a+"Hoppers", arr);
        }
        cfg.saveConfig();
    }
    
    public static void loadMachines(){
        Machines = new ArrayList<>();
        for(String a:cfg.getConfig().getConfigurationSection("").getKeys(false)){
            String type = cfg.getString(a+".Type");
            if(type.equals("CPU")){
                CPU cpu = CPU.loadCPU(getLoc(a));
                for(String b:cfg.getConfig().getStringList(a+".Hoppers")) cpu.getHoppers().add(getLoc(b));
                Machines.add(cpu);
            }else if(type.equals("VacuumHopper")){
                VacuumHopper vh = new VacuumHopper(getLoc(a));
                Machines.add(vh);
            }
        }
    }
    
    public static String getLoc(Location Loc){
        return new StringBuilder().append(Loc.getWorld().getName()).append(' ').append(Loc.getBlockX()).append(' ').append(Loc.getBlockY()).append(' ').append(Loc.getBlockZ()).toString();
    }
    
    public static Location getLoc(String Loc){
        String[] bits = Loc.split(" ");
        return new Location(Bukkit.getWorld(bits[0]), Double.parseDouble(bits[1]), Double.parseDouble(bits[2]), Double.parseDouble(bits[3]));
    }
    
}
