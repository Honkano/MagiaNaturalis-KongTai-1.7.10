package com.github.elenterius.magianaturalis.init;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;

public class DeferredHolder<T> implements Supplier<T> {

    private final ResourceLocation name;
    private T value;

    public DeferredHolder(ResourceLocation name) {
        this.name = name;
    }

    @Override
    public T get() {
        Objects.requireNonNull(value, () -> "Deferred Object not present: " + name);
        return value;
    }

    public ResourceLocation getId() {
        return name;
    }

    void bind(T value) {
        if (name == null) return;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object.getClass() != this.getClass()) return false;
        if (this == object) return true;
        DeferredHolder<?> that = (DeferredHolder<?>) object;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

}
