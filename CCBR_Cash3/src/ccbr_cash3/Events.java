package ccbr_cash3;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener{
    
    public Api api;
    
    public void Init(Api api){
        this.api = api;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(api.getCash(e.getPlayer())<0) api.setCash(e.getPlayer(), 0);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String name = e.getInventory().getName();
        if(api.Iapi.isAir(e.getCurrentItem())) return;
        if(api.InvNames.contains(name)){
            e.setCancelled(true);
            Player p = (Player)e.getWhoClicked();
            if(!api.playerLoja.containsKey(p)) {
                p.closeInventory();
                return;
            }
            if(!e.getClickedInventory().equals(p.getInventory())){
                int s = e.getRawSlot();
                Loja loja = api.playerLoja.get(p);
                if(loja.getLojas().containsKey(s)){
                    Loja loja2 = loja.getLojas().get(s);
                    api.openLoja(p, loja2);
                }else if(loja.getMercadorias().containsKey(s)){
                    Mercadoria merc = loja.getMercadorias().get(s);
                    api.openMercadoria(p, merc);
                }else if(loja.getVoltar().containsKey(s)){
                    api.playerLoja2.add(p);
                    api.openLoja(p, loja.getLojaMae());
                }
            }
        }else if(name.equals("§a§lCCBR §2§lConfirmação")){
            e.setCancelled(true);
            Player p = (Player)e.getWhoClicked();
            if(!api.playerLoja.containsKey(p)) {
                p.closeInventory();
                return;
            }
            if(!e.getClickedInventory().equals(p.getInventory())){
                int s = e.getRawSlot();
                Inventory inv = e.getInventory();
                int n = e.getCurrentItem().getAmount();
                if(s==9 || s==10 || s==11){
                    for(int i=0;i<9;i++){
                        int S;
                        if(i<5) S=31+i;
                        else S=35-i;
                        ItemStack is = inv.getItem(S);
                        if(api.Iapi.isAir(is)){
                            ItemStack is2 = inv.getItem(31).clone();
                            is2.setAmount(n);
                            inv.setItem(S, is2);
                            break;
                        }
                        int nf = is.getMaxStackSize()-is.getAmount();
                        if(nf>0){
                            if(nf>=n){
                                is.setAmount(is.getAmount()+n);
                                break;
                            }else{
                                is.setAmount(is.getMaxStackSize());
                                n -= nf;
                                if(n==0) break;
                            }
                        }
                    }
                }else if(s==15 || s==16 || s==17){
                    for(int i=8;i>=0;i--){
                        int S;
                        if(i<5) S=31+i;
                        else S=35-i;
                        ItemStack is = inv.getItem(S);
                        if(!api.Iapi.isAir(is)){
                            int nt = is.getAmount();
                            if(nt>-n){
                                is.setAmount(nt+n);
                                break;
                            }else{
                                if(S==31) {
                                    is.setAmount(1);
                                    break;
                                }
                                inv.setItem(S, api.Iapi.newItem(0));
                                n += nt;
                                if(n==0) break;
                            }
                        }
                    }
                }else if(s==47) {
                    api.playerLoja2.add(p);
                    p.openInventory(api.playerLoja.get(p).getInventory());
                    return;
                }else if(s==51){
                    Mercadoria merc = api.playerMercadoria.get(p);
                    p.closeInventory();
                    int cash = merc.getCash();
                    double money = merc.getMoney();
                    int N=0;
                    for(int i=0;i<9;i++) {
                        ItemStack is = inv.getItem(27+i);
                        if(!api.Iapi.isAir(is)) N += is.getAmount();
                    }
                    if(cash>0){
                        if(api.getCash(p)<cash*N){
                            p.sendMessage(api.noCash);
                            return;
                        }else{
                            Bukkit.getPluginManager().callEvent(new CashEvent(cash*N, p, CashEvent.Origem.USED));
                            for(ItemStack is:merc.getItens()) api.darItem(p, is.clone(), N);
                            Pattern pat = Pattern.compile("\\{\\[(.*?)\\]\\}");
                            Matcher mat;
                            for(String a:merc.getCmds()) {
                                a = a.replace("%p%", p.getName()).replace("%custo%", ""+(cash*N));
                                mat = pat.matcher(a);
                                while(mat.find()){
                                    try{
                                        a = a.replace(mat.group(0), ""+(Integer.parseInt(mat.group(1))*N));
                                    }catch(Exception Ex){}
                                }
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a);
                            }
                            api.imprimirLog(p, merc, N);
                            return;
                        }
                    }else{
                        if(api.economy.getBalance(p.getName())<money*N){
                            p.sendMessage(api.noMoney);
                            return;
                        }else{
                            api.economy.withdrawPlayer(p.getName(), money*N);
                            for(ItemStack is:merc.getItens()) api.darItem(p, is, N);
                            Pattern pat = Pattern.compile("\\{\\[(.*?)\\]\\}");
                            Matcher mat;
                            for(String a:merc.getCmds()) {
                                a = a.replace("%p%", p.getName()).replace("%custo%", ""+(money*N));
                                mat = pat.matcher(a);
                                while(mat.find()){
                                    try{
                                        a = a.replace(mat.group(0), ""+(Integer.parseInt(mat.group(1))*N));
                                    }catch(Exception Ex){}
                                }
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a);
                            }
                            return;
                        }
                    }
                }else return;
                int N=0;
                for(int i=0;i<9;i++) {
                    ItemStack is = inv.getItem(27+i);
                    if(!api.Iapi.isAir(is)) N += is.getAmount();
                }
                Mercadoria merc = api.playerMercadoria.get(p);
                int cash = merc.getCash();
                double money = merc.getMoney();
                DecimalFormat df = new DecimalFormat("#.##");
                if(cash>0) inv.setItem(49, api.Iapi.setName(api.Iapi.newItem(339), "§e§l"+(cash*N)+" §a§lCASH"));
                else inv.setItem(49, api.Iapi.setName(api.Iapi.newItem(339), "§a§lR$§e§l"+df.format((money*N)).replace(',', '.')+" §a§lReais"));
            }
        }
    }
    
    @EventHandler
    public void onInvetoryClose(InventoryCloseEvent e){
        try{
            Player p = (Player)e.getPlayer();
            if(api.playerLoja.containsKey(p)){
                if(api.playerLoja2.contains(p)) api.playerLoja2.remove(p);
                else {
                    api.playerLoja.remove(p);
                    if(api.playerMercadoria.containsKey(p)) api.playerMercadoria.remove(p);
                }
            }
        }catch(Exception ex){}
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler
    public void onCash(CashEvent e){
        CashEvent.Origem o = e.getOrigem();
        String pn = e.getPn();
        int cash = e.getCash();
        if(o==CashEvent.Origem.BOUGHT){
                api.addCash(pn, cash);
        }else if(o==CashEvent.Origem.USED){
                api.remCash(pn, cash);
        }else if(o==CashEvent.Origem.CMD){
                if(cash<0) api.remCash(pn, cash);
                else api.addCash(pn, cash);
        }else if(o==CashEvent.Origem.KEY){
                api.addCash(pn, cash);
        }else if(o==CashEvent.Origem.CMDS){
                api.setCash(pn, cash);
        }
    }
    
}
