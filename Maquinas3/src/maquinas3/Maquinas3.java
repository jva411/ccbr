package maquinas3;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Maquinas3 extends JavaPlugin {
    
    static API api;
    
    @Override
    public void onEnable(){
        api = new API();
        Bukkit.getPluginManager().registerEvents(new Events(), this);
        api.saveDefaults(this);
    }
    @Override
    public void onDisable(){
        
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command cmd, String lb, String[] args){
        if(cmd.getName().equals("maquinas")){
            if(!snd.hasPermission("maquina.admin")) return true;
            if(args.length>0){
                if(args[0].equalsIgnoreCase("teste")) for(Entity e:((Player)snd).getNearbyEntities(0.5, 1, 0.5)) if(e instanceof ArmorStand) e.remove();
                if(args[0].equalsIgnoreCase("give")){
                    if(args.length>1){
                        if(args[1].equalsIgnoreCase("drop")){ 
                            if(args.length>4){
                                int amount = 0;
                                try{
                                    amount = Integer.parseInt(args[4]);
                                }catch(Exception e){snd.sendMessage(api.NaN.replace("%n%", args[4])); return false;}
                                String name = args[2];
                                String pn = args[3];
                                Player p = null;
                                if(name.equalsIgnoreCase("all")){
                                    for(Drop drop:api.Drops.values()) {
                                        ItemStack item = drop.getDrop();
                                        item.setAmount(amount);
                                        try{
                                            p = Bukkit.getPlayer(pn);
                                        }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                        api.darItem(p, item);
                                    }
                                    return true;
                                }
                                Drop drop = api.Drops.getOrDefault(name, null);
                                if(drop!=null){
                                    ItemStack item = drop.getDrop();
                                    item.setAmount(amount);
                                    try{
                                        p = Bukkit.getPlayer(pn);
                                    }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                    api.darItem(p, item);
                                }
                            }else snd.sendMessage(api.helpGiveComb);
                        }else if(args[1].equalsIgnoreCase("combustivel")){
                            if(args.length>4){
                                int amount = 0;
                                try{
                                    amount = Integer.parseInt(args[4]);
                                }catch(Exception e){snd.sendMessage(api.NaN.replace("%n%", args[4])); return false;}
                                String name = args[2];
                                String pn = args[3];
                                Player p = null;
                                if(name.equalsIgnoreCase("all")){
                                    for(Combustivel comb:api.Combs.values()) {
                                        ItemStack item = comb.getCombustivel();
                                        item.setAmount(amount);
                                        try{
                                            p = Bukkit.getPlayer(pn);
                                        }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                        api.darItem(p, item);
                                    }
                                    return true;
                                }
                                Combustivel comb = api.Combs.getOrDefault(name, null);
                                if(comb!=null){
                                    ItemStack item = comb.getCombustivel();
                                    item.setAmount(amount);
                                    try{
                                        p = Bukkit.getPlayer(pn);
                                    }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                    api.darItem(p, item);
                                }
                            }else snd.sendMessage(api.helpGiveComb);
                        }else if(args[1].equalsIgnoreCase("maquina")){
                            if(args.length>4){
                                int amount = 0;
                                try{
                                    amount = Integer.parseInt(args[4]);
                                }catch(Exception e){snd.sendMessage(api.NaN.replace("%n%", args[4])); return false;}
                                String name = args[2];
                                String pn = args[3];
                                Player p = null;
                                if(name.equalsIgnoreCase("all")){
                                    for(MachineType type:api.Mtypes.values()) {
                                        ItemStack item = type.getBlock();
                                        item.setAmount(amount);
                                        try{
                                            p = Bukkit.getPlayer(pn);
                                        }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                        api.darItem(p, item);
                                    }
                                    return true;
                                }
                                MachineType mt = api.Mtypes.getOrDefault(name, null);
                                if(mt!=null){
                                    ItemStack item = mt.getBlock();
                                    item.setAmount(amount);
                                    try{
                                        p = Bukkit.getPlayer(pn);
                                    }catch(Exception e){snd.sendMessage(api.pNaoEnc.replace("%p%", pn)); return false;}
                                    api.darItem(p, item);
                                }
                            }
                        }else snd.sendMessage(api.helpGiveMaq);
                    }else snd.sendMessage(api.helpGive);
                }else if(args[0].equalsIgnoreCase("list")){
                    if(args.length>1){
                        if(args[1].equalsIgnoreCase("drop")) for(String a:api.Drops.keySet()) snd.sendMessage(a);
                        else if(args[1].equalsIgnoreCase("combustivel")) for(String a:api.Combs.keySet()) snd.sendMessage(a);
                        else if(args[1].equalsIgnoreCase("maquina")) for(String a:api.Mtypes.keySet()) snd.sendMessage(a);
                        else snd.sendMessage(api.helpList);
                    }else snd.sendMessage(api.helpList);
                }else if(args[0].equalsIgnoreCase("new")){ 
                    if(snd instanceof Player){
                        Player p = (Player)snd;
                        if(args.length>1){
                            if(args[1].equalsIgnoreCase("combustivel")){
                                if(args.length>3){
                                    ItemStack comb = p.getItemInHand();
                                    int time = 60;
                                    if(api.isAir(comb)){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    if(comb.getItemMeta().getDisplayName()==null){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    try{
                                        time = Integer.parseInt(args[3]);
                                    }catch(Exception e){snd.sendMessage(api.NaN.replace("%n%", args[3])); return false;}
                                    ItemStack Comb = comb.clone();
                                    Comb.clone();
                                    api.setCombustivel(Comb, args[2], time, 1);
                                }else p.sendMessage(api.helpNewComb);
                            }else if(args[1].equalsIgnoreCase("maquina")){
                                if(args.length>4){
                                    ItemStack maq = p.getItemInHand();
                                    if(api.isAir(maq)){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    if(maq.getItemMeta().getDisplayName()==null){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    String displayName = args[4];
                                    if(args.length>5){
                                        for(int i=5;i<args.length;i++){
                                            displayName +=" "+args[i];
                                        }
                                    }
                                    ItemStack block = maq.clone();
                                    block.setAmount(1);
                                    int[] ups = new int[4];
                                    for(int i=0;i<4;i++) ups[i] = 0;
                                    api.setMachineType(block, args[2], new ArrayList<>(), api.Drops.getOrDefault(args[3], null), 1, displayName, ups);
                                }else p.sendMessage(api.helpNewMaq);
                            }else if(args[1].equalsIgnoreCase("drop")){
                                if(args.length>2){
                                    ItemStack comb = p.getItemInHand();
                                    if(api.isAir(comb)){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    if(comb.getItemMeta().getDisplayName()==null){
                                        p.sendMessage(api.itemInvalido);
                                        return false;
                                    }
                                    ItemStack Comb = comb.clone();
                                    Comb.clone();
                                    api.setDrop(Comb, args[2]);
                                }else p.sendMessage(api.helpNewDrop);
                            }else p.sendMessage(api.helpNew);
                        }else p.sendMessage(api.helpNew);
                    }else snd.sendMessage(api.onlyPlayers);
                }else if(args[0].equalsIgnoreCase("reload")){
                    api = new API();
                    api.saveDefaults(this);
                    snd.sendMessage(api.reload);
                }else for(String a:api.help) snd.sendMessage(a);
            }else for(String a:api.help) snd.sendMessage(a);
        }
        return true;
    }
    
}
