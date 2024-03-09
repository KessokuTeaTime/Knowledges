package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.Shortcuts;
import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.proxy.KnowledgeProxy;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BeehiveBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        representable.blockEntity().flatMap(blockEntity -> {
            if (blockEntity instanceof BeehiveBlockEntity) {
                NbtCompound data = representable.data();
                System.out.println(data);

                int honeyLevel = representable.blockState().get(BeehiveBlock.HONEY_LEVEL), fullHoneyLevel = BeehiveBlock.FULL_HONEY_LEVEL;
                MutableText honeyLevelText = Text.translatable(
                        localizationKey("honey_level"),
                        honeyLevel, fullHoneyLevel
                );

                MutableText beeCountText = Text.empty();
                if (data.contains("Bees")) {
                    byte beeCount = data.getByte("Bees");
                    beeCountText = beeCount == 0 ? localize("bee_count", "empty") : Text.translatable(
                            localizationKey("bee_count"),
                            beeCount
                    );
                }

                if (data.contains("Full")) {
                    boolean isFull = data.getBoolean("Full");
                    if (isFull) beeCountText = localize("bee_count", "full");
                }

                return Shortcuts.Text.combineToMultiline(honeyLevelText, beeCountText);
            }

            return Optional.empty();
        });

        return Optional.empty();
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
