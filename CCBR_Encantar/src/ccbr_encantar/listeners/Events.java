package ccbr_encantar.listeners;

import ccbr_encantar.models.LivroAleatorio;
import ccbr_encantar.utils.API;
import ccbr_encantar.utils.SetExpFix;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener{
    
    public API api;
    
    public Events(API api){
        this.api = api;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if(inv.getName().equals("§a§lCCBR§7§l_§e§lEncantar")){
            e.setCancelled(true);
            if(inv.equals(e.getClickedInventory())){
                Player p = (Player) e.getWhoClicked();
                int s = e.getRawSlot();
                if(s==19 || s==20 || s==21 || s==28 || s==29 || s==30) api.confirmaLivro(api.getLivroAleatorio(e.getCurrentItem()), p);
                else if(s==32 || s==33 || s==34) api.openAmuletShop(p);
            }
        }else if(inv.getName().equals("§a§lCCBR§7§l_§e§lEncantar §b§lRandomBook")){
            e.setCancelled(true);
            if(inv.equals(e.getClickedInventory())){
                Player p = (Player) e.getWhoClicked();
                int s = e.getRawSlot();
                int n = e.getCurrentItem().getAmount();
                LivroAleatorio la = api.getLivroAleatorio(inv.getItem(31));
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
                    p.openInventory(api.Menu);
                    return;
                }else if(s==51){
                    p.closeInventory();
                    int N=0;
                    for(int i=0;i<9;i++) {
                        ItemStack is = inv.getItem(27+i);
                        if(!api.Iapi.isAir(is)) N += is.getAmount();
                    }
                    int exp = la.getExp()*N;
                    if(SetExpFix.getTotalExperience(p)<exp){
                        p.sendMessage(api.Config.getString("Msgs.noEXP").replace('&', '§'));
                        return;
                    }else{
                        SetExpFix.setTotalExperience(p, SetExpFix.getTotalExperience(p)-exp);
                        api.ccbr_cash.api.darItem(p, api.getLivroAleatorio(inv.getItem(31)).getLivro(), N);
                        return;
                    }
                }else return;
                int N=0;
                for(int i=0;i<9;i++) {
                    ItemStack is = inv.getItem(27+i);
                    if(!api.Iapi.isAir(is)) N += is.getAmount();
                }
                inv.setItem(49, api.Iapi.setName(api.Iapi.newItem(339), "§e§l"+(N*la.getExp())+" §a§lExp"));
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
            if(e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)) e.setCancelled(true);
            else if(e.getClickedBlock().getType().equals(Material.ANVIL)) e.setCancelled(true);
        }else if(e.getAction()==Action.RIGHT_CLICK_AIR){
            ItemStack is = p.getItemInHand();
            if(api.isRandomBook(is)){
                p.setItemInHand(API.Iapi.separeInAmount(is, 1)[1]);
                api.darItem(p, api.getLivroAleatorio(is).open().getLivro());
            }
        }
    }
    
}
