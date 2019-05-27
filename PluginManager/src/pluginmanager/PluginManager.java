package pluginmanager;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManager extends JavaPlugin{
    
    org.bukkit.plugin.PluginManager pm;
    
    @Override
    public void onEnable(){
        pm = Bukkit.getPluginManager();
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args){
        String cmd = Cmd.getName();
        int n = args.length;
        if(cmd.equals("PluginManager")){
            if(!snd.hasPermission("PluginManager.adm")) return true;
            if(n>0){
                if(args[0].equalsIgnoreCase("load")){
                    if(n>2){
                        try{
                            Plugin pl = pm.getPlugin(args[1]);
                            if(pl.isEnabled()){
                                snd.sendMessage("§cEsse plugin ja esta habilitado!");
                                return true;
                            }
                            if(args[2].equalsIgnoreCase("s")) pm.enablePlugin(pm.loadPlugin(new File(getDataFolder()+"/../"+args[1]+".jar")));
                            else pm.enablePlugin(pl);
                        }catch(Exception e){
                            try{
                                pm.enablePlugin(pm.loadPlugin(new File(getDataFolder()+"/../"+args[1]+".jar")));
                            }catch(Exception ex){
                                snd.sendMessage("§cNão foi possível reconhecer o plugin "+args[0]+'!');
                            }
                        }
                    }else snd.sendMessage("§e/plm load <plugin> <s/n> para carregar um novo plugin!");
                }else if(args[0].equalsIgnoreCase("reload")){
                    if(n>2){
                        try{
                            Plugin pl = pm.getPlugin(args[1]);
                            if(pl.isEnabled()) pm.disablePlugin(pl);
                            if(args[2].equalsIgnoreCase("s")) pm.enablePlugin(pm.loadPlugin(new File(getDataFolder()+"/../"+args[1]+".jar")));
                            else pm.enablePlugin(pl);
                        }catch(Exception e){
                            snd.sendMessage("§cNão foi possível reconhecer o plugin "+args[0]+'!');
                        }
                    }else snd.sendMessage("§e/plm reload <plugin> <s/n> para recarregar um plugin!");
                }else if(args[0].equalsIgnoreCase("unload")){
                    if(n>1){
                        try{
                            Plugin pl = pm.getPlugin(args[1]);
                            if(pl.isEnabled()) pm.disablePlugin(pl);
                        }catch(Exception e){
                            snd.sendMessage("§cNão foi possível reconhecer o plugin "+args[0]+'!');
                        }
                    }else snd.sendMessage("§e/plm unload <plugin> para descarregar um plugin!");
                }else{
                    snd.sendMessage("§e/plm load <plugin> <s/n> para carregar um plugin!");
                    snd.sendMessage("§e/plm reload <plugin> <s/n> para recarregar um plugin!");
                    snd.sendMessage("§e/plm unload <plugin> para descarregar um plugin!");
                    snd.sendMessage("§e<s/n> 's' para carregar um plugin pelo .jar na pasta!");
                    snd.sendMessage("§e<s/n> 'n' para carregar um plugin que ja foi registrado!");
                }
            }else{
                snd.sendMessage("§e/plm load <plugin> para carregar um novo plugin!");
                snd.sendMessage("§e/plm reload <plugin> para recarregar um plugin!");
                snd.sendMessage("§e/plm unload <plugin> para descarregar um plugin!");
                snd.sendMessage("§e<s/n> 's' para carregar um plugin pelo .jar na pasta!");
                snd.sendMessage("§e<s/n> 'n' para carregar um plugin que ja foi registrado!");
            }
        }
        return true;
    }
    
}
