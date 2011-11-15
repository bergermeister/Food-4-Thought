package us.food4thought.pantryprotect;

import java.util.ArrayList;

public class Recipe extends Meal {
	private String name;
	private ArrayList <Item> ingredients = new ArrayList<Item>();
	
	Recipe(String n, ArrayList <Item> i){
		name = n;
		ingredients = i;
	}
	
	public boolean addItem(Item item){
		return ingredients.add(item);
	}
	
	public boolean removeitem(Item item){
		return ingredients.remove(item);
	}
	
	public ArrayList <Item> getIngredients(){
		return ingredients;
	}
	
	public String getName(){
		return name;
	}
}
