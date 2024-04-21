package band.kessokuteatime.knowledges.config.modmenu.impl;

import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class EmptyEntryBuilder extends FieldBuilder<Object, EmptyEntry, EmptyEntryBuilder> {
    private int height;

    public EmptyEntryBuilder(int height) {
        super(Text.empty(), Text.empty());
        this.height = height;
    }

    public EmptyEntryBuilder() {
        this(12);
    }

    public EmptyEntryBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public @NotNull EmptyEntry build() {
        return new EmptyEntry(height);
    }
}
