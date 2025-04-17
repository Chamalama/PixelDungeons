package mike.pixelDungeons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.dungeon.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
public class DungeonTeamWrapper {

    private String teamName;
    private LinkedHashMap<UUID, DungeonPlayerWrapper> teammates;
    private Set<UUID> visibleMobs;
    private Dungeon attemptingDungeon;
    private DungeonRoomWrapper currentDungeonRoom;
    private int roomsCleared;
    private long currentRoomTimer;

    public Player getLeader() {
        return Bukkit.getPlayer(UUID.fromString(teamName));
    }

    public List<Player> getTeamPlayers() {
        final List<Player> teamPlayers = new ArrayList<>();
        for(UUID uuid : teammates.keySet()) {
            final Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;
            teamPlayers.add(player);
        }
        return teamPlayers;
    }

    public void clearMobs() {
        for(UUID uuid : visibleMobs) {
            final Entity entity = Bukkit.getEntity(uuid);
            if(entity == null) continue;
            entity.remove();
        }
    }

}
