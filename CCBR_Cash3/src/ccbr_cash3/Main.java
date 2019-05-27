package ccbr_cash3;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
    
    public Api api;
    public Events events;
    
    @Override
    public void onEnable(){
        events = new Events();
        load();
        Bukkit.getPluginManager().registerEvents(events, this);
    }
    
    public void load(){
        api = new Api();
        api.Init(this);
        events.Init(api);
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(cmd.equals("shop")){
            if(snd instanceof Player){
                api.openMenu((Player)snd);
            }
        }else if(cmd.equals("cash")){
            if(snd.hasPermission("CCBR_Cash.cash.adm")){
                if(args.length>0){
                    if(args[0].equalsIgnoreCase("add")){
                        if(args.length>2){
                            Player p2 = Bukkit.getPlayer(args[1]);
                            if(p2==null) {
                                snd.sendMessage(api.PnE.replace("%p%", args[1]));
                                return false;
                            }
                            int cash = 0;
                            try{
                                cash = Integer.parseInt(args[2]);
                            }catch(Exception e){ snd.sendMessage(api.NaN); return false; }
                            if(args.length>3){
                                if(args[3].equalsIgnoreCase("bought")){
                                    snd.sendMessage(api.addCash.replace("%p%", args[1]).replace("%cash%", args[2]).replace("%newcash%", ""+(cash+api.getCash(p2))));
                                    getServer().getPluginManager().callEvent(new CashEvent(cash, p2, CashEvent.Origem.BOUGHT));
                                }
                            }
                            snd.sendMessage(api.addCash.replace("%p%", args[1]).replace("%cash%", args[2]).replace("%newcash%", ""+(cash+api.getCash(p2))));
                            getServer().getPluginManager().callEvent(new CashEvent(cash, p2, CashEvent.Origem.CMD));
                        }else snd.sendMessage(api.helpADD);
                    }else if(args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("remove")){
                        if(args.length>2){
                            Player p2 = Bukkit.getPlayer(args[1]);
                            if(p2==null) {
                                snd.sendMessage(api.PnE.replace("%p%", args[0]));
                                return false;
                            }
                            int cash = 0;
                            try{
                                cash = Integer.parseInt(args[2]);
                            }catch(Exception e){ snd.sendMessage(api.NaN); return false; }
                            snd.sendMessage(api.remCash.replace("%p%", args[1]).replace("%cash%", args[2]).replace("%newcash%", ""+(api.getCash(p2)-cash)));
                            getServer().getPluginManager().callEvent(new CashEvent(-cash, p2, CashEvent.Origem.CMD));
                        }else snd.sendMessage(api.helpREM);
                    }else if(args[0].equalsIgnoreCase("set")){
                        if(args.length>2){
                            Player p2 = Bukkit.getPlayer(args[1]);
                            if(p2==null) {
                                snd.sendMessage(api.PnE.replace("%p%", args[0]));
                                return false;
                            }
                            int cash = 0;
                            try{
                                cash = Integer.parseInt(args[2]);
                            }catch(Exception e){ snd.sendMessage(api.NaN); return false; }
                            snd.sendMessage(api.setCash.replace("%p%", args[1]).replace("%cash%", args[2]));
                            getServer().getPluginManager().callEvent(new CashEvent(cash, p2, CashEvent.Origem.CMDS));
                        }else snd.sendMessage(api.helpSET);
                    }else if(args[0].equalsIgnoreCase("help")){
                        snd.sendMessage(api.helpADD);
                        snd.sendMessage(api.helpREM);
                        snd.sendMessage(api.helpSET);
                        snd.sendMessage(api.helpOUTRO);
                        snd.sendMessage(api.helpHELP);
                        return true;
                    }else if(args[0].equalsIgnoreCase("reload")){
                        load();
                        snd.sendMessage(api.Reload);
                    }else{
                        Player p2 = Bukkit.getPlayer(args[0]);
                        if(p2==null){
                            snd.sendMessage(api.PnE.replace("%p%", args[0]));
                            return false;
                        }
                        snd.sendMessage(api.outroCash.replace("%p%", args[0]).replace("%cash%", api.getCash(p2)+""));
                        return true;
                    }
                }else{
                    if(snd instanceof Player){
                        Player p = (Player)snd;
                        p.sendMessage(api.seuCash.replace("%cash%", api.getCash(p)+""));
                        return true;
                    }
                }
            }else{
                if(snd instanceof Player){
                    Player p = (Player)snd;
                    p.sendMessage(api.seuCash.replace("%cash%", api.getCash(p)+""));
                    return true;
                }
            }
        }else if(cmd.equals("keyc")){
            if(args.length>0){
                if(args[0].equalsIgnoreCase("pagseguro") ||args[0].equalsIgnoreCase("ps")){
                    if(!(snd instanceof Player)){
                        return false;
                    }
                    Player p = (Player)snd;
                    if(api.Config.getBoolean("PagSeguro.use")){
                        if(args.length>1){
                            if(api.using_ps.containsKey(args[1].toUpperCase())){
                                p.sendMessage("§cEsse codigo ja esta sendo usado");
                            }else if(api.pagseguro.contains(args[1].toUpperCase())){
                                p.sendMessage("§cEsse codigo ja foi usado");
                            }else{
                                api.using_ps.put(args[1], "");
                                PagSeguro ps = new PagSeguro(this, args[1], p);
                                ps.start();
                            }
                        }else p.sendMessage("§fuse /keyc ps <codigo>");
                    }else p.sendMessage("§4O sistema §b§lKEYC §4do §b§lPagSeguro§4 esta desligado!");
                    return true;
                }else if(args[0].equalsIgnoreCase("paypal") || args[0].equalsIgnoreCase("pp")){
                    if(!(snd instanceof Player)){
                        return false;
                    }
                    Player p = (Player)snd;
                    if(api.Config.getBoolean("PayPal.use")){
                        if(args.length>1){
                            if(api.using_ps.containsKey(args[1].toUpperCase())){
                                p.sendMessage("§cEsse codigo ja esta sendo usado");
                            }else if(api.pagseguro.contains(args[1].toUpperCase())){
                                p.sendMessage("§cEsse codigo ja foi usado");
                            }else{
                                api.using_pp.put(args[1], "");
                                Paypal pp = new Paypal(this, args[1], p);
                                pp.start();
                            }
                        }else p.sendMessage("§fuse /keyc pp <codigo>");
                    }else p.sendMessage("§4O sistema §b§lKEYC §4do §b§lPayPal§4 esta desligado!");
                    return true;
                }else if(snd.hasPermission("CCBR_Cash.keyc.adm")){
                    if(args[0].equalsIgnoreCase("use")){
                        if(snd instanceof Player){
                            Player p = (Player)snd;
                            if(args.length>1){
                                String key = args[1];
                                if(api.Config.contains("Keys."+key)){
                                    int cash = api.Config.getInt("Keys."+key);
                                    Bukkit.getPluginManager().callEvent(new CashEvent(cash, p, CashEvent.Origem.KEY));
                                    p.sendMessage(api.keycUse.replace("%key%", key).replace("%cash%", cash+""));
                                    api.Config.set("Keys."+key, null);
                                    api.Config.saveConfig();
                                    return true;
                                }
                            }else {
                                p.sendMessage(api.keycHelpUse);
                                return false;
                            }
                        }
                    }else if(args[0].equalsIgnoreCase("new")){
                        if(args.length>1){
                            int cash = 0;
                            try{
                                cash = Integer.parseInt(args[1]);
                            }catch(Exception e){ snd.sendMessage(api.NaN); return false;}
                            String key = api.gerarKey();
                            api.Config.set("Keys."+key, cash);
                            api.Config.saveConfig();
                            snd.sendMessage(api.keycNew.replace("%key%", key).replace("%cash%", cash+""));
                            return true;
                        }else {
                            snd.sendMessage(api.keycHelpNew);
                            return false;
                        }
                    }else if(args[0].equalsIgnoreCase("list")){
                        int page = 0;
                        if(args.length>1) try{ page = Integer.parseInt(args[1]); }catch(Exception e){ snd.sendMessage(api.NaN); return false; }
                        HashMap<String, Integer> keys = api.getKeys();
                        int lastPage = ((int)Math.ceil((double)keys.size()/10d))-1;
                        if(page>lastPage) page = lastPage;
                        snd.sendMessage(api.listTop.replace("%page%", (page+1)+"").replace("%total%", (lastPage+1)+""));
                        try{
                            ArrayList<String> Keys = new ArrayList<>();
                            ArrayList<Integer> Cashs = new ArrayList<>();
                            for(String a:keys.keySet()) Keys.add(a);
                            for(int i:keys.values()) Cashs.add(i);
                            for(int i=page*10;i<(page*10)+10;i++){
                                String key = Keys.get(i);
                                int cash = Cashs.get(i);
                                snd.sendMessage(api.listIndice.replace("%key%", key).replace("%cash%", cash+""));
                            }
                        }catch(Exception e){ return false;}
                        return true;
                    }else {
                        snd.sendMessage(api.keycHelpUse);
                        snd.sendMessage(api.keycHelpNew);
                        snd.sendMessage(api.keycHelpList);
                        return true;
                    }
                }else{
                    if(snd instanceof Player){
                        Player p = (Player)snd;
                        if(args.length>1){
                            if(args[0].equals("use")){
                                String key = args[1];
                                if(api.Config.contains("Keys."+key)){
                                    int cash = api.Config.getInt("Keys."+key);
                                    api.Config.saveConfig();
                                    Bukkit.getPluginManager().callEvent(new CashEvent(cash, p, CashEvent.Origem.KEY));
                                    p.sendMessage(api.keycUse.replace("%key%", key).replace("%cash%", cash+""));
                                    api.Config.set("Keys."+key, null);
                                }
                            }else {
                                p.sendMessage(api.keycHelpUse);
                                return true;
                            }
                        }else {
                            p.sendMessage(api.keycHelpUse);
                            return true;
                        }
                    }
                }
            }else{
                snd.sendMessage(api.keycHelpUse);
                if(snd.hasPermission("CCBR_Cash.keyc.adm")) {
                    snd.sendMessage(api.keycHelpNew);
                    snd.sendMessage(api.keycHelpList);
                }
                return true;
            }
        }
        return true;
    }
    
}
