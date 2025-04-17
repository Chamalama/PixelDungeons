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

    @Getter
    public static Set<UUID> stationDisplays = new HashSet<>();

    public SpaceStationDungeon() {
        this.dungeonWrapper = new DungeonWrapper(
                "Space Station",
                new Location(world, 0, 164.5, 0).toCenterLocation(),
                new HashMap<>(),
                new LinkedList<>() {{
                    add(new AirlockRoom());
                }},
                List.of(
                        "Once a deep space research hub, Station Aegis fell",
                        "silent after a Rift experiment went wrong.",
                        "Now it drifts in orbit",
                        "overrun by alien horrors from beyond."
                ),
                List.of(
                        "Station Aegis has been secured",
                        "unfortunately many researchers have been lost",
                        "but there was no way for us to know this would happen.",
                        "Good job on this mission."
                )
        );

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
