package artifacts.loot;

import artifacts.Artifacts;
import artifacts.registry.ModLootConditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.Supplier;

public class ConfigValueChance implements LootItemCondition {

    public static final Codec<ConfigValueChance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ChanceConfig.CODEC.fieldOf("config").forGetter(c -> c.chanceConfig))
                    .apply(instance, ConfigValueChance::new)
    );

    private final ChanceConfig chanceConfig;

    private ConfigValueChance(ChanceConfig chanceConfig) {
        this.chanceConfig = chanceConfig;
    }

    @Override
    public LootItemConditionType getType() {
        return ModLootConditions.CONFIG_VALUE_CHANCE.get();
    }

    @Override
    public boolean test(LootContext context) {
        return context.getRandom().nextDouble() < chanceConfig.value.get();
    }

    public static LootItemCondition.Builder archaeologyChance() {
        return () -> new ConfigValueChance(ChanceConfig.ARCHAEOLOGY);
    }

    public static LootItemCondition.Builder entityEquipmentChance() {
        return () -> new ConfigValueChance(ChanceConfig.ENTITY_EQUIPMENT);
    }

    public static LootItemCondition.Builder everlastingBeefChance() {
        return () -> new ConfigValueChance(ChanceConfig.EVERLASTING_BEEF);
    }

    private enum ChanceConfig implements StringRepresentable {
        ARCHAEOLOGY("archaeology", () -> Artifacts.CONFIG.common.archaeologyChance),
        ENTITY_EQUIPMENT("entity_equipment", () -> Artifacts.CONFIG.common.entityEquipmentChance),
        EVERLASTING_BEEF("everlasting_beef", () -> Artifacts.CONFIG.common.everlastingBeefChance);

        private static final Codec<ChanceConfig> CODEC = StringRepresentable.fromEnum(ChanceConfig::values);

        final String name;
        final Supplier<Double> value;

        ChanceConfig(String name, Supplier<Double> value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
