package mc.craig.software.regeneration;

import com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mc.craig.software.regeneration.commands.RegenAmountCommand;
import mc.craig.software.regeneration.items.ItemUtil;
import mc.craig.software.regeneration.skin.APIManager;
import mc.craig.software.regeneration.skin.SkinManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

public class Regeneration extends JavaPlugin implements Listener {


    private static Regeneration INSTANCE;

    public static final SkinManager skinManager = new SkinManager();
    public static APIManager API_MANAGER;


    public static Plugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getLogger().info("HELLO");
        INSTANCE = this;
        API_MANAGER = new APIManager(INSTANCE, "https://mc.craig.software/api/skin/random-skins", skinManager);
        API_MANAGER.fetchAndPopulateSkinsAsync();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("regen").setExecutor(new RegenAmountCommand(this));


        registerCustomRecipe();

    }

    private void registerCustomRecipe() {
        ShapedRecipe customRecipe = new ShapedRecipe(ItemUtil.createFobWatch());
        customRecipe.shape("QIG", "SES", "IGI");
        customRecipe.setIngredient('G', Material.GHAST_TEAR);
        customRecipe.setIngredient('I', Material.IRON_INGOT);
        customRecipe.setIngredient('E', Material.DIAMOND);
        customRecipe.setIngredient('S', Material.SPIDER_EYE);
        customRecipe.setIngredient('Q', Material.BLAZE_ROD);

        customRecipe.setGroup("regen");
        getServer().addRecipe(customRecipe);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent moveEvent){
        if(!RegenerationManager.isRegenerating(moveEvent.getPlayer())) return;
        if(moveEvent.getTo().x() != moveEvent.getFrom().x()){
            moveEvent.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHurt(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(RegenerationManager.isRegenerating(player)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        @NotNull Entity damager = event.getDamager();
        @NotNull Entity attacked = event.getEntity();
        if(attacked instanceof Player && damager instanceof LivingEntity){
            Player player = (Player) attacked;
            LivingEntity attacker = (LivingEntity) damager;
            if(RegenerationManager.isRegenerating(player)){
                attacker.damage(event.getFinalDamage());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));

        // Initialize custom player data
        player.setMetadata("isRegenerating", new FixedMetadataValue(this, false));
        player.setMetadata("regenerationsLeft", new FixedMetadataValue(this, 12));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.BLAZE_ROD && item.hasItemMeta() && item.getItemMeta().getCustomModelData() == 666) {
            item.subtract();
            RegenerationManager.setRegenerationsLeft(player, 12);
            player.sendMessage("You have now gained 12 Regenerations");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if(!player.hasPermission("regeneration.can_regeneration")) return;

        // Cancel the death event
        event.setCancelled(true);

        if (!RegenerationManager.isRegenerating(player)) {
            RegenerationManager.setRegenerating(player, true);
        }

        RegenerationManager.decrementRegenerationsLeft(player);
        if (RegenerationManager.getRegenerationsLeft(player) <= 0) {
            RegenerationManager.setRegenerating(player, false);
        }

        // Emit regeneration particles for 20 seconds
        final int duration = 20 * 20; // 20 seconds in ticks
        final int particleInterval = 5; // Interval between particles (ticks)


        // Visual Flames
        @NotNull Server server = getServer();
        @NotNull BukkitScheduler schedule = server.getScheduler();


        schedule.runTaskTimer(this, new Runnable() {
            int ticksElapsed = 0;

            @Override
            public void run() {
                ticksElapsed++;
                player.setVisualFire(RegenerationManager.isRegenerating(player));

                if (RegenerationManager.isRegenerating(player)) {
                    @NotNull World level = player.getWorld();
                    @NotNull Collection<LivingEntity> nearbyEntities = level.getNearbyLivingEntities(player.getLocation(), 5);
                    for (LivingEntity targetEntity : nearbyEntities) {
                        if(targetEntity == player) continue;
                        Vector direction = targetEntity.getLocation().getDirection().normalize();
                        Vector knockback = direction.multiply(-2);

                        // Apply the knockback
                        targetEntity.setVelocity(knockback);
                    }
                } else {
                    ticksElapsed = 0;
                }
            }
        }, 0L, 0);


        schedule.runTaskTimer(this, new Runnable() {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed < duration) {
                    int numParticles = 10;
                    double radius = 1.0;
                    double angleIncrement = 2 * Math.PI / numParticles;

                    for (int i = 0; i < numParticles; i++) {
                        double angle = i * angleIncrement;
                        double xOffset = radius * Math.cos(angle);
                        double zOffset = radius * Math.sin(angle);
                        @NotNull World world = player.getWorld();
                        world.spawnParticle(Particle.HEART, player.getLocation().add(xOffset, 1.0, zOffset), 1, 0, 0, 0, 0);
                        world.spawnParticle(Particle.SMALL_FLAME, player.getLocation().add(xOffset /2 , 1.5, zOffset/2), 1, 0, 0, 0, 0);
                        world.spawnParticle(Particle.FLAME, player.getLocation().add(xOffset , 0, zOffset), 1, 0, 0, 0, 0);
                    }



                    if (ticksElapsed == (duration / 2)) {
                        PlayerProfile playerProfile = player.getPlayerProfile();
                        @NotNull PlayerTextures textures = playerProfile.getTextures();

                        try {
                            textures.setSkin(new URL(skinManager.getRandomSkin().getLink()), PlayerTextures.SkinModel.SLIM);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                        playerProfile.setTextures(textures);
                        player.setPlayerProfile(playerProfile);
                    }

                    ticksElapsed += particleInterval;
                } else {
                    RegenerationManager.setRegenerating(player, false);
                }
            }
        }, 0L, particleInterval);
    }
}
