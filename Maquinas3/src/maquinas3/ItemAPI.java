package maquinas3;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemAPI {

    public ItemStack setName(ItemStack item, String name){
        ItemMeta itemm=item.getItemMeta();
        itemm.setDisplayName(name);
        item.setItemMeta(itemm);
        return item;
    }
    
    public ItemStack remLastName(ItemStack item){
        String[] name = item.getItemMeta().getDisplayName().split(" ");
        ArrayList<String> Name = new ArrayList<>();
        String nome = "";
        for(String a:name){
            Name.add(a);
        }
        Name.remove(Name.size()-1);
        for(int i=0; i<Name.size(); i++){
            nome += Name.get(i);
            if(i != Name.size()-1){
                nome += " ";
            }
        }
        return setName(item, nome);
    }
    
    public ItemStack setLore(ItemStack item, String... lrs){
        ItemMeta itemm=item.getItemMeta();
        ArrayList<String> lr = new ArrayList<>();
        for(String name:lrs){
            lr.add(name);
        }
        itemm.setLore(lr);
        item.setItemMeta(itemm);
        return item;
    }
    
    public ItemStack setLore(ItemStack item, int line, String lore){
        ItemMeta itemm=item.getItemMeta();
        ArrayList<String> lr = new ArrayList<>();
        lr = getLore(itemm, lr);
        if(lr.size()>=line){
            line--;
            lr.set(line, lore);
            itemm.setLore(lr);
            item.setItemMeta(itemm);
        }
        return item;
    }
    
    public ItemStack addLore(ItemStack item, String... lrs){
        ItemMeta itemm=item.getItemMeta();
        ArrayList<String> lr = new ArrayList<>();
        lr = getLore(itemm, lr);
        for(String name:lrs){
            lr.add(name);
        }
        itemm.setLore(lr);
        item.setItemMeta(itemm);
        return item;
    }
    
    public ItemStack remLore(ItemStack item, int index){
        ItemMeta itemm = item.getItemMeta();
        ArrayList<String> lr = new ArrayList<>();
        lr = getLore(itemm, lr);
        lr.remove(index);
        itemm.setLore(lr);
        item.setItemMeta(itemm);
        return item;
    }
    
    public ItemStack remLastLore(ItemStack item){
        return remLore(item, item.getItemMeta().getLore().size()-1);
    }
    
    public ArrayList<String> getLore(ItemMeta itemm, ArrayList<String> lr){
        try{
            for(String a:itemm.getLore()){
                lr.add(a);
            }
        }catch(Exception e){}
        return lr;
    }
}
