package nl.lucemans.Core.skin;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;

import skinsrestorer.shared.api.SkinsRestorerAPI;

public class SkinManipulator {
	
	public HashMap<String, ArrayList<SkinChange>> skins = new HashMap<String, ArrayList<SkinChange>>();
	public HashMap<String, String> prevSkins = new HashMap<String, String>();
	
	public ArrayList<SkinChange> getSkins(String str)
	{
		for (String key : skins.keySet())
			if (key.equalsIgnoreCase(str))
				return skins.get(key);
		ArrayList<SkinChange> list = new ArrayList<SkinChange>();
		skins.put(str, list);
		return list;
	}
	
	public void applySkin(String user, SkinChange skin)
	{
		ArrayList<SkinChange> curList = getSkins(user);
		//ArrayList<SkinChange> removeFrom = new ArrayList<SkinChange>();
		/*for (SkinChange sc : curList)
		{
			if (sc.reason.equalsIgnoreCase(skin.reason))
			{
				removeFrom.add(sc);
			}	
		}
		for (SkinChange sc : removeFrom)
			curList.remove(sc);*/
		if (curList.size() <= 0)
		{
			prevSkins.put(user, getSkin(user));
		}
		curList.add(skin);
		skins.put(user, curList);
		Bukkit.getLogger().info("ADDED A SKIN for " + user);
	}
	
	public void removeSkin(String user, String reason)
	{
		ArrayList<SkinChange> curList = getSkins(user);
		ArrayList<SkinChange> removeFrom = new ArrayList<SkinChange>();
		for (SkinChange sc : curList)
		{
			removeFrom.add(sc);
		}
		for (SkinChange sc : removeFrom)
		{
			curList.remove(sc);
			Bukkit.getLogger().info("Removed " + sc.reason);
		}
		skins.put(user, curList);
		Bukkit.getLogger().info("REMOVED A SKIN for " + user);
	}
	
	public String getPrevSkin(String user)
	{
		if (prevSkins.containsKey(user))
			return prevSkins.get(user);
		String ski = SkinsRestorerAPI.getSkinName(user);
		if (ski != null)
			return ski;
		return user;
	}
	
	public String getSkin(String user)
	{
		ArrayList<SkinChange> curList = getSkins(user);
		SkinChange dominant = new SkinChange(user, "Default", getPrevSkin(user), 0);
		if (curList.size() <= 0)
		{
			String last = getPrevSkin(user);
			if (prevSkins.containsKey(user))
				prevSkins.remove(user);
			return last;
		}
		for (SkinChange sc : curList)
		{
			if (sc.priority >= dominant.priority)
				dominant = sc;
		}
		return dominant.skin;
	}

	public Boolean hasSkin(String user, String reason) {
		ArrayList<SkinChange> curList = getSkins(user);
		for (SkinChange sc : curList)
		{
			if (sc.reason.equalsIgnoreCase(reason))
				return true;
		}
		return false;
	}
}
