package mike.pixelDungeons.entity;

import lombok.Getter;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.mdungeons.dungeons.station.entity.StationAlien;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Location;
import org.bukkit.metadata.FixedMetadataValue;

@Getter
public enum Registry {

    STATION_ALIEN(new StationAlien());

    private final DungeonEntity dungeonEntity;

    Registry(DungeonEntity dungeonEntity) {
        this.dungeonEntity = dungeonEntity;
    }

    public void spawnEntity(Registry registry, Location location) {
        registry.getDungeonEntity().spawn(location);
    }

    public void spawnEntity(Location location, DungeonTeamWrapper dungeonTeamWrapper) {
        dungeonEntity.spawn(location);
        dungeonEntity.getDungeonEntity().setMetadata("DUNGEON", new FixedMetadataValue(PixelDungeons.get(), ""));
        dungeonTeamWrapper.getVisibleMobs().add(dungeonEntity.getDungeonEntity().getUniqueId());
    }

}
