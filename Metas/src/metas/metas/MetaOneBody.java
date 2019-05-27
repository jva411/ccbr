package metas.metas;

import java.util.HashMap;
import metas.Metas;
import metas.rewards.Rewards;

public abstract class MetaOneBody {
    
    protected int Finished, Amount;
    protected HashMap<Integer, Integer> Metas;
    protected HashMap<Integer, Rewards> Rewards;
    protected Metas Plugin;

    public MetaOneBody(HashMap<Integer, Integer> Metas, HashMap<Integer, Rewards> Rewards, Metas plugin) {
        this.Finished = 0;
        this.Amount = 0;
        this.Metas = Metas;
        this.Rewards = Rewards;
        this.Plugin = plugin;
    }

    public HashMap<Integer, Integer> getMetas() {
        return Metas;
    }

    public HashMap<Integer, Rewards> getRewards() {
        return Rewards;
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
