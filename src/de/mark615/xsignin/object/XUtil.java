package de.mark615.xsignin.object;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mark615.xsignin.SettingManager;
import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.Updater.UpdateResult;
import de.mark615.xsignin.object.Updater.UpdateType;

public class XUtil
{
	public static void info(String info)
	{
		Bukkit.getLogger().info(XSignIn.PLUGIN_NAME + info);
	}

	public static void warning(String severe)
	{
		Bukkit.getLogger().info(XSignIn.PLUGIN_NAME + "[WARNING] " + severe);
	}
	
	public static void severe(String severe)
	{
		Bukkit.getLogger().severe(XSignIn.PLUGIN_NAME + severe);
	}
	
	public static void severe(String severe, Exception e)
	{
		severe(severe);
		e.printStackTrace();
	}
	
	public static void debug(Exception e)
	{
		e.printStackTrace();
	}
	
	public static String replaceColorCodes(String message)
	{
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	public static String getMessage(String file)
	{
		String raw = SettingManager.getInstance().getMessage().getString(file);
		if (raw == null)
		{
			raw = file + " (not found in messages.yml)";
		}
		raw = replaceColorCodes(raw);
		return raw;
	}
	
	private static void sendMessage(CommandSender sender, String message, boolean prefix)
	{
		message = replaceColorCodes(message);
		
		for (String line : message.split("%ln%"))
		{
			if (!prefix)
				sender.sendMessage(line);
			else
				sender.sendMessage(XSignIn.PLUGIN_NAME_SHORT + line);
		}
	}
	
	private static void sendMessage(CommandSender sender, String message)
	{
		sendMessage(sender, message, false);
	}
	
	public static void sendFileMessage(CommandSender s, String file, ChatColor color)
	{
		String message = getMessage(file);
		if (s instanceof Player)
			message = color + message;
		
		sendMessage(s, message, true);
	}

	public static void sendFileMessage(CommandSender s, String msg)
	{
		sendMessage(s, getMessage(msg));
	}

	public static void sendFileMessage(CommandSender s, String msg, boolean prefix)
	{
		sendMessage(s, getMessage(msg), prefix);
	}
	
	public static void sendCommandUsage(CommandSender s, String usage)
	{
		usage = ChatColor.RED + usage;
		sendMessage(s, usage, true);
	}
	
	public static void sendCommandInfo(CommandSender s, String info)
	{
		info = ChatColor.GREEN + info;
		sendMessage(s, info, true);
	}
	
	public static void sendCommandHelp(CommandSender s, String help)
	{
		help = ChatColor.YELLOW + help;
		sendMessage(s, help, true);
	}
	
	public static void sendCommandError(CommandSender s, String error)
	{
		error = ChatColor.RED + error;
		sendMessage(s, error, true);
	}
	
	public static void sendMessage(Player p, String info)
	{
		sendMessage(p, info, false);
	}
	
	public static String toHash(String pw)
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
	
	public static PassMatch matchPasswordRules(String password)
	{
	    /*
	    ^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$

	    Explanation:

	    ^                 # start-of-string
	    (?=.*[0-9])       # a digit must occur at least once
	    (?=.*[a-z])       # a lower case letter must occur at least once
	    (?=.*[A-Z])       # an upper case letter must occur at least once
	    (?=.*[@#$%^&+=])  # a special character must occur at least once
	    (?=\\S+$)          # no whitespace allowed in the entire string
	    .{8,}             # anything, at least eight places though
	    $                 # end-of-string*/
		
	    if (password == null)
	    	return PassMatch.ERROR;
	    
	    if (SettingManager.getInstance().needPasswordLength() != -1)
	    {
	    	if (password.length() < SettingManager.getInstance().needPasswordLength())
	    		return PassMatch.LENGTH;
	    }
	    if (SettingManager.getInstance().needPasswordUpperAndLower())
	    {
	    	if (!password.matches(".*[a-zA-Z]+.*"))
	    		return PassMatch.UPPERLOWER;
	    }
	    if (SettingManager.getInstance().needPasswordDigitChar())
	    {
	    	if (!password.matches(".*[0-9]+.*"))
	    		return PassMatch.DIGIT;
	    }
	    if (SettingManager.getInstance().needPasswordSpecialChar())
	    {
	    	if (!password.matches(".*[!?@#$%^§&+=\\-*\\/<>\\[\\]|{}]+.*"))
	    		return PassMatch.SPECIAL;
	    }
	    return PassMatch.OK;
	}

	
	
	public static void updateCheck(final JavaPlugin plugin)
	{
		Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (SettingManager.getInstance().hasCheckVersion())
				{
					try
					{
						Updater updater = new Updater(plugin, 267923, plugin.getDataFolder(), UpdateType.NO_DOWNLOAD, true);
						if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
						    XUtil.info("New version available! " + updater.getLatestName());
						}
					}
					catch(Exception e)
					{
						XUtil.severe("Can't check version at Bukkit.com");
					}
				}
			}
		}, 20, 6 * 60 * 60 * 20);
	}
	
	
	public enum PassMatch
	{
		OK,
		ERROR,
		DIGIT,
		SPECIAL,
		UPPERLOWER,
		LENGTH
	}
}
