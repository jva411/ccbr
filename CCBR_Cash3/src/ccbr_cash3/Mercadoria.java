package ccbr_cash3;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class Mercadoria {
    
    private ItemStack icone;
    private double money;
    private int cash;
    private ArrayList<String> cmds;
    private ArrayList<ItemStack> items;
    private String permission;

    public Mercadoria(ItemStack icone, double money, int cash, ArrayList<String> cmds, ArrayList<ItemStack> items, String permission) {
        this.icone = icone;
        this.money = money;
        this.cash = cash;
        this.cmds = cmds;
        this.items = items;
        this.permission = "CCBR_Cash.shop."+permission;
    }
    
    public ItemStack getIcone() {
        return icone;
    }
    public double getMoney() {
        return money;
    }
    public int getCash() {
        return cash;
    }
    public ArrayList<String> getCmds() {
        return cmds;
    }
    public ArrayList<ItemStack> getItens() {
        return items;
    }
    public String getPermission() {
        return permission;
    }
    
}
