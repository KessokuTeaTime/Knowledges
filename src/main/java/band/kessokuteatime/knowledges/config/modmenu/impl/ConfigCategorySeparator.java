package band.kessokuteatime.knowledges.config.modmenu.impl;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ConfigCategorySeparator implements ConfigCategory {
    private final ConfigBuilder builder;
    private final Text categoryKey;
    private @Nullable Supplier<Optional<StringVisitable[]>> description = Optional::empty;

    public ConfigCategorySeparator(ConfigBuilder builder, Text categoryKey) {
        this.builder = builder;
        this.categoryKey = categoryKey;
    }

    @Override
    public List<Object> getEntries() {
        // Not supported
        return new ArrayList<>();
    }

    @Override
    public ConfigCategory addEntry(AbstractConfigListEntry abstractConfigListEntry) {
        // Not supported
        return null;
    }

    @Override
    public ConfigCategory setCategoryBackground(Identifier identifier) {
        // Not supported
        return null;
    }

    @Override
    public void setBackground(@Nullable Identifier identifier) {
        // Not supported
    }

    @Override
    public @Nullable Identifier getBackground() {
        // Not supported
        return null;
    }

    @Override
    public @Nullable Supplier<Optional<StringVisitable[]>> getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(@Nullable Supplier<Optional<StringVisitable[]>> description) {
        this.description = description;
    }

    @Override
    public void removeCategory() {
        this.builder.removeCategory(this.categoryKey);
    }

    @Override
    public Text getCategoryKey() {
        return this.categoryKey;
    }
}
