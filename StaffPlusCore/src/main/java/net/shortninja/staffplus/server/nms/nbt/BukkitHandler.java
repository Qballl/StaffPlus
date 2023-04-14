package net.shortninja.staffplus.server.nms.nbt;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.compatibility.IItemHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BukkitHandler implements IItemHandler {
    @Override
    public ItemStack addNbtString(ItemStack item, String value) {

        // Get the metadata
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        // Get the namespaced key - if it doesn't work out, stop there
        NamespacedKey key = NamespacedKey.fromString(NBT_IDENTIFIER.toLowerCase(), StaffPlus.get());
        if (key == null) return item;

        // Get the data container and namespaced key
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getNbtString(ItemStack item) {
        // Get the metadata
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "";

        // Get the namespaced key - if it doesn't work out, stop there
        NamespacedKey key = NamespacedKey.fromString(NBT_IDENTIFIER.toLowerCase(), StaffPlus.get());
        if (key == null) return "";

        // Get the data container and namespaced key
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.get(key, PersistentDataType.STRING);
    }
}
