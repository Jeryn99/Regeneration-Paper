package mc.craig.software.regeneration;

import mc.craig.software.regeneration.skin.PlayerSkinStorage;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class RegenerationManager {
    public static boolean isRegenerating(Player player) {
        return player.getMetadata("isRegenerating").get(0).asBoolean();
    }

    public static void initPlayer(Player player) {
        if (!player.hasMetadata("isRegenerating")) {
            player.setMetadata("isRegenerating", new FixedMetadataValue(Regeneration.getInstance(), false));
        }

        if (!player.hasMetadata("regenerationsLeft")) {
            player.setMetadata("regenerationsLeft", new FixedMetadataValue(Regeneration.getInstance(), 0));
        }

        String url = Regeneration.PLAYER_SKIN_STORAGE.getSkinURL(String.valueOf(player.getUniqueId()));
        Regeneration.changePlayersSkin(player, url);

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
