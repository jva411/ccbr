package me.jvagamer.cpu.com;

import org.bukkit.inventory.ItemStack;
import me.jvagamer.cpu.api.IsBuilder;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;

public class Sucker {
    
    public enum Type {
        
        X3(new IsBuilder().newItem(Material.DIAMOND).setName("§e§lVálvula de Vácuo Nv.1").setLore("§7Essa válvula consegue sugar todos",
                "§7Os itens em um cubo 3x3x3",
                "§7Cujo o centro é o funil.").getItemStack(), (byte)1),
        X5(new IsBuilder().newItem(Material.DIAMOND).setName("§e§lVálvula de Vácuo Nv.2").setLore("§7Essa válvula consegue sugar todos",
                "§7Os itens em um cubo 5x5x5",
                "§7Cujo o centro é o funil.").getItemStack(), (byte)2),
        X7(new IsBuilder().newItem(Material.DIAMOND).setName("§e§lVálvula de Vácuo Nv.3").setLore("§7Essa válvula consegue sugar todos",
                "§7Os itens em um cubo 7x7x7",
                "§7Cujo o centro é o funil.").getItemStack(), (byte)3);
        

        private Type(ItemStack Is, byte b) {
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(Is);
            NBTTagCompound Tag = new NBTTagCompound(), tag = new NBTTagCompound();
            tag.setBoolean(Strings.canBeTransefered, false);
            tag.setBoolean(Strings.isSucker, true);
            tag.setByte(Strings.suckPower, b);
            Tag.set(Strings.ComputerCraft, tag);
            this.Is = new IsBuilder(Is).addTag(Tag).getItemStack();
        }
        
        public ItemStack Is;
        
    }
    
    public static byte getSuckerPower(ItemStack is){
        return CraftItemStack.asNMSCopy(is).getTag().getCompound(Strings.ComputerCraft).getByte(Strings.suckPower);
    }
    
    public static boolean isSucker(ItemStack is){
        if(!IsBuilder.isAir(is)){
            if(is.getType()==Material.DIAMOND){
                net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
                if(nmsIs.hasTag()){
                    NBTTagCompound Tag = nmsIs.getTag();
                    if(Tag.hasKey(Strings.ComputerCraft)){
                        NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
                        if(tag.hasKey(Strings.isSucker)){
                            return tag.getBoolean(Strings.isSucker);
                        }
                    }
                }
            }
        }
        return false;
    }
    
}
