package ccbr_encantar.utils;

import ccbr_cash3.Loja;
import ccbr_cash3.Main;
import ccbr_encantar.CCBR_Encantar;
import ccbr_encantar.Config;
import ccbr_encantar.models.Amuleto;
import ccbr_encantar.models.EnchantArea;
import ccbr_encantar.models.LivroAleatorio;
import ccbr_encantar.utils.exceptions.IlegalItemFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class API {
    
    public static Main ccbr_cash;
    public static ItemAPI Iapi;
    public static HashMap<String, Enchantment> enchs;
    public static HashMap<Player, EnchantArea> encantando;
    public static Config Config;
    public CCBR_Encantar Plugin;
    public Inventory Menu;

    public API(CCBR_Encantar Plugin) {
        this.Plugin = Plugin;
        Config = new Config(Plugin, "config.yml");
        Iapi = new ItemAPI();
        Plugin pl = Plugin.getServer().getPluginManager().getPlugin("CCBR_Cash");
        if(pl instanceof Main) ccbr_cash = (Main)pl;
        saveMenu();
        enchs = new HashMap<>();
        encantando = new HashMap<>();
        saveEnchs();
    }
    
    public void openAmuletShop(Player p){
        String[] arr = Config.getString("AmuletoShop").split("/");
        Loja loja = ccbr_cash.api.menu;
        l12pp34:
        for(int i=0;i<arr.length;i++){
            String a = arr[i];
            l12cc232:
            while(true){
                for(Loja Loja:loja.getLojas().values()){
                    if(Loja.getName().equals(a)) {
                        loja = Loja;
                        break l12cc232;
                    }
                }
            Bukkit.getConsoleSender().sendMessage("§e§l[CCBR_Encantar]: §r§l[ERROR]: §fLoja de amuletos não encontrada!");
            return;
            }
            if(i<arr.length-1) continue l12pp34;
        }
        ccbr_cash.api.openLoja(p, loja);
    }
    
    public void saveMenu(){
        Menu = Bukkit.createInventory(null, 56, "§a§lCCBR§7§l_§e§lEncantar");
        ItemStack vv = Iapi.setName(Iapi.newItem(160, 5), "§a");
        ItemStack va = Iapi.setName(Iapi.newItem(160, 14), "§a");
        Menu.setItem(0, vv); Menu.setItem(4, vv); Menu.setItem(8, vv); Menu.setItem(18, vv); Menu.setItem(27, vv);
        Menu.setItem(26, vv); Menu.setItem(35, vv); Menu.setItem(45, vv); Menu.setItem(49, vv); Menu.setItem(53, vv);
        Menu.setItem(1, va); Menu.setItem(2, va); Menu.setItem(3, va); Menu.setItem(5, va); Menu.setItem(6, va);
        Menu.setItem(7, va); Menu.setItem(9, va); Menu.setItem(17, va); Menu.setItem(36, va); Menu.setItem(44, va);
        Menu.setItem(46, va); Menu.setItem(47, va); Menu.setItem(48, va); Menu.setItem(50, va); Menu.setItem(51, va);
        Menu.setItem(52, va);
        Menu.setItem(19, LivroAleatorio.BASICO.getLivro());
        Menu.setItem(20, LivroAleatorio.MEDIO.getLivro());
        Menu.setItem(21, LivroAleatorio.AVANÇADO.getLivro());
        Menu.setItem(28, LivroAleatorio.RARO.getLivro());
        Menu.setItem(29, LivroAleatorio.EPICO.getLivro());
        Menu.setItem(30, LivroAleatorio.LENDARIO.getLivro());
        Menu.setItem(32, Amuleto.Tier_1.getItem());
        Menu.setItem(33, Amuleto.Tier_2.getItem());
        Menu.setItem(34, Amuleto.Tier_3.getItem());
    }
    
    public void confirmaLivro(LivroAleatorio la, Player p){
        ItemStack is = la.getLivro();
        int exp = la.getExp();
        int n1 = (int)(Math.ceil((double)is.getMaxStackSize()/4d)), n2 = is.getMaxStackSize();
        Inventory inv = Bukkit.createInventory(null, 54, "§a§lCCBR§7§l_§e§lEncantar §b§lRandomBook");
        ItemStack mais1 = Iapi.setName(Iapi.newItem(160, 5), "§aAdicionar 1"),
                mais10 = Iapi.setName(Iapi.newItem(160, 5, n1), "§aAdicionar "+n1),
                mais64 = Iapi.setName(Iapi.newItem(160, 5, n2), "§aAdicionar "+n2);
        ItemStack menos1 = Iapi.setName(Iapi.newItem(160, 14, -1), "§cRemover 1"),
                menos10 = Iapi.setName(Iapi.newItem(160, 14, -n1), "§cRemover "+n1),
                menos64 = Iapi.setName(Iapi.newItem(160, 14, -n2), "§cRemover "+n2);
        ItemStack confirmar = Iapi.setName(Iapi.newItem(35, 5), "§a§lCONFIRMAR"),
                cancelar = Iapi.setName(Iapi.newItem(35, 14), "§c§lCANCELAR");
        ItemStack preço;
        preço = Iapi.setName(Iapi.newItem(339), "§e§l"+exp+" §a§lExp");
        inv.setItem(9, mais1);
        inv.setItem(10, mais10);
        inv.setItem(11, mais64);
        inv.setItem(17, menos1);
        inv.setItem(16, menos10);
        inv.setItem(15, menos64);
        inv.setItem(31, is);
        inv.setItem(47, cancelar);
        inv.setItem(51, confirmar);
        inv.setItem(49, preço);
        p.openInventory(inv);
    }
    
    public LivroAleatorio getLivroAleatorio(ItemStack Is){
        if(Iapi.isAir(Is)) return null;
        ItemStack is = Is.clone();
        is.setAmount(1);
        if(is.equals(LivroAleatorio.BASICO.getLivro())) return LivroAleatorio.BASICO;
        else if(is.equals(LivroAleatorio.MEDIO.getLivro())) return LivroAleatorio.MEDIO;
        else if(is.equals(LivroAleatorio.AVANÇADO.getLivro())) return LivroAleatorio.MEDIO;
        else if(is.equals(LivroAleatorio.RARO.getLivro())) return LivroAleatorio.MEDIO;
        else if(is.equals(LivroAleatorio.EPICO.getLivro())) return LivroAleatorio.MEDIO;
        else if(is.equals(LivroAleatorio.LENDARIO.getLivro())) return LivroAleatorio.MEDIO;
        return null;
    }
    
    public void darItem(Player p, ItemStack is){
        HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
        for(int i:itens.keySet()) {
            is.setAmount(i);
            Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            Item.setPickupDelay(20);
        }
    }
    
    public HashMap<Enchantment, Integer> getEnchs(ItemStack is) throws IlegalItemFormat{
        if(isEnchantedBook(is)){
            HashMap<Enchantment, Integer> enchs = new HashMap<>();
            ArrayList<String> lr = new ArrayList<>(is.getItemMeta().getLore());
            for(int i=1;i<lr.size()-2;i++){
                String[] args = lr.get(i).replace("§a§l", "").split(" ");
                enchs.put(this.enchs.get(args[1]), Integer.parseInt(args[2]));
            }
            return enchs;
        }else throw new IlegalItemFormat("Item isn't a EnchantedBook");
    }
    
    public static boolean isRandomBook(ItemStack is){
        if(is.getTypeId()==403){
            ItemMeta ism = is.getItemMeta();
            if(ism.hasDisplayName()){
                if(ism.getDisplayName().equals("§a§lLivro de Encantamento")){
                    if(ism.hasLore()){
                        ArrayList<String> lr = new ArrayList<>(ism.getLore());
                        if(lr.size()>3){
                            if(lr.get(0).startsWith("§5-=-=-=-=§6(")){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public static boolean isEnchantedBook(ItemStack is){
        if(isRandomBook(is)){
            ArrayList<String> lr = new ArrayList<>(is.getItemMeta().getLore());
            if(lr.get(2).startsWith("§eEncantamento: §a§l")){
                if(lr.get(lr.size()-2).startsWith("§eChance: §f§l")) return true;
            }
        }
        return false;
    }
    
    public static String getName(Enchantment enc){
        for(Map.Entry<String, Enchantment> encs:enchs.entrySet()) if(encs.getValue().equals(enc)) return encs.getKey();
        return "";
    }
    
    public void saveEnchs(){
        enchs.put("Power", Enchantment.ARROW_DAMAGE);
        enchs.put("Flame", Enchantment.ARROW_FIRE);
        enchs.put("Infinity", Enchantment.ARROW_INFINITE);
        enchs.put("Punch", Enchantment.ARROW_KNOCKBACK);
        enchs.put("Sharpness", Enchantment.DAMAGE_ALL);
        enchs.put("Arthropods", Enchantment.DAMAGE_ARTHROPODS);
        enchs.put("Smite", Enchantment.DAMAGE_UNDEAD);
        enchs.put("Depthstrider", Enchantment.DEPTH_STRIDER);
        enchs.put("Efficiency", Enchantment.DIG_SPEED);
        enchs.put("Unbreaking", Enchantment.DURABILITY);
        enchs.put("Fireaspect", Enchantment.FIRE_ASPECT);
        enchs.put("Knockback", Enchantment.KNOCKBACK);
        enchs.put("Fortune", Enchantment.LOOT_BONUS_BLOCKS);
        enchs.put("Looting", Enchantment.LOOT_BONUS_MOBS);
        enchs.put("Luck", Enchantment.LUCK);
        enchs.put("Lure", Enchantment.LURE);
        enchs.put("Respiration", Enchantment.OXYGEN);
        enchs.put("Protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchs.put("Protectionexplosion", Enchantment.PROTECTION_EXPLOSIONS);
        enchs.put("Featherfalling", Enchantment.PROTECTION_FALL);
        enchs.put("Protectionfire", Enchantment.PROTECTION_FIRE);
        enchs.put("Protectionprojectile", Enchantment.PROTECTION_PROJECTILE);
        enchs.put("Silktouch", Enchantment.SILK_TOUCH);
        enchs.put("Thorns", Enchantment.THORNS);
        enchs.put("Aquaaffinity", Enchantment.WATER_WORKER);
    }
    
}
