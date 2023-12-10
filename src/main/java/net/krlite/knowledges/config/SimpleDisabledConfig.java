package net.krlite.knowledges.config;

import net.krlite.knowledges.Knowledges;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;

public abstract class SimpleDisabledConfig {
    private final File file;
    protected static final ArrayList<String> disabled = new ArrayList<>();

    protected SimpleDisabledConfig(String fileName) {
        this.file = Knowledges.CONFIG_PATH.resolve(fileName + ".txt").toFile();
        load();
    }

    protected void load() {
        if (file.exists()) {
            try {
                disabled.clear();
                disabled.addAll(FileUtils.readLines(file, Charset.defaultCharset()));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        else {
            file.getParentFile().mkdirs();
            save();
        }
    }

    protected void save() {
        try {
            FileUtils.writeLines(file, disabled);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected boolean get(String key) {
        return disabled.contains(key);
    }

    protected void set(String key, boolean flag) {
        if (flag) {
            if (!disabled.contains(key)) {
                disabled.add(key);
                save();
            }
        } else {
            if (disabled.contains(key)) {
                disabled.remove(key);
                save();
            }
        }
    }
}
