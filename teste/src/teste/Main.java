package teste;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    public Config cfg=new Config(this, "config.yml"),
            drops=new Config(this, "drops.yml"),
            vender=new Config(this, "vender.yml"),
            xpBoost=new Config(this, "XPboosters.yml"),
            blocksBlocks=new Config(this, "BlocksBlocks.yml");
    public HashMap<String, ItemStack> itens = new HashMap<>();
    public Snd Sender = new Snd();
    
    public void add(Player p){
        cfg.set(p.getName(), get(p)+1);
        cfg.saveConfig();
    }
    public void rem(Player p){
        cfg.set(p.getName(), get(p)-1);
        cfg.saveConfig();
    }
    public int get(Player p){
        return cfg.getInt(p.getName());
    }
    public boolean tem(Player p){
        return get(p)>0;
    }
    
    @Override
    public void onEnable(){
        reload();
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args){
        if(!(snd instanceof Player)) return true;
        Player p=(Player)snd;
        String cmd=Cmd.getName();
        if(cmd.equals("ccbr")){
            if(args.length>0){
                if(args[0].equalsIgnoreCase("reviver")){
                    if(args.length>1){
                        if(args[1].equalsIgnoreCase("give")){
                            String name=cfg.getString("Item.name").replace("&", "§");
                            int id=cfg.getInt("Item.ID");
                            int data=cfg.getInt("Item.data");
                            ArrayList<String> lr=new ArrayList<>();
                            for(String a:cfg.getConfig().getStringList("Item.lore")){
                                lr.add(a.replace("&", "§"));
                            }
                            ItemStack a=new ItemStack(id, 1, (short)0, (byte)data);
                            ItemMeta am=a.getItemMeta();
                            am.setDisplayName(name);
                            am.setLore(lr);
                            a.setItemMeta(am);
                            p.getInventory().addItem(a);
                        }
                    }
                }else if(args[0].equalsIgnoreCase("drop")){
                    if(args.length>1){
                        if(args[1].equalsIgnoreCase("new")){
                            if(args.length>2){
                                ItemStack item = p.getItemInHand().clone();
                                if(item==null || item.getType()==Material.AIR){
                                    p.sendMessage("&cNao pode crair um drop feito de ar!");
                                }else{
                                    item.setAmount(1);
                                    drops.set("Drops."+args[2], item);
                                    drops.saveConfig();
                                }
                            }
                        }else if(args[1].equalsIgnoreCase("bloco")){
                            if(args.length>4){
                                String ID = args[2].replace(":", "-");
                                String drop = args[3];
                                double chance;
                                try{
                                    String[] a = ID.split("-");
                                    int id = Integer.parseInt(a[0]);
                                    int data;
                                    if(a.length>1) data = Integer.parseInt(a[1]);
                                }catch(Exception e){p.sendMessage("§c"+ID+" nao e um id valido!");return true;}
                                try{
                                    chance = Double.parseDouble(args[4]);
                                }catch(Exception e){p.sendMessage("§c"+args[4]+" nao e um valor double"); return true;}
                                drops.set("Blocos."+ID+"."+drop, chance);
                                drops.saveConfig();
                            }
                        }
                    }
                }else if(args[0].equalsIgnoreCase("reload")) {
                    reload();
                }else if(args[0].equalsIgnoreCase("XpBoost")){
                    if(args.length>2){
                        if(args[1].equalsIgnoreCase("add")){
                            if(args.length>4){
                                try{
                                    String pn = args[2].toLowerCase();
                                    xpBoost.set(pn+".Boost", Double.parseDouble(args[3]));
                                    xpBoost.set(pn+".Dias", Integer.parseInt(args[4]));
                                    xpBoost.set(pn+".Date", getData());
                                    xpBoost.saveConfig();
                                }catch(Exception e){p.sendMessage("§c"+args[3]+"/"+args[4]+" nao e um numero!");}
                            }else p.sendMessage("use /ccbr xpboost add <player> <multiplier> <dias>");
                        }else if(args[1].equalsIgnoreCase("remove")|| args[1].equalsIgnoreCase("rem")){
                            xpBoost.set(args[2].toLowerCase(), null);
                            xpBoost.saveConfig();
                        }else p.sendMessage("use /ccbr xpboost <add/rem/remove> <player>");
                    }else p.sendMessage("use /ccbr xpboost <add/rem/remove> <player>");
                }
                
            }
        }else if(cmd.equals("vender")){
            if(p.hasPermission("ccbr.addons.admin")){
                if(args.length>0){
                    if(args[0].equalsIgnoreCase("add")){
                        if(args.length>2){
                            if(!isAir(p.getItemInHand())){
                                vender.set(args[1], p.getItemInHand());
                                vender.saveConfig();
                                cfg.set(args[1]+"."+args[2], 200);
                                cfg.saveConfig();
                                itens.put(args[1], p.getItemInHand());
                            }
                        }
                        return true;
                    }
                }
            }
            int amount = 0;
            ItemStack item = p.getItemInHand();
            if(isAir(item)) {
                p.sendMessage("§4Voce nao pode vender o ar, ponha algum item na sua mao!");
                return false;
            }
            if(!isItemRegistred(item)){
                p.sendMessage("§4Esse item não pode ser vendido no /vender !");
                return false;
            }
            int value = getValue(p, item);
            if(value<0){
                p.sendMessage("§4Voce nao pode vender esse item!");
                return false;
            }
            if(args.length>0){
                if(args[0].equalsIgnoreCase("all")){
                    for(int o=0;o<36;o++){
                        ItemStack i = p.getInventory().getItem(o);
                        if(!isAir(i)){
                            ItemStack Item = i.clone(), Item2 = item.clone();
                            Item.setAmount(1);
                            Item2.setAmount(1);
                            if(Item.equals(Item2)) {
                                amount += i.getAmount();
                                p.getInventory().setItem(o, null);
                            }
                        }
                    }
                    Bukkit.dispatchCommand(Sender, "eco give "+p.getName()+" "+(value*amount));
                    return true;
                }
                try{
                    amount = Integer.parseInt(args[0]);
                }catch(Exception e){
                    p.sendMessage("§f§l\""+args[0]+"\" §4não é um número!");
                }
                if(amount==0) return false;
                ItemStack Item2 = item.clone();
                amount *= amount<0 ? -1 : 1;
                int Amount = item.getAmount();
                int resta = amount<=Amount ? 0 : amount-Amount, tira = amount-resta;
                item.setAmount(Amount-tira);
                p.setItemInHand(Amount>tira ? item : null);
                if(resta==0){
                    Bukkit.dispatchCommand(Sender, "eco give "+p.getName()+" "+(value*amount));
                    return true;
                }
                for(int i=0;i<36;i++){
                    ItemStack I = p.getInventory().getItem(i);
                    if(!isAir(I)){
                        ItemStack Item = I.clone();
                        Item.setAmount(1);
                        Item2.setAmount(1);
                        if(Item.equals(Item2)) {
                            Amount = I.getAmount();
                            if(Amount<=resta){
                                p.getInventory().setItem(i, null);
                                resta -= Amount;
                            }else{
                                I.setAmount(Amount-resta);
                                p.getInventory().setItem(i, I);
                                resta = 0;
                            }
                            if(resta<=0) break;
                        }
                    }
                }
                amount -= resta;
                Bukkit.dispatchCommand(Sender, "eco give "+p.getName()+" "+(value*amount));
                return true;
            }else{
                amount = item.getAmount();
                p.setItemInHand(null);
                Bukkit.dispatchCommand(Sender, "eco give "+p.getName()+" "+(value*amount));
                return true;
            }
        }
        return false;
    }
    
    
    @EventHandler
    public void onXP(PlayerExpChangeEvent e){
        valida(e.getPlayer());
        String pn = e.getPlayer().getName().toLowerCase();
        if(xpBoost.contains(pn)){
            e.setAmount((int)(e.getAmount()*xpBoost.getDouble(pn)));
        }
    }
    
    public String getData(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
    public void valida(Player p){
        String pn = p.getName().toLowerCase();
        if(xpBoost.contains(pn)){
            String old = xpBoost.getString(pn+".Date"),
                    now = getData();
            int dias = xpBoost.getInt(pn+".Dias"),
                    tempo = (Integer.parseInt(old.split("/")[0])-Integer.parseInt(now.split("/")[0]));
            if(tempo>dias){
                xpBoost.set(pn, null);
                xpBoost.saveConfig();
            }
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public boolean isItemRegistred(ItemStack item){
        ItemStack i = item.clone();
        i.setAmount(1);
        for(ItemStack I:itens.values()) if(I.equals(i)) return true;
        return false;
    }
    
    public boolean isAir(ItemStack i){
        return (i==null || i.getType()==Material.AIR);
    }
    
    public ArrayList<String> getSet(Config cfg, String path){
        ArrayList<String> r = new ArrayList<>();
        for(String a:cfg.getConfig().getConfigurationSection(path).getKeys(false)) r.add(a);
        return r;
    }
    
    public void saveItens(){
        for(String a:getSet(vender, "")) itens.put(a, vender.getConfig().getItemStack(a));
    }
    
    public void reload(){
        if(!cfg.existeConfig()) cfg.saveDefaultConfig();
        if(!drops.existeConfig()) drops.saveDefaultConfig();
        if(!vender.existeConfig()) vender.saveDefaultConfig();
        cfg.reloadConfig();
        drops.reloadConfig();
        vender.reloadConfig();
        saveItens();
    }
    
    public int getValue(Player p, ItemStack item){
        String name = getNameOf(item);
        for(String a:getSet(cfg, name)){
            if(p.hasPermission("ccbr.addon."+a)){
                return cfg.getInt(name+"."+a);
            }
        }
        return -1;
    }
    
    public String getNameOf(ItemStack item){
        ItemStack Item = item.clone();
        Item.setAmount(1);
        for(String a:itens.keySet()) if(itens.get(a).equals(Item)) return a;
        return "";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(blocksBlocks.contains(""+e.getBlock().getTypeId())){
            e.setCancelled(true);
            Player p = e.getPlayer();
            p.sendMessage("§cEssa bloco não pode ser colocado/usado!");
            p.setItemInHand(null);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(blocksBlocks.contains(""+p.getItemInHand().getTypeId())){
            e.setCancelled(true);
            p.sendMessage("§cEsse item não pode ser usado!");
            p.setItemInHand(null);
        }
    }
    
    @EventHandler
    public void onCraftItem(PrepareItemCraftEvent e){
        if(blocksBlocks.contains(""+e.getRecipe().getResult().getTypeId())){
            e.getInventory().setResult(null);
            for(HumanEntity he:e.getInventory().getViewers()) {
                he.closeInventory();
                he.sendMessage("§cEsse item não pode ser craftado!");
            }
        }
    }
    
    
    
    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e){
        Player p=e.getPlayer();
        int id=cfg.getInt("Item.ID");
        int data=cfg.getInt("Item.data");
        String name=cfg.getString("Item.name").replace("&", "§");
        ArrayList<String> lr=new ArrayList<>();
        try{
        for(String a:cfg.getConfig().getStringList("Item.lore")){
            lr.add(a.replace("&", "§"));
        }}catch(Exception E){}
        ItemStack a=new ItemStack(id, 1, (short)0, (byte)data);
        ItemMeta am=a.getItemMeta();
        am.setDisplayName(name);
        am.setLore(lr);
        a.setItemMeta(am);
        if(e.getItem().isSimilar(a)){
            if(p.hasPermission("CCBR_Reviver")){
                add(p);
            }else{
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            if(e.getCause().equals(EntityDamageEvent.DamageCause.VOID)){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn "+p.getName());
                e.setCancelled(true);
                p.setHealth(20);
            }
            if((p.getHealth()-e.getDamage())<=0){
                if(tem(p)){
                    e.setDamage(0);
                    p.setHealth(20);
                }
            }
        }
    }
    
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent e){
        if(!e.isCancelled()){
            Player p=e.getPlayer();
            if(p.getGameMode()==GameMode.CREATIVE) return;
            for(String a:drops.getConfig().getConfigurationSection("Blocos").getKeys(false)){
                if(e.getBlock().getType().getId()==getMaterial(a).getId() || (e.getBlock().getType().getId()==74 && getMaterial(a).getId()==73)){
                    for(String drop:drops.getConfig().getConfigurationSection("Blocos."+a).getKeys(false)){
                        if(drops.contains("Drops."+drop)){
                            double ch = drops.getDouble("Blocos."+a+"."+drop);
                            if(p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)){
                                ch *= (1+(p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)*0.1));
                            }
                            if(RandomUtils.nextDouble()*100<=ch){
                                ItemStack item = drops.getConfig().getItemStack("Drops."+drop);
                                darItem(p, item);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
    
    public Material getMaterial(String id){
        String[] id2 = id.split("-");
        if(id2.length>1) return new ItemStack(Integer.parseInt(id2[0]), 1, (short)0, (byte)Byte.parseByte(id2[1])).getType();
        else return new ItemStack(Integer.parseInt(id2[0])).getType();
    }
    
    public void darItem(Player p, ItemStack item){
        boolean stackou = false;
        if(item.getMaxStackSize()==1){
            for(int i=0;i<36;i++){
                ItemStack I = p.getInventory().getItem(i);
                if(!isAir(I)){
                    if(I.getAmount()<64){
                        ItemStack I2 = I.clone();
                        I2.setAmount(1);
                        if(I2.equals(item)){
                            I.setAmount(I.getAmount()+1);
                            stackou = true;
                            break;
                        }
                    }
                }
            }
        }
        if(stackou) return;
        for(int i:p.getInventory().addItem(item).keySet()){
            item.setAmount(i);
            Item I = (Item)p.getLocation().getWorld().dropItemNaturally(p.getLocation(), item);
            I.setPickupDelay(20);
        }
    }
    
}