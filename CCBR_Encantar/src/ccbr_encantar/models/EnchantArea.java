package ccbr_encantar.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class EnchantArea {
   
    private final Player player;
    private Inventory inv;

    public EnchantArea(Player player) {
        this.player = player;
        saveInv();
    }
    
    private void saveInv(){
        Inventory inv = Bukkit.createInventory(null, 27, "§e§lÁrea de Encantamento");
        
    }
    
}
