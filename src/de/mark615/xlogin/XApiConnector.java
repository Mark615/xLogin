package de.mark615.xlogin;

import org.bukkit.entity.Player;

import de.mark615.xapi.interfaces.XLoginApi;

public class XApiConnector extends XLoginApi
{
	private XLogin plugin;
	//private PriorityConfigBase priority;
	
	public XApiConnector(de.mark615.xapi.XApi xapi, XLogin plugin)
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
	
}
