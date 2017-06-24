package nl.lucemans.Core;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_11_R1.Packet;
import nl.lucemans.Core.item.Item;
import nl.lucemans.Core.race.Human;
import nl.lucemans.Core.race.Race;
import nl.lucemans.Core.role.Role;
import nl.lucemans.Core.settings.LangParse;
import nl.lucemans.Core.skin.SkinChange;
import nl.lucemans.Core.tp.DelayedTP;
import nl.lucemans.Core.type.LucemansListener;
import nl.lucemans.animation.effects.Effect;

/*
 * Made by Lucemans (MrDisk)
 */
public class LucemansCore {
	
	public static Main main;
	
	public String parse(String text)
	{
		text = text.replace("&", "§");
		return text;
	}
	
	public String parse(String text, Player p, HashMap<String, String> h)
	{		
		LangParse parse = new LangParse();
		parse.placeholders.putAll(h);
		parse.placeholders.put("%p-prefix%", getUser(p.getName()).getPersonalPrefix());
		parse.placeholders.put("%f-prefix%", getUser(p.getName()).getFullPrefix());
		parse.placeholders.put("%r-prefix%", getUser(p.getName()).getRolePrefix());
		parse.placeholders.put("%v-prefix%", getUser(p.getName()).getVaultPrefix(p));
		parse.placeholders.put("%c-clan%", getUser(p.getName()).getClanPrefix());
		parse.placeholders.put("%cr-prefix%", getUser(p.getName()).getClanRolePrefix());
		parse.placeholders.put("%race%", getUser(p.getName()).raceStr);
		parse.placeholders.put("%name%", p.getName());
		
		text = parse.parse(this, text, true);

		return text;
	}
	
	public UserData getUser(String name)
	{
		for (UserData data : main.userDatas)
		{
			if (data != null)
			{
				if ((data.user != null ? data.user.equalsIgnoreCase(name) : false) || (data.uuid != null ? data.uuid.equalsIgnoreCase(name) : false))
				{
					data.refreshRoles(main);
					return data;
				}
			}
		}
		
		UserData newData = main.getNewData(name);
		main.userDatas.add(newData);
		
		return newData;
	}
	
	public Player getPlayer(String name)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (p.getName().equalsIgnoreCase(name) || p.getUniqueId().toString().equalsIgnoreCase(name))
				return p;
		}
		return null;
	}
	
	public Race getRace(UserData data)
	{
		if (!data.raceStr.equalsIgnoreCase(""))
		{
			for (Race r : main.races)
			{
				if (data.raceStr.equalsIgnoreCase(r.raceName))
				{
					try {
						data.race = r.getClass().newInstance();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		
		if (data.race != null)
			return data.race;
		
		data.race = new Human();
		
		return data.race;
	}
	
	public String getTimeString(World w)
	{
		long time = w.getTime();
		return (time < 12300 || time > 23650) ? "DAY" : "NIGHT";
	}
	
	public Integer getDaysTillFullMoon(World world)
	{
		Integer daysLeft = -100000000;
		
		Integer phase = getPhase(world);
		daysLeft = 4 - phase;
		if (daysLeft < 0)
		{
			daysLeft += 8;
		}
		
		return daysLeft;
	}
	
	public String getMoonPhase(World world)
	{
		Integer phase = getPhase(world);
		if (phase == 0)
			return "New Moon";
		if (phase == 1)
			return "Waxing Crescent";
		if (phase == 2)
			return "First Quarter";
		if (phase == 3)
			return "Waxing Gibbous";
		if (phase == 4)
			return "Full Moon";
		if (phase == 5)
			return "Waning Gibbous";
		if (phase == 6)
			return "Third Quarter";
		if (phase == 7)
			return "Waning Crescent";
		return "Unknown #"+phase;
	}
	
	public Integer getPhase(World world)
	{
		Integer days = (int) (world.getFullTime()/24000);
		Integer phase = days%8;
		return phase;
	}
	
	// SKINS //
	public void applySkin(String user, SkinChange skin)
	{
		main.skinman.applySkin(user, skin);
	}
	
	public void removeSkin(String user, String reason)
	{
		main.skinman.removeSkin(user, reason);
	}
	
	public boolean hasSkin(String user, String reason)
	{
		return main.skinman.hasSkin(user, reason);
	}
	
	// ITEMS //
	public boolean exists(String loc)
	{
		return main.itemman.exists(loc);
	}
	
	public Item getItem(String loc)
	{
		return main.itemman.getItem(loc);
	}

	public boolean compare(ItemStack item, String loc)
	{
		return main.itemman.compare(item, loc);
	}
	
	// Language handler
	public String getLang(String key)
	{
		return main.setman.getLang(key);
	}
	
	public void addOrSetLang(String key, String value)
	{
		main.setDefaultLang(key, value);
	}
	
	// Animation handler
	public void playEffect(Effect ef)
	{
		main.animan.playEffect(ef);
	}
	
	public void sendToAll(Packet p, ArrayList<Player> players)
	{
		for (Player _p : players)
		{
			((CraftPlayer)_p).getHandle().playerConnection.sendPacket(p);
		}
	}
	
	// Teleport Handler
	public void delayTeleport(Player p, Location to, Integer delay)
	{
		main.dtps.add(new DelayedTP(p, to, delay));
	}
	
	public void delayTeleport(Player p, Location to, Integer delay, Effect ef)
	{
		main.dtps.add(new DelayedTP(p, to, delay, ef));
	}
	
	/*** Handler setup ***/
	
	public void registerListener(LucemansListener c)
	{
		main.listeners.add(c);
		main.getLogger().info(parse("Registered Listener "));
	}
	
	public void registerRace(Race r)
	{
		main.races.add(r);
		main.getLogger().info(r.getClass().getName() + " has been registered as a Race.");
	}
	
	public void registerRole(Role r)
	{
		main.roles.add(r);
		main.getLogger().info(r.name + " has been registered as a Role");
	}
	
	public void registerItem(String loc, Item item)
	{
		main.itemman.registerItem(loc, item);
	}
	
	public void setMain(Main m)
	{
		main = m;
	}
	
	public static LucemansCore getINSTANCE() {
		return main.core;
	}
}
