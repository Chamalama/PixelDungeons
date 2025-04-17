package mike.pixelDungeons.dungeon.storage;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoredDungeonPlayer {

    @Expose
    private int dungeonsCompleted;
    @Expose
    private int dungeonsFailed;
    @Expose
    private int dungeonDeaths;
    @Expose
    private int dungeonBossesKilled;
    @Expose
    private long fastestDungeonCompletion;


}
