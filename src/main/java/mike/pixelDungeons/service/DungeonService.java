package mike.pixelDungeons.service;

import gg.supervisor.core.annotation.Component;
import lombok.Getter;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Component
public class DungeonService {

    public Map<String, Dungeon> keyToDungeon = new LinkedHashMap<>();

    public void registerDungeons(Dungeon... dungeons) {
        for(Dungeon dungeon : dungeons) {
            keyToDungeon.put(dungeon.getDungeonWrapper().getDungeonName().toLowerCase(), dungeon);
            Bukkit.getPluginManager().registerEvents(dungeon, PixelDungeons.get());
        }
    }

    public Set<String> dungeonNames() {
        return keyToDungeon.keySet();
    }

    public Dungeon getDungeon(String key) {
        return keyToDungeon.getOrDefault(key, null);
    }

    public Dungeon getDungeonFromPlayer(Player player) {
        return getDungeon(player.getWorld().getName().replace("_", " ").toLowerCase());
    }

    public Collection<Dungeon> getAll() {
        return keyToDungeon.values();
    }



}
