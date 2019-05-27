package me.jva.ccbr_events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class MyTabCompleter implements TabCompleter{

    CCBR_Events main;

    public MyTabCompleter(CCBR_Events main) {
        this.main = main;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender snd, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("event")){
            ArrayList<String> a0s = new ArrayList<>(Arrays.asList(new String[]{"join", "leave", "kick", "cancel", "start", "list", "define", "proximo", "new", "reload"}));
            ArrayList<String> arr = new ArrayList<>();
            if(args.length==1){
                int max = 1;
                if(snd.hasPermission("CCBR_Events.adm")) max = a0s.size();
                for(int i=0;i<max;i++) if(a0s.get(i).matches(main.getRegex(args[0])) || args[0].matches(main.getRegex(a0s.get(i)))) arr.add(a0s.get(i));
                if(arr.size()==0) for(int i=0;i<max;i++) arr.add(a0s.get(i));
            }else if(args.length==2){
                String a0 = args[0].toLowerCase();
                for(int i=0;i<a0s.size();i++) if(a0s.get(i).matches(main.getRegex(args[0])) || args[0].matches(main.getRegex(a0s.get(i)))) arr.add(a0s.get(i));
                if(arr.size()==1){
                    a0 = arr.get(0);
                    if(a0.equals("kick")){
                        return null;
                    }else if(a0.equals(("new"))){
                        arr.add("<name>");
                    }else if(a0.equals("start")){
                        for(Event event:main.events) if(event.getName().matches(main.getRegex(args[1])) || args[1].matches(main.getRegex(event.getName()))) arr.add(event.getName());
                        if(arr.size()==0) for(Event event:main.events) arr.add(event.getName());
                    }
                }
            }
            return arr;
        }
        return null;
    }
    
}
