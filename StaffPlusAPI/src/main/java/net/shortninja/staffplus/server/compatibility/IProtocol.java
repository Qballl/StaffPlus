package net.shortninja.staffplus.server.compatibility;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public interface IProtocol {

    void listVanish(Player player, boolean shouldEnable);

    void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage);

}