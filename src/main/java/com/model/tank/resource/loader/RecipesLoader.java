package com.model.tank.resource.loader;

import com.model.tank.ModularTank;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.data.Recipe;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RecipesLoader {
    public static void loadRecipesFromDir(Path namespaceDir){
        Path tankDir = namespaceDir.resolve("recipes");
        if(!tankDir.toFile().isDirectory())return;
        try{
            Files.newDirectoryStream(tankDir).forEach(path -> {
                try(InputStream inputStream = Files.newInputStream(path)) {
                    String namespace = namespaceDir.getFileName().toString();
                    String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    Recipe recipe = DataLoader.GSON.fromJson(json, Recipe.class);
                    DataLoader.putRecipe(new ResourceLocation(namespace, path.getFileName().toString().replace(".json", "")), recipe);
                } catch (Exception e) {
                    ModularTank.LOGGER.error("Load {} failed,because", path, e);
                }
            });
        } catch (Exception e) {
            ModularTank.LOGGER.error("Load {} failed,because", tankDir,e);
        }
    }
}
