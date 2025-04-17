package mike.pixelDungeons;

import co.aikar.commands.PaperCommandManager;
import gg.supervisor.core.loader.SupervisorLoader;
import gg.supervisor.core.util.Services;
import mike.pixelDungeons.mdungeons.dungeons.station.SpaceStationDungeon;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class PixelDungeons extends JavaPlugin {

    public static PixelDungeons plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Services.register(Plugin.class, this);
        SupervisorLoader.register(this, new PaperCommandManager(this));
        clear(true);
    }

    @Override
    public void onDisable() {
        clear(false);
    }

    public static PixelDungeons get() {
        return plugin;
    }

    private void clear(boolean onEnable) {

        for(World world : Bukkit.getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(!entity.hasMetadata("DUNGEON")) continue;
                entity.remove();
            }
        }

        if(!onEnable) {
            for (UUID uuid : SpaceStationDungeon.getStationDisplays()) {
                final Entity entity = Bukkit.getEntity(uuid);
                if (entity == null) continue;
                entity.remove();
            }
        }

    }

}
