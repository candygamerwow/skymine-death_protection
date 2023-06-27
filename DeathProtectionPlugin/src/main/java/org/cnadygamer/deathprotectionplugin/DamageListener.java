package org.cnadygamer.deathprotectionplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Map;
import java.util.UUID;

import static org.cnadygamer.deathprotectionplugin.TimeUtils.getIsraelTime;

public class DamageListener implements Listener {

    private final Map<UUID, UUID> killers;
    private final Map<UUID, Integer> deathCounts;
    private final Map<UUID, Long> firstKillTimes;

    public DamageListener(Map<UUID, UUID> killers, Map<UUID, Integer> deathCounts, Map<UUID, Long> firstKillTimes) {
        this.killers = killers;
        this.deathCounts = deathCounts;
        this.firstKillTimes = firstKillTimes;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player defender = (Player) event.getEntity();

            if (isProtected(defender.getUniqueId(), attacker.getUniqueId())) {
                event.setCancelled(true);
                Long firstKillTime = firstKillTimes.get(defender.getUniqueId());
                String expirationTime = getIsraelTime(firstKillTime + 15 * 60 * 1000); // add 15 minutes to the first kill time
                attacker.sendMessage(ChatColor.RED + "הגעת למקסימום כמות הריגות של שחקן זה נסה שוב מאוחר יותר בעוד " + expirationTime);
            }

            if (isProtected(attacker.getUniqueId(), defender.getUniqueId())) {
                event.setCancelled(true);
                Long firstKillTime = firstKillTimes.get(attacker.getUniqueId());
                String expirationTime = getIsraelTime(firstKillTime + 15 * 60 * 1000); // add 15 minutes to the first kill time
                attacker.sendMessage(ChatColor.RED + "אתה לא יכול להרוג את מי שהרג אותך עד שיגמר הזמן הגנה שלך ממנו " + expirationTime);
            }
        }
    }

    private boolean isProtected(UUID defenderUuid, UUID attackerUuid) {
        UUID killerUuid = killers.get(defenderUuid);
        if (killerUuid != null && killerUuid.equals(attackerUuid)) {
            Integer deathCount = deathCounts.get(defenderUuid);
            Long firstKillTime = firstKillTimes.get(defenderUuid);
            return deathCount != null && deathCount >= 3 && firstKillTime != null && (System.currentTimeMillis() - firstKillTime <= 15 * 60 * 1000);
        }
        return false;
    }
}