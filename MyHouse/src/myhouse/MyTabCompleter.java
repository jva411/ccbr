package myhouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import myhouse.com.flags.Flag;
import myhouse.com.region.Region;
import myhouse.com.region.RegionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MyTabCompleter implements TabCompleter{

    private RegionManager rm;

    public MyTabCompleter(RegionManager rm) {
        this.rm = rm;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmd, String string, String[] args) {
        if(cmd.getName().equalsIgnoreCase("myhouse")){
            ArrayList<String> Arr;
            String a0 = args[0].toLowerCase();
            if(args.length>1){
                String a1 = args[1].toLowerCase();
                if(a0.equals("help")) {
                    if(args.length>2) return new ArrayList<>();
                    Arr = new ArrayList<>(Arrays.asList(new String[]{"claim", "darDono", "delwarp", "expand", "flag", "renomear", "setwarp", "trust", "unclaim", "untrust", "vender", "warp"}));
                    if(cs.hasPermission("MyHouse.adm")) Arr.add("reload");
                    ArrayList<String> ret = new ArrayList<>();
                    for(String a:Arr) if(a.matches(getRegex(a1)) || a1.matches(getRegex(a))) ret.add(a);
                    if(ret.size()==0) return Arr;
                    else return ret;
                }else if(a0.equals("trust") || a0.equals("untrust") || a0.equals("dardono")){
                    if(args.length>2) return new ArrayList<>();
                    return null;
                }else if(a0.equals("claim")){
                    if(args.length>2) return new ArrayList<>();
                    return new ArrayList<>(Arrays.asList(new String[]{"<nome>"}));
                }else if(a0.equals("renomear")){
                    return new ArrayList<>(Arrays.asList(new String[]{"<novo nome para o terreno>"}));
                }
                else if(a0.equals("unclaim") || a0.equals("info") || a0.equals("expand") || a0.equals("reload")){
                    return new ArrayList<>();
                }else if(a0.equals("vender")){
                    if(args.length>2) return new ArrayList<>();
                    return new ArrayList<>(Arrays.asList(new String[]{"<valor>"}));
                }else if(a0.equals("flag")){
                    if(args.length>2){
                        if(args.length>3) return new ArrayList<>();
                        String a2 = args[2].toLowerCase();
                        Arr = new ArrayList<>(Arrays.asList(new String[]{"enable", "disable"}));
                        ArrayList<String> ret = new ArrayList<>();
                        for(String a:Arr) if(a.matches(getRegex(a2)) || a2.matches(getRegex(a))) ret.add(a);
                        if(ret.size()==0) return Arr;
                        else return ret;
                    }
                    ArrayList<String> ret = new ArrayList<>();
                    Arr = new ArrayList<>();
                    for(Flag flag:Flag.values()) if(cs.hasPermission("MyHouse.flag."+flag.toString()) || cs.hasPermission("MyHouse.flag.*")) Arr.add(flag.toString());
                    a1 = a1.toUpperCase();
                    for(String a:Arr) if(a.matches(getRegex(a1)) || a1.matches(getRegex(a))) ret.add(a);
                    if(ret.size()==0) return Arr;
                    else return ret;
                }else if(a0.equals("setwarp") || a0.equals("delwarp")) return new ArrayList<>();
                else if(a0.equals("warp")){
                    if(args.length>2) return new ArrayList<>();
                    Arr = new ArrayList<>();
                    ArrayList<String> ret = new ArrayList<>();
                    for(Region region:rm.allRegions) if(region.getWarp()!=null) Arr.add(region.getName().toLowerCase());
                    for(String a:Arr) if(a.matches(getRegex(a1)) || a1.matches(getRegex((a)))) ret.add(a);
                    if(ret.size()==0) return Arr;
                    else return ret;
                }else return new ArrayList<>();
            }
            Arr = new ArrayList<>(Arrays.asList(new String[]{"claim", "darDono", "delwarp", "expand", "flag", "help", "renomear", "setwarp", "trust", "unclaim", "untrust", "vender", "warp", "reload", "setMensagemDeEntrada", "setMensagemDeSaida", "removerMensagemDeSaida", "removerMensagemDeEntrada"}));
            ArrayList<String> ret = new ArrayList<>();
            if(cs.hasPermission("MyHouse.adm")) Arr.add("reload");
            for(String a:Arr) if(a.matches(getRegex(a0)) || a0.matches(getRegex(a))) ret.add(a);
            if(ret.size()==0) return Arr;
            else return ret;
        }
        return null;
    }
    
    public static String getRegex(String str){
        if(str==null || str.length()==0) return "";
        StringBuilder sb = new StringBuilder(".*").append(str.charAt(0));
        for(int i=1;i<str.length();i++) sb.append(".*").append(str.charAt(i));
        return sb.append(".*").toString();
    }
   
}