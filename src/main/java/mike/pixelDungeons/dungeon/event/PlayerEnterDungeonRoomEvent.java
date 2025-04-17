package mike.pixelDungeons.dungeon.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.dungeon.DungeonRoom;
import mike.pixelDungeons.wrapper.DungeonRoomWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class PlayerEnterDungeonRoomEvent extends Event implements Cancellable {

    public static HandlerList HANDLER_LIST = new HandlerList();

    private Player player;
    private DungeonRoom dungeonRoom;

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
