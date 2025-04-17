package mike.pixelDungeons.listener;

import gg.supervisor.core.annotation.Component;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.config.SpawnConfig;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.dungeon.event.*;
import mike.pixelDungeons.dungeon.storage.DungeonPlayerStorage;
import mike.pixelDungeons.dungeon.storage.StoredDungeonPlayer;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

@Component
public class DungeonListener implements Listener {

    private final DungeonPlayerService dungeonPlayerService;
    private final DungeonPlayerStorage dungeonPlayerStorage;
    private final SpawnConfig spawnConfig;

    public DungeonListener(DungeonPlayerService dungeonPlayerService, DungeonPlayerStorage dungeonPlayerStorage, SpawnConfig spawnConfig) {
        this.dungeonPlayerService = dungeonPlayerService;
        this.dungeonPlayerStorage = dungeonPlayerStorage;
        this.spawnConfig = spawnConfig;
    }

    @EventHandler
    public void onDungeonStart(PlayerEnterDungeonEvent event) {
        if(event.isCancelled()) return;

        final Player player = event.getPlayer();
        final DungeonTeamWrapper dungeonTeamWrapper = dungeonPlayerService.getDungeonLeaderTeam(player);
        dungeonTeamWrapper.setAttemptingDungeon(event.getDungeon());

        dungeonTeamWrapper.getTeamPlayers().forEach(teamPlayer -> {
            if(teamPlayer == player) return;
            teamPlayer.teleport(event.getDungeon().getDungeonWrapper().getDungeonSpawnLocation().toCenterLocation(), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
        });

        final Dungeon dungeon = event.getDungeon();
        dungeon.onDungeonStart(dungeonTeamWrapper);

    }

    @EventHandler
    public void onEnterRoomEvent(PlayerEnterDungeonRoomEvent event) {
        if(event.isCancelled()) return;
        final Player player = event.getPlayer();
        final DungeonTeamWrapper dungeonTeamWrapper = dungeonPlayerService.getDungeonLeaderTeam(player);
        event.getDungeonRoom().startRoomEvent(dungeonTeamWrapper);
    }

    @EventHandler
    public void onDungeonFail(DungeonFailEvent event) {
        if(event.isCancelled()) return;
        final DungeonTeamWrapper teamWrapper = event.getDungeonTeamWrapper();
        teamWrapper.clearMobs();

        teamWrapper.getAttemptingDungeon().getDungeonWrapper().getDungeonTeams().remove(teamWrapper.getTeamName());

        teamWrapper.getTeamPlayers().forEach(player -> {
            final StoredDungeonPlayer storedDungeonPlayer = dungeonPlayerStorage.getOrLoad(player.getUniqueId());
            storedDungeonPlayer.setDungeonsFailed(storedDungeonPlayer.getDungeonsFailed() + 1);
            dungeonPlayerStorage.save(player.getUniqueId());
            player.teleport(spawnConfig.getOrLoad(false).toBukkit().toCenterLocation(), PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT);
        });

        dungeonPlayerService.clearTeam(teamWrapper.getLeader());
    }

    @EventHandler
    public void onDungeonComplete(TeamDungeonCompleteEvent event) {
        if(event.isCancelled()) return;
        final DungeonTeamWrapper teamWrapper = event.getDungeonTeamWrapper();

        teamWrapper.getAttemptingDungeon().getDungeonWrapper().getDungeonTeams().remove(teamWrapper.getTeamName());
        teamWrapper.clearMobs();

        teamWrapper.getTeamPlayers().forEach(player -> {

            player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.1F);
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1.0F, 1.0F);
            player.playSound(player, Sound.ENTITY_BAT_TAKEOFF, 1.0F, 1.0F);

            Bukkit.getScheduler().runTaskAsynchronously(PixelDungeons.get(), () -> {

                for(String endLore : teamWrapper.getAttemptingDungeon().getDungeonWrapper().getEndLore()) {
                    DungeonUtil.sendDungeonMessage(player, ChatColor.GREEN + endLore);
                }

                final StoredDungeonPlayer storedDungeonPlayer = dungeonPlayerStorage.getOrLoad(player.getUniqueId());
                storedDungeonPlayer.setDungeonsCompleted(storedDungeonPlayer.getDungeonsCompleted() + 1);
                dungeonPlayerStorage.save(player.getUniqueId());

            });

            player.sendTitle(ChatColor.GOLD + ChatColor.BOLD.toString() + "DUNGEON COMPLETE", ChatColor.GREEN + "Teleporting in 5s", 20, 20, 20);

        });

        Bukkit.getScheduler().runTaskLater(PixelDungeons.get(), () -> {
            teamWrapper.getTeamPlayers().forEach(player -> {
                player.teleport(spawnConfig.getOrLoad(false).toBukkit().toCenterLocation());
            });
            dungeonPlayerService.clearTeam(teamWrapper.getLeader());
        }, 100L);

    }

    @EventHandler
    public void onRoomComplete(TeamDungeonRoomCompleteEvent event) {
        if(event.isCancelled()) return;
        final DungeonTeamWrapper teamWrapper = event.getDungeonTeamWrapper();
        teamWrapper.clearMobs();

        int rooms = teamWrapper.getAttemptingDungeon().getDungeonWrapper().getDungeonRooms().size();

        teamWrapper.setRoomsCleared(teamWrapper.getRoomsCleared() + 1);
        teamWrapper.setCurrentDungeonRoom(null);

        if(teamWrapper.getRoomsCleared() >= rooms) {
            TeamDungeonCompleteEvent teamDungeonCompleteEvent = new TeamDungeonCompleteEvent(teamWrapper);
            Bukkit.getPluginManager().callEvent(teamDungeonCompleteEvent);
        }
    }

    @EventHandler
    public void onDungeonDeath(PlayerDungeonDeathEvent event) {
        final Player player = event.getPlayer();

        final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);

        final StoredDungeonPlayer storedDungeonPlayer = dungeonPlayerStorage.getOrLoad(player.getUniqueId());
        storedDungeonPlayer.setDungeonDeaths(storedDungeonPlayer.getDungeonDeaths() + 1);
        dungeonPlayerStorage.save(player.getUniqueId());
    }

}
