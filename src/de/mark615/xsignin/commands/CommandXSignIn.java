package de.mark615.xsignin.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XUtil;
import de.mark615.xsignin.object.XUtil.PassMatch;

public class CommandXSignIn extends XCommand
{
	private final XSignIn plugin;

	public CommandXSignIn(XSignIn plugin)
	{
		super("xsignin", "xsignin.login");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("login", "l"));
		subcommands.add(new XSubCommand("register", "r", "re"));
		subcommands.add(new XSubCommand("change"));
		subcommands.add(new XSubCommand("set"));
		subcommands.add(new XSubCommand("reset"));
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
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xsignin <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (matchesSubCommand("login", args[0]))
		{
			if (!matchPermission(sender, "xsignin.login"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!(sender instanceof Player))
			{
				XUtil.sendFileMessage(sender, "command.no-consol-command");
				return XCommandReturnType.NEEDTOBEPLAYER;
			}
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use: /xsignin login <pw> " + ChatColor.YELLOW + "- for login");
				return XCommandReturnType.NONE;
			}
			
			Player target = (Player)sender;
			
			if (!this.plugin.getLoginManager().isRegisterd(target))
				return XCommandReturnType.NONE;
			
			if (this.plugin.getLoginManager().isPlayerLoggedIn(target))
			{
				XUtil.sendFileMessage(target, "command.already-logged-in", ChatColor.GREEN);
				return XCommandReturnType.NONE;
			}
			
			if (this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
			{
				PassMatch passmatch = XUtil.matchPasswordRules(args[1]);
				if (!passmatch.equals(PassMatch.OK))
				{
					XUtil.sendFileMessage(target, "command.not-match-" + passmatch, ChatColor.RED);
					XUtil.sendCommandUsage(sender, "use: /xsignin change <oldpw> <newpw> " + ChatColor.YELLOW + "- for changing Password");
					return XCommandReturnType.NONE;
				}
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
			return XCommandReturnType.SUCCESS;
		}

		if (matchesSubCommand("register", args[0]))
		{
			if (!matchPermission(sender, "xsignin.login"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!(sender instanceof Player))
			{
				XUtil.sendFileMessage(sender, "command.no-consol-command");
				return XCommandReturnType.NEEDTOBEPLAYER;
			}
			
			if (args.length < 3)
			{
				XUtil.sendCommandUsage(sender, "use /xsignin register <password> <password>");
				return XCommandReturnType.NONE;
			}

			if (!args[1].equals(args[2]))
			{
				XUtil.sendFileMessage(sender, "command.xsignin.register.error-pw", ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			Player target = (Player)sender;
			
			if (this.plugin.getLoginManager().isPlayerLoggedIn(target))
			{
				XUtil.sendFileMessage(target, "command.already-logged-in", ChatColor.GREEN);
				return XCommandReturnType.NONE;
			}
			
			PassMatch passmatch = XUtil.matchPasswordRules(args[1]);
			if (!passmatch.equals(PassMatch.OK))
			{
				XUtil.sendFileMessage(target, "command.not-match-rules", ChatColor.RED);
				XUtil.sendFileMessage(target, "command.not-match-" + passmatch, ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			
			if (this.plugin.getLoginManager().registerPlayer(this.plugin.getLoginManager().getPlayer(target.getUniqueId()), args[1]))
			{
				XUtil.sendFileMessage((Player)sender, "command.xsignin.register.success", ChatColor.GREEN);
				this.plugin.getLoginManager().getPlayer(target.getUniqueId()).registerPlayer();
			}
			return XCommandReturnType.SUCCESS;
		}

		if (matchesSubCommand("change", args[0]))
		{
			if (!matchPermission(sender, "xsignin.login.change"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!(sender instanceof Player))
			{
				XUtil.sendFileMessage(sender, "command.no-consol-command");
				return XCommandReturnType.NEEDTOBEPLAYER;
			}
			
			if (args.length < 3)
			{
				XUtil.sendCommandUsage(sender, "use /xsignin change <oldpw> <newpw>");
				return XCommandReturnType.NONE;
			}
			
			Player target = (Player)sender;
			
			if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
				return XCommandReturnType.NONE;			
			
			if (!this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
			{
				XUtil.sendFileMessage(target, "command.wrong-pw", ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			PassMatch passmatch = XUtil.matchPasswordRules(args[2]);
			if (!passmatch.equals(PassMatch.OK))
			{
				XUtil.sendFileMessage(target, "command.not-match-rules", ChatColor.RED);
				XUtil.sendFileMessage(target, "command.not-match-" + passmatch, ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			if (this.plugin.getLoginManager().setPlayerPassword(target, args[2]))
			{
				XUtil.sendFileMessage(target, "command.xsignin.change.success", ChatColor.GREEN);
			}
			return XCommandReturnType.SUCCESS;
		}

		if (matchesSubCommand("reset", args[0]))
		{
			if (!matchPermission(sender, "xsignin.login.reset"))
				return XCommandReturnType.NOPERMISSION;
			
			if (!(sender instanceof Player))
			{
				XUtil.sendFileMessage(sender, "command.no-consol-command");
				return XCommandReturnType.NEEDTOBEPLAYER;
			}
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use /xsignin reset <pw>");
				return XCommandReturnType.NONE;
			}
			
			Player target = (Player)sender;
			
			if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
				return XCommandReturnType.NONE;
			
			if (!this.plugin.getLoginManager().isPlayerLoggedIn(target))
			{
				XUtil.sendCommandUsage(target.getPlayer(), "use /xsignin login <pw>");
				return XCommandReturnType.NONE;
			}
			
			
			
			if (!this.plugin.getLoginManager().checkPlayerPassword(target, args[1]))
			{
				XUtil.sendFileMessage(target, "command.wrong-pw", ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			if (this.plugin.getLoginManager().resetPlayer(target))
			{
				this.plugin.getLoginManager().getPlayer(target.getUniqueId()).logPlayerOut();
				XUtil.sendFileMessage(target, "command.xsignin.reset.success", ChatColor.GREEN);
			}
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("set", args[0]))
		{
			if (!matchPermission(sender, "xsignin.login.set"))
				return XCommandReturnType.NOPERMISSION;
			
			if (args.length < 2)
			{
				XUtil.sendCommandUsage(sender, "use /xsignin set <player> <pw>");
				return XCommandReturnType.NONE;
			}
			
			Player target = Bukkit.getServer().getPlayer(args[1]);
			if (target == null)
			{
				XUtil.sendFileMessage(sender, "command.player-not-found", ChatColor.RED);
				return XCommandReturnType.NOPLAYERMATCH;
			}
			
			if (!this.plugin.getLoginManager().isRegisterd((Player)sender))
				return XCommandReturnType.NONE;
			
			PassMatch passmatch = XUtil.matchPasswordRules(args[2]);
			if (!passmatch.equals(PassMatch.OK))
			{
				XUtil.sendFileMessage(target, "command.not-match-rules", ChatColor.RED);
				XUtil.sendFileMessage(target, "command.not-match-" + passmatch, ChatColor.RED);
				return XCommandReturnType.NONE;
			}
			
			if (this.plugin.getLoginManager().setPlayerPassword(target, args[2]))
			{
				XUtil.sendFileMessage((Player)sender, "command.xsignin.set.success-sender", ChatColor.GREEN);
				XUtil.sendFileMessage(target, "command.xsignin.set.success-target", ChatColor.GREEN);
			}
			return XCommandReturnType.SUCCESS;
		}

		return XCommandReturnType.NOCOMMAND;
	}
}
