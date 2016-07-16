package net.shortninja.staffplus.listener;

import net.shortninja.staffplus.StaffPlus;
import net.shortninja.staffplus.data.config.Options;
import net.shortninja.staffplus.player.User;
import net.shortninja.staffplus.player.UserManager;
import net.shortninja.staffplus.player.attribute.gui.FreezeGui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClose implements Listener
{
	private Options options = StaffPlus.get().options;
	private UserManager userManager = StaffPlus.get().userManager;
	
	public InventoryClose()
	{
		Bukkit.getPluginManager().registerEvents(this, StaffPlus.get());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClose(InventoryCloseEvent event)
	{
		final Player player = (Player) event.getPlayer();
		User user = userManager.getUser(player.getUniqueId());
		
		if(user.isFrozen())
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					new FreezeGui(player, options.modeFreezePromptTitle);					
				}
			}.runTaskLater(StaffPlus.get(), 1L);
			return;
		}
		
		if(user.getCurrentGui() != null)
		{
			user.setCurrentGui(null);
		}
	}
}