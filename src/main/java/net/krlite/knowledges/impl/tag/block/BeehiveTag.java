package net.krlite.knowledges.impl.tag.block;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.api.tag.caster.NbtBooleanCaster;
import net.krlite.knowledges.api.tag.caster.NbtByteCaster;
import net.krlite.knowledges.api.tag.caster.NbtCaster;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BeehiveTag implements AdditionalBlockTag {
    public static final NbtByteCaster BEES = new NbtByteCaster("Bees");
    public static final NbtBooleanCaster FULL = new NbtBooleanCaster("Full");

    @Override
    public boolean isApplicableTo(Block block) {
        return block instanceof BeehiveBlock;
    }

    @Override
    public void append(NbtCompound data, BlockRepresentable representable) {
        representable.blockEntity().ifPresent(blockEntity -> {
            if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                BEES.put(data, (byte) beehiveBlockEntity.getBeeCount());
                FULL.put(data, beehiveBlockEntity.isFullOfBees());
            }
        });
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.BEEHIVE).getPath();
    }
}
