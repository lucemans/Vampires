package nl.lucemans.Core;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import nl.lucemans.Core.race.Race;
import nl.lucemans.Core.role.Role;

public class UserData {
	
	/* Data */
	public String user;
	public String uuid;
	
	/* Time */
	public String time = "UPDATE";
	
	/* Races */
	public String raceStr = "";
	public Race race = null;
	
	/* Roles */
	private ArrayList<Role> roles = new ArrayList<Role>();
	public ArrayList<String> rolesToAdd = new ArrayList<String>();
	public String prefix = "";
	
	//*** FUNCTIONS ***/
	public void resetRace()
	{
		race = null;
		raceStr = "Human";
	}
	
	public String getPersonalPrefix()
	{
		return prefix;
	}
	
	public String getFullPrefix()
	{
		String _prefix = "";
		for (Role r : getRolePrior())
		{
			_prefix += r.prefix;
		}
		return _prefix;
	}
	
	public String getRolePrefix()
	{
		if (roles.size() > 0)
			return getRolePrior().get(0).prefix;
		return "";
	}
	
	public String getVaultPrefix(Player player)
	{
		return LucemansCore.main.chat.getPlayerPrefix(player);
	}
	
	public String getGroupPrefix(Player p)
	{
		return LucemansCore.main.chat.getGroupPrefix(p.getWorld(), LucemansCore.main.chat.getPrimaryGroup(p));
	}
	
	public String getClanPrefix()
	{
		return LucemansCore.main.clanman.getUser(user).getPrimaryClan().prefix; //TODO: CHANGE TO PREFIX OF CLAN
	}
	
	public String getClanRolePrefix()
	{
		return LucemansCore.main.clanman.getUser(user).getPrimaryClanPrefix();
	}
	
	public ArrayList<Role> getRolePrior()
	{
		ArrayList<Role> list = new ArrayList<Role>();
		
		for (Role r : roles)
		{
			Integer pos = 0;
			for (Role d : list)
			{
				if (r.priority >= d.priority)
				{
					pos += 1;
				}
				else
				{
					break;
				}
			}
			list.add(pos, r);
		}
		
		return list;
	}
	
	public Role getRole(Role r)
	{
		for (Role d : roles)
		{
			if (d.equals(r))
				return d;
		}
		return null;
	}
	
	public void refreshRoles(Main m)
	{
		if (rolesToAdd.size() > 0)
		{
			ArrayList<String> removeAfter = new ArrayList<String>();
			ArrayList<Role> addAfter = new ArrayList<Role>();
			for (String _r : rolesToAdd)
			{
				for (Role r : m.roles)
				{
					if (r.name != null)
					if (r.name.equalsIgnoreCase(_r))
					{
						addAfter.add(r);
						removeAfter.add(_r);
					}
				}
				for (Role r : addAfter)
				{
					roles.add(r);
				}
				addAfter.clear();
			}
			for (String r : removeAfter)
			{
				rolesToAdd.remove(r);
			}
			removeAfter.clear();
		}
	}
}
