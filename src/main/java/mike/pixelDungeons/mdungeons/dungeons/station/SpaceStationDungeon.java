package mike.pixelDungeons.mdungeons.dungeons.station;

import lombok.Getter;
import mike.mLibrary.entity.Hologram;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import mike.pixelDungeons.wrapper.DungeonWrapper;
import org.bukkit.*;

import java.util.*;

public class SpaceStationDungeon extends Dungeon {

    private World world = Bukkit.getWorld("Space_Station");

    //Will be removed
    @Getter
    public static Set<UUID> stationDisplays = new HashSet<>();

    public SpaceStationDungeon() {
        super(new DungeonWrapper("Space Station"));

        this.dungeonWrapper.setDungeonRooms(new LinkedList<>(){{
            add(new AirlockRoom());
        }});

        spawnHolograms();

    }

    @Override
    public void onDungeonStart(DungeonTeamWrapper dungeonTeamWrapper) {

    }

    private void spawnHolograms() {
        if(world == null) {
            world = new WorldCreator("Space_Station").createWorld();
        }
        stationDisplays.add(Hologram.spawnHologram(new Location(world, 0, 166, 5).toCenterLocation(), ChatColor.RED + "Space Station").getUniqueId());
    }

}
