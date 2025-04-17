package mike.pixelDungeons.listener;

import gg.supervisor.core.annotation.Component;
import mike.mLibrary.util.LocationUtil;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.dungeon.DungeonRoom;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.dungeon.event.PlayerDungeonDeathEvent;
import mike.pixelDungeons.dungeon.event.PlayerEnterDungeonEvent;
import mike.pixelDungeons.dungeon.event.PlayerEnterDungeonRoomEvent;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.service.DungeonService;
import mike.pixelDungeons.wrapper.DungeonRoomWrapper;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import mike.pixelDungeons.dungeon.util.RegionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.UUID;

@Component
public class DungeonPlayerListener implements Listener {

    private final DungeonService dungeonService;
    private final DungeonPlayerService dungeonPlayerService;

    public DungeonPlayerListener(DungeonService dungeonService, DungeonPlayerService dungeonPlayerService) {
        this.dungeonService = dungeonService;
        this.dungeonPlayerService = dungeonPlayerService;
    }

    @EventHandler
    public void onJoinDungeon(PlayerTeleportEvent event) {

        final World fromWorld = event.getFrom().getWorld();
        final World world = event.getTo().getWorld();

        if(fromWorld == world) return;

        final Dungeon previousDungeon = dungeonService.getDungeon(fromWorld.getName());

        final Player player = event.getPlayer();

        if(previousDungeon != null) {
            final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
            dungeonPlayerService.leaveTeam(player);
            previousDungeon.getDungeonWrapper().getDungeonTeams().put(teamWrapper.getTeamName(), teamWrapper);
            return;
        }

        final String worldName = world.getName().toLowerCase().replace("_", " ");

        final Dungeon dungeon = dungeonService.getDungeon(worldName);

        if(dungeon == null) return;

        if(dungeonPlayerService.getDungeonLeaderTeam(player) == null && !player.isOp()) {
            PixelDungeons.get().getLogger().info("Cancelling teleportation for " + player.getName() + " since their dungeon team is null!");
            event.setCancelled(true);
            return;
        }

        final DungeonTeamWrapper dungeonTeamWrapper = dungeonPlayerService.getDungeonLeaderTeam(player);

        if(dungeonTeamWrapper == null) {
            PixelDungeons.get().getLogger().info("Dungeon team is null for " + player.getName() + "!");
            return;
        }

        dungeon.initializeDungeonTeam(dungeonTeamWrapper);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if(LocationUtil.isSameLocation(event.getFrom(), event.getTo())) return;

        final DungeonTeamWrapper dungeonTeamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);

        if(dungeonTeamWrapper == null) return;

        final Dungeon dungeon = dungeonService.getDungeonFromPlayer(player);

        if(dungeon == null) return;

        final Location playerLocation = player.getLocation();

        if(dungeonTeamWrapper.getCurrentDungeonRoom() != null || !dungeon.getDungeonWrapper().getDungeonTeams().containsKey(dungeonTeamWrapper.getTeamName())) return;

        for(DungeonRoom dungeonRoom : dungeon.getDungeonWrapper().getDungeonRooms()) {

            final DungeonRoomWrapper drm = dungeonRoom.getDungeonRoomWrapper();
            if(dungeonTeamWrapper.getRoomsCleared() >= drm.getRoomNumber()) continue;
            if(!RegionUtil.isInDungeonRegion(playerLocation.toCenterLocation(), drm.getRoomRegion())) continue;

            PlayerEnterDungeonRoomEvent playerEnterDungeonEvent = new PlayerEnterDungeonRoomEvent(player, dungeonRoom);
            Bukkit.getPluginManager().callEvent(playerEnterDungeonEvent);

        }

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getPlayer();
        final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
        if(teamWrapper == null) return;
        final Dungeon dungeon = teamWrapper.getAttemptingDungeon();
        if(dungeon == null) return;
        event.setKeepInventory(true);
        event.getDrops().clear();
        PlayerDungeonDeathEvent playerDungeonDeathEvent = new PlayerDungeonDeathEvent(player, dungeon);
        Bukkit.getPluginManager().callEvent(playerDungeonDeathEvent);
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
        if(teamWrapper == null) return;

        teamWrapper.getTeamPlayers().forEach(teamPlayer -> DungeonUtil.sendDungeonMessage(teamPlayer, player.getName() + " has logged out!"));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);
        if(teamWrapper == null) return;

        teamWrapper.getTeamPlayers().forEach(teamPlayer -> DungeonUtil.sendDungeonMessage(teamPlayer, ChatColor.GREEN + player.getName() + " has logged in!"));
    }

}
