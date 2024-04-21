package band.kessokuteatime.knowledges.api.proxy;

import net.krlite.equator.math.algebra.Theory;
import band.kessokuteatime.knowledges.KnowledgesCommon;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class LocalizationProxy {
    public static @NotNull MutableText localizeInstrument(Instrument instrument) {
        return KnowledgesCommon.localize("instrument", instrument.asString());
    }

    public static MutableText dateAndTime() {
        if (MinecraftClient.getInstance().world == null) return Text.empty();

        long time = MinecraftClient.getInstance().world.getTimeOfDay() + 24000 / 4; // Offset to morning
        long day = time / 24000;
        double percentage = (time % 24000) / 24000.0;

        int hour = (int) (24 * percentage), minute = (int) (60 * ((percentage * 24) % 1));

        return Text.translatable(
                KnowledgesCommon.localizationKey("date_and_time", "formatted"),
                String.valueOf(day), String.format("%02d", hour), String.format("%02d", minute)
        );
    }

    public enum TimeUnit {
        MILLISECONDS("ms"),
        SECONDS("s"),
        MINUTES("m"),
        HOURS("h");

        private final String path;

        TimeUnit(String path) {
            this.path = path;
        }

        public MutableText localize(double duration) {
            return Text.translatable(
                    Theory.looseEquals(duration, 1)
                            ? KnowledgesCommon.localizationKey("time_unit", path)
                            : KnowledgesCommon.localizationKey("time_unit", path, "plural"),
                    String.format(duration % 1.0 == 0 ? "%.0f" : "%.2f", duration)
            );
        }

        public static MutableText fitAndLocalize(double milliseconds) {
            if (Math.abs(milliseconds) < 1000) {
                return MILLISECONDS.localize(milliseconds);
            }

            double seconds = milliseconds / 1000;
            if (Math.abs(seconds) < 60) {
                return SECONDS.localize(seconds);
            }

            double minutes = seconds / 60;
            if (Math.abs(minutes) < 60) {
                return MINUTES.localize(minutes);
            }

            double hours = minutes / 60;
            return HOURS.localize(hours);
        }
    }
}
