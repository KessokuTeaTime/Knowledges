package net.krlite.knowledges.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.krlite.knowledges.KnowledgesCommon;

import java.util.Map;
import java.util.TreeMap;

@Config(name = KnowledgesCommon.ID)
public class CommonConfig extends PartitioningSerializer.GlobalData {
    public Contracts contracts = new Contracts();

    @Config(name = "contracts")
    public static class Contracts implements ConfigData {
        public boolean autoTidiesUp = false;

        public Map<String, Boolean> available = new TreeMap<>();
    }
}
