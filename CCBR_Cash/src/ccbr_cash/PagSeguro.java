package ccbr_cash;

import br.com.uol.pagseguro.domain.AccountCredentials;
import br.com.uol.pagseguro.domain.Transaction;
import br.com.uol.pagseguro.service.TransactionSearchService;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import ccbr_cash.CashEvent.*;
import org.bukkit.entity.Player;

public class PagSeguro extends Thread {
  
    public String p1, p2 = "§4[ERROR]§f: ";
    private Main plugin;
    private String transactionCode = "";
    Transaction transaction = null;
    CommandSender sender = null;
  
    public PagSeguro(Main plugin, String transactionCode2, CommandSender cmds) {
        this.plugin = plugin;
        this.transactionCode = transactionCode2.toUpperCase();
        this.sender = cmds;
    }
  
    public void run(){
        p1 = "§2§l[CCBR_Cash]: ";
        try{
            this.transaction = TransactionSearchService.searchByCode(
                    new AccountCredentials(this.plugin.api.Config.getString("PagSeguro.email"),
                    this.plugin.api.Config.getString("PagSeguro.token")),
                    this.transactionCode);
        }catch (Exception e){
            this.sender.sendMessage(p1+p2+"Codigo nao encontrado!");
            this.plugin.api.using_ps.remove(this.transactionCode);
            return;
        }
        if(this.transaction == null){
            this.sender.sendMessage(p1+p2+"Codigo nao encontrado!");
            this.plugin.api.using_ps.remove(this.transactionCode);
            return;
        }
        if((this.transaction.getStatus().getValue() != 4) && (this.transaction.getStatus().getValue() != 3)){
            this.sender.sendMessage(p1+p2+"Esse codigo nao foi pago!");
            this.plugin.api.using_ps.remove(this.transactionCode);
            return;
        }
        List<String> itens = new ArrayList();
        String id = "";
        for(Object item : this.transaction.getItems()){
            id = item.toString().split("id: ")[1].split(",")[0];
            if(id.split(":")[0].equalsIgnoreCase("cash")){
                if(this.plugin.api.ids.containsKey(id.split(":")[1])){
                    itens.add(item.toString());
                    id = id.split(":")[1];
                }
            }
        }
        if((itens.size() == 0) || (itens == null)){
            this.sender.sendMessage(p1+p2+"Nenhum pagamento de CASH foi encontrado nesse codigo!");
            this.plugin.api.using_ps.remove(this.transactionCode);
            return;
        }
        boolean pedido_valido = true;
        for(String item2 : itens){
            boolean achou = false;
            achou = this.plugin.api.ids.containsKey(item2.split("id: cash:")[1].split(",")[0]);
            if(!achou){
                pedido_valido = false;
                break;
            }else break;
        }
        if(!pedido_valido){
            this.sender.sendMessage(p1+p2+"Ocorreu um erro ao tentar validar seu pedido!");
            this.plugin.api.using_ps.remove(this.transactionCode);
            return;
        }
        this.plugin.api.pagseguro.set(this.transactionCode + ".usedBy", this.sender.getName());
        this.plugin.api.pagseguro.set(this.transactionCode + ".date", this.plugin.api.getData());
        this.plugin.api.pagseguro.saveConfig();
        this.plugin.api.using_ps.remove(this.transactionCode);
        int cash = this.plugin.api.ids.get(id);
        this.plugin.getServer().getPluginManager().callEvent(new CashEvent(cash, (Player)sender, Origem.BOUGHT));
//        PreparedStatement addlog;
//        if((!this.plugin.flatfile) && (this.plugin.mysql_pagseguro)) {
//            try{
//                Connection con = DriverManager.getConnection(this.plugin.mysql_url, this.plugin.mysql_user, this.plugin.mysql_pass);
//                addlog = con.prepareStatement("INSERT INTO `vipzero_pagseguro` (`key`,`nome`,`data`) VALUES ('" + this.transactionCode + "','" + this.sender.getName() + "','" + fmt.format(now.getTime()) + "');");
//                addlog.execute();
//                addlog.close();
//                con.close();
//            }catch (SQLException e){
//                e.printStackTrace();
//            }
//        }
    }
}

