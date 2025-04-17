package mike.pixelDungeons.dungeon.storage;

import gg.supervisor.core.annotation.Component;
import mike.mLibrary.config.PlayerConfig;
import mike.pixelDungeons.PixelDungeons;

@Component
public class DungeonPlayerStorage extends PlayerConfig<StoredDungeonPlayer> {

    public DungeonPlayerStorage() {
        super(PixelDungeons.get(), "players", new StoredDungeonPlayer());
    }

}
