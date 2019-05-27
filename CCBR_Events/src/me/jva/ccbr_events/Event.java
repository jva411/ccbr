package me.jva.ccbr_events;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Event{
    
    private ArrayList<Eventer> eventers;
    private String name;
    private Location eventIn, eventStart, eventOut;
    private ArrayList<LosseCube> losseCubes;
    private boolean kickOnLoss, kickOnDeath, lastPlayerWin;
    private STATE state;
    private int timeToStart, timeToEnd, minPlayers;  // AS SECONDS
    private Eventer winner;
    
    private int Task;
    private CCBR_Events main;
    
    public enum STATE{
        DISABLED, ENABLED, WAITING, STARTING, PLAYING;
    }

    public Event(CCBR_Events main, String name, Location eventIn, Location eventOut, ArrayList<Location> lossePoints, boolean kickOnLoss, boolean kickOnDeath, int timeToStart, int timeToEnd, boolean lastPlayerWin, Location eventStart, int minPlayers) {
        this.main = main;
        this.eventers = new ArrayList<>();
        this.name = name.toLowerCase();
        this.eventIn = eventIn;
        this.eventOut = eventOut;
        this.losseCubes = new ArrayList<>();
        for (int i=0; i < lossePoints.size(); i+=2) this.losseCubes.add(new LosseCube(lossePoints.get(i), lossePoints.get(i+1)));
        this.kickOnLoss = kickOnLoss;
        this.kickOnDeath = kickOnDeath;
        this.timeToStart = timeToStart;
        this.timeToEnd = timeToEnd;
        this.lastPlayerWin = lastPlayerWin;
        this.eventStart = eventStart;
        this.minPlayers = minPlayers;
        this.state = STATE.ENABLED;
    }

    public ArrayList<Eventer> getEventers() {
        return eventers;
    }

    public String getName() {
        return name;
    }

    public Location getEventIn() {
        return eventIn;
    }

    public Location getEventOut() {
        return eventOut;
    }

    public ArrayList<LosseCube> getLosseCubes() {
        return losseCubes;
    }

    public boolean isKickOnLoss() {
        return kickOnLoss;
    }

    public boolean isKickOnDeath() {
        return kickOnDeath;
    }
    
    public STATE getState() {
        return state;
    }

    public int getTimeToEnd() {
        return timeToEnd;
    }

    public int getTimeToStart() {
        return timeToStart;
    }

    public boolean isLastPlayerWin() {
        return lastPlayerWin;
    }

    public Location getEventStart() {
        return eventStart;
    }

    public Eventer getWinner() {
        return winner;
    }

    public int getMinPlayers() {
        return minPlayers;
    }
    
    public void setState(STATE state) {
        this.state = state;
    }

    public void setWinner(Eventer winner) {
        this.winner = winner;
    }
    
    public Eventer getEventer(Player player){
        if(state==STATE.PLAYING || state==STATE.STARTING) for(Eventer e:eventers) if(e.getPlayer().equals(player)) return e;
        return null;
    }
    
    public boolean addEventer(Player p){
        if(state==STATE.STARTING){
            eventers.add(new Eventer(p, this));
            p.teleportAsync(eventIn);
            return true;
        }
        return false;
    }
    
    public boolean remEventer(Player p){
        if(state==STATE.PLAYING){
            Eventer evr = getEventer(p);
            if(evr!=null){
                p.teleportAsync(eventOut);
                eventers.remove(evr);
                if(eventers.size()==1){
                    if(lastPlayerWin){
                        winner = evr;
                        finallize();
                    }
                }
                if(eventers.size()==0){
                    for(Player po:Bukkit.getOnlinePlayers()){
                        po.sendMessage(main.lang.getString("EventoEncerrado0").replace('&', '§').replace("%event%", getName()));
                    }
                    cancelTask();
                    cancel();
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean call(){
        if(state==STATE.ENABLED){
            state = STATE.WAITING;
            main.eventsWaiting.add(this);
            return true;
        }
        return false;
    }
    
    public boolean start(){
        if(state == STATE.WAITING){
            if(main.eventON==null){
                int mins = timeToStart/60, seconds = timeToStart%60;
                String msg = main.lang.getString("EventoIniciado1").replace('&', '§').replace("%event%", name);
                if(mins==0){
                    msg = msg.replace("%time%", seconds+"s");
                }else if(seconds==0){
                    msg = msg.replace("%time%", mins+"m");
                }else{
                    msg = msg.replace("%time%", mins+"m"+seconds+"s");
                }
                for(Player po:main.getServer().getOnlinePlayers()) po.sendMessage(msg);
                main.eventON = this;
                this.state = STATE.STARTING;
                Task = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable(){
                    
                    int time = mins*60+seconds;
                    
                    @Override
                    public void run(){
                        time--;
                        if(time==0){
                            if(eventers.size() < minPlayers){
                                cancel();
                                for(Player po:Bukkit.getOnlinePlayers()){
                                    po.sendMessage(main.lang.getString("EventoCancelado2").replace('&', '§').replace("%min%", minPlayers+""));
                                }
                            }else{
                                for(Eventer eventer:eventers) {
                                    eventer.getPlayer().teleportAsync(eventStart);
                                    eventer.setSpawnPoint(eventStart);
                                }
                                for(Player po:Bukkit.getOnlinePlayers()) po.sendMessage(main.lang.getString("EventoIniciado2").replace('&', '§'));
                                state = STATE.PLAYING;
                                int oldTask = Task;
                                Task = Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable(){
                                    @Override
                                    public void run(){
                                        if(cancel()){
                                            for(Player po:Bukkit.getOnlinePlayers()) po.sendMessage(main.lang.getString("EventoEncerrado0").replace('&', '§'));
                                            for(Eventer eventer:eventers) eventer.getPlayer().teleportAsync(eventOut);
                                            main.eventON = null;
                                        }
                                        Bukkit.getScheduler().cancelTask(Task);
                                    }
                                }, timeToEnd*20);
                                Bukkit.getScheduler().cancelTask(oldTask);
                            }
                        }else{
                            if(time<60){
                                if(time==30){
                                    for(Player po:Bukkit.getOnlinePlayers()){
                                        po.sendMessage(main.lang.getString("FaltamParaComecar").replace('&', '§').replace("%time%", time+"s"));
                                    }
                                }else if (time<11){
                                    for(Player po:Bukkit.getOnlinePlayers()){
                                        po.sendMessage(main.lang.getString("FaltamParaComecar").replace('&', '§').replace("%time%", time+"s"));
                                    }
                                }
                            }else if(time%60==0){
                                for(Player po:Bukkit.getOnlinePlayers()){
                                    po.sendMessage(main.lang.getString("FaltamParaComecar").replace('&', '§').replace("%time%", (time/60)+"m"));
                                }
                            }
                        }
                    }
                }, 0, 20);
                return true;
            }
        }
        return false;
    }
    
    public boolean cancel(){
        if(state == STATE.PLAYING || state == STATE.STARTING){
            for(Eventer eventer:eventers) eventer.getPlayer().teleportAsync(eventOut);
            eventers = new ArrayList<>();
            state = STATE.ENABLED;
            main.eventON = null;
            main.eventsWaiting.remove(this);
            return true;
        }
        return false;
    }
    
    public void cancelTask(){
        if(Task!=-1){
            Bukkit.getScheduler().cancelTask(Task);
            Task = -1;
        }
    }
    
    public boolean finallize(){
        if(state == STATE.PLAYING){
            String Name = winner.getPlayer().getCustomName()==null ? winner.getPlayer().getName() : winner.getPlayer().getCustomName();
            for(Player po:main.getServer().getOnlinePlayers()) po.sendMessage(main.lang.getString("EventoEncerrado1").replace('&', '§').replace("%event%", name).replace("%player%", Name));
            for(String str:main.config.getConfig().getStringList("Events."+name+".Cmds")) {
                main.getServer().dispatchCommand(main.getServer().getConsoleSender(), str.replace("%p%", winner.getPlayer().getName()));
            }
            for(Eventer eventer:eventers) eventer.getPlayer().teleportAsync(eventOut);
            cancelTask();
            main.eventON = null;
            state = STATE.ENABLED;
            return true;
        }
        return false;
    }
    
}
