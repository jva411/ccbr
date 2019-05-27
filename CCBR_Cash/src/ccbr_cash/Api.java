package ccbr_cash;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Api {
    
    public static Main Main;
    public Meta Meta;
    public Economy economy;
    public Config Cash, Menu, Config, paypal, pagseguro, log;
    public Loja menu;
    public HashMap<Player, Loja> playerLoja = new HashMap<>();
    public ArrayList<Player> playerLoja2 = new ArrayList<>();
    public HashMap<Player, Mercadoria> playerMercadoria = new HashMap<>();
    public ArrayList<String> InvNames = new ArrayList<>();
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
        Meta = new Meta(Config);
        log = new Config(main, "Registro.data");
        Menu.saveDefaultConfig();
        Cash.saveDefaultConfig();
        Config.saveDefaultConfig();
        saveMsgs();
        loadMenu();
        RegisteredServiceProvider<Economy> economyProvider = main.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider!=null) economy = economyProvider.getProvider();
        else System.out.println("[CCBR_Cash]: Nao foi possivel encontrar o ECONOMYPROVIDER do Vault!");
        for(String a:getSet(Config, "Cashs")) ids.put(a, Config.getInt("Cashs."+a));
    }
    
    public void openMenu(Player p){
        if(menu==null){
            p.sendMessage();
            return;
        }
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
        int cash = getOutDiscount(p, merc.getCash());
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
        if(!p.hasPermission("CCBR_Cash.adm")){
            StringBuilder sb = new StringBuilder();
            sb.append(p.getName().toLowerCase()).append(getData()).append('.').append('.').append(getHora());
            String mercadoria = merc.getIcone().getItemMeta().hasDisplayName() ? merc.getIcone().getItemMeta().getDisplayName().replace('§', '&') : merc.getIcone().getType().toString();
            StringBuilder Loja = getLoja(playerLoja.get(p), new StringBuilder());
            sb.append(Loja).append('/').append(mercadoria);
            for(String a:log.getConfig().getStringList(p.getName().toLowerCase()+".Unicos")){
                if(a.equals(sb.toString())){
                    p.sendMessage(jaComprou);
                    return;
                }
            }
        }
        ItemStack is = merc.getIcone();
        int n1 = (int)(Math.ceil((double)is.getMaxStackSize()/4d)), n2 = is.getMaxStackSize();
        Inventory inv = Bukkit.createInventory(null, 54, "§a§lCCBR §2§lConfirmação");
        IsBuilder ib = new IsBuilder();
        ItemStack mais1 = ib.newItem(Material.LIME_STAINED_GLASS_PANE).setName("§aAdicionar 1").getItemStack(),
                mais10 = ib.newItem(Material.LIME_STAINED_GLASS_PANE, n1).setName("§aAdicionar "+n1).getItemStack(),
                mais64 = ib.newItem(Material.LIME_STAINED_GLASS_PANE, n2).setName("§aAdicionar "+n2).getItemStack();
        ItemStack menos1 = ib.newItem(Material.RED_STAINED_GLASS_PANE, 1).setName("§cRemover 1").getItemStack(),
                menos10 = ib.newItem(Material.RED_STAINED_GLASS_PANE, n1).setName("§cRemover "+n1).getItemStack(),
                menos64 = ib.newItem(Material.RED_STAINED_GLASS_PANE, n2).setName("§cRemover "+n2).getItemStack();
        ItemStack confirmar = ib.newItem(Material.LIME_WOOL).setName("§a§lCONFIRMAR").getItemStack(),
                cancelar = ib.newItem(Material.RED_WOOL).setName("§c§lCANCELAR").getItemStack();
        ItemStack preço;
        DecimalFormat df = new DecimalFormat("#.##");
        if(cash>0) preço = ib.newItem(Material.PAPER).setName("§e§l"+cash+" §a§lCASH").getItemStack();
        else preço = ib.newItem(Material.PAPER).setName("§a§lR$§e§l"+df.format(money).replace(',', '.')+" §a§lReais").getItemStack();
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
    
    public static void darItem(Player p, ItemStack is){
        HashMap<Integer, ItemStack> itens = p.getInventory().addItem(is);
        for(int i:itens.keySet()) {
            is.setAmount(i);
            Item Item = p.getLocation().getWorld().dropItemNaturally(p.getLocation(), is);
            Item.setPickupDelay(20);
        }
    }
    
    
    public void loadMenu(){
        menu = loadLoja(Menu, "MENU", "", new ItemStack(Material.AIR));
        if(menu==null) {
            System.out.println("§e[CCBR_Cash]: §cERROR: Houve um erro ao cerregar o menu!");
            return;
        }
        InvNames.add("§a§lCCBR §4§lMENU");
        menu.setLojaMae(saveLojaMaes(menu));
    }
    
    public Loja saveLojaMaes(Loja first){
        if(first==null) return null;
        for(Loja loja:first.getLojas().values()){
            Loja Loja = saveLojaMaes(loja);
            Loja.setLojaMae(loja);
            loja.setLojaMae(first);
        }
        return first;
    }
    
    public Loja loadLoja(Config cfg, String loja, String permissao, ItemStack icone){
        try{
        if(!cfg.existeConfig()) cfg.saveConfig();
        HashMap<Integer, Loja> Lojas = new HashMap<>();
        HashMap<Integer, Mercadoria> Mercadorias = new HashMap<>();
        HashMap<Integer, ItemStack> Enfeites = new HashMap<>();
        HashMap<Integer, ItemStack> Voltar = new HashMap<>();
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
                    if(Loja != null){
                        InvNames.add(Loja.getInventory().getName());
                        for(int i:Locais) Lojas.put(i, Loja);
                    }
                }else if(type.equalsIgnoreCase("Mercadoria")){
                    int cash = cfg.getInt(a+".Cash");
                    double money = cfg.getDouble(a+".Money");
                    boolean theOne = cfg.getBoolean(a+".compraUnica");
                    ArrayList<ItemStack> items = new ArrayList<>();
                    ArrayList<String> cmds = new ArrayList<>();
                    for(String b:cfg.getConfig().getStringList(a+".Items")) items.add(Icons.getItem(b));
                    for(String b:cfg.getConfig().getStringList(a+".Cmds")) cmds.add(b);
                    Mercadoria merc = new Mercadoria(item, money, cash, cmds, items, permission, theOne);
                    for(int i:Locais) Mercadorias.put(i, merc);
                }else if(type.equalsIgnoreCase("Voltar")){
                    for(int i:Locais) Voltar.put(i, item);
                }else if(type.equalsIgnoreCase("Enfeite")){
                    for(int i:Locais) Enfeites.put(i, item);
                }
            }
        }
        return new Loja(icone, permissao, loja, Enfeites, Mercadorias, Lojas, Voltar);
        }catch(Exception e){
            System.out.println("§l[CCBR_Cash]: §cErro ao carrega a loja "+cfg.getFile().getAbsolutePath().replace("\\", "/").replaceFirst(Main.getDataFolder().getAbsolutePath().replace("\\", "/"), ""));
            return null;
        }
    }
    
    public Set<String> getSet(Config cfg, String path){
        try{
            return cfg.getConfig().getConfigurationSection(path).getKeys(false);
        }catch(Exception e){
            return new HashSet<>();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void imprimirLog(Player p, Loja loja, Mercadoria merc, int N){
        int cash = getOutDiscount(p, merc.getCash());
        if(cash<0) return;
        StringBuilder sb = new StringBuilder(), sb2 = new StringBuilder();
        sb.append(p.getName().toLowerCase()).append(getData()).append('.').append('.').append(getHora());
        String mercadoria = merc.getIcone().getItemMeta().hasDisplayName() ? merc.getIcone().getItemMeta().getDisplayName().replace('§', '&') : merc.getIcone().getType().toString();
        StringBuilder Loja = getLoja(loja, new StringBuilder());
        sb2.append(p.getName()).append(" comprou ").append(mercadoria).append(" por ").append(cash).append("de cash na loja ").append(Loja).append(", ").append(N).append(" vezes!");
        log.set(sb.toString(), sb2.toString());
        if(merc.isTheOne()){
            if(!p.hasPermission("CCBR_Cash.adm")){
                ArrayList<String> strA = new ArrayList<>(log.getConfig().getStringList(p.getName().toLowerCase()+".Unicos"));
                strA.add(new StringBuilder(loja.toString()).append('/').append(mercadoria).toString());
                log.set(p.getName().toLowerCase()+".Unicos", strA);
            }
        }
        log.saveConfig();
    }
    public StringBuilder getLoja(Loja loja, StringBuilder sb){
        if(loja.getLojaMae()!=null) getLoja(loja.getLojaMae(), sb).append('/');
        else sb.append(loja.getName());
        return sb;
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
            listTop, listIndice,
            erroLoja,
            jaComprou, minimoR;
    
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
        erroLoja = Config.getString("Msgs.Shop.erroAoCarregarLoja").replace('&', '§');
        jaComprou = Config.getString("Msgs.Shop.jaComprou").replace('&', '§');
        minimoR = Config.getString("Msgs.Shop.minimoRequirido").replace('&', '§').replace("%min%", Config.getInt("MinimoRequirido")+"");
    }
    
    
    
    
    
    
    public int getOutDiscount(Player p, int cash){
        double discount = 1;
        for(int i=99;i>-1;i--) if(p.hasPermission("CCBR_Cash.discount."+i)){
            discount -= i/100;
            break;
        }
        return (int)Math.floor(cash*discount);
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
