package me.jva.ccbr_events;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener{
    
    public final CCBR_Events Main;

    public EventListener(CCBR_Events main) {
        this.Main = main;
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        Eventer evr = Main.getEventer(p);
        if(evr!=null){
            Event event = evr.getEvent();
            if(event.getState() == Event.STATE.PLAYING){
                for (LosseCube lc : event.getLosseCubes()){
                    if(lc.isLossePoint(e.getTo())){
                        if(event.isKickOnLoss()) event.remEventer(p);
                        else p.teleportAsync(evr.getSpawnPoint());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDisconnect(PlayerQuitEvent e){
        Eventer evr = Main.getEventer(e.getPlayer());
        if(evr!=null){
            evr.getEvent().remEventer(evr.getPlayer());
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Eventer evr = Main.getEventer(p);
        if(e.getAction()==Action.RIGHT_CLICK_BLOCK){
            if(p.getItemInHand()==null || p.getItemInHand().getType()==Material.AIR){
                if(Main.makingEvent.containsKey(p)){
                    EventBuilder eb = Main.makingEvent.get(p);
                    if(eb.getStage()==3){
                        eb.setPos1(e.getClickedBlock().getLocation());
                        p.sendMessage(Main.lang.getString("CriandoNovoEvento.Pos2Definida").replace('&', '§'));
                        e.setCancelled(true);
                    }
                }else if(evr != null){
                    if(e.getClickedBlock().getType().toString().contains("SIGN")){
                        Sign sign = (Sign)e.getClickedBlock().getState();
                        if(sign.getLine(0).equals("§a§l[CCBR_Events]") && sign.getLine(1).equals("§e§lCheckPoint") && sign.getLine(2).length()==0 && sign.getLine(3).length()==0){
                            evr.setSpawnPoint(p.getLocation());
                            evr.getPlayer().sendMessage(Main.lang.getString("CheckpointDefinido").replace('&', '§'));
                        }
                        else if(sign.getLine(0).equals("§a§l[CCBR_Events]") && sign.getLine(1).equals("§c§lFinalizar") && sign.getLine(2).length()==0 && sign.getLine(3).length()==0){
                            evr.getEvent().setWinner(evr);
                            evr.getEvent().finallize();
                        }
                    }
                }
            }
        }else if(e.getAction()==Action.LEFT_CLICK_BLOCK){
            if(p.getItemInHand()==null || p.getItemInHand().getType()==Material.AIR){
                if(Main.makingEvent.containsKey(p)){
                    EventBuilder ev = Main.makingEvent.get(p);
                    if(ev.getStage()==3){
                        ev.setPos2(e.getClickedBlock().getLocation());
                        p.sendMessage(Main.lang.getString("CriandoNovoEvento.Pos1Definida").replace('&', '§'));
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onChangeSign(SignChangeEvent sign){
        if(sign.getLine(0).equals("[CCBR_Events]") && sign.getLine(1).equals("final")){
            sign.setLine(0, "§a§l[CCBR_Events]");
            sign.setLine(1, "§c§lFinalizar");
            sign.setLine(2, "");
            sign.setLine(3, "");
        }else if(sign.getLine(0).equals("[CCBR_Events]") && sign.getLine(1).equals("checkpoint")){
            sign.setLine(0, "§a§l[CCBR_Events]");
            sign.setLine(1, "§e§lCheckPoint");
            sign.setLine(2, "");
            sign.setLine(3, "");
        }
    }
    
}
