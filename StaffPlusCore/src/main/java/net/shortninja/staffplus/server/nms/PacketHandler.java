package net.shortninja.staffplus.server.nms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import net.shortninja.staffplus.IStaffPlus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.annotation.Nonnull;

public class PacketHandler extends PacketAdapter {

    public PacketHandler(@Nonnull Plugin plugin) {
        super(
                plugin,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.NAMED_SOUND_EFFECT
        );
    }

    @Override
    public void onPacketSending(PacketEvent event) {

        RegisteredServiceProvider<IStaffPlus> provider = Bukkit.getServicesManager().getRegistration(IStaffPlus.class);
        if (provider == null) return;

        IStaffPlus api = provider.getProvider();
        if (api.getUserManager().get(event.getPlayer().getUniqueId()).isVanished()) event.setCancelled(true);
    }
}
