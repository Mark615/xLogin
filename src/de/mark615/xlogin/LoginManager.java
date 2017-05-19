package de.mark615.xlogin;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import de.mark615.xlogin.object.XDatabase;
import de.mark615.xlogin.object.XPlayerSubject;
import de.mark615.xlogin.object.XUtil;

public class LoginManager
{
	private XDatabase database = null;
	private XLogin plugin = null;
	private Map<UUID, XPlayerSubject> player = null;
	
	public LoginManager(XLogin plugin)
	{
		this.plugin = plugin;
		this.player = new HashMap<>();
		this.database = new XDatabase();
		database.isValid();
	}
	
	public void registerPlayer(Player target)
	{
		XPlayerSubject subject = player.get(target.getUniqueId());
		if (subject != null)
		{
			if (target.hasPermission("xlogin.autorelog") && plugin.getSettingManager().hasAutoRelog())
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
			this.player.put(target.getUniqueId(), new XPlayerSubject(target.getUniqueId()));
		}
	}
	
	public void unregisterPlayer(Player target)
	{
		this.player.get(target.getUniqueId()).logPlayerOut();
		try
		{
			this.database.unregisterXPlayerSubject(player.get(target.getUniqueId()));
		}
		catch (SQLException e)
		{
			XUtil.severe("Database error");
			XUtil.severe(e.getMessage());
		}
	}
	
	public boolean registerPlayer(XPlayerSubject target, String pw)
	{
		if (database.hasPlayerAccount(target.getUUID()))
		{
			XUtil.sendCommandInfo(target.getPlayer(), XUtil.getMessage("command.xlogin.register.use-login") + " use /xlogin login <pw>");
			return false;
		}

		try
		{
			this.database.registerXPlayerSubject(player.get(target.getUUID()), passwordToHash(pw));
			if (plugin.getSettingManager().hasFirstJoinMessage())
			{
				XUtil.sendFileMessage(target.getPlayer(), "message.first-join");
			}
			if (this.plugin.hasAPI())
				this.plugin.getAPI().createPlayerFirstJoinEvent(target.getPlayer());
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
			database.setPlayerPassword(target.getUniqueId(), passwordToHash(pw));

			if (this.plugin.hasAPI())
				this.plugin.getAPI().createPlayerPasswordChangedEvent(target);
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

			if (this.plugin.hasAPI())
				this.plugin.getAPI().createPlayerResetEvent(target);
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
			result = database.checkPassword(target.getUniqueId(), passwordToHash(pw));
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
			result = database.loginPlayer(target.getUniqueId(), passwordToHash(pw));
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
			XUtil.sendCommandInfo(target, XUtil.getMessage("command.xlogin.login.use-register") + " use /xlogin register <pw> <pw>");
			return false;
		}
		return true;
	}
	
	public boolean hasAccount(Player target)
	{
		return database.hasPlayerAccount(target.getUniqueId());
	}
	
	private String passwordToHash(String pw)
	{
		String hashtext = null;
		byte[] bytesOfMessage;
		try {
			bytesOfMessage = pw.getBytes("UTF-8");
	
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] thedigest = md.digest(bytesOfMessage);
			
			BigInteger number = new BigInteger(1, thedigest);
			hashtext = number.toString(16);
			
			while (hashtext.length() < 32)
			{
				hashtext = "0" + hashtext;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return hashtext;
	}
	
	public XPlayerSubject getPlayer(UUID uuid)
	{
		return player.get(uuid);
	}
}
