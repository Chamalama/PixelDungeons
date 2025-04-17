package mike.pixelDungeons.dungeon.util;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class DungeonRegion {

    @Expose
    private String worldName, regionName;
    @Expose
    private double minX, minY, minZ, maxX, maxY, maxZ;

    public DungeonRegion(String worldName, String regionName, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.worldName = worldName;
        this.regionName = regionName;
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxY = Math.max(minY, maxY);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public DungeonRegion(Location one, Location two, String regionName) {
        this(one.getWorld().getName(), regionName, one.getBlockX(), one.getBlockY(), one.getBlockZ(), two.getBlockX(), two.getBlockY(), two.getBlockZ());
    }

    public boolean contains(Location location) {
        return location.getWorld().getName().equalsIgnoreCase(worldName) &&
                location.getX() >= minX && location.getX() <= maxX &&
                location.getY() >= minY && location.getY() <= maxY &&
                location.getZ() >= minZ && location.getZ() <= maxZ;
    }

}
