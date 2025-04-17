package mike.pixelDungeons.entity;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import lombok.Getter;
import lombok.Setter;
import mike.mLibrary.entity.AttributeUtil;
import mike.mLibrary.text.Chat;
import mike.pixelDungeons.PixelDungeons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Warden;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public abstract class DungeonEntity {

    private String name;
    protected Mob dungeonEntity;
    protected EntityType entityType;
    protected Map<Attribute, Double> attributes;

    public DungeonEntity(String name, EntityType entityType) {
        this.name = name;
        this.entityType = entityType;
        this.attributes = new HashMap<>();
    }

    public void spawn(Location location) {
        dungeonEntity = (Mob) location.getWorld().spawnEntity(location, entityType);
        dungeonEntity.setCustomNameVisible(true);
        dungeonEntity.customName(Chat.translate(name));

        AttributeUtil.setAttributes(dungeonEntity, attributes);
    }

    public abstract void tick();

}
