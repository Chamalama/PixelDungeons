package mike.pixelDungeons.dungeon.storage;

import gg.supervisor.core.annotation.Component;
import mike.mLibrary.config.MConfig;
import mike.mLibrary.gui.GuiWrapper;
import mike.pixelDungeons.PixelDungeons;

@Component
public class DungeonGUIStorage extends MConfig<GuiWrapper> {

    public DungeonGUIStorage() {
        super(PixelDungeons.get(), "config", "dungeon-gui", new GuiWrapper());
    }
}
