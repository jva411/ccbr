package me.jvagamer.cpu.api;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class IsBuilder {
    
    private ItemStack is;
    private ItemMeta ism;

    public ItemStack getItemStack() {
        return is;
    }
    
    public void setMeta(){
        is.setItemMeta(ism);
    }

    public IsBuilder() {
    }
    
    public IsBuilder(ItemStack is){
        this.is = is;
        if(!isAir()) this.ism = is.getItemMeta();
    }
    
    public IsBuilder newItem(Material mtrl, int damage, int amount){
        is = new ItemStack(mtrl, amount, (short)damage);
        if(!isAir()) this.ism = is.getItemMeta();
        return this;
    }
    public IsBuilder newItem(Material mtrl, int damage){
        return newItem(mtrl, damage, 1);
    }
    public IsBuilder newItem(Material mtrl){
        return newItem(mtrl, 0, 1);
    }
    
    public IsBuilder setName(String name){
        ism.setDisplayName(name);
        setMeta();
        return this;
    }
    public IsBuilder setName(String[] args){
        if(args.length<1) return this;
        StringBuilder sb = new StringBuilder(args[0]);
        for(int i=1;i<args.length;i++) sb.append(' ').append(args[i]);
        return setName(sb.toString());
    }
    
    public IsBuilder addName(String... add){
        if(add.length<1) return this;
        StringBuilder sb = new StringBuilder();
        String name = getName();
        if(name.length()>0) sb.append(name).append(' ');
        sb.append(add[0]);
        for(int i=1;i<add.length;i++) sb.append(' ').append(add[i]);
        ism.setDisplayName(sb.toString());
        setMeta();
        return this;
    }
    
    public IsBuilder remLastName(){
        String[] bits = getName().split(" ");
        if(bits.length<2) {
            ism.setDisplayName("");
            setMeta();
            return this;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(bits[0]);
        for(int i=1;i<bits.length-1;i++) sb.append(' ').append(bits[i]);
        ism.setDisplayName(sb.toString());
        setMeta();
        return this;
    }
    
    public IsBuilder setLore(int line, String lr){
        ArrayList<String> Lr;
        if(ism.hasLore()) Lr = new ArrayList<>(ism.getLore());
        else {
            Lr = new ArrayList<>();
            Lr.add("line0");
        }
        line--;
        if(!(line>-1 && line<Lr.size())) return this;
        Lr.set(line, lr);
        ism.setLore(Lr);
        setMeta();
        return this;
    }
    
    public IsBuilder setLore(ArrayList<String> lr){
        ism.setLore(lr);
        setMeta();
        return this;
    }
    public IsBuilder addLore(ArrayList<String> lr){
        if(!ism.hasLore()) return setLore(lr);
        ArrayList<String> Lr = new ArrayList<>(ism.getLore());
        Lr.addAll(lr);
        return setLore(Lr);
    }
    public IsBuilder addLore(int line, String linha){
        if(!ism.hasLore()) return setLore(1, linha);
        line--;
        if(!(line>-1 && line<ism.getLore().size())) return this;
        ArrayList<String> Lr = new ArrayList<>();
        for(int i=0;i<ism.getLore().size();i++){
            if(i==line) Lr.add(linha);
            Lr.add(ism.getLore().get(i));
        }
        return setLore(Lr);
    }
    public IsBuilder setLore(String... lr){
        return setLore(new ArrayList<>(Arrays.asList(lr)));
    }
    public IsBuilder addLore(String... lr){
        return addLore(new ArrayList<>(Arrays.asList(lr)));
    }
    public IsBuilder addLore(String lr){
        ArrayList<String> Lr;
        if(ism.hasLore()) Lr = new ArrayList<>(ism.getLore());
        else Lr = new ArrayList<>();
        Lr.add(lr);
        ism.setLore(Lr);
        setMeta();
        return this;
    }
    
    public IsBuilder remLore(int... lines){
        if(!ism.hasLore()) return this;
        ArrayList<String> lr = new ArrayList<>(ism.getLore());
        for(int line:lines){
            line--;
            if(line>-1 && line<lr.size()) lr.remove(line);
        }
        return setLore(lr);
    }
    public IsBuilder remLastLore(){
        return remLore(ism.getLore().size());
    }
    
    public boolean hasName(){
        return getName().length()>0;
    }
    
    public String getName(){
        if(ism.hasDisplayName()) return is.getItemMeta().getDisplayName();
        else return "";
    }
    
    public ArrayList<String> getLore(){
        return new ArrayList<>(ism.getLore());
    }
    
    public static boolean isAir(ItemStack is){
        return is==null || is.getType().toString().contains("AIR");
    }
    
    public boolean isAir(){
        return is==null || is.getType().toString().contains("AIR");
    }
    
    public static boolean compareTo(ItemStack is, ItemStack is2){
        if(isAir(is) || isAir(is2)) return false;
        ItemStack IS1 = is;
        ItemStack IS2 = is2.clone();
        IS2.setAmount(IS1.getAmount());
        return IS1.equals(IS2);
    }
    
    public boolean compareTo(ItemStack is2){
        ItemStack IS1 = getItemStack();
        ItemStack IS2 = is2.clone();
        IS1.setAmount(IS2.getAmount());
        return IS1.equals(IS2);
    }
    
    public IsBuilder addTag(NBTTagCompound tag){
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        NBTTagCompound Tag = nmsIs.hasTag() ? nmsIs.getTag() : new NBTTagCompound();
        Tag = Tag.a(tag);
        nmsIs.setTag(Tag);
        is = nmsIs.asBukkitCopy();
        return this;
    }
    
    public boolean hasTag(){
        return getTag()!=null;
    }
    
    public NBTTagCompound getTag(){
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        if(nmsIs.hasTag()) return nmsIs.getTag();
        return null;
    }
    
    public static ItemStack[] separeInAmount(ItemStack is, int amount){
        if(amount<is.getAmount() && amount>0){
            is.setAmount(is.getAmount()-amount);
            ItemStack is2 = is.clone();
            is2.setAmount(amount);
            return new ItemStack[]{is2, is};
        }else return new ItemStack[]{is, null};
    }
    public ItemStack[] separeInAmount(int amount){
        if(amount<is.getAmount() && amount>0){
            is.setAmount(is.getAmount()-amount);
            ItemStack is2 = is.clone();
            is2.setAmount(amount);
            return new ItemStack[]{is2, is};
        }else return new ItemStack[]{is, null};
    }
    
    public ItemStack clone(){
        return clone(is);
    }
    
    public static ItemStack clone(ItemStack is){
        IsBuilder ib = new IsBuilder().newItem(is.getType(), 0, is.getAmount());
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
        if(nmsIs.hasTag()) ib.addTag(nmsIs.getTag());
        return ib.getItemStack();
    }
    
}
