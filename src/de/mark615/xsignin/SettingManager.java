package de.mark615.xsignin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
   
    @SuppressWarnings("deprecation")
	public void setup(Plugin p)
    {
    	if (!p.getDataFolder().exists())
    		p.getDataFolder().mkdir();
    	
    	cFile = new File(p.getDataFolder(), "config.yml");
    	if(!cFile.exists())
    		p.saveResource("config.yml", true);

		//Store it
		config = YamlConfiguration.loadConfiguration(cFile);
		config.options().copyDefaults(true);
		
		//Load default messages
		InputStream defConfigStream = p.getResource("config.yml");
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		config.setDefaults(defConfig);
		
        
        
        mFile = new File(p.getDataFolder(), "messages.yml");
        if(!mFile.exists())
			p.saveResource("messages.yml", true);
		
		//Store it
		message = YamlConfiguration.loadConfiguration(mFile);
		message.options().copyDefaults(true);
		
		//Load default messages
		InputStream defMessageStream = p.getResource("messages.yml");
		YamlConfiguration defMessages = YamlConfiguration.loadConfiguration(defMessageStream);
		message.setDefaults(defMessages);
		try
		{
			message.save(mFile);
		}
		catch (IOException e)
		{
			XUtil.severe("Could not save message.yml!");
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
    	return config.getBoolean("auto-relog", false);
    }
    
    public int getAutoRelogTime()
    {
    	return (config.getInt("auto-relog-min", 5) * 1000);
    }
    
    public int getLoginMessageIntervall()
    {
    	return (config.getInt("login-message-intervall", 10) * 1000);
    }
    
    public boolean hasCheckVersion()
    {
    	return config.getBoolean("updatecheck", true);
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
