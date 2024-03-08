package net.shortninja.staffplus.server.compatibility.v1_1x;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.shortninja.staffplus.IStaffPlus;
import net.shortninja.staffplus.server.compatibility.AbstractPacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class PacketHandler_v1_19_R2 extends AbstractPacketHandler {

    public PacketHandler_v1_19_R2(Player player) {
        super(player);
    }

    @Override
    public boolean onSend(ChannelHandlerContext context, Object o, ChannelPromise promise) throws Exception {
        if (o instanceof ClientboundSoundPacket) {
            RegisteredServiceProvider<IStaffPlus> provider = Bukkit.getServicesManager().getRegistration(IStaffPlus.class);
            if (provider != null) {
                return !provider.getProvider().getUserManager().get(player.getUniqueId()).isVanished();
            }
        }

        return true;
    }

    @Override
    public boolean onReceive(ChannelHandlerContext context, Object o) throws Exception {
        return true;
    }
}
