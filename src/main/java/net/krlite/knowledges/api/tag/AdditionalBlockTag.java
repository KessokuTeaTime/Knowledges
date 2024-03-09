package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.minecraft.block.Block;

public interface AdditionalBlockTag extends AdditionalTag<BlockRepresentable, Block> {
    @Override
    default Class<BlockRepresentable> targetRepresentableClass() {
        return BlockRepresentable.class;
    }
}
