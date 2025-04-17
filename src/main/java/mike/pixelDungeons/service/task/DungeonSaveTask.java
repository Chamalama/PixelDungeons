package mike.pixelDungeons.service.task;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.util.runnable.AbstractRunnable;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.storage.DungeonPlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

@Component
public class DungeonSaveTask extends AbstractRunnable {

    private final Plugin plugin;
    private final DungeonPlayerStorage dungeonPlayerStorage;

    public DungeonSaveTask(Plugin plugin, DungeonPlayerStorage dungeonPlayerStorage) {
        super(PixelDungeons.get(), 1800, true);
        this.plugin = plugin;
        this.dungeonPlayerStorage = dungeonPlayerStorage;
    }

    @Override
    public void run() {
        if(Bukkit.getOnlinePlayers().isEmpty()) return;
        plugin.getLogger().info("Saving all dungeon player stats!");
        for(Player player : Bukkit.getOnlinePlayers()) {
            final UUID id = player.getUniqueId();
            dungeonPlayerStorage.save(id);
        }
    }
}
