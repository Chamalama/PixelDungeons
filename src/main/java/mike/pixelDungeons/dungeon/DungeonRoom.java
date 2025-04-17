package mike.pixelDungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.event.TeamDungeonRoomCompleteEvent;
import mike.pixelDungeons.wrapper.DungeonRoomWrapper;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.Listener;

import java.util.List;

@Getter
@Setter
public abstract class DungeonRoom implements Listener {

    private DungeonRoomWrapper dungeonRoomWrapper;

    public DungeonRoom(DungeonRoomWrapper dungeonRoomWrapper) {
        this.dungeonRoomWrapper = dungeonRoomWrapper;
        Bukkit.getPluginManager().registerEvents(this, PixelDungeons.get());
    }

    public void startRoomEvent(DungeonTeamWrapper dungeonTeamWrapper) {
        dungeonTeamWrapper.setCurrentRoomTimer(dungeonRoomWrapper.getTimer());
        dungeonTeamWrapper.setCurrentDungeonRoom(dungeonRoomWrapper);

        Bukkit.getScheduler().runTaskAsynchronously(PixelDungeons.get(), () -> {
            dungeonTeamWrapper.getTeamPlayers().forEach(player -> {
                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0F, 1.0F);
                DungeonUtil.sendDungeonMessage(player, "Complete the objective!");
                DungeonUtil.sendDungeonMessage(player, dungeonRoomWrapper.getObjective());
                for(String strings : dungeonTeamWrapper.getCurrentDungeonRoom().getStartLore()) {
                    DungeonUtil.sendUrgentDungeonMessage(player, strings);
                }
            });
        });
    }

    public void onCompleteRoomEvent(DungeonTeamWrapper dungeonTeamWrapper) {

        final List<String> endLore = dungeonTeamWrapper.getCurrentDungeonRoom().getEndLore();

        Bukkit.getScheduler().runTaskAsynchronously(PixelDungeons.get(), () -> {

            dungeonTeamWrapper.getTeamPlayers().forEach(player -> {
                for(String lore : endLore) {
                    DungeonUtil.sendDungeonMessage(player, ChatColor.WHITE + lore);
                }
            });

        });



        TeamDungeonRoomCompleteEvent roomCompleteEvent = new TeamDungeonRoomCompleteEvent(dungeonTeamWrapper);
        Bukkit.getPluginManager().callEvent(roomCompleteEvent);
    }

}
