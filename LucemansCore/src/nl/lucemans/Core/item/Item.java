package nl.lucemans.Core.item;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;

import nl.lucemans.Core.LucemansCore;

public class Item {

	public String name = "";
	public Material type;
	public Integer amount;
	public ArrayList<String> lore;
	public Recipe r;
	
	public ItemStack getItem()
	{
		ItemStack item = new ItemStack(type, amount);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(LucemansCore.getINSTANCE().parse(name));
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		return item;
	}
}
