package mike.pixelDungeons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.dungeon.util.DungeonRegion;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DungeonRoomWrapper {

    private DungeonRegion roomRegion;
    private int roomNumber;
    private long timer;
    private String objective;
    private List<String> startLore;
    private List<String> endLore;

}
