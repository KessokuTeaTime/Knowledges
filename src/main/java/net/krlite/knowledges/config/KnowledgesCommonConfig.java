package net.krlite.knowledges.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import java.util.Map;
import java.util.TreeMap;

@Config(name = "knowledges")
public class KnowledgesCommonConfig extends PartitioningSerializer.GlobalData{
    public General general = new General();

    public Tags tags = new Tags();

    @Config(name = "general")
    public static class General implements ConfigData {
        public boolean autoTidiesUp = false;
    }

    @Config(name = "tags")
    public static class Tags implements ConfigData {
        public Map<String, Boolean> enabled = new TreeMap<>();
    }
}
