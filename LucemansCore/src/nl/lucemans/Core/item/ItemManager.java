package nl.lucemans.Core.item;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	
	public HashMap<String, Item> items = new HashMap<String, Item>();
	
	public Item getItem(String loc)
	{
		if (items.containsKey(loc))
			return items.get(loc);
		return null;
	}
	
	public void registerItem(String loc, Item item)
	{
		items.put(loc, item);
	}
	
	public boolean exists(String loc)
	{
		return items.containsKey(loc);
	}

	public void initRecipes()
	{
		for (Item i : items.values())
		{
			if (i.r != null)
			{
				Bukkit.addRecipe(i.r);
			}
		}
	}
	
	public boolean compare(ItemStack item, String loc)
	{
		ItemStack dupe = getItem(loc).getItem();
		if (dupe == null)
			return false;
		
		if (dupe.hasItemMeta() && item.hasItemMeta())
		{
			ItemMeta m1 = item.getItemMeta();
			ItemMeta m2 = dupe.getItemMeta();
			if (m1.getDisplayName().equalsIgnoreCase(m2.getDisplayName()))
			{
				if (m1.hasLore() && m2.hasLore())
				{
					if (m1.getLore().equals(m2.getLore()))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					if (!m1.hasLore() && !m2.hasLore())
					{
						
					}
					else
					{
						return false;
					}
				}
				return true;
			}
		}
		
		return false;
	}
}
