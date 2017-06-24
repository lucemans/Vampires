package nl.lucemans.vampire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import nl.lucemans.Core.LucemansCore;
import nl.lucemans.Core.UserData;
import nl.lucemans.Core.item.Item;
import nl.lucemans.Core.settings.LangParse;
import nl.lucemans.Core.type.LucemansEvent;
import nl.lucemans.Core.type.LucemansListener;

/*
 * Made by Lucemans (MrDisk)!
 */
public class Main extends JavaPlugin implements Listener, LucemansListener{

	public ArrayList<Vampire> vampires = new ArrayList<Vampire>();
	public ArrayList<Vampire> backupVamps = new ArrayList<Vampire>();
	
	public String prefix = "&7&l[&b&lFantasy&8&lVampires&7&l]&7";
	public LangParse globalLang = new LangParse();
	
	public HashMap<Material, Double> terrainProt = new HashMap<Material, Double>();
	public HashMap<String, Boolean> time = new HashMap<String, Boolean>();
	
	public boolean cBurn = false;
	public Integer maxBloodTick = 20;
	
	public LucemansCore core = null;
	
	public int moonId = -1;
	
	@Override
	public void onEnable() {
		
		if (!getCore())
		{
			getLogger().severe("No instance of LucemansCore detected.");
			getLogger().severe("Disabling");
			setEnabled(false);
			return;
		}
		core.registerListener(this);
		core.registerRace(new RaceVampire());
		
		core.addOrSetLang("vampires.prefix", "&7[&rNova&bVampires&7]");
		core.addOrSetLang("vampires.nopermission", "%prefix% You dont have permissions to do this.");
		core.addOrSetLang("vampires.startup", "Hello World!!! I am a vampire!!!");
		core.addOrSetLang("vampires.infection.bite", "%prefix% You feel a weird vibe going through your body.");
		core.addOrSetLang("vampires.cmd.moon.start", "Setting it to full moon");
		core.addOrSetLang("vampires.cmd.moon.interrupt", "Please wait while the previous one is done.");
		core.addOrSetLang("vampires.cmd.moon.notplayer", "You need to be a Player to do this.");
		core.addOrSetLang("vampires.cmd.infect.noeffect", "%prefix% It seems to have had no effect.");
		core.addOrSetLang("vampires.cmd.infect.notyourself", "%prefix% Don't even try to infect yourself.");
		core.addOrSetLang("vampires.cmd.infect.success", "%prefix% Successfully infected %target%");
		core.addOrSetLang("vampires.cmd.infect.distance", "%prefix% You have to be close to your target to do this.");
		core.addOrSetLang("vampires.cmd.infect.targetnotfound", "%prefix% We could not find %target%");
		core.addOrSetLang("vampires.cmd.infect.limit", "%prefix% You dont feel like you have the power to infect someone right now.");
		core.addOrSetLang("vampires.cmd.infect.notfullcmd", "%prefix% Please specify who you want to infect.");
		core.addOrSetLang("vampires.cmd.infect.notvampire", "%prefix% I dont think you can do that.");
		core.addOrSetLang("vampires.cmd.nv.enable", "%prefix% Nightvision has now been &a&lEnabled&7.");
		core.addOrSetLang("vampires.cmd.nv.disable", "%prefix% Nightvision has now been &c&lDisabled&7.");
		core.addOrSetLang("vampires.cmd.nv.notvampire", "%prefix% I dont think you can do that.");
		core.addOrSetLang("vampires.cmd.intend.enable", "%prefix% Intend has now been &a&lEnabled&7.");
		core.addOrSetLang("vampires.cmd.intend.disable", "%prefix% Intend has now been &c&lDisabled&7.");
		core.addOrSetLang("vampires.cmd.intend.notvampire", "%prefix% I dont think you can do that.");
		core.addOrSetLang("vampires.cmd.jump.enable", "%prefix% Jump Boost has now been &a&lEnabled&7.");
		core.addOrSetLang("vampires.cmd.jump.disable", "%prefix% Jump Boost has now been &c&lDisabled&7.");
		core.addOrSetLang("vampires.cmd.jump.notvampire", "%prefix% I dont think you can do that.");
		core.addOrSetLang("vampires.cmd.setrace.human", "%prefix% You are not a vampire anymore..");
		core.addOrSetLang("vampires.cmd.setrace.unknown", "%prefix% That is not a valid race.");
		core.addOrSetLang("vampires.cmd.setrace.targetnotfound", "%prefix% We could not find %target%");
		core.addOrSetLang("vampires.cmd.setrace.notfullcmd", "%prefix% Please use /v set <player> <vampire/human>");
		core.addOrSetLang("vampires.cmd.blood.notvampire", "%prefix% You are not a vampire");
		core.addOrSetLang("vampires.cmd.blood.notfullcmd", "%prefix% please use /v blood <amount>");
		core.addOrSetLang("vampires.transform.stone", "%prefix% You have turned to stone.");
		
		prefix = core.getLang("vampires.prefix");
		
		globalLang.placeholders.put("%prefix%", prefix);
		globalLang.placeholders.put("%header%", "&7------&b&lFantasy&8&lVampires&7-----");
		globalLang.placeholders.put("%footer%", "&7------&l---------------&7-----");
		
		getLogger().info(parse(core.getLang("vampires.startup")));
		
		setDefaults();
		loadConfig();
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		terrainProt.put(Material.GLASS, 0.5);
		terrainProt.put(Material.STAINED_GLASS, 1.0);
		terrainProt.put(Material.FENCE, 0.5);
		terrainProt.put(Material.FENCE_GATE, 0.5);
		terrainProt.put(Material.SIGN_POST, 1.0);
		terrainProt.put(Material.WALL_SIGN, 1.0);
		terrainProt.put(Material.ICE, 0.5);
		terrainProt.put(Material.NETHER_FENCE, 1.0);
		terrainProt.put(Material.VINE, 0.5);
		terrainProt.put(Material.LEAVES, 0.5);
		terrainProt.put(Material.LEAVES_2, 0.5);
		terrainProt.put(Material.GRASS, 0.25);
		terrainProt.put(Material.LONG_GRASS, 0.5);
		terrainProt.put(Material.YELLOW_FLOWER, 0.0);
		
		registerItems();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onVampTick();
			}
		}, 10, 10);
	}
	
	@Override
	public void onDisable() {
		dumpConfig();
	}
	
	public void registerItems()
	{
		if (!LucemansCore.main.itemman.exists("pot-cureall"))
		{
			getLogger().info("Claimed the pot-cureall Item");
			Item i = new Item();
			i.name = "&7Potion of &rCure-All";
			i.amount = 1;
			i.type = Material.POTION;
			ArrayList<String> iPotCureallLore = new ArrayList<String>();
			iPotCureallLore.add(parse("&7Some say that this potion cures everything"));
			iPotCureallLore.add(parse("&7Others say its completely useless..."));
			i.lore = iPotCureallLore;
			
			ShapelessRecipe potionCureAll = new ShapelessRecipe(i.getItem());
			potionCureAll.addIngredient(Material.NETHER_STALK);
			potionCureAll.addIngredient(Material.GHAST_TEAR);
			potionCureAll.addIngredient(Material.POTION);
			potionCureAll.addIngredient(Material.SUGAR);
			potionCureAll.addIngredient(Material.SPIDER_EYE);
			this.getServer().addRecipe(potionCureAll);
			
			LucemansCore.main.itemman.registerItem("pot-cureall", i);
		}
		
		if (!LucemansCore.main.itemman.exists("pot-holywater"))
		{
			getLogger().info("Claimed the pot-holywater Item");
			Item i = new Item();
			i.name = "&7Holy Water";
			i.amount = 1;
			i.type = Material.SPLASH_POTION;
			ArrayList<String> iPotHolyWaterLore = new ArrayList<String>();
			iPotHolyWaterLore.add(parse("&7Water with the hell burned out of it."));
			iPotHolyWaterLore.add(parse("&7It comes from a church... It must atleast do something."));
			i.lore = iPotHolyWaterLore;
			
			ShapelessRecipe potionHolyWater = new ShapelessRecipe(i.getItem());
			potionHolyWater.addIngredient(Material.POTION);
			potionHolyWater.addIngredient(Material.BLAZE_POWDER);
			potionHolyWater.addIngredient(Material.NETHER_STALK);
			this.getServer().addRecipe(potionHolyWater);
			
			LucemansCore.main.itemman.registerItem("pot-holywater", i);
		}
	}
	
	public boolean getCore()
	{
		try{
        RegisteredServiceProvider<LucemansCore> rsp = getServer().getServicesManager().getRegistration(LucemansCore.class);
        core = rsp.getProvider();
		}catch(Exception e)
		{
			
		}
		return core != null;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("v") || label.equalsIgnoreCase("vamp") || label.equalsIgnoreCase("vampire"))
		{
			if (args.length > 0)
			{
				// args
				String mod = args[0];
				if (mod.equalsIgnoreCase("s") || mod.equalsIgnoreCase("show"))
				{
					sender.sendMessage(parse("Based on " + (core.getUser(sender.getName()).race instanceof RaceVampire ? (getVamp(sender.getName()) != null ? "You are a vampire" : "You are not infected") : "You are not from the vampiric race")));
					Vampire vamp = getVamp(sender.getName());
					sender.sendMessage(parse("%header%"));
					if (vamp != null)
					{

						Player p = (Player) sender;
						if (vamp.total)
						{
							sender.sendMessage(parse("&7You are a Vampire [Level "+vamp.level+", Exp "+vamp.xp+"]"));
							sender.sendMessage(parse("&7" + vamp.getInfectionString()));
							//sender.sendMessage(parse("&7Level: " + vamp.level + " Exp: " + vamp.xp));
							if (p.getWorld().getBiome(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ()).equals(Biome.HELL) || p.getWorld().getBiome(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ()).equals(Biome.SKY))
							{
								sender.sendMessage(parse("&7Current moon phase: " + "Unknown..."));
							}
							else
							{
								sender.sendMessage(parse("&7Current moon phase: " + core.getMoonPhase(p.getWorld())));
							}
							sender.sendMessage(parse("&7Temperature: " + vamp.heat + "%"));
							Integer heat = getHeat(p);
							sender.sendMessage(parse("&7Radiation = &eSun &a* Terrain * &9Armor &7- 20"));
							sender.sendMessage(parse("&7" + hotSpace(heat.toString(), "Radiation") + heat + " = &e" + getSun(p) + "&a * " + getTerrain(p) + "&9 * " + getArmor(p) + " &7-20"));
							sender.sendMessage(parse("Blood: " + bloodScale(10, vamp)));
							sender.sendMessage(parse("&7-----&7&l----Abilities----&7-----"));
							sender.sendMessage(parse((vamp.nightvision ? "&a" : "&c") + "NV &7| " +
							(vamp.jumpboost ? "&a" : "&c") + "Jump &7| " +
							(vamp.intend ? "&a" : "&c") + "Intend"));
						}
						else
						{
							sender.sendMessage(parse("&7You are infected with the Dark Dissease."));
							sender.sendMessage(parse("&7" + vamp.getInfectionString()));
							if (p.getWorld().getBiome(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ()).equals(Biome.HELL) || p.getWorld().getBiome(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ()).equals(Biome.SKY))
							{
								sender.sendMessage(parse("&7Current moon phase: " + "Unknown..."));
								sender.sendMessage(parse("&7Days left: Unknown"));
							}
							else
							{
								sender.sendMessage(parse("&7Current moon phase: " + core.getMoonPhase(p.getWorld())));
								sender.sendMessage(parse("&7Days left: "+ core.getDaysTillFullMoon(p.getWorld())+" Days"));
							}
						}
					}
					else
					{
						sender.sendMessage(parse("&7You are neither a vampire nor infected with the dark dissease."));
					}
					//sender.sendMessage(parse("%footer%"));
					return true;
				}
				if (mod.equalsIgnoreCase("moon"))
				{
					if (sender instanceof Player)
					{
						if (sender.hasPermission("vampires.moon"))
						{
							if (moonId == -1)
							{
								Player p = (Player) sender;
								sender.sendMessage(parse(core.getLang("vampires.cmd.moon.start")));
								setFullMoon(p, p.getWorld());
							}
							else
							{
								sender.sendMessage(parse("vampires.cmd.moon.interrupt"));
							}
						}
					}
					sender.sendMessage(parse(core.getLang("vampires.cmd.moon.notplayer")));
					return true;
				}
				if (mod.equalsIgnoreCase("infect"))
				{
					Vampire _vamp = getVamp(sender.getName());
					if (_vamp != null || sender.getName().equalsIgnoreCase("CONSOLE"))
					{
						if (args.length > 1)
						{
							if (_vamp.infectees.size() < 3)
							{
								Player p = Bukkit.getPlayer(args[1]);
								if (p != null)
								{
									double dist = 0.0;
									if (sender instanceof Player)
										dist = ((Player)sender).getLocation().distance(p.getLocation());
									if (dist < 2.5)
									{
										Vampire vamp = getVamp(args[1]);
										if (vamp != null)
										{
											sender.sendMessage(parse(core.getLang("vampires.cmd.infect.noeffect")));
										}
										else
										{
											if (sender.getName().equalsIgnoreCase(args[1]))
											{
												sender.sendMessage(parse(core.getLang("vampires.cmd.infect.notyourself")));
											}
											else
											{
												infect(Bukkit.getPlayer(args[1]), sender.getName());
												sender.sendMessage(parse(core.getLang("vampires.cmd.infect.success")));
												_vamp.infectees.add(p.getName());
											}
										}
									}
									else
									{
										sender.sendMessage(parse(core.getLang("vampires.cmd.infect.distance")));
									}
								}
								else
								{
									sender.sendMessage(parse(core.getLang("vampires.cmd.infect.targetnotfound"))); // %target% args[1]
								}
							}
							else
							{
								sender.sendMessage(parse(core.getLang("vampires.cmd.infect.limit")));
							}
						}
						else
						{
							sender.sendMessage(parse(core.getLang("vampires.cmd.infect.notfullcmd")));
						}
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.cmd.infect.notvampire")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("blood"))
				{
					Vampire vamp = getVamp(sender.getName());
					if (vamp != null)
					{
						if (args.length >= 2)
						{
							try{
								vamp.blood = Integer.parseInt(args[1]);
								sender.sendMessage("Success!!");
							}
							catch(Exception e)
							{
								sender.sendMessage(parse("Something went wrong."));
							}
							return true;
						}
						sender.sendMessage(parse(core.getLang("vampires.cmd.blood.notfullcmd")));
						return true;
					}
					sender.sendMessage(parse(core.getLang("vampires.cmd.blood.notvampire")));
					return true;
				}
				if (mod.equalsIgnoreCase("nv") || mod.equalsIgnoreCase("nightvision"))
				{
					Vampire vamp = getVamp(sender.getName());
					if (vamp != null)
					{
						vamp.nightvision = !vamp.nightvision;
						if (vamp.nightvision)
							sender.sendMessage(parse(core.getLang("vampires.cmd.nv.enable")));
						else
							sender.sendMessage(parse(core.getLang("vampires.cmd.nv.disable")));
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.cmd.nv.notvampire")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("i") || mod.equalsIgnoreCase("intend"))
				{
					Vampire vamp = getVamp(sender.getName());
					if (vamp != null)
					{
						vamp.intend = !vamp.intend;
						if (vamp.intend)
							sender.sendMessage(parse(core.getLang("vampires.cmd.intend.enable")));
						else
							sender.sendMessage(parse(core.getLang("vampires.cmd.intend.disable")));
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.cmd.intend.notvampire")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("j") || mod.equalsIgnoreCase("jb") || mod.equalsIgnoreCase("jump") || mod.equalsIgnoreCase("jumpboost"))
				{
					Vampire vamp = getVamp(sender.getName());
					if (vamp != null)
					{
						vamp.jumpboost = !vamp.jumpboost;
						if (vamp.jumpboost)
							sender.sendMessage(parse(core.getLang("vampires.cmd.jump.enable")));
						else
							sender.sendMessage(parse(core.getLang("vampires.cmd.jump.disable")));
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.cmd.jump.notvampire")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("set") || mod.equalsIgnoreCase("race"))
				{
					if (sender.hasPermission("fantasyvampires.setrace"))
					{
						if (args.length > 2)
						{
							Player p = Bukkit.getPlayer(args[1]);
							if (p != null)
							{
								if (args[2].equalsIgnoreCase("vamp") || args[2].equalsIgnoreCase("v") || args[2].equalsIgnoreCase("vampire"))
								{
									Vampire vamp = getVamp(args[1]);
									if (vamp != null)
									{
										
									}
									else
									{
										infect(p, sender.getName(), true);
										//p.sendMessage(parse("%prefix% You are now a vampire.."));
									}
									return true;
								}
								if (args[2].equalsIgnoreCase("human") || args[2].equalsIgnoreCase("h"))
								{
									Vampire vamp = getVamp(args[1]);
									if (vamp != null)
									{
										vampires.remove(vamp);
										core.getUser(args[1]).resetRace();
										p.sendMessage(parse(core.getLang("vampires.cmd.setrace.human")));
									}
									else
									{
										
									}
									return true;
								}
								sender.sendMessage(parse(core.getLang("vampires.cmd.setrace.unknown")));
							}
							else
							{
								sender.sendMessage(parse(core.getLang("vampires.cmd.setrace.targetnotfound"))); //args[1]
							}
						}
						else
						{
							sender.sendMessage(parse(core.getLang("vampires.cmd.setrace.notfullcmd")));
						}
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.nopermission")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("ilist") || mod.equalsIgnoreCase("infectlist") || mod.equalsIgnoreCase("il"))
				{
					Vampire vamp = getVamp(sender.getName());
					if (vamp != null)
					{
						sender.sendMessage(parse("%header%"));
						for (String str : vamp.infectees)
						{
							sender.sendMessage(parse(" - " + str));
						}
						if (vamp.infectees.size() == 0)
						{
							sender.sendMessage(parse("It looks like you have infected noone yet."));
							sender.sendMessage(parse("Use /v infect <name> to infect someone."));
						}
						sender.sendMessage(parse("%footer%"));
					}
					else
					{
						sender.sendMessage(parse("%prefix% I dont think you can do that."));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("reload") || mod.equalsIgnoreCase("rl"))
				{
					if (sender.hasPermission("fantasyvampires.reload"))
					{
						dumpConfig();
						setDefaults();
						loadConfig();
						sender.sendMessage(parse("%prefix% Successfully reloaded."));
					}
					else
					{
						sender.sendMessage(parse(core.getLang("vampires.nopermission")));
					}
					return true;
				}
				if (mod.equalsIgnoreCase("?") || mod.equalsIgnoreCase("help"))
				{
					sender.sendMessage(parse("%header%"));
					sender.sendMessage(parse("&e/v s,show : shows you vampire status."));
					sender.sendMessage(parse("&e/v ?,help : shows this help menu."));
					sender.sendMessage(parse("&e/v infect <name> : infects a player"));
					sender.sendMessage(parse("&e/v j,jb,jump : toggle your jumpboost"));
					sender.sendMessage(parse("&e/v nv,nightvision, : toggle your nightvision"));
					sender.sendMessage(parse("&c/v i,intend : toggle your intend"));
					sender.sendMessage(parse("%footer%"));
					return true;
				}

				sender.sendMessage(parse(prefix) + "Please use /v ?");
			}
			else
			{
				sender.sendMessage(parse("&7------&b&lFantasy&8&lVampires&7-----"));
				sender.sendMessage(parse("Vampires v1.0 by MrDisk"));
				sender.sendMessage(parse("&7Please use /v ?"));
			}
			return true;
		}
		return false;
	}
	
	public String bloodScale(Integer amount, Vampire v)
	{
		String temp = "";
		for (int i = 0; i <= amount; i++)
		{
			if (((double) i/amount*100)-1 >= ((double)v.blood/20*100))
				temp += "&7-";
			else
				temp += "&c-";
		}
		temp += " " + ((double)v.blood/20*100) + "%";
		return temp;
	}
	
	public void setFullMoon(Player p, World world)
	{
		moonId = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				
				Integer d = core.getDaysTillFullMoon(world);
				if (d > 0)
				{
					world.setTime(world.getTime()+200);
				}
				if (d == 0)
				{
					if (core.getTimeString(world).equalsIgnoreCase("day"))
					{
						world.setTime(world.getTime()+200);
					}
					else
					{
						p.sendMessage(parse("Successfully set it to full moon."));
						Bukkit.getScheduler().cancelTask(moonId);
						moonId = -1;
					}
				}
				
			}
		}, 1, 1);
	}
	
	public String parse(String text)
	{
		return globalLang.parse(core, text, true);
	}
	
	public String hotSpace(String p, String a)
	{
		String str = "";
		Integer dif = a.length() - p.length();
		if (dif < 0)
			dif = dif * -1;
		for (int i = 0; i < dif; i++)
		{
			str += " ";
		}
		return str;
	}
	
	/*
	 * Infect
	 *  - SideFunc
	 */
	public void infect(Player target, String cause)
	{
		infect(target, cause, false);
	}
	
	public void infect(Player target, String cause, Boolean cmd)
	{
		RaceVampire race = new RaceVampire();
		core.getUser(target.getName()).race = race;
		Vampire vamp = new Vampire();
		vamp.name = target.getName();
		if (cmd)
			vamp.cause = "cmd";
		else
			vamp.cause = "bite";
		vamp.causeStr = cause;
		vamp.total = false;
		vampires.add(vamp);
		target.sendMessage(parse("%prefix% You feel a weird vibe going through your body."));
	}
	
	/*
	 * CalcHeat
	 *  - SideFunc
	 */
	public void calcHeat(Player p, Vampire vamp)
	{
		// sun (0/100)
		// terrain (0/1)
		// armor ?
		// -20
		
		Integer heat = getHeat(p);
		if (heat > 0)
			heat = heat/6;
		else
			heat = heat/2;
		
		Integer lastHeat = vamp.heat;
		
		if (!(p.getGameMode().equals(GameMode.CREATIVE) || p.getGameMode().equals(GameMode.SPECTATOR)))
			vamp.heat += heat;
		
		if (lastHeat < 100 && vamp.heat >= 100)
		{
			p.sendMessage(parse(core.getLang("vampires.transform.stone")));
		}
		if (lastHeat >= 100 && vamp.heat < 100)
		{
			p.sendMessage(parse(prefix+"Things start to feel soft again."));
		}//TODO: 
		
		while (vamp.heat > 100)
		{
			vamp.heat = 100;
		}
		while (vamp.heat < 0)
		{
			vamp.heat = 0;
		}
	}
	
	public Integer getHeat(Player p)
	{
		return (int) Math.round(getSun(p) * getTerrain(p) * getArmor(p) - 20);
	}
	
	public double getArmor(Player p)
	{
		boolean hasFireRes = false;
		for (PotionEffect pe : p.getActivePotionEffects())
		{
			if (pe.getType().equals(PotionEffectType.FIRE_RESISTANCE))
			{
				hasFireRes = true;
				break;
			}
		}
		
		if (hasFireRes)
			return 0.0;
		double i = 1;
		for (ItemStack item : p.getInventory().getArmorContents())
		{
			if (item != null)
			{
				i -= 0.125;
				if (item.getItemMeta() != null)
				{
					if (item.getItemMeta().getDisplayName() != null)
					{
						if (item.getItemMeta().getDisplayName().contains(parse("&8&l[&7Vampire&8&l]")))
						{
							i -= 0.125/2;
						}
					}
				}
			}
		}
		return i;
	}
	
	public Integer getSun(Player p)
	{
		if (p.getWorld().hasStorm() || p.getWorld().isThundering())
			return 0;
		long gameTime = p.getWorld().getTime(), hours = gameTime / 100 + 6*10;
		if (hours >= 240) {hours -= 240;}
		while (hours >= 240) {hours -= 240;}
		
		Integer sun = 0;
		if (hours >= 60 && hours <= 110)
		{
			sun = Math.round((hours - 60)/5*10);
		}
		if (hours > 110 && hours < 130)
		{
			sun = 100;
		}
		if (hours >= 130 && hours <= 180)
		{
			sun = Math.round((hours - 180)/5*10)*-1;
		}
		return sun;	
	}
	
	public double getTerrain(Player p)
	{
		if (p.getWorld().getEnvironment().equals(World.Environment.THE_END))
				return 0.0;
		double terrain = 1.0;
		Integer k = 255 - (p.getLocation().getBlockY()+2);
		for (Integer i = 0; i < k; i++)
		{
			if (terrain <= 0.0)
			{
				terrain = 0.0;
				break;
			}
			Block b = p.getLocation().getWorld().getBlockAt(p.getLocation().add(0,i+2,0));
			if (b != null)
			{
				if (!b.isEmpty())
				{
					if (terrainProt.containsKey(b.getType()))
					{
						terrain -= terrainProt.get(b.getType());
					}
					else
					{
						terrain -= 1.0;
					}
				}
			}
		}
		return terrain;
	}
	
	/*
	 * getVampire
	 *  - SideFunc
	 */
	public Vampire getVamp(String name)
	{
		/*for (Vampire vamp : vampires)
		{
			if (vamp.name.equalsIgnoreCase(name))
				return vamp;
		}*/
		UserData p = core.getUser(name);
			if (core.getRace(p) instanceof RaceVampire)
			{
				for (Vampire vamp : vampires)
				{
					if (vamp.name.equalsIgnoreCase(name))
						return vamp;
				}
			}
		return null;
	}
	
	/*
	 * dumpConfigs
	 *  - runFunc
	 */
	public void dumpConfig()
	{
		for (Vampire vamp : vampires)
		{
			getConfig().set("vampires."+vamp.name+".total", vamp.total);
			getConfig().set("vampires."+vamp.name+".cause", vamp.cause);
			getConfig().set("vampires."+vamp.name+".causeStr", vamp.causeStr);
			getConfig().set("vampires."+vamp.name+".causeIntend", vamp.causeIntend);
			getConfig().set("vampires."+vamp.name+".heat", vamp.heat);
			getConfig().set("vampires."+vamp.name+".blood", vamp.blood);
			getConfig().set("vampires."+vamp.name+".bloodTick", vamp.bloodTick);
			getConfig().set("vampires."+vamp.name+".intend", vamp.intend);
			getConfig().set("vampires."+vamp.name+".jump", vamp.jumpboost);
			getConfig().set("vampires."+vamp.name+".nightvision", vamp.nightvision);
			getConfig().set("vampires."+vamp.name+".level", vamp.level);
			getConfig().set("vampires."+vamp.name+".xp", vamp.xp);
			getConfig().set("vampires."+vamp.name+".infectees", vamp.infectees);
		}
		for (Vampire vamp : backupVamps)
		{
			boolean contains = false;
			for (Vampire _vamp : vampires)
			{
				if (_vamp.name.equalsIgnoreCase(vamp.name))
				{
					contains = true;
				}
			}
			if (!contains)
			{
				getConfig().set("vampires."+vamp.name, null);
			}
		}
		saveConfig();
	}
	
	public void checkStr(String loc, Object val)
	{
		if (!getConfig().contains(loc))
			getConfig().set(loc, val);
	}
	
	/*
	 * loadConfigs
	 *  - runFunc
	 */
	@SuppressWarnings("unchecked")
	public void loadConfig()
	{
		vampires.clear();
		if (getConfig().getConfigurationSection("vampires") != null)
			for (String pname : getConfig().getConfigurationSection("vampires").getKeys(false))
			{
				try{
					checkStr("vampires."+pname+".cause", "bite");
					checkStr("vampires."+pname+".causeStr", "bite");
					checkStr("vampires."+pname+".heat", 0);
					checkStr("vampires."+pname+".causeIntend", false);
					checkStr("vampires."+pname+".intend", false);
					checkStr("vampires."+pname+".jump", false);
					checkStr("vampires."+pname+".nightvision", false);
					checkStr("vampires."+pname+".total", false);
					checkStr("vampires."+pname+".level", 0);
					checkStr("vampires."+pname+".xp", 0);
					checkStr("vampires."+pname+".infectees", new ArrayList<String>());
					checkStr("vampires."+pname+".blood", 20);
					checkStr("vampires."+pname+".bloodTick", 100);
					saveConfig();
				}catch(Exception e)
				{
					getLogger().info("Failed to load data for " + pname);
				}
			} 
			for (String pname : getConfig().getConfigurationSection("vampires").getKeys(false))
			{
				try{
					Vampire vamp = new Vampire();
					vamp.name = pname;
					vamp.cause = getConfig().getString("vampires."+pname+".cause");
					vamp.causeStr = getConfig().getString("vampires."+pname+".causeStr");
					vamp.heat = getConfig().getInt("vampires."+pname+".heat");
					vamp.causeIntend = getConfig().getBoolean("vampires."+pname+".causeIntend");
					vamp.intend = getConfig().getBoolean("vampires."+pname+".intend");
					vamp.jumpboost = getConfig().getBoolean("vampires."+pname+".jump");
					vamp.nightvision = getConfig().getBoolean("vampires."+pname+".nightvision");
					vamp.total = getConfig().getBoolean("vampires."+pname+".total");
					vamp.level = getConfig().getInt("vampires."+pname+".level");
					vamp.xp = getConfig().getInt("vampires."+pname+".xp");
					vamp.infectees = (ArrayList<String>) getConfig().getList("vampires."+pname+".infectees");
					vamp.blood = getConfig().getInt("vampires."+pname+".blood");
					vampires.add(vamp);
					backupVamps.add(vamp);
				}catch(Exception e)
				{
					getLogger().info("Failed to load data for " + pname);
				}
			} 
		cBurn = !getConfig().getBoolean("stonetransform");
	}
	
	/*
	 * setDefaults
	 *  - runFunc
	 */
	public void setDefaults()
	{
		saveDefaultConfig();
		/*getConfig().addDefault("vampires", new String[]{});
		getConfig().addDefault("stonetransform", true);
		getConfig().options().copyDefaults(true);
		saveConfig();*/
	}
	
	/*
	 * onVampTick
	 *  - timedFunc
	 */
	public void onVampTick()
	{
		if (!getCore())
		{
			getLogger().severe("No instance of LucemansCore detected.");
			getLogger().severe("Disabling");
			setEnabled(false);
			return;
		}
		for (Player p : Bukkit.getOnlinePlayers())
		{
			Vampire vamp = getVamp(p.getName());
			if (vamp != null)
			{
				if (vamp.total)
				{
					calcHeat(p, vamp);
					calcHunger(p, vamp);
					if (vamp.jumpboost && vamp.heat < 99)
					{
						p.removePotionEffect(PotionEffectType.JUMP);
						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 30*20, 1));
					}
					if (vamp.nightvision)
					{
						p.removePotionEffect(PotionEffectType.NIGHT_VISION);
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 30*20, 1));
					}
					if (vamp.truce >= 0)
					{
						vamp.truce -= 1;
						if (vamp.truce == 0)
						{
							p.sendMessage(parse("%prefix% Your truce with the 	monsters has been restored"));
						}
					}
					if (vamp.heat > 50)
					{
						if (cBurn)
						{
							p.setFireTicks(20);
							Integer level = (vamp.heat - 50)/5;
							if (vamp.heat >= 99)
							{
								level = 100;
								p.removePotionEffect(PotionEffectType.WITHER);
								p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5*20, 128));
							}
							p.removePotionEffect(PotionEffectType.SLOW);
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, level));
						}
						else
						{
							Integer level = (vamp.heat - 50)/5;
							if (vamp.heat >= 99)
							{
								level = 100;
								p.removePotionEffect(PotionEffectType.JUMP);
								p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5*20, 128));
							}
							p.removePotionEffect(PotionEffectType.SLOW);
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5*20, level));
						}
					}
					if (vamp.heat > 20)
					{
						if (cBurn)
						{
							Integer level = (vamp.heat - 20)/8;
							p.removePotionEffect(PotionEffectType.CONFUSION);
							p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 5*20, level));
						}
						else
						{
							Integer level = (vamp.heat - 20)/8;
							if (vamp.heat >= 99)
							{
								level = 100;
								p.removePotionEffect(PotionEffectType.JUMP);
								p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 5*20, 128));
							}
							p.removePotionEffect(PotionEffectType.SLOW_DIGGING);
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 5*20, level));
						}
					}
					if (vamp.xp >= 100)
					{
						vamp.xp -= 100;
						vamp.level += 1;
						p.sendMessage(parse("%prefix% You have leveled up, you are now level &e"+vamp.level));
						if (vamp.xp < 100)
							p.sendMessage(parse("&7You need &e"+(100-vamp.xp)+"&7 more xp"));
					}
				}
			}
		}
		for (World w : Bukkit.getWorlds())
		{
			if (!time.containsKey(w.getName()))
				time.put(w.getName(), false);
			if (!(w.getTime() < 12300 || w.getTime() > 23850))
			{
				if (!time.get(w.getName()))
				{
					getLogger().info("NIGHT");
					time.put(w.getName(), true);
					checkForEvolve(w);
				}
			}
			else
			{
				if (time.get(w.getName()))
				{
					getLogger().info("DAY");
					time.put(w.getName(), false);
				}
			}
		}
	}
	
	/*
	 * calcHunger
	 *  - Calculate the hunger value for the player
	 */
	public void calcHunger(Player p, Vampire v)
	{
		if (v.bloodTick <= 0)
		{
			v.bloodTick = maxBloodTick;
			if (v.blood > 0)
				v.blood -= 1;
		}
		else
			v.bloodTick -= 1;
		
		getLogger().info("BLOOD FOR " + p.getName() + " set to " + v.blood);
		p.setFoodLevel(v.blood);
		p.setSaturation(0f);
	}
	
	public void checkForEvolve(World w)
	{
		getLogger().info("CHECK FOR EVOLVE");
		if (core.getPhase(w) == 4)
		{
			getLogger().info("Phase 4");
			for (Player p : w.getPlayers())
			{
				getLogger().info("PLAYER " + p.getName());
				Vampire vamp = getVamp(p.getName());
				if (vamp != null)
				{
					getLogger().info("IS VAMPIRE!!");
					if (!vamp.total)
					{
						getLogger().info("NOT TOTAL!!!");
						vamp.total = true;
						p.sendMessage(parse("%prefix% You feel no more blood flowing through your vessels."));
						p.sendMessage(parse("&8&l[&7Unknown Voice&8&l] &8"+randomize("COME OVER TO ME!!! QUUUICK PLEAAASEE!!")));
						p.sendMessage(parse("&8&l[&7Unknown Voice&8&l] &8"+randomize("THE INFECTION HAS STRUCK ME!!! IM TURNING INTO A VVVAAAAAMMMMPPPPPIIIRRRREEE")));
						p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 100));
					}
					else
					{
						vamp.xp += 30;
						p.sendMessage(parse("%prefix% You look at the moon, you gained &e30&7xp"));
					}
				}
			}
		}
	}
	
	public String randomize(String str)
	{
		String encrypted = "";
		
		for (int i = 1; i < str.length(); i++)
		{
			if ((str.charAt(i)+"").equalsIgnoreCase(" "))
			{
				encrypted += " ";
				continue;
			}
				
			Random r = new Random();
			Integer rand = r.nextInt(2);
			rand = Math.round(rand);
			if (rand == 1)
				encrypted += "-";
			else
				encrypted += str.charAt(i);
		}
		
		return encrypted;
	}
	
	@EventHandler
	public void onMobTrack(EntityTargetLivingEntityEvent event)
	{
		if (event.getTarget() instanceof Player)
		{
			Player p = (Player) event.getTarget();
			Vampire vamp = getVamp(p.getName());
			if (vamp != null)
			{
				if (vamp.total)
				{
					if (vamp.truce == -1)
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			if (!(event.getEntity() instanceof Player))
			{
				Vampire vamp = getVamp(event.getDamager().getName());
				if (vamp != null)
				{
					if (vamp.total)
					{
						if (vamp.truce <= 0)
						{
							event.getDamager().sendMessage(parse(prefix+"You have broken your truce with the monsters"));
						}
						vamp.truce = 30;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event)
	{
		if (event.getCause().equals(DamageCause.DROWNING) || event.getCause().equals(DamageCause.FALL))
		{
			if (event.getEntity() instanceof Player)
			{
				Vampire vamp = getVamp(((Player) event.getEntity()).getName());
				if (vamp != null)
				{
					if (vamp.total)
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@LucemansEvent
	public void onNight(Player p)
	{
		
	}
	
	@EventHandler
	public void onEat(PlayerItemConsumeEvent event)
	{
		if (!event.isCancelled())
		if (event.getItem() != null)
		{
			if (event.getItem().getType().equals(Material.POTION))
			{
				getLogger().info(event.getPlayer().getName() + " drunk a potion");
				if (core.compare(event.getItem(), "pot-cureall"))
				{
					Boolean isUndead = core.getRace(core.getUser(event.getPlayer().getName())).tags.contains("undead");
					if (isUndead)
					{
						UserData data = core.getUser(event.getPlayer().getName());
						data.resetRace();
						event.getPlayer().sendMessage("You skin turns red and thick, blood flows through your vessels");
						event.getPlayer().sendMessage(parse("%prefix% &rYou feel &c&lAlive&r."));
					}
					else
					{
						event.getPlayer().sendMessage("It seems to have had no effect");
					}
					event.getItem().setAmount(event.getItem().getAmount()-1);
					event.setCancelled(true);
				}
			}
			else
			{
				Vampire v = getVamp(event.getPlayer().getName());
				if (v != null)
				{
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPotion(PotionSplashEvent event)
	{
		if (!event.isCancelled())
		if (core.compare(event.getPotion().getItem(), "pot-holywater"))
		{
			for (LivingEntity e : event.getAffectedEntities())
			{
				if (e instanceof Player)
				{
					Boolean isUndead = core.getRace(core.getUser(((Player) e).getName())).tags.contains("undead");
					//Vampire vamp = getVamp(((Player) e).getName());
					if (isUndead)
					{
						((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 3*20, 4));
					}
					else
					{
						((Player) e).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10*20, 4));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event)
	{
		if (!event.isCancelled())
		if (core.compare(event.getRecipe().getResult(), "pot-holywater"))
		{
			Boolean isUndead = core.getRace(core.getUser(event.getWhoClicked().getName())).tags.contains("undead");
			if (isUndead)
			{
				event.getWhoClicked().sendMessage(parse("%prefix% You dont think you can touch that."));
				event.setCancelled(true);
			}
			else
			{
				
			}
		}
	}
}
