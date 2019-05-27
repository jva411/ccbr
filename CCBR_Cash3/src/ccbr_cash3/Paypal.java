package ccbr_cash3;

import ccbr_cash3.CashEvent.*;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import paypalnvp.profile.BaseProfile;
import paypalnvp.profile.Profile;
import paypalnvp.request.GetTransactionDetails;

public class Paypal extends Thread {
    
    public String p;
    
	private Main plugin = null;
	private String transactID = "";
	private CommandSender sender = null;
        
	public Paypal(Main pl,String transactID2,CommandSender cmdss) {
		plugin=pl;
		transactID=transactID2.toUpperCase();
		sender=cmdss;
		
	}
	
	public void run() {
            p = "§2§l[CCBR_Cash]: §4[ERROR]§f: ";
		paypalnvp.core.PayPal pp = null;
		GetTransactionDetails tr = null;
		try {
			Profile user = new BaseProfile.Builder(plugin.api.Config.getString("PayPal.username"),plugin.api.Config.getString("PayPal.password")).signature(plugin.api.Config.getString("PayPal.signature")).build();
			pp = new paypalnvp.core.PayPal(user,paypalnvp.core.PayPal.Environment.LIVE);
			tr = new GetTransactionDetails(transactID);
		}
		catch(Exception e) {
                        sender.sendMessage(p+"Codigo nao encontrado!");
                        plugin.api.using_pp.remove(transactID);
                        return;
		}
		if (pp==null||tr==null) {
                        sender.sendMessage(p+"Codigo nao encontrado!");
                        plugin.api.using_pp.remove(transactID);
                        return;
                }
		
		pp.setResponse(tr);
		
		if(!tr.getNVPResponse().containsKey("PAYMENTSTATUS")) {
                        sender.sendMessage(p+"Codigo nao encontrado!");
                        plugin.api.using_pp.remove(transactID);
                        return;
                }
		if(!tr.getNVPResponse().get("PAYMENTSTATUS").equals("Completed")) {
                        sender.sendMessage(p+"Este codigo nao foi pago!");
			plugin.api.using_pp.remove(transactID);
			return;
		}
		
                String id = "";
		List<String> itens = new ArrayList<String>();
		for(String key : tr.getNVPResponse().keySet()){
                    if(key.startsWith("L_NAME")) {
                        System.out.println(tr.getNVPResponse().get(key));
                        if(tr.getNVPResponse().get(key).contains("(cash:")){
                            id = tr.getNVPResponse().get(key).split("\\(cash:")[1].split("\\)")[0];
                            itens.add(id);
                        }
                    }
                }
		if(itens.size()==0||itens==null) {
                        sender.sendMessage(p+"Nenhum pagamento de CASH foi encontrado nesse codigo!");
			plugin.api.using_pp.remove(transactID);
			return;
		}
		boolean pedido_valido = true;
		for(String item2 : itens) {
                    boolean achou = false;
                    System.out.println("Item2 = "+item2);
                    achou = this.plugin.api.ids.containsKey(item2);
                    if(!achou) {
                        pedido_valido=false;
                        break;
                    }
		}
		if(!pedido_valido) {
                    sender.sendMessage(p+"Um erro ocorreu ao validar esse codigo!");
                    return;
		}
		plugin.api.paypal.set(transactID+".usedBy", sender.getName());
		plugin.api.paypal.set(transactID+".date", plugin.api.getData());
		plugin.api.using_pp.remove(transactID);
                int cash = this.plugin.api.ids.get(id);
                plugin.getServer().getPluginManager().callEvent(new CashEvent(cash, (Player)sender, Origem.BOUGHT));
	}
}
