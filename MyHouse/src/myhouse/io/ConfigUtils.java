package myhouse.io;

import java.util.ArrayList;
import myhouse.com.flags.Flag;
import myhouse.com.flags.FlagManager;
import myhouse.com.region.Region;
import myhouse.com.region.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtils {
    
    public Config Config, Regions, Lang, PlayersData;
    public RegionManager rm;
    public JavaPlugin Plugin;
    
    public ConfigUtils(JavaPlugin plugin, RegionManager rm){
        Plugin = plugin;
        this.rm = rm;
        reload();
    }
    
    public Config getWGRegions(String worldName){
        return new Config(Plugin, "../WorldGuard/worlds/"+worldName+"/regions.yml");
    }
    
    public void reload(){
        Config = new Config(Plugin, "config.yml");
        Regions = new Config(Plugin, "regions.yml");
        PlayersData = new Config(Plugin, "players.dat");
        Config.saveDefaultConfig();
        Regions.saveDefaultConfig();
        Lang = new Config(Plugin, Config.getString("Lang")+".yml");
        Lang.saveDefaultConfig();
        rm.reload();
        loadAllRegions();
    }
    
    public String getProtectionMaterial(){
        return Config.getString("Material_De_Protecao").toUpperCase();
    }
    
    public Region loadRegion(String name){
        if(Regions.contains(name)){
            String[] Points = Regions.getString(name+".Points").split(",");
            String World = Regions.getString(name+".World");
            String Owner = Regions.getString(name+".Owner");
            String[] Flags = Regions.getString(name+".Flags").split(",");
            ArrayList<String> Members = new ArrayList<>(Regions.getConfig().getStringList(name+".Members"));
            String warp = Regions.getString(name+".Warp");
            String SuperRegion = Regions.getString(name+".SuperRegion");
            int preço = -1;
            if(Regions.contains(name+".Preço")) preço = Regions.getInt(name+".Preço");
            boolean vendendo = false;
            if(Regions.contains(name+".Vendendo")) vendendo = Regions.getBoolean(name+".Vendendo");
            World world = Bukkit.getWorld(World);
            Location loc1 = new Location(world, Double.parseDouble(Points[0]), 256, Double.parseDouble(Points[1]));
            Location loc2 = new Location(world, Double.parseDouble(Points[2]), 0, Double.parseDouble(Points[3]));
            ArrayList<Flag> flags = new ArrayList<>();
            for(String a:Flags){
                flags.add(Flag.valueOf(a));
            }
            Region region = new Region(loc1, loc2, new FlagManager(flags), Owner, name);
            if(Regions.contains(name+".MensagemDeEntrada")) region.setEntryMessage(Regions.getString(name+".MensagemDeEntrada"));
            if(Regions.contains(name+".MensagemDeSaida")) region.setExitMessage(Regions.getString(name+".MensagemDeSaida"));
            if(Regions.contains(name+".DisplayName")) region.setCustomName(Regions.getString(name+".DisplayName"));
            region.setMembers(Members);
            if(SuperRegion != null && SuperRegion.length()>0){
                Region Region = rm.getRegion(SuperRegion);
                Region.getSubRegions().add(region);
            }
            if(warp != null && warp.length()>0) {
                String[] Warp = warp.split(",");
                region.setWarp(new Location(world, Double.parseDouble(Warp[0]), Double.parseDouble(Warp[1]), Double.parseDouble(Warp[2]), Float.parseFloat(Warp[3]), Float.parseFloat(Warp[4])));
            }
            if(preço>0) region.setPreco(preço);
            region.setVendendo(vendendo);
            rm.allRegions.add(region);
        }
        return null;
    }
    
    public void loadAllRegions(){
        for(String a:Regions.getConfig().getConfigurationSection("").getKeys(false)){
            try{
                loadRegion(a);
            }catch(Exception e){System.out.println("[MyHouse]: Terreno nao carregado \""+a+"\"");}
        }
    }
    
    public void saveRegion(Region region){
        StringBuilder sb = new StringBuilder();
        Regions.set(region.getRealName()+".Points", sb.append(region.getLoc1().getBlockX()).append(',').append(region.getLoc1().getBlockZ()).append(',').append(region.getLoc2().getBlockX()).append(',').append(region.getLoc2().getBlockZ()).toString());
        Regions.set(region.getRealName()+".World", region.getWorld().getName());
        Regions.set(region.getRealName()+".Owner", region.getOwner());
        if(!region.getRealName().equals(region.getName())) Regions.set(region.getRealName()+".DisplayName", region.getName());
        sb = new StringBuilder();
        for(Flag Flag:region.getFlagManager().getEnabledFlags()) sb.append(Flag.toString()).append(",");
        Regions.set(region.getRealName()+".Flags", sb.toString());
        Regions.set(region.getRealName()+".Members", region.getMembers());
        if(region.getWarp()!=null) {
            sb = new StringBuilder();
            Regions.set(region.getRealName()+".Warp", sb.append(region.getWarp().getBlockX()).append(',').append(region.getWarp().getBlockY()).append(',').append(region.getWarp().getBlockZ()).append(',').append(region.getWarp().getYaw()).append(',').append(region.getWarp().getPitch()).toString());
        }
        if(region.getSuperRegion() != null) Regions.set(region.getRealName()+".SuperRegion", region.getSuperRegion().getName());
        ArrayList<String> subRegions = new ArrayList<>();
        for(Region subRegion:region.getSubRegions()) subRegions.add(subRegion.getName());
        Regions.set(region.getRealName()+".Vendendo", region.isVendendo());
        Regions.set(region.getRealName()+".Preço", region.getPreco());
        Regions.set(region.getRealName()+".SubRegions", subRegions);
        if(region.getEntryMessage()!=null) Regions.set(region.getRealName()+".MensagemDeEntrada",region.getEntryMessage());
        if(region.getExitMessage()!=null) Regions.set(region.getRealName()+".MensagemDeSaida",region.getExitMessage());
        Regions.saveConfig();
        PlayersData.set(region.getOwner().toLowerCase(), "");
        PlayersData.saveConfig();
    }
    
    public void deleteRegion(Region region){
        Regions.set(region.getRealName(), null);
        for(Region Region:region.getSubRegions()) Regions.set(Region.getName(), null);
        Regions.saveConfig();
    }
    
}
