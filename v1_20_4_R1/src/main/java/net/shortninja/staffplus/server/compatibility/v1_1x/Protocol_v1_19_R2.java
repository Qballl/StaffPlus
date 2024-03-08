package net.shortninja.staffplus.server.compatibility.v1_1x;

import com.google.gson.JsonElement;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;

import net.minecraft.nbt.CompoundTag;


import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;

import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;

import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.util.Crypt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.shortninja.staffplus.IStaffPlus;
import net.shortninja.staffplus.server.compatibility.AbstractProtocol;
import net.shortninja.staffplus.server.compatibility.IProtocol;
import net.shortninja.staffplus.util.lib.json.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;

import org.bukkit.craftbukkit.v1_19_R2.CraftServer;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;

import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;


import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;

public class Protocol_v1_19_R2 extends AbstractProtocol implements IProtocol {
    public Protocol_v1_19_R2(IStaffPlus staffPlus) {
        super(staffPlus);
    }

    @Override
    public org.bukkit.inventory.ItemStack addNbtString(org.bukkit.inventory.ItemStack item, String value) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);
        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        nbtCompound.putString(NBT_IDENTIFIER, value);
        craftItem.setTag(nbtCompound);

        return CraftItemStack.asCraftMirror(craftItem);
    }

    @Override
    public String getNbtString(org.bukkit.inventory.ItemStack item) {
        ItemStack craftItem = CraftItemStack.asNMSCopy(item);

        if (craftItem == null) {
            return "";
        }

        CompoundTag nbtCompound = craftItem.getTag() == null ? new CompoundTag() : craftItem.getTag();

        return nbtCompound.getString(NBT_IDENTIFIER);
        //return item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey("StaffPlus","staff"),PersistentDataType.STRING);
    }

    @Override
    public void registerCommand(String match, Command command) {
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(match, command);
    }




    @Override
    public String getSound(Object object) {
        return null;
    }

    @Override
    public void listVanish(Player player, boolean shouldEnable) {
        Packet packet = null;
        CraftPlayer cp = (CraftPlayer)player;
        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.add(player.getUniqueId());
        if (shouldEnable) {
            packet = new ClientboundPlayerInfoRemovePacket(uuids);
        } else {
            packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, cp.getHandle());
            sendGlobalPacket(packet);
            packet = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, cp.getHandle());
            sendGlobalPacket(packet);
        }

        sendGlobalPacket(packet);
    }

    @Override
    public void sendHoverableJsonMessage(Set<Player> players, String message, String hoverMessage) {
        JsonMessage json = new JsonMessage().append(message).setHoverAsTooltip(hoverMessage).save();
        ClientboundSystemChatPacket packet = new ClientboundSystemChatPacket(Component.Serializer.fromJson(json.getMessage()), false);

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().connection.connection.send(packet);
        }
    }


    private void sendGlobalPacket(Packet<?> packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.connection.send(packet);

        }
    }

    @Override
    public void inject(Player player) {
        final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().connection.connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getUniqueId().toString(), new PacketHandler_v1_19_R2(player));
    }

    @Override
    public void uninject(Player player) {
        final Channel channel = ((CraftPlayer) player).getHandle().connection.connection.channel;
        channel.eventLoop().submit(() -> channel.pipeline().remove(player.getUniqueId().toString()));
    }
}