package metas;

import java.util.ArrayList;
import metas.apis.Config;
import java.util.HashMap;
import java.util.Set;
import metas.apis.ItemConstructor;
import metas.metas.CashBought;
import metas.metas.MetaGroup;
import metas.metas.MetaOneBody;
import metas.metas.Miner;
import metas.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Metas extends JavaPlugin implements Listener{
    
    public Config cfg = new Config(this, "config.yml");
    public HashMap<Player, Aspirante> aspirantes = new HashMap<>();
    public ArrayList<MetaGroup> MetasGroups = new ArrayList<>();
    public ArrayList<MetaOneBody> MetasOne = new ArrayList<>();
    public ItemConstructor Icons = new ItemConstructor();

    @Override
    public void onEnable() {
        cfg.saveDefaultConfig();
        Icons.saveEnchs();
        if(cfg.getBoolean("Metas.Cash.Ativar")){
            try{
                HashMap<Integer, Integer> niveis = new HashMap<>();
                HashMap<Integer, Rewards> RewForAll = new HashMap<>();
                HashMap<Integer, Rewards> RewForOne = new HashMap<>();
                for(String a:getSet(cfg, "Metas.Cash.Niveis")){
                    int i = Integer.parseInt(a)-1;
                    int meta = cfg.getInt("Metas.Cash.Niveis."+a+".Meta");
                    ArrayList<ItemStack> issForAll = new ArrayList<>();
                    for(String b:cfg.getConfig().getStringList("Metas.Cash.Niveis."+a+".Items_Para_Todos")) issForAll.add(Icons.getItem(b));
                    ArrayList<String> cmdsForAll = (ArrayList<String>)cfg.getConfig().getStringList("Metas.Cash.Niveis."+a+".Comandos_Para_Todos");
                    ArrayList<ItemStack> issForOne = new ArrayList<>();
                    for(String b:cfg.getConfig().getStringList("Metas.Cash.Niveis."+a+".Items_Para_Quem_Completou")) issForOne.add(Icons.getItem(b));
                    ArrayList<String> cmdsForOne = (ArrayList<String>)cfg.getConfig().getStringList("Metas.Cash.Niveis."+a+".Comandos_Para_Quem_Completou");
                    Rewards rewForAll = new Rewards(issForAll, cmdsForAll);
                    Rewards rewForOne = new Rewards(issForOne, cmdsForOne);
                    niveis.put(i, meta);
                    RewForAll.put(i, rewForAll);
                    RewForOne.put(i, rewForOne);
                }
                CashBought cashMeta = new CashBought(niveis, RewForOne, RewForAll, this);
                MetasGroups.add(cashMeta);
                Bukkit.getPluginManager().registerEvents(cashMeta, this);
                sout("§e[METAS]: A meta §fCash §efoi carregada com sucesso!");
            }catch(Exception e){
                sout("§e[METAS]: A meta §fCash §enao pode ser carregada!");
            }
        }else sout("§e[METAS]: A meta §fCash §enao esta habilitada!");
        if(cfg.getBoolean("Metas.Minerador.Ativar")){
            try{
                HashMap<Integer, Integer> niveis = new HashMap<>();
                HashMap<Integer, Rewards> RewForOne = new HashMap<>();
                for(String a:getSet(cfg, "Metas.Minerador.Niveis")){
                    int i = Integer.parseInt(a)-1;
                    int meta = cfg.getInt("Metas.Minerador.Niveis."+a+".Meta");
                    ArrayList<ItemStack> issForOne = new ArrayList<>();
                    for(String b:cfg.getConfig().getStringList("Metas.Minerador.Niveis."+a+".Items")) issForOne.add(Icons.getItem(b));
                    ArrayList<String> cmdsForOne = (ArrayList<String>)cfg.getConfig().getStringList("Metas.Minerador.Niveis."+a+".Comandos");
                    Rewards rewForOne = new Rewards(issForOne, cmdsForOne);
                    niveis.put(i, meta);
                    RewForOne.put(i, rewForOne);
                }
                Miner miner = new Miner(niveis, RewForOne, this);
                MetasOne.add(miner);
                sout("§e[METAS]: A meta §fCash §efoi carregada com sucesso!");
            }catch(Exception e){
                sout("§e[METAS]: A meta §fCash §enao pode ser carregada!");
            }
        }else sout("§e[METAS]: A meta §fMinerador §enao esta habilitada!");
        
    }
    
    
    public void sout(String linha){
        Bukkit.getConsoleSender().sendMessage(linha);
    }
    
    public Set<String> getSet(Config cfg, String path){
        return cfg.getConfig().getConfigurationSection(path).getKeys(false);
    }
    
    public static void darItem(Player p, ItemStack is){
        HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
        for(int i:itens.keySet()) {
            is.setAmount(i);
            Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            Item.setPickupDelay(20);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        
    }
    
}
