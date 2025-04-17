package mike.pixelDungeons.dungeon.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@AllArgsConstructor
public class DungeonFailEvent extends Event implements Cancellable {

    public static HandlerList HANDLER_LIST = new HandlerList();

    private final DungeonTeamWrapper dungeonTeamWrapper;

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
