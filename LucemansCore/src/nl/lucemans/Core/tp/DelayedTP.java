package nl.lucemans.Core.tp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import nl.lucemans.Core.LucemansCore;
import nl.lucemans.animation.effects.Effect;

public class DelayedTP {

	public Player p;
	public Location to;
	public Integer delay;
	public Effect ef;
	
	public DelayedTP(Player p, Location to, Integer delay)
	{
		this.p = p;
		this.to = to;
		this.delay = delay;
	}
	
	public DelayedTP(Player p, Location to, Integer delay, Effect ef)
	{
		this.p = p;
		this.to = to;
		this.delay = delay;
		this.ef = ef;
	}
	
	public DelayedTP onTick()
	{
		Bukkit.getLogger().info("TP TICK");
		if (p == null)
		{
			return this;
		}
		if (ef != null)
		{
			if (LucemansCore.main.usesAnimations)
				ef.playTick();
		}
		if (delay <= 0)
		{
			delay = 0;
			teleport();
			return this;
		}
		if (delay % 20 == 0 && delay / 20 <= 5)
		{
			p.sendMessage(LucemansCore.getINSTANCE().parse("&7[&c&lTP&7] &rTeleport commencing in " + delay / 20 + " Seconds."));
		}
		delay -= 1;
		return null;
	}
	
	public void teleport()
	{
		p.teleport(to);
	}
}
