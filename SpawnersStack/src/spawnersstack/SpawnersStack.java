package spawnersstack;

import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Trident;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnersStack extends JavaPlugin implements Listener{

    public Thread Thread;
    public static SpawnersStack SpawnersStack;
    public HashMap<Entity, Map.Entry<Player, Entity>> EntityDiedByPlayerWithEntity;
    public HashMap<Arrow, Integer> Arrows;
    
    @Override
    public void onEnable() {
        restartThread();
        Bukkit.getPluginManager().registerEvents(this, this);
        SpawnersStack = this;
        Arrows = new HashMap<>();
        EntityDiedByPlayerWithEntity = new HashMap<>();
    }
    
    public void restartThread(){
        Thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    while(true){
                        Thread.sleep(9*60000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 1min...");
                        }
                        Thread.sleep(30000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 30s...");
                        }
                        Thread.sleep(20000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 10s...");
                        }
                        Thread.sleep(5000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 5s...");
                        }
                        Thread.sleep(1000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 4s...");
                        }
                        Thread.sleep(1000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 3s...");
                        }
                        Thread.sleep(1000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 2s...");
                        }
                        Thread.sleep(1000);
                        for(Player p:Bukkit.getOnlinePlayers()){
                            p.sendMessage("§7Limpando tos os itens em 1s...");
                        }
                        Thread.sleep(1000);
                        for(Player p:getServer().getOnlinePlayers()){
                            for(Entity e:p.getNearbyEntities(100, 100, 100)){
                                if(e.getType()==EntityType.ARROW) e.remove();
                                else if(e instanceof Item) e.remove();
                                else if(e.getType()==EntityType.EXPERIENCE_ORB) e.remove();
                            }
                        }                  
                    }
                }catch(Exception e){
                    restartThread();
                }
            }
        });
        Thread.start();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e) {
        if(!e.isCancelled()){
            try{
                if(e.getBlockPlaced().getType()==Material.SPAWNER){
                    if(e.getBlockAgainst().getType()==Material.SPAWNER){
                        String a = e.getBlockReplacedState().getType().toString();
                        if(a.contains("GRASS") || a.contains("BUSH") || a.contains("SNOW") || a.contains("FERN")) return;
                        CreatureSpawner cs = (CreatureSpawner)e.getBlockPlaced().getState(), cs2 = (CreatureSpawner)e.getBlockAgainst().getState();
                        if(cs.getSpawnedType()==cs2.getSpawnedType()){
                            ArmorStand as = null;
                            for(Entity en:cs2.getLocation().add(0.5, -1, 0.5).getNearbyEntities(0.1, 0.1, 0.1)){
                                if(en instanceof ArmorStand){
                                    as = (ArmorStand)en;
                                    break;
                                }
                            }
                            int n = 2;
                            if(as==null){
                                as = (ArmorStand)cs2.getWorld().spawnEntity(cs2.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);
                                as.setVisible(false);
                                as.setInvulnerable(true);
                                as.setGravity(false);
                                as.setCanPickupItems(false);
                                as.setCollidable(false);
                                as.setCustomNameVisible(true);
                                as.setDisabledSlots(EquipmentSlot.values());
                            }else{
                                n = Integer.parseInt(as.getCustomName().replace("§a", "").replace("x", ""));
                                n++;
                            }
                            if(n>64){
                                e.setCancelled(true);
                                return;
                            }
                            as.setCustomName("§a"+n+"x");
                            e.getBlockPlaced().setType(e.getBlockReplacedState().getType());
                            setSpawnerTag(n, cs2);
                        }
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
    
    private void setSpawnerTag(int n, CreatureSpawner cs){
        cs.setMaxNearbyEntities(100);
        cs.setDelay(20);
        if(n<17){
            cs.setMinSpawnDelay(200);
            cs.setMaxSpawnDelay(800);
        }else if(n<33){
            cs.setMinSpawnDelay(160);
            cs.setMaxSpawnDelay(720);
        }else if(n<49){
            cs.setMinSpawnDelay(120);
            cs.setMaxSpawnDelay(640);
        }else{
            cs.setMinSpawnDelay(100);
            cs.setMaxSpawnDelay(600);
        }
        cs.setSpawnCount(n*6);
        cs.setSpawnRange(4);
        cs.setRequiredPlayerRange(80);
        cs.update();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if(!e.isCancelled()){
            if(e.getBlock().getType()==Material.SPAWNER){
                ArmorStand as = null;
                for(Entity en:e.getBlock().getLocation().add(0.5, -1, 0.5).getNearbyEntities(0.1, 0.1, 0.1)){
                    if(en instanceof ArmorStand){
                        as = (ArmorStand)en;
                        break;
                    }
                }
                if(as!=null){
                    int n = Integer.parseInt(as.getCustomName().replace("§a", "").replace("x", ""));
                    n--;
                    if(n<2) as.remove();
                    else as.setCustomName("§a"+n+"x");
                    final CreatureSpawner block = (CreatureSpawner)e.getBlock().getState();
                    final Location loc = new Location(block.getWorld(), block.getLocation().getBlockX(),
                            block.getLocation().getBlockY(),
                            block.getLocation().getBlockZ());
                    final int N = n;
                    getServer().getScheduler().runTaskLater(this, new Runnable(){
                        
                        final int N2 = N;
                        final Location Loc = loc;
                        final CreatureSpawner Cs = block;
                        
                        @Override
                        public void run(){
                            
                            Loc.getBlock().setType(Material.SPAWNER);
                            CreatureSpawner cs2 = (CreatureSpawner)Loc.getBlock().getState();
                            setSpawnerTag(N, cs2);
                            cs2.setSpawnedType(Cs.getSpawnedType());
                            cs2.update();
                            
                        }
                    }, 1);
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMonsterSpawn(CreatureSpawnEvent e){
        if(e.getEntity() instanceof Wither || e.getEntity() instanceof EnderDragon) return;
        if((e.getEntity() instanceof Monster && !(e.getEntity() instanceof Player)) || e.getEntity() instanceof Phantom || e.getEntity() instanceof Slime){
            if(!e.isCancelled()){
                int n = 0;
                Entity En = null;
                if(e.getEntity() instanceof Slime){
                    Slime s = (Slime)e.getEntity();
                    for(Entity en:e.getEntity().getNearbyEntities(7, 4, 7)){
                        try{
                            if(en.getType()==s.getType()){
                                if(((Slime)en).getSize()==s.getSize()){
                                    int n1 = Integer.parseInt(en.getCustomName().replace("§a", "").replace("x", ""));
                                    if(n1 > n && n1 < 129) {
                                        En = en;
                                        n = n1;
                                    }
                                }
                            }
                        }catch(Exception ex){}
                    }
                }else{
                    for(Entity en:e.getEntity().getNearbyEntities(7, 4, 7)){
                        try{
                            if(en.getType()==e.getEntityType()){
                                int n1 = Integer.parseInt(en.getCustomName().replace("§a", "").replace("x", ""));
                                if(n1 > n && n1 < 129) {
                                    En = en;
                                    n = n1;
                                }
                            }
                        }catch(Exception ex){}
                    }
                }
                if(En==null) En = e.getEntity();
                n++;
                if(n<129 && n>1) e.getEntity().remove();
                else En = e.getEntity();
                if(n>128) n = n%128;
                En.setCustomName("§a"+n+"x");
                En.setCustomNameVisible(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureDeath(EntityDeathEvent e){
        try{
            if(!e.isCancelled()){
                Entity en = e.getEntity();
                if(en instanceof Monster || en instanceof Phantom || en instanceof Slime){
                    LivingEntity le = (LivingEntity)en;
                    int n = Integer.parseInt(le.getCustomName().replace("§a", "").replace("x", "")), soma = 2;
                    if(n>0) {
                        int total = 1;
                        if(EntityDiedByPlayerWithEntity.containsKey(en)){
                            Player p = EntityDiedByPlayerWithEntity.get(en).getKey();
                            Entity En = EntityDiedByPlayerWithEntity.get(en).getValue();
                            for(int i=127;i>0;i--){
                                if(p.hasPermission("SpawnersStack.kill."+i)){
                                    total += i;
                                    break;
                                }
                            }
                            if(En instanceof Arrow) soma += (int)(Math.ceil(Arrows.get((Arrow)En)*1.3));
                            else if(p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) soma += (int)(Math.ceil(p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)*1.3));
                            EntityDiedByPlayerWithEntity.remove(en);
                        }else total = n;
                        total = total > n ? n : total;
                        soma *= total;
                        for(ItemStack is:e.getDrops()) {
                            int soma2;
                            EntityEquipment ee = le.getEquipment();
                            Random rd = new Random();
                            if(is.equals(ee.getBoots())){
                                soma2 = 1;
                            }else if(is.equals(ee.getLeggings())){
                                soma2 = 1;
                            }else if(is.equals(ee.getChestplate())){
                                soma2 = 1;
                            }else if(is.equals(ee.getHelmet())){
                                soma2 = 1;
                            }else if(is.equals(ee.getItemInMainHand())){
                                soma2 = 1;
                            }else if(is.equals(ee.getItemInOffHand())){
                                soma2 = 1;
                            }else if(le instanceof Zombie){
                                if(is.getType()==Material.IRON_INGOT){
                                    soma2 = soma-aa(soma-1, (int)(total/(((double)rd.nextInt(800)/6000d)+0.52d)), (int)((double)rd.nextInt(20)/1000d+0.0001d*total));
                                }else soma2 = soma-aa(soma-1, -1, 0);
                            }else{
                                soma2 = aa(soma-1, (int)(soma/(double)(rd.nextInt(6)/10d+0.8d)), (int)(soma/(double)(rd.nextInt(60)/100d+0.8d)));
                            }
                            int soma3 = soma2;
                            ItemStack Is = is.clone();
                            for(int i=0;i<(soma3/64)+1;i++){
                                if(soma2>64) {
                                    Is.setAmount(64);
                                    soma2 -= 64;
                                }else {
                                    Is.setAmount(soma2);
                                    soma2 = 0;
                                }
                                le.getWorld().dropItemNaturally(le.getLocation(), Is).setPickupDelay(20);
                            }
                        }
                        ((ExperienceOrb)le.getWorld().spawn(le.getLocation(), ExperienceOrb.class)).setExperience(e.getDroppedExp()*total);
                        n -= total;
                        le.setCustomName("§a"+n+"x");
                        le.setHealth(le.getMaxHealth());
                        if(le instanceof Slime){
                            Slime s = (Slime)le;
                            int size = s.getSize();
                            if(size!=1){
                                EntityType et;
                                if(s instanceof MagmaCube) et = EntityType.MAGMA_CUBE;
                                else et = EntityType.SLIME;
                                for(int i=0;i<total;i++){
                                    ((Slime)le.getWorld().spawnEntity(le.getLocation(), et)).setSize(size-1);
                                }
                            }
                        }
                        e.setCancelled(true);
                        if(n==0) le.remove();
                    }
                }
            }
        }catch(Exception ex){}
    }
    
    private static int aa(int n, int N, int r){
        if(N>-1) n+= r;
        Random rd = new Random();
        int n1 = (n+1)*n/2, n2 = rd.nextInt(n1+1);
        int n3 = 0;
        for(int n4=0;n4<n;n4++){
            for(int n5=0;n5<n-n4;n5++){
                if(n5+n3==n2) {
                    if(N>-1){
                        if(n4<r) return N + rd.nextInt(3)-1;
                        else return n4-r;
                    }
                    return n4;
                }
            }
            n3 += (n-n4);
        }
        if(N>-1) return n-r;
        return n;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
        if(!e.isCancelled()){
            if(e.getEntity() instanceof Monster || e.getEntity() instanceof Phantom || e.getEntity() instanceof Slime){
                LivingEntity le = (LivingEntity)e.getEntity();
                if(le.getHealth() - e.getDamage() <= 0){
                    if(e.getDamager() instanceof Player){
                        Player p =(Player)e.getDamager();
                        EntityDiedByPlayerWithEntity.put(le, new AbstractMap.SimpleEntry<>(p, p));
                    }else if(e.getDamager() instanceof Arrow){
                        Arrow a = (Arrow)e.getDamager();
                        if(a.getShooter() instanceof Player){
                            Player p = (Player)a.getShooter();
                            EntityDiedByPlayerWithEntity.put(le, new AbstractMap.SimpleEntry<>(p, a));
                        }
                    }
                }
            }
        }
    }
    
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onBowShootEvent(EntityShootBowEvent e){
//        if(e.getEntity() instanceof Player){
//            if(e.getBow().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) Arrows.put((Arrow)e.getArrowItem(), e.getBow().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
//            else Arrows.put((Arrow)e.getProjectile(), 0);
//        }
//    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLaunchEvent(ProjectileLaunchEvent e){
        if(e.getEntity().getShooter() instanceof Player){
            Player p = (Player)e.getEntity().getShooter();
            if(p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) Arrows.put((Arrow)e.getEntity(), p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
            else Arrows.put((Arrow)e.getEntity(), 0);
        }
    }
    
    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e){
        if(e.getPlayer().getItemInHand().getType().toString().contains("NAME")){
            if(e.getPlayer().getItemInHand().getItemMeta().getDisplayName().matches("§a\\d+x")){
                e.setCancelled(true);
                e.getPlayer().setItemInHand(null);
            }
        }
    }
    
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void onMonsterDamaged(EntityDamageEvent e){
//        if(total==0){
//            if(!e.isCancelled()){
//                Entity en = e.getEntity();
//                if(en instanceof Monster || en instanceof Phantom){
//                    try{
//                        Creature c = (Creature)en;
//                        if(c.getHealth()-e.getDamage()<=0){
//                            int n = Integer.parseInt(c.getCustomName().replace("§a", "").replace("x", ""));
//                            if(n>1){
//                                total = 10;
//                                if(e instanceof EntityDamageByEntityEvent){
//                                    EntityDamageByEntityEvent E = (EntityDamageByEntityEvent)e;
//                                    if(E.getDamager() instanceof EntityDiedByPlayerWithEntity){
//                                        EntityDiedByPlayerWithEntity p = (EntityDiedByPlayerWithEntity)E.getDamager();
//                                        for(int i=118;i>0;i--){
//                                            if(p.hasPermission("SpawnersStack.kill."+i)) {
//                                                total += i; 
//                                                break;
//                                            }
//                                        }
//                                    }
//                                }
//                                for(int i=0;i<total;i++){
//                                    Bukkit.getPluginManager().callEvent(new EntityDamageEvent(en, e.getCause(), 1000));
//                                }
//                                total = 0 ;
//                                e.setCancelled(true);
//                                e.setDamage(0);
//                            }
//                        }
//                    }catch(Exception ex){}
//                }
//            }
//        }
//    }
    
    @Override
    public boolean onCommand(CommandSender snd, Command Cmd, String lb, String[] args) {
        String cmd = Cmd.getName();
        if(cmd.equals("spawnersstack")){
            if(snd.hasPermission("spawnersstack.adm")){
//                for(Entity e:p.getLocation().getNearbyEntities(0.5, 0.5, 0.5)) {
//                    if(!(e instanceof EntityDiedByPlayerWithEntity)) e.remove();
//                }
            }
        }
        return true;
    }
    
}
