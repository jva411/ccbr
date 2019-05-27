package metas.rewards;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class Rewards {
    
    private ArrayList<ItemStack> Items;
    private ArrayList<String> Commands;

    public Rewards(ArrayList<ItemStack> Items, ArrayList<String> Commands) {
        this.Items = Items;
        this.Commands = Commands;
    }

    public ArrayList<String> getCommands() {
        return Commands;
    }

    public ArrayList<ItemStack> getItems() {
        return Items;
    }
    
    
    
}
