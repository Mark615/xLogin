package de.mark615.xsignin;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mark615.xsignin.database.XAGBDatabase;
import de.mark615.xsignin.object.XPlayerSubject;

public class AGBManager
{
	private XAGBDatabase db;
	private XSignIn plugin;
	private int version;
	private boolean enabled;

	public AGBManager(XSignIn plugin, XAGBDatabase db)
	{
		this.plugin = plugin;
		this.db = db;
		this.version = 0;
		this.enabled = SettingManager.getInstance().isAGBEnbale();
		if (this.enabled)
			hasAGBChanged();
	}
	
	private void hasAGBChanged()
	{
		db.loadAGB();
		try
		{
			this.version = db.getAGBVersion();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void refreshAGBState()
	{
		this.enabled = SettingManager.getInstance().isAGBEnbale();
	}
	
	public boolean hasXPlayerAcceptAGB(UUID uuid)
	{
		if (!this.enabled)
			return true;
		
		boolean value = false;
		try
		{
			XPlayerSubject subject = plugin.getLoginManager().getPlayer(uuid);
			if (subject == null)
				return false;
			value = db.hasXPlayerAcceptAGB(subject.getDBID(), version);
			if (value)
				subject.setAGBAccepted();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
	public void setXPlayerAcceptAGB(Player p)
	{
		try
		{
			XPlayerSubject subject = plugin.getLoginManager().getPlayer(p.getUniqueId());
			if (subject == null)
				return;
			db.setXPlayerAcceptAGB(subject.getDBID(), version);
			subject.setAGBAccepted();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
	
}
