package myhouse;

import myhouse.com.listeners.Listener;
import myhouse.com.listeners.ListenerUtils;
import myhouse.com.listeners.ProtectListener;
import myhouse.com.region.RegionManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MyHouse extends JavaPlugin{

    public Economy Economy;
    public RegionManager rm;
    public ListenerUtils lu;
    public PluginManager pm = Bukkit.getPluginManager();
    public ProtectListener PL;
    public Listener L;
    
    @Override
    public void onEnable() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if(economyProvider!=null) Economy = economyProvider.getProvider();
        else System.out.println("[MyHouse]: §4Nao foi possivel encontrar o ECONOMYPROVIDER do Vault!");
        PL = new ProtectListener();
        L = new Listener();
        rm = new RegionManager(this, Economy);
        lu = new ListenerUtils(rm);
        rm.setLu(lu);
        PL.setListU(lu);
        L.setListU(lu);
        pm.registerEvents(PL, this);
        pm.registerEvents(L, this);
        getCommand("myhouse").setTabCompleter(new MyTabCompleter(rm));
    }
    
    public void reload(){
        rm.ConfigU.reload();
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(!(snd instanceof Player)) return true;
        Player p = (Player)snd;
        if(cmd.equals("myhouse")){
            if(args.length>0){
                if(args[0].equalsIgnoreCase("claim")){
                    if(lu.Loc1s.containsKey(p) && lu.Loc2s.containsKey(p)){
                        if(args.length>1){
                            rm.createRegion(p, lu.Loc1s.get(p), lu.Loc2s.get(p), args[1].toLowerCase());
                        }else{
                            p.sendMessage("§e/mh claim <nome> §a- para comprar um novo terreno!");
                        }
                    }else{
                        p.sendMessage(rm.ConfigU.Lang.getString("selecioneLocal1").replace('&', '§'));
                    }
                }else if(args[0].equalsIgnoreCase("unclaim")){
                    rm.removeRegion(p);
                }else if(args[0].equalsIgnoreCase("setMensagemDeEntrada")){
                    rm.setMensagem(p, 0);
                }else if(args[0].equalsIgnoreCase("setMensagemDeSaida")){
                    rm.setMensagem(p, 2);
                }else if(args[0].equalsIgnoreCase("removerMensagemDeSaida")){
                    rm.setMensagem(p, 3);
                }else if(args[0].equalsIgnoreCase("removerMensagemDeEntrada")){
                    rm.setMensagem(p, 1);
                }
                else if(args[0].equalsIgnoreCase("help")){
                    if(args.length>1){
                        if(args[1].equalsIgnoreCase("claim")) p.sendMessage("§e/mh claim <nome> §a- para comprar um novo terreno!");
                        else if(args[1].equalsIgnoreCase("reload")) p.sendMessage("§e/mh reload - Recarrega todas as configurações!");
                        else if(args[1].equalsIgnoreCase("unclaim")) p.sendMessage("§e/mh unclaim §a- para deletar um terreno!");
                        else if(args[1].equalsIgnoreCase("expand")) p.sendMessage("§e/mh expand §a- para expandir um terreno!");
                        else if(args[1].equalsIgnoreCase("dardono")) p.sendMessage("§e/mh darDono <player> §a- para dar dono para outro player!");
                        else if(args[1].equalsIgnoreCase("trust")) p.sendMessage("§e/mh trust <player> §a- para adicionar um membro no terreno!");
                        else if(args[1].equalsIgnoreCase("untrust")) p.sendMessage("§e/mh untrust <player> §a- para remover um membro no terreno!");
                        else if(args[1].equalsIgnoreCase("info")) p.sendMessage("§e/mh info §a- para ver as informaçoes sobre o terreno!");
                        else if(args[1].equalsIgnoreCase("flag")) p.sendMessage("§e/mh flag <flag> <enable/disable> §a- para editar as flags do terreno!");
                        else if(args[1].equalsIgnoreCase("setwarp")) p.sendMessage("§e/mh setwarp §a- para setar a warp do terreno!");
                        else if(args[1].equalsIgnoreCase("warp")) p.sendMessage("§e/mh warp §a- para ir para um terreno!");
                        else if(args[1].equalsIgnoreCase("renomear")) p.sendMessage("§e/mh renomear <nome> §a- para dar um nome para seu terreno!");
                        else if(args[1].equalsIgnoreCase("delwarp")) p.sendMessage("§e/mh delwarp §a- para deletar a warp do terreno!");
                        else if(args[1].equalsIgnoreCase("vender")) p.sendMessage("§e/mh vender <preço> §a- para colocar o terreno à venda!");
                        else sendHelpMessage(p);
                    }else{
                        sendHelpMessage(p);
                    }
                }else if(args[0].equalsIgnoreCase("trust")){
                    if(args.length>1){
                        rm.addMember(p, args[1]);
                    }else{
                        p.sendMessage("§e/mh trust <player> §a- para adicionar um membro no terreno!");
                    }
                }else if(args[0].equalsIgnoreCase("untrust")){
                    if(args.length>1){
                        rm.remMember(p, args[1]);
                    }else{
                        p.sendMessage("§e/mh trust <player> §a- para adicionar um membro no terreno!");
                    }
                }else if(args[0].equalsIgnoreCase("setwarp")){
                    rm.setWarp(p);
                }else if(args[0].equalsIgnoreCase("warp")){
                    if(args.length>1){
                        StringBuilder sb = new StringBuilder(args[1]);
                        for(int i=2;i<args.length;i++) sb.append(' ').append(args[i]);
                        rm.warp(p, sb.toString());
                    }else{
                        p.sendMessage("§e/mh warp §a- para ir para um terreno!");
                    }
                }else if(args[0].equalsIgnoreCase("info")){
                    rm.sendInfo(p);
                }else if(args[0].equalsIgnoreCase("flag")){
                    if(args.length>2){
                        rm.setFlag(p, args[1], args[2]);
                    }else{
                        p.sendMessage("§e/mh flag <flag> <enable/disable> §a- para editar as flags do terreno!");
                    }
                }else if(args[0].equalsIgnoreCase("reload")){
                    if(rm.isOp(p)){
                        reload();
                        p.sendMessage("[MyHouse]: §aReload completo!");
                    }
                }else if(args[0].equalsIgnoreCase("delwarp")){
                    rm.delWarp(p);
                }else if(args[0].equalsIgnoreCase("vender")){
                    if(args.length>1){
                        int n = 0;
                        try{
                            n = Integer.parseInt(args[1]);
                        }catch(Exception e){
                            p.sendMessage(rm.ConfigU.Lang.getString("NaN").replace('&', '§').replace("{n}", args[1]));
                            return true;
                        }
                        rm.sellRegion(p, n);
                    }else{
                        p.sendMessage("§e/mh vender <preço> §a- para colocar o terreno à venda!");
                    }
                }else if(args[0].equalsIgnoreCase("dardono")){
                    if(args.length>1){
                        rm.transferOwnRegion(p, args[1]);
                    }else{
                        p.sendMessage("§e/mh darDono <player> §a- para dar dono para outro player!");
                    }
                }else if(args[0].equalsIgnoreCase("expand")){
                    if(lu.Loc1s.containsKey(p) && lu.Loc2s.containsKey(p)){
                        rm.expandRegion(p, lu.Loc1s.get(p), lu.Loc2s.get(p));
                    }else{
                        p.sendMessage(rm.ConfigU.Lang.getString("selecioneLocal2").replace('&', '§'));
                    }
                }else if(args[0].equalsIgnoreCase("renomear")){
                    if(args.length>1){
                        StringBuilder sb = new StringBuilder(args[1]);
                        for(int i=2;i<args.length;i++) sb.append(' ').append(args[i]);
                        rm.setCustomName(p, sb.toString());
                    }else{
                        p.sendMessage("§e/mh renomear <nome> §a- para dar um nome para seu terreno!");
                    }
                }else{
                    sendHelpMessage(p);
                }
            }else{
                sendHelpMessage(p);
            }
        }
        return true;
    }
    
    public void sendHelpMessage(Player p){
        p.sendMessage("§e--------My House--------");
        if(rm.isOp(p)) p.sendMessage("§e/mh reload");
        p.sendMessage("§e/mh help <arg>");
        p.sendMessage("§e/mh untrust <player>");
        p.sendMessage("§e/mh unclaim");
        p.sendMessage("§e/mh delwarp");
        p.sendMessage("§e/mh darDono <player>");
        p.sendMessage("§e/mh renomear <nome>");
        p.sendMessage("§e/mh info");
        p.sendMessage("§e/mh vender <preço>");
        p.sendMessage("§e/mh expand");
        p.sendMessage("§e/mh flag <flag> <enable/disable>");
        p.sendMessage("§e/mh trust <player>");
        p.sendMessage("§e/mh warp <terreno>");
        p.sendMessage("§e/mh setwarp");
        p.sendMessage("§e/mh claim <nome>");
    }
    
}