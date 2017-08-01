package de.mark615.xsignin.object;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.mark615.xsignin.SettingManager;

public class XAGBDatabase
{
	private XDatabase db;
	private Statement stmt;
	private Connection con;
	
	public XAGBDatabase(XDatabase db)
	{
		this.db = db;
	}

	public void loadDatabase(Connection con) throws SQLException
	{
		//agb database
		this.con = con;
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS xagb (date TEXT not null, hash TEXT not null, version INTEGER not null)");
		stmt.close();
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS xagbuser (xuserID INTEGER not null, date TEXT not null, version INTEGER)");
		stmt.close();
	}
	
	//agb database
	
	public void loadAGB()
	{
		String oldHash = null;
		String hash = hashCalc(SettingManager.getInstance().getAGBMessage());
		
		try
		{
			oldHash = getAGBHash();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			if (oldHash == null)
			{
				setAGBChangedHash(true, hash);
			}
			else
			if (!oldHash.equals(hash))
			{
				setAGBChangedHash(false, hash);
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	private String getAGBHash() throws SQLException
	{
		String hash = null;
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT hash FROM xagb");
		if (res.next())
		{
			hash = res.getString("hash");
		}
		stmt.close();
		
		return hash;
	}
	
	public int getAGBVersion() throws SQLException
	{
		int version = 0;
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT version FROM xagb");
		if (res.next())
		{
			version = res.getInt("version");
		}
		stmt.close();
		
		return version;
	}
	
	private void setAGBChangedHash(boolean insert, String hash) throws SQLException
	{
		int oldVersion = 0;
		try
		{
			oldVersion = getAGBVersion();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		Calendar c = Calendar.getInstance();
		String date = new SimpleDateFormat("YYYY-MM-dd 'at' HH:mm").format(c.getTime());

		stmt = con.createStatement();
		if (insert)
		{
			stmt.execute("INSERT INTO xagb (date, hash, version) values ('" + date + "', '" + hash + "', " + (oldVersion + 1) + ");");
		}
		else
		{
			stmt.execute("UPDATE xagb set hash = '" + hash + "', version = " + (oldVersion + 1) + ", date = '" + date + "';");
		}
		stmt.close();
	}
	
	private String hashCalc(List<String> list)
	{
		StringBuilder x = new StringBuilder();
		for (String key : list)
			x.append(key);
		
		return XUtil.toHash(x.toString());
	}
	
	
	public boolean hasXPlayerAcceptAGB(int dbID, int version) throws SQLException
	{
		boolean hasUser = false;
		try
		{
			hasUser = hasXPlayer(dbID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return false;
		}
		
		if (!hasUser)
			return hasUser;
		
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT xuserID, version FROM xagbuser where xuserID = " + dbID + " and version = " + version);
		if (res.next())
		{
			hasUser = true;
		}
		else
		{
			hasUser = false;
		}
		stmt.close();
		return hasUser;
	}
	
	public void setXPlayerAcceptAGB(int dbID, int version) throws SQLException
	{
		boolean hasUser = false;
		try
		{
			hasUser = hasXPlayer(dbID);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return;
		}
		
		Calendar c = Calendar.getInstance();
		String date = new SimpleDateFormat("YYYY-MM-dd 'at' HH:mm").format(c.getTime());
		
		if (hasUser)
		{
			stmt = con.createStatement();
			stmt.execute("UPDATE xagbuser set date = '" + date + "', version = " + version + " where xuserID = " + dbID);
			stmt.close();
		}
		else
		{
			stmt = con.createStatement();
			stmt.execute("INSERT INTO xagbuser (xuserID, date, version) values (" + dbID + ", '" + date + "', " + version + ");");
			stmt.close();
		}
	}
	
	private boolean hasXPlayer(int dbID) throws SQLException
	{
		boolean hasUser = false;
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT xuserID FROM xagbuser where xuserID = " + dbID);
		if (res.next())
		{
			hasUser = true;
		}
		stmt.close();
		return hasUser;
	}
}
