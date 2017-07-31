package de.mark615.xsignin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XUtil;

public class CommandXMaintanance extends XCommand
{
	private XSignIn plugin;
	
	public CommandXMaintanance(XSignIn plugin)
	{
		super("xmaintenance", "xsignin.maintenance.admin");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("enable"));
		subcommands.add(new XSubCommand("disable"));
		subcommands.add(new XSubCommand("clear"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!(sender instanceof Player))
		{
			XUtil.sendFileMessage(sender, "command.no-consol-command");
			return XCommandReturnType.NEEDTOBEPLAYER;
		}
		
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xmaintenance <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NOCOMMAND;
		}
		
		if (matchesSubCommand("enable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.maintenance.admin"))
				return XCommandReturnType.NOPERMISSION;
			
			if (this.plugin.isMaintenanceMode())
			{
				XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.enable.error"));
				return XCommandReturnType.NONE;
			}
			
			this.plugin.setMaintenanceMode(true);
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.enable.success"));
			return XCommandReturnType.SUCCESS;
		}
		
		if(matchesSubCommand("disable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.maintenance.admin"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!this.plugin.isMaintenanceMode())
			{
				XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.disable.error"));
				return XCommandReturnType.NONE;
			}
			
			this.plugin.setMaintenanceMode(false);
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.disable.success"));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("clear", args[0]))
		{
			if (!matchPermission(sender, "xsignin.maintenance.admin"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!this.plugin.isMaintenanceMode())
			{
				XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.clear.error"));
				return XCommandReturnType.NONE;
			}
			
			for (Player target : Bukkit.getServer().getOnlinePlayers())
			{
				target.kickPlayer(XUtil.getMessage("command.xmaintenance.clear.maintenance_kick"));
			}
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.clear.success"));
			return XCommandReturnType.SUCCESS;
		}
		
		return XCommandReturnType.NOCOMMAND;
	}

	@Override
	protected void showHelp(CommandSender p)
	{
		boolean permission = matchPermission(p, "xsignin.maintenance.admin"); 
		p.sendMessage(ChatColor.GREEN + XSignIn.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(permission) p.sendMessage(ChatColor.GREEN + "/xmt enable" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xmaintenance.enable.description"));
		if(permission) p.sendMessage(ChatColor.GREEN + "/xmt disable" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xmaintenance.disable.description"));
		if(permission) p.sendMessage(ChatColor.GREEN + "/xmt clear" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xmaintenance.clear.description"));
	}
	
}
