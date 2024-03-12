package net.krlite.knowledges.config.modmenu.impl;

import me.shedaniel.clothconfig2.gui.entries.TextListEntry;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class InvisibleSeparatorListEntry extends TextListEntry {
    public static class Builder extends FieldBuilder<Text, InvisibleSeparatorListEntry, Builder> {
        public Builder() {
            super(Text.empty(), Text.empty());
        }

        @Override
        public @NotNull InvisibleSeparatorListEntry build() {
            return finishBuilding(new InvisibleSeparatorListEntry(getFieldNameKey()));
        }
    }

    public InvisibleSeparatorListEntry(Text fieldName) {
        super(fieldName, Text.literal(" "));
    }

    @Override
    public void render(DrawContext graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        // Does nothing
    }
}
