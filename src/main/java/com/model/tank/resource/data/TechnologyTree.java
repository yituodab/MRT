package com.model.tank.resource.data;

import com.model.tank.resource.Countries;
import com.model.tank.resource.DataLoader;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class TechnologyTree {
    private final Countries country;
    private final HashMap<ResourceLocation, TechnologyRecipe> recipes = new HashMap<>();

    private final HashMap<ResourceLocation, TechnologyRecipe> recipe_cache = new HashMap<>();

    // 这个是用来渲染的，一列为一个List
    private final List<List<TechnologyRecipe>> recipes_to_render = new ArrayList<>();
    public List<List<TechnologyRecipe>> getRecipes_to_render() {
        return recipes_to_render;
    }

    public TechnologyTree(Countries country){
        this.country = country;
    }
    public static void init(){
        for(Countries country : Countries.values()){
            TechnologyTree tree = new TechnologyTree(country);
            tree.instance_init();
            DataLoader.putTechnologyTree(country,tree);
        }
    }
    public void instance_init(){
        for(Map.Entry<ResourceLocation, Recipe> recipe : DataLoader.getAllRecipes()){
            Countries country = DataLoader.getTankIndex(recipe.getValue().getOutput()).getCountry();
            if(country == this.country){
                recipes.put(recipe.getKey(),new TechnologyRecipe(recipe.getKey(),recipe.getValue()));
            }
        }
        // 遍历所有配方
        for(Map.Entry<ResourceLocation, TechnologyRecipe> recipe : this.recipes.entrySet()){
            ResourceLocation[] next_recipes = recipe.getValue().getNext();
            if(next_recipes == null)continue;
            // 遍历该配方的所有下一配方
            for(ResourceLocation next : next_recipes){
                TechnologyRecipe next_recipe = this.recipes.get(next);
                if(next_recipe == null)continue;
                next_recipe.last_recipe.add(recipe.getKey());
            }
        }
        recipes_render_init();
    }
    private void recipes_render_init(){
        recipe_cache.putAll(recipes);
        int current_level = 0;
        while (!this.recipe_cache.isEmpty()){
            Iterator<Map.Entry<ResourceLocation, TechnologyRecipe>> iterator = recipe_cache.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<ResourceLocation, TechnologyRecipe> recipe = iterator.next();
                // 如果该配方没有上个配方，那就将其丢到第一列
                if(recipe.getValue().last_recipe.isEmpty()){
                    if(recipes_to_render.isEmpty())recipes_to_render.add(new ArrayList<>());
                    recipes_to_render.get(0).add(recipe.getValue());
                    iterator.remove();
                    continue;
                }
                if(current_level > 0){
                    boolean found = false;
                    for(TechnologyRecipe last : recipes_to_render.get(current_level-1)){
                        for (ResourceLocation next : last.getNext()) {
                            if(next.equals(recipe.getKey())){
                                if(recipes_to_render.size() <= (current_level))recipes_to_render.add(new ArrayList<>());
                                recipes_to_render.get(current_level).add(recipe.getValue());
                                iterator.remove();
                                found = true;
                                break;
                            }
                        }
                        if(found)break;
                    }
                }
            }
            if(current_level == 20)return;
            current_level++;
        }

    }
    public static class TechnologyRecipe{
        private final Recipe recipe;
        private final ResourceLocation id;
        private final List<ResourceLocation> last_recipe = new ArrayList<>();
        public TechnologyRecipe(ResourceLocation id,Recipe recipe){
            this.id = id;
            this.recipe = recipe;
        }

        public List<ResourceLocation> getLast() {
            return last_recipe;
        }
        public Recipe.Material[] getMaterials() {
            return recipe.getMaterials();
        }

        public ResourceLocation getOutput() {
            return recipe.getOutput();
        }

        public ResourceLocation[] getNext() {
            return recipe.getNext();
        }

        public ResourceLocation getId() {
            return id;
        }

        public Recipe getRecipe() {
            return recipe;
        }
    }
}
