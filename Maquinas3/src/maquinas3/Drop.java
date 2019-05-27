package maquinas3;

import org.bukkit.inventory.ItemStack;

public class Drop {
    
    private ItemStack drop;

    public Drop(ItemStack drop) {
        this.drop = drop;
    }

    public ItemStack getDrop() {
        return drop.clone();
    }
    
    
}
