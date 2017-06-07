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
	public void fillSubCommands(List<String> subcommands)
	{
		subcommands.add("enable");
		subcommands.add("disable");
		subcommands.add("clear");
	}

	@Override
	public boolean run(CommandSender sender, Command command, String s, String[] args)
	{
		if(args.length > 0)
		{
			if (args[0].equalsIgnoreCase("help") || args[0].equals("?"))
			{
				showHelp(sender);
			}
			
			if (!(sender instanceof Player))
			{
				XUtil.sendFileMessage(sender, "command.no-consol-command");
				return true;
			}
			
			if (!this.containsSubCommand(args[0]))
			{
				XUtil.sendCommandUsage(sender, "use: /xmaintenance <help/?> " + ChatColor.YELLOW + "- for help");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("enable"))
			{
				if (!matchPermission(sender, "xsignin.maintenance.admin"))
					return false;
				
				if (this.plugin.isMaintenanceMode())
				{
					XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.enable.error"));
					return true;
				}
				
				this.plugin.setMaintenanceMode(true);
				XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.enable.success"));
				return true;
			}
			
			if(args[0].equalsIgnoreCase("disable"))
			{
				if (!matchPermission(sender, "xsignin.maintenance.admin"))
					return false;
				
				if (!this.plugin.isMaintenanceMode())
				{
					XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.disable.error"));
					return true;
				}
				
				this.plugin.setMaintenanceMode(false);
				XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.disable.success"));
				return true;
			}
			
			if (args[0].equalsIgnoreCase("clear"))
			{
				if (!matchPermission(sender, "xsignin.maintenance.admin"))
					return false;
				
				if (!this.plugin.isMaintenanceMode())
				{
					XUtil.sendCommandError(sender, XUtil.getMessage("command.xmaintenance.clear.error"));
					return true;
				}
				
				for (Player target : Bukkit.getServer().getOnlinePlayers())
				{
					target.kickPlayer(XUtil.getMessage("command.xmaintenance.clear.maintenance_kick"));
				}
				XUtil.sendCommandInfo(sender, XUtil.getMessage("command.xmaintenance.clear.success"));
				return true;
			}
		}
		else
		{
			showHelp(sender);
		}
		return true;
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
