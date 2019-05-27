package ccbr_cash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MyTabCompleter implements TabCompleter{

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] args) {
        if(cmnd.getName().equalsIgnoreCase("cash")){
            if(cs.hasPermission("CCBR_Cash.cash.adm")){
                String a0 = args[0].toLowerCase();
                if(args.length==1){
                    ArrayList<String> Arr = new ArrayList<>();
                    if(a0.equalsIgnoreCase("cash")) return Arr;
                    for(Player p:Bukkit.getOnlinePlayers()) if(p.getName().toLowerCase().startsWith(a0)) Arr.add(p.getName());
                    if(a0.length()==0){
                        Arr.add("add");
                        Arr.add("rem");
                        Arr.add("set");
                        Arr.add("remove");
                        Arr.add("reload");
                        Arr.add("help");
                    }else if("add".startsWith(a0)) {
                        Arr.add("add");
                    }else if("set".startsWith(a0)){
                        Arr.add("set");
                    }
                    else if("re".startsWith(a0)){
                        Arr.add("rem");
                        Arr.add("remove");
                        Arr.add("remove");
                    }else if("remove".startsWith(a0)) {
                        if(a0.length()<4) Arr.add("rem");
                        Arr.add("remove");
                    }else if("help".startsWith(a0)) Arr.add("help");
                    else if("reload".startsWith(a0)) Arr.add("reload");
                    else{
                        Arr.add("add");
                        Arr.add("rem");
                        Arr.add("set");
                        Arr.add("remove");
                        Arr.add("reload");
                        Arr.add("help");
                    }
                    return Arr;
                }else if(args.length==2){
                    if("add".startsWith(a0) || "remove".startsWith(a0) || "set".startsWith(a0)) return null;
                    else return new ArrayList<>();
                }else{
                    if("add".equalsIgnoreCase(a0) || "remove".equalsIgnoreCase(a0) || "set".equalsIgnoreCase(a0)) return new ArrayList<>(Arrays.asList(new String[]{"(valor)"}));
                }
            }else{
                return new ArrayList<>();
            }
        }else if(cmnd.getName().equalsIgnoreCase("shop")){
            if(cs.hasPermission("CCBR_Cash.shop.adm")){
                String a0 = args[0].toLowerCase();
                ArrayList<String> Arr = new ArrayList<>();
                if(args.length==1){
                    if(a0.equalsIgnoreCase("shop")) return Arr;
                    if(a0.length()==0){
                        Arr.add("add");
                        Arr.add("set");
                    }else if(a0.startsWith("add")){
                        Arr.add("add");
                    }else if(a0.startsWith("set")){
                        Arr.add("set");
                    }else{
                        Arr.add("add");
                        Arr.add("set");
                    }
                }else if(args.length==2){
                    if(a0.equalsIgnoreCase("add") || a0.equalsIgnoreCase("set")) Arr.add("(caminho) - Ex:PvP/PvP.yml"); 
                }else if(args.length==3){
                    if(a0.equalsIgnoreCase("add") || a0.equalsIgnoreCase("set")) Arr.add("(Linha) - [i] pega o código do item na sua mão"); 
                }
                return Arr;
            }
            return new ArrayList<>();
        }
        return null;
    }
    
}
