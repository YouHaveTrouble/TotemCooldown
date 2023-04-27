package me.youhavetrouble.totemcooldown;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class TotemCooldown extends JavaPlugin implements Listener {

    private final NamespacedKey key = new NamespacedKey(this, "totem-last-used");
    private int cooldown = 100;
    private boolean resetOnDeath = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        cooldown = getConfig().getInt("totem-cooldown-ticks", 100);
        resetOnDeath = getConfig().getBoolean("reset-on-death", true);
        getLogger().info("Totem of undying cooldown is set to " + cooldown + " ticks ("+ cooldown / 20 + " seconds)");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.hasCooldown(Material.TOTEM_OF_UNDYING)) return;
            player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, player.getCooldown(Material.TOTEM_OF_UNDYING));
        });
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTotemUse(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (player.hasCooldown(Material.TOTEM_OF_UNDYING)) {
            event.setCancelled(true);
            return;
        }

        player.setCooldown(Material.TOTEM_OF_UNDYING, cooldown);

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!resetOnDeath) return;
        Player player = event.getEntity();
        player.getPersistentDataContainer().remove(key);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!player.hasCooldown(Material.TOTEM_OF_UNDYING)) return;
        player.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, player.getCooldown(Material.TOTEM_OF_UNDYING));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Integer cooldown = player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if (cooldown == null) return;

        player.setCooldown(Material.TOTEM_OF_UNDYING, cooldown);
        player.getPersistentDataContainer().remove(key);

    }

}
