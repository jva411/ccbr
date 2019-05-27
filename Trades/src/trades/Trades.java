package trades;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Trades extends JavaPlugin{

    public HashMap<Player, Player> Traders = new HashMap<>();
    
    @Override
    public void onEnable() {
        
    }

    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(!(snd instanceof Player)) return false;
        Player p = (Player)snd;
        if(cmd.equals("trade")){
            if(args.length>0){
                Player p2 = Bukkit.getPlayer(args[0]);
                if(p2==null){
                    p.sendMessage("§cNão consegui encontrar o player "+args[0]);
                }else{
                    if(Traders.containsKey(p2)){
                        if(Traders.get(p2).equals(p)){
                            startTrade(p2, p);
                            Traders.remove(p);
                            Traders.remove(p2);
                            return true;
                        }
                    }
                    Traders.put(p, p2);
                    p.sendMessage("§aPedido enviado, aguarde ele aceitar!");
                    p2.sendMessage("§aO jogador "+p.getName()+" quer fazer uma troca com você!");
                }
            }else{
                p.sendMessage("/trade <player>");
            }
        }
        return true;
    }
    
    public void startTrade(Player sender, Player recivier){
        
    }
    
}
