package me.jvagamer.cpu.com;

import me.jvagamer.cpu.api.IsBuilder;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class SuperStack{
    
    private ItemStack Item, SuperStack;
    private int Amount;
    
    public SuperStack(ItemStack is, int Amount){
        this.Item = clone(is);
        this.Item.setAmount(1);
        this.Amount = Amount;
        if(isSuperStack(is)) this.Item = loadSuper();
        setSuper();
    }
    
    public SuperStack(ItemStack is){
        this.Item = clone(is);
        this.Item.setAmount(1);
        this.Amount = 1;
        if(isSuperStack(is)) this.Item = loadSuper();
        setSuper();
    }
    
    private ItemStack loadSuper(){
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(Item);
        NBTTagCompound Tag = nmsIs.getTag(), tag = Tag.getCompound(Strings.ComputerCraft);
        this.Amount = tag.getInt(Strings.superStackAmount);
        tag.remove(Strings.isSuperStack);
        tag.remove(Strings.superStackAmount);
        if(tag.getKeys().size()<1) Tag.remove(Strings.ComputerCraft);
        nmsIs.setTag(Tag.getKeys().size()<1 ? null : Tag);
        IsBuilder ib = new IsBuilder(nmsIs.asBukkitCopy().clone());
        ib.remLastName();
        ib.remLore(1);
        return ib.getItemStack();
    }
    
    private void setSuper(){
        IsBuilder ib = new IsBuilder(Item.clone());
        if(!isSuperStack(ib.getItemStack())) ib.addName("§5Compactado!").addLore(1, "§5Itens Compactados: §f§l"+Amount);
        else ib.setLore(1, "§5Itens Compactados: §f§l"+Amount);
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(ib.getItemStack());
        NBTTagCompound Tag = nmsIs.hasTag() ? nmsIs.getTag() : new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        if(Tag.hasKey(Strings.ComputerCraft)) tag = Tag.getCompound(Strings.ComputerCraft);
        tag.setBoolean(Strings.isSuperStack, true);
        tag.setInt(Strings.superStackAmount, Amount);
        Tag.set(Strings.ComputerCraft, tag);
        nmsIs.setTag(Tag);
        SuperStack = CraftItemStack.asBukkitCopy(nmsIs);
    }

    public ItemStack getItem() {
        return Item;
    }

    public ItemStack getSuperStack() {
        return SuperStack;
    }
    
    public int getAmount(){
        return Amount;
    }
    
    public void setAmount(int Amount){
        this.Amount = Amount;
        setSuper();
    }
    
    public static int getAmount(ItemStack is){
        if(isSuperStack(is)){
            return CraftItemStack.asNMSCopy(is).getTag().getInt(Strings.superStackAmount);
        }
        return -1;
    }
    
    public static boolean isSuperStack(ItemStack is){
        if(!IsBuilder.isAir(is)){
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
            if(nmsIs.hasTag()){
                if(nmsIs.getTag().hasKey(Strings.ComputerCraft)){
                    NBTTagCompound tag = nmsIs.getTag().getCompound(Strings.ComputerCraft);
                    if(tag.hasKey(Strings.isSuperStack)){
                        return tag.getBoolean(Strings.isSuperStack);
                    }
                }
            }
        }
        return false;
    }
    
    private ItemStack clone(ItemStack is){
        return IsBuilder.clone(is);
    }
    
    public SuperStack clone(){
        return new SuperStack(clone(SuperStack));
    }
    
}
