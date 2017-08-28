package de.mark615.xsignin;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.mark615.xsignin.object.XUtil;

public class SettingManager
{
    static SettingManager instance = new SettingManager();
   
    public static SettingManager getInstance()
    {
    	return instance;
    }
    
    FileConfiguration config;
    File cFile;
    
    FileConfiguration message;
    File mFile;
    
    private int dataID;
   
	public void setup(Plugin p)
    {
    	if (!p.getDataFolder().exists())
    		p.getDataFolder().mkdir();

    	//load config
    	cFile = new File(p.getDataFolder(), "config.yml");
    	if(!cFile.exists())
    		p.saveResource("config.yml", true);
		config = YamlConfiguration.loadConfiguration(cFile);
		config.options().copyDefaults(true);
		
		boolean updateConfig = (config.getConfigurationSection("login") == null);
		
		//Load default config
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getResource("config.yml"), "UTF-8"));
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(br);
			config.setDefaults(defConfig);	
		}
		catch(Exception e)
		{
			XUtil.severe("cant copy default config.yml", e);
		}
        
		
        //load message
        mFile = new File(p.getDataFolder(), "messages.yml");
        if(!mFile.exists())
			p.saveResource("messages.yml", true);
		message = YamlConfiguration.loadConfiguration(mFile);
		message.options().copyDefaults(true);
		
		//Load default messages
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getResource("messages.yml"), "UTF-8"));
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(br);
			message.setDefaults(defConfig);	
		}
		catch(Exception e)
		{
			XUtil.severe("cant copy default message.yml", e);
		}
		try
		{
			message.save(mFile);
		}
		catch (IOException e)
		{
			XUtil.severe("Could not save message.yml!");
		}
		
		
		if (updateConfig)
		{
			config.set("login.auto-relog", config.getBoolean("auto-relog"));
			config.set("login.auto-relog-min", config.getBoolean("auto-relog-min"));
			config.set("login.password.length", config.getBoolean("password.length"));
			config.set("login.password.digitChar", config.getBoolean("password.digitChar"));
			config.set("login.password.upperAndLowerChar", config.getBoolean("password.upperAndLowerChar"));
			config.set("login.password.specialChar", config.getBoolean("password.specialChar"));

			config.set("auto-relog", null);
			config.set("auto-relog-min", null);
			config.set("password", null);
			
			saveConfig();
		}
    }
    
   
//---------Configuration section
    
    public FileConfiguration getConfig()
    {
        return config;
    }
   
    public void saveConfig()
    {
        try {
            config.save(cFile);
        }
        catch (IOException e) {
        	XUtil.severe("Could not save config.yml!");
        }
    }
   
    public void reloadConfig()
    {
    	config = YamlConfiguration.loadConfiguration(cFile);
    }
    
    
    
    public boolean hasFirstJoinMessage()
    {
    	return config.getBoolean("first-join-message", true);
    }
    
    public boolean hasAutoRelog()
    {
    	return config.getBoolean("login.auto-relog", false);
    }
    
    public int getAutoRelogTime()
    {
    	return (config.getInt("login.auto-relog-min", 5) * 1000);
    }
    
    public int getLoginMessageIntervall()
    {
    	return (config.getInt("login-message-intervall", 10) * 1000);
    }
    
    public boolean hasCheckVersion()
    {
    	return config.getBoolean("updatecheck", true);
    }
    
    public boolean isLogin()
    {
    	return config.getBoolean("login.enable", true);
    }
    
    public boolean isMaintenance()
    {
    	return config.getBoolean("maintenance", false);
    }
    
    public boolean isAGBEnbale()
    {
    	return config.getBoolean("agb.enable", false);
    }
    
    public void setAGBEnabled(boolean value)
    {
    	config.set("agb.enable", value);
    }
    
    public List<String> getAGBMessage()
    {
    	return config.getStringList("agb.message");
    }
    
    public int needPasswordLength()
    {
    	return config.getInt("login.password.length", 0);
    }
    
    public boolean needPasswordUpperAndLower()
    {
    	return config.getBoolean("login.password.upperAndLowerChar", false);
    }
    
    public boolean needPasswordSpecialChar()
    {
    	return config.getBoolean("login.password.specialChar", false);
    }
    
    public boolean needPasswordDigitChar()
    {
    	return config.getBoolean("login.password.digitChar", false);
    }
	
	public boolean isWhitelist()
	{
    	return config.getBoolean("whitelist", false);
	}
	
	public boolean isBlacklist()
	{
    	return config.getBoolean("blacklist", false);
	}
	
	public void setWhitelist(boolean value)
	{
		config.set("whitelist", value);
	}
	
	public void setBlacklist(boolean value)
	{
		config.set("blacklist", value);
	}
    
    
    
    
    public void setAPIKey(UUID uuid)
    {
    	config.set("apikey", uuid.toString());
    }
    
    public UUID getAPIKey()
    {
    	return config.getString("apikey", null) == null ? null : UUID.fromString(config.getString("apikey"));
    }
    
    public void setDataID(int dataID)
    {
    	this.dataID = dataID;
    }
    
    public int getDataID()
    {
    	return dataID;
    }
    

//---------Message section
    
    public FileConfiguration getMessage()
    {
        return message;
    }
   
    public void reloadMessage()
    {
    	message = YamlConfiguration.loadConfiguration(mFile);
    }
}
