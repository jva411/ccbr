package ccbr_encantar.models;

import ccbr_encantar.utils.API;
import java.util.Random;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LivroDeEncantamento {
    
    private ItemStack is;
    private Enchantment[] Enchs;
    private int lvl;

    public LivroDeEncantamento(Enchantment[] Enchs, int lvl) {
        this.Enchs = Enchs;
        this.lvl = lvl;
        setLivro();
    }

    public ItemStack getLivro() {
        return is;
    }
    
    private void setLivro(){
        this.is = API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
                "§5-=-=-=-=§6(§3Básico§6)§5=-=-=-=-",
                "§eEnriquecido: §b(0%)",
                "§eChance: §f§l"+getChance()+"%",
                "§5-=-=-=-=-=-=-=-=-=-=-=-=");
        setLore();
    }
    
    private void setLore(){
        for(Enchantment Ench:Enchs) is = API.Iapi.addLore(is, 2, "§eEncantamento: §a§l"+API.getName(Ench)+" "+lvl);
    }
    
    private double getChance(){
        Random rd = new Random();
        double variance = (rd.nextDouble()*4)-2;
        double chance = 0;
        if(lvl==1) chance = 97+variance;
        else if(lvl==2) chance = 93+variance;
        else if(lvl==3) chance = 88+variance;
        else if(lvl==4) chance = 82+variance;
        else if(lvl==5) chance = 75+variance;
        else if(lvl==6) chance = 67+variance;
        else if(lvl==7) chance = 59+variance;
        else if(lvl==8) chance = 50+variance;
        else if(lvl==9) chance = 40+variance;
        else if(lvl==10) chance = 29+variance;
        else if(lvl==11) chance = 16+variance;
        else if(lvl==12) chance = 7+variance;
        else chance = 4+variance;
        return chance;
    }
    
    public static int enriquecer(ItemStack is, int exp){
        if(API.isEnchantedBook(is)){
            String a = API.Iapi.getLore(is).get(1).substring(19).replace("%)", "");
            double chance = Double.parseDouble(a)*100, soma = exp*100;
            if(chance<10000){
                String b = API.Iapi.getLore(is).get(API.Iapi.getLore(is).size()-2).substring(15).replace("%", "");
                double chance2 = Double.parseDouble(b);
                if(chance+soma>10000){
                    API.Iapi.setLore(is, 2, "§eEnriquecido: §b("+((chance+soma)/100)+"%)");
                    chance2 += (soma/100)*0.025;
                    API.Iapi.setLore(is, API.Iapi.getLore(is).size()-1, "§eChance: §f§l"+chance2+"%");
                    return 0;
                }else{
                    API.Iapi.setLore(is, 2, "\"§eEnriquecido: §b(100%)");
                    int i = 100-(int)(chance+soma/100);
                    chance2 += ((soma/100)-i)*0.025;
                    API.Iapi.setLore(is, API.Iapi.getLore(is).size()-1, "§eChance: §f§l"+chance2+"%");
                    return i;
                }
            }
        }
        return exp;
    }
}