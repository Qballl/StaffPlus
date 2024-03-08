package net.shortninja.staffplus.player.attribute.gui;

import net.shortninja.staffplus.StaffPlus;

import net.shortninja.staffplus.player.User;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.player.attribute.infraction.InfractionCoordinator;
import net.shortninja.staffplus.player.attribute.infraction.Warning;
import net.shortninja.staffplus.player.attribute.mode.handler.FreezeHandler;
import net.shortninja.staffplus.player.attribute.mode.handler.GadgetHandler;
import net.shortninja.staffplus.server.data.config.Messages;
import net.shortninja.staffplus.server.data.config.Options;
import net.shortninja.staffplus.unordered.IAction;
import net.shortninja.staffplus.unordered.IReport;
import net.shortninja.staffplus.unordered.IUser;
import net.shortninja.staffplus.util.MessageCoordinator;
import net.shortninja.staffplus.util.lib.JavaUtils;
import net.shortninja.staffplus.util.lib.hex.Items;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExamineGui extends AbstractGui {

    private static final int SIZE = 54;
    private MessageCoordinator message = StaffPlus.get().message;

    private UserManager userManager = StaffPlus.get().userManager;
    private FreezeHandler freezeHandler = StaffPlus.get().freezeHandler;
    private GadgetHandler gadgetHandler = StaffPlus.get().gadgetHandler;
    private InfractionCoordinator infractionCoordinator = StaffPlus.get().infractionCoordinator;

    public ExamineGui(Player player, Player targetPlayer, String title) {
        super(SIZE, title);

        setInventoryContents(targetPlayer);
        Options options = StaffPlus.get().options;
        Messages messages = StaffPlus.get().messages;
        if (options.modeExamineFood >= 0) {
            setItem(options.modeExamineFood, foodItem(targetPlayer), null);
        }

        if (options.modeExamineIp >= 0) {
            setItem(options.modeExamineIp, ipItem(targetPlayer), null);
        }


//        if (options.modeExaminePing >= 0) {
//            setItem(options.modeExaminePing, pingItem(targetPlayer), null);
//        }

        if (options.modeExamineGamemode >= 0) {
            setItem(options.modeExamineGamemode, gameModeItem(targetPlayer), null);
        }

        if (options.modeExamineInfractions >= 0) {
            setItem(options.modeExamineInfractions, infractionsItem(userManager.get(targetPlayer.getUniqueId())), null);
        }

        setInteractiveItems(targetPlayer);
        player.openInventory(getInventory());
        userManager.get(player.getUniqueId()).setCurrentGui(this);
    }

    private void setInventoryContents(Player targetPlayer) {
        ItemStack[] items = targetPlayer.getInventory().getContents();
        ItemStack[] armor = targetPlayer.getInventory().getArmorContents();

        JavaUtils.reverse(armor);
        for (int i = 0; i < items.length; i++) {
            setItem(i, items[i], null);
        }
        for (int i = 0; i <= armor.length - 1; i++)
        {
            if (i == 3) {
                setItem(39 + i, targetPlayer.getItemInHand(), null);
            }
            setItem(38 + i, armor[i], null);
        }
    }

    private void setInteractiveItems(final Player targetPlayer) {
        Options options = StaffPlus.get().options;
        Messages messages = StaffPlus.get().messages;
        if (options.modeExamineLocation >= 0) {
            setItem(options.modeExamineLocation, locationItem(targetPlayer), new IAction() {
                @Override
                public void click(Player player, ItemStack item, int slot) {
                    player.teleport(targetPlayer);
                }

                @Override
                public boolean shouldClose() {
                    return true;
                }

                @Override
                public void execute(Player player, String input) {
                }
            });
        }

        if (options.modeExamineNotes >= 0) {
            setItem(options.modeExamineNotes, notesItem(userManager.get(targetPlayer.getUniqueId())), new IAction() {
                @Override
                public void click(Player player, ItemStack item, int slot) {
                    IUser user = userManager.get(player.getUniqueId());

                    message.send(player, messages.typeInput, messages.prefixGeneral);

                    user.setQueuedAction(new IAction() {
                        @Override
                        public void execute(Player player, String input) {
                            userManager.get(targetPlayer.getUniqueId()).addPlayerNote("&7" + input);
                            message.send(player, messages.inputAccepted, messages.prefixGeneral);
                        }

                        @Override
                        public boolean shouldClose() {
                            return true;
                        }

                        @Override
                        public void click(Player player, ItemStack item, int slot) {
                        }
                    });
                }

                @Override
                public boolean shouldClose() {
                    return true;
                }

                @Override
                public void execute(Player player, String input) {
                }
            });
        }

        if (options.modeExamineFreeze >= 0) {
            setItem(options.modeExamineFreeze, freezeItem(targetPlayer), new IAction() {
                @Override
                public void click(Player player, ItemStack item, int slot) {
                    gadgetHandler.onFreeze(player, targetPlayer);
                }

                @Override
                public boolean shouldClose() {
                    return true;
                }

                @Override
                public void execute(Player player, String input) {
                }
            });
        }

        if (options.modeExamineWarn >= 0) {
            setItem(options.modeExamineWarn, warnItem(), new IAction() {
                @Override
                public void click(Player player, ItemStack item, int slot) {
                    IUser user = userManager.get(player.getUniqueId());

                    message.send(player, messages.typeInput, messages.prefixGeneral);

                    user.setQueuedAction(new IAction() {
                        @Override
                        public void execute(Player player, String input) {
                            UUID uuid = targetPlayer.getUniqueId();

                            infractionCoordinator.sendWarning(player, new Warning(uuid, targetPlayer.getName(), input, player.getName(), player.getUniqueId(), System.currentTimeMillis()));
                            message.send(player, messages.inputAccepted, messages.prefixGeneral);
                        }

                        @Override
                        public boolean shouldClose() {
                            return true;
                        }

                        @Override
                        public void click(Player player, ItemStack item, int slot) {
                        }
                    });
                }

                @Override
                public boolean shouldClose() {
                    return true;
                }

                @Override
                public void execute(Player player, String input) {
                }
            });
        }
    }

    private ItemStack foodItem(Player player) {
        Options options = StaffPlus.get().options;
        Messages messages = StaffPlus.get().messages;
        int healthLevel = (int) player.getHealth();
        int foodLevel = player.getFoodLevel();
        List<String> lore = new ArrayList<String>();

        for (String string : messages.examineFood) {
            lore.add(string.replace("%health%", healthLevel + "/20").replace("%hunger%", foodLevel + "/20"));
        }

        ItemStack item = Items.builder()
                .setMaterial(Material.BREAD).setAmount(1)
                .setName("&bFood")
                .setLore(lore)
                .build();

        return item;
    }

    private ItemStack ipItem(Player player) {
        String ip = player.hasPermission(StaffPlus.get().options.ipHidePerm) ? "127.0.0.1" : player.getAddress().getAddress().getHostAddress().replace("/", "");

        ItemStack item = Items.builder()
                .setMaterial(Material.COMPASS).setAmount(1)
                .setName("&bConnection")
                .addLore(StaffPlus.get().messages.examineIp.replace("%ipaddress%", ip))
                .build();

        return item;
    }

    private ItemStack pingItem(Player player) {
        ItemStack item = Items.builder()
                .setMaterial(Material.PAPER).setAmount(1)
                .setName("&bPing")
                .addLore(StaffPlus.get().messages.examineIp.replace("%ping%", String.valueOf(User.getPing(player))))
                .build();

        return item;
    }

    private ItemStack gameModeItem(Player player) {
        ItemStack item = Items.builder()
                .setMaterial(Material.GRASS_BLOCK).setAmount(1)
                .setName("&bGamemode")
                .addLore(StaffPlus.get().messages.examineGamemode.replace("%gamemode%", player.getGameMode().toString()))
                .build();

        return item;
    }

    private ItemStack infractionsItem(IUser user) {
        List<String> lore = new ArrayList<String>();
        IReport latestReport = user.getReports().size() >= 1 ? user.getReports().get(user.getReports().size() - 1) : null;
        String latestReason = latestReport == null ? "null" : latestReport.getReason();

        for (String string : StaffPlus.get().messages.infractionItem) {
            lore.add(string.replace("%warnings%", Integer.toString(user.getWarnings().size())).replace("%reports%", Integer.toString(user.getReports().size())).replace("%reason%", latestReason));
        }

        ItemStack item = Items.builder()
                .setMaterial(Material.BOOK).setAmount(1)
                .setName("&bInfractions")
                .setLore(lore)
                .build();

        return item;
    }

    private ItemStack locationItem(Player player) {
        Location location = player.getLocation();

        ItemStack item = Items.builder()
                .setMaterial(Material.MAP).setAmount(1)
                .setName("&bLocation")
                .addLore(StaffPlus.get().messages.examineLocation.replace("%location%", location.getWorld().getName() + " &8� &7" + JavaUtils.serializeLocation(location)))
                .build();

        return item;
    }

    private ItemStack notesItem(IUser user) {
        List<String> notes = user.getPlayerNotes().isEmpty() ? Arrays.asList("&7No notes found") : user.getPlayerNotes();

        ItemStack item = Items.builder()
                .setMaterial(Material.MAP).setAmount(1)
                .setName(StaffPlus.get().messages.examineNotes)
                .setLore(notes)
                .build();

        return item;
    }

    private ItemStack freezeItem(Player player) {
        String frozenStatus = freezeHandler.isFrozen(player.getUniqueId()) ? "" : "not ";

        ItemStack item = Items.builder()
                .setMaterial(Material.BLAZE_ROD).setAmount(1)
                .setName("&bFreeze player")
                .setLore(Arrays.asList(StaffPlus.get().messages.examineFreeze, "&7Currently " + frozenStatus + "frozen."))
                .build();

        return item;
    }

    private ItemStack warnItem() {
        ItemStack item = Items.builder()
                .setMaterial(Material.PAPER).setAmount(1)
                .setName("&bWarn player")
                .addLore(StaffPlus.get().messages.examineWarn)
                .build();

        return item;
    }
}