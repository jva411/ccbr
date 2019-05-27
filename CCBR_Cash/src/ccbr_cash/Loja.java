package ccbr_cash;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Loja{
    
    private ItemStack icone;
    private String permission, name;
    private HashMap<Integer, ItemStack> enfeites;
    private HashMap<Integer, ItemStack> voltar;
    private HashMap<Integer, Mercadoria> mercadorias;
    private HashMap<Integer, Loja> lojas;
    private Inventory inventory;
    private Loja lojaMae;

    public Loja(ItemStack icone, String permission, String name, HashMap<Integer, ItemStack> enfeites, HashMap<Integer, Mercadoria> mercadorias, HashMap<Integer, Loja> lojas, HashMap<Integer, ItemStack> voltar) {
        this.icone = icone;
        this.permission = "CCBR_Cash.shop."+permission;
        this.name = name;
        this.enfeites = enfeites;
        this.mercadorias = mercadorias;
        this.lojas = lojas;
        this.voltar = voltar;
        
        Inventory inv;
        if(name.equals("MENU")) inv = Bukkit.createInventory(null, 54, "§a§lCCBR §4§lMENU");
        else inv = Bukkit.createInventory(null, 54, "§a§lCCBR §f"+icone.getItemMeta().getDisplayName());
        for(Map.Entry entry:enfeites.entrySet()) inv.setItem((int)entry.getKey(), (ItemStack)entry.getValue());
        for(Map.Entry entry:voltar.entrySet()) inv.setItem((int)entry.getKey(), (ItemStack)entry.getValue());
        for(Map.Entry entry:mercadorias.entrySet()) inv.setItem((int)entry.getKey(), ((Mercadoria)entry.getValue()).getIcone());
        for(Map.Entry entry:lojas.entrySet()) inv.setItem((int)entry.getKey(), ((Loja)entry.getValue()).getIcone());
        this.inventory = inv;
    }

    public ItemStack getIcone() {
        return icone;
    }

    public String getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, ItemStack> getEnfeites() {
        return enfeites;
    }

    public HashMap<Integer, Mercadoria> getMercadorias() {
        return mercadorias;
    }

    public HashMap<Integer, Loja> getLojas() {
        return lojas;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public HashMap<Integer, ItemStack> getVoltar() {
        return voltar;
    }

    public Loja getLojaMae() {
        return lojaMae;
    }

    public void setLojaMae(Loja lojaMae) {
        this.lojaMae = lojaMae;
    }
    
}
