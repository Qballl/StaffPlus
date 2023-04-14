package net.shortninja.staffplus.server.nms;

import com.comphenix.protocol.ProtocolManager;
import net.shortninja.staffplus.IStaffPlus;
import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.server.compatibility.AbstractProtocol;
import net.shortninja.staffplus.util.lib.json.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;

public class NewProtocol extends AbstractProtocol {

    private ProtocolManager manager;

    public NewProtocol(IStaffPlus staffPlus) {
        super(staffPlus);

        this.manager = StaffPlus.get().protocolManager;
    }

    @Override
    public void listVanish(Player player, boolean shouldEnable) {

    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();

        for (Player player : players) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + player.getName() + " " + json.getMessage());
        }
    }
}
