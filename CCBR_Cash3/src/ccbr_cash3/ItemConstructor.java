package ccbr_cash3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemConstructor {
    
    ItemAPI Iapi = new ItemAPI();
    StringBuilder sb;
    Pattern p = Pattern.compile("\\[(.*?)\\]");
    Matcher m;
    HashMap<String, Enchantment> enchs = new HashMap<>();
    
    public ItemStack getItem(String line){
        sb = new StringBuilder();
        String[] bits = line.split(" ");
        int id = 0, data = 0, amount = 0;
        try{
            id = Integer.parseInt(bits[0].split("-")[0]);
            amount = Integer.parseInt(bits[1]);
            data = Integer.parseInt(bits[0].split("-")[1]);
        }catch(Exception e){}
        ItemStack is = new ItemStack(getMaterialById(id), amount, (short)0, (byte)data);
        ItemMeta im = is.getItemMeta();
        for(int i=0;i<bits.length;i++) if(i>1) sb.append(bits[i]).append(" ");
        line = sb.toString().trim().replace('&', '§');
        m = p.matcher(line);
        try{
            while(m.find()) if(starts(m.group(1), "splash")) is = Potion.fromItemStack(is).splash().toItemStack(is.getAmount());
        }catch(Exception e){}
        m = p.matcher(line);
        while(m.find()){
            String a = m.group(1);
            if(starts(a, "name=")) Iapi.setName(is, a.substring(5));
            else if(starts(a, "lore=")){
                String b = a.substring(5);
                ArrayList<String> lr = new ArrayList<>(Arrays.asList(b.split("/N")));
                Iapi.setLore(is, lr);
            }else if(starts(a, "enchhide")) im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            im = is.getItemMeta();
        }
        m = p.matcher(line);
        while(m.find()){
            String a = m.group(1).toLowerCase();
            String[] b = a.split("=");
            try{
                Enchantment enc = enchs.getOrDefault(b[0], null);
                if(enc==null) throw new Exception();
                int lv = Integer.parseInt(b[1]);
                is.addUnsafeEnchantment(enc, lv);
            }catch(Exception e){
                try{
                    String B = b[0].toUpperCase();
                    String[] c = b[1].split(",");
                    int pwr = Integer.parseInt(c[0]),
                            dur = Integer.parseInt(c[1])*20;
                    PotionMeta pm = (PotionMeta)im;
                    pm.addCustomEffect(new PotionEffect(PotionEffectType.getByName(B), dur, pwr), true);
                    is.setItemMeta(im);
                }catch(Exception E){}
            }
        }
        return is;
    }
    
    
    
    
    public String getString(ItemStack is){
        sb = new StringBuilder();
        ItemMeta im = is.getItemMeta();
        sb.append(is.getType().getId());
        try{
            byte b = is.getData().getData();
            if(b>0) sb.append('-').append(is.getData().getData());
        }catch(Exception e){}
        sb.append(' ').append(is.getAmount());
        if(im.hasDisplayName()) sb.append(" [name=").append(im.getDisplayName()).append(']');
        if(im.hasLore()) sb.append(" [lore=").append(StringUtils.join(im.getLore(), "/N")).append(']');
        if(is.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) sb.append(" enchhide");
        for(Map.Entry<Enchantment, Integer> enc:is.getEnchantments().entrySet()) 
            sb.append(" [").append(getName(enc.getKey())).append('=').append(enc.getValue()).append(']');
        try{
            PotionMeta pm = (PotionMeta)im;
            Potion pot = Potion.fromItemStack(is);
            if(pot.isSplash()) sb.append(" [splash]");
            for(PotionEffect pf : pm.getCustomEffects()){
                int pwr = pf.getAmplifier();
                int dur = pf.getDuration()/20;
                String name = pf.getType().getName();
                sb.append(" [").append(name).append('=').append(pwr).append(',').append(dur).append(']');
            }
        }catch(Exception e){}
        return sb.toString().trim().replace('§', '&');
    }
    
    
    
    
    
    
    public boolean starts(String str, String find){
        return StringUtils.startsWithIgnoreCase(str, find);
    }
    
    public String getName(Enchantment enc){
        for(Map.Entry<String, Enchantment> encs:enchs.entrySet()) if(encs.getValue().equals(enc)) return encs.getKey();
        return "";
    }
    
    public Material getMaterialById(int id){
        for(Material mtrl:Material.values()) if(id==mtrl.getId()) return mtrl;
        return Material.AIR;
    }
    
    public void saveEnchs(){
        enchs.put("Power", Enchantment.ARROW_DAMAGE);
        enchs.put("Flame", Enchantment.ARROW_FIRE);
        enchs.put("Infinity", Enchantment.ARROW_INFINITE);
        enchs.put("Punch", Enchantment.ARROW_KNOCKBACK);
        enchs.put("Sharpness", Enchantment.DAMAGE_ALL);
        enchs.put("Arthropods", Enchantment.DAMAGE_ARTHROPODS);
        enchs.put("Smite", Enchantment.DAMAGE_UNDEAD);
        enchs.put("Depthstrider", Enchantment.DEPTH_STRIDER);
        enchs.put("Efficiency", Enchantment.DIG_SPEED);
        enchs.put("Unbreaking", Enchantment.DURABILITY);
        enchs.put("Fireaspect", Enchantment.FIRE_ASPECT);
        enchs.put("Knockback", Enchantment.KNOCKBACK);
        enchs.put("Fortune", Enchantment.LOOT_BONUS_BLOCKS);
        enchs.put("Looting", Enchantment.LOOT_BONUS_MOBS);
        enchs.put("Luck", Enchantment.LUCK);
        enchs.put("Lure", Enchantment.LURE);
        enchs.put("Respiration", Enchantment.OXYGEN);
        enchs.put("Protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchs.put("Protectionexplosion", Enchantment.PROTECTION_EXPLOSIONS);
        enchs.put("Featherfalling", Enchantment.PROTECTION_FALL);
        enchs.put("Protectionfire", Enchantment.PROTECTION_FIRE);
        enchs.put("Protectionprojectile", Enchantment.PROTECTION_PROJECTILE);
        enchs.put("Silktouch", Enchantment.SILK_TOUCH);
        enchs.put("Thorns", Enchantment.THORNS);
        enchs.put("Aquaaffinity", Enchantment.WATER_WORKER);
    }
    
}
