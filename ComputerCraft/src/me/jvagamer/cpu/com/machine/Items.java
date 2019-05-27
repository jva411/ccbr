package me.jvagamer.cpu.com.machine;

import me.jvagamer.cpu.api.IsBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Items {
    
    CPU(new IsBuilder().newItem(Material.FURNACE).setName("§9ComputerCraft").getItemStack()),
    WRENCH(new IsBuilder().newItem(Material.BLAZE_ROD).setName("§eChave de fenda").getItemStack());
    
    public final ItemStack ItemStack;

    private Items(ItemStack ItemStack) {
        this.ItemStack = ItemStack;
    }
    
}
