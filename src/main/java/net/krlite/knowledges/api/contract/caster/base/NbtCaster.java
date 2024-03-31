package net.krlite.knowledges.api.contract.caster.base;

import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public abstract class NbtCaster<T> {
    @FunctionalInterface
    public interface FromData<T> {
        T get(NbtCompound data, String key);
    }

    @FunctionalInterface
    public interface ToData<T> {
        void set(NbtCompound data, String key, T t);
    }

    private final String identifier;
    private final FromData<T> fromData;
    private final ToData<T> toData;

    protected NbtCaster(String identifier, FromData<T> fromData, ToData<T> toData) {
        this.fromData = fromData;
        this.toData = toData;
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }

    public boolean hasValueIn(NbtCompound data) {
        return data.contains(identifier);
    }

    public Optional<T> get(NbtCompound data) {
        return Optional.ofNullable(hasValueIn(data) ? fromData.get(data, identifier) : null);
    }

    public void put(NbtCompound data, T t) {
        toData.set(data, identifier, t);
    }
}
