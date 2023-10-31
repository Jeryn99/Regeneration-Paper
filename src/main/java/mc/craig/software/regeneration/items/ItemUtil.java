package mc.craig.software.regeneration.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ItemUtil {

    public static ItemStack createFobWatch() {
        ItemStack fobWatch = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = fobWatch.getItemMeta();
        itemMeta.setDisplayName("Fob Watch");
        itemMeta.setLore(Arrays.asList("omg timelord."));
        itemMeta.setCustomModelData(666);
        itemMeta.removeItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON);
        fobWatch.setItemMeta(itemMeta);
        return fobWatch;
    }

}
