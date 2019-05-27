package ccbr_encantar.models;

import ccbr_encantar.utils.API;
import org.bukkit.inventory.ItemStack;

public class Amuleto {
    
    public static Amuleto Tier_1 = new Amuleto(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(385), "§6§kk§a§lAmuleto da Sorte§6§kk"),
            "§5-=-=-=§3Básico§5=-=-=-",
            "§eUse este item para",
            "§eaumentar em §a15% §ea",
            "§etaxa de sucesso do",
            "§eencantamento"), 0.15);
    public static Amuleto Tier_2 = new Amuleto(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(385), ""),
            "§5-=-=-=§6Médio§5=-=-=-",
            "§eUse este item para",
            "§eaumentar em §a30% §ea",
            "§etaxa de sucesso do",
            "§eencantamento"), 0.3);
    public static Amuleto Tier_3 = new Amuleto(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(385), ""),
            "§5-=-=-=§bAvançado§5=-=-=-",
            "§eUse este item para",
            "§eaumentar em §a50% §ea",
            "§etaxa de sucesso do",
            "§eencantamento"), 0.5);
    
    private ItemStack item;
    private double multiplier;
    
    private Amuleto(ItemStack item, double multiplier){
        this.item = item;
        this.multiplier = multiplier;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public double getMultiplier() {
        return multiplier;
    }
    
}
