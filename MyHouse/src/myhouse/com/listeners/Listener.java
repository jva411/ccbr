package myhouse.com.listeners;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import myhouse.BlockChanger;
import myhouse.com.region.Region;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listener implements org.bukkit.event.Listener{
    
    public ListenerUtils listU;
    public ArrayList<Player> comprando = new ArrayList<>();

    public void setListU(ListenerUtils listU) {
        this.listU = listU;
    }
    
    @EventHandler
    public void onBlockPlace(BlockBreakEvent e) {
        if(e.getBlock().getState() instanceof Sign){
            Sign sign = (Sign)e.getBlock().getState();
            if(sign.getLines()[0].equals("§4§l[MyHouse]") && sign.getLines()[1].equals("§2§lÀ Venda!") && sign.getLines()[2].equals("§2§lPreço:")){
                Region region = listU.rm.getRegion(sign.getLocation(), false);
                if(region!=null){
                    region.setVendendo(false);
                    region.setPreco(0);
                   listU.rm.ConfigU.saveRegion(region);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        listU.rm.blocks.put(e.getPlayer(), new BlockChanger(e.getPlayer(), listU.rm.Plugin));
        if(listU.rm.ConfigU.PlayersData.contains(e.getPlayer().getName().toLowerCase())){
            String a = listU.rm.ConfigU.PlayersData.getString(e.getPlayer().getName().toLowerCase());
            if(a.length()>0){
                e.getPlayer().sendMessage(a);
                listU.rm.ConfigU.PlayersData.set(e.getPlayer().getName().toLowerCase(), "");
                listU.rm.ConfigU.PlayersData.saveConfig();
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        listU.rm.blocks.remove(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
            if(e.getClickedBlock().getType().toString().contains("SIGN")){
                if(listU.rm.playerVender.containsKey(p)){
                    Region region = listU.rm.getRegion(e.getClickedBlock().getLocation(), false);
                    if(region==null){
                        p.sendMessage(listU.rm.ConfigU.Lang.getString("terrenoNaoEncontrado").replace('&', '§'));
                    }else{
                        if(listU.rm.isOwner(p.getName(), region)){
                            int preço = listU.rm.playerVender.get(p);
                            Sign sign = (Sign)e.getClickedBlock().getState();
                            sign.setLine(0, "§4§l[MyHouse]");
                            sign.setLine(1, "§2§lÀ Venda!");
                            sign.setLine(2, "§2§lPreço:");
                            sign.setLine(3, "§6§l"+preço);
                            sign.update();
                            region.setPreco(preço);
                            region.setVendendo(true);
                            listU.rm.ConfigU.saveRegion(region);
                            p.sendMessage(listU.rm.ConfigU.Lang.getString("terrenoAVenda").replace('&', '§').replace("{custo}", preço+"").replace("{terreno}", region.getName()));
                            listU.rm.playerVender.remove(p);
                        }else{
                            p.sendMessage(listU.rm.ConfigU.Lang.getString("naoDono").replace('&', '§'));
                        }
                    }
                }else{
                    Region region = listU.rm.getRegion(e.getClickedBlock().getLocation(), false);
                    if(region!=null){
                        Sign sign = (Sign)e.getClickedBlock().getState();
                        if(sign.getLines()[0].equals("§4§l[MyHouse]") && sign.getLines()[1].equals("§2§lÀ Venda!") && sign.getLines()[2].equals("§2§lPreço:")){
                            if(comprando.contains(p)){
                                if(listU.rm.buyRegion(p, region)){
                                    sign.setLine(0, "§4§l[MyHouse]");
                                    sign.setLine(1, "§2§lVendido!");
                                    sign.setLine(2, "");
                                    sign.setLine(3, "");
                                    sign.update();
                                }
                                comprando.remove(p);
                            }else{
                                p.sendMessage(listU.rm.ConfigU.Lang.getString("confirmarCompra").replace('&', '§').replace("{dono}", region.getOwner()).replace("{custo}", region.getPreco()+"").replace("{terreno}", region.getName()));
                                comprando.add(p);
                                listU.rm.Plugin.getServer().getScheduler().runTaskLaterAsynchronously(listU.rm.Plugin, new Runnable(){
                                    
                                    final Player player = p;
                                    
                                    @Override
                                    public void run(){
                                        if(comprando.contains(player)){
                                            player.sendMessage(listU.rm.ConfigU.Lang.getString("compraCancelada").replace('&', '§'));
                                            comprando.remove(player);
                                        }
                                    }
                                    
                                }, 200);
                            }
                        }
                    }
                }
            }
        }
        if(p.getItemInHand().getType()==Material.valueOf(listU.rm.ConfigU.getProtectionMaterial())){
            if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
                e.setCancelled(true);
                listU.setLocation2(p, e.getClickedBlock().getLocation());
            }else if(e.getAction()==Action.LEFT_CLICK_BLOCK){
                e.setCancelled(true);
                listU.setLocation1(p, e.getClickedBlock().getLocation());
            }
        }
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        if(!e.isCancelled()){
            Player p = e.getPlayer();
            Region region = listU.rm.getRegion(e.getTo(), true);
            Region Region = listU.rm.getRegion(e.getFrom(), true);
            if(region==null || !region.equals(Region)){
                if(Region!=null && !Region.equals(region.getSuperRegion())){
                    p.sendMessage(Region.getExitMessage());
                }
            }
            if(region!=null) p.sendMessage(region.getEntryMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void chatPlayer(AsyncPlayerChatEvent e){
        if(listU.rm.setandoMensagens.containsKey(e.getPlayer())){
            Pattern pt = Pattern.compile("&[0-9a-fA-Fk-lK-L]");
            Matcher mt = pt.matcher(e.getMessage());
            StringBuilder sb = new StringBuilder(e.getMessage());
            while(mt.find()) sb.replace(mt.start(), mt.end()-1, "§");
            if(listU.rm.setandoMensagens.get(e.getPlayer())) {
                listU.rm.setandoMensagens2.get(e.getPlayer()).setEntryMessage(sb.toString());
                e.getPlayer().sendMessage(listU.rm.ConfigU.Lang.getString("mensagemDefinida").replace('&', '§').replace("{modo}", "entrada"));
            }else {
                listU.rm.setandoMensagens2.get(e.getPlayer()).setExitMessage(sb.toString());
                e.getPlayer().sendMessage(listU.rm.ConfigU.Lang.getString("mensagemDefinida").replace('&', '§').replace("{modo}", "saída"));
            }
            listU.rm.ConfigU.saveRegion(listU.rm.setandoMensagens2.get(e.getPlayer()));
            listU.rm.setandoMensagens.remove(e.getPlayer());
            listU.rm.setandoMensagens2.remove(e.getPlayer());
            e.setCancelled(true);
        }
    }
    
}