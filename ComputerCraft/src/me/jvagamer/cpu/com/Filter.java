package me.jvagamer.cpu.com;

import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.api.ItemConstructor;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Filter {
    
    public static ItemConstructor Icons = new ItemConstructor();
    
    public enum Type {
        
        GENERIC(new IsBuilder().newItem(Material.DIAMOND).setName("§bFiltro §e§lGenérico").getItemStack(), "GENERIC"),
        SPECIFIC(new IsBuilder().newItem(Material.DIAMOND).setName("§bFiltro §e§lEspecífico").getItemStack(), "SPECIFIC");
        
        private Type(ItemStack Is, String Type) {
            this.Type = Type;
            NBTTagCompound Tag = new NBTTagCompound(), tag = new NBTTagCompound(), items = new NBTTagCompound();
            tag.setBoolean(Strings.canBeTransefered, false);
            tag.setBoolean(Strings.isFilter, true);
            tag.setString(Strings.filterType, Type);
            tag.set(Strings.filterItems, items);
            Tag.set(Strings.ComputerCraft, tag);
            this.Is = new IsBuilder(Is).addTag(Tag).getItemStack();
        }
        
        public ItemStack Is;
        public String Type;
        
    }
    
    public static boolean isFilter(ItemStack is){
        if(!IsBuilder.isAir(is)){
            if(is.getType()==Material.DIAMOND){
                net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
                if(nmsIs.hasTag()){
                    NBTTagCompound Tag = nmsIs.getTag();
                    if(Tag.hasKey(Strings.ComputerCraft)){
                        NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
                        if(tag.hasKey(Strings.isFilter)){
                            if(tag.getBoolean(Strings.isFilter)){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static void open(Player p, ItemStack is){
        Inventory inv = Bukkit.createInventory(null, 9, "§eSeleção de Filtro");
        int s = 0;
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        NBTTagCompound Tag = nmsIs.getTag(), tag = Tag.getCompound(Strings.ComputerCraft), items = tag.getCompound(Strings.filterItems);
        for(String key:items.getKeys()){
            ItemStack Is = Icons.getItem(items.getString(key));
            inv.setItem(s, Is);
            s++;
        }
        p.openInventory(inv);
    }
    
    public static ItemStack close(Player p, ItemStack is, Inventory inv){
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        NBTTagCompound Tag = nmsIs.getTag(), tag = Tag.getCompound(Strings.ComputerCraft), items = new NBTTagCompound();
        for(int i=0;i<9;i++){
            ItemStack Is = inv.getItem(i);
            if(!IsBuilder.isAir(Is)){
                items.setString("i"+i, Icons.getString(Is));
            }
        }
        tag.set(Strings.filterItems, items);
        Tag.set(Strings.ComputerCraft, tag);
        nmsIs.setTag(Tag);
        return nmsIs.asBukkitCopy();
    }
    
}
