package trades;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Trader {
    
    private final Player Player;
    private double Money;
    private ArrayList<ItemStack> items;
    private boolean ready;

    public Trader(Player p){
        Player = p;
    }

    public ArrayList<ItemStack> getItems() {
        return items;
    }
    public double getMoney() {
        return Money;
    }
    public Player getPlayer() {
        return Player;
    }
    public boolean isReady() {
        return ready;
    }

    public void setItems(ArrayList<ItemStack> items) {
        this.items = items;
    }
    public void setMoney(double Money) {
        this.Money = Money;
    }
    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
