package net.shortninja.staffplus.server.nms.nbt;

import de.tr7zw.nbtapi.NBTItem;
import net.shortninja.staffplus.server.compatibility.IItemHandler;
import org.bukkit.inventory.ItemStack;

public class NBTHandler implements IItemHandler {
    @Override
    public ItemStack addNbtString(ItemStack item, String value) {
        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString(NBT_IDENTIFIER, value);
        return nbtItem.getItem();
    }

    @Override
    public String getNbtString(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        return nbtItem.getString(NBT_IDENTIFIER);
    }
}
