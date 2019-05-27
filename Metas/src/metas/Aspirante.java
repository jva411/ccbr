package metas;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import metas.metas.MetaOneBody;
import metas.rewards.Rewards;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class Aspirante {
    
    private Player player;
    private ArrayList<MetaOneBody> metas;
    
    public ArrayList<MetaOneBody> getMetas() {
        return metas;
    }

    public Player getPlayer() {
        return player;
    }
    
    
    
    public void Reward(Rewards Rewards, int meta){
        for(ItemStack is:Rewards.getItems()) Metas.darItem(getPlayer(), is);
        for(String a:Rewards.getCommands()) 
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a.replace("%p%", player.getName()).replace("%meta%", meta+""));
    }
    
}