package maquinas3;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class HD {
    
    private Location loc;
    private ArrayList<ArmorStand> lines;
    
    public void disable(){
        for(ArmorStand as:lines) as.remove();
        lines = new ArrayList<>();
    }
    
    public HD(Location loc, ArrayList<ArmorStand> lines){
        Location Loc = loc.clone();
        Loc.add(0.5, -1, 0.5);
        this.loc = Loc;
        this.lines = lines;
    }
    
    public void addLine(String line){
        lines.add(getAS(line));
    }
    public void setLine(int line, String Line){
        line--;
        if(line>=0 && line<lines.size()) lines.get(line).setCustomName(Line);
    }
    public void remLine(int line){
        line--;
        if(line>=0 && line<lines.size()) lines.remove(line);
    }
    
    public ArmorStand getAS(String name){
        return getAS(name, loc);
    }
    
    public ArmorStand getAS(String name, Location loc){
        ArmorStand as = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setCanPickupItems(false);
        as.setCustomNameVisible(true);
        as.setCustomName(name);
        for(Entity e:loc.getWorld().getNearbyEntities(loc, 0.1, 0.1, 0.1)) if(e instanceof ArmorStand) {
            if(!((ArmorStand)e).equals(as)) e.remove();
        }
        return as;
    }
    
    public void ajuste(){
        ArrayList<ArmorStand> newLines = new ArrayList<>();
        Location Loc = loc.clone();
        Loc.add(0, lines.size()*0.24, 0);
        for(ArmorStand as:lines){
            Loc.add(0, -0.24, 0);
            newLines.add(getAS(as.getCustomName(), Loc));
        }
        disable();
        lines = newLines;
    }
    
}
