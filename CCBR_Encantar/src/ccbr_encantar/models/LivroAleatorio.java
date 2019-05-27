package ccbr_encantar.models;

import ccbr_encantar.utils.API;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LivroAleatorio {
    
    private ArrayList<Enchantment> Enchs = new ArrayList<>(Arrays.asList(new Enchantment[]{Enchantment.ARROW_DAMAGE,
        Enchantment.ARROW_FIRE, Enchantment.ARROW_INFINITE, Enchantment.ARROW_KNOCKBACK, Enchantment.DAMAGE_ALL, 
        Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD, Enchantment.DEPTH_STRIDER, Enchantment.DEPTH_STRIDER,
        Enchantment.DIG_SPEED, Enchantment.DURABILITY, Enchantment.FIRE_ASPECT, Enchantment.KNOCKBACK, Enchantment.LOOT_BONUS_BLOCKS,
        Enchantment.LOOT_BONUS_MOBS, Enchantment.LUCK, Enchantment.LURE, Enchantment.OXYGEN, Enchantment.PROTECTION_ENVIRONMENTAL,
        Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE,
        Enchantment.SILK_TOUCH, Enchantment.THORNS, Enchantment.WATER_WORKER}));
    
    public static LivroAleatorio BASICO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§3Básico§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3I, II§6)",
            "§5-=-=-=-=-=-=-=-=-=-=-=-="), API.Config.getInt("Preços.RandomBook.Básico"));
    public static LivroAleatorio MEDIO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§6Médio§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3III, IV§6)",
            "§5-=-=-=-=-=-=-=-=-=-=-=-="), API.Config.getInt("Preços.RandomBook.Médio"));
    public static LivroAleatorio AVANÇADO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§bAvançado§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3V, VI§6)",
            "§5-=-=-=-=-=-=-=-=-=-=-=-="), API.Config.getInt("Preços.RandomBook.Avançado"));
    public static LivroAleatorio RARO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§eRaro§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3VII, VIII§6)",
            "§5-=-=-=-=-=-=-=-=-=-=-=-="), API.Config.getInt("Preços.RandomBook.Raro"));
    public static LivroAleatorio EPICO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§4ÉPICO§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3IX, X§6)",
            "§5-=-=-=-=-=-=-=-=-=- =-=-="), API.Config.getInt("Preços.RandomBook.Épico"));
    public static LivroAleatorio LENDARIO = new LivroAleatorio(API.Iapi.addLore(API.Iapi.setName(API.Iapi.newItem(403), "§a§lLivro de Encantamento"),
            "§5-=-=-=-=§6(§dLENDÁRIO§6)§5=-=-=-=-",
            "§eCusto: §2§l999 XP",
            "§eNíveis: §6(§3XI, XII, XIII§6)",
            "§5-=-=-=-=-=-=-=-=-=- =-=-="), API.Config.getInt("Preços.RandomBook.Lendário"));
    
    private ItemStack Livro;
    private int exp;

    public LivroAleatorio(ItemStack Livro, int exp) {
        this.Livro = Livro;
        this.exp = exp;
    }

    public ItemStack getLivro() {
        return Livro.clone();
    }

    public int getExp() {
        return exp;
    }
    
    public LivroDeEncantamento open(){
        Random rd = new Random();
        Enchantment ench = Enchs.get(rd.nextInt(Enchs.size()));
        int lvl = 0;
        if(Livro.equals(BASICO.getLivro())){
            lvl = rd.nextInt(2)+1;
        }else if(Livro.equals(MEDIO.getLivro())){
            lvl = rd.nextInt(2)+3;
        }else if(Livro.equals(AVANÇADO.getLivro())){
            lvl = rd.nextInt(2)+5;
        }else if(Livro.equals(RARO.getLivro())){
            lvl = rd.nextInt(2)+7;
        }else if(Livro.equals(EPICO.getLivro())){
            lvl = rd.nextInt(2)+9;
        }else if(Livro.equals(LENDARIO.getLivro())){
            lvl = rd.nextInt(3)+11;
        }
        ArrayList<Enchantment> enchs = new ArrayList<>();
        enchs.add(ench);
//        for(Enchantment Ench:Enchs){
//            if(!enchs.contains(Ench)){
//                if(rd.nextDouble()*100<2/Math.pow(2, enchs.size()-1)){
//                    enchs.add(Ench);
//                }
//            }
//        }
        return new LivroDeEncantamento((Enchantment[])enchs.toArray(), lvl);
    }
    
}