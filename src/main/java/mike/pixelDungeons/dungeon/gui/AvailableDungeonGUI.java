package mike.pixelDungeons.dungeon.gui;

import com.google.gson.annotations.Expose;
import gg.supervisor.core.annotation.Component;
import mike.mLibrary.gui.BaseGUI;
import mike.mLibrary.item.ItemBuilder;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.Dungeon;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.service.DungeonPlayerService;
import mike.pixelDungeons.service.DungeonService;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AvailableDungeonGUI extends BaseGUI {

    private final DungeonService dungeonService;
    private final DungeonPlayerService dungeonPlayerService;

    private final Map<Integer, Dungeon> slotToDungeon = new HashMap<>();

    public AvailableDungeonGUI(DungeonService dungeonService, DungeonPlayerService dungeonPlayerService) {
        super(PixelDungeons.get(), "Dungeons", 27, new HashMap<>(), Material.ORANGE_STAINED_GLASS_PANE, false);
        this.dungeonService = dungeonService;
        this.dungeonPlayerService = dungeonPlayerService;

        Bukkit.getScheduler().runTaskTimerAsynchronously(PixelDungeons.get(), () -> {

            for(Dungeon dungeon : dungeonService.getKeyToDungeon().values()) {
                int slot = getFirstEmptySlot();
                if(slot == -1) continue;
                if(slotToDungeon.containsValue(dungeon)) continue;
                slotToDungeon.put(slot, dungeon);
            }

            for(int slots : slotToDungeon.keySet()) {
                this.getInventory().setItem(slots, ItemBuilder.build(Material.ENDER_EYE, ChatColor.GOLD + ChatColor.BOLD.toString() + slotToDungeon.get(slots).getDungeonWrapper().getDungeonName(), dungeonPlayersLore(slotToDungeon.get(slots))));
            }

        }, 0L, 100L);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equalsIgnoreCase(this.getInventoryTitle())) return;
        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();

        if(event.getClickedInventory() == player.getInventory()) return;

        final DungeonTeamWrapper teamWrapper = dungeonPlayerService.getPlayersDungeonTeam(player);

        if(teamWrapper == null) {
            DungeonUtil.sendUrgentDungeonMessage(player, "You must be a Dungeon Team Leader to do this!");
            return;
        }

        if(teamWrapper.getLeader() != player) {
            DungeonUtil.sendUrgentDungeonMessage(player, "Only the Dungeon Team Leader can start a dungeon!");
            return;
        }

        int slot = event.getSlot();

        final Dungeon dungeon = slotToDungeon.get(slot);

        if(dungeon == null) return;

        player.teleport(dungeon.getDungeonWrapper().getDungeonSpawnLocation().toBukkit().toCenterLocation());

    }

    private int getFirstEmptySlot() {
        for(int i = 0; i < this.getInventory().getSize(); i++) {
            final ItemStack stack = this.getInventory().getItem(i);
            if(stack != null && stack.getType() == Material.ORANGE_STAINED_GLASS_PANE) {
                return i;
            }
        }
        return -1;
    }

    private List<String> dungeonPlayersLore(Dungeon dungeon) {
        final List<String> dungeonItemLore = new ArrayList<>();

        for(String loreString : dungeon.getDungeonWrapper().getLore()) {
            loreString = ChatColor.GRAY + loreString;
            dungeonItemLore.add(loreString);
        }

        dungeonItemLore.add("");
        dungeonItemLore.add(ChatColor.WHITE + ChatColor.BOLD.toString() + "Dungeon Teams:");
        final Map<String, DungeonTeamWrapper> teams = dungeon.getDungeonWrapper().getDungeonTeams();

        if(teams.isEmpty()) {
            dungeonItemLore.add(ChatColor.RED + "No active dungeon teams!");
        }else {
            for (DungeonTeamWrapper wrapper : teams.values()) {
                dungeonItemLore.add(ChatColor.WHITE + ChatColor.BOLD.toString() + "* " + ChatColor.WHITE + wrapper.getLeader().getName() + "'s team");
                if(wrapper.getCurrentDungeonRoom() != null) {
                    dungeonItemLore.add(ChatColor.WHITE + "   (Room: " + ChatColor.RED + wrapper.getCurrentDungeonRoom().getRoomRegion().getRegionName() + ChatColor.WHITE + ")");
                }
            }
        }

        return dungeonItemLore;
    }

}
