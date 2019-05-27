package maquinas3;

import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class MachineType {
    
    private ItemStack block;
    private ArrayList<Combustivel> combCompativeis;
    private Drop drop;
    private double multiplier;
    private String displayName;
    private int[] Upgrades;

    public MachineType(ItemStack block, ArrayList<Combustivel> combCompativeis, Drop drop, double multiplier, String displayName, int[] Upgrades) {
        this.block = block;
        this.combCompativeis = combCompativeis;
        this.drop = drop;
        this.multiplier = multiplier;
        this.displayName = displayName;
        this.Upgrades = Upgrades;
    }

    public ItemStack getBlock() {
        return block.clone();
    }
    public ArrayList<Combustivel> getCombCompativeis() {
        return combCompativeis;
    }
    public Drop getDrop() {
        return drop;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public String getDisplayName() {
        return displayName;
    }
    public int[] getUpgrades() {
        return Upgrades;
    }
    
}
