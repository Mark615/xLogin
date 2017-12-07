package de.mark615.xsignin;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.java.mcstats.Metrics;

import de.mark615.xapi.XApi;
import de.mark615.xapi.versioncheck.VersionCheck;
import de.mark615.xapi.versioncheck.VersionCheck.XType;
import de.mark615.xsignin.commands.CommandAGB;
import de.mark615.xsignin.commands.CommandBlacklist;
import de.mark615.xsignin.commands.CommandMaintanance;
import de.mark615.xsignin.commands.CommandWhitelist;
import de.mark615.xsignin.commands.CommandXSignIn;
import de.mark615.xsignin.commands.XCommand;
import de.mark615.xsignin.events.EventListener;
import de.mark615.xsignin.object.XUtil;

public class XSignIn extends JavaPlugin
{
	public static final int BUILD = 7;
	public static final String PLUGIN_NAME = "[xSignIn] ";
	public static final String PLUGIN_NAME_SHORT = "[xSignIn] ";
	
	private static XSignIn instance = null;
	private Metrics metrics = null;

	private XApi xapi;
	private XApiConnector xapiconn = null;
	private SettingManager settings = null;
	private LoginManager loginManager = null;
	private EventListener events = null;
	private boolean maintenanceMode = false;

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
		
		try{
			metrics = new Metrics(this);
			metrics.start();
			XUtil.info("hooked to [Metrics]");
		} catch (Exception e){
			XUtil.severe("Can't hook to [Metrics]", e);
		}
		
		loadPlugin();
		XUtil.updateCheck(this);
		XUtil.info("Enabled Build " + BUILD);
	}

	@Override
	public void onDisable()
	{
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.loginManager.unregisterPlayer(p);
		}
		settings.saveConfig();
	}
	
	public void loadPlugin()
	{
		maintenanceMode = settings.getConfig().getBoolean("maintenance", false);
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			this.loginManager.registerPlayer(p);
		}
		events.messageAllPlayer();
	}

	public static XSignIn getInstance()
	{
		return instance;
	}
	
	
	
	

	private void registerEvents()
	{
		events = new EventListener(this);
		Bukkit.getServer().getPluginManager().registerEvents(events, this);
	}
	
	private void registerCommands()
	{
		commands.put("xsignin", new CommandXSignIn(this));
		commands.put("maintenance", new CommandMaintanance(this));
		commands.put("agb", new CommandAGB(this));
		commands.put("whitelist", new CommandWhitelist(this));
		commands.put("blacklist", new CommandBlacklist(this));
	}

	private boolean setupXApi() 
	{
		XApi xapi = (XApi)getServer().getPluginManager().getPlugin("xApi");
    	if(xapi == null)
    		return false;
    	
    	this.xapi = xapi;
    	try
    	{
	    	if (xapi.checkVersion(XType.xSignIn, BUILD))
	    	{
	        	xapiconn = new XApiConnector(xapi, this);
	        	xapi.registerXSignIn(xapiconn);
	    	}
	    	else
	    	{
	    		XUtil.severe("Can't hook to xApi!"); 
	    		if (VersionCheck.isXPluginHigherXApi(XType.xSignIn, BUILD))
	    		{
		    		XUtil.warning("Please update your xApi!");
		    		XUtil.warning("Trying to hook to xApi. Have an eye into console for errors with xApi!");

		        	xapiconn = new XApiConnector(xapi, this);
		        	xapi.registerXSignIn(xapiconn);
	    		}
	    		else
	    		{
		    		XUtil.severe("Please update your xSignIn for hooking.");
	    		}
	    	}
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
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
			if (!((Player) commandSender).hasPermission(xCommand.getPermission()) || !xCommand.runCommand((Player) commandSender, command, s, args))
			{
				commandSender.sendMessage(ChatColor.RED + XUtil.getMessage("command.nopermission"));
				return true;
			}
		}
		else
		{
			if (!xCommand.runCommand(commandSender, command, s, args))
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

	public boolean hasXApi()
	{
		return (xapi != null && xapi.getXPlugin(XType.xSignIn) != null);
	}
	
	public boolean hasXApi(XType type)
	{
		return (hasXApi() && xapi.getXPlugin(type) != null);
	}
	
	public XApi getXApi()
	{
		return xapi;
	}
	
	public boolean hasXApiConnector()
	{
		return xapiconn != null;
	}
	
	public XApiConnector getXApiConnector()
	{
		return xapiconn;
	}
	
	public LoginManager getLoginManager()
	{
		return loginManager;
	}
	
	public boolean isMaintenanceMode()
	{
		return maintenanceMode;
	}
	
	public void setMaintenanceMode(boolean value)
	{
		if (this.maintenanceMode == value)
			return;
		
		this.maintenanceMode = value;
		settings.getConfig().set("maintenance", value);
		settings.saveConfig();
		if (hasXApiConnector())
			getXApiConnector().createMaintenanceModeSwitchEvent(value);
	}
}
