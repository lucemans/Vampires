package nl.lucemans.vampire;

import java.util.ArrayList;

public class Vampire {

	public String name;
	public String cause;
	public String causeStr;
	public boolean causeIntend = false;
	public boolean total = false;
	public Integer heat = 0;
	public Integer blood = 20;
	public Integer bloodTick = 100;
	public boolean nightvision = false;
	public boolean jumpboost = true;
	public boolean intend = false;
	public Integer truce = -1;
	public Integer level = 0;
	public Integer xp = 0;
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
