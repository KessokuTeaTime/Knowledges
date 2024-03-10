package net.krlite.knowledges.config.modmenu.cache;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.krlite.knowledges.KnowledgesClient;
import net.krlite.knowledges.KnowledgesCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public abstract class Cache<K, V> {
    public static final Logger LOGGER = LoggerFactory.getLogger(KnowledgesCommon.ID + ":cache");
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(KnowledgesCommon.ID).resolve("cache");

    private final Map<K, V> map = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File file;
    private boolean loading = false;

    protected Cache(String fileName) {
        this.file = PATH.resolve(fileName + ".json").toFile();
    }

    protected abstract boolean validate(V value);

    public Map<K, V> asMap() {
        return ImmutableMap.copyOf(map);
    }

    public boolean containsKey(K key) {
        return asMap().containsKey(key);
    }

    public void put(K key, V value) {
        if (validate(value)) {
            V prev = map.put(key, value);
            if (!loading && !Objects.equals(prev, value)) {
                save();
            }
        }
    }

    public void putIfAbsent(K key, V value) {
        if (containsKey(key)) return;
        put(key, value);
    }

    public Optional<V> get(K key) {
        return Optional.ofNullable(containsKey(key) ? asMap().get(key) : null);
    }

    public V getOrDefault(K key, V defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public Optional<V> remove(K key) {
        V prev = map.remove(key);
        if (prev != null) {
            save();
        }

        return Optional.ofNullable(prev);
    }

    public void operate(Consumer<Map<K, V>> mapConsumer) {
        mapConsumer.accept(map);
    }

    public void save() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Failed creating file!", e);
            }
        }

        new SaveThread(file, gson.toJson(asMap())).start();
    }

    public void load() {
        if (!file.exists()) {
            save();
            return;
        }

        loading = true;
        try (final BufferedReader reader = Files.newBufferedReader(file.toPath(), Charsets.UTF_8)) {
            Type type = new TypeToken<Map<UUID, String>>() {}.getType();
            Map<K, V> tempMap = gson.fromJson(reader, type);

            if (tempMap != null) {
                map.clear();
                tempMap.forEach(this::put);
            }
        } catch (Exception e) {
            LOGGER.error("Error caching! Deleting file at {}", file.getPath());
            file.delete();
        } finally {
            loading = false;
        }
    }

    private static class SaveThread extends Thread {
        private final File file;
        private final String data;

        public SaveThread(File file, String data) {
            this.file = file;
            this.data = data;
        }

        @Override
        public void run() {
            try {
                // Make sure we don't save when another thread is still saving
                synchronized (file) {
                    Files.writeString(file.toPath(), data);
                }
            } catch (IOException e) {
                LOGGER.error("Failed saving cache at {}!", file.getPath());
            }
        }
    }
}
