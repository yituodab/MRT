package com.model.tank.api.client.render;

import com.model.tank.api.client.interfaces.ILocalPlayer;
import com.model.tank.api.entity.ModularEntity;
import com.model.tank.resource.DataLoader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ModularEntityRender extends GeoEntityRenderer<ModularEntity> {

    public static final String ROOT = "root";
    public ModularEntityRender(EntityRendererProvider.Context renderManager, GeoModel model) {
        super(renderManager, model);
    }

    @Override
    public void render(@NotNull ModularEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getVehicle() != null && player.getVehicle().equals(entity) && ((ILocalPlayer)player).isCheckingModules())
            modulesRender(entity,entityYaw,partialTick,poseStack,bufferSource,packedLight);
    }

    @Override
    public void defaultRender(PoseStack poseStack, ModularEntity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        poseStack.pushPose();
        Color renderColor = this.getRenderColor(animatable, partialTick, packedLight);
        float red = renderColor.getRedFloat();
        float green = renderColor.getGreenFloat();
        float blue = renderColor.getBlueFloat();
        float alpha = renderColor.getAlphaFloat();
        int packedOverlay = this.getPackedOverlay(animatable, 0.0F, partialTick);
        ResourceLocation resource = this.getGeoModel().getModelResource(animatable);
        Model gotModel = DataLoader.getModel(resource);
        BakedGeoModel model = gotModel == null ? this.getGeoModel().getBakedModel(resource) : BakedModelFactory.getForNamespace(resource.getNamespace()).constructGeoModel(GeometryTree.fromModel(gotModel));
        if (renderType == null) {
            renderType = this.getRenderType(animatable, this.getTextureLocation(animatable), bufferSource, partialTick);
        }
        if (buffer == null) {
            buffer = bufferSource.getBuffer(renderType);
        }
        this.preRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        if (this.firePreRenderEvent(poseStack, model, bufferSource, partialTick, packedLight)) {
            this.preApplyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, (float)packedLight, packedLight, packedOverlay);
            this.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.applyRenderLayers(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);
            this.postRender(poseStack, animatable, model, bufferSource, buffer, false, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
            this.firePostRenderEvent(poseStack, model, bufferSource, partialTick, packedLight);
        }

        poseStack.popPose();
        this.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        this.doPostRenderCleanup();
    }
    public void modulesRender(@NotNull ModularEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        //
//        poseStack.pushPose();
//
//        // 抬高 0.5 格，放大 3 倍保证显眼
//        poseStack.translate(0, 0.5, 0);
//        poseStack.scale(3.0f, 3.0f, 3.0f);
//
//        // 强制开启所有渲染开关
//        RenderSystem.disableCull();      // 禁用背面剔除
//        RenderSystem.disableDepthTest(); // 无视深度遮挡
//
//        // ★★★ 关键修改：使用 RenderType.text() ★★★
//        // 它使用 DefaultVertexFormat.POSITION_COLOR，不需要 UV/光照/法线！
//        VertexConsumer consumer = bufferSource.getBuffer(RenderType.text(new ResourceLocation("minecraft", "textures/block/white_concrete.png")));
//        Matrix4f matrixs = poseStack.last().pose();
//
//        // 1x1x1 立方体顶点
//        float[] c = {
//                -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f,
//                -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f
//        };
//        int[][] facess = {
//                {0,1,2,0,2,3}, {4,5,6,4,6,7}, {0,1,5,0,5,4},
//                {2,3,7,2,7,6}, {0,3,7,0,7,4}, {1,2,6,1,6,5}
//        };
//
//        // 只需要 vertex + color，其他全都不要！
//        for (int[] f : facess) {
//            for (int idx : f) {
//                consumer.vertex(matrixs, c[idx*3], c[idx*3+1], c[idx*3+2])
//                        .color(1.0f, 0.0f, 0.0f, 1.0f) // 亮红不透明
//                        .endVertex(); // ★ 注意：这里只有 vertex 和 color，结束！
//            }
//        }
//
//        // 恢复状态
//        RenderSystem.enableDepthTest();
//        RenderSystem.enableCull();
//
//        poseStack.popPose();
//        //
//        for(Module module : entity.getAllModules()){
//            // by DS
//            double[] position = module.getPositionToArray();
//            double x = position[0];
//            double y = position[1];
//            double z = position[2];
//            double[] size = module.getSizeToArray();
//            double width = size[0];
//            double height = size[1];
//            double depth = size[2];
//
//            // 获取一个用于渲染半透明物体的 VertexConsumer
//            // 这里使用 RenderType.translucent()，这是实现“无视遮挡”效果的关键之一
//            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.cutout());
//
//            // 开始构建模型矩阵
//            poseStack.pushPose();
//
//            // 将模块平移到正确的位置 (相对于载具)
//            poseStack.translate(x, y, z);
//
//            // 获取当前的模型矩阵
//            Matrix4f matrix = poseStack.last().pose();
//
//            // 设置颜色为蓝色 (RGBA: 红0, 绿0, 蓝1, 透明度0.6 使其半透明)
//            float r = 1.0f;
//            float g = 0.0f;
//            float b = 0.0f;
//            float a = 1.0f;
//
//            // 绘制一个立方体 (12个三角形，每个三角形3个顶点，共36个顶点)
//            // 注意：这里使用的是最简单的顶点定义方式，没有进行顶点复用
//            // 为了更清晰地展示，这里以绘制一个1x1x1的立方体为例
//            double halfW = width / 2.0f;
//            double halfH = height / 2.0f;
//            double halfD = depth / 2.0f;
//
//            // 定义立方体的8个顶点 (局部坐标)
//            double[] corners = {
//                    -halfW, -halfH, -halfD, // 0: 左下后
//                    halfW, -halfH, -halfD, // 1: 右下后
//                    halfW,  halfH, -halfD, // 2: 右上后
//                    -halfW,  halfH, -halfD, // 3: 左上后
//                    -halfW, -halfH,  halfD, // 4: 左下前
//                    halfW, -halfH,  halfD, // 5: 右下前
//                    halfW,  halfH,  halfD, // 6: 右上前
//                    -halfW,  halfH,  halfD  // 7: 左上前
//            };
//
//            // 定义6个面的顶点索引 (每个面2个三角形)
//            int[][] faces = {
//                    {0, 1, 2, 0, 2, 3}, // 后面 (Z负)
//                    {4, 5, 6, 4, 6, 7}, // 前面 (Z正)
//                    {0, 1, 5, 0, 5, 4}, // 底面 (Y负)
//                    {2, 3, 7, 2, 7, 6}, // 顶面 (Y正)
//                    {0, 3, 7, 0, 7, 4}, // 左面 (X负)
//                    {1, 2, 6, 1, 6, 5}  // 右面 (X正)
//            };
//            RenderSystem.disableDepthTest();
//
////            RenderSystem.enableBlend();
////            RenderSystem.defaultBlendFunc();
//            // 遍历所有面
//            for (int[] face : faces) {
//                for (int index : face) {
//                    float vx = (float) corners[index * 3];
//                    float vy = (float) corners[index * 3 + 1];
//                    float vz = (float) corners[index * 3 + 2];
//                    // 添加顶点: 位置 (x,y,z), 颜色 (r,g,b,a), 纹理坐标 (0,0), 光照 (packedLight)
//                    // 注意：这里的光照参数传0可以让它不受光照影响，保持颜色纯粹
//                    vertexConsumer.vertex(matrix, vx, vy, vz)
//                            .color(r, g, b, a)
//                            .uv(0, 0)
//                            .uv2(0)
//                            .normal(0,1,0)// 使用0可以避免光照影响
//                            .endVertex();
//                }
//            }
//            //RenderSystem.disableBlend();
//            RenderSystem.enableDepthTest();
//
//            // 恢复模型矩阵
//            poseStack.popPose();
//            //
//        }
    }
}
