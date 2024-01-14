package net.krlite.knowledges.data.info.block.blockinformation;

import net.krlite.knowledges.api.Knowledge;
import net.krlite.knowledges.core.util.Helper;
import net.krlite.knowledges.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BeehiveBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockState blockState, PlayerEntity player) {
        Optional<BlockEntity> blockEntity = Knowledge.Info.crosshairBlockEntity();

        if (blockEntity.isPresent() && blockEntity.get() instanceof BeehiveBlockEntity beehiveBlockEntity) {
            int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL), fullHoneyLevel = BeehiveBlock.FULL_HONEY_LEVEL;
            MutableText honeyLevelText = Text.translatable(
                    localizationKey("honey_level"),
                    honeyLevel, fullHoneyLevel
            );

            /*
            // TODO: Make this stuff work
            int beeCount = beehiveBlockEntity.getBeeCount();
            MutableText beeCountText = beeCount == 0 ? localize("bee_count", "empty") : Text.translatable(
                    localizationKey("bee_count"),
                    beeCount
            );

            return Helper.Text.combineToMultiline(honeyLevelText, beeCountText);

             */
            return Optional.of(honeyLevelText);
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.BLOCK.getId(Blocks.BEEHIVE).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
