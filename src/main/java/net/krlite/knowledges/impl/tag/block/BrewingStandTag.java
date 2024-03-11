package net.krlite.knowledges.impl.tag.block;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.api.tag.caster.NbtCompoundCaster;
import net.krlite.knowledges.api.tag.caster.NbtIntCaster;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BrewingStandTag implements AdditionalBlockTag {
    public static final NbtCompoundCaster BREWING_STAND = new NbtCompoundCaster("BrewingStand");

    public static class BrewingStand {
        public static final NbtIntCaster FUEL = new NbtIntCaster("Fuel");
        public static final NbtIntCaster TIME = new NbtIntCaster("Time");
    }

    @Override
    public boolean isApplicableTo(Block block) {
        return block instanceof BrewingStandBlock;
    }

    @Override
    public void append(NbtCompound data, BlockRepresentable representable) {
        representable.blockEntity().ifPresent(blockEntity -> {
            if (blockEntity instanceof BrewingStandBlockEntity brewingStandBlockEntity) {
                NbtCompound brewingStand = new NbtCompound();
                BrewingStand.FUEL.put(brewingStand, brewingStandBlockEntity.fuel);
                BrewingStand.TIME.put(brewingStand, brewingStandBlockEntity.brewTime);

                BREWING_STAND.put(data, brewingStand);
            }
        });
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.BREWING_STAND).getPath();
    }
}
