package band.kessokuteatime.knowledges.mixin.client.clothconfig;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.ConfigBuilderImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ConfigBuilderImpl.class)
public interface ConfigBuilderImplAccessor {
    @Accessor(remap = false)
    Map<String, ConfigCategory> getCategoryMap();
}
