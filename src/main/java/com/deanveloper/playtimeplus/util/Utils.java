package com.deanveloper.playtimeplus.util;

import com.deanveloper.playtimeplus.storage.PlayerEntry;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Dean B
 */
public class Utils {
    private static BiMap<UUID, String> nameIdMap = HashBiMap.create(new CaseInsensitiveMap<UUID>()).inverse();
    private static Map<String, String> correctCaseMap = new HashMap<>();

    public static UUID getUuid(String name) {
        if (name == null) {
            throw new NullPointerException("Cannot get the UUID of a null username!");
        }
        return nameIdMap.inverse().get(name.toLowerCase());
    }

    public static String getName(UUID id) {
        if (id == null) {
            throw new NullPointerException("Cannot get the name of a null UUID!");
        }
        return correctCaseMap.get(nameIdMap.get(id));
    }

    public static String getNameForce(UUID id) {
        String name = getName(id);
        if (name == null) {

            try {
                String newId = id.toString().replace("-", "");
                String json = getContent("https://sessionserver.mojang.com/session/minecraft/profile/" + newId);
                JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
                name = obj.get("name").getAsString();
            } catch (IOException e) {
                throw new RuntimeException("Problem getting name of " + id, e);
            }

            update(id, name);
        }
        return name;
    }

    public static String correctCase(String name) {
        return correctCaseMap.getOrDefault(name.toLowerCase(), name.toLowerCase());
    }

    public static void update(UUID id, String name) {
        nameIdMap.forcePut(id, name.toLowerCase());
        correctCaseMap.put(name.toLowerCase(), name);
    }

    public static String getPrefix(String name) {
        if (name == null) {
            throw new NullPointerException("Cannot get the prefix of a null name!");
        }
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(name);
        if (team == null) {
            return "";
        } else {
            return team.getPrefix();
        }
    }

    public static String format(Duration dur) {
        StringBuilder sb = new StringBuilder();

        int hours = (int) dur.toHours();
        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours != 1) {
                sb.append('s');
            }
            sb.append(", ");
        }

        //always include minutes, even if 0
        int minutes = (int) dur.toMinutes() % 60;
        sb.append(minutes).append(" minute");
        if (minutes != 1) {
            sb.append('s');
        }

        return sb.toString();
    }

    public static Set<PlayerEntry.TimeEntry> cloneElements(Collection<PlayerEntry.TimeEntry> col) {
        return col.stream()
                .map(PlayerEntry.TimeEntry::clone)
                .collect(Collectors.toSet());
    }

    private static String getContent(String web) throws IOException {
        try {
            URL url = new URL(web);
            URLConnection connection = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String input;

            while ((input = in.readLine()) != null) {
                response.append(input);
            }

            in.close();

            return response.toString();
        } catch (MalformedURLException e) {
            // no one smart enough will let this happen
            throw new RuntimeException(e);
        }
    }
}
