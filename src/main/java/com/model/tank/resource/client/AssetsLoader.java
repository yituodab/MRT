package com.model.tank.resource.client;

import com.model.tank.ModularTank;
import com.model.tank.resource.DataLoader;
import com.model.tank.resource.client.data.tank.TankDisplay;
import com.model.tank.utils.FileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@OnlyIn(value = Dist.CLIENT)
public class AssetsLoader {
    public static void loadAssetsFromDir(Path root){
        if(Files.isDirectory(root)){
            try{
                Files.newDirectoryStream(root).forEach(path -> {
                    try {
                        String modid = path.getFileName().toString();
                        Files.newDirectoryStream(path.resolve("lang")).forEach(
                                LanguageLoader::load
                        );
                        Files.newDirectoryStream(path.resolve("models")).forEach(model ->
                                loadModelFromFile(modid, model)
                        );
                        Files.newDirectoryStream(path.resolve("textures")).forEach(texture ->
                                loadTextureFromFile(modid, texture)
                        );
                        Files.newDirectoryStream(path.resolve("display")).forEach(display ->
                                loadDisplayFromFile(modid, display)
                        );
                    } catch (IOException e) {
                        ModularTank.LOGGER.error("error",e);
                    }
                });
                ModularTank.LOGGER.info("Loaded assets file successful");
            } catch (IOException e) {
                ModularTank.LOGGER.error("error",e);
            }
        }
    }
    public static void loadModelFromFile(String modid, Path path){
        try(InputStream inputStream = Files.newInputStream(path)){
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Model model = JsonUtil.GEO_GSON.fromJson(json, Model.class);
            DataLoader.putModel(new ResourceLocation(modid, path.getFileName().toString()), model);
        } catch (Exception e) {
            ModularTank.LOGGER.error("load model fail,because",e);
        }
    }
    public static void loadTextureFromFile(String modid, Path path){
        try{
            Minecraft mc = Minecraft.getInstance();
            FileTexture texture = new FileTexture(new ResourceLocation(modid, path.getFileName().toString()), path);
            mc.textureManager.register(texture.getID(),texture);
        } catch (Exception e) {
            ModularTank.LOGGER.error("load texture fail,because",e);
        }
    }
    public static void loadDisplayFromFile(String modid, Path path){
        try(InputStream inputStream = Files.newInputStream(path)){
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            TankDisplay display = DataLoader.GSON.fromJson(json, TankDisplay.class);
            DataLoader.putTankDisplay(new ResourceLocation(modid, path.getFileName().toString().replace(".json","")), display);
        } catch (Exception e) {
            ModularTank.LOGGER.error("Load display fail,because",e);
        }
    }
}
