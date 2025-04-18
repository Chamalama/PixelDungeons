package mike.pixelDungeons.wrapper;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.mLibrary.util.LocWrapper;
import mike.pixelDungeons.dungeon.DungeonRoom;
import org.bukkit.Location;

import java.util.*;

@Getter
@Setter
public class DungeonWrapper {

    private transient String dungeonName;
    private transient Map<String, DungeonTeamWrapper> dungeonTeams;
    private transient LinkedList<DungeonRoom> dungeonRooms;
    @Expose
    private LocWrapper dungeonSpawnLocation;
    @Expose
    private List<String> lore = new ArrayList<>();
    @Expose
    private List<String> endLore = new ArrayList<>();

    public DungeonWrapper(String dungeonName) {
        this.dungeonName = dungeonName;
        this.dungeonSpawnLocation = new LocWrapper("", 0, 0, 0, 0, 0);
        this.dungeonTeams = new HashMap<>();
        this.dungeonRooms = new LinkedList<>();
    }

}
