package tempo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Tempo extends JavaPlugin implements Listener{

    private final HashMap<Player, MyRunnable> playerRunnable = new HashMap<>();
    private final HashMap<Player, Integer> playerTask = new HashMap<>();
    private HashMap<Integer, Prize> Prizes;
    private Config Cfg;
    private final ItemConstructor Icons = new ItemConstructor();
    
    @Override
    public void onEnable() {
        Cfg = new Config(this, "config.yml");
        loadPrizes();
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(cmd.equals("tempo")){
            if(snd instanceof Player){
                Player p = (Player)snd;
                if(p.hasPermission("tempo.adm")){
                    if(args.length>0){
                        String a0 = args[0].toLowerCase();
                        if(a0.equals("reload")){
                            Cfg = new Config(this, "config.yml");
                            loadPrizes();
                        }else if(a0.equals("set")){
                            if(args.length>2){                                
                                Player p1;
                                try{p1 = Bukkit.getPlayer(args[1]);}catch(Exception ex){p.sendMessage("§cNão consegui encontrar nenhum §e"+args[1]+"§c online!");return false;}
                                int n;
                                try{n = Integer.parseInt(args[1]);}catch(Exception ex){p.sendMessage("§cO valor §e"+args[1]+"§c não é um número válido!");return false;}
                                playerRunnable.get(p1).time = n;
                            }
                        }else{
                            Player p1;
                            try{p1 = Bukkit.getPlayer(args[0]);}catch(Exception ex){p.sendMessage("§cNão consegui encontrar nenhum §e"+args[1]+"§c online!");return false;}
                            p.sendMessage(getTempo(p1));
                        }
                    }else{
                        p.sendMessage(getTempo(p));
                    }
                }else{
                    p.sendMessage(getTempo(p));
                }
            }
        }
        return true;
    }
    
    private String getTempo(Player p){
        int time = playerRunnable.get(p).time;
        StringBuilder sb = new StringBuilder("§aSeu tempo online é de §e");
        int hours = 0, minutes = 0, seconds = 0;
        if(time<60) seconds = time;
        else if(time<3600) {
            minutes = time/60;
            seconds = time%60;
        }else{
            hours = time/3600;
            minutes = (time%3600)/60;
            seconds = (time%3600)%60;
        }
        sb.append(hours==0 ? "00" : hours>9 ? hours : "0"+hours).append("§0:§e");
        sb.append(hours==0 ? "00" : minutes>9 ? minutes : "0"+minutes).append("§0:§e");
        sb.append(hours==0 ? "00" : seconds>9 ? seconds : "0"+seconds);
        return sb.toString();
    }
    
    private void loadPrizes(){
        Prizes = new HashMap<>();
        for(String a:Cfg.getConfig().getConfigurationSection("").getKeys(false)){
            try{
                int time = Integer.parseInt(a);
                ArrayList<ItemStack> Items = new ArrayList<>();
                ArrayList<String> Cmds = new ArrayList<>();
                boolean repeat = Cfg.getBoolean(a+".Repeat");
                for(String b:Cfg.getConfig().getStringList(a+".Commands")) Cmds.add(b);
                for(String b:Cfg.getConfig().getStringList(a+".Items")) {
                    try{Items.add(Icons.getItem(b));}catch(Exception ex){ System.out.println("&e[Tempo]: §4ERROR: §fErro ao carregar o item: §e"+b+" §fno tempo: §e"+a);}
                }
                Prize p = new Prize(time, Items, Cmds, repeat);
                Prizes.put(time, p);
            }catch(Exception ex){
                System.out.println("&e[Tempo]: §4ERROR: §fErro ao carregar o tempo §e"+a);
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        final MyRunnable mr = new MyRunnable(p);
        playerRunnable.put(p, mr);
        playerTask.put(p, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, mr, 0, 20));
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        playerRunnable.remove(e.getPlayer());
        Bukkit.getScheduler().cancelTask(playerTask.get(e.getPlayer()));
        playerTask.remove(e.getPlayer());
    }
    
    public class MyRunnable implements Runnable{

        public MyRunnable(Player p) {
            this.P = p;
        }
        
        public final Player P;
        public int time = 0;
        @Override
        public void run(){
            if(Prizes.containsKey(time)){
                Prizes.get(time).giveToPlayer(P);
            }else{
                for(int i:Prizes.keySet()){
                    if(time%i==0){
                        Prizes.get(i).giveToPlayer(P);
                        break;
                    }
                }
            }
            time++;
        }   
    }
    
}
