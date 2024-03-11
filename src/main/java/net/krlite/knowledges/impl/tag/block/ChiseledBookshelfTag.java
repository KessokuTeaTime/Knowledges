package net.krlite.knowledges.impl.tag.block;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.api.tag.caster.NbtCompoundCaster;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class ChiseledBookshelfTag implements AdditionalBlockTag {
    public static final NbtCompoundCaster CHISELED_BOOKSHELF = new NbtCompoundCaster("ChiseledBookshelf");

    @Override
    public boolean isApplicableTo(Block block) {
        return block instanceof ChiseledBookshelfBlock;
    }

    @Override
    public void append(NbtCompound data, BlockRepresentable representable) {
        representable.blockEntity().ifPresent(blockEntity -> {
            if (blockEntity instanceof ChiseledBookshelfBlockEntity chiseledBookshelfBlockEntity) {
                CHISELED_BOOKSHELF.put(data, chiseledBookshelfBlockEntity);
            }
        });
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.CHISELED_BOOKSHELF).getPath();
    }
}
