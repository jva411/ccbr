package ccbr_cash3;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Api {
    
    public Main Main;
    public Economy economy;
    public Config Cash, Menu, Config, paypal, pagseguro, log;
    public Loja menu;
    public HashMap<Player, Loja> playerLoja = new HashMap<>();
    public ArrayList<Player> playerLoja2 = new ArrayList<>();
    public HashMap<Player, Mercadoria> playerMercadoria = new HashMap<>();
    public ArrayList<String> InvNames = new ArrayList<>();
    public ItemAPI Iapi = new ItemAPI();
    public ItemConstructor Icons = new ItemConstructor();
    public HashMap<String, String> using_ps = new HashMap<>(), using_pp = new HashMap<>();
    public HashMap<String, Integer> ids = new HashMap<>();
    
    public void Init(Main main){
        Main = main;
        Cash = new Config(main, "cash.yml");
        Menu = new Config(main, "menu.yml");
        Config = new Config(main, "config.yml");
        paypal = new Config(main, "paypal.log");
        pagseguro = new Config(main, "pagseguro.log");
        log = new Config(main, "Registro.log");
        Menu.saveDefaultConfig();
        Cash.saveDefaultConfig();
        Config.saveDefaultConfig();
        Icons.saveEnchs();
        saveMsgs();
        loadMenu();
        RegisteredServiceProvider<Economy> economyProvider = main.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider!=null) economy = economyProvider.getProvider();
        else System.out.println("[CCBR_Cash]: Nao foi possivel encontrar o ECONOMYPROVIDER do Vault!");
        for(String a:getSet(Config, "Cashs")) ids.put(a, Config.getInt("Cashs."+a));
    }
    
    public void openMenu(Player p){
        p.openInventory(menu.getInventory());
        playerLoja.put(p, menu);
    }
    
    public void openLoja(Player p, Loja loja){
        String perm = loja.getPermission();
        if(!perm.equals("CCBR_Cash.shop.")) if(!p.hasPermission(perm)){
            p.sendMessage(noPerm);
            return;
        }
        playerLoja2.add(p);
        p.openInventory(loja.getInventory());
        playerLoja.put(p, loja);
    }
    
    public void openMercadoria(Player p, Mercadoria merc){
        String perm = merc.getPermission();
        if(!perm.equals("CCBR_Cash.shop.")) if(!p.hasPermission(perm)){
            p.sendMessage(noPerm);
            return;
        }
        int cash = merc.getCash();
        double money = merc.getMoney();
        if(cash>0){
            if(getCash(p)<cash) {
                p.sendMessage(noCash);
                return;
            }
        }else{
            if(economy.getBalance(p.getName())<money){
                p.sendMessage(noMoney);
                return;
            }
        }
        ItemStack is = merc.getIcone();
        int n1 = (int)(Math.ceil((double)is.getMaxStackSize()/4d)), n2 = is.getMaxStackSize();
        Inventory inv = Bukkit.createInventory(null, 54, "§a§lCCBR §2§lConfirmação");
        ItemStack mais1 = Iapi.setName(Iapi.newItem(160, 5), "§aAdicionar 1"),
                mais10 = Iapi.setName(Iapi.newItem(160, 5, n1), "§aAdicionar "+n1),
                mais64 = Iapi.setName(Iapi.newItem(160, 5, n2), "§aAdicionar "+n2);
        ItemStack menos1 = Iapi.setName(Iapi.newItem(160, 14, -1), "§cRemover 1"),
                menos10 = Iapi.setName(Iapi.newItem(160, 14, -n1), "§cRemover "+n1),
                menos64 = Iapi.setName(Iapi.newItem(160, 14, -n2), "§cRemover "+n2);
        ItemStack confirmar = Iapi.setName(Iapi.newItem(35, 5), "§a§lCONFIRMAR"),
                cancelar = Iapi.setName(Iapi.newItem(35, 14), "§c§lCANCELAR");
        ItemStack preço;
        DecimalFormat df = new DecimalFormat("#.##");
        if(cash>0) preço = Iapi.setName(Iapi.newItem(339), "§e§l"+cash+" §a§lCASH");
        else preço = Iapi.setName(Iapi.newItem(339), "§a§lR$§e§l"+df.format(money).replace(',', '.')+" §a§lReais");
        inv.setItem(9, mais1);
        inv.setItem(10, mais10);
        inv.setItem(11, mais64);
        inv.setItem(17, menos1);
        inv.setItem(16, menos10);
        inv.setItem(15, menos64);
        inv.setItem(31, merc.getIcone());
        inv.setItem(47, cancelar);
        inv.setItem(51, confirmar);
        inv.setItem(49, preço);
        playerLoja2.add(p);
        p.openInventory(inv);
        playerMercadoria.put(p, merc);
    }
    
    
    
    
    
    
    
    public void darItem(Player p, ItemStack is, int amount){
        int n = is.getAmount(), n1 = is.getMaxStackSize(), n2=n1;
        amount *= n;
        if(amount<n1){
            is.setAmount(amount);
            darItem(p, is);
        }else{
            is.setAmount(n1);
            while(amount>0){
                darItem(p, is);
                amount -= n2;
                if(amount<n1 && amount>0){
                    is.setAmount(amount);
                    n2 = amount;
                }
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
    
    
    public void loadMenu(){
        menu = loadLoja(Menu, "MENU", "", Iapi.newItem(0));
        InvNames.add("§a§lCCBR §4§lMENU");
        menu.setLojaMae(saveLojaMaes(menu));
    }
    
    public Loja saveLojaMaes(Loja first){
        for(Loja loja:first.getLojas().values()){
            Loja Loja = saveLojaMaes(loja);
            Loja.setLojaMae(loja);
            loja.setLojaMae(first);
        }
        return first;
    }
    
    public Loja loadLoja(Config cfg, String loja, String permissao, ItemStack icone){
        if(!cfg.existeConfig()) cfg.saveConfig();
        HashMap<Integer, Loja> Lojas = new HashMap<>();
        HashMap<Integer, Mercadoria> Mercadorias = new HashMap<>();
        HashMap<Integer, ItemStack> Enfeites = new HashMap<>(), Voltar = new HashMap<>();
        if(!loja.equals("MENU")) {
            String newPath = cfg.getFile().getParent()+"\\"+loja+"\\"+loja+".yml";
            cfg = new Config(cfg.getPlugin(), newPath.replace("plugins\\CCBR_Cash\\", ""));
        }
        for(String a:getSet(cfg, "")){
            int lc = 0;
            boolean ok = false;
            try{
                lc = Integer.parseInt(a);
                ok = true;
            }catch(Exception e){}
            if(ok){
                ItemStack item = Icons.getItem(cfg.getString(a+".Item"));
                String permission = cfg.getString(a+".Permission");
                String locais = cfg.getString(a+".Locais");
                ArrayList<Integer> Locais = new ArrayList<>();
                Locais.add(lc);
                if(!(locais==null || locais.length()<=0)) for(String b:locais.split(",")) try{ Locais.add(Integer.parseInt(b)); } catch (Exception e) {}
                String type = cfg.getString(a+".Type");
                if(type.equalsIgnoreCase("Loja")) {
                    String name = cfg.getString(a+".Loja");
                    Loja Loja = loadLoja(cfg, name, permission, item);
                    InvNames.add(Loja.getInventory().getName());
                    for(int i:Locais) Lojas.put(i, Loja);
                }else if(type.equalsIgnoreCase("Mercadoria")){
                    int cash = cfg.getInt(a+".Cash");
                    double money = cfg.getDouble(a+".Money");
                    ArrayList<ItemStack> items = new ArrayList<>();
                    ArrayList<String> cmds = new ArrayList<>();
                    for(String b:cfg.getConfig().getStringList(a+".Items")) items.add(Icons.getItem(b));
                    for(String b:cfg.getConfig().getStringList(a+".Cmds")) cmds.add(b);
                    Mercadoria merc = new Mercadoria(item, money, cash, cmds, items, permission);
                    for(int i:Locais) Mercadorias.put(i, merc);
                }else if(type.equalsIgnoreCase("Voltar")){
                    for(int i:Locais) Voltar.put(i, item);
                }else if(type.equalsIgnoreCase("Enfeite")){
                    for(int i:Locais) Enfeites.put(i, item);
                }
            }
        }
        return new Loja(icone, permissao, loja, Enfeites, Mercadorias, Lojas, Voltar);
    }
    
    public Set<String> getSet(Config cfg, String path){
        return cfg.getConfig().getConfigurationSection(path).getKeys(false);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void imprimirLog(Player p, Mercadoria merc, int N){
        int cash = merc.getCash();
        if(cash<0) return;
        StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
        sb.append(getData()).append('.').append(p.getName()).append('.').append(getHora());
        sb2.append(p.getName()).append(" comprou ").append(merc.getIcone().getItemMeta().getDisplayName().replace('§', '&')).append(" por ").append(cash).append(' ').append(N).append(" vezes!");
        log.set(sb.toString(), sb2.toString());
        log.saveConfig();
    }
    public String getData(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    public String getHora(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
    
    
    
    
    
    
    public HashMap<String, Integer> getKeys(){
        HashMap<String, Integer> keys = new HashMap<>();
        for(String a:getSet(Config, "Keys")) keys.put(a, Config.getInt("Keys."+a));
        return keys;
    }
    
    
    
    
    
    public String gerarKey(){
        StringBuilder sb = new StringBuilder();
        char[] cs = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O'
                ,'P','Q','R','S','T','U','V','W','X','Y','Z','0','1','2','3','4','5','6','7','8','9'};
        Random rd = new Random();
        for(int i=0;i<10;i++) {
            sb.append(cs[rd.nextInt(35)]);
        }
        return sb.toString();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public String noPerm, noCash, noMoney,
            seuCash, outroCash,
            addCash, remCash, setCash,
            helpADD, helpREM, helpSET, helpHELP, helpOUTRO,
            PnE, NaN, Reload,
            keycNew, keycUse, keycHelpNew, keycHelpUse, keycHelpList,
            listTop, listIndice;
    
    public void saveMsgs(){
        noPerm = Config.getString("Msgs.noPermission").replace('&', '§');
        noCash = Config.getString("Msgs.Cash.noCash").replace('&', '§');
        noMoney = Config.getString("Msgs.Cash.noMoney").replace('&', '§');
        seuCash = Config.getString("Msgs.Cash.seuCash").replace('&', '§');
        outroCash = Config.getString("Msgs.Cash.outroCash").replace('&', '§');
        addCash = Config.getString("Msgs.Cash.addCash").replace('&', '§');
        remCash = Config.getString("Msgs.Cash.remCash").replace('&', '§');
        setCash = Config.getString("Msgs.Cash.setCash").replace('&', '§');
        helpADD = Config.getString("Msgs.Help.add").replace('&', '§');
        helpREM = Config.getString("Msgs.Help.rem").replace('&', '§');
        helpSET = Config.getString("Msgs.Help.set").replace('&', '§');
        helpHELP = Config.getString("Msgs.Help.help").replace('&', '§');
        helpOUTRO = Config.getString("Msgs.Help.outro").replace('&', '§');
        PnE = Config.getString("Msgs.PnE").replace('&', '§');
        NaN = Config.getString("Msgs.NaN").replace('&', '§');
        Reload = Config.getString("Msgs.Reload");
        keycNew = Config.getString("Msgs.Keyc.newKey").replace('&', '§');
        keycUse = Config.getString("Msgs.Keyc.useKey").replace('&', '§');
        keycHelpNew = Config.getString("Msgs.Keyc.Help.new").replace('&', '§');
        keycHelpUse = Config.getString("Msgs.Keyc.Help.use").replace('&', '§');
        keycHelpList = Config.getString("Msgs.Keyc.Help.list").replace('&', '§');
        listTop = Config.getString("Msgs.Keyc.listTop").replace('&', '§');
        listIndice = Config.getString("Msgs.Keyc.listIndice").replace('&', '§');
    }
    
    
    
    
    
    
    
    
    
    public int getCash(Player p) { return getCash(p.getName()); }
    public int getCash(String pn){
        pn = pn.toLowerCase();
        if(Cash.contains(pn)) return Cash.getInt(pn);
        else return -1;
    }
    
    public void remCash(Player p, int cash){ remCash(p.getName(), cash); }
    public void remCash(String pn, int cash){
        setCash(pn, getCash(pn) + (cash>0 ? -cash : cash));
    }
    
    public void addCash(Player p, int cash){ addCash(p.getName(), cash); }
    public void addCash(String pn, int cash){
        setCash(pn, getCash(pn) + (cash<0 ? -cash : cash));
    }
    
    public void setCash(Player p, int cash){ setCash(p.getName(), cash );}
    public void setCash(String pn, int cash) {
        Cash.set(pn.toLowerCase(), cash<0 ? 0 : cash);
        Cash.saveConfig();
    }
    
}
