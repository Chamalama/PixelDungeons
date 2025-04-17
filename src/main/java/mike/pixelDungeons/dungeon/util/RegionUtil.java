package mike.pixelDungeons.dungeon.util;

import lombok.Getter;
import org.bukkit.Location;

@Getter
public class RegionUtil {

    public static boolean isInDungeonRegion(Location location, DungeonRegion dungeonRegion) {
        return dungeonRegion.contains(location);
    }

}
