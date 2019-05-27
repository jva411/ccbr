package itemapi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
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
        if(!isAir(is)) this.ism = is.getItemMeta();
    }
    public IsBuilder newItem(Material mtrl, int amount){
        is = new ItemStack(mtrl, amount);
        if(!isAir(is)) ism = is.getItemMeta();
        return this;
    }
    public IsBuilder newItem(Material mtrl){
        return newItem(mtrl, 1);
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
        sb.append(getName()).append(' ');
        sb.append(add[0]);
        for(String a:add) sb.append(' ').append(a);
        ism.setDisplayName(sb.toString());
        setMeta();
        return this;
    }
    
    public IsBuilder remLastName(){
        StringBuilder sb = new StringBuilder();
        String[] bits = getName().split(" ");
        if(bits.length<1) return this;
        sb.append(bits[0]);
        for(int i=1;i<bits.length-2;i++) sb.append(' ').append(bits[i]);
        setMeta();
        return this;
    }
    
    public IsBuilder setLore(int line, String lr){
        ArrayList<String> Lr;
        if(ism.hasLore()) Lr = new ArrayList<>(ism.getLore());
        else Lr = new ArrayList<>();
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
    
    public String getName(){
        if(ism.hasDisplayName()) return is.getItemMeta().getDisplayName();
        else return "";
    }
    
    public ArrayList<String> getLore(){
        return new ArrayList<>(ism.getLore());
    }
    
    public boolean isAir(){
        return is==null || is.getType().toString().contains("AIR");
    }
    
    public static boolean isAir(ItemStack is){
        return is==null || is.getType().toString().contains("AIR");
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
        Tag.a(tag);
        nmsIs.setTag(Tag);
        is = nmsIs.asBukkitCopy();
        return this;
    }
    
    public IsBuilder addFlag(ItemFlag... ifs){
        ism.addItemFlags(ifs);
        setMeta();
        return this;
    }
    
    public IsBuilder addEnch(Enchantment ench, boolean unsafe){
        return addEnch(ench, 1, unsafe);
    }
    
    public IsBuilder addEnch(Enchantment ench, int lvl, boolean unsafe){
        ism.addEnchant(ench, lvl, unsafe);
        setMeta();
        return this;
    }
    
    /**
     * Separa algum item não vazio em 2 itens
     * @param amount - Quantidade para retirar
     * @return ItemStack[2] onde ItemStack[0] é o item com "amount" e o ItemStack[1] é o item com o resto!
     * 
     * (Pode retornar nulo em [0] caso "amount==0" ou em [1] caso "amount>=item.getAmount()" )
     */
    public ItemStack[] separeInAmount(int amount){
        ItemStack is = getItemStack();
        if(amount<is.getAmount() && amount>0){
            is.setAmount(is.getAmount()-amount);
            ItemStack is2 = is.clone();
            is2.setAmount(amount);
            return new ItemStack[]{is2, is};
        }else return new ItemStack[]{is, null};
    }
    
}
