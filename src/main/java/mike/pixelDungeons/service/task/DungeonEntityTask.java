package mike.pixelDungeons.service.task;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.util.runnable.AbstractRunnable;
import mike.pixelDungeons.entity.DungeonEntity;
import mike.pixelDungeons.entity.Registry;
import org.bukkit.plugin.Plugin;

@Component
public class DungeonEntityTask extends AbstractRunnable {

    private final Plugin plugin;

    public DungeonEntityTask(Plugin plugin) {
        super(plugin, 1, true);
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Registry entity : Registry.values()) {
            final DungeonEntity dungeonEntity = entity.getDungeonEntity();
            if(dungeonEntity == null || dungeonEntity.getDungeonEntity() == null) continue;
            if(!dungeonEntity.getDungeonEntity().isDead()) {
                dungeonEntity.tick();
            }
        }
    }
}
