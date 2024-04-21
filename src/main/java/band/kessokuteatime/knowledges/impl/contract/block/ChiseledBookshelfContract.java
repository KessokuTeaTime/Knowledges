package band.kessokuteatime.knowledges.impl.contract.block;

import band.kessokuteatime.knowledges.api.proxy.ModProxy;
import band.kessokuteatime.knowledges.api.representable.BlockRepresentable;
import band.kessokuteatime.knowledges.api.contract.BlockContract;
import band.kessokuteatime.knowledges.api.contract.caster.NbtCompoundCaster;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class ChiseledBookshelfContract implements BlockContract {
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
        return ModProxy.getId(Blocks.CHISELED_BOOKSHELF).getPath();
    }
}
