package com.deanveloper.playtimeplus.hooks;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.util.Utils;
import com.earth2me.essentials.Essentials;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

/**
 * @author Dean B
 */
public class EssentialsHook {
    private Essentials plugin;

    public EssentialsHook() {
        Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Essentials");
        if (plug != null) {
            plugin = (Essentials) plug;
            registerAfkHook();
        } else {
            Bukkit.getLogger().info("Essentials is not installed, we cannot account for if the player is AFK");
        }
    }

    /**
     * If the player is afk
     *
     * @param p The player to check
     * @return If they are AFK, assumes not AFK if essentials is not installed
     */
    public boolean isAfk(Player p) {
        return plugin != null && plugin.getUser(p).isAfk();
    }

    /**
     * If the player is afk
     *
     * @param p The player to check
     * @return If they are AFK, assumes not AFK if essentials is not installed
     */
    public boolean isAfk(UUID p) {
        return plugin != null && plugin.getUser(p).isAfk();
    }

    /**
     * The player's full name
     *
     * @param name The player to get the full name of
     * @return The player's full name
     */
    public String fullName(String name) {
        String nickName = name;
        if (plugin != null) {
            Player p = Bukkit.getPlayerExact(name);
            if (!plugin.getPermissionsHandler().getPrefix(p).isEmpty()) {
                return Utils.getPrefix(p.getName()) + plugin.getUser(p).getNick(true);
            } else {
                nickName = plugin.getUser(name).getNick(true);
            }
        }
        return Utils.getPrefix(name) + nickName;
    }

    private void registerAfkHook() {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onAfk(AfkStatusChangeEvent e) {
                Player p = e.getAffected().getBase();
                if (e.getValue()) { // if turning afk
                    PlayTimePlus.getStorage().get(p.getUniqueId()).setOnline(false);
                    PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to afk");
                } else {
                    PlayTimePlus.getStorage().get(p.getUniqueId()).setOnline(true);
                    PlayTimePlus.debug("Setting player " + e.getAffected().getName() + " to non-afk");
                }
            }
        }, PlayTimePlus.getInstance());
    }
}
