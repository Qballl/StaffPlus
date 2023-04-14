package net.shortninja.staffplus.server.compatibility;

import org.bukkit.inventory.ItemStack;

public interface IItemHandler {
    String NBT_IDENTIFIER = "StaffPlus";

    ItemStack addNbtString(ItemStack item, String value);

    String getNbtString(ItemStack item);
}
