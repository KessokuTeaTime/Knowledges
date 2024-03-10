package net.krlite.knowledges.manager;

import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import net.krlite.knowledges.api.tag.AdditionalBlockTag;
import net.krlite.knowledges.api.tag.AdditionalEntityTag;
import net.krlite.knowledges.api.tag.AdditionalTag;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import java.util.List;

public class KnowledgesTagManager extends KnowledgesManager<AdditionalTag<?, ?>> {
    public KnowledgesTagManager() {
        super(() -> KnowledgesCommon.CONFIG.get().tags.enabled);
    }

    @Override
    protected String localizationPrefix() {
        return "tag";
    }

    public <T extends AdditionalTag<?, ?>> List<T> ofSpecifiedType(Class<T> tClass) {
        return asList().stream()
                .filter(t -> tClass.isAssignableFrom(t.getClass()))
                .map(t -> (T) t)
                .toList();
    }

    public List<AdditionalBlockTag> byBlock(Block block) {
        return ofSpecifiedType(AdditionalBlockTag.class).stream()
                .filter(t -> t.isApplicableTo(block))
                .toList();
    }

    public List<AdditionalEntityTag> byEntity(Entity entity) {
        return ofSpecifiedType(AdditionalEntityTag.class).stream()
                .filter(t -> t.isApplicableTo(entity))
                .toList();
    }
}
