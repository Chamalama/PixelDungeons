package mike.pixelDungeons.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import gg.supervisor.core.annotation.Component;
import mike.mLibrary.text.Message;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.dungeon.gui.DungeonGUI;
import mike.pixelDungeons.dungeon.storage.DungeonGUIStorage;
import mike.pixelDungeons.dungeon.storage.DungeonPlayerStorage;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.service.DungeonService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import mike.pixelDungeons.wrapper.DungeonWrapper;
import org.bukkit.entity.Player;

@Component
@CommandAlias("dungeon|d")
public class DungeonCMD extends BaseCommand {

    private final DungeonService dungeonService;
    private final DungeonPlayerService dungeonPlayerService;
    private final DungeonGUIStorage dungeonGUIStorage;
    private final DungeonPlayerStorage dungeonPlayerStorage;

    public DungeonCMD(PaperCommandManager paperCommandManager, DungeonService dungeonService, DungeonPlayerService dungeonPlayerService, DungeonGUIStorage dungeonGUIStorage, DungeonPlayerStorage dungeonPlayerStorage) {
        this.dungeonService = dungeonService;
        this.dungeonPlayerService = dungeonPlayerService;
        this.dungeonGUIStorage = dungeonGUIStorage;
        this.dungeonPlayerStorage = dungeonPlayerStorage;
        paperCommandManager.registerCommand(this);
    }

    @Default
    public void onDefault(Player player) {
        player.openInventory(new DungeonGUI(dungeonGUIStorage, dungeonPlayerService.getDungeonLeaderTeam(player), dungeonPlayerStorage).getInventory());
    }

    @Subcommand("team create")
    @CommandCompletion("name")
    public void onTeamCreate(Player player) {
        dungeonPlayerService.createTeam(player);
    }

    @Subcommand("team disband")
    public void onDisband(Player player) {
        if(!dungeonPlayerService.getLeaderToTeam().containsKey(player.getUniqueId())) {
            DungeonUtil.sendUrgentDungeonMessage(player, "You are not a dungeon team leader!");
            return;
        }
        dungeonPlayerService.disbandTeam(player);
    }

    @Subcommand("team invite")
    @CommandCompletion("@players")
    public void onInvite(Player player, OnlinePlayer onlinePlayer) {
        dungeonPlayerService.invitePlayer(player, onlinePlayer.getPlayer());
    }

    @Subcommand("team leave")
    public void onLeave(Player player) {
        dungeonPlayerService.leaveTeam(player);
    }

    @Subcommand("team kick")
    @CommandCompletion("@players")
    public void onKick(Player player, OnlinePlayer onlinePlayer) {
        dungeonPlayerService.kickPlayer(player, onlinePlayer.getPlayer());
    }

    @Subcommand("team join")
    @CommandCompletion("@players")
    public void onJoin(Player player, OnlinePlayer onlinePlayer) {
        final DungeonTeamWrapper dungeonTeamWrapper = dungeonPlayerService.getDungeonLeaderTeam(onlinePlayer.getPlayer());
        dungeonPlayerService.joinTeam(dungeonTeamWrapper, player);
    }

    @Subcommand("reload")
    @CommandPermission("admin.dungeon.cmd")
    public void onReload(Player player) {
        for(Dungeon dungeon : dungeonService.getAll()) {
            dungeon.updateConfig();
            final DungeonWrapper memoryWrapper = dungeon.getDungeonWrapper();
            final DungeonWrapper configWrapper = dungeon.getOrLoad(false);
            memoryWrapper.setLore(configWrapper.getLore());
            memoryWrapper.setEndLore(configWrapper.getEndLore());
            memoryWrapper.setDungeonSpawnLocation(configWrapper.getDungeonSpawnLocation());
        }
        Message.message(player, "Reloaded dungeon configs!");
    }

}
