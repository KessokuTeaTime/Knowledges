package band.kessokuteatime.knowledges.config.modmenu.impl;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.ConfigBuilderImpl;
import band.kessokuteatime.knowledges.mixin.client.clothconfig.ConfigBuilderImplAccessor;
import net.minecraft.text.Text;

import java.util.Map;

public class KnowledgesConfigBuilder extends ConfigBuilderImpl {
    private Map<String, ConfigCategory> getCategoryMap() {
        return ((ConfigBuilderImplAccessor) this).getCategoryMap();
    }

    public ConfigCategory getOrCreateCategorySeparator(Text categoryKey) {
        if (getCategoryMap().containsKey(categoryKey.getString())) {
            ConfigCategory category = getCategoryMap().get(categoryKey.getString());
            if (category instanceof ConfigCategorySeparator) return category;
            else throw new RuntimeException("Desired class " + ConfigCategorySeparator.class.getName() + ", but found " + category.getClass().getName() + "!");
        } else {
            return getCategoryMap().computeIfAbsent(categoryKey.getString(), (key) -> new ConfigCategorySeparator(this, categoryKey));
        }
    }
}
