package de.mark615.xsignin;

import org.bukkit.entity.Player;

import de.mark615.xapi.interfaces.XSignInApi;

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
	
}
