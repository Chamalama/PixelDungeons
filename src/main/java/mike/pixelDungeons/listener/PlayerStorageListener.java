package mike.pixelDungeons.listener;

import gg.supervisor.core.annotation.Component;
import mike.pixelDungeons.dungeon.storage.DungeonPlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Component
public class PlayerStorageListener implements Listener {

    private final DungeonPlayerStorage dungeonPlayerStorage;

    public PlayerStorageListener(DungeonPlayerStorage dungeonPlayerStorage) {
        this.dungeonPlayerStorage = dungeonPlayerStorage;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        dungeonPlayerStorage.getOrLoad(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        dungeonPlayerStorage.save(player.getUniqueId());
    }

}
