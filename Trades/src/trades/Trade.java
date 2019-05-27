package trades;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

public class Trade {
    
    private final Trader sender, recivier;
    private Inventory inv;

    public Trade(Trader sender, Trader recivier) {
        this.sender = sender;
        this.recivier = recivier;
        inv = Bukkit.createInventory(null, 54, "§aTrade");
        IsBuilder ib = new IsBuilder();
        ib.newItem(Material.RED_STAINED_GLASS_PANE).setName(" ");
    }
    
}
