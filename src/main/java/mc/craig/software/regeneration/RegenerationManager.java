package mc.craig.software.regeneration;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class RegenerationManager {
    public static boolean isRegenerating(Player player) {
        return player.getMetadata("isRegenerating").get(0).asBoolean();
    }

    public static void setRegenerating(Player player, boolean value) {
        player.setMetadata("isRegenerating", new FixedMetadataValue(Regeneration.getInstance(), value));
    }

    public static int getRegenerationsLeft(Player player) {
        return player.getMetadata("regenerationsLeft").get(0).asInt();
    }

    public static void setRegenerationsLeft(Player player, int value) {
        player.setMetadata("regenerationsLeft", new FixedMetadataValue(Regeneration.getInstance(), value));
    }

    public static void decrementRegenerationsLeft(Player player) {
        int regenerationsLeft = getRegenerationsLeft(player);
        if (regenerationsLeft > 0) {
            setRegenerationsLeft(player, regenerationsLeft - 1);
        }
    }
}
