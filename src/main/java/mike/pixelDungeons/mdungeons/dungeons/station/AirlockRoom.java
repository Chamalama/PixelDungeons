package mike.pixelDungeons.mdungeons.dungeons.station;

import gg.supervisor.core.util.Services;
import mike.mLibrary.util.LocWrapper;
import mike.pixelDungeons.dungeon.DungeonRoom;
import mike.pixelDungeons.entity.Registry;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.wrapper.DungeonRoomWrapper;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import mike.pixelDungeons.dungeon.util.DungeonRegion;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TEST DUNGEON ROOM
public class AirlockRoom extends DungeonRoom {

    private final String worldName = "Space_Station";

    private final List<LocWrapper> mobLocations = List.of(
            new LocWrapper(worldName, 9, 164, 54, 146, 0),
            new LocWrapper(worldName, -8, 164, 46, -156, 0)
    );

    private final Map<DungeonTeamWrapper, Integer> mobsKilled = new HashMap<>();

    public AirlockRoom() {
        super(new DungeonRoomWrapper(new DungeonRegion(
                "Space_Station",
                "Airlock",
                -20, 163, 7.3, 20, 175, 60.7),
                1,
                120,
                "Kill all hostile aliens!",
                List.of("These must be some of the aliens that came through the rift!", "Don't let a single one leave alive!"),
                new ArrayList<>()
                )
        );
    }

    @Override
    public void startRoomEvent(DungeonTeamWrapper dungeonTeamWrapper) {
        super.startRoomEvent(dungeonTeamWrapper);
        spawnMobs(dungeonTeamWrapper);
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        final LivingEntity le = event.getEntity();
        final Player killer = le.getKiller();
        if(killer == null) return;
        final DungeonTeamWrapper teamWrapper = Services.getService(DungeonPlayerService.class).getPlayersDungeonTeam(killer);
        if(teamWrapper == null) return;

        mobsKilled.compute(teamWrapper, (key, val) -> val == null ? 1 : val + 1);

        if(teamWrapper.getCurrentDungeonRoom() != this.getDungeonRoomWrapper()) return;
        if(!le.hasMetadata("DUNGEON")) return;
        teamWrapper.getVisibleMobs().remove(le.getUniqueId());

        if(mobsKilled.get(teamWrapper) >= 2) {
            super.onCompleteRoomEvent(teamWrapper);
        }

    }

    private void spawnMobs(DungeonTeamWrapper teamWrapper) {
        for(LocWrapper wrapper : mobLocations) {
            final Location bukkitLocation = wrapper.toBukkit().toCenterLocation();
            Registry.STATION_ALIEN.spawnEntity(bukkitLocation, teamWrapper);
        }
    }


}
