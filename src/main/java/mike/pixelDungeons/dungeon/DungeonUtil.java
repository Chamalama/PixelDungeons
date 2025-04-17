package mike.pixelDungeons.dungeon;

import gg.supervisor.util.chat.Text;
import org.bukkit.entity.Player;

public class DungeonUtil {

    public static void sendDungeonMessage(Player player, String message) {
        player.sendMessage(Text.translate("<gold><b>(DUNGEON) </b><yellow>" + message));
    }

    public static void sendUrgentDungeonMessage(Player player, String message) {
        player.sendMessage(Text.translate("<gold><b>(DUNGEON) </b><red>" + message));
    }

}
