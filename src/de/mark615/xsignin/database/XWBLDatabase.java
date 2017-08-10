package de.mark615.xsignin.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import de.mark615.xapi.object.XUtil;
import de.mark615.xsignin.ListManager.ListElement;
import de.mark615.xsignin.ListManager.ListType;

public class XWBLDatabase
{
	private XDatabase db;
	private Statement stmt;
	private Connection con;
	
	public XWBLDatabase(XDatabase db)
	{
		this.db = db;
	}

	public void loadDatabase(Connection con) throws SQLException
	{
		this.con = con;
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS whitelist (id INTEGER PRIMARY KEY, value TEXT not null, type TEXT not null)");
		stmt.close();
		stmt = con.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS blacklist (id INTEGER PRIMARY KEY, value TEXT not null, type TEXT not null)");
		stmt.close();
	}
	
	
	
	//whitelist
	public List<ListElement> getWhitelist() throws SQLException
	{
		List<ListElement> list = new ArrayList<>();
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT * FROM whitelist");
		while (res.next())
		{
			list.add(new ListElement(res.getInt("id"), res.getString("value"), ListType.valueOf(res.getString("type"))));
		}
		stmt.close();
		return list;
	}
	
	public void addWhitelistElement(String value, ListType type)
	{
		try
		{
			stmt = con.createStatement();
			stmt.execute("INSERT INTO whitelist (value, type) VALUES ('" + value + "', '" + type + "')");
			stmt.close();
		}
		catch (SQLException e)
		{
			XUtil.info("Can't add whitelist element");
		}
	}
	
	public void removeWhitelistElement(ListElement element)
	{
		try
		{
			stmt = con.createStatement();
			stmt.execute("REMOVE FROM whitelist where id = " + element.getID());
			stmt.close();
		}
		catch (SQLException e)
		{
			XUtil.info("Can't remove whitelist element");
		}
	}
	
	
	
	//blacklist
	public List<ListElement> getBlacklist() throws SQLException
	{
		List<ListElement> list = new ArrayList<>();
		stmt = con.createStatement();
		ResultSet res = stmt.executeQuery("SELECT * FROM blacklist");
		while (res.next())
		{
			list.add(new ListElement(res.getInt("id"), res.getString("value"), ListType.valueOf(res.getString("type"))));
		}
		stmt.close();
		return list;
	}
	
	public void addBlacklistElement(String value, ListType type)
	{
		try
		{
			stmt = con.createStatement();
			stmt.execute("INSERT INTO blacklist (value, type) VALUES ('" + value + "', '" + type + "')");
			stmt.close();
		}
		catch (SQLException e)
		{
			XUtil.info("Can't add blacklist element");
		}
	}
	
	public void removeBlacklistElement(ListElement element)
	{
		try
		{
			stmt = con.createStatement();
			stmt.execute("REMOVE FROM blacklist where id = " + element.getID());
			stmt.close();
		}
		catch (SQLException e)
		{
			XUtil.info("Can't remove blacklist element");
		}
	}
	
}
