package net.krlite.knowledges.config.disabled;

import net.krlite.knowledges.Knowledges;
import net.krlite.knowledges.api.Data;
import net.minecraft.util.Identifier;

public class DisabledDataConfig extends AbstractDisabledConfig<Data<?>> {
    public DisabledDataConfig() {
        super("disabled_data");
    }

    public boolean get(Data<?> data) {
        return Knowledges.DATA.identifier(data)
                .map(Identifier::toString)
                .filter(super::get)
                .isPresent();
    }

    public void set(Data<?> data, boolean flag) {
        Knowledges.DATA.identifier(data)
                .map(Identifier::toString)
                .ifPresent(key -> super.set(key, flag));
    }
}
