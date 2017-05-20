package de.mark615.xsignin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XUtil;

public class CommandXSignIn extends XCommand
{
	private final XSignIn plugin;

	public CommandXSignIn(XSignIn plugin)
	{
		super("xsignin", "xsignin.login");
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
		p.sendMessage(ChatColor.GREEN + XSignIn.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		if(matchPermission(p, "xsignin.login")) p.sendMessage(ChatColor.GREEN + "/xsignin login <password>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xsignin.login.description"));
		if(matchPermission(p, "xsignin.login")) p.sendMessage(ChatColor.GREEN + "/xsignin register <pw> <pw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xsignin.register.description"));
		if(matchPermission(p, "xsignin.login.change")) p.sendMessage(ChatColor.GREEN + "/xsignin change <oldpw> <newpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xsignin.change.description"));
		if(matchPermission(p, "xsignin.login.reset")) p.sendMessage(ChatColor.GREEN + "/xsignin reset <oldpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xsignin.reset.description"));
		if(matchPermission(p, "xsignin.login.set")) p.sendMessage(ChatColor.GREEN + "/xsignin set <player> <newpw>" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xsignin.set.description"));
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
				XUtil.sendCommandUsage(sender, "use: /xsignin <help/?> " + ChatColor.YELLOW + "- for help");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("login"))
			{
				if (!matchPermission(sender, "xsignin.login"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use: /xsignin login <pw> " + ChatColor.YELLOW + "- for login");
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
					XUtil.sendFileMessage(sender, "command.xsignin.login.success", ChatColor.GREEN);
					this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerIn();
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("register"))
			{
				if (!matchPermission(sender, "xsignin.login"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 3)
				{
					XUtil.sendCommandUsage(sender, "use /xsignin register <password> <password>");
					return true;
				}

				if (!args[1].equals(args[2]))
				{
					XUtil.sendFileMessage(sender, "command.xsignin.register.error-pw", ChatColor.RED);
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
					XUtil.sendFileMessage((Player)sender, "command.xsignin.register.success", ChatColor.GREEN);
					this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerIn();
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("change"))
			{
				if (!matchPermission(sender, "xsignin.login.change"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 3)
				{
					XUtil.sendCommandUsage(sender, "use /xsignin change <oldpw> <newpw>");
					return true;
				}
				
				Player target = (Player)sender;
				
				if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
					return true;
				
				if (!this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendCommandUsage(target.getPlayer(), "use /xsignin login <pw>");
					return true;
				}
				
				
				
				if (!this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
				{
					XUtil.sendFileMessage(target, "command.wrong-pw", ChatColor.RED);
					return true;
				}
				
				if (this.plugin.getLoginManager().setPlayerPassword(target, args[2]))
				{
					XUtil.sendFileMessage(target, "command.xsignin.change.success", ChatColor.GREEN);
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("reset"))
			{
				if (!matchPermission(sender, "xsignin.login.reset"))
					return false;
				
				if (!(sender instanceof Player))
				{
					XUtil.sendFileMessage(sender, "command.no-consol-command");
					return true;
				}
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use /xsignin reset <pw>");
					return true;
				}
				
				Player target = (Player)sender;
				
				if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
					return true;
				
				if (!this.plugin.getLoginManager().isPlayerLoggedIn(target))
				{
					XUtil.sendCommandUsage(target.getPlayer(), "use /xsignin login <pw>");
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
					XUtil.sendFileMessage(target, "command.xsignin.reset.success", ChatColor.GREEN);
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("set"))
			{
				if (!matchPermission(sender, "xsignin.login.set"))
					return false;
				
				if (args.length < 2)
				{
					XUtil.sendCommandUsage(sender, "use /xsignin set <player> <pw>");
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
					XUtil.sendFileMessage((Player)sender, "command.xsignin.set.success-sender", ChatColor.GREEN);
					XUtil.sendFileMessage(target, "command.xsignin.set.success-target", ChatColor.GREEN);
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
