package net.krlite.knowledges.config.modmenu;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.krlite.knowledges.Knowledges;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

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
												  .setTitle(Text.translatable("screen." + Knowledges.ID + ".config.title"))
												  .transparentBackground()
												  .setSavingRunnable(CONFIG::save);

	private final ConfigEntryBuilder entryBuilder = builder.entryBuilder();

	private final ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config." + Knowledges.ID + "category.general"));

	private final ConfigCategory components = builder.getOrCreateCategory(Text.translatable("config." + Knowledges.ID + "category.components"));

	private void initEntries() {
		// General

		general.addEntry(entryBuilder.startIntSlider(
						Text.translatable("config." + Knowledges.ID + ".general.crosshair_safe_area_size_scalar"),
						(int) (CONFIG.crosshairSafeAreaSizeScalar() * 50),
						0, 100
				)
								 .setDefaultValue(50)
								 .setTooltip(Text.translatable("config." + Knowledges.ID + ".general.crosshair_safe_area_size_scalar.tooltip"))
								 .setSaveConsumer(value -> CONFIG.crosshairSafeAreaSizeScalar(value.floatValue() / 50))
								 .build());

		general.addEntry(entryBuilder.startIntSlider(
						Text.translatable("config." + Knowledges.ID + ".general.scalar"),
						(int) (CONFIG.scalar() * 50),
						0, 100
				)
								 .setDefaultValue(50)
								 .setTooltip(Text.translatable("config." + Knowledges.ID + ".general.scalar.tooltip"))
								 .setSaveConsumer(value -> CONFIG.scalar(value.floatValue() / 50))
								 .build());

		// Components

		Knowledges.KNOWLEDGES.forEach(knowledge -> {
			components.addEntry(entryBuilder.startBooleanToggle(
							knowledge.name(),
							Knowledges.knowledgeState(knowledge)
					)
										.setDefaultValue(true)
										.setTooltip(knowledge.tooltip())
										.setSaveConsumer(value -> Knowledges.knowledgeState(knowledge, value))
										.build());
		});
	}
}
