package net.krlite.knowledges.api.tag;

import net.krlite.knowledges.api.core.path.WithPartialPath;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

public interface AdditionalBlockTag extends AdditionalTag<BlockRepresentable, Block>, WithPartialPath {
    @Override
    default Class<BlockRepresentable> targetRepresentableClass() {
        return BlockRepresentable.class;
    }

    @Override
    default @NotNull String currentPath() {
        return "block";
    };
}
