package mike.pixelDungeons.service;

import gg.supervisor.core.annotation.Component;
import mike.mLibrary.item.ItemBuilder;
import mike.pixelDungeons.PixelDungeons;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.profile.PlayerTextures;

@Component
public class DungeonDeathService {

    public void spawnPlayerRevive(Player player, Location location) {
        final Block block = location.getBlock().getRelative(BlockFace.UP);
        block.setType(Material.PLAYER_HEAD);
        block.setMetadata("REVIVE", new FixedMetadataValue(PixelDungeons.get(), player.getUniqueId().toString()));
        Skull skull = (Skull) block.getState();
        skull.setOwningPlayer(player);
        skull.update();
    }

}
