package net.krlite.knowledges.impl.data.info.entity.entitydescription;

import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.representable.EntityRepresentable;
import net.krlite.knowledges.impl.data.info.entity.AbstractEntityDescriptionData;
import net.krlite.knowledges.impl.tag.entity.AnimalOwnerTag;
import net.minecraft.entity.Tameable;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class AnimalOwnerEntityDescriptionData extends AbstractEntityDescriptionData {
    @Override
    public Optional<MutableText> entityDescription(EntityRepresentable representable) {
        if (representable.entity() instanceof Tameable tameable && tameable.getOwnerUuid() != null) {
            final Function<String, MutableText> localization = name -> localize("owner", name);
            System.out.println(tameable.getOwnerUuid());

            return AnimalOwnerTag.OWNER.get(representable.data())
                    .map(localization)
                    .or(() -> {
                        UUID ownerUuid = tameable.getOwnerUuid();
                        return KnowledgesCommon.CACHE_USERNAME.get(ownerUuid)
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
