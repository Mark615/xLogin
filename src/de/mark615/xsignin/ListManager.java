package de.mark615.xsignin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mark615.xsignin.database.XWBLDatabase;
import de.mark615.xsignin.object.XUtil;

public class ListManager
{
	private XWBLDatabase db;
	private XSignIn plugin;
	private boolean whitelist;
	private boolean blacklist;

	public ListManager(XSignIn plugin, XWBLDatabase db)
	{
		this.plugin = plugin;
		this.db = db;
		refreshListState();
		
		if (whitelist)
		{
			XUtil.info("Whitelist is enabled");
			if (blacklist)
			{
				SettingManager.getInstance().setBlacklist(false);
				SettingManager.getInstance().saveConfig();
				this.blacklist = SettingManager.getInstance().isBlacklist();
				XUtil.info("Blacklist can't be active while Whitelist is active!");
			}
		}
		else
		if (blacklist)
			XUtil.info("Blacklist is enabled");
	}
	
	public void refreshListState()
	{
		this.whitelist = SettingManager.getInstance().isWhitelist();
		this.blacklist = SettingManager.getInstance().isBlacklist();
	}
	
	public boolean isPlayerAllowedToJoin(Player p)
	{
		if (whitelist)
		{
			return isPlayerOnWhitelist(p);
		}
		
		if (blacklist)
		{
			return (!isPlayerOnBlacklist(p));
		}
		
		return true;
	}
	
	public boolean isPlayerOnWhitelist(Player p)
	{
		boolean result = false;
		try
		{
			result = containslistPlayer(db.getWhitelist(), p);
		}
		catch (Exception e)
		{
			XUtil.severe("is player on whitelist error", e);
		}
		return result;
	}
	
	public boolean isElementOnWhitelist(String elm)
	{
		boolean result = false;
		try
		{
			result = containslistElement(db.getWhitelist(), elm);
		}
		catch (Exception e)
		{
			XUtil.severe("is element on whitelist error", e);
		}
		return result;
	}
	
	public boolean isPlayerOnBlacklist(Player p)
	{
		boolean result = false;
		try
		{
			result = containslistPlayer(db.getBlacklist(), p);
		}
		catch (Exception e)
		{
			XUtil.severe("is player on blacklist error", e);
		}
		return result;
	}
	
	public boolean isElementOnBlacklist(String elm)
	{
		boolean result = false;
		try
		{
			result = containslistElement(db.getWhitelist(), elm);
		}
		catch (Exception e)
		{
			XUtil.severe("is element on blacklist error", e);
		}
		return result;
	}
	
	private boolean containslistPlayer(List<ListElement> list, Player target)
	{
		for (ListElement element : list)
		{
			switch (element.getType())
			{
			case IP:
				if (target.getAddress().getAddress().getHostAddress().equals(element.getIP()))
					return true;
				break;
			case NAME:
				if (target.getName().equals(element.getName()))
					return true;
				break;
			case UUID:
				if (target.getUniqueId().equals(element.getUUID()))
					return true;
				break;
			default:
				break;
			}
		}
		return false;
	}
	
	private boolean containslistElement(List<ListElement> list, String elm)
	{
		for (ListElement element : list)
		{
			switch (element.getType())
			{
			case IP:
				if (elm.equals(element.getIP()))
					return true;
				break;
			case NAME:
				if (elm.equals(element.getName()))
					return true;
				break;
			case UUID:
				if (elm.equals(element.getUUID()))
					return true;
				break;
			default:
				break;
			}
		}
		return false;
	}
	
	public boolean isWhitelist()
	{
		return whitelist;
	}
	
	public void setWhitelist(boolean value)
	{
		SettingManager.getInstance().setWhitelist(value);
		if (value)
		{
			XUtil.info("Whitelist is enabled");
			SettingManager.getInstance().setBlacklist(false);
			if (this.plugin.hasXApiConnector())
				this.plugin.getXApiConnector().createBlacklistChangedStateEvent(false);
		}
		SettingManager.getInstance().saveConfig();
		this.plugin.getLoginManager().getListManager().refreshListState();
		
		if (this.plugin.hasXApiConnector())
			this.plugin.getXApiConnector().createWhitelistChangedStateEvent(value);
	}
	
	public boolean containsWhitelistElement(String value, ListType type)
	{
		try
		{
			return (matchElement(db.getWhitelist(), value, type) != null);
		}
		catch (SQLException e)
		{
			XUtil.severe("contains Whitelist element");
		}
		return false;
	}
	
	public void addWhitelistElement(String value, ListType type)
	{
		db.addWhitelistElement(value, type);
	}
	
	public void removeWhitelistElement(String value, ListType type)
	{
		try
		{
			ListElement elm = matchElement(db.getWhitelist(), value, type);
			if (elm != null)
				db.removeWhitelistElement(elm);
		}
		catch (SQLException e)
		{
			XUtil.severe("Can't remove Whitelist element");
		}
	}
	
	
	public boolean isBlacklist()
	{
		return blacklist;
	}
	
	public void setBlacklist(boolean value)
	{
		SettingManager.getInstance().setBlacklist(value);
		if (value)
		{
			XUtil.info("Blacklist is enabled");
			SettingManager.getInstance().setWhitelist(false);
			if (this.plugin.hasXApiConnector())
				this.plugin.getXApiConnector().createWhitelistChangedStateEvent(false);
		}
		SettingManager.getInstance().saveConfig();
		this.plugin.getLoginManager().getListManager().refreshListState();
		
		if (this.plugin.hasXApiConnector())
			this.plugin.getXApiConnector().createBlacklistChangedStateEvent(value);
	}
	
	public boolean containsBlacklistElement(String value, ListType type)
	{
		try
		{
			return (matchElement(db.getBlacklist(), value, type) != null);
		}
		catch (SQLException e)
		{
			XUtil.severe("contains Blacklist element");
		}
		return false;
	}
	
	public boolean addBlacklistElement(String value, ListType type)
	{
		db.addBlacklistElement(value, type);
		return true;
	}
	
	public void removeBlacklistElement(String value, ListType type)
	{
		try
		{
			ListElement elm = matchElement(db.getBlacklist(), value, type);
			if (elm != null)
				db.removeBlacklistElement(elm);
		}
		catch (SQLException e)
		{
			XUtil.severe("Can't remove Blacklist element");
		}
	}
	
	private ListElement matchElement(List<ListElement> list, String value, ListType type)
	{
		for (ListElement elm : list)
		{
			if (elm.getType().equals(type))
			{
				if (elm.getValue().equalsIgnoreCase(value))
					return elm;
			}
		}
		return null;
	}
	
	
	
	public static class ListElement
	{
		private int id;
		private String value;
		private ListType type;
		
		public ListElement(int id, String value, ListType type)
		{
			this.id = id;
			this.value = value;
			this.type = type;
		}
		
		public int getID()
		{
			return id;
		}
		
		public ListType getType()
		{
			return type;
		}
		
		public String getValue()
		{
			return value;
		}
		
		public UUID getUUID()
		{
			if (type.equals(ListType.UUID))
				return UUID.fromString(value);
			return null;
		}
		
		public String getIP()
		{
			if (type.equals(ListType.IP))
				return value;
			return null;	
		}
		
		public String getName()
		{
			if (type.equals(ListType.NAME))
				return value;
			return null;
		}
	}
	
	public static ListType checkListType(String value)
	{
		if (value == null)
			return null;
		
		if (value.equalsIgnoreCase("IP"))
			return ListType.IP;
		if (value.equalsIgnoreCase("UUID"))
			return ListType.UUID;
		if (value.equalsIgnoreCase("NAME"))
			return ListType.NAME;
		
		return null;
	}
	
	public enum ListType
	{
		IP,
		UUID,
		NAME,
	}
}
