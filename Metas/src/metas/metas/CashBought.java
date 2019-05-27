package metas.metas;

import ccbr_cash3.CashEvent;
import java.util.HashMap;
import metas.Aspirante;
import metas.Metas;
import metas.rewards.Rewards;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CashBought extends MetaGroup implements Listener{

    public CashBought(HashMap<Integer, Integer> Metas, HashMap<Integer, Rewards> RewardsOne, HashMap<Integer, Rewards> RewardsAll, Metas Plugin) {
        super(Metas, RewardsOne, RewardsAll, Plugin);
    }
    
    @EventHandler
    public void onCash(CashEvent e){
        if(getFinished()>=getMetas().size()) return;
        if(e.getOrigem().equals(CashEvent.Origem.BOUGHT)){
            int a = getAmount()+e.getCash();
            setAmount(a);
            if(a>getMetas().get(getFinished())){
                getPlugin().aspirantes.get(e.getP()).Reward(getRewardsOne().get(getFinished()), getFinished());
                for(Aspirante asp:getPlugin().aspirantes.values()) asp.Reward(getRewardsAll().get(getFinished()), getFinished());
                setFinished(getFinished()+1);
            }
        }
    }
    
}
