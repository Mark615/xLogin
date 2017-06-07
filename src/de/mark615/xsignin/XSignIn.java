package de.mark615.xsignin;

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
import de.mark615.xsignin.commands.CommandXMaintanance;
import de.mark615.xsignin.commands.CommandXSignIn;
import de.mark615.xsignin.commands.XCommand;
import de.mark615.xsignin.events.EventListener;
import de.mark615.xsignin.object.Updater;
import de.mark615.xsignin.object.Updater.UpdateResult;
import de.mark615.xsignin.object.Updater.UpdateType;
import de.mark615.xsignin.object.XUtil;

public class XSignIn extends JavaPlugin
{
	public static final int BUILD = 4;
	public static final String PLUGIN_NAME = "[xSignIn] ";
	public static final String PLUGIN_NAME_SHORT = "[xSignIn] ";
	
	private static XSignIn instance = null;

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
		
		XUtil.onEnable();
		updateCheck();
		loadPlugin();
	}

	@Override
	public void onDisable()
	{
		XUtil.onDisable();
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
	
	private void updateCheck()
	{
		if (SettingManager.getInstance().hasCheckVersion())
		{
			try
			{
				Updater updater = new Updater(this, 267923, this.getFile(), UpdateType.NO_DOWNLOAD, true);
				if (updater.getResult() == UpdateResult.UPDATE_AVAILABLE) {
				    XUtil.info("New version available! " + updater.getLatestName());
				}
			}
			catch(Exception e)
			{
				XUtil.severe("Can't generate checkUpdate webrequest");
			}
		}
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
		commands.put("xlogin", new CommandXSignIn(this));
		commands.put("xmaintenance", new CommandXMaintanance(this));
	}

	private boolean setupXApi() 
	{
		XApi xapi = (XApi)getServer().getPluginManager().getPlugin("xApi");
    	if(xapi == null)
    		return false;
    	
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
		if (hasAPI())
			getAPI().createMaintenanceModeSwitchEvent(value);
	}
}
