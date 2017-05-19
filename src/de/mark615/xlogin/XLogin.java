package de.mark615.xlogin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.mark615.xapi.XApi;
import de.mark615.xapi.versioncheck.VersionCheck;
import de.mark615.xapi.versioncheck.VersionCheck.XType;
import de.mark615.xlogin.commands.CommandXLogin;
import de.mark615.xlogin.commands.XCommand;
import de.mark615.xlogin.events.PlayerEvents;
import de.mark615.xlogin.object.XUtil;

public class XLogin extends JavaPlugin
{
	public static final int BUILD = 1;
	public static final String PLUGIN_NAME = "[xLogin] ";
	public static final String PLUGIN_NAME_SHORT = "[xLogin] ";
	
	private static XLogin instance = null;

	private XApiConnector xapiconn = null;
	private SettingManager settings = null;
	private LoginManager loginManager = null;
	private PlayerEvents events = null;

	private Map<String, XCommand> commands = null;

	public void onEnable()
	{
		instance = this;
		this.commands = new HashMap<>();

		settings = SettingManager.getInstance();
		settings.setup(this);
		
		this.loginManager = new LoginManager(this);
		registerEvents();
		registerCommands();
		
		setupXApi();
		if (xapiconn != null)
		{
			XUtil.info("connected with xApi");
		}
		
		loadPlugin();
	}

	@Override
	public void onDisable()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.loginManager.unregisterPlayer(p);
		}
	}
	
	public void loadPlugin()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.loginManager.registerPlayer(p);
		}
		events.messageAllPlayer();
	}
	
	
	
	

	private void registerEvents()
	{
		events = new PlayerEvents(this);
		Bukkit.getServer().getPluginManager().registerEvents(events, this);
	}
	
	private void registerCommands()
	{
		commands.put("xlogin", new CommandXLogin(this));
	}

	private boolean setupXApi() 
	{
		XApi xapi = (XApi)getServer().getPluginManager().getPlugin("xApi");
    	if(xapi == null)
    		return false;
    	
    	try
    	{
	    	if (xapi.checkVersion(XType.xLogin, BUILD))
	    	{
	        	xapiconn = new XApiConnector(xapi, this);
	        	xapi.registerXLogin(xapiconn);
	    	}
	    	else
	    	{
	    		XUtil.severe("Can't hook to xApi!"); 
	    		if (VersionCheck.isXPluginHigherXApi(XType.xLogin, BUILD))
	    		{
		    		XUtil.warning("Please update your xApi!");
		    		XUtil.warning("Trying to hook to xApi. Have an eye into console for errors with xApi!");

		        	xapiconn = new XApiConnector(xapi, this);
		        	xapi.registerXLogin(xapiconn);
	    		}
	    		else
	    		{
		    		XUtil.severe("Please update your xLogin for hooking.");
	    		}
	    	}
    	}
    	catch (Exception e)
    	{
    		XUtil.severe("An error accurred during connection to xApi!");
    	}
    	
    	return xapiconn != null;
	}

	
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
	{
		XCommand xCommand = commands.get(command.getLabel());

		if (xCommand == null)
			return false;
		
		if (commandSender instanceof Player)
		{
			if (!((Player) commandSender).hasPermission(xCommand.getPermission()) || !xCommand.run((Player) commandSender, command, s, args))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
				return true;
			}
		}
		else
		{
			if (!xCommand.run(commandSender, command, s, args))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
			}
		}
		return true;
	}

	public SettingManager getSettingManager()
	{
		return this.settings;
	}

	public static XLogin getInstance()
	{
		return instance;
	}
	
	public XApiConnector getAPI()
	{
		return this.xapiconn;
	}
	
	public boolean hasAPI()
	{
		return this.xapiconn != null;
	}
	
	public LoginManager getLoginManager()
	{
		return loginManager;
	}
}
