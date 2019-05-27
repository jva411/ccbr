package maquinas3;

import ccbr_cash3.*;
import ccbr_cash3.CashEvent.*;
import java.util.ArrayList;
import java.util.HashMap;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class API {
    
    static Economy eco;
    int taks, indice = 0;
    static Main ccbr;
    
    public Config cfg, machs, machsW;
    public ItemAPI Iapi = new ItemAPI();
    public ItemConstructor Icons = new ItemConstructor();
    public HashMap<String, MachineType> Mtypes = new HashMap<>();
    public HashMap<String, Drop> Drops = new HashMap<>();
    public HashMap<String, Combustivel> Combs = new HashMap<>();
    public HashMap<Location, Machine> machines = new HashMap<>();
    public HashMap<Player, Machine> players = new HashMap<>();
    public HashMap<Player, Integer> pages = new HashMap<>();
    public static BukkitScheduler bs = Bukkit.getScheduler();
    public static Maquinas3 main;
    public HashMap<Player, Boolean> fechou = new HashMap<>();
    public int drops1, drops2, drops3, drops4, drops5;
    
    public void saveDefaults(JavaPlugin plugin){
        Icons.saveEnchs();
        main = (Maquinas3) plugin;
        cfg = new Config(plugin, "config.yml");
        machs = new Config(plugin, "maquinas.yml");
        machsW = new Config(plugin, "inWorld/maquinas.yml");
        if(!cfg.existeConfig()) cfg.saveDefaultConfig();
        if(!machs.existeConfig()) machs.saveDefaultConfig();
        if(!machsW.existeConfig()) machsW.saveDefaultConfig();
        saveMsgs();
        saveItens();
        taks = bs.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(indice>0){
                    saveConfigs();
                    bs.cancelTask(taks);
                }else indice++;
            }
        }, 0, 300);
        if(Bukkit.getPluginManager().getPlugin("Vault") instanceof Vault){
            RegisteredServiceProvider<Economy> service = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if(service != null) eco = service.getProvider();
        }
        if(Bukkit.getPluginManager().getPlugin("CCBR_Cash") instanceof Main){
            ccbr = (Main)Bukkit.getPluginManager().getPlugin("CCBR_Cash");
        }
    }
    
    public void saveConfigs(){
        System.out.println("§aSalvando as configs! Maquinas");
        for(String a:getSet(machs, "Drops")) Drops.put(a, new Drop(Icons.getItem(machs.getString("Drops."+a))));
        System.out.println("§aSalvou Drops");
        for(String a:getSet(machs, "Combustiveis")){
            ItemStack combustivel = Icons.getItem(machs.getString("Combustiveis."+a+".Item"));
            double multiplier = machs.getDouble("Combustiveis."+a+".Multiplier");
            int time = machs.getInt("Combustiveis."+a+".Time");
            Combs.put(a, new Combustivel(combustivel, multiplier, time));
        }
        System.out.println("§aSalvou Combustiveis");
        for(String a:getSet(machs, "Maquinas")){
            ItemStack block = Icons.getItem(machs.getString("Maquinas."+a+".Bloco"));
            block.setAmount(1);
            ArrayList<Combustivel> combsCompativeis = new ArrayList<>();
            for(String b:machs.getConfig().getStringList("Maquinas."+a+".Combustiveis")) combsCompativeis.add(Combs.get(b));
            Drop drop = Drops.get(machs.getString("Maquinas."+a+".Drop"));
            double multiplier = machs.getDouble("Maquinas."+a+".Multiplier");
            String displayName = machs.getString("Maquinas."+a+".DisplayName").replace("&", "§");
            int[] Upgrades = new int[4];
            int i =0;
            for(String b:machs.getConfig().getStringList("Maquinas."+a+".Upgrades")){
                Upgrades[i] = Integer.parseInt(b);
                i++;
                if(i==4) break;
            }
            Mtypes.put(a, new MachineType(block, combsCompativeis, drop, multiplier, displayName, Upgrades));
        }
        System.out.println("§aSalvou MaquinaTypes");
        for(String a:getSet(machsW, "Maquinas")){
            String set = "Maquinas."+a+".";
            Location loc = getLoc(a);
            String own = machsW.getString(set+"Dono");
            MachineType machineType = Mtypes.get(machsW.getString(set+"Type"));
            int level = machsW.getInt(set+"Nivel"), time = machsW.getInt(set+"Tempo"),
                    drops = machsW.getInt(set+"Drops"),
                    maxTime = machsW.getInt(set+"maxTempo");
            boolean hdEnable = machsW.getBoolean(set+"HdsEnable"),
                    chestEnable = machsW.getBoolean(set+"ChestEnable"),
                    enabled = machsW.getBoolean(set+"Enabled");
            Machine maq = new Machine(loc.clone(), machineType, own, level, time, maxTime, hdEnable, chestEnable, enabled, drops);
            machines.put(loc.clone(), maq);
        }
        System.out.println("§aSalvou Maquinas");
        String setD = "Maquinas.Drops.";
        drops1 = cfg.getInt(setD+"1");
        drops2 = cfg.getInt(setD+"2");
        drops3 = cfg.getInt(setD+"3");
        drops4 = cfg.getInt(setD+"4");
        drops5 = cfg.getInt(setD+"5");
    }
    
    public ArrayList<String> getSet(Config file, String path){
        ArrayList<String> r = new ArrayList<>();
        if(file.contains(path)) {
            for(String a:file.getConfig().getConfigurationSection(path).getKeys(false)) r.add(a);
        }
        return r;
    }
    
    
    
    
    
    
    
    public void openSettings(Player p, Machine maq){
        String money = maq.getLevel()>2 ? "cash" : "money";
        Inventory inv = Bukkit.createInventory(null, 45, settings);
        inv.setItem(10, maq.isEnabled() ? lampON : lampOFF);
        inv.setItem(12, maq.isChestEnable() ? chestON : chestOFF);
        inv.setItem(14, maq.isHdEnable() ? hdON : hdOFF );
        ItemStack Upgrade = upgrade.clone();
        Iapi.setLore(Upgrade);
        if(maq.getLevel()>=5){
            Iapi.setLore(Upgrade, "", "§eLvl.§4MAX");
        }else for(String a:upgrade.getItemMeta().getLore()) Iapi.addLore(Upgrade
                , a.replace("%preco%", maq.getUpgrade()+" §ede §f"+money+"§e").replace("%proxLv%", (maq.getLevel()+1)+""));
        inv.setItem(16, Upgrade);
        maq.setPlayer(p, true);
        players.put(p, maq);
        pages.put(p, 0);
        p.openInventory(inv);
        fechou.put(p, false);
    }
    
    public void openChest(Player p, Machine maq){
        Inventory inv = Bukkit.createInventory(null, 54, settings);
        ItemStack item = maq.getType().getDrop().getDrop();
        int drops = maq.getDrops(),
                maxStack = item.getMaxStackSize();
        if(drops>0){
            for(int i=0;i<54;i++) {
                int amount = drops>maxStack ? maxStack : drops;
                item.setAmount(amount);
                drops -= amount;
                inv.setItem(i, item);
                if(drops<=0) break;
            }
        }
        fechou.put(p, false);
        p.openInventory(inv);
        players.put(p, maq);
        maq.setPlayer(p, true);
        pages.put(p, 1);
    }
    
    public void openUpgrade(Player p, Machine maq){
        int preco = maq.getUpgrade();
        boolean ok = true;
        if(maq.getLevel()>2){
            if(preco==-1 || ccbr.api.getCash(p.getName())<preco){
                ok = false;
                p.sendMessage(noCash);
            }
        }else{
            if(preco==-1 || eco.getBalance(p.getName())<preco) {
                ok = false;
                p.sendMessage(noMoney);
            }
        }
        if(!ok) return;
        Inventory inv = Bukkit.createInventory(null, 9, settings);
        inv.setItem(2, Iapi.setName(new ItemStack(35, 1, (short)0, (byte)14), "§c§lCANCELAR"));
        inv.setItem(6, Iapi.setName(new ItemStack(35, 1, (short)0, (byte)5), "§a§lCONFIRMAR"));
        String money = maq.getLevel()>2 ? "cash" : "money";
        inv.setItem(4, Iapi.setName(new ItemStack(388), "§bPreco para upar: "+preco+" de "+money));
        p.openInventory(inv);
        fechou.put(p, false);
        players.put(p, maq);
        maq.setPlayer(p, true);
        pages.put(p, 2);
    }
    
    public void upgrade(Player p, Machine maq){
        int l = maq.getLevel(), preco = maq.getUpgrade();
        if(preco<=0) return;
        String pn = p.getName();
        if(l>2){
            if(ccbr.api.getCash(pn)>=preco){
                Bukkit.getPluginManager().callEvent(new CashEvent(preco, p, Origem.USED));
                maq.setLevel(l+1);
                maq.enableHd();
            }else p.sendMessage(noMoney);
        }else{
            if(eco.getBalance(pn)>=preco){
                eco.withdrawPlayer(pn, preco);
                maq.setLevel(l+1);
                maq.enableHd();
            }else p.sendMessage(noMoney);
        }
        openSettings(p, maq);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void drop(int drops, ItemStack item, Location loc){
        if(drops<=0) return;
        int stack, maxStack = item.getMaxStackSize();
        do{
            stack = drops>maxStack ? maxStack : drops;
            item.setAmount(stack);
            Item i = loc.getWorld().dropItemNaturally(loc, item);
            i.setPickupDelay(20);
            drops -= stack==drops ? drops : maxStack;
        }while(drops>0);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public boolean addMachine(Player p,Location loc, Machine maq){
        String pn = p.getName(), set = "Players."+pn+"."+getName(maq);
        int amount = 0, l = 1;
        for(int i=2;i<6;i++) {
            int o = machsW.getInt(set+"."+i);
            if(o>0){
                amount = o;
                l = i;
            }
        } 
        if(amount>0) {
            maq.setLevel(l);
            if(amount>1) machsW.set(set, amount-1);
            else machsW.set("Players."+pn, null);
            machsW.saveConfig();
        }
        machines.put(loc, maq);
        setMachine(maq);
        maq.enableHd();
        p.sendMessage(colocada);
        return true;
    }
    public boolean remMachine(Player p, Location loc, Machine maq){
        if(isOwn(p, maq) || p.hasPermission("maquinas.bypass")){
            if(maq.getTime()<=0){
                drop(maq.getDrops(), maq.getType().getDrop().getDrop(), maq.getLoc());
                loc.getBlock().setType(Material.AIR);
                for(Location Loc:machines.keySet()) if(machines.get(Loc).equals(maq)){
                    machines.remove(Loc);
                    maq.stop();
                    break;
                }
                remMachine(maq);
                setPlayer(p, maq);
                if(!p.getGameMode().equals(GameMode.CREATIVE)) darItem(p, maq.getType().getBlock());
                p.sendMessage(retirada);
                return true;
            }else p.sendMessage(nRetirou);
        }else p.sendMessage(noOwn);
        return false;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void setDrop(ItemStack drop, String name){
        machs.set("Drops."+name, Icons.getString(drop));
        machs.saveConfig();
        Drops.put(name, new Drop(drop));
    }
    public void setCombustivel(ItemStack combustivel, String name, int time, double multiplier){
        machs.set("Combustiveis."+name+".Item", Icons.getString(combustivel));
        machs.set("Combustiveis."+name+".Time", time);
        machs.set("Combustiveis."+name+".Multiplier", multiplier);
        machs.saveConfig();
        Combs.put(name, new Combustivel(combustivel, multiplier, time));
    }
    public void setMachineType(ItemStack block, String name, ArrayList<Combustivel> combsCompativeis, Drop drop, double multiplier, String displayName, int[] Upgrades){
        String set = "Maquinas."+name+".";
        ArrayList<String> combs = new ArrayList<>();
        for(Combustivel comb:combsCompativeis) combs.add(getName(comb));
        machs.set(set+"Bloco", Icons.getString(block));
        machs.set(set+"Combustiveis", combs);
        machs.set(set+"Drop", getName(drop));
        machs.set(set+"Multiplier", multiplier);
        machs.set(set+"DisplayName", displayName);
        ArrayList<String> upgrades = new ArrayList<>();
        for(int i:Upgrades) upgrades.add(""+i);
        machs.set(set+"Upgrades", upgrades);
        machs.saveConfig();
        Mtypes.put(name, new MachineType(block, combsCompativeis, drop, multiplier, displayName, Upgrades));
    }
    public void setMachine(Machine maquina){
        Location loc = maquina.getLoc();
        MachineType machineType = maquina.getType();
        String own = maquina.getOwn();
        int level = maquina.getLevel(), 
                time = maquina.getTime(),
                maxTime = maquina.getMaxTime(),
                drops = maquina.getDrops();
        boolean hdEnable = maquina.isHdEnable(),
                enabled = maquina.isEnabled(),
                chestEnable = maquina.isChestEnable();
        String set = "Maquinas."+getLoc(loc)+".";
        machsW.set(set+"Dono", own);
        machsW.set(set+"Type", getName(machineType));
        machsW.set(set+"Tempo", time);
        machsW.set(set+"maxTempo", maxTime);
        machsW.set(set+"Nivel", level);
        machsW.set(set+"Drops", drops);
        machsW.set(set+"HdsEnable", hdEnable);
        machsW.set(set+"ChestEnable", chestEnable);
        machsW.set(set+"Enabled", enabled);
        machsW.saveConfig();
    }
    public void remMachine(Machine maquina){
        machsW.set("Maquinas."+getLoc(maquina.getLoc()), null);
        machsW.saveConfig();
    }
    public void setPlayer(Player p, Machine maq){
        String pn = p.getName();
        if(maq.getLevel()>1){
            int amount = machsW.getInt("Players."+pn+"."+getName(maq)+"."+maq.getLevel())+1;
            machsW.set("Players."+pn+"."+getName(maq)+"."+maq.getLevel(), amount);
            machsW.saveConfig();
        }
    }
    public <T> String getName(T obj){
        if(obj instanceof Drop) for(String name:Drops.keySet()) if(Drops.get(name).equals(obj)) return name;
        if(obj instanceof Machine) return getName(((Machine) obj).getType());
        if(obj instanceof Combustivel) for(String name:Combs.keySet()) if(Combs.get(name).equals(obj)) return name;
        if(obj instanceof MachineType) for(String name:Mtypes.keySet()) if(Mtypes.get(name).equals(obj)) return name;
        return "";
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public Location getLoc(String loc){
        String[] Loc = loc.split(" ");
        int x = Integer.parseInt(Loc[1]), y = Integer.parseInt(Loc[2]), z = Integer.parseInt(Loc[3]);
        return new Location(Bukkit.getWorld(Loc[0]), x, y, z);
    }
    public String getLoc(Location loc){
        return loc.getWorld().getName()+" "+loc.getBlockX()+" "+loc.getBlockY()+" "+loc.getBlockZ();
    }
    
    
    
    public void saveMsgs(){
        String set = "Msgs.";
        help = new ArrayList<>();
        helpGive = cfg.getString(set+"helpGive").replace("&", "§");
        helpNew = cfg.getString(set+"helpNew").replace("&", "§");
        helpList = cfg.getString(set+"helpList").replace("&", "§");
        onlyPlayers = cfg.getString(set+"onlyPlayers").replace("&", "§");
        helpNewDrop = cfg.getString(set+"helpNewDrop").replace("&", "§");
        helpNewComb = cfg.getString(set+"helpNewComb").replace("&", "§");
        helpNewMaq = cfg.getString(set+"helpNewMaq").replace("&", "§");
        itemInvalido = cfg.getString(set+"itemInvalido").replace("&", "§");
        helpGiveDrop = cfg.getString(set+"helpGiveDrop").replace("&", "§");
        helpGiveComb = cfg.getString(set+"helpGiveComb").replace("&", "§");
        helpGiveMaq = cfg.getString(set+"helpGiveMaq").replace("&", "§");
        reload = cfg.getString(set+"reload").replace("&", "§");
        NaN = cfg.getString(set+"NaN").replace("&", "§");
        pNaoEnc = cfg.getString(set+"playerNaoEncontrado").replace("&", "§");
        combInv = cfg.getString(set+"combustivelErrado").replace("&", "§");
        noOwn = cfg.getString(set+"naoDono").replace("&", "§");
        abasteceu = cfg.getString(set+"abasteceu").replace("&", "§");
        jaAbastecida = cfg.getString(set+"jaAbastecida").replace("&", "§");
        colocada = cfg.getString(set+"colocouMaquina").replace("&", "§");
        retirada = cfg.getString(set+"retirouMaquina").replace("&", "§");
        nRetirou = cfg.getString(set+"naoRetirouMaquina").replace("&", "§");
        noMoney = cfg.getString(set+"moneyInsuficiente").replace("&", "§");
        noCash = cfg.getString(set+"cashInsuficiente").replace("&", "§");
        help.add(helpGive);
        help.add(helpNew);
        help.add(helpList);
        settings = "§9§lMáquina §6§lSettings";
    }
    
    public void saveItens(){
        chestON = Iapi.setLore(Iapi.setName(new ItemStack(54), "§eInventario §aON"),
                "§a<Left-Click> §epara abrir e",
                "§a<Right-Click> §epara mudar §a§lON§e/§c§lOFF",
                "", "§eOs drops comecarao a ir para o inventario",
                "§eda maquina, e dropara no chao quando estiver cheio!");
        chestOFF = Iapi.setLore(Iapi.setName(new ItemStack(54), "§eInventario §cOFF"),
                "§a<Left-Click> §epara abrir e",
                "§a<Right-Click> §epara mudar §a§lON§e/§c§lOFF",
                "", "§eOs drops irao dropar no chao!");
        hdON = Iapi.setName(new ItemStack(160, 1, (short)0, (byte)5), "§eHologramas §aON");
        hdOFF = Iapi.setName(new ItemStack(160, 1, (short)0, (byte)14), "§eHologramas §cOFF");
        lampON = Iapi.setName(new ItemStack(89), "§eMaquina §aON");
        lampOFF = Iapi.setName(new ItemStack(123), "§eMaquina §cOFF");
        upgrade = Iapi.setLore(Iapi.setName(new ItemStack(388), "§aUPGRADE"),
                "", "§eCusta §a%preco% §epara upar para o lvl §f%proxLv%");
    }
    
    public String helpGive,
            helpNew,
            helpList,
            onlyPlayers,
            helpNewComb,
            helpNewMaq,
            helpNewDrop,
            itemInvalido,
            helpGiveDrop,
            helpGiveComb,
            helpGiveMaq,
            NaN,
            pNaoEnc,
            settings,
            reload,
            combInv,
            noOwn,
            abasteceu,
            jaAbastecida,
            colocada,
            retirada,
            nRetirou,
            noMoney,
            noCash;
    public ArrayList<String> help;
    
    public ItemStack chestON,
            chestOFF,
            hdON,
            hdOFF,
            lampON,
            lampOFF,
            upgrade;
    
    
    
    public boolean isOwn(Player p, Machine maq){
        return p.getName().equalsIgnoreCase(maq.getOwn());
    }
    
    public void darItem(Player p, ItemStack item){
        for(int i:p.getInventory().addItem(item).keySet()){
            item.setAmount(i);
            Item I = (Item)p.getLocation().getWorld().dropItemNaturally(p.getLocation(), item);
            I.setPickupDelay(20);
        }
    }
    
    public boolean isDropOf(ItemStack item, Machine maq){
        ItemStack Item = item.clone();
        Item.setAmount(1);
        return Item.equals(maq.getType().getDrop().getDrop());
    }
    public boolean isCombOf(ItemStack item, Combustivel comb){
        ItemStack Item = item.clone();
        Item.setAmount(1);
        return Item.equals(comb.getCombustivel());
    }
    public boolean isCombOf(Combustivel comb, Machine maq){
        return maq.getType().getCombCompativeis().contains(comb);
    }
    public boolean isAir(ItemStack item){
        return (item==null || item.getType()==Material.AIR);
    }
    
}
