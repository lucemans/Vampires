package nl.lucemans.Core.settings;

import java.util.HashMap;

import nl.lucemans.Core.LucemansCore;

public class LangParse {

	public HashMap<String, String> placeholders = new HashMap<String, String>();
	
	public String parse(LucemansCore c, String str, boolean color)
	{
		for (String _str : placeholders.keySet())
		{
			str = str.replaceAll(_str, placeholders.get(_str));
		}
		
		if (color)
			str = c.parse(str);
		
		return str;
	}
}
