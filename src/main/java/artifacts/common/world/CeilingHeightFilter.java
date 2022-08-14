package artifacts.common.world;

import artifacts.common.init.ModFeatures;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Random;

public class CeilingHeightFilter extends PlacementFilter {

    public static final Codec<CeilingHeightFilter> CODEC = ExtraCodecs.NON_NEGATIVE_INT
            .fieldOf("max_height")
            .xmap(CeilingHeightFilter::new, f -> f.maxHeight)
            .codec();

    private final int maxHeight;

    private CeilingHeightFilter(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public static CeilingHeightFilter create(int maxHeight) {
        return new CeilingHeightFilter(maxHeight);
    }

    protected boolean shouldPlace(PlacementContext context, Random random, BlockPos pos) {
        if (maxHeight == 0) {
            return true;
        }
        for (int i = 1; i <= maxHeight; i ++) {
            if (!context.getBlockState(pos.above(i)).isAir()) {
                return true;
            }
        }

        return false;
    }

    public PlacementModifierType<?> type() {
        return ModFeatures.CEILING_HEIGHT_FILTER;
    }
}