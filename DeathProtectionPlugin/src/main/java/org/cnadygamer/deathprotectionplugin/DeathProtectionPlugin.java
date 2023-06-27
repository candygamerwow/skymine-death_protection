package org.cnadygamer.deathprotectionplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class DeathProtectionPlugin extends JavaPlugin {
    private File configFile;
    private FileConfiguration config;
    private Map<UUID, UUID> killers;
    private Map<UUID, Integer> deathCounts;
    private Map<UUID, Long> firstKillTimes;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Jerusalem"));
    @Override
    public void onEnable() {
        killers = new HashMap<>();
        deathCounts = new HashMap<>();
        firstKillTimes = new HashMap<>();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        configFile = new File(getDataFolder(), "data.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        loadData();

        getServer().getPluginManager().registerEvents(new KillListener(killers, deathCounts, firstKillTimes), this);
        getServer().getPluginManager().registerEvents(new DamageListener(killers, deathCounts, firstKillTimes), this);

        // Save any existing data to the file
        saveData();
    }


    @Override
    public void onDisable() {
        saveData();
    }

    private void loadData() {
        if (config.isList("data")) {
            List<String> dataList = config.getStringList("data");

            for (String data : dataList) {
                String[] parts = data.split(",");

                UUID playerUuid = UUID.fromString(parts[0]);
                UUID killerUuid = UUID.fromString(parts[1]);
                int deathCount = Integer.parseInt(parts[2]);
                ZonedDateTime firstKillTime = ZonedDateTime.parse(parts[3], formatter);

                killers.put(playerUuid, killerUuid);
                deathCounts.put(playerUuid, deathCount);
                firstKillTimes.put(playerUuid, firstKillTime.toInstant().toEpochMilli());
            }
        }
    }

    private void saveData() {
        List<String> dataList = new ArrayList<>();

        for (UUID playerUuid : killers.keySet()) {
            UUID killerUuid = killers.get(playerUuid);
            int deathCount = deathCounts.getOrDefault(playerUuid, 0);
            long firstKillTime = firstKillTimes.getOrDefault(playerUuid, 0L);

            String firstKillTimeStr = formatter.format(Instant.ofEpochMilli(firstKillTime));
            dataList.add(playerUuid.toString() + "," + killerUuid.toString() + "," + deathCount + "," + firstKillTimeStr);
        }

        config.set("data", dataList);

        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}