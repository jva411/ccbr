package me.jvagamer.cpu.com.inventory;

import me.jvagamer.cpu.listeners.Listenner;
import me.jvagamer.cpu.api.IsBuilder;
import me.jvagamer.cpu.com.SuperStack;
import me.jvagamer.cpu.com.component.Component;
import me.jvagamer.cpu.com.machine.ComplexMachine;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Inventore {
    
    private ArrayList<Page> Paginas;
    private HashMap<Player, Page> Players;

    public Inventore(String name) {
        this.Paginas = new ArrayList<>();
        this.Players = new HashMap<>();
        update(new ArrayList<>(), new ArrayList<>(), name);
    }
    
    public void update(ArrayList<SuperStack> SSs, ArrayList<Component> Components, String name){
        Paginas = new ArrayList<>();
        Page work = new Page(0, 9, name+" §0Work");
        for(int i=0;i<Components.size();i++) work.Inventory.setItem(i, Components.get(i).getItemStack());
        Paginas.add(work);
        SSs = Sorter.sortByName(SSs);
        for(int i=0;i<=SSs.size()/36;i++) Paginas.add(new Page(i+1, 54, name));
        for(int i=36;i<45;i++) Paginas.get(1).Inventory.setItem(i, new IsBuilder().newItem(Material.BLACK_STAINED_GLASS_PANE).setName("§a").getItemStack());
        for(int i=0;i<SSs.size();i++){
            SuperStack ss = SSs.get(i);
            Page page = Paginas.get((i/36)+1);
            page.Inventory.setItem(i%36, ss.getSuperStack());
            if(i%36==35){
                page.Inventory.setItem(53, new IsBuilder().newItem(Material.PAPER).setName("nextPage").getItemStack());
            }
            if(i%36==0){
                if(i/36>0){
                    page.Inventory.setItem(45, new IsBuilder().newItem(Material.PAPER).setName("lastPage").getItemStack());
                }
                for(int j=36;j<45;j++) {
                    page.Inventory.setItem(j, new IsBuilder().newItem(Material.BLACK_STAINED_GLASS_PANE).setName("§a").getItemStack());
                }
            }
        }
        HashMap<Player, Page> players = new HashMap<>();
        for(Player p:Players.keySet()){
            int n = (SSs.size()/36)+1;
            int i = Players.get(p).Page;
            if(i>n) i = n; 
            open(p, Listenner.Players.get(p), i);
            players.put(p, Paginas.get(i));
        }
        Players = players;
    }
    
    public void closeWork(Player p, ComplexMachine maq){
        Players.get(p).Viewers.remove(p);
        Players.remove(p);
    }
    
    public void closeAll(ComplexMachine maq){
        for(Player p:Players.keySet()) close(p, maq);
    }
    
    public void close(Player p, ComplexMachine maq){
        p.closeInventory();
        remove(p, maq);
    }
    
    public void remove(Player p, ComplexMachine maq){
        Players.get(p).Viewers.remove(p);
        Players.remove(p);
    }
    
    public void openWrench(Player p, ComplexMachine maq){
        open(p, maq, 0);
    }
    
    public void open(Player p, ComplexMachine maq){
        open(p, maq, 1);
    }
    
    public void open(Player p, ComplexMachine maq, int page){
        Page Page = Paginas.get(page);
        Listenner.Players2.add(p);
        p.openInventory(Page.Inventory);
        Players.put(p, Page);
        Listenner.Players.put(p, maq);
    }
    
    public void nextPage(Player p){
        Page page = Players.getOrDefault(p, null);
        Validate.notNull(page, "O player"+p.getName()+" não está conectado à esse inventário!");
        page.Viewers.remove(p);
        page = Paginas.get(Paginas.indexOf(page)+1);
        page.Viewers.add(p);
        Listenner.Players2.add(p);
        p.openInventory(page.Inventory);
        Players.put(p, page);
    }
    
    public void lastPage(Player p){
        Page page = Players.getOrDefault(p, null);
        Validate.notNull(page, "O player"+p.getName()+" não está conectado à esse inventário!");
        page.Viewers.remove(p);
        page = Paginas.get(Paginas.indexOf(page)-1);
        page.Viewers.add(p);
        Listenner.Players2.add(p);
        p.openInventory(page.Inventory);
        Players.put(p, page);
    }
    
}
