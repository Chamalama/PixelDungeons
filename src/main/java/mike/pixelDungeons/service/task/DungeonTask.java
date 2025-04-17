package mike.pixelDungeons.service.task;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.util.runnable.AbstractRunnable;
import mike.mLibrary.text.Chat;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.event.DungeonFailEvent;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Component
public class DungeonTask extends AbstractRunnable {

    private final DungeonPlayerService dungeonPlayerService;

    public DungeonTask(DungeonPlayerService dungeonPlayerService) {
        super(PixelDungeons.get(), 20, false);
        this.dungeonPlayerService = dungeonPlayerService;
    }

    @Override
    public void run() {
        if(dungeonPlayerService.getLeaderToTeam().isEmpty()) return;
        for(DungeonTeamWrapper dungeonTeamWrapper : dungeonPlayerService.getDungeonTeams()) {
            if(dungeonTeamWrapper.getCurrentDungeonRoom() == null) continue;
            dungeonTeamWrapper.setCurrentRoomTimer(dungeonTeamWrapper.getCurrentRoomTimer() - 1);
            dungeonTeamWrapper.getTeamPlayers().forEach(teamPlayer -> teamPlayer.sendActionBar(Chat.translate(ChatColor.YELLOW + "Remaining Time: " + formatTime(dungeonTeamWrapper.getCurrentRoomTimer()))));
            if(dungeonTeamWrapper.getCurrentRoomTimer() <= 0) {
                DungeonFailEvent dungeonFailEvent = new DungeonFailEvent(dungeonTeamWrapper);
                Bukkit.getPluginManager().callEvent(dungeonFailEvent);
            }
        }
    }

    private String formatTime(long time) {
        int minutes = (int) (time / 60);
        int seconds = (int) (time % 60);
        return minutes > 0 ? String.format("%dm, %ds", minutes, seconds) : String.format("%ds", seconds);
    }

}
