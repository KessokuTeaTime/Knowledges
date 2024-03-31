package net.krlite.knowledges.impl.contract.block;

import net.krlite.knowledges.api.proxy.ModProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.contract.BlockContract;
import net.krlite.knowledges.api.contract.caster.NbtBooleanCaster;
import net.krlite.knowledges.api.contract.caster.NbtByteCaster;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BeehiveContract implements BlockContract {
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
        return ModProxy.getId(Blocks.BEEHIVE).getPath();
    }
}
