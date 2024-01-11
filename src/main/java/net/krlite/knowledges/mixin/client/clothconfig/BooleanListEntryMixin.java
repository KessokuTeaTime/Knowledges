package net.krlite.knowledges.mixin.client.clothconfig;

import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.krlite.knowledges.config.modmenu.KnowledgesConfigScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Mixin(BooleanListEntry.class)
public abstract class BooleanListEntryMixin {
    @Shadow(remap = false) public abstract Boolean getValue();

    @Unique
    private void updateOthers() {
        Arrays.stream(KnowledgesConfigScreen.BooleanListEntrySyncHelper.values())
                .filter(helper -> helper.object(this).isPresent())
                .map(helper -> helper.entries(helper.object(this).get()))
                .flatMap(List::stream)
                .map(entry -> (BooleanListEntryAccessor) entry)
                .forEach(entry -> entry.getBool().set(getValue()));
    }

    @Inject(method = "lambda$new$0", at = @At("TAIL"))
    private void buttonCallback(ButtonWidget widget, CallbackInfo ci) {
        updateOthers();
    }

    @Inject(method = "lambda$new$1", at = @At("TAIL"))
    private void resetButtonCallback(Supplier<Boolean> defaultValue, ButtonWidget widget, CallbackInfo ci) {
        updateOthers();
    }
}

@Mixin(BooleanListEntry.class)
interface BooleanListEntryAccessor {
    @Accessor(remap = false)
    AtomicBoolean getBool();
}
