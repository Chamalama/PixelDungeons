package mike.pixelDungeons.mdungeons.dungeons.station.entity;

import lombok.Getter;
import lombok.Setter;
import mike.mLibrary.entity.Target;
import mike.pixelDungeons.PixelDungeons;
import mike.pixelDungeons.entity.DungeonEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Warden;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class StationAlien extends DungeonEntity {

    private int tick = 0;
    private final Set<String> exclusions = Set.of("DUNGEON");

    public StationAlien() {
        super(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Rift Alien", EntityType.WARDEN);
        this.attributes = Map.of(
                Attribute.GENERIC_MOVEMENT_SPEED, 0.5,
                Attribute.GENERIC_ATTACK_DAMAGE, 2.0,
                Attribute.GENERIC_FOLLOW_RANGE, 144.0,
                Attribute.GENERIC_MAX_HEALTH, 150.0,
                Attribute.GENERIC_SCALE, 0.5
        );
    }

    @Override
    public void tick() {

    }
}
