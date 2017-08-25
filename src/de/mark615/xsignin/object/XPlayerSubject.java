package de.mark615.xsignin.object;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.mark615.xsignin.XSignIn;

public class XPlayerSubject
{
	private int DBID;
	private boolean loggedIN;
	private boolean agbaccepted;
	private String loginIP;
	private long loginTime;
	private long logoutTime;
	private long lastloginInfo;
	private UUID uuid;
	
	public XPlayerSubject(int dbid, UUID uuid)
	{
		this.loggedIN = false;
		this.DBID = dbid;
		this.uuid = uuid;
		this.loginIP = "0.0.0.0";
		this.loginTime = 0;
		this.logoutTime = 0;
		this.lastloginInfo = 0;
	}
	
	public boolean isLoggedIn()
	{
		return loggedIN;
	}
	
	public void setAGBAccepted()
	{
		this.agbaccepted = true;
	}
	
	public void logPlayerIn()
	{
		this.loggedIN = true;
		this.loginIP = getPlayer().getAddress().getHostName();
		this.loginTime = System.currentTimeMillis();
		triggerLoginEvent();
	}
	
	public void registerPlayer()
	{
		this.DBID = XSignIn.getInstance().getLoginManager().getXPlayerSubjectID(uuid);
		this.loggedIN = true;
		this.loginIP = getPlayer().getAddress().getHostName();
		this.loginTime = System.currentTimeMillis();
		triggerLoginEvent();
	}
	
	public boolean autorelogPlayer()
	{
		if (loginIP.equals(getPlayer().getAddress().getHostName()))
		{	
			logPlayerIn();
			return true;
		}
		return false;
	}
	
	private void triggerLoginEvent()
	{
		if (XSignIn.getInstance() != null && XSignIn.getInstance().hasAPI())
			XSignIn.getInstance().getAPI().createPlayerLoggedInEvent(getPlayer());
	}
	
	public void logPlayerOut()
	{
		this.loggedIN = false;
		this.logoutTime = System.currentTimeMillis();
		this.lastloginInfo = 0;
		this.agbaccepted = false;
	}
	
	public int getDBID()
	{
		return DBID;
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayer(uuid);
	}
	
	public long getLoginTime()
	{
		return loginTime;
	}
	
	public long getLogoutTime()
	{
		return logoutTime;
	}
	
	public String getLoginIP()
	{
		return loginIP;
	}
	
	public long getLastLoginInfo()
	{
		return lastloginInfo;
	}
	
	public void setLastLoginInfo(long time)
	{
		this.lastloginInfo = time;
	}
	
	public boolean hasAGBAccepted()
	{
		return agbaccepted;
	}
}
