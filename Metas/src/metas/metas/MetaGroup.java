package metas.metas;

import java.util.HashMap;
import metas.Metas;
import metas.rewards.Rewards;

public abstract class MetaGroup {
    
    protected int Finished, Amount;
    protected HashMap<Integer, Integer> Metas;
    protected HashMap<Integer, Rewards> RewardsOne, RewardsAll;
    protected Metas Plugin;

    public MetaGroup(HashMap<Integer, Integer> Metas, HashMap<Integer, Rewards> RewardsOne, HashMap<Integer, Rewards> RewardsAll, Metas Plugin) {
        this.Metas = Metas;
        this.RewardsOne = RewardsOne;
        this.RewardsAll = RewardsAll;
        this.Plugin = Plugin;
        this.Finished = 0;
        this.Amount = 0;
    }
    
    public HashMap<Integer, Integer> getMetas() {
        return Metas;
    }

    public HashMap<Integer, Rewards> getRewardsAll() {
        return RewardsAll;
    }

    public HashMap<Integer, Rewards> getRewardsOne() {
        return RewardsOne;
    }

    public Metas getPlugin() {
        return Plugin;
    }

    public int getFinished() {
        return Finished;
    }

    public int getAmount() {
        return Amount;
    }

    public void setFinished(int Finished) {
        this.Finished = Finished;
    }

    public void setAmount(int Amount) {
        this.Amount = Amount;
    }
    
}
