package mike.pixelDungeons.config;

import gg.supervisor.core.annotation.Component;
import lombok.Getter;
import mike.mLibrary.config.MConfig;
import mike.mLibrary.util.LocWrapper;
import mike.pixelDungeons.PixelDungeons;

@Component
@Getter
public class SpawnConfig extends MConfig<LocWrapper> {

    public SpawnConfig() {
        super(PixelDungeons.get(), "location", "spawn", new LocWrapper("world", 0, 100, 0, 0, 0));
    }
}
