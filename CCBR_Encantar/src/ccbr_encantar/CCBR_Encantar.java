package ccbr_encantar;

import ccbr_encantar.listeners.Events;
import ccbr_encantar.utils.API;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CCBR_Encantar extends JavaPlugin {

    public static API api;
    public Events Events;
    
    @Override
    public void onEnable() {
        api = new API(this);
        Events = new Events(api);
        Bukkit.getPluginManager().registerEvents(Events, this);
    }

    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        
        return true;
    }
    
}
