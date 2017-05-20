package de.mark615.xsignin.object;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.mark615.xsignin.XSignIn;

public class XPlayerSubject
{
	private boolean loggedIN;
	private String loginIP;
	private long loginTime;
	private long logoutTime;
	private long lastMessage;
	private UUID uuid;
	
	public XPlayerSubject(UUID uuid)
	{
		this.loggedIN = false;
		this.uuid = uuid;
		this.loginIP = "0.0.0.0";
		this.loginTime = 0;
		this.logoutTime = 0;
		this.lastMessage = 0;
	}
	
	public boolean isLoggedIn()
	{
		return loggedIN;
	}
	
	public void logPlayerIn()
	{
		this.loggedIN = true;
		this.loginIP = getPlayer().getAddress().getHostName();
		this.loginTime = System.currentTimeMillis();
		triggerLoginEvent();
	}
	
	public boolean autorelogPlayer()
	{
		if (loginIP.equals(getPlayer().getAddress().getHostName()))
		{	
			this.loggedIN = true;
			this.loginIP = getPlayer().getAddress().getHostName();
			this.loginTime = System.currentTimeMillis();
			triggerLoginEvent();
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
		this.lastMessage = 0;
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
	
	public long getLastMessage()
	{
		return lastMessage;
	}
	
	public void setLastMessage(long time)
	{
		this.lastMessage = time;
	}
}
