package net.krlite.knowledges.impl.data.info.block.blockinformation;

import net.krlite.knowledges.api.component.Knowledge;
import net.krlite.knowledges.api.representable.BlockRepresentable;
import net.krlite.knowledges.impl.data.info.block.AbstractBlockInformationData;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BannerBlockInformationData extends AbstractBlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        if (representable.blockState().isIn(BlockTags.BANNERS)) {
            if (!(representable.blockEntity() instanceof BannerBlockEntity bannerBlockEntity)) return Optional.empty();

            var patterns = bannerBlockEntity.getPatterns();
            int available = patterns.size() - 1;
            // The first pattern is always the background color, so ignore it

            if (available > 0) {
                return patterns.get(1).getFirst().getKey()
                        .map(RegistryKey::getValue)
                        .map(Identifier::toShortTranslationKey)
                        .map(translationKey -> {
                            MutableText name = Text.translatable(
                                    localizationKey("pattern"),
                                    Text.translatable("block.minecraft.banner." + translationKey + "." + patterns.get(1).getSecond().getName()).getString()
                            );

                            if (available > 2) {
                                return Text.translatable(
                                        localizationKey("more_patterns"),
                                        name.getString(),
                                        available - 1,
                                        // Counts the rest of the patterns. Use '%2$d' to reference.
                                        available
                                        // Counts all the patterns. Use '%3$d' to reference.
                                );
                            } else if (available > 1) {
                                return Text.translatable(
                                        localizationKey("one_more_pattern"),
                                        name.getString()
                                );
                            } else {
                                return name;
                            }
                        });
            }
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return "banner";
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
