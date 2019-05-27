package myhouse.com.region;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import myhouse.BlockChanger;
import myhouse.com.flags.Flag;
import myhouse.com.flags.FlagManager;
import myhouse.com.listeners.ListenerUtils;
import myhouse.io.Config;
import myhouse.io.ConfigUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionManager {
    
    public ArrayList<Region> allRegions;
    public HashMap<Player, Region> playerRemove = new HashMap<>();
    public HashMap<Player, Integer> playerVender = new HashMap<>();
    public HashMap<Player, BlockChanger> blocks = new HashMap<>();
    public HashMap<Player, Boolean> setandoMensagens = new HashMap<>();
    public HashMap<Player, Region> setandoMensagens2 = new HashMap<>();
    public ConfigUtils ConfigU;
    public Economy Economy;
    public JavaPlugin Plugin;
    public ListenerUtils lu;

    public void setLu(ListenerUtils lu) {
        this.lu = lu;
    }
    
    public void reload(){
        allRegions = new ArrayList<>();
        for(Region region:allRegions) allRegions.remove(region);
    }
    
    public RegionManager(JavaPlugin plugin, Economy eco){
        allRegions = new ArrayList<>();
        ConfigU = new ConfigUtils(plugin, this);
        Economy = eco;
        Plugin = plugin;
    }
   
    public boolean existsRegion(String name){
        for(Region region:allRegions){
            if(region.getRealName().equals(name)) return true;
        }
        return false;
    }
    
    public boolean isOp(Player p){
        return (p.hasPermission("MyHouse.adm") || p.isOp());
    }
    
    public boolean isMember(String name, Location loc, boolean subRegion){
        for(String a:getRegion(loc, subRegion).getMembers()) if(a.equalsIgnoreCase(name)) return true;
        return false;
    }
    public boolean isMember(String name, Region region){
        for(String a:region.getMembers()) if(a.equalsIgnoreCase(name)) return true;
        return false;
    }
    
    public boolean isOwner(String name, Location loc, boolean subRegion){
        return isOwner(name, getRegion(loc, subRegion));
    }
    public boolean isOwner(String name, Region region){
        return region.getOwner().equals(name.toLowerCase());
    }
    
    public Region getRegion(String name){
        for(Region region:allRegions){
            if(region.getName().equalsIgnoreCase(name)){
                return region;
            }
        }
        return null;
    }
    
    public Region getRegion(Player p, String name){
        for(Region region:getAllRegionsOfPlayer(p, false)){
            if(region.getName().replaceFirst(p.getName()+'_', "").equals(name)){
                return region;
            }else{
                for(Region Region:region.getSubRegions()){
                    if(Region.getName().replaceFirst(p.getName()+'_', "").equals(name)){
                        return Region;
                    }
                }
            }
        }
        return null;
    }
    
    public Region getRegion(Location loc, boolean subRegion){
        for(Region region:allRegions){
            if(region.getWorld().equals(loc.getWorld())){
                if(regionContainsLocation(loc, region)){
                    if(subRegion){
                        for(Region Region:region.getSubRegions()){
                            if(regionContainsLocation(loc, Region)){
                                return Region;
                            }
                        }
                    }
                    if(region.getSuperRegion()==null) return region;
                }
            }
        }
        return null;
    }
    
    public Region getRegion(Location Loc1, Location Loc2, boolean subRegion){
        Region region1 = getRegion(Loc1, subRegion);
        Region region2 = getRegion(Loc2, subRegion);
        if(region1==null) return region2;
        else return region1;
    }
    
    public boolean HasWGRegion(Location Loc1, Location Loc2){
        Config cfg = ConfigU.getWGRegions(Loc1.getWorld().getName());
        System.out.println(cfg.getFile().getAbsolutePath());
        if(cfg.existeConfig()){
            for(String a:cfg.getConfig().getConfigurationSection("regions").getKeys(false)){
                if(!a.equals("__global__")){
                    MemorySection ms = (MemorySection)cfg.getConfig().get("regions."+a+".min");
                    Location loc1 = new Location(Loc1.getWorld(), ms.getDouble("x"), ms.getDouble("y"), ms.getDouble("z"));
                    ms = (MemorySection)cfg.getConfig().get("regions."+a+".max");
                    Location loc2 = new Location(Loc1.getWorld(), ms.getDouble("x"), ms.getDouble("y"), ms.getDouble("z"));
                    Location[] Locs = ajustLocations(loc1, loc2);
                    Location[] Locs2 = ajustLocations(Loc1, Loc2);
                    if(locationsContainsLocation(Locs[0], Locs[1], Locs2[0])) return true;
                    else if(locationsContainsLocation(Locs[0], Locs[1], Locs2[1])) return true;
                    else if(locationsContainsLocation(Locs[0], Locs[1], Locs2[1].clone().add(Locs2[0].getBlockX()-Locs2[1].getBlockX(), 0, 0))) return true;
                    else if(locationsContainsLocation(Locs[0], Locs[1], Locs2[1].clone().add(0, 0, Locs2[0].getBlockZ()-Locs2[1].getBlockZ()))) return true;
                    else if(locationsContainsLocation(Locs2[0], Locs2[1], Locs[0])) return true;
                }
            }
        }
        return false;
    }
    
    public boolean hasWGRegion(Location Loc){
        Config cfg = ConfigU.getWGRegions(Loc.getWorld().getName());
        if(cfg.existeConfig()){
            for(String a:cfg.getConfig().getConfigurationSection("regions").getKeys(false)){
                String[] ar = cfg.getString("regions."+a+".min").split(",");
                Location loc1 = new Location(Loc.getWorld(), Double.parseDouble(ar[0].split("x: ")[1]), Double.parseDouble(ar[1].split("y: ")[1]), Double.parseDouble(ar[2].split("z: ")[1].replace("}", "")));
                ar = cfg.getString("regions."+a+".max").split(",");
                Location loc2 = new Location(Loc.getWorld(), Double.parseDouble(ar[0].split("x: ")[1]), Double.parseDouble(ar[1].split("y: ")[1]), Double.parseDouble(ar[2].split("z: ")[1].replace("}", "")));
                Location[] Locs = ajustLocations(loc1, loc2);
                if(locationsContainsLocation(Locs[0], Locs[1], Loc)) return true;
            }
        }
        return false;
    }
    
    private boolean locationsContainsLocation(Location loc1, Location loc2, Location loc3){
        int x1 = loc1.getBlockX(), z1 = loc1.getBlockZ(),
            x2 = loc2.getBlockX(), z2 = loc2.getBlockZ(),
            x3 = loc3.getBlockX(), z3 = loc3.getBlockZ();
        return (x3<=x1 && x3>=x2 && z3<=z1 && z3>=z2);
    }
    
    private boolean regionContainsLocation(Location loc, Region region){
        return locationsContainsLocation(region.getLoc1(), region.getLoc2(), loc);
    }
    
    public ArrayList<Region> getAllRegions(Location loc1, Location loc2, boolean subRegions){
        Location[] Locs = ajustLocations(loc1, loc2);
        loc1 = Locs[0];
        loc2 = Locs[1];
        ArrayList<Region> regions = new ArrayList<>();
        for(Region region:allRegions){
            if(region.getLoc1().getBlockX() <= loc1.getBlockX() && region.getLoc1().getBlockX() >= loc2.getBlockX() && region.getLoc1().getBlockZ() <= loc1.getBlockZ() && region.getLoc1().getBlockZ() >= loc2.getBlockZ()){
                if(subRegions) {
                    regions.add(region);
                }else{
                    if(region.getSuperRegion()==null){
                        regions.add(region);
                    }
                }
            }else if(region.getLoc2().getBlockX() <= loc1.getBlockX() && region.getLoc2().getBlockX() >= loc2.getBlockX() && region.getLoc2().getBlockZ() <= loc1.getBlockZ() && region.getLoc2().getBlockZ() >= loc2.getBlockZ()){
                if(subRegions) {
                    regions.add(region);
                }else{
                    if(region.getSuperRegion()==null){
                        regions.add(region);
                    }
                }
            }
        }
        return regions;
    }
    
    public boolean hasRegion(Location loc){
        for(Region region:allRegions){
            if(region.getWorld().equals(loc.getWorld())){
                return regionContainsLocation(loc, region);
            }
        }
        return false;
    }
    
    public boolean hasRegion(Location loc1, Location loc2, boolean WGRegion){
        for(Region region:allRegions){
            if(region.getWorld().equals(loc1.getWorld())){
                if(hasRegion(loc1)) return true;
                else if(hasRegion(loc2)) return true;
                else if(hasRegion(loc2.clone().add(loc1.getBlockX()-loc2.getBlockX(), 0, 0))) return true;
                else if(hasRegion(loc2.clone().add(0, 0, loc1.getBlockZ()-loc2.getBlockZ()))) return true;
            }
        }
        if(locationsContainsRegion(loc1, loc2)) return true;
        else if(WGRegion) return HasWGRegion(loc1, loc2);
        return false;
    }
    
    public boolean regionContainsLocs(Region region, Location Loc1, Location Loc2){
        return (regionContainsLocation(Loc1, region) && regionContainsLocation(Loc2, region));
    }
    
    public boolean locationsContainsRegion(Location Loc1, Location Loc2){
        for(Region rg:allRegions){
            if(locationsContainsLocation(Loc1, Loc2, rg.getLoc1())) return true;
            else if(locationsContainsLocation(Loc1, Loc2, rg.getLoc2())) return true;
            else if(locationsContainsLocation(Loc1, Loc2, rg.getLoc2().clone().add(rg.getLoc1().getBlockX()-rg.getLoc1().getBlockX(), 0, 0))) return true;
            else if(locationsContainsLocation(Loc1, Loc2, rg.getLoc2().clone().add(0, 0, rg.getLoc1().getBlockZ()-rg.getLoc1().getBlockZ()))) return true;
        }
        return false;
    }
    
    public boolean hasSubRegion(Region region, Location Loc1, Location Loc2){
        for(Region subRegion:region.getSubRegions()){
            if(regionContainsLocation(Loc1, subRegion)) return true;
            else if(regionContainsLocation(Loc2, region)) return true;
        }
        return false;
    }
    
    public ArrayList<Region> getAllSubRegionsOfPlayer(Player p){
        ArrayList<Region> Regions = new ArrayList<>();
        for(Region region:getAllRegionsOfPlayer(p, true)){
            if(region.getSuperRegion()!=null){
                Regions.add(region);
            }
        }
        return Regions;
    }
    
    public ArrayList<Region> getAllRegionsOfPlayer(Player p, boolean regionsAndSubRegions){
        ArrayList<Region> regions = new ArrayList<>();
        for(Region region:allRegions){
            if(region.getOwner().equalsIgnoreCase(p.getName())){
                if(regionsAndSubRegions){
                    regions.add(region);
                }else{
                    if(region.getSuperRegion()==null){
                        regions.add(region);
                    }
                }
            }
        }
        return regions;
    }
    
    public boolean canBuildAnotherRegion(Player p){
        int regionsOfPlayer = getAllRegionsOfPlayer(p, false).size();
        int maxPlayerRegions = 0;
        int n = ConfigU.Config.getInt("Max_Terrenos");
        for(int i=1;i<=n;i++){
            if(p.hasPermission("MyHouse.protect."+i)){
                maxPlayerRegions = i;
            }
        }
        return regionsOfPlayer<maxPlayerRegions;
    }
    
    public boolean isBigger(Location Loc1, Location Loc2, int size){
        Location[] Locs = ajustLocations(Loc1, Loc2);
        Loc1 = Locs[0];
        Loc2 = Locs[1];
        return (Loc1.getBlockX()-Loc2.getBlockX()>size || Loc1.getBlockZ()-Loc2.getBlockZ()>size);
    }
    
    public boolean isSmall(Location Loc1, Location Loc2, int size){
        Location[] Locs = ajustLocations(Loc1, Loc2);
        Loc1 = Locs[0];
        Loc2 = Locs[1];
        return (Loc1.getBlockX()-Loc2.getBlockX()<size || Loc1.getBlockZ()-Loc2.getBlockZ()<size);
    }
    
    public boolean isFirstRegionOfPlayer(Player p){
        return !ConfigU.PlayersData.contains(p.getName().toLowerCase());
    }
    
    public double getCostOfRegion(Location Loc1, Location Loc2){
        int x = Loc1.getBlockX() - Loc2.getBlockX(),
            z = Loc1.getBlockZ() - Loc2.getBlockZ();
        if(x<0) x = -x;
        if(z<0) z = -z;
        return ConfigU.Config.getDouble("Custo_Por_Bloco_1")*(x*z);
    }
    
    private Location fds(Location Loc1, Region region){
        Location loc1 = region.getLoc1(), loc2 = region.getLoc2(), loc3 = loc2.clone().add(loc1.getBlockX()-loc2.getBlockX(), 0, 0), loc4 = loc2.clone().add(0, 0, loc1.getBlockZ()-loc2.getBlockZ());
        double d1 = getPlaneDistance(Loc1, loc1);
        double d2 = getPlaneDistance(Loc1, loc2);
        double d3 = getPlaneDistance(Loc1, loc3);
        double d4 = getPlaneDistance(Loc1, loc4);
        Location loc = null;
        if(d1>=d2 && d1>=d3 && d1>=d4){
            loc = loc1;
        }else if(d2>=d1 && d2>=d3 && d2>=d4){
            loc = loc2;
        }else if(d3>=d1 && d3>=d2 && d3>=d4){
            loc = loc3;
        }else{
            loc = loc4;
        }
        return loc;
    }
    
    public double getCostToExpand(Location Loc1, Region region){
        
        Location loc = fds(Loc1, region);
        
        int x1 = loc.getBlockX()-Loc1.getBlockX(), z1 = loc.getBlockZ()-Loc1.getBlockZ();
        int X1 = region.getLoc1().getBlockX()-region.getLoc2().getBlockX(), Z1 = region.getLoc1().getBlockZ()-region.getLoc2().getBlockZ();
        x1 = x1 < 0 ? -x1 : x1;
        z1 = z1 < 0 ? -z1 : z1;
        
        double d = ConfigU.Config.getDouble("Custo_Por_Bloco_2");
        
        if(x1<=X1 && z1<=Z1){
            return -1;
        }else if(x1<=X1 && z1>Z1){
            return ((X1*z1)-(X1*Z1))*d;
        }else if(x1>X1 && z1<=Z1){
            return ((x1*Z1)-(X1*Z1))*d;
        }else{
            return ((x1*z1)-(X1*Z1))*d;
        }
        
    }
    
    public double getPlaneDistance(Location loc1, Location loc2){
        Location[] locs = ajustLocations(loc1, loc2);
        int a = locs[0].getBlockX()-locs[1].getBlockX();
        int b = locs[0].getBlockZ()-locs[1].getBlockZ();
        return Math.sqrt((a*a)+(b*b));
    }
    
    public boolean hasMoney(Player p, Location Loc1, Location Loc2){
        return Economy.getBalance(p.getName()) >= getCostOfRegion(Loc1, Loc2);
    }
    
    public void setFlag(Player p, String flag, String arg){
        Region region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            flag = flag.toUpperCase();
            try{
                Flag FLag = Flag.valueOf(flag);
                if(p.hasPermission("MyHouse.flag."+FLag.toString()) || p.hasPermission("MyHouse.flag.*")){
                    if(isOwner(p.getName(), region) || isOp(p)){
                        if(arg.equalsIgnoreCase("enable")){
                            region.getFlagManager().enableFlag(FLag);
                            p.sendMessage(ConfigU.Lang.getString("flagDefinida").replace('&','§').replace("{flag}", FLag.toString()).replace("{set}", arg.toLowerCase()));
                        }else if(arg.equalsIgnoreCase("disable")){
                            region.getFlagManager().disableFlag(FLag);
                            p.sendMessage(ConfigU.Lang.getString("flagDefinida").replace('&','§').replace("{flag}", FLag.toString()).replace("{set}", arg.toLowerCase()));
                        }else{
                            p.sendMessage("§e/mh flag <flag> <enable/disable> §a- para editar as flags do terreno!");
                        }
                        ConfigU.saveRegion(region);
                    }else{
                        p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&', '§'));
                    }
                }else{
                    p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));
                }
            }catch(Exception e){
                p.sendMessage(ConfigU.Lang.getString("flagNaoExiste").replace('&', '§').replace("{flag}", flag));
            }
        }
    }
    
    public void sellRegion(Player p, int preço){
        Region region = getRegion(p.getLocation(), false);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(isOwner(p.getName(), region)){
                if(region.isVendendo()){
                    
                }else{
                    if(preço<0) {
                        preço = -preço;
                    }
                    p.sendMessage(ConfigU.Lang.getString("vendendoTerreno").replace('&', '§').replace("{custo}", preço+""));
                    if(!playerVender.containsKey(p)) {
                        playerVender.put(p, preço);
                        Plugin.getServer().getScheduler().runTaskLater(Plugin, new Runnable(){

                            final Player player = p;

                            @Override
                            public void run(){
                                if(playerVender.containsKey(player)){
                                    p.sendMessage("Cancelando a venda do terreno!");
                                    playerVender.remove(p);
                                }
                            }
                        }, 400);
                    }
                }
            }else{
                p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&', '§'));
            }
        }
    }
    
    public void warp(Player p, String name){
        Region region = getRegion(name);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado2").replace('&', '§').replace("{nome}", name));
        }else{
            if(region.getWarp()==null){
                p.sendMessage(ConfigU.Lang.getString("semWarp").replace('&', '§'));
            }else{
                p.teleport(region.getWarp());
            }
        }
    }
    
    public void setWarp(Player p){
        Region region = getRegion(p.getLocation(), false);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(p.hasPermission("MyHouse.setwarp")){
                if(isOwner(p.getName(), region) || isMember(p.getName(), region)){
                    region.setWarp(p.getLocation());
                    ConfigU.saveRegion(region);
                    p.sendMessage(ConfigU.Lang.getString("warpDefinida").replace('&', '§'));
                }else{
                    p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&','§'));
                }
            }else{
                p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));
            }
        }
    }
    
    public void delWarp(Player p){
        Region region = getRegion(p.getLocation(), false);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(p.hasPermission("MyHouse.setwarp")){
                if(isOwner(p.getName(), region) || isMember(p.getName(), region)){
                    region.setWarp(null);
                    ConfigU.saveRegion(region);
                    p.sendMessage(ConfigU.Lang.getString("warpDeletada").replace('&', '§'));
                }else{
                    p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&','§'));
                }
            }else{
                p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));
            }
        }
    }
    
    public void setMensagem(Player p, int modo){
        if(modo<0 || modo>3) return;
        Region region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(isOwner(p.getName(), region) || isMember(p.getName(), region)){
                if(modo==0 || modo==2){
                    if(!setandoMensagens.containsKey(p)) {
                        setandoMensagens.put(p, modo==0);
                        setandoMensagens2.put(p, region);
                    }
                    p.sendMessage(ConfigU.Lang.getString("definindoMensagem").replace('&', '§').replace("{modo}", modo==0 ? "entrada" : "saída"));
                }else{
                    if(modo==1){
                        if(region.getEntryMessage()==null){
                            p.sendMessage(ConfigU.Lang.getString("mensagemNaoDefinida").replace('&', '§').replace("{modo}", "entrada"));
                        }else{
                            region.setEntryMessage(null);
                            ConfigU.saveRegion(region);
                            p.sendMessage(ConfigU.Lang.getString("mensagemRemovida").replace('&', '§').replace("{modo}", "entrada"));
                        }
                    }else{
                        if(region.getExitMessage()==null){
                            p.sendMessage(ConfigU.Lang.getString("mensagemNaoDefinida").replace('&', '§').replace("{modo}", "saída"));
                        }else{
                            region.setExitMessage(null);
                            ConfigU.saveRegion(region);
                            p.sendMessage(ConfigU.Lang.getString("mensagemRemovida").replace('&', '§').replace("{modo}", "saída"));
                        }
                    }
                }
            }else{
                p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&','§'));
            }
        }
    }
    
    public void addMember(Player p, String name){
        Region region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(isOwner(p.getName(), region) || isMember(p.getName(), region)){
                if(!(isOwner(name, region) || isMember(name, region))){
                    region.getMembers().add(name.toLowerCase());
                    ConfigU.saveRegion(region);
                }
                p.sendMessage(ConfigU.Lang.getString("membroAdicionado").replace('&', '§').replace("{player}", name));
            }else{
                p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&','§'));
            }
        }
    }
    
    public void remMember(Player p, String name){
        Region region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(isOwner(p.getName(), region) || isMember(p.getName(), region)){
                if(isMember(name, region)){
                    region.getMembers().remove(name.toLowerCase());
                    ConfigU.saveRegion(region);
                }
                p.sendMessage(ConfigU.Lang.getString("membroRemovido").replace('&', '§').replace("{player}", name));
            }else{
                p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&','§'));
            }
        }
    }
    
    public void setCustomName(Player p, String customName){
        if(p.hasPermission("MyHouse.rename")){
            Region region = getRegion(p.getLocation(), false);
            if(region==null){
                p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&','§'));
            }else{
                if(isOwner(p.getName(), region)){
                    double preço = ConfigU.Config.getDouble("Custo_Para_Renomear");
                    if(Economy.getBalance(p.getName())>=preço){
                        Pattern pt = Pattern.compile("&[0-9a-fA-Fk-lK-L]");
                        Matcher mt = pt.matcher(customName);
                        StringBuilder sb = new StringBuilder(customName);
                        while(mt.find()) sb.replace(mt.start(), mt.end()-1, "§");
                        customName = sb.toString();
                        region.setCustomName(customName);
                        ConfigU.saveRegion(region);
                        Economy.withdrawPlayer(p.getName(), preço);
                        p.sendMessage(ConfigU.Lang.getString("terrenoRenomeado").replace('&', '§').replace("{nome}", customName));
                    }else{
                        p.sendMessage(ConfigU.Lang.getString("semDinheiro").replace('&', '§'));
                    }
                }else{
                    p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&','§'));
                }
            }
        }else{
            p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));
        }
    }
    
    public boolean buyRegion(Player p, Region region){
        if(Economy.getBalance(p.getName())>=region.getPreco()){
            transferOwnRegion(region.getOwner(), p, region);
            p.sendMessage(ConfigU.Lang.getString("terrenoComprado").replace('&', '§').replace("{custo}", region.getPreco()+""));
            Player player = null;
            for(Player P:Plugin.getServer().getOnlinePlayers()){
                if(P.getName().equalsIgnoreCase(region.getOwner())){
                    player = P;
                    break;
                }
            }
            if(player==null){
                ConfigU.PlayersData.set(region.getOwner(), ConfigU.Lang.getString("terrenoVendido").replace('&', '§').replace("{custo}", region.getPreco()+"").replace("{player}", p.getName()).replace("{terreno}", region.getName()));
                ConfigU.PlayersData.saveConfig();
                Economy.depositPlayer(region.getOwner(), region.getPreco());
            }else{
                player.sendMessage(ConfigU.Lang.getString("terrenoVendido").replace('&', '§').replace("{custo}", region.getPreco()+"").replace("{player}", p.getName()).replace("{terreno}", region.getName()));
                Economy.depositPlayer(player.getName(), region.getPreco());
            }
            Economy.withdrawPlayer(p.getName(), region.getPreco());
            region.setVendendo(false);
            region.setPreco(0);
            ConfigU.saveRegion(region);
            return true;
        }else{
            p.sendMessage(ConfigU.Lang.getString("semDinheiro").replace('&', '§'));
        }
        return false;
    }
    
    public void transferOwnRegion(Player p, String newOwn){
        Region region = getRegion(p.getLocation(), false);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(isOwner(p.getName(), region)){
                Player player = null;
                try{
                    for(Player P:Plugin.getServer().getOnlinePlayers()){
                        if(P.getName().equalsIgnoreCase(newOwn)){
                            player = P;
                            break;
                        }
                    }
                }catch(Exception e){
                    p.sendMessage(ConfigU.Lang.getString("jogadorNaoEncontrado").replace('&','§').replace("{player}", newOwn));
                    return;
                }
                if(player==null){
                    p.sendMessage(ConfigU.Lang.getString("jogadorNaoEncontrado").replace('&','§').replace("{player}", newOwn));
                }else{
                    transferOwnRegion(p.getName(), player, region);
                    player.sendMessage(ConfigU.Lang.getString("novoDono1").replace('&','§').replace("{terreno}", region.getName()));
                    p.sendMessage(ConfigU.Lang.getString("novoDono2").replace('&','§').replace("{player}", newOwn));
                }
            }else{
                p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&','§'));
            }
        }
    }
    
    private void transferOwnRegion(String oldOwn, Player newOwn, Region region){
        
        String newName = setRegionName(newOwn, region.getRealName().replaceFirst(oldOwn.toLowerCase()+"_", ""));
        int n = 1;
        if(existsRegion(newName)){
            while(true){
                String a = newName;
                a += " ("+n+")";
                if(existsRegion(a)) {
                    n++;
                    continue;
                }
                break;
            }
        }
        Region Region = new Region(region.getLoc1(), region.getLoc2(), new FlagManager(getDefaultFlags()), newOwn.getName().toLowerCase(), newName);
        if(region.getCustomName()!=null) Region.setCustomName(region.getCustomName());
        for(Region REgion:region.getSubRegions()) {
            ConfigU.deleteRegion(REgion);
            allRegions.remove(REgion);
        }
        ConfigU.deleteRegion(region);
        allRegions.remove(region);
        ConfigU.saveRegion(Region);
        allRegions.add(Region);
    }
    
    public void expandRegion(Player p, Location loc1, Location loc2){
        Region region1 = getRegion(loc1, false), region2 = getRegion(loc2, false);
        if(region1 == null && region2 == null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            if(hasRegion(loc1, loc2, false)){
                if(HasWGRegion(loc1, loc2)){
                    p.sendMessage(ConfigU.Lang.getString("terrenoNoLocal").replace('&', '§'));
                    return;
                }
                Region region = region1 == null ? region2 : region1;
                Location loc = region1 == null ? loc1 : loc2;
                if(isOwner(p.getName(), region) || isMember(p.getName(), region) || isOp(p)){
                    if(getAllRegions(loc1, loc2, false).size()>1){
                        p.sendMessage(ConfigU.Lang.getString("terrenoNoLocal").replace('&', '§'));
                    }else{
                        double preço = getCostToExpand(loc, region);
                        if(preço<1){
                            p.sendMessage(ConfigU.Lang.getString("muitoPequeno2").replace('&', '§'));
                        }else{
                            if(Economy.getBalance(p.getName())>=preço){
                                Location Loc1 = fds(loc, region);
                                int x1 = Loc1.getBlockX()-loc.getBlockX(), z1 = Loc1.getBlockZ()-loc.getBlockZ();
                                int X1 = region.getLoc1().getBlockX()-region.getLoc2().getBlockX(), 
                                        Z1 = region.getLoc1().getBlockZ()-region.getLoc2().getBlockZ();
                                Location Loc2 = null;
                                x1 = x1 < 0 ? -x1 : x1;
                                z1 = z1 < 0 ? -z1 : z1;
                                if(x1>X1 && z1>Z1){
                                    Loc2 = loc;
                                }else if(x1<=X1 && z1>Z1){
                                    Loc2 = loc.clone().add(X1-x1, 0, 0);
                                    if(Loc2.getBlockX()-loc.getBlockX()<X1){
                                        Loc2.add((x1-X1)*2, 0, 0);
                                    }
                                }else if(x1>X1 && z1<=Z1){
                                    Loc2 = loc.clone().add(0, 0, Z1-z1);
                                    if(Loc2.getBlockZ()-loc.getBlockZ()<Z1){
                                        Loc2.add(0, 0, (z1-Z1)*2);
                                    }
                                }else{
                                    p.sendMessage(ConfigU.Lang.getString("muitoPequeno2").replace('&', '§'));
                                    return;
                                }
                                if(isBigger(Loc1, Loc2, ConfigU.Config.getInt("Max_Tamanho_Terreno"))){
                                    p.sendMessage(ConfigU.Lang.getString("muitoGrande3").replace('&', '§'));
                                    return;
                                }
                                Location[] Locs = ajustLocations(Loc1, Loc2);
                                region.setLoc1(Locs[0]);
                                region.setLoc2(Locs[1]);
                                ConfigU.saveRegion(region);
                                cercar(region);
                                delimiter(p, Loc1, Loc2);
                                Economy.withdrawPlayer(p.getName(), preço);
                                p.sendMessage(ConfigU.Lang.getString("terrenoExpandido").replace('&', '§').replace("{custo}", preço+""));
                                lu.Loc1s.remove(p);
                                lu.Loc2s.remove(p);
                            }else{
                                p.sendMessage(ConfigU.Lang.getString("semDinheiro").replace('&', '§'));
                            }
                        }
                    }
                }else{
                    p.sendMessage(ConfigU.Lang.getString("naoMembro").replace('&', '§'));
                }
            }
        }
    }
    
    public void createRegion(Player p, Location Loc1, Location Loc2, String name){
        Location[] Locs = ajustLocations(Loc1, Loc2);
        Loc1 = Locs[0];
        Loc2 = Locs[1];
        if(p.hasPermission("MyHouse.protect")){
            if(hasRegion(Loc1, Loc2, true)){
                Region region = getRegion(Loc1, Loc2, false);
                if(region==null){
                    p.sendMessage(ConfigU.Lang.getString("terrenoNoLocal").replace('&', '§'));
                }else{
                    createSubRegion(p, Loc1, Loc2, name);
                }
            }else{
                if(isSmall(Loc1, Loc2, 10)){
                    p.sendMessage(ConfigU.Lang.getString("muitoPequeno").replace('&', '§'));
                    return;
                }
                if(canBuildAnotherRegion(p)){
                    if(isFirstRegionOfPlayer(p)){
                        if(!isBigger(Loc1, Loc2, ConfigU.Config.getInt("Max_Gratis_Terreno"))){
                            name = setRegionName(p, name);
                            Region region = new Region(Loc1, Loc2, new FlagManager(getDefaultFlags()), p.getName().toLowerCase(), name);
                            ConfigU.saveRegion(region);
                            allRegions.add(region);
                            p.sendMessage(ConfigU.Lang.getString("terrenoComprado").replace('&', '§').replace("{custo}", "0"));
                            delimiter(p, Loc1, Loc2);
                            cercar(region);
                            lu.Loc1s.remove(p);
                            lu.Loc2s.remove(p);
                        }else{
                            p.sendMessage(ConfigU.Lang.getString("muitoGrande1").replace('&', '§'));
                        }
                    }else{
                        if(isBigger(Loc1, Loc2, ConfigU.Config.getInt("Max_Compra_Terreno"))){
                            p.sendMessage(ConfigU.Lang.getString("muitoGrande2").replace('&', '§'));
                        }else{
                            if(hasMoney(p, Loc1, Loc2)){
                                double custo = getCostOfRegion(Loc1, Loc2);
                                name = setRegionName(p, name);
                                for(Region region:getAllRegionsOfPlayer(p, false)){
                                    if(region.getName().equalsIgnoreCase(name)){
                                        p.sendMessage(ConfigU.Lang.getString("terrenoJaExiste").replace('&', '§'));
                                        return;
                                    }
                                }
                                Region region = new Region(Loc1, Loc2, new FlagManager(getDefaultFlags()), p.getName().toLowerCase(), name);
                                ConfigU.saveRegion(region);
                                allRegions.add(region);
                                p.sendMessage(ConfigU.Lang.getString("terrenoComprado").replace('&', '§').replace("{custo}", custo+""));
                                delimiter(p, Loc1, Loc2);
                                cercar(region);
                                lu.Loc1s.remove(p);
                                lu.Loc2s.remove(p);
                                Economy.withdrawPlayer(p.getName(), custo);
                            }else{
                                p.sendMessage(ConfigU.Lang.getString("semDinheiro").replace('&', '§'));
                            }
                        }
                    }
                }else{
                    p.sendMessage(ConfigU.Lang.getString("maximoDeTerrenosComprados").replace('&', '§'));
                }
            }
        }else{
            p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));
        }
    }
    
    public void createSubRegion(Player p, Location Loc1, Location Loc2, String name){
        if(p.hasPermission("MyHouse.protect.subterreno")){
            Region region = getRegion(Loc1, Loc2, false);
            if(region==null){
                p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
            }else{
                if(region.getOwner().equalsIgnoreCase(p.getName())){
                    if(regionContainsLocs(region, Loc1, Loc2)){
                        if(!hasSubRegion(region, Loc1, Loc2)){
                            for(Region Region:region.getSubRegions()){
                                String Name = setSubRegionName(region, name);
                                if(Region.getName().equalsIgnoreCase(Name)){
                                    p.sendMessage(ConfigU.Lang.getString("subTerrenoJaExiste").replace('&','§'));
                                    return;
                                }
                            }
                            Region subRegion = new Region(Loc1, Loc2, new FlagManager(getDefaultFlags()), p.getName().toLowerCase(), setSubRegionName(region, name));
                            subRegion.setSuperRegion(region);
                            region.getSubRegions().add(subRegion);
                            ConfigU.saveRegion(subRegion);
                            ConfigU.saveRegion(region);
                            allRegions.add(subRegion);
                            p.sendMessage(ConfigU.Lang.getString("subTerrenoProtegido").replace('&', '§'));
                            delimiter(p, Loc1, Loc2);
                            lu.Loc1s.remove(p);
                            lu.Loc2s.remove(p);
                        }else{
                            p.sendMessage(ConfigU.Lang.getString("subTerrenoNoLocal").replace('&', '§'));
                        }
                    }else{
                        p.sendMessage(ConfigU.Lang.getString("terrenoNoLocal").replace('&', '§'));
                    }
                }else{
                    p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&', '§'));  
                }
            }
        }else{
            p.sendMessage(ConfigU.Lang.getString("semPermissao").replace('&', '§'));   
        }
    }
    
    public void removeRegion(Player p){
        Region region;
        if(playerRemove.containsKey(p)) region = playerRemove.get(p);
        else region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else removeRegion(p, region);
    }
    
    public void removeRegion(Player p, Region region){
        if(region.getOwner().equals(p.getName().toLowerCase()) || isOp(p)){
            if(playerRemove.containsKey(p)){
                ConfigU.deleteRegion(region);
                allRegions.remove(region);
                if(ConfigU.Config.getBoolean("Delete_Rembolso")) Economy.depositPlayer(p.getName(), getCostOfRegion(region.getLoc1(), region.getLoc2()));
                playerRemove.remove(p);
                try{
                    region.getSuperRegion().getSubRegions().remove(region);
                }catch(Exception e){}
                p.sendMessage(ConfigU.Lang.getString("terrenoRemovido").replace('&', '§'));
            }else {
                p.sendMessage(ConfigU.Lang.getString("removerTerrenoConfirma").replace('&', '§').replace("{terreno}", region.getName()));
                playerRemove.put(p, region);
                new Thread(new Runnable() {

                    final Player player = p;

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            if(playerRemove.containsKey(p)) playerRemove.remove(p);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(RegionManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }).start();
            }
        }else{
            p.sendMessage(ConfigU.Lang.getString("naoDono").replace('&','§'));
        }
    }
    
    public void cercar(Region region){
        if(ConfigU.Config.getBoolean("Cercar_Terreno")){
            Location Loc1 = region.getLoc1().clone(), Loc2 = region.getLoc2().clone();
            int x = Loc1.getBlockX()-Loc2.getBlockX(), z = Loc1.getBlockZ()-Loc2.getBlockZ();
            Location Loc3 = Loc2.clone().add(x, 0, 0);
            Location Loc4 = Loc2.clone().add(0, 0, z);
            for(int i=0;i<x;i++){
                getUpperBlock(Loc2.add(1, 0, 0).clone()).add(0, 1, 0).getBlock().setType(Material.LEGACY_BIRCH_FENCE);
                getUpperBlock(Loc1.add(-1, 0, 0).clone()).add(0, 1, 0).getBlock().setType(Material.LEGACY_BIRCH_FENCE);
            }
            for(int i=0;i<z;i++){
                getUpperBlock(Loc3.add(0, 0, 1).clone()).add(0, 1, 0).getBlock().setType(Material.LEGACY_BIRCH_FENCE);
                getUpperBlock(Loc4.add(0, 0, -1).clone()).add(0, 1, 0).getBlock().setType(Material.LEGACY_BIRCH_FENCE);
            }
        }
    }
    
    public Location getUpperBlock(Location loc){
        for(int i=256;i>0;i--){
            loc.setY(i);
            Material mtrl = loc.getBlock().getType();
            if(mtrl != Material.AIR && mtrl != Material.VOID_AIR && mtrl != Material.GRASS && mtrl != Material.TALL_GRASS) {
                return loc;
            }
        }
        return loc;
    }
    
    public void delimiter(Player p, Location Loc1, Location Loc2){
        Loc1 = Loc1.clone();
        Loc2 = Loc2.clone();
        BlockChanger bc = blocks.get(p);
        bc.sendBlock(getUpperBlock(Loc1.clone()), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc1.clone().add(-1, 0, 0)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc1.clone().add(0, 0, -1)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone()), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(Loc1.getBlockX()-Loc2.getBlockX(), 0, 0)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(Loc1.getBlockX()-Loc2.getBlockX()-1, 0, 0)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(Loc1.getBlockX()-Loc2.getBlockX(), 0, 1)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(0, 0, Loc1.getBlockZ()-Loc2.getBlockZ())), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(0, 0, Loc1.getBlockZ()-Loc2.getBlockZ()-1)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(1, 0, Loc1.getBlockZ()-Loc2.getBlockZ())), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(1, 0, 0)), Material.GLOWSTONE, (byte)0);
        bc.sendBlock(getUpperBlock(Loc2.clone().add(0, 0, 1)), Material.GLOWSTONE, (byte)0);
        int x1 = Loc1.getBlockX()-Loc2.getBlockX();
        int z1 = Loc1.getBlockZ()-Loc2.getBlockZ();
        if(x1>19){
            for(int i=1;i<x1/20;i++){
                bc.sendBlock(getUpperBlock(Loc2.clone().add(10*i, 0, 0)), Material.GLOWSTONE, (byte)0);
                bc.sendBlock(getUpperBlock(Loc2.clone().add(-(10*i), 0, 0)), Material.GLOWSTONE, (byte)0);
            }
        }
        if(z1>19){
            for(int i=1;i<x1/20;i++){
                bc.sendBlock(getUpperBlock(Loc2.clone().add(0, 0, 10*i)), Material.GLOWSTONE, (byte)0);
                bc.sendBlock(getUpperBlock(Loc2.clone().add(0, 0,-(10*i))), Material.GLOWSTONE, (byte)0);
            }
        }
    }
    
    public void sendInfo(Player p){
        Region region = getRegion(p.getLocation(), true);
        if(region==null){
            p.sendMessage(ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
        }else{
            sendInfo(p, region);
        }
    }
    
    public void sendInfo(Player p, Region region){
        delimiter(p, region.getLoc1(), region.getLoc2());
        p.sendMessage("§d------------Region Info------------");
        p.sendMessage("§dNome: "+region.getName());
        p.sendMessage("§dDono: "+region.getOwner());
        StringBuilder sb = new StringBuilder("§dMembros: [ ");
        for(String a:region.getMembers()) sb.append(a).append(", ");
        p.sendMessage(sb.append(']').toString());
        p.sendMessage("§dPonta1: "+region.getLoc1().getBlockX()+' '+region.getLoc1().getBlockY()+' '+region.getLoc1().getBlockZ());
        p.sendMessage("§dPonta2: "+region.getLoc2().getBlockX()+' '+region.getLoc2().getBlockY()+' '+region.getLoc2().getBlockZ());
        sb = new StringBuilder("§dFlags: [ ");
        for(Flag a:region.getFlagManager().getEnabledFlags()) sb.append(a.toString()).append(", ");
        sb.append(']');
        p.sendMessage(sb.toString());
    }
    
    public ArrayList<Flag> getDefaultFlags(){
        ArrayList<Flag> defaultFlags = new ArrayList<>();
        for(String a:ConfigU.Config.getString("Default_Flags").split(",")) {
            try{
                defaultFlags.add(Flag.valueOf(a));
            }catch(Exception e){System.out.println("[MyHouse]: A Flag "+a+" nao existe!");}
        }
        return defaultFlags;
    } 
    
    public String setRegionName(Player p, String name){
        return (p.getName()+"_"+name).toLowerCase();
    }
    
    public String setSubRegionName(Region region, String name){
        return (region.getName()+"_"+name).toLowerCase();
    }
    
    public Location[] ajustLocations(Location Loc1, Location Loc2){
        Location[] Locs = new Location[2];
        int x1 = Loc1.getBlockX(), z1 = Loc1.getBlockZ(), x2 = Loc2.getBlockX(), z2 = Loc2.getBlockZ();
        if(x1 > x2 && z1 > z2){
            Locs[0] = Loc1.clone();
            Locs[1] = Loc2.clone();
        }else if(x1 > x2 && z1 <= z2){
            Locs[0] = Loc1.clone().add(0, 0, z2-z1);
            Locs[1] = Loc2.clone().add(0, 0, z1-z2);
        }else if(x1 <= x2 && z1 > z2){
            Locs[0] = Loc1.clone().add(x2-x1, 0, 0);
            Locs[1] = Loc2.clone().add(x1-x2, 0, 0);
        }else if(x1 <= x2 && z1 <= z2){
            Locs[0] = Loc1.clone().add(x2-x1, 0, z2-z1);
            Locs[1] = Loc2.clone().add(x1-x2, 0, z1-z2);
        }
        return Locs;
    }
    
}