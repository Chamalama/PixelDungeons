package mike.pixelDungeons.service;

import gg.supervisor.core.annotation.Component;
import lombok.Getter;
import mike.mLibrary.text.Message;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.config.SpawnConfig;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.dungeon.event.DungeonFailEvent;
import mike.pixelDungeons.mdungeons.dungeons.station.SpaceStationDungeon;
import mike.pixelDungeons.wrapper.DungeonPlayerWrapper;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Component
public class DungeonPlayerService {

    private final DungeonService dungeonService;
    private final SpawnConfig spawnConfig;

    public Map<DungeonTeamWrapper, List<UUID>> teamInviteToPlayer = new HashMap<>();

    public Map<UUID, DungeonTeamWrapper> playerToTeam = new HashMap<>();

    public Map<UUID, DungeonTeamWrapper> leaderToTeam = new HashMap<>();

    public static int MAX_TEAM_PLAYERS = 6;

    public DungeonPlayerService(DungeonService dungeonService, SpawnConfig spawnConfig) {
        this.dungeonService = dungeonService;
        this.spawnConfig = spawnConfig;
        dungeonService.registerDungeons(new SpaceStationDungeon());
    }

    public void createTeam(Player player) {
        if(leaderToTeam.containsKey(player.getUniqueId()) || playerToTeam.containsKey(player.getUniqueId())) {
            DungeonUtil.sendUrgentDungeonMessage(player, "You're already in a dungeon team!");
            return;
        }
        final DungeonTeamWrapper teamWrapper = new DungeonTeamWrapper(player.getUniqueId().toString(), new LinkedHashMap<>(), new HashSet<>(), null, null,0, 0);
        final DungeonPlayerWrapper wrapper = new DungeonPlayerWrapper(player.getUniqueId(), null, null, 1, 0);
        teamWrapper.getTeammates().put(player.getUniqueId(), wrapper);
        leaderToTeam.put(player.getUniqueId(), teamWrapper);
        playerToTeam.put(player.getUniqueId(), teamWrapper);

        DungeonUtil.sendDungeonMessage(player, "Created a new dungeon team!");
    }

    public void disbandTeam(Player leader) {
        final DungeonTeamWrapper dungeonTeamWrapper = getDungeonLeaderTeam(leader);

        if(dungeonTeamWrapper.getAttemptingDungeon() != null) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You cannot disband your team while attempting a dungeon!");
            return;
        }

        leaderToTeam.get(leader.getUniqueId()).getTeammates().remove(leader.getUniqueId());
        playerToTeam.remove(leader.getUniqueId());
        dungeonTeamWrapper.getTeammates().keySet().forEach(uuid -> {
            playerToTeam.remove(uuid);
        });
        leaderToTeam.remove(leader.getUniqueId());
        dungeonTeamWrapper.getTeammates().clear();
        teamInviteToPlayer.remove(dungeonTeamWrapper);
        DungeonUtil.sendDungeonMessage(leader, "Disbanded your dungeon team!");
    }

    public void invitePlayer(Player leader, Player invitee) {

        if(leader == invitee) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You cannot invite yourself!");
            return;
        }

        if(dungeonService.getDungeonFromPlayer(leader) != null) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You cannot invite a player while in a dungeon!");
            return;
        }

        final DungeonTeamWrapper dungeonTeamWrapper = getDungeonLeaderTeam(leader);

        if(dungeonTeamWrapper == null) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You need to be a dungeon leader to do this!");
            return;
        }

        if(dungeonTeamWrapper.getTeammates().size() >= MAX_TEAM_PLAYERS) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "Your dungeon team is full!");
            return;
        }

        DungeonUtil.sendDungeonMessage(leader, "You invited " + invitee.getName() + " to your dungeon team!");
        DungeonUtil.sendDungeonMessage(invitee, "You have been invited to " + leader.getName() + "'s dungeon team! ");
        DungeonUtil.sendDungeonMessage(invitee, ChatColor.GRAY + "(Type /dungeon team join " + leader.getName() + ")");

        teamInviteToPlayer.putIfAbsent(dungeonTeamWrapper, new ArrayList<>());
        teamInviteToPlayer.get(dungeonTeamWrapper).add(invitee.getUniqueId());

        Bukkit.getScheduler().runTaskLater(PixelDungeons.get(), () -> {
            teamInviteToPlayer.get(dungeonTeamWrapper).remove(invitee.getUniqueId());
            if(teamInviteToPlayer.get(dungeonTeamWrapper).contains(invitee.getUniqueId())) {
                Message.urgent(invitee, "Your invite to " + dungeonTeamWrapper.getLeader().getName() + "'s dungeon team expired!");
            }
        }, 300L);

    }

    public void removedInvitedPlayer(DungeonTeamWrapper dungeonTeamWrapper, Player player) {
        teamInviteToPlayer.get(dungeonTeamWrapper).remove(player.getUniqueId());
    }

    public void joinTeam(DungeonTeamWrapper dungeonTeamWrapper, Player player) {
        if(dungeonTeamWrapper == null) {
            DungeonUtil.sendUrgentDungeonMessage(player, "This dungeon team does not exist!");
            return;
        }
        if(!canJoinTeam(player, dungeonTeamWrapper)) {
            DungeonUtil.sendUrgentDungeonMessage(player, "You were not invited to this dungeon team!");
            return;
        }
        DungeonPlayerWrapper wrapper = new DungeonPlayerWrapper(player.getUniqueId(), null, null, dungeonTeamWrapper.getTeammates().size() + 1, 0);
        dungeonTeamWrapper.getTeammates().put(wrapper.getPlayerID(), wrapper);
        DungeonUtil.sendDungeonMessage(dungeonTeamWrapper.getLeader(), player.getName() + " has joined your dungeon team!");
        DungeonUtil.sendDungeonMessage(player, "You joined " + dungeonTeamWrapper.getLeader().getName() + "'s dungeon team!");
        playerToTeam.put(player.getUniqueId(), dungeonTeamWrapper);
        removedInvitedPlayer(dungeonTeamWrapper, player);
    }

    public void leaveTeam(Player player) {

        final DungeonTeamWrapper dungeonTeamWrapper = getPlayersDungeonTeam(player);

        if(dungeonTeamWrapper == null) {
            DungeonUtil.sendUrgentDungeonMessage(player, "You are not in a dungeon team!");
            return;
        }

        if(player == dungeonTeamWrapper.getLeader()) {

            final Dungeon dungeon = dungeonTeamWrapper.getAttemptingDungeon();

            if(dungeon != null) {

                if(dungeonTeamWrapper.getTeammates().size() == 1) {
                    DungeonFailEvent dungeonFailEvent = new DungeonFailEvent(dungeonTeamWrapper);
                    Bukkit.getPluginManager().callEvent(dungeonFailEvent);
                    DungeonUtil.sendUrgentDungeonMessage(player, "You left the dungeon!");
                    return;
                }

                if(!dungeonTeamWrapper.getTeammates().isEmpty()) {
                    final UUID newLeader = selectRandomMember(dungeonTeamWrapper);
                    leaderToTeam.put(newLeader, dungeonTeamWrapper);
                    dungeonTeamWrapper.setTeamName(newLeader.toString());
                }

            } else {
                DungeonUtil.sendUrgentDungeonMessage(player, "Use /d team disband!");
            }

            return;
        }

        dungeonTeamWrapper.getTeammates().remove(player.getUniqueId());
        DungeonUtil.sendDungeonMessage(player, "Left your dungeon team!");

        if(dungeonTeamWrapper.getLeader() != null) {
            DungeonUtil.sendUrgentDungeonMessage(dungeonTeamWrapper.getLeader(), player.getName() + " has left your dungeon team!");
        }

        playerToTeam.remove(player.getUniqueId());

    }

    public void kickPlayer(Player leader, Player toKick) {
        DungeonTeamWrapper dungeonTeamWrapper = getDungeonLeaderTeam(leader);
        if(dungeonTeamWrapper == null) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You are not a dungeon team leader!");
            return;
        }
        if(!dungeonTeamWrapper.getTeammates().containsKey(toKick.getUniqueId())) {
            DungeonUtil.sendUrgentDungeonMessage(leader, toKick.getName() + " is not in your dungeon team!");
            return;
        }
        if(toKick == leader) {
            DungeonUtil.sendUrgentDungeonMessage(leader, "You cannot kick yourself!");
            return;
        }

        final Dungeon dungeon = dungeonTeamWrapper.getAttemptingDungeon();

        if(dungeon != null) {
            toKick.teleport(spawnConfig.getOrLoad(false).toBukkit().toCenterLocation());
        }

        dungeonTeamWrapper.getTeammates().remove(toKick.getUniqueId());
        DungeonUtil.sendUrgentDungeonMessage(toKick, "You have been kicked from " + leader.getName() + "'s dungeon team!");
        DungeonUtil.sendDungeonMessage(leader, "You have kicked " + toKick.getName() + " from your dungeon team!");
    }

    public void clearTeam(Player leader) {
        DungeonTeamWrapper dungeonTeamWrapper = getDungeonLeaderTeam(leader);
        leaderToTeam.remove(leader.getUniqueId());
        dungeonTeamWrapper.getTeammates().keySet().forEach(uuid -> {
            playerToTeam.remove(uuid);
        });
        dungeonTeamWrapper.getTeammates().clear();
        teamInviteToPlayer.remove(dungeonTeamWrapper);
    }

    public void clearPlayer(Player player) {
        DungeonTeamWrapper teamWrapper = getPlayersDungeonTeam(player);
        teamWrapper.getTeammates().remove(player.getUniqueId());
        teamWrapper.getTeamPlayers().remove(player);
        playerToTeam.remove(player.getUniqueId());
    }

    public boolean canJoinTeam(Player p, DungeonTeamWrapper teamWrapper) {
        if(!teamInviteToPlayer.containsKey(teamWrapper)) return false;
        final List<UUID> invites = teamInviteToPlayer.get(teamWrapper);
        if(invites == null) return false;
        return invites.contains(p.getUniqueId()) && teamWrapper.getTeammates().size() < MAX_TEAM_PLAYERS;
    }

    public boolean isInSameTeam(Player pOne, Player pTwo) {
        if(getPlayersDungeonTeam(pOne) == null || getPlayersDungeonTeam(pTwo) == null) return false;
        final DungeonTeamWrapper pOneTeam = getPlayersDungeonTeam(pOne);
        final DungeonTeamWrapper pTwoTeam = getPlayersDungeonTeam(pTwo);
        return pOneTeam.getTeamName().equalsIgnoreCase(pTwoTeam.getTeamName());
    }

    public DungeonTeamWrapper getPlayersDungeonTeam(Player player) {
        return playerToTeam.get(player.getUniqueId());
    }

    public Map<String, DungeonTeamWrapper> getDungeonTeams(String dungeon) {
        Dungeon check = dungeonService.getDungeon(dungeon);
        return check.getDungeonWrapper().getDungeonTeams();
    }

    public DungeonTeamWrapper getDungeonTeam(String dungeon, Player teamName) {
        return getDungeonTeams(dungeon).get(teamName.getUniqueId().toString());
    }

    public DungeonTeamWrapper getDungeonLeaderTeam(Player leader) {
        return leaderToTeam.getOrDefault(leader.getUniqueId(), null);
    }

    private UUID selectRandomMember(DungeonTeamWrapper teamWrapper) {
        int random = new Random().nextInt(teamWrapper.getTeamPlayers().size());
        final Player player = teamWrapper.getTeamPlayers().get(random);
        return player.getUniqueId();
    }

    public Collection<DungeonTeamWrapper> getDungeonTeams() {
        return leaderToTeam.values();
    }


}
