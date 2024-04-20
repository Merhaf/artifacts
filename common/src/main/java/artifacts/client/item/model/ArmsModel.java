package artifacts.client.item.model;

import artifacts.client.item.ArtifactLayers;
import artifacts.client.item.RendererUtil;
import artifacts.extensions.pocketpiston.LivingEntityExtensions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class ArmsModel extends HumanoidModel<LivingEntity> {

    public ArmsModel(ModelPart part, Function<ResourceLocation, RenderType> renderType) {
        super(part, renderType);
    }

    public ArmsModel(ModelPart part) {
        this(part, RenderType::entityCutoutNoCull);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(leftArm, rightArm);
    }

    public void renderArm(HumanoidArm handSide, PoseStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        getArm(handSide).visible = true;
        getArm(handSide.getOpposite()).visible = false;
        renderToBuffer(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static ArmsModel createClawsModel(boolean smallArms) {
        return new ArmsModel(RendererUtil.bakeLayer(ArtifactLayers.claws(smallArms)));
    }

    public static ArmsModel createGloveModel(boolean smallArms) {
        return new ArmsModel(RendererUtil.bakeLayer(ArtifactLayers.glove(smallArms)));
    }

    public static ArmsModel createGoldenHookModel(boolean smallArms) {
        return new ArmsModel(RendererUtil.bakeLayer(ArtifactLayers.goldenHook(smallArms)));
    }

    public static ArmsModel createPocketPistonModel(boolean smallArms) {
        return new ArmsModel(RendererUtil.bakeLayer(ArtifactLayers.pocketPiston(smallArms))) {

            @Override
            public void setupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                HumanoidArm mainHandSide = RendererUtil.getArmSide(entity, entity.swingingArm);
                getPistonHead(mainHandSide.getOpposite()).y = 0;
                getPistonHead(mainHandSide).y = ((LivingEntityExtensions) entity).getPocketPistonLength() * 2;
            }

            private ModelPart getPistonHead(HumanoidArm arm) {
                return getArm(arm).getChild("piston_head");
            }
        };
    }

    public static ArmsModel createOnionRingModel(boolean smallArms) {
        return new ArmsModel(RendererUtil.bakeLayer(ArtifactLayers.onionRing(smallArms)));
    }

    public static MeshDefinition createEmptyArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean smallArms) {
        MeshDefinition mesh = createMesh(CubeDeformation.NONE, 0);

        mesh.getRoot().addOrReplaceChild("left_arm", leftArm, PartPose.ZERO);
        mesh.getRoot().addOrReplaceChild("right_arm", rightArm, PartPose.ZERO);

        return mesh;
    }

    public static MeshDefinition createEmptyArmsCentered(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean smallArms) {
        MeshDefinition mesh = createMesh(CubeDeformation.NONE, 0);

        mesh.getRoot().addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        mesh.getRoot().addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        float armWidth = smallArms ? 3F : 4F;
        mesh.getRoot().getChild("left_arm").addOrReplaceChild(
                "artifact",
                leftArm,
                PartPose.offset(- 1 + armWidth / 2, 10, 0)
        );
        mesh.getRoot().getChild("right_arm").addOrReplaceChild(
                "artifact",
                rightArm,
                PartPose.offset(1 - armWidth / 2, 10, 0)
        );

        return mesh;
    }

    public static MeshDefinition createArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean smallArms) {
        leftArm.texOffs(0, 0);
        rightArm.texOffs(16, 0);
        addArms(leftArm, rightArm, new CubeDeformation(0.5F), smallArms);

        return createEmptyArms(leftArm, rightArm, smallArms);
    }

    public static MeshDefinition createSleevedArms(CubeListBuilder leftArm, CubeListBuilder rightArm, boolean smallArms) {
        leftArm.texOffs(0, 16);
        rightArm.texOffs(16, 16);
        addArms(leftArm, rightArm, new CubeDeformation(0.75F), smallArms);

        return createArms(leftArm, rightArm, smallArms);
    }

    public static MeshDefinition createSleevedArms(boolean smallArms) {
        return createSleevedArms(CubeListBuilder.create(), CubeListBuilder.create(), smallArms);
    }

    private static void addArms(CubeListBuilder leftArm, CubeListBuilder rightArm, CubeDeformation deformation, boolean smallArms) {
        leftArm.addBox(-1, -2, -2, smallArms ? 3 : 4, 12, 4, deformation);
        rightArm.addBox(smallArms ? -2 : -3, -2, -2, smallArms ? 3 : 4, 12, 4, deformation);
    }

    public static MeshDefinition createClaws(boolean smallArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        int smallArmsOffset = smallArms ? 1 : 0;

        // claw 1 lower
        leftArm.texOffs(0, 0);
        leftArm.addBox(-smallArmsOffset, 10, -1.5F, 3, 5, 1);
        rightArm.texOffs(8, 0);
        rightArm.addBox(-3 + smallArmsOffset, 10, -1.5F, 3, 5, 1);

        // claw 2 lower
        leftArm.texOffs(0, 6);
        leftArm.addBox(-smallArmsOffset, 10, 0.5F, 3, 5, 1);
        rightArm.texOffs(8, 6);
        rightArm.addBox(-3 + smallArmsOffset, 10, 0.5F, 3, 5, 1);

        // claw 1 upper
        leftArm.texOffs(16, 0);
        leftArm.addBox(3 - smallArmsOffset, 10, -1.5F, 1, 4, 1);
        rightArm.texOffs(20, 0);
        rightArm.addBox(-4 + smallArmsOffset, 10, -1.5F, 1, 4, 1);

        // claw 2 upper
        leftArm.texOffs(16, 6);
        leftArm.addBox(3 - smallArmsOffset, 10, 0.5F, 1, 4, 1);
        rightArm.texOffs(20, 6);
        rightArm.addBox(-4 + smallArmsOffset, 10, 0.5F, 1, 4, 1);

        return createEmptyArms(leftArm, rightArm, smallArms);
    }

    public static MeshDefinition createGoldenHook(boolean smallArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        // hook
        leftArm.texOffs(32, 0);
        leftArm.addBox(smallArms ? -2 : -1.5F, 12, -0.5F, 5, 5, 1);
        rightArm.texOffs(48, 0);
        rightArm.addBox(smallArms ? -3 : -3.5F, 12, -0.5F, 5, 5, 1);

        // hook base
        leftArm.texOffs(32, 6);
        leftArm.addBox(smallArms ? 0 : 0.5F, 10, -0.5F, 1, 2, 1);
        rightArm.texOffs(48, 6);
        rightArm.addBox(smallArms ? -1 : -1.5F, 10, -0.5F, 1, 2, 1);

        return createSleevedArms(leftArm, rightArm, smallArms);
    }

    public static MeshDefinition createPocketPiston(boolean smallArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();
        CubeListBuilder leftPistonHead = CubeListBuilder.create();
        CubeListBuilder rightPistonHead = CubeListBuilder.create();

        float d = 0.5F / 4 + 0.01F;

        // piston base
        CubeDeformation baseDeformation = new CubeDeformation(d * 4, d * 3, d * 4);
        leftArm.texOffs(0, 0)
                .addBox(-1, 7, -2, smallArms ? 3 : 4, 3, 4, baseDeformation);
        rightArm.texOffs(16, 0)
                .addBox(smallArms ? -2 : -3, 7, -2, smallArms ? 3 : 4, 3, 4, baseDeformation);

        // piston rod
        CubeDeformation rodDeformation = new CubeDeformation(smallArms ? d * 1.333F : d * 2, 0, d * 2);
        leftPistonHead.texOffs(0, 12)
                .addBox(0, 8 + d * 3, -1, smallArms ? 1 : 2, 2, 2, rodDeformation);
        rightPistonHead.texOffs(16, 12)
                .addBox(smallArms ? -1 : -2, 8 + d * 3, -1, smallArms ? 1 : 2, 2, 2, rodDeformation);

        // piston head
        CubeDeformation headDeformation = new CubeDeformation(d * 4, d, d * 4);
        leftPistonHead.texOffs(0, 7)
                .addBox(-1, 10 + d * 3 + d, -2, smallArms ? 3 : 4, 1, 4, headDeformation);
        rightPistonHead.texOffs(16, 7)
                .addBox(smallArms ? -2 : -3, 10 + d * 3 + d, -2, smallArms ? 3 : 4, 1, 4, headDeformation);

        MeshDefinition mesh = createEmptyArms(leftArm, rightArm, smallArms);
        mesh.getRoot()
                .getChild("left_arm")
                .addOrReplaceChild("piston_head", leftPistonHead, PartPose.ZERO);
        mesh.getRoot()
                .getChild("right_arm")
                .addOrReplaceChild("piston_head", rightPistonHead, PartPose.ZERO);

        return mesh;
    }
    public static MeshDefinition createOnionRing(boolean smallArms) {
        CubeListBuilder leftArm = CubeListBuilder.create();
        CubeListBuilder rightArm = CubeListBuilder.create();

        float armWidth = smallArms ? 3 : 4;
        float armDepth = 4;
        float h = -4;

        leftArm.texOffs(0, 0);
        leftArm.addBox(-1 - armWidth / 2, h, -1 - armDepth / 2, armWidth + 2, 2, 1);
        rightArm.texOffs(16, 0);
        rightArm.addBox(-1 - armWidth / 2, h, -1 - armDepth / 2, armWidth + 2, 2, 1);

        leftArm.texOffs(0, 3);
        leftArm.addBox(-1 - armWidth / 2, h, armDepth / 2, armWidth + 2, 2, 1);
        rightArm.texOffs(16, 3);
        rightArm.addBox(-1 - armWidth / 2, h, armDepth / 2, armWidth + 2, 2, 1);

        leftArm.texOffs(0, 6);
        leftArm.addBox(armWidth / 2, h, - armDepth / 2, 1, 2, armDepth);
        rightArm.texOffs(16, 6);
        rightArm.addBox(armWidth / 2, h, - armDepth / 2, 1, 2, armDepth);

        leftArm.texOffs(0, 12);
        leftArm.addBox(-1 - armWidth / 2, h, - armDepth / 2, 1, 2, armDepth);
        rightArm.texOffs(16, 12);
        rightArm.addBox(-1 - armWidth / 2, h, - armDepth / 2, 1, 2, armDepth);

        return createEmptyArmsCentered(leftArm, rightArm, smallArms);
    }

}
