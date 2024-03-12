package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.Util;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.krlite.knowledges.impl.tag.block.BeehiveTag;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BeehiveBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        return representable.blockEntity().flatMap(blockEntity -> {
            if (blockEntity instanceof BeehiveBlockEntity) {
                NbtCompound data = representable.data();

                int honeyLevel = representable.blockState().get(BeehiveBlock.HONEY_LEVEL), fullHoneyLevel = BeehiveBlock.FULL_HONEY_LEVEL;
                MutableText honeyLevelText = Text.translatable(
                        localizationKey("honey_level"),
                        honeyLevel, fullHoneyLevel
                );

                final AtomicReference<MutableText> beeCountText = new AtomicReference<>(Text.empty());
                BeehiveTag.BEES.get(data).ifPresent(beeCount -> {
                    beeCountText.set(beeCount == 0 ? localize("bee_count", "empty") : Text.translatable(
                            localizationKey("bee_count"),
                            beeCount
                    ));
                });
                BeehiveTag.FULL.get(data).ifPresent(full -> {
                    if (full) beeCountText.set(localize("bee_count", "full"));
                });

                return Util.Text.combineToMultiline(honeyLevelText, beeCountText.get());
            }

            return Optional.empty();
        });
    }

    @Override
    public @NotNull String partialPath() {
        return KnowledgeProxy.getId(Blocks.BEEHIVE).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
