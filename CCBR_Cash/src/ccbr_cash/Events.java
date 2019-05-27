package ccbr_cash;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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
        if(IsBuilder.isAir(e.getCurrentItem())) return;
        if(name.equals("§a§lCCBR_Cash §e§lMetas")) {
            e.setCancelled(true);
            return;
        }
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
                        if(IsBuilder.isAir(is)){
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
                    n = -n;
                    for(int i=8;i>=0;i--){
                        int S;
                        if(i<5) S=31+i;
                        else S=35-i;
                        ItemStack is = inv.getItem(S);
                        if(!IsBuilder.isAir(is)){
                            int nt = is.getAmount();
                            if(nt>-n){
                                is.setAmount(nt+n);
                                break;
                            }else{
                                if(S==31) {
                                    is.setAmount(1);
                                    break;
                                }
                                inv.setItem(S, new ItemStack(Material.AIR));
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
                    Loja loja = api.playerLoja.get(p);
                    p.closeInventory();
                    int cash = merc.getCash();
                    double money = merc.getMoney();
                    int N=0;
                    for(int i=0;i<9;i++) {
                        ItemStack is = inv.getItem(27+i);
                        if(!IsBuilder.isAir(is)) N += is.getAmount();
                    }
                    if((api.Config.contains("Players."+p.getName()+".buyed") && api.Config.getInt("Players."+p.getName()+".buyed")>=api.Config.getInt("MinimoRequirido")) || p.hasPermission("CCBR_Cash.adm")){
                        if(cash>0){
                            cash = api.getOutDiscount(p, cash)*N;
                            if(api.getCash(p)<cash){
                                p.sendMessage(api.noCash);
                                return;
                            }else{
                                Bukkit.getPluginManager().callEvent(new CashEvent(cash, p, CashEvent.Origem.USED));
                                for(ItemStack is:merc.getItens()) api.darItem(p, is.clone(), N);
                                Pattern pat = Pattern.compile("\\{\\[(.*?)\\]\\}");
                                Matcher mat;
                                for(String a:merc.getCmds()) {
                                    a = a.replace("%p%", p.getName()).replace("%custo%", ""+(cash));
                                    mat = pat.matcher(a);
                                    while(mat.find()){
                                        try{
                                            a = a.replace(mat.group(0), ""+(Integer.parseInt(mat.group(1))*N));
                                        }catch(Exception Ex){}
                                    }
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), a);
                                }
                                api.imprimirLog(p, loja, merc, N);
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
                    }else{
                        p.sendMessage(api.minimoR);
                    }
                }else return;
                int N=0;
                for(int i=0;i<9;i++) {
                    ItemStack is = inv.getItem(27+i);
                    if(!IsBuilder.isAir(is)) N += is.getAmount();
                }
                Mercadoria merc = api.playerMercadoria.get(p);
                int cash = merc.getCash();
                double money = merc.getMoney();
                DecimalFormat df = new DecimalFormat("#.##");
                IsBuilder ib = new IsBuilder();
                if(cash>0) inv.setItem(49, ib.newItem(Material.PAPER).setName("§e§l"+(cash*N)+" §a§lCASH").getItemStack());
                else inv.setItem(49, ib.newItem(Material.PAPER).setName("§a§lR$§e§l"+df.format((money*N)).replace(',', '.')+" §a§lReais").getItemStack());
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCash(CashEvent e){
        CashEvent.Origem o = e.getOrigem();
        String pn = e.getPn();
        int cash = e.getCash();
        if(o==CashEvent.Origem.BOUGHT){
                api.Meta.add(e.getP(), cash);
                for(int i=100;i>0;i--) if(e.getP().hasPermission("CCBR_Cash.buy.buff."+i)) cash *= 1+(i/100);
                api.Config.set("Players."+e.getPn()+".buyed", api.Config.getInt("Players."+e.getPn()+".buyed")+cash);
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
