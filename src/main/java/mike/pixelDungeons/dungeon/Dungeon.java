package mike.pixelDungeons.dungeon;

import lombok.Getter;
import lombok.Setter;
import mike.mLibrary.config.MConfig;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.event.PlayerEnterDungeonEvent;
import mike.pixelDungeons.wrapper.DungeonPlayerWrapper;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import mike.pixelDungeons.wrapper.DungeonWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

@Getter
@Setter
public abstract class Dungeon extends MConfig<DungeonWrapper> implements Listener {

    protected DungeonWrapper dungeonWrapper;

    public Dungeon(DungeonWrapper dungeonWrapper) {
        super(PixelDungeons.get(), dungeonWrapper.getDungeonName(), dungeonWrapper.getDungeonName(), new DungeonWrapper(dungeonWrapper.getDungeonName()));
        this.dungeonWrapper = dungeonWrapper;
        this.dungeonWrapper.setDungeonSpawnLocation(this.getOrLoad(false).getDungeonSpawnLocation());
        this.dungeonWrapper.setLore(this.getOrLoad(false).getLore());
        this.dungeonWrapper.setEndLore(this.getOrLoad(false).getEndLore());
    }

    public void initializeDungeonTeam(DungeonTeamWrapper dungeonTeamWrapper) {

        final String teamName = dungeonTeamWrapper.getTeamName();

        dungeonWrapper.getDungeonTeams().putIfAbsent(teamName, dungeonTeamWrapper);

        final Player leader = Bukkit.getPlayer(UUID.fromString(teamName));

        if(leader == null) {
            PixelDungeons.get().getLogger().info("The leader for this dungeon team is somehow null? Team: " + dungeonTeamWrapper.getTeamName());
            return;
        }

        PixelDungeons.get().getLogger().info(leader.getName() + "'s team has entered the " + dungeonWrapper.getDungeonName() + " dungeon!");

        PlayerEnterDungeonEvent playerEnterDungeonEvent = new PlayerEnterDungeonEvent(dungeonTeamWrapper.getLeader(), this);
        Bukkit.getPluginManager().callEvent(playerEnterDungeonEvent);

    }

    public void onDungeonStart(DungeonTeamWrapper dungeonTeamWrapper) {

    }

}
