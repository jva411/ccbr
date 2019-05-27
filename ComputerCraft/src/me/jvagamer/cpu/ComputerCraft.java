package me.jvagamer.cpu;

import me.jvagamer.cpu.com.Filter;
import me.jvagamer.cpu.com.SuperStack;
import me.jvagamer.cpu.com.component.HD;
import me.jvagamer.cpu.com.component.HD_Data;
import me.jvagamer.cpu.com.machine.Items;
import me.jvagamer.cpu.com.machine.MachineManager;
import me.jvagamer.cpu.listeners.Listenner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.jvagamer.cpu.com.Sucker;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class ComputerCraft extends JavaPlugin{

    public static ComputerCraft ComputerCraft;
    
    @Override
    public void onEnable() {
        ComputerCraft = this;
        Bukkit.getPluginManager().registerEvents(new Listenner(), this);
        MachineManager.loadMachines();
        getCommand("computercraft").setTabCompleter(new TabCompleter() {

            @Override
            public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] args) {
                if(!cs.hasPermission("ComputerCraft.adm")) return new ArrayList<>();
                ArrayList<String> Arr = new ArrayList<>();
                String a0 = args[0].toLowerCase(), a0R = getRegex(a0);
                if(a0.equals(cmnd.getName())){
                    return Arr;
                }
                String[] args0 = new String[]{"give", "reload"};
                for(String a:args0) if(a.matches(a0R) || a0.matches (getRegex(a))) Arr.add(a);
                if(args.length==1) {
                    if(Arr.isEmpty()) Arr.addAll(Arrays.asList(args0));
                }else if(args.length>1){
                    Arr = new ArrayList<>();
                    if(!a0.equals("give")) return Arr;
                    String a1 = args[1].toLowerCase();
                    if(args.length>2){
                        String a2 = args[2].toUpperCase();
                        if(args.length>3){
                            if(args.length>4){
                                if(args.length>5) return Arr;
                                if(a1.equals("machine") || a1.equals("hd") || a1.equals("filtro") || a1.equals("sucker")) {
                                    Arr.add("(quantidade)");
                                }
                                return Arr;
                            }
                            if(a1.equals("machine") || a1.equals("hd") || a1.equals("filtro") || a1.equals("sucker")) Arr = null;
                            return Arr;
                        }
                        String a2R = getRegex(a2);
                        String[] args2;
                        if(a1.equals("machine")){
                            args2 = new String[]{Items.CPU.toString()};
                        }else if(a1.equals("hd")){
                            args2 = new String[]{HD_Data.BASICO.toString(), HD_Data.MEDIO.toString(), HD_Data.AVANCADO.toString(), HD_Data.HEXTEC.toString()};
                        }else if(a1.equals("filtro")){
                            args2 = new String[]{Filter.Type.GENERIC.Type, Filter.Type.SPECIFIC.Type};
                        }else if(a1.equals("sucker")){
                            args2 = new String[]{Sucker.Type.X3.toString(), Sucker.Type.X5.toString(), Sucker.Type.X7.toString()};
                        }
                        else{
                            return null;
                        }
                        for(String a:args2) if(a2.matches(getRegex(a)) || a.matches(a2R)) Arr.add(a);
                        if(Arr.isEmpty()) Arr.addAll(Arrays.asList(args2));
                        return Arr;
                    }
                    String a1R = getRegex(a1);
                    String[] args1 = new String[]{"machine", "hd", "wrench", "filtro", "sucker"};
                    for(String a:args1) if(a.matches(a1R) || a1.matches(getRegex(a))) Arr.add(a);
                    if(Arr.isEmpty()) Arr.addAll(Arrays.asList(args1));
                    return Arr;
                }
                return Arr;
            }
        });
    }
    
    private String getRegex(String regex){
        StringBuilder sb = new StringBuilder("(?i:(.*?)");
        for(char c:regex.toCharArray()) sb.append(c).append("(.*?)");
        return sb.append(')').toString().trim();
    }

    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(cmd.equals("computercraft")){
            if(snd.hasPermission("ComputerCraft.adm")){
                if(args[0].equalsIgnoreCase("give")){
                    if(args.length>2){
                        String a1 = args[1].toLowerCase();
                        if(a1.equals("wrench")){
                            Player p1 = null;
                            try{
                                p1 = Bukkit.getPlayer(args[2]);
                                Listenner.darItem(p1, Items.WRENCH.ItemStack, true);
                            }catch(Exception e){
                                snd.sendMessage("§cNão consegui achar o player "+args[2]+"!");
                                return false;
                            }
                        }else{
                            if(args.length>4){
                                if(a1.equals("machine") || a1.equals("hd") || a1.equals("filtro") || a1.equals("sucker")){
                                    ItemStack is = null;
                                    if(a1.equals("machine")){
                                        try{
                                            is = Items.valueOf(args[2].toUpperCase()).ItemStack;
                                        }catch(Exception ex){
                                            snd.sendMessage("§cNão encontrei nenhuma maquina "+args[2].toUpperCase());
                                            return false;
                                        }
                                    }else if(a1.equals("hd")){
                                        try{
                                            is = new HD(HD_Data.valueOf(args[2].toUpperCase()), new ArrayList<>()).getItemStack();
                                        }catch(Exception ex){
                                            snd.sendMessage("§cNão encontrei nenhum HD "+args[2].toUpperCase());
                                            return false;
                                        }
                                    }else if(a1.equals("filtro")){
                                        try{
                                            is = Filter.Type.valueOf(args[2].toUpperCase()).Is;
                                        }catch(Exception ex){
                                            snd.sendMessage("§cNão encontrei nenhum Filtro "+args[2].toUpperCase());
                                            return false;
                                        }
                                    }else{
                                        try{
                                            is = Sucker.Type.valueOf(args[2].toUpperCase()).Is;
                                        }catch(Exception ex){
                                            snd.sendMessage("§cNão encontrei nenhuma Válvula de Vácuo "+args[2].toUpperCase());
                                            return false;
                                        }
                                    }
                                    Player p1 = null;
                                    try{
                                        p1 = Bukkit.getPlayer(args[3]);
                                    }catch(Exception e){
                                        snd.sendMessage("§cNão consegui achar o player "+args[3]+"!");
                                        return false;
                                    }
                                    int i = 0;
                                    try{
                                        i = Integer.parseInt(args[4]);
                                    }catch(Exception ex){
                                        snd.sendMessage("§cO valor "+args[4]+" não é um número inteiro!");
                                        return false;
                                    }
                                    Listenner.darItem(p1, new SuperStack(is, i), true);
                                }else{
                                    snd.sendMessage("§e/cpu give (machine/hd) (type) (player) (amount)");
                                }
                            }else{
                                snd.sendMessage("§e/cpu give (machine/hd) (type) (player) (amount)");
                            }
                        }
                    }else{
                        snd.sendMessage("§e/cpu give (machine/hd) (type) (player) (amount)");
                        snd.sendMessage("§e/cpu give wrench (player)");
                    }
                }else if(args[0].equalsIgnoreCase("reload")){

                }
            }
        }
        return true;
    }
    
}
