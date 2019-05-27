package itemapi;

import itemapi.utils.IsBuilder;
import itemapi.utils.ItemConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    ItemConstructor Icons;
    
    @Override
    public void onEnable(){
        Icons = new ItemConstructor();
    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        if(!(snd instanceof Player)){
            if(args.length>3){
                if(args[0].equalsIgnoreCase("give")){
                    try{
                        Player p1 = Bukkit.getPlayer(args[1]);
                        int m = Integer.parseInt(args[2]);
                        StringBuilder sb = new StringBuilder(args[3]);
                        for(int i=4;i<args.length;i++) sb.append(' ').append(args[i]);
                        ItemStack is = Icons.getItem(sb.toString());
                        is.setAmount(m);
                        p1.getInventory().addItem(is);
                        return true;
                    }catch(Exception ex){}
                }
            }
            return true;
        }
        Player p = (Player)snd;
        String cmd = Cmd.getName();
        int n = args.length;
        if(cmd.equals("rename")){
            if(!p.hasPermission("ItemApi.rename")){
                p.sendMessage("§cVocê não tem permissão para renomear items!");
                return true;
            }else{
                if(n>0){
                    IsBuilder ib = new IsBuilder(p.getItemInHand());
                    StringBuilder sb = new StringBuilder(args[0]);
                    for(int i=1;i<args.length;i++) sb.append(' ').append(args[i]);
                    if(!ib.isAir()) p.setItemInHand(ib.setName(sb.toString().replace('&', '§')).getItemStack());
                    else p.sendMessage("§cVocê não pode renomear o ar!");
                }else{
                    IsBuilder ib = new IsBuilder(p.getItemInHand());
                    p.setItemInHand(ib.setName("").getItemStack());
                }
            }
        }else if(cmd.equals("lore")){
            if(!p.hasPermission("ItemApi.lore")){
                p.sendMessage("§cVocê não tem permissão para editar as lores!");
                return true;
            }else{
                if(n>0){
                    ItemStack is = p.getItemInHand();
                    IsBuilder ib = new IsBuilder(is);
                    if(ib.isAir()) p.sendMessage("§cVocê não pode costumizar o ar!");
                    if(args[0].equalsIgnoreCase("add")){
                        if(n>1){
                            try{
                                int linha = Integer.parseInt(args[1]);
                                if(n>2){
                                    StringBuilder sb = new StringBuilder(args[2]);
                                    for(int i=3;i<args.length;i++) sb.append(' ').append(args[i]);
                                    p.setItemInHand(ib.addLore(linha, sb.toString().replace('&', '§')).getItemStack());
                                }else p.sendMessage("§f/lore add <linha> <descrição>");
                            }catch(Exception e){
                                StringBuilder sb = new StringBuilder(args[1]);
                                for(int i=2;i<args.length;i++) sb.append(' ').append(args[i]);
                                p.setItemInHand(ib.addLore(sb.toString().replace('&', '§')).getItemStack());
                            }
                        }else p.sendMessage("§f/lore add <descrição> ou /lore add <linha> <descrição>");
                    }else if(args[0].equalsIgnoreCase("rem") || args[0].equalsIgnoreCase("remove")){
                        if(n>1){
                            try{
                                int linha = Integer.parseInt(args[1]);
                                p.setItemInHand(ib.remLore(linha).getItemStack());
                            }catch(Exception e){
                                p.sendMessage("§f/lore rem <linha>");
                            }
                        }else p.sendMessage("§f/lore rem <linha>");;
                    }else if(args[0].equalsIgnoreCase("set")){
                        if(n>2){
                            try{
                                int linha = Integer.parseInt(args[1]);
                                StringBuilder sb = new StringBuilder(args[2]);
                                for(int i=3;i<args.length;i++) sb.append(' ').append(args[i]);
                                p.setItemInHand(ib.setLore(linha, sb.toString().replace('&', '§')).getItemStack());
                            }catch(Exception e){
                                p.sendMessage("§f/lore set <linha> <descrição>");
                            }
                        }else p.sendMessage("§f/lore set <linha> <descrição>");
                    }else{
                        p.sendMessage("§f/lore add <descrição> ou /lore add <linha> <descrição>");
                        p.sendMessage("§f/lore rem <linha>");
                        p.sendMessage("§f/lore set <linha> <descrição>");
                    }
                }else{
                    p.sendMessage("§f/lore add <descrição> ou /lore add <linha> <descrição>");
                    p.sendMessage("§f/lore rem <linha>");
                    p.sendMessage("§f/lore set <linha> <descrição>");
                }
            }
        }else if(cmd.equals("itemapi")){
            if(!p.hasPermission("ItemApi.adm")) return false;
            if(args.length>3){
                if(args[0].equalsIgnoreCase("give")){
                    try{
                        Player p1 = Bukkit.getPlayer(args[1]);
                        int m = Integer.parseInt(args[2]);
                        StringBuilder sb = new StringBuilder(args[3]);
                        for(int i=4;i<args.length;i++) sb.append(' ').append(args[i]);
                        ItemStack is = Icons.getItem(sb.toString());
                        is.setAmount(m);
                        p1.getInventory().addItem(is);
                        return true;
                    }catch(Exception ex){}
                }
            }
            ItemStack is = p.getItemInHand();
            if(is==null || is.getType().toString().contains("AIR")) return false;
            System.out.println("§e[ItemAPI] §f"+p.getName()+": "+Icons.getString(is));
        }
        return true;
    }
    
}
