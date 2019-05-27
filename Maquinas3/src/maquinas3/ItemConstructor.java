package maquinas3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemConstructor {
    
    public HashMap<String, Enchantment> enchs=new HashMap<>();
    
    public ItemStack getItem(String linha){
        String[] A=linha.split(" ");
        int id=0;
        byte data=0;
        int amount=0;
        try{
            id=Integer.parseInt(A[0].split("-")[0]);
            amount=Integer.parseInt(A[1]);
            data=Byte.parseByte(A[0].split("-")[1]);
        }catch(Exception e){}
        ItemStack it=new ItemStack(id, amount, (short)0, data);
        ItemMeta itm=it.getItemMeta();
        try{
            for(int i=2;i<A.length;i++){
                if(A[i].equals("splash")){
                    Potion pot = Potion.fromItemStack(it);
                    it = pot.splash().toItemStack(it.getAmount());
                }
            }
        }catch(Exception e){}
        for(int i=2;i<A.length;i++){
            String[] b=A[i].split(":");
            String B=b[0].toLowerCase();
            if(B.equals("name")){
                try{
                    itm.setDisplayName(b[1].replace("&", "§").replace("_N", " "));
                    if(b.length>2) for(int I=2;I<b.length;I++) itm.setDisplayName(itm.getDisplayName()+":"+
                                b[I].replace("&", "§").replace("_N", " "));
                }catch(Exception e){}
            }
            if(B.equals("lore")){
                try{
                    ArrayList<String> lr=new ArrayList<>();
                    String lore = b[1].replace("&", "§");
                    if(b.length>2) for(int o=2;o<b.length;o++) lore += ":"+b[o].replace("&", "§");
                    for(String LR:lore.split("/N")) lr.add(LR.replace("_N", " ").replace("&", "§"));
                    itm.setLore(lr);
                }catch(Exception e){}
            }
            if(B.equals("enchhide")){
                itm.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        it.setItemMeta(itm);
        for(int i=2;i<A.length;i++){
            String[] b=A[i].split(":");
            String B=b[0].toLowerCase();
            try{
                int lv=Integer.parseInt(b[1]);
                it.addUnsafeEnchantment(enchs.get(B), lv);
                Enchantment.values();
            }catch(Exception e){}
        }
        for(int i=2;i<A.length;i++){
            String[] b=A[i].split(":");
            String B=b[0].toUpperCase();
            try{
                String[] c = b[1].split(",");
                int pwr = Integer.parseInt(c[0]);
                int dur = Integer.parseInt(c[1])*20;
                PotionMeta pm = (PotionMeta)it.getItemMeta();
                pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(B), dur, pwr), true);
                it.setItemMeta(pm);
            }catch(Exception e){}
        }
        return it;
    }
    
    public String getString(ItemStack item){
        String retorno="";
        retorno+=item.getTypeId();
        try{
            if(item.getData().getData()>0) {
                retorno+="-"+item.getData().getData();
            }
        }catch(Exception e){}
        retorno+=" "+item.getAmount();
        ItemMeta itemm=item.getItemMeta();
        try{
            String name=itemm.getDisplayName();
        if(name!=null) retorno+=" name:"+name.replace(" ", "_N").replace("§", "&");
        }catch(Exception e){}
        try{
            List<String> lr=itemm.getLore();
            int I=lr.size();
            if(I>0) retorno+=" lore:";
            for(int i=0;i<I;i++){
                if(i==(I-1)){
                    retorno+=lr.get(i).replace("§", "&").replace(" ", "_N");
                }else{
                    retorno+=lr.get(i).replace("§", "&").replace(" ", "_N")+"/N";
                }
            }
        }catch(Exception e){}
        try{
            for(Enchantment enc:item.getEnchantments().keySet()){
                String key="";
                for(String Key:enchs.keySet()){
                    if(enchs.get(Key)==enc) {
                        key=Key;
                        break;
                    }
                }
                retorno+=" "+key+":"+item.getEnchantmentLevel(enc);
            }
        }catch(Exception e){}
        try{
            if(itemm.hasItemFlag(ItemFlag.HIDE_ENCHANTS)){
                retorno+=" enchhide";
            }
        }catch(Exception e){}
        try{
            PotionMeta pm = (PotionMeta)item.getItemMeta();
            Potion pot = Potion.fromItemStack(item);
            if(pot.isSplash()) retorno+=" splash";
            for(PotionEffect pf : pm.getCustomEffects()){
                int pwr = pf.getAmplifier();
                int dur = pf.getDuration()/20;
                String name = pf.getType().getName();
                retorno+=" "+name+":"+pwr+","+dur;
            }
        }catch(Exception e){}
        return retorno;
    }
    
    public void saveEnchs(){
        enchs.put("power", Enchantment.getByName("ARROW_DAMAGE"));
        enchs.put("flame", Enchantment.getByName("ARROW_FIRE"));
        enchs.put("infinity", Enchantment.getByName("ARROW_INFINITE"));
        enchs.put("punch", Enchantment.getByName("ARROW_KNOCKBACK"));
        enchs.put("sharpness", Enchantment.getByName("DAMAGE_ALL"));
        enchs.put("arthropods", Enchantment.getByName("DAMAGE_ARTHROPODS"));
        enchs.put("smite", Enchantment.getByName("DAMAGE_UNDEAD"));
        enchs.put("depthstrider", Enchantment.getByName("DEPTH_STRIDER"));
        enchs.put("efficiency", Enchantment.getByName("DIG_SPEED"));
        enchs.put("unbreaking", Enchantment.getByName("DURABILITY"));
        enchs.put("fireaspect", Enchantment.getByName("FIRE_ASPECT"));
        enchs.put("knockback", Enchantment.getByName("KNOCKBACK"));
        enchs.put("fortune", Enchantment.getByName("LOOT_BONUS_BLOCKS"));
        enchs.put("looting", Enchantment.getByName("LOOT_BONUS_MOBS"));
        enchs.put("luck", Enchantment.getByName("LUCK"));
        enchs.put("lure", Enchantment.getByName("LURE"));
        enchs.put("respiration", Enchantment.getByName("OXYGEN"));
        enchs.put("protection", Enchantment.getByName("PROTECTION_ENVIRONMENTAL"));
        enchs.put("protectionexplosion", Enchantment.getByName("PROTECTION_EXPLOSIONS"));
        enchs.put("featherfalling", Enchantment.getByName("PROTECTION_FALL"));
        enchs.put("protectionfire", Enchantment.getByName("PROTECTION_FIRE"));
        enchs.put("protectionprojectile", Enchantment.getByName("PROTECTION_PROJECTILE"));
        enchs.put("silktouch", Enchantment.getByName("SILK_TOUCH"));
        enchs.put("thorns", Enchantment.getByName("THORNS"));
        enchs.put("aquaaffinity", Enchantment.getByName("WATER_WORKER"));
    }
    
}
