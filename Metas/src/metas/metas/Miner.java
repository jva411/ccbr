package metas.metas;

import java.util.HashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class Miner extends MetaOneBody implements Listener{

    public Miner(HashMap<Integer, Integer> Metas, HashMap<Integer, metas.rewards.Rewards> Rewards, metas.Metas plugin) {
        super(Metas, Rewards, plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockBreakEvent e) {
        if(!e.isCancelled()){
            int a = getAmount()+1;
            setAmount(a);
            if(a==getMetas().get(getFinished())){
                getPlugin().aspirantes.get(e.getPlayer()).Reward(getRewards().get(getFinished()), getFinished());
                setFinished(getFinished()+1);
            }
        }
    }
}
