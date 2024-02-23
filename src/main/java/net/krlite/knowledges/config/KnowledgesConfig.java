package net.krlite.knowledges.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.krlite.knowledges.data.info.block.blockinformation.NoteBlockInformationData;

@Config(name = "knowledges")
public class KnowledgesConfig extends PartitioningSerializer.GlobalData {
	@ConfigEntry.Category("global")
	@ConfigEntry.Gui.TransitiveObject
	public Global global = new Global();

	@ConfigEntry.Category("components")
	@ConfigEntry.Gui.TransitiveObject
	public Components components = new Components();

	@ConfigEntry.Category("data")
	@ConfigEntry.Gui.TransitiveObject
	public Data data = new Data();

	@Config(name = "general")
	public static class Global implements ConfigData {
		@ConfigEntry.BoundedDiscrete(min = 500, max = 2000)
		public int mainScalar = 1000;

		@ConfigEntry.BoundedDiscrete(min = 500, max = 2000)
		public int crosshairSafeAreaScalar = 1000;
	}

	@Config(name = "components")
	public static class Components implements ConfigData {
		@ConfigEntry.Gui.TransitiveObject
		public Crosshair crosshair = new Crosshair();
		@ConfigEntry.Gui.TransitiveObject
		public InfoBlock infoBlock = new InfoBlock();
		@ConfigEntry.Gui.TransitiveObject
		public InfoEntity infoEntity = new InfoEntity();
		@ConfigEntry.Gui.TransitiveObject
		public InfoFluid infoFluid = new InfoFluid();

		public static class Crosshair {
			public boolean cursorRingOutlineEnabled = true;
			public boolean textsRightEnabled = true;
			public boolean textsLeftEnabled = true;
			public boolean subtitlesEnabled = true;
		}

		public static class InfoBlock {
			public boolean showBlockPoweredStatus = true;
		}

		public static class InfoEntity {
			public boolean showNumericHealth = false;
		}

		public static class InfoFluid {
			public boolean ignoresWater = false;
			public boolean ignoresLava = false;
			public boolean ignoresOtherFluids = false;
		}
	}

	@Config(name = "data")
	public static class Data implements ConfigData {
		@ConfigEntry.Gui.TransitiveObject
		public NoteBlockInformation noteBlockInformation = new NoteBlockInformation();

		public static class NoteBlockInformation {
			@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
			public NoteBlockInformationData.NoteModifier noteModifier = NoteBlockInformationData.NoteModifier.SHARPS;

			@ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
			public NoteBlockInformationData.MusicalAlphabet musicalAlphabet = NoteBlockInformationData.MusicalAlphabet.ENGLISH;
		}
	}
}
