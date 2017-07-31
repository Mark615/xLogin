package de.mark615.xsignin.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XPlayerSubject;
import de.mark615.xsignin.object.XUtil;

public class EventListener implements Listener
{
	private XSignIn plugin;

	public EventListener(XSignIn instance)
	{
		this.plugin = instance;
		registerTask();
	}

	
	public void messageAllPlayer()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			plugin.getLoginManager().getPlayer(p.getUniqueId()).setLastLoginInfo(0);
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		this.plugin.getLoginManager().registerPlayer(e.getPlayer());
		plugin.getLoginManager().getPlayer(e.getPlayer().getUniqueId()).setLastLoginInfo(0);
		
		if (plugin.isMaintenanceMode() && !e.getPlayer().hasPermission("xsignin.xmaintenance.join"))
		{
			e.getPlayer().kickPlayer(XUtil.getMessage("message.maintenance"));
			return;
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		this.plugin.getLoginManager().unregisterPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlayeEvent(BlockPlaceEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}

	@EventHandler
	public void PlayerDropEvent(PlayerDropItemEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	
	
	private boolean checkLoggedIn(Player p)
	{
		boolean value = false;
		if (p != null)
		{
			if (this.plugin.getLoginManager().isPlayerLoggedIn(p))
			{
				if (this.plugin.getLoginManager().getAGBManager().hasXPlayerAcceptAGB(p))
					value = true;
			}
		}
		return value;
	}
	
	private void registerTask()
	{
		Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
			
			@Override
			public void run()
			{
				for (Player p : Bukkit.getServer().getOnlinePlayers())
				{
					XPlayerSubject subject = plugin.getLoginManager().getPlayer(p.getUniqueId());
					if (System.currentTimeMillis() - subject.getLastLoginInfo() > plugin.getSettingManager().getLoginMessageIntervall())
					{
						if (!plugin.getLoginManager().hasAccount(p))
						{
							XUtil.sendFileMessage(p, "message.register", true);
							subject.setLastLoginInfo(System.currentTimeMillis());
						}
						else
						if (!subject.isLoggedIn())
						{
							XUtil.sendFileMessage(p, "message.login", true);
							subject.setLastLoginInfo(System.currentTimeMillis());
						}
						else
						if (!subject.hasAGBAccepted())
						{
							XUtil.sendFileMessage(p, "message.agb", true);
							subject.setLastLoginInfo(System.currentTimeMillis());
						}
					}
				}
			}
		}, 1, 4);
	}
	
	
}