package maquinas3;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Machine {
    
    API api = Maquinas3.api;
    int task, maxDrops;
    
    private HashMap<Player, Boolean> player;
    private Location loc;
    private MachineType type;
    private String own;
    private int level, time, i, maxTime;
    private HD hd;
    private boolean hdEnable, chestEnable, enabled;
    private int drops;
    private double multiplier;

    public Machine(Location loc, MachineType type, String own, int level, int time, int maxTime, boolean hdEnable, boolean chestEnable, boolean enabled, int drops) {
        this.loc = loc;
        this.type = type;
        this.own = own;
        this.level = level;
        this.time = time;
        this.maxTime = maxTime;
        this.hdEnable = hdEnable;
        this.chestEnable = chestEnable;
        this.enabled = enabled;
        this.hd = new HD(loc.clone(), new ArrayList<>());
        this.drops = drops;
        this.maxDrops = (type.getDrop().getDrop().getMaxStackSize()*54);
        this.multiplier = type.getMultiplier();
        this.player = new HashMap<>();
        scheduler();
        if(hdEnable) enableHd();
    }
    
    
    public void stop(){
        api.bs.cancelTask(task);
        getHd().disable();
    }
    public void enableHd(){
        int l = getLevel();
        hd.disable();
        hd.addLine(getType().getDisplayName()+"§e(§2lvl"+(l<5 ? l : "§4.MAX")+"§e)");
        hd.addLine("§eDono: §4"+getOwn());
        hd.addLine("§eCombustivel: "+bar());
        hd.addLine("§eTempo restante: "+timer());
        hd.ajuste();
    }
    
    
    public void scheduler(){
        api.bs.scheduleSyncDelayedTask(API.main, new Runnable() {
            @Override
            public void run() {
                if(isHdEnable()) enableHd();
            }
        }, 20);
        task = api.bs.scheduleSyncRepeatingTask(API.main, new Runnable() {
            @Override
            public void run() {
                if(isEnabled()){
                    if(getTime()>0){
                        drop();
                        setTime(getTime()-1);
                        if(isHdEnable()) {
                            getHd().setLine(3, "§eCombustivel: "+bar());
                            getHd().setLine(4, "§eTempo restante: "+timer());
                        }
                        save();
                    }else{
                        setMultiplier(getType().getMultiplier());
                    }
                }
            }
        }, 0, 20);
    }
    
    public void save(){
        api.setMachine(this);
    }
    
    public void drop(){
        ItemStack item = getType().getDrop().getDrop();
        int l=getLevel(),
                drops = l==1 ? api.drops1 : l==2 ? api.drops2 : l==3 ? api.drops3 : l==4 ? api.drops4 : api.drops5;
        drops *= getMultiplier();
        if(isChestEnable()){
            int add = getDrops()+drops>this.maxDrops ? this.maxDrops-getDrops() : drops, resto = drops-add;
            addDrops(add);
            for(Player p:getPlayer().keySet()) if(api.pages.get(p)==1) api.openChest(p, this);
            if(resto<=0) return;
        }
        api.drop(drops, item, getLoc());
        save();
    }
    
    
    public Location getLoc() {
        return loc;
    }
    public MachineType getType() {
        return type;
    }
    public String getOwn() {
        return own;
    }
    public int getLevel() {
        return level;
    }
    public int getTime() {
        return time;
    }
    public int getMaxTime() {
        return maxTime;
    }
    public boolean isHdEnable() {
        return hdEnable;
    }
    public HD getHd() {
        return hd;
    }
    public boolean isChestEnable() {
        return chestEnable;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public int getDrops() {
        return drops;
    }
    public double getMultiplier() {
        return multiplier;
    }
    public HashMap<Player, Boolean> getPlayer(){
        return player;
    }
    public int getUpgrade(){
        return getLevel()==5 ? -1 : getType().getUpgrades()[getLevel()-1];
    }
    

    public void setLoc(Location loc) {
        this.loc = loc;
    }
    public void setType(MachineType type) {
        this.type = type;
    }
    public void setOwn(String own) {
        this.own = own;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }
    public void setHdEnable(boolean hdEnable) {
        this.hdEnable = hdEnable;
    }
    public void setChestEnable(boolean chestEnable) {
        this.chestEnable = chestEnable;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setDrops(int drops) {
        this.drops = drops;
    }
    public void addDrops(int drops){
        setDrops(getDrops()+drops);
    }
    public void remDrops(int drops){
        addDrops(-drops);
    }
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
    public void setPlayer(Player player, boolean ok) {
        HashMap<Player, Boolean> Player = new HashMap<>();
        if(ok) Player.put(player, ok);
        this.player = Player;
    }
    
    
    public String timer(){
        int time = getTime();
        if(time>=3600){
            return "§f"+time/3600+"§7h §f"+((time%3600)/60)+"§7min";
        }else if(time>=60){
            return "§f"+time/60+"§7min §f"+(time%60>9 ? time%60 : "0"+time%60)+"§7s";
        }else{
            return "§f00§7min §f"+time+"§7s";
        }
    }
    
    public String bar(){
        int amount = 10; 
        double lifePerChar = getMaxTime()/amount;
        int lifeChars = (int)Math.ceil(getTime()/lifePerChar);
        String bar = "";
        for (int i=0;i<lifeChars;i++) bar += "§a§l|";
        if (lifeChars<amount) for (int i=lifeChars;i<amount;i++) bar += "§4§l|";
        double percent = (double)((double)(getTime()*100)/(double)getMaxTime());
        NumberFormat nf = new DecimalFormat("##.#");
        String Percent = nf.format(percent);
        int l = Percent.length();
        Percent += l==1 ? ",0" : ""; 
        return bar+"§2("+Percent+"%)";
    }
    
    
}
