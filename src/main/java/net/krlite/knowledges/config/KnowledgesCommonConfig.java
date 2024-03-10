package net.krlite.knowledges.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

import java.util.Map;
import java.util.TreeMap;

public class KnowledgesCommonConfig extends PartitioningSerializer.GlobalData{
    @ConfigEntry.Category("tags")
    public Tags tags = new Tags();

    @Config(name = "tags")
    public static class Tags implements ConfigData {
        public Map<String, Boolean> enabled = new TreeMap<>();
    }
}
