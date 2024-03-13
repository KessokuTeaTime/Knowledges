package net.krlite.knowledges.api.proxy;

import net.krlite.equator.math.algebra.Theory;
import net.krlite.knowledges.KnowledgesClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class LocalizationProxy {
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
                            ? KnowledgesClient.localizationKey("time_unit", path)
                            : KnowledgesClient.localizationKey("time_unit", path, "plural"),
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
