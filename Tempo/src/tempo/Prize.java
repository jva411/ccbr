package tempo;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Prize {
    
    private final int Time;
    private final ArrayList<ItemStack> Items;
    private final ArrayList<String> Commands;
    private final boolean Repeat;

    public Prize(int Time, ArrayList<ItemStack> Items, ArrayList<String> Commands, boolean Repeat) {
        this.Time = Time;
        this.Items = Items;
        this.Commands = Commands;
        this.Repeat = Repeat;
    }

    public ArrayList<String> getCommands() {
        return Commands;
    }

    public ArrayList<ItemStack> getItems() {
        return Items;
    }

    public int getTime() {
        return Time;
    }

    public boolean isRepeat() {
        return Repeat;
    }
    
    public void giveToPlayer(Player p){
        for(ItemStack is:Items) darItem(p, is);
        for(String a:Commands) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.replace("%p%", p.getName()).replace('&', '§'));
    }
    
    private void darItem(Player p, ItemStack is){
        HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
        for(int i:itens.keySet()) {
            is.setAmount(i);
            Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            Item.setPickupDelay(20);
        }
    }
    
}
