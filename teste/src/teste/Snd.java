package teste;

import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class Snd implements CommandSender{

    @Override
    public void sendMessage(String string) {}

    @Override
    public void sendMessage(String[] strings) {}

    @Override
    public Server getServer() {return Bukkit.getServer();}

    @Override
    public String getName() {return "AnonymousSender";}

    @Override
    public boolean isPermissionSet(String string) {return true;}

    @Override
    public boolean isPermissionSet(Permission prmsn) {return true;}

    @Override
    public boolean hasPermission(String string) {return true;}

    @Override
    public boolean hasPermission(Permission prmsn) {return true;}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln) {return new PermissionAttachment(plugin, this);}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {return new PermissionAttachment(plugin, this);}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String string, boolean bln, int i) {return new PermissionAttachment(plugin, this);}

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i) {return new PermissionAttachment(plugin, this);}

    @Override
    public void removeAttachment(PermissionAttachment pa) {}

    @Override
    public void recalculatePermissions() {}

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {return new TreeSet<>();}

    @Override
    public boolean isOp() {return true;}

    @Override
    public void setOp(boolean bln) {}
    
}
