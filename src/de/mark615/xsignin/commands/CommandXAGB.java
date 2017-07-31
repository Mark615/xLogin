package de.mark615.xsignin.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.mark615.xsignin.SettingManager;
import de.mark615.xsignin.XSignIn;
import de.mark615.xsignin.object.XUtil;

public class CommandXAGB extends XCommand
{
	private final XSignIn plugin;

	public CommandXAGB(XSignIn plugin)
	{
		super("xagb", "");
		this.plugin = plugin;
	}

	@Override
	public void fillSubCommands(List<XSubCommand> subcommands)
	{
		subcommands.add(new XSubCommand("accept", "a"));
		subcommands.add(new XSubCommand("decline", "r"));
		subcommands.add(new XSubCommand("show", "s"));
	}
	

	@Override
	protected void showHelp(CommandSender p)
	{
		p.sendMessage(ChatColor.GREEN + XSignIn.PLUGIN_NAME + ChatColor.GRAY + " - " + ChatColor.YELLOW + XUtil.getMessage("command.description"));
		p.sendMessage(ChatColor.GREEN + "/xagb accept" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xagb.accept.description"));
		p.sendMessage(ChatColor.GREEN + "/xagb decline" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xagb.decline.description"));
		p.sendMessage(ChatColor.GREEN + "/xagb show" + ChatColor.YELLOW + " - " + XUtil.getMessage("command.xagb.show.description"));
	}

	@Override
	public XCommandReturnType run(CommandSender sender, Command command, String s, String[] args)
	{
		if (!this.isSubCommand(args[0]))
		{
			XUtil.sendCommandUsage(sender, "use: /xagb <help/?> " + ChatColor.YELLOW + "- for help");
			return XCommandReturnType.NONE;
		}
		
		if (!(sender instanceof Player))
		{
			XUtil.sendFileMessage(sender, "command.no-consol-command");
			return XCommandReturnType.NEEDTOBEPLAYER;
		}

		Player target = (Player)sender;
		if (matchesSubCommand("accept", args[0]))
		{
			plugin.getLoginManager().getAGBManager().setXPlayerAcceptAGB(target);
			XUtil.sendFileMessage(target, "command.xagb.accept.success");
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("decline", args[0]))
		{
			target.kickPlayer(XUtil.getMessage("command.xagb.decline.error"));
			return XCommandReturnType.SUCCESS;
		}
		
		if (matchesSubCommand("show", args[0]))
		{
			List<String> agbs = SettingManager.getInstance().getAGBMessage();
			for (String agb : agbs)
			{
				XUtil.sendMessage(target, agb);
			}
			
			return XCommandReturnType.SUCCESS;
		}
		
		return XCommandReturnType.NOCOMMAND;
	}

}
