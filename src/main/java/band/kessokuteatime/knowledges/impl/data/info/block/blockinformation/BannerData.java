package band.kessokuteatime.knowledges.impl.data.info.block.blockinformation;

import band.kessokuteatime.knowledges.api.representable.BlockRepresentable;
import band.kessokuteatime.knowledges.impl.data.info.base.block.BlockInformationData;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BannerData extends BlockInformationData {
    @Override
    public Optional<MutableText> blockInformation(BlockRepresentable representable) {
        return representable.blockEntity().flatMap(blockEntity -> {
            if (representable.blockState().isIn(BlockTags.BANNERS)) {
                if (blockEntity instanceof BannerBlockEntity bannerBlockEntity) {
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
                                                localizationKey("pattern", "more"),
                                                name.getString(),
                                                available - 1,
                                                // Counts the rest of the patterns. Use '%2$d' to reference.
                                                available
                                                // Counts all the patterns. Use '%3$d' to reference.
                                        );
                                    } else if (available > 1) {
                                        return Text.translatable(
                                                localizationKey("pattern", "one_more"),
                                                name.getString()
                                        );
                                    } else {
                                        return name;
                                    }
                                });
                    }
                }
            }

            return Optional.empty();
        });
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
