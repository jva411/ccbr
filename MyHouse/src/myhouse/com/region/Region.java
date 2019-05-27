package myhouse.com.region;

import java.util.ArrayList;
import myhouse.com.flags.FlagManager;
import org.bukkit.Location;
import org.bukkit.World;

public class Region {
    
    private Location Loc1, Loc2, Warp;
    private World world;
    private boolean Vendendo;
    private int preco;
    private FlagManager FlagManager;
    private ArrayList<String> members = new ArrayList<>();
    private String Owner, Name, CustomName, entryMessage, exitMessage;
    private ArrayList<Region> SubRegions = new ArrayList<>();
    private Region SuperRegion;

    public Region(Location Loc1, Location Loc2, FlagManager FlagManager, String Owner, String Name) {
        this.Loc1 = Loc1;
        this.Loc2 = Loc2;
        this.FlagManager = FlagManager;
        this.Owner = Owner.toLowerCase();
        this.Name = Name.toLowerCase();
        this.Loc1.setY(256);
        this.Loc2.setY(0);
        this.world = Loc1.getWorld();
        this.Vendendo = false;
        this.CustomName = "";
    }

    public Location getLoc1() {
        return Loc1;
    }

    public Location getLoc2() {
        return Loc2;
    }

    public FlagManager getFlagManager() {
        return FlagManager;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getOwner() {
        return Owner;
    }

    public World getWorld() {
        return world;
    }

    public ArrayList<Region> getSubRegions() {
        return SubRegions;
    }

    public Region getSuperRegion() {
        return SuperRegion;
    }

    public String getRealName(){
        return Name;
    }
    
    public String getName() {
        return CustomName.length()<1 ? Name : CustomName;
    }

    public String getCustomName() {
        return CustomName;
    }
    
    public Location getWarp() {
        return Warp;
    }

    public boolean isVendendo() {
        return Vendendo;
    }

    public int getPreco() {
        return preco;
    }

    public String getEntryMessage() {
        return entryMessage;
    }

    public String getExitMessage() {
        return exitMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    public void setExitMessage(String exitMessage) {
        this.exitMessage = exitMessage;
    }
    
    public void setLoc1(Location Loc1) {
        this.Loc1 = Loc1;
    }

    public void setLoc2(Location Loc2) {
        this.Loc2 = Loc2;
    }

    public void setFlagManager(FlagManager FlagManager) {
        this.FlagManager = FlagManager;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public void setOwner(String Owner) {
        this.Owner = Owner;
    }

    public void setSubRegions(ArrayList<Region> SubRegions) {
        this.SubRegions = SubRegions;
    }

    public void setSuperRegion(Region SuperRegion) {
        this.SuperRegion = SuperRegion;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setWarp(Location Warp) {
        this.Warp = Warp;
    }

    public void setVendendo(boolean Vendendo) {
        this.Vendendo = Vendendo;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public void setCustomName(String CustomName) {
        this.CustomName = CustomName;
    }
    
}
