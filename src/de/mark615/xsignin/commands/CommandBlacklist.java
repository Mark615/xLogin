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

public class CommandBlacklist extends XCommand
{
	private XSignIn plugin;
	
	public CommandBlacklist(XSignIn plugin)
	{
		super("blacklist", "xsignin.blacklist");
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
		if(matchPermission(p, "xsignin.blacklist.add")) p.sendMessage(ChatColor.GREEN + "/blacklist add <value> <type>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.blacklist.add.description"));
		if(matchPermission(p, "xsignin.blacklist.remove")) p.sendMessage(ChatColor.GREEN + "/blacklist remove <value> <typ>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.blacklist.remove.description"));
		if(matchPermission(p, "xsignin.blacklist")) p.sendMessage(ChatColor.GREEN + "/blacklist list" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.blacklist.list.description"));
		if(matchPermission(p, "xsignin.blacklist.check")) p.sendMessage(ChatColor.GREEN + "/blacklist check <player>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.blacklist.check.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /blacklist <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NOCOMMAND;
		}
		
		if (matchesSubCommand("enable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist.mode"))
				return XCommandReturnType.NOPERMISSION;
			
			this.plugin.getLoginManager().getListManager().setBlacklist(true);
			XUtil.sendFileMessage(sender, "command.blacklist.enable.success", ChatColor.GREEN);

			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("disable", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist.add"))
				return XCommandReturnType.NOPERMISSION;
			
			this.plugin.getLoginManager().getListManager().setBlacklist(false);
			XUtil.sendFileMessage(sender, "command.blacklist.disable.success", ChatColor.GREEN);
			
			return XCommandReturnType.SUCCESS;
		}
		
		if (!SettingManager.getInstance().isBlacklist())
		{
			XUtil.sendFileMessage(sender, "command.not-enable", ChatColor.YELLOW);
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("add", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist.set"))
				return XCommandReturnType.NOPERMISSION;
			
			boolean usage = false;
			if (args.length < 3)
				usage = true;
			
			ListType type = ListManager.checkListType(args[2]);
			if (type == null)
				usage = true;
			
			if (usage)
			{
				XUtil.sendCommandUsage(sender, "use /blacklist add <value> <IP|UUID|NAME>");
				return XCommandReturnType.NONE;
			}
			
			if (plugin.getLoginManager().getListManager().containsBlacklistElement(args[1], type))
			{
				XUtil.sendFileMessage(sender, "command.blacklist.add.on-list", true);
				return XCommandReturnType.NONE;
			}
			
			try
			{
				if (type.equals(ListType.IP))
				{
					plugin.getLoginManager().getListManager().addBlacklistElement(args[1], type);
				}
				else
				{
					if (Bukkit.getServer().getPlayer(args[1]) != null)
					{
						Player p = Bukkit.getServer().getPlayer(args[1]);
						args[1] = p.getUniqueId().toString();
						plugin.getLoginManager().getListManager().addBlacklistElement(p.getUniqueId().toString(), ListType.UUID);
					}
					else
					{
						plugin.getLoginManager().getListManager().addBlacklistElement(args[1], type);
					}
				}
			}
			catch (Exception e)
			{
				XUtil.severe("can't add Element '" + args[1] + "' type '" + type + "' to blacklist", e);
				XUtil.sendFileMessage(sender, "command.blacklist.add.error", true);
				return XCommandReturnType.NONE;
			}
			
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.blacklist.add.success").replace("%value%", args[1]));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("remove", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist.set"))
				return XCommandReturnType.NOPERMISSION;
			
			boolean usage = false;
			if (args.length < 3)
				usage = true;
			
			ListType type = ListManager.checkListType(args[2]);
			if (type == null)
				usage = true;
			
			if (usage)
			{
				XUtil.sendCommandUsage(sender, "use /blacklist remove <value> <IP|UUID|NAME>");
				return XCommandReturnType.NONE;
			}
			
			if (!plugin.getLoginManager().getListManager().containsBlacklistElement(args[1], type))
			{
				XUtil.sendFileMessage(sender, "command.blacklist.remove.not-on-list", true);
				return XCommandReturnType.NONE;
			}
			
			plugin.getLoginManager().getListManager().removeBlacklistElement(args[1], type);
			XUtil.sendCommandInfo(sender, XUtil.getMessage("command.blacklist.remove.success").replace("%value%", args[1]));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("list", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist"))
				return XCommandReturnType.NOPERMISSION;
			
			
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("check", args[0]))
		{
			if (!matchPermission(sender, "xsignin.blacklist.check"))
				return XCommandReturnType.NOPERMISSION;
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use /blacklist check <value>");
				return XCommandReturnType.NONE;
			}
			
			if (plugin.getLoginManager().getListManager().isElementOnBlacklist(args[1]))
			{
				XUtil.sendCommandInfo(sender, "command.blacklist.check.on-list");
			}
			else
			{
				XUtil.sendCommandInfo(sender, "command.blacklist.check.not-on-list");
			}
			
			return XCommandReturnType.SUCCESS;
		}
		
		return XCommandReturnType.NONE;
	}

}
