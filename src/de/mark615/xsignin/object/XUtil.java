package de.mark615.xsignin.object;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.mark615.xsignin.SettingManager;
import de.mark615.xsignin.XSignIn;

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
	
	public static void debug(Exception e)
	{
		e.printStackTrace();
	}
	
	public static String getMessage(String file)
	{
		String raw = SettingManager.getInstance().getMessage().getString(file);
		if (raw == null)
		{
			raw = file + " (not found in messages.yml)";
		}
		raw = raw.replace("&", "§");
		return raw;
	}
	
	private static void sendMessage(CommandSender sender, String message, boolean prefix)
	{
		message = message.replace("&", "§");
		
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
		if (s instanceof Player)
			usage = ChatColor.RED + usage;
		
		sendMessage(s, usage);
	}
	
	public static void sendCommandInfo(CommandSender s, String info)
	{
		if (s instanceof Player)
			info = ChatColor.GREEN + info;
		
		sendMessage(s, info);
	}
	
	public static void sendCommandHelp(CommandSender s, String help)
	{
		if (s instanceof Player)
			help = ChatColor.YELLOW + help;
		
		sendMessage(s, help);
	}
	
	public static void sendCommandError(CommandSender s, String error)
	{
		if (s instanceof Player)
			error = ChatColor.RED + error;
		
		sendMessage(s, error);
	}
	
	public static void onEnable()
	{
		try
		{
			sendGet("setmode?type=xSignIn&mode=on&build=" + XSignIn.BUILD);
		}
		catch(Exception e)
		{
			severe("Can't generate onEnable webrequest");
		}
		
		checkUpdate();
	}
	
	public static void onDisable()
	{
		try
		{
			sendGet("setmode?type=xSignIn&mode=off&build=" + XSignIn.BUILD);
		}
		catch(Exception e)
		{
			severe("Can't generate onDisable webrequest");
		}
	}
	
	private static void checkUpdate()
	{
		try
		{
		    JsonElement jsonelement = new JsonParser().parse(sendGet("checkversion?type=xSignIn&build=" + XSignIn.BUILD));
		    JsonObject json = jsonelement.getAsJsonObject();
			if (json.has("build") && json.get("build").getAsInt() > XSignIn.BUILD)
			{
				info("A newer version of xSignIn is avaible. V." + json.get("version").getAsString());
			}
		}
		catch(Exception e)
		{
			severe("Can't generate checkUpdate webrequest");
		}
	}
	
	// HTTP GET request
	private static String sendGet(String message) throws Exception {

		String url = "http://134.255.217.210:8080/";

		URL obj = new URL(url + message);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		
		//reponse
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}
}
