package nl.lucemans.vampire;

import java.util.ArrayList;

import nl.lucemans.Core.race.Race;
import nl.lucemans.Core.type.LucemansSave;

public class RaceVampire extends Race{
	
	public RaceVampire()
	{
		raceName = "Vampire";
		tags.add("undead");
	}
	
	@LucemansSave.String
	public String username;
	@LucemansSave.String
	public String cause;
	@LucemansSave.String
	public String causeStr;
	@LucemansSave.Boolean
	public boolean causeIntend = false;
	@LucemansSave.Boolean
	public boolean total = false;
	@LucemansSave.Integer
	public Integer heat = 0;
	@LucemansSave.Boolean
	public boolean nightvision = false;
	@LucemansSave.Boolean
	public boolean jumpboost = true;
	@LucemansSave.Boolean
	public boolean intend = false;
	@LucemansSave.Integer
	public Integer truce = -1;
	@LucemansSave.Integer
	public Integer level = 0;
	@LucemansSave.Integer
	public Integer xp = 0;
	@LucemansSave.ArrayList
	public ArrayList<String> infectees = new ArrayList<String>();
	
	public String getInfectionString()
	{
		if (cause.equalsIgnoreCase("bite"))
			return "Bitten by a vampire ("+causeStr+")";
		if (cause.equalsIgnoreCase("pot"))
			return "Drinking something malicious ("+causeStr+")";
		if (cause.equalsIgnoreCase("combat"))
			if (causeIntend)
				return "Purposly infected in combat ("+causeStr+")";
			else
				return "Accidentaly infected in combat ("+causeStr+")";
		return "Randomly mutated into a vampire";
	}
	
}
