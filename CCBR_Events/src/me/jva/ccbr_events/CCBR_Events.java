package me.jva.ccbr_events;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CCBR_Events extends JavaPlugin{

    public ArrayList<Event> events, eventsWaiting;
    public Event eventON;
    public Config config, lang;
    public HashMap<Player, EventBuilder> makingEvent;
    public EventListener el = new EventListener(this);
    
    private int Task;
    
    @Override
    public void onEnable() {
        events = new ArrayList<>();
        eventsWaiting = new ArrayList<>();
        makingEvent = new HashMap<>();
        config = new Config(this, "config.yml");
        config.saveDefaultConfig();
        lang = new Config(this, config.getString("lang")+".yml");
        lang.saveDefaultConfig();
        el = new EventListener(this);
        Bukkit.getPluginManager().registerEvents(el, this);
        loadEvents();
        Task = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, EventsController, 0, 1200);
        Bukkit.getPluginCommand("event").setTabCompleter(new MyTabCompleter(this));
    }
    
    public void reload(){
        for(Event event:events){
            event.cancel();
            event.cancelTask();
        }
        eventON = null;
        Bukkit.getScheduler().cancelTask(Task);
        this.onEnable();
    }
    
    public Eventer getEventer(Player p){
        for (Event event: events){
            Eventer evr = event.getEventer(p);
            if(evr != null) return evr;
        }
        return null;
    }
    
    public void saveEvent(Event e){
        config.set("Events."+e.getName()+".spawnIn", getLocation2(e.getEventIn()));
        config.set("Events."+e.getName()+".spawnOut", getLocation2(e.getEventOut()));
        config.set("Events."+e.getName()+".spawnStart", getLocation2(e.getEventStart()));
        ArrayList<String> lossePoints = new ArrayList<>();
        for (LosseCube lc : e.getLosseCubes()){
            StringBuilder sb = new StringBuilder();
            lossePoints.add(sb.append(lc.Start.getBlockX())
                    .append(' ').append(lc.Start.getBlockY())
                    .append(' ').append(lc.Start.getBlockZ())
                    .append(' ').append(lc.End.getBlockX())
                    .append(' ').append(lc.End.getBlockY())
                    .append(' ').append(lc.End.getBlockZ())
                    .toString());
        }
        config.set("Events."+e.getName()+".lossePoints", lossePoints);
        config.set("Events."+e.getName()+".kickOnLoss", e.isKickOnLoss());
        config.set("Events."+e.getName()+".kickOnDeath", e.isKickOnDeath());
        config.set("Events."+e.getName()+".lastPlayerWin", e.isLastPlayerWin());
        config.set("Events."+e.getName()+".timeToStart", e.getTimeToStart());
        config.set("Events."+e.getName()+".timeToEnd", e.getTimeToEnd());
        config.set("Events."+e.getName()+".minPlayers", e.getMinPlayers());
        if(!config.contains("Events."+e.getName()+".Cmds")) config.set("Events."+e.getName()+".Cmds", new ArrayList<>());
        config.saveConfig();
    }
    
    public Event loadEvent(String name){
        try{
            Location spawnIn = getLocation2(config.getString("Events."+name+".spawnIn"));
            ArrayList<Location> lossePoints = new ArrayList<>();
            List<String> list = config.getConfig().getStringList("Events."+name+".lossePoints");
            for(String a:list) {
                String[] as = a.split(" ");
                lossePoints.add(new Location(spawnIn.getWorld(), Integer.parseInt(as[0]), Integer.parseInt(as[1]), Integer.parseInt(as[2])));
                lossePoints.add(new Location(spawnIn.getWorld(), Integer.parseInt(as[3]), Integer.parseInt(as[4]), Integer.parseInt(as[5])));
            }  
            return new Event(
                this,
                name,
                spawnIn,
                getLocation2(config.getString("Events."+name+".spawnOut")),
                new ArrayList<>(lossePoints),
                config.getBoolean("Events."+name+".kickOnLoss"),
                config.getBoolean("Events."+name+".kickOnDeath"),
                config.getInt("Events."+name+".timeToStart"),
                config.getInt("Events."+name+".timeToEnd"),
                config.getBoolean("Events."+name+".lastPlayerWin"),
                getLocation2(config.getString("Events."+name+".spawnStart")),
                config.getInt("Events."+name+".minPlayers")
            );
        }catch(Exception ex){
            System.out.println("§e§l[EventsGame]: §4§lERROR: §f§lNão foi possível carregar o evento: §e§l"+name);
            return null;
        }
    }
    
    public void loadEvents(){
        if(config.contains("Events")){
            for(String name:config.getConfig().getConfigurationSection("Events").getKeys(false)){
                events.add(loadEvent(name));
            }
        }
    }
    
    public String getLocation(Location loc){
        String format = "%.2f";
        StringBuilder sb = new StringBuilder(loc.getWorld().getName()).
                append(' ').append(String.format(format, loc.getX())).
                append(' ').append(String.format(format, loc.getY()));
        sb.append(' ').append(String.format(format, loc.getZ()));
        return sb.toString().replace(',', '.');
    }
    
    public Location getLocation(String loc){
        String[] locS = loc.split(" ");
        return new Location(
                Bukkit.getWorld(locS[0]),
                Double.parseDouble(locS[1]),
                Double.parseDouble(locS[2]),
                Double.parseDouble(locS[3])
        );
    }
    
    public String getLocation2(Location loc){
        String format = "%.2f";
        StringBuilder sb = new StringBuilder(loc.getWorld().getName()).
                append(' ').append(String.format(format, loc.getX())).
                append(' ').append(String.format(format, loc.getY())).
                append(' ').append(String.format(format, loc.getZ())).
                append(' ').append(String.format(format, loc.getYaw())).
                append(' ').append(String.format(format, loc.getPitch()));
        return sb.toString().replace(',', '.');
    }
    
    public Location getLocation2(String loc){
        String[] locS = loc.split(" ");
        return new Location(
                Bukkit.getWorld(locS[0]),
                Double.parseDouble(locS[1]),
                Double.parseDouble(locS[2]),
                Double.parseDouble(locS[3]),
                Float.parseFloat(locS[4]),
                Float.parseFloat(locS[5])
        );
    }
    
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(!(snd instanceof Player)) return true;
        if(cmd.equals("event")){
            if(args.length==0){
                for(String help:lang.getConfig().getStringList("Help")) snd.sendMessage(help.replace('&', '§'));
                if(snd.hasPermission("CCBR_Events.help")) for(String help:lang.getConfig().getStringList("HelpADM")) snd.sendMessage(help.replace('&', '§'));
            }else{
                String a0 = args[0].toLowerCase();
                ArrayList<String> Args = new ArrayList<>(Arrays.asList(new String[]{"join", "leave", "kick", "cancel", "reload", "start", "list", "define", "proximo", "new"}));
                if(snd.hasPermission("CCBR_Events.adm")){
                    ArrayList<String> possibles = new ArrayList<>();
                    for(String Arg:Args) if(Arg.matches(getRegex(a0)) || a0.matches(getRegex(Arg))) possibles.add(Arg);
                    if(possibles.size() != 1) {
                        for(String help:lang.getConfig().getStringList("Help")) snd.sendMessage(help.replace('&', '§'));
                        for(String help:lang.getConfig().getStringList("HelpADM")) snd.sendMessage(help.replace('&', '§'));
                    }
                    else{
                        a0 = possibles.get(0);
                        if(a0.equals("new")){
                            if(args.length<2){
                                snd.sendMessage("/event new <nome do evento>");
                                return true;
                            }else{
                                String a1 = args[1].toLowerCase();
                                if(makingEvent.containsKey(snd)) snd.sendMessage(lang.getString("CriandoNovoEvento0").replace('&', '§'));
                                for(Event event:events){
                                    if(event.getName().equals(a1)){
                                        snd.sendMessage(lang.getString("NomeJaExiste").replace('&', '§'));
                                        break;
                                    }
                                }
                                makingEvent.put((Player)snd, new EventBuilder((Player)snd, a1, this));
                                for(String str:lang.getConfig().getStringList("CriandoNovoEvento.1")){
                                    snd.sendMessage(str.replace('&', '§').replace("%event%", a1));
                                }
                                return true;
                            }
                        }else if(a0.equals("proximo")){
                            if(makingEvent.containsKey(snd)){
                                EventBuilder eb = makingEvent.get(snd);
                                if(eb.getStage()==0){
                                    eb.setEventIn(((Player)snd).getLocation());
                                    eb.setStage(eb.getStage()+1);
                                }else if(eb.getStage()==1){
                                    eb.setEventStart(((Player)snd).getLocation());
                                    eb.setStage(eb.getStage()+1);
                                }else if(eb.getStage()==2){
                                    eb.setEventOut(((Player)snd).getLocation());
                                    eb.setStage(eb.getStage()+1);
                                }else if(eb.getStage()==3){
                                    if(eb.canFinnalize()){
                                        snd.sendMessage(lang.getString("CriandoNovoEvento.EventoCriado").replace('&', '§'));
                                        Event event = eb.make();
                                        events.add(event);
                                        saveEvent(event);
                                        eb = null;
                                        makingEvent.remove(snd);
                                    }else{
                                        snd.sendMessage(lang.getString("CriandoNovoEvento.PeloMenosUma").replace('&', '§'));
                                    }
                                }
                                for(String str:lang.getConfig().getStringList("CriandoNovoEvento."+(1+eb.getStage()))){
                                    snd.sendMessage(str.replace('&', '§').replace("%event%", eb.getName()));
                                }
                            }else{
                                snd.sendMessage(lang.getString("CriandoNovoEvento.Nao").replace('&', '§'));
                            }
                            return true;
                        }else if(a0.equals("define")){
                            if(makingEvent.containsKey(snd)){
                                EventBuilder eb = makingEvent.get(snd);
                                if(eb.defRegion()){
                                    snd.sendMessage(lang.getString("CriandoNovoEvento.RegiaoDePerdaDefinida").replace('&', '§'));
                                }else{
                                    snd.sendMessage(lang.getString("CriandoNovoEvento.RegiaoDePerdaNaoDefinida").replace('&', '§'));
                                }
                            }else{
                                snd.sendMessage(lang.getString("CriandoNovoEvento.Nao").replace('&', '§'));
                            }
                            return true;
                        }else if(a0.equals("kick")){
                            if(args.length>2){
                                String a1 = args[1].toLowerCase();
                                for(Event event:events){
                                    if(event.getName().equals(a1)){
                                        if(!event.remEventer((Player)snd)) snd.sendMessage(lang.getString("JogadorNaoEstaNoEvento0").replace('&', '§').replace("%player%", args[2].toLowerCase()));
                                        return true;
                                    }
                                }
                                snd.sendMessage(lang.getString("NomeNaoEncontrado").replace('&', '§').replace("%event%", a1));
                            }else{
                                snd.sendMessage("/event kick <evento> <player>");
                            }
                        }else if(a0.equals("start")){
                            if(args.length>1){
                                String a1 = args[1].toLowerCase();
                                ArrayList<Event> possibles1 = new ArrayList<>();
                                for(Event event:events) if(event.getName().matches(getRegex(a1)) || a1.matches(getRegex(event.getName()))) possibles1.add(event);
                                if(possibles.size() != 1){
                                    snd.sendMessage(lang.getString("NomeNaoEncontrado").replace('&', '§').replace("%event%", a1));
                                    return false;
                                }
                                Event event = possibles1.get(0);
                                if(event.call()) snd.sendMessage(lang.getString("EventoIniciado0").replace('&', '§').replace("%event%", event.getName()));
                                else snd.sendMessage(lang.getString("EventoEmEspera").replace('&', '§').replace("%event%", event.getName()));
                                return true;
                            }else{
                                snd.sendMessage("/event start <evento>");
                            }
                            return true;
                        }else if(a0.equals("cancel")){
                            if(eventON==null) snd.sendMessage(lang.getString("NenhumEventoOcorrendo").replace('&', '§'));
                            else {
                                snd.sendMessage(lang.getString("EventoCancelado0").replace('&', '§').replace("%event%", eventON.getName()));
                                eventON.cancelTask();
                                eventON.cancel();
                            }
                            return true;
                        }else if(a0.equals("list")){
                            snd.sendMessage(("---------------------------------------------"));
                            for(Event event:events) snd.sendMessage("§f§levent - "+event.getName());
                            snd.sendMessage(("---------------------------------------------"));
                            return true;
                        }else if(a0.equals("reload")){
                            reload();
                            snd.sendMessage("§e§l[CCBR_Events]: §bTodas as configurações foram recarregadas com sucesso!");
                            return true;
                        }
                    }
                }
                ArrayList<String> possibles = new ArrayList<>();
                for(int i=0;i<2;i++) if(Args.get(i).matches(getRegex(a0)) || a0.matches(getRegex(Args.get(i)))) possibles.add(Args.get(i));
                if(possibles.size() != 1) for(String help:lang.getConfig().getStringList("Help")) snd.sendMessage(help.replace('&', '§'));
                if(a0.equals("leave")){
                    if(eventON==null){
                        snd.sendMessage(lang.getString("NenhumEventoOcorrendo").replace('&', '§'));
                    }else{
                        if(!eventON.remEventer((Player)snd)) snd.sendMessage(lang.getString("JogadorNaoEstaNoEvento1").replace('&', '§'));
                    }
                    return true;
                }else if(a0.equals("join")){
                    if(eventON==null){
                        snd.sendMessage(lang.getString("NenhumEventoOcorrendo").replace('&', '§'));
                    }else{
                        if(!eventON.addEventer((Player)snd)) snd.sendMessage(lang.getString("EventoAcontecendo").replace('&', '§').replace("%event%", eventON.getName()));
                    }
                    return true;
                }else{
                    for(String help:lang.getConfig().getStringList("Help")) snd.sendMessage(help.replace('&', '§'));
                    if(snd.hasPermission("CCBR_Events.help")) for(String help:lang.getConfig().getStringList("HelpADM")) snd.sendMessage(help.replace('&', '§'));
                }
            }
        }
        return true;
    }
    
    public String getRegex(String strg){
       if(strg !=null && strg.length()>0){
           StringBuilder sb = new StringBuilder("(?i).*").append(strg.charAt(0));
           for(int i=1;i<strg.length();i++) sb.append(".*").append(strg.charAt(i));
           return sb.append(".*").toString();
       }
       return "";
   }
    
    public Runnable EventsController = new Runnable(){
        
        @Override
        public void run(){
            LocalDateTime now = LocalDateTime.now();
            String dow = now.getDayOfWeek().toString();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
            String hor = dtf.format(now);
            for(Event event:events){
                if(config.contains("Events."+event.getName()+".Start."+dow)){
                    if(config.getString("Events."+event.getName()+".Start."+dow).contains(hor)){
                        event.call();
                    }
                }
            }
            if(eventsWaiting.size()>0){
                if(eventsWaiting.get(0).start()){
                    eventON = eventsWaiting.get(0);
                    eventsWaiting.remove(0);
                }
            }
        }
        
    }; 
    
}
