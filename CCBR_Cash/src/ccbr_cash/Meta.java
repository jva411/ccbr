package ccbr_cash;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Meta {
    
    private Config Cfg;
    private int state;
    private Inventory Inv;
    private ArrayList<Prize> Prizes;
    private ItemConstructor Icons = new ItemConstructor();
    Prize p = new Prize(50);

    public Meta(Config cfg) {
        Prizes = new ArrayList<>();
        state = 0;
        loadMetas();
    }
    
    public void loadMetas(){
        Cfg = new Config(Api.Main, "config.yml");
        state = Cfg.getInt("Meta.estado");
        for(String a:Cfg.getConfig().getConfigurationSection("Meta.metas").getKeys(false)){
            try{
                int cash = Integer.parseInt(a);
                ArrayList<ItemStack> itemsForOne = new ArrayList<>(), itemsForAll = new ArrayList<>();
                ArrayList<String> cmdsForOne = new ArrayList<>(), cmdsForAll = new ArrayList<>();
                for(String b:Cfg.getConfig().getStringList("Meta.metas."+a+".itemsParaQuemBateu")) itemsForOne.add(Icons.getItem(b));
                for(String b:Cfg.getConfig().getStringList("Meta.metas."+a+".itemsParaTodos")) itemsForAll.add(Icons.getItem(b));
                for(String b:Cfg.getConfig().getStringList("Meta.metas."+a+".comandosParaQuemBateu")) cmdsForOne.add(b);
                for(String b:Cfg.getConfig().getStringList("Meta.metas."+a+".comandosParaTodos")) cmdsForAll.add(b);
                Prize p = new Prize(cash);
                p.CommandsForAll = cmdsForAll;
                p.CommandsForOne = cmdsForOne;
                p.ItemsForAll = itemsForAll;
                p.ItemsForOne = itemsForOne;
                p.knocked = cash<=state;
                Prizes.add(p);
            }catch(Exception ex){}
        }
        Inv = Bukkit.createInventory(null, Prizes.size()==0 ? 9 : 9*(int)Math.ceil((double)Prizes.size()/9d), "§a§lCCBR_Cash §e§lMetas");
        int i = 0;
        for(Prize p:Prizes){
            IsBuilder Ib = new IsBuilder().newItem(Material.EMERALD).setName("§a§l"+p.meta);
            if(p.knocked) Ib.setLore("§c§lEssa meta já foi batida!").addEnch(Enchantment.LUCK, true).addFlag(ItemFlag.HIDE_ENCHANTS);
            else Ib.setLore("§a§lFaltam §e§l"+(p.meta-state)+" §a§lcashs para bater essa meta!");
            Inv.setItem(i, Ib.getItemStack());
        }
    }
    
    public void add(Player P, int amount){
        state += amount<0 ? -amount : amount;
        Cfg.set("Meta.estado", state);
        Cfg.saveConfig();
        for(Prize p:Prizes){
            if(!p.knocked){
                if(state>=p.meta){
                    p.knocked = true;
                    for(ItemStack is:p.ItemsForOne) Api.darItem(P, is);
                    for(String a:p.CommandsForOne) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.replace("%p%", P.getName()));
                    for(ItemStack Is:p.ItemsForAll) for(Player P2:Bukkit.getOnlinePlayers()) Api.darItem(P2, Is);
                    for(String a:p.CommandsForAll) for(Player P2:Bukkit.getOnlinePlayers()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.replace("%p%", P2.getName()));
                    loadMetas();
                    return;
                }
            }
        }
    }
    
    
    public void openInv(Player p){
        p.openInventory(Inv);
    }
    
    private class Prize {
        
        private int meta;
        private boolean knocked;
        private ArrayList<ItemStack> ItemsForOne, ItemsForAll;
        private ArrayList<String> CommandsForOne, CommandsForAll;

        public Prize(int meta) {
            this.meta = meta;
            this.ItemsForOne = new ArrayList<>();
            this.ItemsForAll = new ArrayList<>();
            this.CommandsForOne = new ArrayList<>();
            this.CommandsForAll = new ArrayList<>();
            this.knocked = false;
        }
        
    }
    
}
