package de.mark615.xsignin;

import java.util.List;

import org.bukkit.entity.Player;

import de.mark615.xapi.interfaces.XSignInApi;
import de.mark615.xsignin.ListManager.ListType;

public class XApiConnector extends XSignInApi
{
	private XSignIn plugin;
	//private PriorityConfigBase priority;
	
	public XApiConnector(de.mark615.xapi.XApi xapi, XSignIn plugin)
	{
		super(xapi);
		//this.priority = xapi.getPriorityConfig();
		this.plugin = plugin;
	}

	@Override
	public boolean loginPlayer(Player sender, Player target, String password)
	{
		return this.plugin.getLoginManager().loginPlayer(target, password);
	}

	@Override
	public boolean resetPlayer(Player sender, Player target)
	{
		return this.plugin.getLoginManager().resetPlayer(target);
	}

	@Override
	public boolean setPlayerPassword(Player sender, Player target, String password)
	{
		return this.plugin.getLoginManager().setPlayerPassword(target, password);
	}

	@Override
	public boolean isPlayerLoggedIn(Player sender, Player target)
	{
		return this.plugin.getLoginManager().isPlayerLoggedIn(target);
	}

	@Override
	public boolean isMaintenanceMode()
	{
		return this.plugin.isMaintenanceMode();
	}

	@Override
	public boolean setMaintenanceMode(boolean value)
	{
		this.plugin.setMaintenanceMode(value);
		return true;
	}

	@Override
	public List<String> getAGB()
	{
		return SettingManager.getInstance().getAGBMessage();
	}

	@Override
	public boolean isBlacklist()
	{
		return this.plugin.getLoginManager().getListManager().isBlacklist();
	}

	@Override
	public boolean isWhitelist()
	{
		return this.plugin.getLoginManager().getListManager().isWhitelist();
	}

	@Override
	public void setAGB(boolean arg0)
	{
		SettingManager.getInstance().setAGBEnabled(arg0);
		SettingManager.getInstance().saveConfig();
		this.plugin.getLoginManager().getAGBManager().refreshAGBState();
	}

	@Override
	public void setBlacklist(boolean value)
	{
		this.plugin.getLoginManager().getListManager().setBlacklist(value);
	}

	@Override
	public void setWhitelist(boolean value)
	{
		this.plugin.getLoginManager().getListManager().setWhitelist(value);
	}

	@Override
	public boolean addElementToBlacklist(String value, String typeValue)
	{
		ListType type = hasListType(typeValue);
		if (type == null)
			return false;
		
		if (this.plugin.getLoginManager().getListManager().containsBlacklistElement(value, type))
			return true;
		
		this.plugin.getLoginManager().getListManager().addBlacklistElement(typeValue, type);
		return true;
	}

	@Override
	public boolean addElementToWhitelist(String value, String typeValue)
	{
		ListType type = hasListType(typeValue);
		if (type == null)
			return false;
		
		if (this.plugin.getLoginManager().getListManager().containsWhitelistElement(value, type))
			return true;
		
		this.plugin.getLoginManager().getListManager().addWhitelistElement(typeValue, type);
		return true;
	}

	@Override
	public boolean removeElementToBlacklist(String value, String typeValue)
	{
		ListType type = hasListType(typeValue);
		if (type == null)
			return false;
		
		this.plugin.getLoginManager().getListManager().removeBlacklistElement(typeValue, type);
		return true;
	}

	@Override
	public boolean removeElementToWhitelist(String value, String typeValue)
	{
		ListType type = hasListType(typeValue);
		if (type == null)
			return false;
		
		this.plugin.getLoginManager().getListManager().removeWhitelistElement(typeValue, type);
		return true;
	}
	
	private ListType hasListType(String type)
	{
		ListType result = null;
		try
		{
			result = ListType.valueOf(type);
			if (result == null)
				return null;
		}
		catch (Exception e)
		{
			return null;
		}
		
		return result;
	}
	
}
