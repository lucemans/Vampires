package nl.lucemans.Core.skin;

public class SkinChange {
	
	public String username = "";
	public String reason = "";
	public String skin = "MrDisk";
	public Integer priority = 100;
	
	public SkinChange(String username, String reason, String skin, Integer priority) {
		super();
		this.username = username;
		this.reason = reason;
		this.skin = skin;
		this.priority = priority;
	}

}
