package net.krlite.knowledges.impl.tag.block;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BeehiveAdditionalTag implements AdditionalBlockTag {
    @Override
    public boolean shouldApply(Block block) {
        System.out.println(block);
        return block instanceof BeehiveBlock;
    }

    @Override
    public void append(NbtCompound data, BlockRepresentable representable) {
        System.out.println(1);
        representable.blockEntity().ifPresent(blockEntity -> {
            if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                data.putByte("Bees", (byte) beehiveBlockEntity.getBeeCount());
                data.putBoolean("Full", beehiveBlockEntity.isFullOfBees());
            }
        });
    }

    @Override
    public @NotNull String path() {
        return KnowledgeProxy.getId(Blocks.BEEHIVE).getPath();
    }
}
