package me.jvagamer.cpu.com.component;

import me.jvagamer.cpu.api.IsBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum HD_Data {
    
    BASICO(4608, 36, "§bHD Básico"), MEDIO(9216, 72, "§bHD Médio"), AVANCADO(18432, 144, "§bHD Avançado"), HEXTEC(36964, 288, "§bHD HexTec");
    
    private int maxBytes;
    private int maxItens;
    private ItemStack HD;
    
    HD_Data(int maxBytes, int maxItens, String name) {
        this.maxBytes = maxBytes;
        this.maxItens = maxItens;
        IsBuilder ib = new IsBuilder();
        ib.newItem(Material.DIAMOND).setName(name).addLore("§6§oBytes: §f§l0/"+maxBytes, "§6§oItens: §f§l0/"+maxItens);
        HD = ib.getItemStack();
    }

    public int getMaxBytes() {
        return maxBytes;
    }

    public int getMaxItens() {
        return maxItens;
    }

    public ItemStack getHD() {
        return HD;
    }
    
}