package band.kessokuteatime.knowledges.impl.data.info.entity.entityinformation;

import band.kessokuteatime.knowledges.api.representable.EntityRepresentable;
import band.kessokuteatime.knowledges.impl.data.info.base.entity.EntityInformationData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PaintingData extends EntityInformationData {
    @Override
    public Optional<MutableText> entityInformation(EntityRepresentable representable) {
        if (representable.entity().getType() == EntityType.PAINTING) {
            return ((PaintingEntity) representable.entity()).getVariant().getKey().map(key -> Text.translatable(
                    key.getValue().toTranslationKey("painting", "title")
            ));
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return Registries.ITEM.getId(Items.PAINTING).getPath();
    }

    @Override
    public boolean providesTooltip() {
        return true;
    }
}
