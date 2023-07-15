package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import static net.krlite.knowledges.Knowledges.CONFIG;

public class KnowledgesConfigScreen {
	public KnowledgesConfigScreen(Screen parent) {
		builder.setParentScreen(parent);
		initEntries();
	}

	public Screen build() {
		return builder.build();
	}

	private final ConfigBuilder builder = ConfigBuilder.create()
												  .setTitle(Knowledges.localize("screen", "config", "title"))
												  .transparentBackground()
												  .setSavingRunnable(CONFIG::save);

	private final ConfigEntryBuilder entryBuilder = builder.entryBuilder();

	private final ConfigCategory general = builder.getOrCreateCategory(Knowledges.localize("config", "category", "general"));

	private final ConfigCategory components = builder.getOrCreateCategory(Knowledges.localize("config", "category", "components"));

	private void initEntries() {
		// General

		general.addEntry(entryBuilder.startIntSlider(
						Knowledges.localize("config", "general", "crosshair_safe_area_size_scalar"),
						(int) (CONFIG.crosshairSafeAreaSizeScalar() * 50),
						0, 100
				)
								 .setDefaultValue(50)
								 .setTooltip(Knowledges.localize("config", "general", "crosshair_safe_area_size_scalar", "tooltip"))
								 .setSaveConsumer(value -> CONFIG.crosshairSafeAreaSizeScalar(value.floatValue() / 50))
								 .build());

		general.addEntry(entryBuilder.startIntSlider(
						Knowledges.localize("config", "general", "scalar"),
						(int) (CONFIG.scalar() * 50),
						0, 100
				)
								 .setDefaultValue(50)
								 .setTooltip(Knowledges.localize("config", "general", "scalar", "tooltip"))
								 .setSaveConsumer(value -> CONFIG.scalar(value.floatValue() / 50))
								 .build());

		// Components

		Knowledges.knowledges().forEach(knowledge -> {
			BooleanToggleBuilder booleanBuilder =
					entryBuilder.startBooleanToggle(
							knowledge.name(),
							Knowledges.knowledgeState(knowledge)
					)
							.setDefaultValue(true)
							.setSaveConsumer(value -> Knowledges.knowledgeState(knowledge, value));

			@Nullable Text tooltip = knowledge.tooltip();

			if (tooltip == null) {
				components.addEntry(booleanBuilder.build());
			} else {
				components.addEntry(booleanBuilder.setTooltip(tooltip).build());
			}
		});
	}
}
