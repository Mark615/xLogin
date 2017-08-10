package de.mark615.xsignin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.bukkit.entity.Player;

import de.mark615.xsignin.object.XPlayerSubject;
import de.mark615.xsignin.object.XUtil;

public class XDatabase
{
	private XAGBDatabase dbAGB;
	private XWBLDatabase dbWBL;
	private Connection con;
	private Statement stmt;
	
	public XDatabase()
	{
		this.con = null;
		this.stmt = null;
		this.dbAGB = new XAGBDatabase(this);
		this.dbWBL = new XWBLDatabase(this);
	}
	
	public boolean isValid()
	{
	    try {
	    	Class.forName("org.sqlite.JDBC");
	    	con = DriverManager.getConnection("jdbc:sqlite:plugins/xSignIn/xsignin.db");
	    } catch ( Exception e )
	    {
	    	XUtil.severe("Can't open database");
	    	XUtil.severe(e.getMessage());
	    	return false;
	    }
	    
	    try
	    {
	    	loadDatabase();
	    	dbAGB.loadDatabase(con);
	    	dbWBL.loadDatabase(con);
	    }
	    catch (SQLException e)
	    {
	    	XUtil.severe("Can't load database");
	    	XUtil.severe(e.getMessage());
	    }
	    return true;
	}
	
	private void loadDatabase() throws SQLException
	{
		//user database
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS xuser (id INTEGER PRIMARY KEY, uuid TEXT not null, name TEXT not null, password TEXT not null, lastlogin INTEGER, lastlogout INTEGER, logincounter INTEGER)");
		stmt.close();
	}
	
	public void registerXPlayerSubject(XPlayerSubject subject, String pw) throws SQLException
	{
		generateXPlayer(subject, pw);
	}
	
	public void unregisterXPlayerSubject(XPlayerSubject subject) throws SQLException
	{
		saveXPlayer(subject);
	}
	
	public XPlayerSubject loadXPlayerSubject(UUID uuid)
	{
		XPlayerSubject subject = null;
		try
		{
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT id, uuid FROM xuser WHERE uuid = '" + uuid.toString() + "'");
			if (res.next())
			{
				subject = new XPlayerSubject(res.getInt("id"), uuid);
			}
			else
			{
				subject = new XPlayerSubject(0, uuid);
			}
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return subject;
	}
	
	public boolean hasPlayerAccount(UUID target)
	{
		boolean result = false;
		try
		{
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT uuid FROM xuser WHERE uuid = '" + target.toString() + "'");
			if (res.next())
			{
				result = true;
			}
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public void setPlayerPassword(UUID target, String pw) throws SQLException
	{
		stmt = con.createStatement();
		stmt.execute("UPDATE xuser set password = '" + pw + "' WHERE uuid = '" + target.toString() + "'");
		stmt.close();
	}
	
	public void resetPlayer(UUID target) throws SQLException
	{
		stmt = con.createStatement();
		stmt.execute("DELETE FROM xuser WHERE uuid = '" + target.toString() + "'");
		stmt.close();
	}
	
	public boolean checkPassword(UUID target, String pw) throws SQLException
	{
		boolean result = false;
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT uuid, name, password from xuser where uuid = '" + target.toString() + "'");
		if (res.next())
		{
			if (res.getString("password").equals(pw))
			{
				result = true;
			}
		}
		stmt.close();
		
		return result;
	}
	
	public boolean loginPlayer(Player target, String pw) throws SQLException
	{
		boolean result = false;
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT uuid, name, password from xuser where uuid = '" + target.getUniqueId().toString() + "'");
		if (res.next())
		{
			if (res.getString("password").equals(pw))
			{
				result = true;
			}
		}
		stmt.close();
		if (result)
		{
			stmt = con.createStatement();
			stmt.execute("UPDATE xuser set lastlogin = " + System.currentTimeMillis() + ", " +
					"logincounter = ((SELECT logincounter FROM xuser WHERE uuid = '" + target.getUniqueId().toString() + "') + 1), " +
					"name = '" + target.getName() + "' " + 
					"WHERE uuid = '" + target.getUniqueId().toString() + "'");
			stmt.close();
		}
		
		return result;
	}
	
	
	
	private void generateXPlayer(XPlayerSubject subject, String password) throws SQLException
	{
		stmt = con.createStatement();
		stmt.execute("INSERT INTO xuser(uuid, name, password, lastlogin, lastlogout, logincounter) " +
				"values ('" + subject.getUUID().toString() + "', '" + subject.getPlayer().getName() + "', '" + password + "', " + System.currentTimeMillis() + ", 0, 1)");
		stmt.close();
	}
	
	private void saveXPlayer(XPlayerSubject subject) throws SQLException
	{	
		stmt = con.createStatement();
		stmt.execute("UPDATE xuser SET lastlogin = " + subject.getLoginTime() + ", lastlogout = " + subject.getLogoutTime() + " WHERE uuid = '" + subject.getUUID().toString() + "'");
		stmt.close();
	}
	
	
	
	public XAGBDatabase getAGBDatabse()
	{
		return dbAGB;
	}
	
	public XWBLDatabase getWBLDatabase()
	{
		return dbWBL;
	}
	
	public int getXPlayerSubjectID(UUID uuid)
	{
		int id = 0;
		try
		{
			stmt = con.createStatement();
			ResultSet res = stmt.executeQuery("SELECT id, uuid FROM xuser WHERE uuid = '" + uuid.toString() + "'");
			if (res.next())
			{
				id = res.getInt("id");
			}
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return id;
	}
	
}
