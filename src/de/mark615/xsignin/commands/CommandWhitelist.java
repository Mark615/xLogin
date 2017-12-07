package de.mark615.xsignin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xsignin.ListManager;
import de.mark615.xsignin.ListManager.ListType;
import de.mark615.xsignin.SettingManager;
import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XUtil;

public class CommandWhitelist extends XCommand
{
	private XSignIn plugin;
	
	public CommandWhitelist(XSignIn plugin)
	{
		super("whitelist", "xsignin.whitelist");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("enable"));
		subcommands.add(new XSubCommand("disable"));
		subcommands.add(new XSubCommand("add"));
		subcommands.add(new XSubCommand("remove"));
		subcommands.add(new XSubCommand("list"));
		subcommands.add(new XSubCommand("check"));
	}

	@Override
	protected void showHelp(CommandSender p) 
	{
		p.sendMessage(ChatColor.GREEN + XSignIn.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(matchPermission(p, "xsignin.whitelist.add")) p.sendMessage(ChatColor.GREEN + "/whitelist add <value> <type>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.whitelist.add.description"));
		if(matchPermission(p, "xsignin.whitelist.remove")) p.sendMessage(ChatColor.GREEN + "/whitelist remove <value> <typ>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.whitelist.remove.description"));
		if(matchPermission(p, "xsignin.whitelist")) p.sendMessage(ChatColor.GREEN + "/whitelist list" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.whitelist.list.description"));
		if(matchPermission(p, "xsignin.whitelist.check")) p.sendMessage(ChatColor.GREEN + "/whitelist check <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.whitelist.check.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /whitelist <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NOCOMMAND;
		}
		
		if (matchesSubCommand("enable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist.mode"))
				return XCommandReturnType.NOPERMISSION;
			
			this.plugin.getLoginManager().getListManager().setWhitelist(true);
			XUtil.sendFileMessage(sender, "command.whitelist.enable.success", ChatColor.GREEN);

			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("disable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist.add"))
				return XCommandReturnType.NOPERMISSION;
			
			this.plugin.getLoginManager().getListManager().setWhitelist(false);
			XUtil.sendFileMessage(sender, "command.whitelist.disable.success", ChatColor.GREEN);
			
			return XCommandReturnType.SUCCESS;
		}
		
		if (!SettingManager.getInstance().isWhitelist())
		{
			XUtil.sendFileMessage(sender, "command.not-enable", ChatColor.YELLOW);
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("add", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist.set"))
				return XCommandReturnType.NOPERMISSION;
			
			boolean usage = false;
			if (args.length < 3)
				usage = true;
			
			ListType type = null;
			if (args.length > 2)
				type = ListManager.checkListType(args[2]);
			if (type == null)
				usage = true;
			
			if (usage)
			{
				XUtil.sendCommandUsage(sender, "use /whitelist add <value> <IP|UUID|NAME>");
				return XCommandReturnType.NONE;
			}
			
			if (plugin.getLoginManager().getListManager().containsWhitelistElement(args[1], type))
			{
				XUtil.sendFileMessage(sender, "command.whitelist.add.on-list", true);
				return XCommandReturnType.NONE;
			}
			
			try
			{
				if (type.equals(ListType.IP))
				{
					plugin.getLoginManager().getListManager().addWhitelistElement(args[1], type);
				}
				else
				{
					if (Bukkit.getServer().getPlayer(args[1]) != null)
					{
						Player p = Bukkit.getServer().getPlayer(args[1]);
						args[1] = p.getUniqueId().toString();
						plugin.getLoginManager().getListManager().addWhitelistElement(p.getUniqueId().toString(), ListType.UUID);
					}
					else
					{
						plugin.getLoginManager().getListManager().addWhitelistElement(args[1], type);
					}
				}
			}
			catch (Exception e)
			{
				XUtil.severe("can't add Element '" + args[1] + "' type '" + type + "' to whitelist", e);
				XUtil.sendFileMessage(sender, "command.whitelist.add.error", true);
				return XCommandReturnType.NONE;
			}
			
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.whitelist.add.success").replace("%value%", args[1]));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("remove", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist.set"))
				return XCommandReturnType.NOPERMISSION;
			
			boolean usage = false;
			if (args.length < 3)
				usage = true;
			
			ListType type = ListManager.checkListType(args[2]);
			if (type == null)
				usage = true;
			
			if (usage)
			{
				XUtil.sendCommandUsage(sender, "use /whitelist remove <value> <IP|UUID|NAME>");
				return XCommandReturnType.NONE;
			}
			
			if (!plugin.getLoginManager().getListManager().containsWhitelistElement(args[1], type))
			{
				XUtil.sendFileMessage(sender, "command.whitelist.remove.not-on-list", true);
				return XCommandReturnType.NONE;
			}
			
			plugin.getLoginManager().getListManager().removeWhitelistElement(args[1], type);
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.whitelist.remove.success").replace("%value%", args[1]));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("list", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist"))
				return XCommandReturnType.NOPERMISSION;
			
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("check", args[0]))
		{
			if (!matchPermission(sender, "xsignin.whitelist.check"))
				return XCommandReturnType.NOPERMISSION;
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use /whitelist check <value>");
				return XCommandReturnType.NONE;
			}
			
			if (plugin.getLoginManager().getListManager().isElementOnWhitelist(args[1]))
			{
				XUtil.sendCommandInfo(sender, "command.whitelist.check.on-list");
			}
			else
			{
				XUtil.sendCommandInfo(sender, "command.whitelist.check.not-on-list");
			}
			
			return XCommandReturnType.SUCCESS;
		}
		
		return XCommandReturnType.NONE;
	}

}
