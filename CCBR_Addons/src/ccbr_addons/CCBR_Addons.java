package ccbr_addons;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class CCBR_Addons extends JavaPlugin implements  Listener{

    public Config Config;
    public ItemConstructor Icons;
    public Economy economy;
    public ArrayList<String> Mutados = new ArrayList<>();
    public CrazyCrates Cc;
    
    @Override
    public void onEnable() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CrazyCrates");
        if(pl!=null){
            Cc = CrazyCrates.getInstance();
        }
        Config = new Config(this, "config.yml");
        Config.saveDefaultConfig();
        Icons = new ItemConstructor();
        new Thread(new Runnable(){
            @Override
            public void run(){
                boolean Continue = true;
                try{
                    int i = 20;
                    while(Continue && i>=0){
                        if(getServer().getPluginManager().getPlugin("Essentials").isEnabled()){
                            Continue = false;
                            break;
                        }
                        i--;
                        Thread.sleep(500);
                    }
                }catch(Exception e){}
//                if(!Continue){
//                    getServer().getPluginCommand("warp").setTabCompleter(new TabCompleter() {
//
//                        @Override
//                        public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
//                            if(cmnd.getName().equalsIgnoreCase("warp")){
//                                ArrayList<String> Arr = new ArrayList<>();
//                                if(strings.length<2){
//                                    Warps W = new Warps(Bukkit.getServer(), Bukkit.getPluginManager().getPlugin("Essentials").getDataFolder());
//                                    for(String a:W.getList()){
//                                        if(a.startsWith(strings[0])) Arr.add(a);
//                                    }
//                                }
//                                return Arr;
//                            }
//                            return null;
//                        }
//                    });
//                }
                Continue = true;
                try{
                    int i = 20;
                    while(Continue && i>=0){
                        if(getServer().getPluginManager().getPlugin("MineableSpawners").isEnabled()){
                            Continue = false;
                            break;
                        }
                        i--;
                        Thread.sleep(500);
                    }
                }catch(Exception e){}
                if(!Continue){
                    getServer().getPluginCommand("spawnergive").setTabCompleter(new TabCompleter() {

                        @Override
                        public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] args) {
                            if(cmnd.getName().equalsIgnoreCase("spawnergive")){
                                if(args.length==2){
                                    ArrayList<String> Arr = new ArrayList<>();
                                    String a1 = args[1].toUpperCase();
                                    for(EntityType et:EntityType.values()){
                                        if(et.toString().matches(getRegex(a1)) || a1.matches(getRegex(et.toString()))){
                                            Arr.add(et.toString().toUpperCase());
                                        }
                                    }
                                    return Arr;
                                }else if(args.length==3) return new ArrayList<>(Arrays.asList(new String[]{"(amount)"}));
                            }
                            return null;
                        }
                    });
                }
            }
        }).start();
        getCommand("enchant").setTabCompleter(new TabCompleter() {

            @Override
            public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] args) {
                if(cmnd.getName().equalsIgnoreCase("enchant")){
                    if(cs.hasPermission("CCBR_Addons.enchant")){
                        if(args.length==1){
                            ArrayList<String> Arr = new ArrayList<>();
                            for(Enchantment ench:Enchantment.values()) Arr.add(ench.toString().split(":")[1].split(",")[0].toUpperCase());
                            String a0 = args[0].toUpperCase();
                            ArrayList<String> ret = new ArrayList<>();
                            for(String str:Arr) if(a0.matches(getRegex(str)) || str.matches((getRegex(a0)))) ret.add(str);;
                            if(ret.size()==0) return Arr;
                            else return ret;
                        }else if(args.length==2){
                            return new ArrayList<>(Arrays.asList(new String[]{"0-1000"}));
                        }else return new ArrayList<>();
                    }
                }
                return null;
            }
        });
        Bukkit.getPluginManager().registerEvents(this, this);
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider!=null) economy = economyProvider.getProvider();
        else System.out.println("[CCBR_Cash]: Nao foi possivel encontrar o ECONOMYPROVIDER do Vault!");
    }
    
   public String getRegex(String strg){
       if(strg !=null && strg.length()>0){
           StringBuilder sb = new StringBuilder(".*").append(strg.charAt(0));
           for(int i=1;i<strg.length();i++) sb.append(".*").append(strg.charAt(i));
           return sb.append(".*").toString();
       }
       return "";
   }
 
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(cmd.equals("randomteleport")){
            if(snd instanceof Player){
                final Player p = (Player)snd;
                if(playerInRTPCoolDown.containsKey(p)){
                    snd.sendMessage("§cFaltam §f"+(Config.getInt("RandomTeleport.cooldown")-(int)Math.floor(((System.currentTimeMillis()-playerInRTPCoolDown.get(p))/1000)))+" §csegundos para você poder usar o rtp novamente!");
                    return false;
                }
                randomTeleport(p, Config.getInt("RandomTeleport.raio"));
                if(!p.hasPermission("CCBR_Addons.rtp.nocooldown")) playerInRTPCoolDown.put(p, System.currentTimeMillis());
                Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable(){
                    final Player P = p;
                        @Override
                        public void run(){
                            try{
                                Thread.sleep(((long)Config.getInt("RandomTeleport.cooldown")*1000L));
                                playerInRTPCoolDown.remove(P);
                                if(P.isOnline()) P.sendMessage("§aVocê agora já pode usar o /rtp novamente!");
                            }catch(Exception ex){}
                        }
                }, ((long)Config.getInt("RandomTeleport.cooldown")*1000L));
            }
        }else if(cmd.equals("enchant")){
            if(snd instanceof Player){
                Player p = (Player)snd;
                if(p.hasPermission("CCBR_Addons.enchant")){
                    if(args.length>1){
                        if(!p.getItemInHand().getType().toString().contains("AIR")){
                            if(args[0].equalsIgnoreCase("all")){
                                ItemStack is = p.getItemInHand();
                                for(Enchantment ench:Enchantment.values()){
                                    is.addUnsafeEnchantment(ench, 1000);
                                }
                                p.setItemInHand(is);
                                return true;
                            }
                            int lv = 0;
                            try {
                                lv = Integer.parseInt(args[1]);
                            }catch(Exception e){
                                p.sendMessage("§4O valor §e\""+args[1]+"\" §4não é um número!");
                                return false;
                            }
                            ItemStack is = p.getItemInHand();
                            String a0 = args[0].toUpperCase();
                            ArrayList<String> Arr = new ArrayList<>(), res = new ArrayList<>();
                            for(Enchantment ench:Enchantment.values()) Arr.add(ench.toString().split(":")[1].split(",")[0].toUpperCase());
                            for(String a:Arr) if(a0.matches(getRegex(a)) || a.matches(getRegex(a0))) res.add(a);
                            for(String a:Arr) if(a0.toUpperCase().equals(a)) {
                                res = new ArrayList<>();
                                res.add(a);
                            }
                            if(res.size()==0){
                                p.sendMessage("§4O encantamento §e\""+args[0].toUpperCase()+"\" §4não existe!");
                                return false;
                            }else if(res.size()!=1){
                                p.sendMessage("§4Seja mais específico!");
                                return false;
                            }
                            try{
                                if(lv<1){
                                    is.removeEnchantment(getEnchByName(res.get(0)));
                                }else{
                                    is.addUnsafeEnchantment(getEnchByName(res.get(0)), lv > 1000 ? 1000 : lv);
                                }
                            }catch(Exception ex){}
                            p.setItemInHand(is);
                        }
                    }else{
                        if(args.length>0){
                            if(args[0].equalsIgnoreCase("all")){
                                ItemStack is = p.getItemInHand();
                                for(Enchantment ench:Enchantment.values()){
                                    is.addUnsafeEnchantment(ench, 1000);
                                }
                                p.setItemInHand(is);
                                return true;
                            }
                        }
                    }
                }
            }
        }else if(cmd.equals("vender")){
            if(snd instanceof Player){
                Player p = (Player)snd;
                if(args.length>0){
                    if(args[0].equalsIgnoreCase("ver")){
                        Inventory inv = Bukkit.createInventory(null, 54, "§2§l[CCBR] §6§lVender Ver");
                        int i=0;
                        for(String a:Config.getConfig().getConfigurationSection("Vender").getKeys(false)){
                            IsBuilder ib = new IsBuilder(Icons.getItem(Config.getString("Vender."+a+".Item")));
                            double valor = 0;
                            for(String b:Config.getConfig().getConfigurationSection("Vender."+a).getKeys(false)){
                                if(!b.equals("Item")){
                                    if(p.hasPermission("CCBR_Addons.vender."+b)){
                                        valor = Config.getDouble("Vender."+a+"."+b);
                                    }
                                }
                            }
                            inv.setItem(i, ib.addLore("§7Valor: §eR$"+valor).getItemStack());
                            i++;
                        }
                        p.openInventory(inv);
                        return true;
                    }else if(args[0].equalsIgnoreCase("reload")){
                        if(p.hasPermission("CCBR_Addons.vender.reload")){
                            Config = new Config(this, "config.yml");
                            return true;
                        }
                    }
                    else{
                        p.sendMessage("§euse (/vender ver) para ver todos os itens que você pode vender!");
                        if(p.hasPermission("CCBR_Addons.vender.reload")) p.sendMessage("§ause (/vender reload) para recarregar os preços!");;
                        return true;
                    }
                }
                p.openInventory(Bukkit.createInventory(null, 54, "§2§l[CCBR] §6§lVender"));
            }
        }else if(cmd.equals("mutar")){
            if(snd instanceof Player){
                String pn = ((Player)snd).getName().toLowerCase();
                if(Mutados.contains(pn)){
                    snd.sendMessage("§aO som da notificação de chat agora §c§lnão §aestá mais mutado para você!");
                    Mutados.remove(pn);
                }else{
                    snd.sendMessage("§aO som da notificação de chat agora está mutado para você!");
                    Mutados.add(pn);
                }
            }
        }
        return true;
    }
    
    public Enchantment getEnchByName(String name){
        for(Enchantment ench:Enchantment.values()) if(ench.toString().split(":")[1].split(",")[0].equalsIgnoreCase(name)) return ench;
        return null;
    }
    
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e){
        if(e.getInventory().getName().equals("§2§l[CCBR] §6§lVender")){
            Player p = (Player)e.getPlayer();
            double total = 0;
            label0:
            for(int i=0;i<54;i++){
                ItemStack is = e.getInventory().getItem(i);
                IsBuilder ib = new IsBuilder(is);
                if(!ib.isAir()){
                    label1:
                    for(String a:Config.getConfig().getConfigurationSection("Vender").getKeys(false)){
                        ItemStack is2 = Icons.getItem(Config.getString("Vender."+a+".Item"));
                        if(!IsBuilder.isAir(is2)){
                            if(ib.compareTo(is2)){
                                double valor = 0;
                                for(String b:Config.getConfig().getConfigurationSection("Vender."+a).getKeys(false)){
                                    if(!b.equals("Item")){
                                        if(p.hasPermission("CCBR_Addons.vender."+b)){
                                            valor = Config.getDouble("Vender."+a+"."+b);
                                        }
                                    }
                                }
                                if(valor>0){
                                    total += valor*is.getAmount();
                                }else{
                                    darItem(p, is);
                                }
                                continue label0;
                            }
                        }
                    }
                    darItem(p, is);
                }
            }
            if(total>0){
                DecimalFormat df = new DecimalFormat("0.##");
                economy.depositPlayer(p.getName(), total);
                p.sendMessage("§aVocê ganhou §e§lR$"+df.format(total).replace(',', '.'));
            }
        }
    }
    
    public void darItem(Player p, ItemStack is){
        HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
        for(int i:itens.keySet()) {
            is.setAmount(i);
            Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            Item.setPickupDelay(20);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getTitle().equals("§2§l[CCBR] §6§lVender Ver")){
            e.setCancelled(true);
        }
    }
    
    private final HashMap<Player, Long> playerInRTPCoolDown = new HashMap<>();
    
    public void randomTeleport(Player p, int r){
        double d1 = (Math.random()*r)-r, d2 = (Math.random()*r)-r;
        Location loc = p.getLocation().clone().add(d1, 0, d2);
        p.teleportAsync(getUpperBlock(loc).add(0, 1, 0));
    }
    
    public Location getUpperBlock(Location loc){
        for(int i=256;i>0;i--){
            loc.setY(i);
            Material mtrl = loc.getBlock().getType();
            if(!mtrl.toString().contains("AIR")) {
                return loc;
            }
        }
        return loc;
    }
    
    @EventHandler
    public void ChatEvent(AsyncPlayerChatEvent e){
        StringBuilder sb = new StringBuilder();
        for(String a:e.getMessage().split(" ")){
            for(int i=0;i<a.length();i++){
                char c = a.toCharArray()[i];
                if(c=='§'){
                    try{
                        char c1 = a.toCharArray()[i+1];
                        if(c1=='0' || c1=='1' || c1=='2' || c1=='3' || c1=='4' || c1=='5' || c1=='6' || c1=='7' || c1=='8' || c1=='9' || c1=='a' || c1=='A' || c1=='b' || c1=='B' || c1=='c' || c1=='C' || c1=='d' ||c1=='D' || c1=='e' ||c1=='E' || c1=='F' ||c1=='F'){
                            sb = new StringBuilder();
                            sb.append(c).append(c1);
                            i++;
                            char c2 = a.toCharArray()[i+1];
                            if(c2=='§'){
                                char c3 = a.toCharArray()[i+2];
                                if(c3=='k' || c3=='K' || c3=='l' || c3=='L' || c3=='m' || c3=='M' || c3=='n' || c3=='N' || c3=='o' || c3=='O'){
                                    sb.append(c2).append(c3);
                                    i += 2;
                                }
                            }
                        }else if(c1=='k' || c1=='K' || c1=='l' || c1=='L' || c1=='m' || c1=='M' || c1=='n' || c1=='N' || c1=='o' || c1=='O'){
                            sb = new StringBuilder();
                            sb.append(c).append(c1);
                            i++;
                        }
                    }catch(Exception ex){}
                }
            }
            for(Player p:getServer().getOnlinePlayers()){
                if(a.matches(".*@(?i)"+p.getName()+".*")){
                    e.setMessage(e.getMessage().replaceFirst("@(?i)"+p.getName(), "§e@"+p.getName()+(sb.length()==0 ? "§f" : sb.toString())));
                    if(!Mutados.contains(p.getName().toLowerCase())) p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(!e.isCancelled()){
            if(Cc!=null){
                Player p = e.getPlayer();
                final ItemStack is;
                if(e.getHand()==EquipmentSlot.OFF_HAND) is = p.getInventory().getItemInOffHand();
                else is = p.getInventory().getItemInMainHand();
                final int amount = is.getAmount();
                if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
                    if(!IsBuilder.isAir(is)){
                        if(IsBuilder.compareTo(is, Icons.getItem("TRIPWIRE_HOOK 1 [name=&e&lCAIXA VIP &b&lKey] [lore=&7Uma chave especial/N&7Para uma caixa especial.] [enchhide] [tag={Enchantments:[{lvl:1s,id:\"minecraft:luck_of_the_sea\"}]}]"))){
                            for(CrateLocation cl:Cc.getCrateLocations()){
                                if(cl.getLocation().equals(e.getClickedBlock().getLocation())){
                                    if(cl.getCrate().getName().equals("Vip")){
                                        if(!p.hasPermission("CrazzyCrates.open.Vip")){
                                            e.setCancelled(true);
                                            p.setItemInHand(null);
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                                                @Override
                                                public void run(){

                                                    is.setAmount(amount);
                                                    for(int i:p.getInventory().addItem(is).keySet()){
                                                        is.setAmount(i);
                                                        p.getWorld().dropItemNaturally(p.getLocation(), is).setPickupDelay(20);
                                                    }

                                                }

                                            }, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
}
