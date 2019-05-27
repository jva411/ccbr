package me.jvagamer.cpu.com.component;

import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.api.ItemConstructor;
import me.jvagamer.cpu.com.Strings;
import me.jvagamer.cpu.com.SuperStack;
import java.util.ArrayList;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class HD extends Component{
    
    private static ItemConstructor Icons = new ItemConstructor();
    
    private HD_Data Data;
    private ItemStack HD;
    private ArrayList<SuperStack> SuperStacks;
    private int Bytes, Itens;
    
    public HD(HD_Data Data, ArrayList<SuperStack> SuperStacks) {
        this.Data = Data;
        this.SuperStacks = SuperStacks;
        Itens = 0;
        Bytes = 0;
        update();
    }
    
    private void setHD(){
        net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(Data.getHD());
        NBTTagCompound Tag = nmsIs.getTag();
        NBTTagCompound tag = Tag.getCompound(Strings.ComputerCraft);
        tag.setBoolean(Strings.isHD, true);
        tag.setInt(Strings.hdBytes, Bytes);
        tag.setInt(Strings.hdItens, Itens);
        tag.setString(Strings.hdType, Data.toString());
        NBTTagCompound Items = new NBTTagCompound();
        for(SuperStack sp:SuperStacks) Items.setInt(Icons.getString(sp.getItem()), sp.getAmount());
        tag.set(Strings.hdSsItem, Items);
        Tag.set(Strings.ComputerCraft, tag);
        nmsIs.setTag(Tag);
        IsBuilder ib = new IsBuilder(nmsIs.asBukkitCopy());
        ib.setLore("§6§oBytes: §f§l"+Bytes+"/"+Data.getMaxBytes(), "§6§oItens: §f§l"+Itens+"/"+Data.getMaxItens());
        HD = ib.getItemStack();
    }
    
    public void update(){
        Itens = 0;
        Bytes = 0;
        for(SuperStack sp:SuperStacks){
            Itens++;
            Bytes += (64/sp.getItem().getMaxStackSize())*2*sp.getAmount();
        }
        setHD();
    }
    
    public SuperStack addItem(SuperStack sp){
        return addItem(sp, sp.getAmount());
    }
    
    public SuperStack addItem(SuperStack sp, int amount){
        SuperStack sp2 = getSS(sp);
        if(getItens()==Data.getMaxItens() && sp2==null) return sp;
        int b = 128/sp.getItem().getMaxStackSize();
        int totalB = amount*b;
        int restoB = totalB+getBytes()>getData().getMaxBytes() ? totalB+getBytes()-getData().getMaxBytes() : 0;
        int daB = (totalB-restoB);
        int daI = daB/b;
        if(daI<1) return sp;
        sp.setAmount(sp.getAmount()-daI);
        if(sp2!=null) sp2.setAmount(sp2.getAmount()+daI);
        else SuperStacks.add(new SuperStack(sp.getItem(), daI));
        update();
        return sp;
    }
    
    public SuperStack remItem(SuperStack sp){
        return remItem(sp, sp.getAmount());
    }
    
    public SuperStack remItem(SuperStack sp, int amount){
        if(!contains(sp)) return sp;
        int b = 128/sp.getItem().getMaxStackSize();
        int totalB = b*amount;
        int temB = b*get(sp);
        int tiraB = totalB > temB ? temB : totalB;
        int tira = tiraB/b;
        SuperStack sp2 = getSS(sp);
        sp.setAmount(sp.getAmount()-tira);
        sp2.setAmount(sp2.getAmount()-tira);
        if(sp2.getAmount()<1) SuperStacks.remove(sp2);
        update();
        return sp;
    }
    
    public boolean contains(SuperStack sp){
        return getSS(sp)!=null;
    }
    
    public int get(SuperStack sp){
        for(SuperStack Sp:SuperStacks){
            if(IsBuilder.compareTo(Sp.getItem(), sp.getItem())){
                return Sp.getAmount();
            }
        }
        return 0;
    }
    
    public SuperStack getSS(SuperStack sp){
        for(SuperStack Sp:SuperStacks){
            if(IsBuilder.compareTo(Sp.getItem(), sp.getItem())){
                return Sp;
            }
        }
        return null;
    }
    
    public HD_Data getData() {
        return Data;
    }

    public ArrayList<SuperStack> getSuperStacks() {
        return SuperStacks;
    }

    public int getBytes() {
        return Bytes;
    }

    public int getItens() {
        return Itens;
    }
    
    public void setSuperStacks(ArrayList<SuperStack> SuperStacks) {
        this.SuperStacks = SuperStacks;
        update();
    }

    @Override
    public ItemStack getItemStack() {
        return HD;
    }
    
    public static boolean isHD(ItemStack is){
        if(!IsBuilder.isAir(is)){
            if(is.getType()==Material.DIAMOND){
                net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
                if(nmsIs.hasTag()){
                    if(nmsIs.getTag().hasKey(Strings.ComputerCraft)){
                        NBTTagCompound tag = nmsIs.getTag().getCompound(Strings.ComputerCraft);
                        if(tag.hasKey(Strings.isHD)){
                            return tag.getBoolean(Strings.isHD);
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static HD loadHD(ItemStack is){
        if(isHD(is)){
            NBTTagCompound tag = CraftItemStack.asNMSCopy(is).getTag().getCompound(Strings.ComputerCraft);
            NBTTagCompound Itens = tag.getCompound(Strings.hdSsItem);
            ArrayList<SuperStack> SSs = new ArrayList<>();
            for(String a:Itens.getKeys()){
                SSs.add(new SuperStack(Icons.getItem(a), Itens.getInt(a)));
            }
            HD hd = new HD(HD_Data.valueOf(tag.getString(Strings.hdType)), SSs);
            return hd;
        }
        return null;
    }
    
}
