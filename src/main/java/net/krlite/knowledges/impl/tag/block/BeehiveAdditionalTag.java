package net.krlite.knowledges.impl.tag.block;

import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.api.tag.TagProtocol;
import net.krlite.knowledges.api.tag.caster.NbtBooleanCaster;
import net.krlite.knowledges.api.tag.caster.NbtByteCaster;
import net.krlite.knowledges.api.tag.caster.NbtCaster;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public class BeehiveAdditionalTag implements AdditionalBlockTag {
    public enum Protocol implements TagProtocol<BeehiveAdditionalTag, Protocol> {
        BEES_BYTE(new NbtByteCaster("Bees")),
        FULL_BOOLEAN(new NbtBooleanCaster("Full"));

        private final NbtCaster<?> caster;

        Protocol(NbtCaster<?> caster) {
            this.caster = caster;
        }

        @Override
        public NbtCaster<?> caster() {
            return caster;
        }
    }

    @Override
    public boolean shouldApply(Block block) {
        System.out.println(block);
        return block instanceof BeehiveBlock;
    }

    @Override
    public void append(NbtCompound data, BlockRepresentable representable) {
        representable.blockEntity().ifPresent(blockEntity -> {
            if (blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
                ((NbtByteCaster) Protocol.BEES_BYTE.caster()).put(data, (byte) beehiveBlockEntity.getBeeCount());
                ((NbtBooleanCaster) Protocol.FULL_BOOLEAN.caster()).put(data, beehiveBlockEntity.isFullOfBees());
            }
        });
    }

    @Override
    public @NotNull String path() {
        return KnowledgeProxy.getId(Blocks.BEEHIVE).getPath();
    }
}
