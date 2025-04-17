package mike.pixelDungeons.dungeon.gui;

import mike.mLibrary.data.Data;
import mike.mLibrary.gui.BaseGUI;
import mike.mLibrary.item.ItemBuilder;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.dungeon.storage.DungeonGUIStorage;
import mike.pixelDungeons.dungeon.storage.DungeonPlayerStorage;
import mike.pixelDungeons.dungeon.storage.StoredDungeonPlayer;
import mike.pixelDungeons.wrapper.DungeonTeamWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.profile.PlayerTextures;

import java.util.*;

public class DungeonGUI extends BaseGUI {

    private final DungeonGUIStorage dungeonGUIStorage;
    private final DungeonTeamWrapper dungeonTeamWrapper;
    private final DungeonPlayerStorage dungeonPlayerStorage;

    private final int leaderSlot = 4;

    private static final NamespacedKey headKey = new NamespacedKey(PixelDungeons.get(), "DUNGEON");

    private final List<Integer> displaySlots = List.of(11, 12, 13, 14, 15);

    public DungeonGUI(DungeonGUIStorage dungeonGUIStorage, DungeonTeamWrapper dungeonTeamWrapper, DungeonPlayerStorage dungeonPlayerStorage) {
        super(PixelDungeons.get(), dungeonGUIStorage.getOrLoad(false).getTitle(), 27, new HashMap<>(), dungeonGUIStorage.getOrLoad(false).getFillerMaterial(), false);
        this.dungeonGUIStorage = dungeonGUIStorage;
        this.dungeonTeamWrapper = dungeonTeamWrapper;
        this.dungeonPlayerStorage = dungeonPlayerStorage;
        generateHeadDisplays();
        this.getInventory().setItem(8, ItemBuilder.build(
                Material.IRON_BARS,
                ChatColor.GOLD + ChatColor.BOLD.toString() + "Dungeons",
                List.of(ChatColor.WHITE + "Click to view the available dungeons.")));
    }

    public void generateHeadDisplays() {

        if(dungeonTeamWrapper == null) return;

        final Set<UUID> teammates = dungeonTeamWrapper.getTeammates().keySet();

        for(UUID uuid : teammates) {

            final Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;
            final PlayerTextures textures = player.getPlayerProfile().getTextures();
            if(textures.getSkin() == null) continue;

            final StoredDungeonPlayer dungeonPlayer = dungeonPlayerStorage.getOrLoad(player.getUniqueId());

            final List<String> headLore = new ArrayList<>();

            final String url = textures.getSkin().toString();

            headLore.addAll(List.of(
                ChatColor.GOLD + "Position: " + positionString(dungeonTeamWrapper.getTeammates().get(uuid).getPlayerPosition()),
                ChatColor.GOLD + "Dungeons Completed: " + ChatColor.WHITE + dungeonPlayer.getDungeonsCompleted(),
                ChatColor.GOLD + "Dungeons Failed: " + ChatColor.WHITE + dungeonPlayer.getDungeonsFailed(),
                ChatColor.GOLD + "Dungeon Bosses Killed: " + ChatColor.WHITE + dungeonPlayer.getDungeonBossesKilled(),
                ChatColor.GOLD + "Dungeon Deaths: " + ChatColor.WHITE + dungeonPlayer.getDungeonDeaths()
            ));

            final ItemStack headDisplay = ItemBuilder.buildSkull(url, ChatColor.YELLOW + player.getName(), headLore, headKey, new Data<>());

            if(getFirstSlot() != -1 && dungeonTeamWrapper.getLeader() != player) {
                this.getInventory().setItem(getFirstSlot(), headDisplay);
            }

            if(dungeonTeamWrapper.getLeader() == player) {
                this.getInventory().setItem(leaderSlot, headDisplay);
            }

        }

    }

    private int getFirstSlot() {
        for(int i : displaySlots) {
            final ItemStack inSlot = this.getInventory().getItem(i);
            if(inSlot != null && inSlot.getType() == this.getFiller()) {
                return i;
            }
        }
        return -1;
    }

    private String positionString(int position) {
        return position == 1 ? ChatColor.GREEN + "Leader" : ChatColor.GREEN + "Member";
    }




}
