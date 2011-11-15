package us.food4thought.pantryprotect;

import java.util.ArrayList;

public class Meal {
	private String name;
	private ArrayList <Recipe> recipes = new ArrayList <Recipe> ();
	
	public boolean addRecipe(Recipe recipe){
		return recipes.add(recipe);
	}
	
	public boolean removeRecipe(Recipe recipe){
		return recipes.remove(recipe);
	}
	
	public ArrayList <Recipe> getRecipes(){
		return recipes;
	}
	
}
