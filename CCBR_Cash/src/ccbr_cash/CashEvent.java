package ccbr_cash;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CashEvent extends Event {
    
    public enum Origem {
        BOUGHT, CMD, CMDS, KEY, USED;
    }
    final private Origem origem;
    final private int cash;
    final private String pn;
    final private Player p;
    public static final HandlerList Handlers = new HandlerList();
    
    public CashEvent(int cash, Player p, Origem origem){
        this.cash = cash;
        this.pn = p.getName();
        this.p = p;
        this.origem = origem;
    }
    
//    public CashEvent(int cash, String pn, Origem origem){
//        this.cash = cash;
//        this.pn = pn;
//        this.origem = origem;
//    }

    public int getCash() {
        return cash;
    }
    public String getPn() {
        return pn;
    }
    public Player getP() {
        return p;
    }
    public Origem getOrigem() {
        return origem;
    }
    
    
    
    
    @Override
    public HandlerList getHandlers() {
        return Handlers;
    }
    
    public static HandlerList getHandlerList(){
        return Handlers;
    }
    
}
