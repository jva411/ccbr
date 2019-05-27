package maquinas3;

import org.bukkit.inventory.ItemStack;

public class Combustivel {
    
    private ItemStack combustivel;
    private double multiplier;
    private int time;

    public Combustivel(ItemStack combustivel, double multiplier, int time) {
        this.combustivel = combustivel;
        this.multiplier = multiplier;
        this.time = time;
    }

    public ItemStack getCombustivel() {
        return combustivel.clone();
    }
    public double getMultiplier() {
        return multiplier;
    }
    public int getTime() {
        return time;
    }
}
