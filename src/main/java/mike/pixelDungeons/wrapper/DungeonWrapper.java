package mike.pixelDungeons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.dungeon.DungeonRoom;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class DungeonWrapper {

    private String dungeonName;
    private Location dungeonSpawnLocation;
    private Map<String, DungeonTeamWrapper> dungeonTeams;
    private LinkedList<DungeonRoom> dungeonRooms;
    private List<String> lore;
    private List<String> endLore;

}
