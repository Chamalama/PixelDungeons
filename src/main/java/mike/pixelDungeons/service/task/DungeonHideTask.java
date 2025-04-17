package mike.pixelDungeons.service.task;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.util.runnable.AbstractRunnable;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.ArrayList;
import java.util.List;

@Component
public class DungeonHideTask extends AbstractRunnable {

    private final DungeonPlayerService dungeonPlayerService;

    public DungeonHideTask(DungeonPlayerService dungeonPlayerService) {
        super(PixelDungeons.get(), 80, false);
        this.dungeonPlayerService = dungeonPlayerService;
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
            if(teamWrapper == null) continue;
            final Dungeon dungeon = teamWrapper.getAttemptingDungeon();
            if(dungeon == null) continue;

            final World world = player.getWorld();

            for(Entity entity : world.getEntities()) {
                if(teamWrapper.getVisibleMobs().contains(entity.getUniqueId()) || entity instanceof TextDisplay) continue;
                player.hideEntity(PixelDungeons.get(), entity);
            }

            final List<Player> playersToHide = new ArrayList<>();

            for(DungeonTeamWrapper toHide : dungeonPlayerService.getDungeonTeams()) {
                if(teamWrapper.getLeader() == toHide.getLeader() || teamWrapper.getAttemptingDungeon() != toHide.getAttemptingDungeon()) continue;
                playersToHide.addAll(toHide.getTeamPlayers());
            }

            for(Player hidden : playersToHide) {
                player.hidePlayer(PixelDungeons.get(), hidden);
            }
        }
    }
}
