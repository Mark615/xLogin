package de.mark615.xlogin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xlogin.XLogin;
import de.mark615.xlogin.object.XUtil;

public class CommandXLogin extends XCommand
{
	private final XLogin plugin;

	public CommandXLogin(XLogin plugin)
	{
		super("xlogin", "xlogin.login");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<String> subcommands)
	{
		subcommands.add("login");
		subcommands.add("register");
		subcommands.add("change");
		subcommands.add("set");
		subcommands.add("reset");
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XLogin.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(matchPermission(p, "xlogin.login")) p.sendMessage(ChatColor.GREEN + "/xlogin login <password>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xlogin.login.description"));
		if(matchPermission(p, "xlogin.login")) p.sendMessage(ChatColor.GREEN + "/xlogin register <pw> <pw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xlogin.register.description"));
		if(matchPermission(p, "xlogin.login.change")) p.sendMessage(ChatColor.GREEN + "/xlogin change <oldpw> <newpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xlogin.change.description"));
		if(matchPermission(p, "xlogin.login.reset")) p.sendMessage(ChatColor.GREEN + "/xlogin reset <oldpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xlogin.reset.description"));
		if(matchPermission(p, "xlogin.login.set")) p.sendMessage(ChatColor.GREEN + "/xlogin set <player> <newpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xlogin.set.description"));
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
			
			if (!this.containsSubCommand(args[0]))
			{
				XUtil.sendCommandUsage(sender, "use: /xlogin <help/?> " + ChatColor.YELLOW + "- for help");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("login"))
			{
				if (!matchPermission(sender, "xlogin.login"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use: /xlogin login <pw> " + ChatColor.YELLOW + "- for login");
					return true;
				}
				
				Player target = (Player)sender;
				
				if (!this.plugin.getLoginManager().isRegisterd(target))
					return true;
				
				if (this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendFileMessage(target, "command.already-logged-in", ChatColor.GREEN);
					return true;
				}
				
				
				
				if (!this.plugin.getLoginManager().loginPlayer(target, args[1]))
				{
					XUtil.sendFileMessage(sender, "command.wrong-pw", ChatColor.RED);
				}
				else
				{
					XUtil.sendFileMessage(sender, "command.xlogin.login.success", ChatColor.GREEN);
					this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerIn();
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("register"))
			{
				if (!matchPermission(sender, "xlogin.login"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 3)
				{
					XUtil.sendCommandUsage(sender, "use /xlogin register <password> <password>");
					return true;
				}

				if (!args[1].equals(args[2]))
				{
					XUtil.sendFileMessage(sender, "command.xlogin.register.error-pw", ChatColor.RED);
					return true;
				}
				
				Player target = (Player)sender;
				
				if (this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendFileMessage(target, "command.already-logged-in", ChatColor.GREEN);
					return true;
				}
				
				
				
				if (this.plugin.getLoginManager().registerPlayer(this.plugin.getLoginManager().getPlayer(target.getUniqueId()), args[1]))
				{
					XUtil.sendFileMessage((Player)sender, "command.xlogin.register.success", ChatColor.GREEN);
					this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerIn();
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("change"))
			{
				if (!matchPermission(sender, "xlogin.login.change"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 3)
				{
					XUtil.sendCommandUsage(sender, "use /xlogin change <oldpw> <newpw>");
					return true;
				}
				
				Player target = (Player)sender;
				
				if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
					return true;
				
				if (!this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendCommandUsage(target.getPlayer(), "use /xlogin login <pw>");
					return true;
				}
				
				
				
				if (!this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
				{
					XUtil.sendFileMessage(target, "command.wrong-pw", ChatColor.RED);
					return true;
				}
				
				if (this.plugin.getLoginManager().setPlayerPassword(target, args[2]))
				{
					XUtil.sendFileMessage(target, "command.xlogin.change.success", ChatColor.GREEN);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("reset"))
			{
				if (!matchPermission(sender, "xlogin.login.reset"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use /xlogin reset <pw>");
					return true;
				}
				
				Player target = (Player)sender;
				
				if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
					return true;
				
				if (!this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendCommandUsage(target.getPlayer(), "use /xlogin login <pw>");
					return true;
				}
				
				
				
				if (!this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
				{
					XUtil.sendFileMessage(target, "command.wrong-pw", ChatColor.RED);
					return true;
				}
				
				if (this.plugin.getLoginManager().resetPlayer(target))
				{
					this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerOut();
					XUtil.sendFileMessage(target, "command.xlogin.reset.success", ChatColor.GREEN);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("set"))
			{
				if (!matchPermission(sender, "xlogin.login.set"))
					return false;
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use /xlogin set <player> <pw>");
					return true;
				}
				
				Player target = Bukkit.getServer().getPlayer(args[1]);
				if (target == null)
				{
					XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
					return true;
				}
				
				if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
					return true;
				
				

				if (this.plugin.getLoginManager().setPlayerPassword(target, args[2]))
				{
					XUtil.sendFileMessage((Player)sender, "command.xlogin.set.success-sender", ChatColor.GREEN);
					XUtil.sendFileMessage(target, "command.xlogin.set.success-target", ChatColor.GREEN);
				}
				return true;
			}

			//TODO AGB
		}
		else
		{
			showHelp(sender);
		}
		return true;
	}
}
