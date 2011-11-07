package us.food4thought.pantryprotect;

public class Item {
	private static String name;
	private static String exp;
	private static String summary;
	private static String location;
	private static String category;
	
	public Item(String n, String e, String s, String l, String c){
		name = n;
		exp = e;
		summary = s;
		location = l;
		category = c;
	}
	
	public String getName(){
		return name;
	}
	
	public String getExp(){
		return exp;
	}
	
	public String getSummary(){
		return summary;
	}
	
	public String getLocation(){
		return location;
	}
	
	public String category(){
		return category;
	}
}
