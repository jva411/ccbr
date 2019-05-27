package ccbr_cash3;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemAPI {
    
    StringBuilder sb;
    ItemMeta ism;
    
    public ItemStack newItem(Material mtrl, int data, int amount){
        return new ItemStack(mtrl, amount, (short)0, (byte)data);
    }
    public ItemStack newItem(Material mtrl, int data){
        return newItem(mtrl, data, 1);
    }
    public ItemStack newItem(Material mtrl){
        return newItem(mtrl, 0, 1);
    }
    
    public ItemStack setName(ItemStack is, String name){
        ism = is.getItemMeta();
        ism.setDisplayName(name);
        is.setItemMeta(ism);
        return is;
    }
    
    public ItemStack addName(ItemStack is, String... add){
        ism = is.getItemMeta();
        sb = new StringBuilder();
        sb.append(getName(is)).append(' ');
        for(String a:add) sb.append(a).append(' ');
        ism.setDisplayName(sb.toString().trim());
        is.setItemMeta(ism);
        return is;
    }
    
    public ItemStack remLastName(ItemStack is){
        ism = is.getItemMeta();
        sb = new StringBuilder();
        String[] bits = getName(is).split(" ");
        if(bits.length<1) return is;
        sb.append(bits[0]);
        for(int i=1;i<bits.length-2;i++) sb.append(' ').append(bits[i]);
        is.setItemMeta(ism);
        return is;
    }
    
    public ItemStack setLore(ItemStack is, int line, String lr){
        ism = is.getItemMeta();
        if(!ism.hasLore()) return is;
        ArrayList<String> Lr = new ArrayList<>(ism.getLore());
        line--;
        if(line<0 || line>=Lr.size()) return is;
        Lr.set(line, lr);
        ism.setLore(Lr);
        is.setItemMeta(ism);
        return is;
    }
    
    public ItemStack setLore(ItemStack is, ArrayList<String> lr){
        ism = is.getItemMeta();
        ism.setLore(lr);
        is.setItemMeta(ism);
        return is;
    }
    public ItemStack addLore(ItemStack is, ArrayList<String> lr){
        ism = is.getItemMeta();
        if(!ism.hasLore()) return setLore(is, lr);
        ArrayList<String> Lr = new ArrayList<>(ism.getLore());
        Lr.addAll(lr);
        return setLore(is, Lr);
    }
    public ItemStack setLore(ItemStack is, String... lr){
        return setLore(is, new ArrayList<>(Arrays.asList(lr)));
    }
    public ItemStack addLore(ItemStack is, String... lr){
        return addLore(is, new ArrayList<>(Arrays.asList(lr)));
    }
    
    public ItemStack remLore(ItemStack is, int... lines){
        ism = is.getItemMeta();
        ArrayList<String> lr = new ArrayList<>(ism.getLore());
        for(int line:lines){
            line--;
            if(!(line<0 || line>=lr.size())) lr.remove(line);
        }
        ism.setLore(lr);
        is.setItemMeta(ism);
        return is;
    }
    public ItemStack remLastLore(ItemStack is){
        return remLore(is, is.getItemMeta().getLore().size());
    }
    
    public String getName(ItemStack is){
        return is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : "";
    }
    
    public ArrayList<String> getLore(ItemStack is){
        return new ArrayList<>(is.getItemMeta().getLore());
    }
    
    public boolean isAir(ItemStack is){
        return is==null || is.getType()==Material.AIR;
    }
    
    public boolean isEquals(ItemStack is1, ItemStack is2){
        ItemStack IS1 = is1.clone(), IS2 = is2.clone();
        IS1.setAmount(IS2.getAmount());
        return IS1.equals(IS2);
    }
    
    /**
     * Separa algum item não vazio em 2 itens
     * @param is - Item para separar
     * @param amount - Quantidade para retirar
     * @return ItemStack[2] onde ItemStack[0] é o item com "amount" e o ItemStack[1] é o item com o resto!
     * 
     * (Pode retornar nulo em [0] caso "amount==0" ou em [1] caso "amount>=item.getAmount()" )
     */
    public ItemStack[] separeInAmount(ItemStack is, int amount){
        if(amount<is.getAmount()){
            is.setAmount(is.getAmount()-amount);
            ItemStack is2 = is.clone();
            is2.setAmount(amount);
            return new ItemStack[]{is2, is};
        }else return new ItemStack[]{is, null};
    }
    
}
