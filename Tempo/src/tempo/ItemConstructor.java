package tempo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import net.minecraft.server.v1_13_R2.MojangsonParser;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemConstructor {
    
    IsBuilder Ib;
    StringBuilder sb;
    
    private class MyPattern{
        
        private String line;
        private ArrayList<String> founds;
        
        public boolean find(){
            founds = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            boolean bln = false;
            int i = 0;
            for(char c:line.toCharArray()){
                if(c==']') {
                    if(i==1) {
                        bln = false;
                        founds.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    i--;
                }
                if(bln) sb.append(c);
                if(c=='[') {
                    bln = true;
                    i++;
                }
            }
            return founds.size()>0;
        }
        
        public MyPattern(String line) {
            this.line = line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String group(int i) {
            return founds.get(i);
        }
        
        public int lastGroup(){
            return founds.size();
        }
        
    }
    
    public ItemStack getItem(String line){
        sb = new StringBuilder();
        String[] bits = line.split(" ");
        String type = bits[0].split("-")[0].toUpperCase();
        int amount = 0;
        try{
            amount = Integer.parseInt(bits[1]);
        }catch(Exception e){}
        ItemStack is = new ItemStack(Material.valueOf(type), amount);
        Ib = new IsBuilder(is);
        for(int i=0;i<bits.length;i++) if(i>1) sb.append(bits[i]).append(' ');
        line = sb.toString().trim().replace('&', '§');
        MyPattern mp = new MyPattern(line);
        if(mp.find()){
            for(int i=0;i<mp.lastGroup();i++){
                String a = mp.group(i);
                if(starts(a, "name=")) Ib.setName(a.substring(5));
                else if(starts(a, "lore=")){
                    String b = a.substring(5);
                    ArrayList<String> lr = new ArrayList<>(Arrays.asList(b.split("/N")));
                    Ib.setLore(lr);
                }else if(starts(a, "enchhide")) Ib.addFlag(ItemFlag.HIDE_ENCHANTS);
                else if(starts(a, "tag=")) try{ Ib.addTag(MojangsonParser.parse(a.substring(4)));}catch(Exception e){}
                else{
                    try{
                        String[] b = a.split("=");
                        Enchantment enc = getByName(b[0]);
                        if(enc==null) throw new Exception();
                        int lv = Integer.parseInt(b[1]);
                        is.addUnsafeEnchantment(enc, lv);
                    }catch(Exception e){ }
                }
            }
        }
        is = Ib.getItemStack();
        return is;
    }
    
    
    
    public String getString(ItemStack is){
        sb = new StringBuilder();
        ItemMeta im = is.getItemMeta();
        sb.append(is.getType().toString());
        int damage = 0;
        try{
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
            if(nmsIs.hasTag()){
                if(nmsIs.getTag().hasKey("Damage")){
                    damage = nmsIs.getTag().getInt("Damage");
                }
            }
        }catch(Exception e){}
        if(damage>0) sb.append('-').append(damage);
        sb.append(' ').append(is.getAmount());
        if(im.hasDisplayName()) sb.append(" [name=").append(im.getDisplayName()).append(']');
        if(im.hasLore()) sb.append(" [lore=").append(StringUtils.join(im.getLore(), "/N")).append(']');
        if(is.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) sb.append(" enchhide");
        for(Map.Entry<Enchantment, Integer> enc:is.getEnchantments().entrySet()) 
            sb.append(" [").append(getName(enc.getKey())).append('=').append(enc.getValue()).append(']');
        try{
            net.minecraft.server.v1_13_R2.ItemStack nmsIs = CraftItemStack.asNMSCopy(is);
            if(nmsIs.hasTag()){
                NBTTagCompound tag = nmsIs.getTag();
                if(tag.hasKey("display")) tag.remove("display");
                if(tag.hasKey("Enchantments")) tag.remove("Enchantments");
                if(tag.hasKey("HideFlags")) tag.remove("HideFlags");
                if(tag.map.size()>0) sb.append(" [tag=").append(tag).append(']');
            }
        }catch(Exception e){}
        return sb.toString().trim().replace('§', '&');
    }
    
    
    
    
    
    
    private boolean starts(String str, String find){
        return StringUtils.startsWithIgnoreCase(str, find);
    }
    
    private String getName(Enchantment enc){
        return enc.toString().split(":")[1].split(",")[0].toUpperCase();
    }
    
    private Enchantment getByName(String name){
        for(Enchantment ench:Enchantment.values()){
            if(ench.toString().split(":")[1].split(",")[0].toUpperCase().equalsIgnoreCase(name)){
                return ench;
            }
        }
        return null;
    }
    
}