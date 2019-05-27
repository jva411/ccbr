package me.jvagamer.cpu.com.inventory;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Page {
    
    public Inventory Inventory;
    public int Page;
    public ArrayList<Player> Viewers;

    public Page(int page, int size, String name) {
        this.Inventory = Bukkit.createInventory(null, size, name);
        this.Viewers = new ArrayList<>();
        this.Page = page;
    }
    
}
