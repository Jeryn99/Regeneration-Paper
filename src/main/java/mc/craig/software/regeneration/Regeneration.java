package mc.craig.software.regeneration;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import mc.craig.software.regeneration.commands.RegenAmountCommand;
import mc.craig.software.regeneration.skin.APIManager;
import mc.craig.software.regeneration.skin.SkinManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
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
        System.out.println("HELLO");
        INSTANCE = this;
        API_MANAGER = new APIManager(INSTANCE, "https://mc.craig.software/api/skin/random-skins", skinManager);
        API_MANAGER.fetchAndPopulateSkinsAsync();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("regen").setExecutor(new RegenAmountCommand(this));

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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(Component.text("Hello, " + player.getName() + "!"));

        // Initialize custom player data
        player.setMetadata("isRegenerating", new FixedMetadataValue(this, false));
        player.setMetadata("regenerationsLeft", new FixedMetadataValue(this, 12));
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

        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            int ticksElapsed = 0;

            @Override
            public void run() {
                if (ticksElapsed < duration) {
                    int numParticles = 10;  // Number of particles in the circle
                    double radius = 1.0;    // Radius of the circle
                    double angleIncrement = 2 * Math.PI / numParticles;

                    for (int i = 0; i < numParticles; i++) {
                        double angle = i * angleIncrement;
                        double xOffset = radius * Math.cos(angle);
                        double zOffset = radius * Math.sin(angle);
                        player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(xOffset, 1.0, zOffset), 1, 0, 0, 0, 0);
                        player.getWorld().spawnParticle(Particle.SMALL_FLAME, player.getLocation().add(xOffset, 1.0, zOffset), 1, 0, 0, 0, 0);
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
