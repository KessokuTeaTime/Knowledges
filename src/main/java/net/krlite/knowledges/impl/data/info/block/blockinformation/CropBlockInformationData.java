package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CropBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        if (representable.block() instanceof CropBlock) {
            int age = representable.blockState().get(CropBlock.AGE), maxAge = CropBlock.MAX_AGE;

            return Optional.of(age == maxAge ? localize("mature") : Text.translatable(
                    localizationKey("age"),
                    representable.blockState().get(CropBlock.AGE), CropBlock.MAX_AGE
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return "crop";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
