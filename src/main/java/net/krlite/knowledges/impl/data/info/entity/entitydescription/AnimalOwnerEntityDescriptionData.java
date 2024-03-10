package net.krlite.knowledges.impl.data.info.entity.entitydescription;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.impl.data.info.entity.AbstractEntityDescriptionData;
import net.krlite.knowledges.impl.tag.entity.AnimalOwnerTag;
import net.minecraft.entity.Ownable;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class AnimalOwnerEntityDescriptionData extends AbstractEntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(EntityRepresentable representable) {
        if (representable.entity() instanceof Ownable ownable && ownable.getOwner() != null) {
            final Function<String, MutableText> localization = name -> localize("owner", name);

            return AnimalOwnerTag.OWNER.get(representable.data())
                    .map(localization)
                    .or(() -> {
                        UUID ownerUuid = ownable.getOwner().getUuid();
                        return KnowledgesClient.CACHE_USERNAME.get(ownerUuid)
                                .map(localization)
                                .or(() -> Optional.of(localize("owner.none")));
                    });
        }

        return Optional.empty();
    }

    @Override
    public @NotNull String partialPath() {
        return "animal_owner";
    }
}
