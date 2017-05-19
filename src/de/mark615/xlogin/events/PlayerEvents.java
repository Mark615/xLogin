package de.mark615.xlogin.events;

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

import de.mark615.xlogin.XLogin;
import de.mark615.xlogin.object.XPlayerSubject;
import de.mark615.xlogin.object.XUtil;

public class PlayerEvents implements Listener
{
	private XLogin plugin;

	public PlayerEvents(XLogin instance)
	{
		this.plugin = instance;
		registerTask();
	}

	
	public void messageAllPlayer()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			plugin.getLoginManager().getPlayer(p.getUniqueId()).setLastMessage(0);
		}
	}
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		plugin.getLoginManager().getPlayer(e.getPlayer().getUniqueId()).setLastMessage(0);
		this.plugin.getLoginManager().registerPlayer(e.getPlayer());
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
	
	public void onPlayerInteractEvent(PlayerInteractEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	public void PlayerDropEvent(PlayerDropItemEvent e)
	{
		final Player p = e.getPlayer();
		if (!checkLoggedIn(p))
			e.setCancelled(true);
	}
	
	private boolean checkLoggedIn(Player p)
	{
		if (p != null)
		{
			if (this.plugin.getLoginManager().isPlayerLoggedIn(p))
				return true;
		}
		return false;
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
					if (System.currentTimeMillis() - subject.getLastMessage() > plugin.getSettingManager().getLoginMessageIntervall())
					{
						if (!plugin.getLoginManager().hasAccount(p))
						{
							XUtil.sendFileMessage(p, "message.register", true);
							subject.setLastMessage(System.currentTimeMillis());
						}
						else
						if (!plugin.getLoginManager().isPlayerLoggedIn(p))
						{
							XUtil.sendFileMessage(p, "message.login", true);
							subject.setLastMessage(System.currentTimeMillis());
						}
					}
				}
			}
		}, 1, 4);
	}
	
	
}
