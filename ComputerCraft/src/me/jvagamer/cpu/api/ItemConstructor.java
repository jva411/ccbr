package me.jvagamer.cpu.api;

import net.minecraft.server.v1_13_R2.MojangsonParser;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ItemConstructor {
    
    IsBuilder Ib;
    StringBuilder sb;
    
    public ItemStack getItem(String line){
        sb = new StringBuilder();
        String[] bits = line.split(" ");
        String type = bits[0].toUpperCase();
        int amount = 1;
        try{
            amount = Integer.parseInt(bits[1]);
        }catch(Exception e){}
        ItemStack is = new ItemStack(Material.valueOf(type), amount);
        Ib = new IsBuilder(is);
        for(int i=0;i<bits.length;i++) if(i>1) sb.append(bits[i]).append(' ');
        line = sb.toString().trim();
        try{
            Ib.addTag(MojangsonParser.parse(line));
        }catch(Exception ex){}
        return Ib.getItemStack();
    }
    
    public String getString(ItemStack is){
        sb = new StringBuilder().append(is.getType().toString()).append(' ').append(is.getAmount());
        try{
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
            if(nmsIs.hasTag()){
                sb.append(' ').append(nmsIs.getTag());
            }
        }catch(Exception e){}
        return sb.toString().trim();
    }
    
}
