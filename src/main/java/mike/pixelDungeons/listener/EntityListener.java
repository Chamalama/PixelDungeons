package mike.pixelDungeons.listener;

import gg.supervisor.core.annotation.Component;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

@Component
public class EntityListener implements Listener {

    private final DungeonPlayerService dungeonPlayerService;

    public EntityListener(DungeonPlayerService dungeonPlayerService) {
        this.dungeonPlayerService = dungeonPlayerService;
    }

    @EventHandler
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if(!(event.getTarget() instanceof Player player)) return;
        final DungeonTeamWrapper wrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
        if(wrapper == null) return;
        if(!wrapper.getVisibleMobs().contains(event.getEntity().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity().hasMetadata("DUNGEON")) {
            event.setDroppedExp(0);
            event.getDrops().clear();
        }
    }

}
