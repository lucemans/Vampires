package nl.lucemans.Core.settings;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.md_5.bungee.api.ChatColor;
import nl.lucemans.Core.Main;

public class SettingsManager {

	public File langFile;
	public FileConfiguration lang;
	public Main m;
	
	public void init(Main m)
	{
		this.m = m;
		
		if (!m.getDataFolder().exists())
		{
			try {
				m.getDataFolder().createNewFile();
			} catch (IOException e)
			{
				m.getLogger().severe(ChatColor.RED + "Could not create config folder");
			}
		}
		
		langFile = new File(m.getDataFolder(), "lang.yml");
		
		if (!langFile.exists())
		{
			try {
				langFile.createNewFile();
			} catch (IOException e)
			{
				m.getLogger().severe(ChatColor.RED + "Could not create lang.yml");
			}
		}
		
		lang = YamlConfiguration.loadConfiguration(langFile);
	}
	
	public void shutdown()
	{
		saveLang();
	}
	
	/*** Utils */
	
	public void saveLang()
	{
		try{
			lang.save(langFile);
		} catch (Exception e) {
			m.getLogger().severe(ChatColor.RED + "Could not save lang.yml");
		}
	}
	
	public void reloadLang()
	{
		lang = YamlConfiguration.loadConfiguration(langFile);
	}

	public String getLang(String key) {
		String str = lang.getString(key);
		if (str != null)
			return str;
		return "";
	}
	
	public FileConfiguration getLang()
	{
		return lang;
	}

	public void setDefaultLang(String s, String k) {
		String str = lang.getString(s);
		if (str == "" || str == null)
		{
			lang.set(s, k);
			// save file
			saveLang();
		}
	}
}
