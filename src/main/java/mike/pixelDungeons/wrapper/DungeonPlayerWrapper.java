package mike.pixelDungeons.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.dungeon.Dungeon;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class DungeonPlayerWrapper {

    private UUID playerID;
    private Dungeon currentDungeon;
    private DungeonRoomWrapper currentDungeonRoom;
    private int playerPosition;
    private int roomsCleared;

}
