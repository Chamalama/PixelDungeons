package mike.pixelDungeons.dungeon.gui.listener;

import gg.supervisor.core.annotation.Component;
import mike.pixelDungeons.dungeon.DungeonUtil;
import mike.pixelDungeons.dungeon.gui.AvailableDungeonGUI;
import mike.pixelDungeons.dungeon.storage.DungeonGUIStorage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Component
public class DungeonGUIListener implements Listener {

    private final DungeonGUIStorage dungeonGUIStorage;
    private final AvailableDungeonGUI availableDungeonGUI;

    public DungeonGUIListener(DungeonGUIStorage dungeonGUIStorage, AvailableDungeonGUI availableDungeonGUI) {
        this.dungeonGUIStorage = dungeonGUIStorage;
        this.availableDungeonGUI = availableDungeonGUI;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!event.getView().getTitle().equalsIgnoreCase(dungeonGUIStorage.getOrLoad(false).getTitle())) return;
        event.setCancelled(true);

        final Player player = (Player) event.getWhoClicked();

        final ItemStack clickedItem = event.getCurrentItem();

        if(clickedItem == null) return;

        if(clickedItem.getType() == Material.IRON_BARS) {
            player.playSound(player, Sound.ENTITY_BAT_TAKEOFF, 1.0F, 1.0F);
            player.openInventory(availableDungeonGUI.getInventory());
            DungeonUtil.sendDungeonMessage(player, "Viewing available dungeons...");
        }

    }

}
