package org.cnadygamer.deathprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.Map;
import java.util.UUID;

import static org.cnadygamer.deathprotectionplugin.TimeUtils.getIsraelTime;
public class KillListener implements Listener {

    // Stores the UUID of the killer for each player
    private final Map<UUID, UUID> killers;
    // Stores the death count for each player
    private final Map<UUID, Integer> deathCounts;
    // Stores the time of the first kill for each player in milliseconds
    private final Map<UUID, Long> firstKillTimes;

    public KillListener(Map<UUID, UUID> killers, Map<UUID, Integer> deathCounts, Map<UUID, Long> firstKillTimes) {
        this.killers = killers;
        this.deathCounts = deathCounts;
        this.firstKillTimes = firstKillTimes;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            Player victim = event.getEntity();

            UUID killerUuid = killer.getUniqueId();
            UUID victimUuid = victim.getUniqueId();

            if (killers.get(victimUuid) != null && killers.get(victimUuid).equals(killerUuid)) {
                deathCounts.put(victimUuid, deathCounts.getOrDefault(victimUuid, 0) + 1);
            } else {
                killers.put(victimUuid, killerUuid);
                deathCounts.put(victimUuid, 1);
                firstKillTimes.put(victimUuid, System.currentTimeMillis());
            }

            if (deathCounts.get(victimUuid) == 4) {
                Long firstKillTime = firstKillTimes.get(victimUuid);
                String expirationTime = getIsraelTime(firstKillTime + 15 * 60 * 1000); // add 15 minutes to the first kill time
                killer.sendMessage(ChatColor.RED + "You have reached the maximum kills of this player, you can attack again after " + expirationTime);
                victim.sendMessage(ChatColor.RED + "You have been killed by the same player for the fourth time. You can't hit your killer until " + expirationTime);
            }
        }
    }
}