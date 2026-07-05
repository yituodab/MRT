package com.model.tank.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.tank.ModularTank;
import com.model.tank.api.resource.serializer.MaterialDeserializer;
import com.model.tank.api.resource.serializer.UUIDSerializer;
import com.model.tank.api.resource.serializer.Vec3Serializer;
import com.model.tank.resource.client.AssetsLoader;
import com.model.tank.resource.client.data.tank.TankDisplay;
import com.model.tank.resource.data.Recipe;
import com.model.tank.resource.data.TechnologyTree;
import com.model.tank.resource.data.index.TankIndex;
import com.model.tank.resource.data.tank.CannonballData;
import com.model.tank.resource.data.tank.TankData;
import com.model.tank.resource.data.tank.TankIndexData;
import com.model.tank.resource.loader.CannonballDataLoader;
import com.model.tank.resource.loader.RecipesLoader;
import com.model.tank.resource.loader.TankDataLoader;
import com.model.tank.resource.loader.TankIndexLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import software.bernie.geckolib.loading.json.raw.Model;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.model.tank.ModularTank.MODID;

@Mod.EventBusSubscriber
public class DataLoader {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
            .registerTypeAdapter(Recipe.Material.class, new MaterialDeserializer())
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(Vec3.class, new Vec3Serializer())
            .create();
    private static boolean firstLoad = true;
    public static final Path MRTDataDirPath = FMLPaths.GAMEDIR.get().resolve("mrt");
    private static final HashMap<ResourceLocation, TankData> TANKS = new HashMap<>();
    private static final HashMap<ResourceLocation, TankIndex> TANK_INDEX = new HashMap<>();
    private static final HashMap<ResourceLocation, CannonballData> CANNONBALLS = new HashMap<>();
    private static final HashMap<ResourceLocation, Model> MODELS = new HashMap<>();
    private static final HashMap<ResourceLocation, TankDisplay> TANK_DISPLAY = new HashMap<>();
    private static final HashMap<ResourceLocation, Recipe> RECIPES = new HashMap<>();
    private static final HashMap<Countries, TechnologyTree> TECHNOLOGY_TREES = new HashMap<>();

    public static final ResourceLocation DEFAULT_TANK_ID = new ResourceLocation(MODID, "default");

    public static void loadData(){
        if(firstLoad){
            copyModDirectory(ModularTank.class,
                    "/assets/mrt/default_pack",
                    MRTDataDirPath,
                    "default_pack");
            firstLoad = false;
        }
        try {
            Files.newDirectoryStream(MRTDataDirPath).forEach(path -> {
                if(Files.isDirectory(path)){
                    loadDirData(path);
                }
                if(path.getFileName().endsWith(".zip")){
                }
            });
        } catch (IOException e) {
            ModularTank.LOGGER.error("load json failed",e);
        }
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void loadAssets(){
        try {
            Files.newDirectoryStream(MRTDataDirPath).forEach(path -> {
                if(Files.isDirectory(path)){
                    AssetsLoader.loadAssetsFromDir(path.resolve("assets"));
                }
            });
        } catch (IOException e) {
            ModularTank.LOGGER.error("load json failed",e);
        }
    }

    public static void loadDirData(Path dir){
        Path dataPath = dir.resolve("data");
        if(!dataPath.toFile().isDirectory()){
            return;
        }
        try {
            Files.newDirectoryStream(dataPath).forEach(namespace -> {
                CannonballDataLoader.loadCannonballDataFromDir(namespace);
                TankDataLoader.loadTankDataFromDir(namespace);

                TankIndexLoader.loadTankIndexFromDir(namespace);
                RecipesLoader.loadRecipesFromDir(namespace);
                TechnologyTree.init();
            });
        } catch (IOException e) {
            ModularTank.LOGGER.error("Load {} failed",dataPath,e);
        }
    }

    @SubscribeEvent
    public static void atReload(AddReloadListenerEvent event){
        loadAssets();
        loadData();
    }

    public static TankDisplay getTankDisplay(ResourceLocation id) {
        TankDisplay display = TANK_DISPLAY.get(id);
        if(display == null){
            return TANK_DISPLAY.get(DEFAULT_TANK_ID);
        }
        return display;
    }
    public static TankData getTankData(ResourceLocation id){
        TankData data = TANKS.get(id);
        if(data == null){
            return TANKS.get(DEFAULT_TANK_ID);
        }
        return data;
    }
    public static Set<Map.Entry<ResourceLocation, TankData>> getAllTanks() {
        return TANKS.entrySet();
    }
    public static TankIndex getTankIndex(ResourceLocation id) {
        TankIndex index = TANK_INDEX.get(id);
        if(index == null){
            return TANK_INDEX.get(DEFAULT_TANK_ID);
        }
        return index;
    }public static Set<Map.Entry<ResourceLocation, TankIndex>> getAllTankIndexes() {
        return TANK_INDEX.entrySet();
    }
    public static Model getModel(ResourceLocation id) {
        Model model = MODELS.get(id);
        if(model == null){
            return MODELS.get(DEFAULT_TANK_ID);
        }
        return model;
    }

    public static Recipe getRecipe(ResourceLocation id){
        Recipe recipe = RECIPES.get(id);
        if(recipe == null){
            return RECIPES.get(DEFAULT_TANK_ID);
        }
        return recipe;
    }

    public static Set<Map.Entry<ResourceLocation, Recipe>> getAllRecipes() {
        return RECIPES.entrySet();
    }

    public static CannonballData getCannonballData(ResourceLocation id){
        return CANNONBALLS.get(id);
    }

    public static TechnologyTree getTechnologyTree(Countries country) {
        return TECHNOLOGY_TREES.get(country);
    }

    public static void putTankDisplay(ResourceLocation id, TankDisplay display){
        TANK_DISPLAY.put(id, display);
    }
    public static void putTankData(ResourceLocation id, TankData tank){
        TANKS.put(id, tank);
    }
    public static void putTankIndex(ResourceLocation id,TankIndexData index){
        TANK_INDEX.put(id, new TankIndex(index));
    }
    public static void putModel(ResourceLocation id, Model model){
        MODELS.put(id, model);
    }
    public static void putCannonballData(ResourceLocation id,CannonballData data){
        CANNONBALLS.put(id, data);
    }
    public static void putRecipe(ResourceLocation id,Recipe data){
        RECIPES.put(id, data);
    }
    public static void putTechnologyTree(Countries country, TechnologyTree tree){
        TECHNOLOGY_TREES.put(country,tree);
    }

    /**
     * 复制本模组的文件夹到指定文件夹。将强行覆盖原文件夹。
     *
     * @param srcPath jar 中的源文件地址
     * @param root    想要复制到的根目录
     * @param path    复制后的路径
     */
    public static void copyModDirectory(Class<?> resourceClass, String srcPath, Path root, String path) {
        URL url = resourceClass.getResource(srcPath);
        try {
            if (url != null) {
                copyFolder(url.toURI(), root.resolve(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private static void copyFolder(URI sourceURI, Path targetPath) throws IOException {
        if (Files.isDirectory(targetPath)) {
            // 删掉原文件夹，达到强行覆盖的效果
            deleteFiles(targetPath);
        }
        // 使用 Files.walk() 遍历文件夹中的所有内容
        try (Stream<Path> stream = Files.walk(Paths.get(sourceURI), Integer.MAX_VALUE)) {
            stream.forEach(source -> {
                // 生成目标路径
                Path target = targetPath.resolve(sourceURI.relativize(source.toUri()).toString());
                try {
                    // 复制文件或文件夹
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    // 处理异常，例如权限问题等
                    e.printStackTrace();
                }
            });
        }
    }
    private static void deleteFiles(Path targetPath) throws IOException {
        Files.walkFileTree(targetPath, new SimpleFileVisitor<>() {
            // 先去遍历删除文件
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            // 再去遍历删除目录
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
