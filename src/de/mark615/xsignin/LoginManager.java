package de.mark615.xsignin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.mark615.xsignin.database.XDatabase;
import de.mark615.xsignin.object.XPlayerSubject;
import de.mark615.xsignin.object.XUtil;

public class LoginManager
{
	private XDatabase database = null;
	private XSignIn plugin = null;
	private boolean enable = true;
	private AGBManager agbManager = null;
	private ListManager listManager = null;
	private Map<UUID, XPlayerSubject> player = null;
	
	public LoginManager(XSignIn plugin)
	{
		this.plugin = plugin;
		this.player = new HashMap<>();
		this.database = new XDatabase();
		database.isValid();
		
		this.enable = SettingManager.getInstance().isLogin();
		this.agbManager = new AGBManager(plugin, database.getAGBDatabse());
		this.listManager = new ListManager(plugin, database.getWBLDatabase());
	}
	
	public void registerPlayer(Player target)
	{
		XPlayerSubject subject = player.get(target.getUniqueId());
		if (subject != null && enable)
		{
			if (target.hasPermission("xsignin.autorelog") && plugin.getSettingManager().hasAutoRelog())
			{
				if ((System.currentTimeMillis() - subject.getLogoutTime()) < plugin.getSettingManager().getAutoRelogTime())
				{
					if (this.player.get(target.getUniqueId()).autorelogPlayer())
					{
						XUtil.sendFileMessage(target, "message.autorelog", ChatColor.GREEN);
					}
				}
			}
		}
		else
		{
			this.player.put(target.getUniqueId(), database.loadXPlayerSubject(target.getUniqueId()));
			subject = this.player.get(target.getUniqueId());
		}
		
		if (!enable)
			subject.logPlayerIn();
		
		if (agbManager.hasXPlayerAcceptAGB(subject.getPlayer().getUniqueId()))
			subject.setAGBAccepted();
	}
	
	public void unregisterPlayer(Player target)
	{
		if (this.player.get(target.getUniqueId()) != null)
		{
			try
			{
				this.player.get(target.getUniqueId()).logPlayerOut();
				this.database.unregisterXPlayerSubject(player.get(target.getUniqueId()));
			}
			catch (SQLException e)
			{
				XUtil.severe("Database error", e);
			}
		}
	}
	
	public boolean registerPlayer(XPlayerSubject target, String pw)
	{
		if (database.hasPlayerAccount(target.getUUID()))
		{
			XUtil.sendCommandInfo(target.getPlayer(), XUtil.getMessage("command.xsignin.register.use-login") + " use /xsignin login <pw>");
			return false;
		}

		try
		{
			this.database.registerXPlayerSubject(player.get(target.getUUID()), XUtil.toHash(pw));
			if (plugin.getSettingManager().hasFirstJoinMessage())
			{
				XUtil.sendFileMessage(target.getPlayer(), "message.first-join");
			}
			if (this.plugin.hasXApiConnector())
				this.plugin.getXApiConnector().createPlayerFirstJoinEvent(target.getPlayer());
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean isPlayerLoggedIn(Player target)
	{
		if (target == null)
			return false;
		
		if (player.get(target.getUniqueId()) != null)
			return player.get(target.getUniqueId()).isLoggedIn();
		return false;
	}
	
	public boolean setPlayerPassword(Player target, String pw)
	{
		if (target == null)
			return false;
		
		try
		{
			database.setPlayerPassword(target.getUniqueId(), XUtil.toHash(pw));

			if (this.plugin.hasXApiConnector())
				this.plugin.getXApiConnector().createPlayerPasswordChangedEvent(target);
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean resetPlayer(Player target)
	{
		if (target == null)
			return false;
		
		try
		{
			database.resetPlayer(target.getUniqueId());

			if (this.plugin.hasXApiConnector())
				this.plugin.getXApiConnector().createPlayerResetEvent(target);
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean checkPlayerPassword(Player target, String pw)
	{
		if (target == null)
			return false;

		boolean result = false;
		try
		{
			result = database.checkPassword(target.getUniqueId(), XUtil.toHash(pw));
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
			return false;
		}
		return result;
	}
	
	public boolean loginPlayer(Player target, String pw)
	{
		if (target == null)
			return false;

		boolean result = false;
		try
		{
			result = database.loginPlayer(target, XUtil.toHash(pw));
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
			return false;
		}
		return result;
	}
	
	public boolean isRegisterd(Player target)
	{
		if (!database.hasPlayerAccount(target.getUniqueId()))
		{
			XUtil.sendCommandInfo(target, XUtil.getMessage("command.xsignin.login.use-register") + " use /xsignin register <pw> <pw>");
			return false;
		}
		return true;
	}
	
	public boolean hasAccount(Player target)
	{
		return database.hasPlayerAccount(target.getUniqueId());
	}
	
	
	
	public boolean isEnabled()
	{
		return enable;
	}
	
	public AGBManager getAGBManager()
	{
		return agbManager;
	}
	
	public ListManager getListManager()
	{
		return listManager;
	}
	
	public XPlayerSubject getXSubjectPlayer(UUID uuid)
	{
		return player.get(uuid);
	}
	
	public int getXPlayerSubjectID(UUID uuid)
	{
		return database.getXPlayerSubjectID(uuid);
	}
	
	
	public String getNameFromIp(String ip)
	{
		return database.getNameFromIp(ip);
	}
	
	public UUID getUUIDFromIp(String ip)
	{
		return database.getUUIDFromIp(ip);
	}
}
