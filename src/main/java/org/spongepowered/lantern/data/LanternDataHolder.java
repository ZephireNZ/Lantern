package org.spongepowered.lantern.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionBuilder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class LanternDataHolder implements DataHolder {

    private Map<Class<? extends DataManipulator<?, ?>>, DataManipulator<?, ?>> containerStore = Maps.newIdentityHashMap();

    public LanternDataHolder(Map<Class<? extends DataManipulator<?, ?>>, DataManipulator<?, ?>> containerStore) {
        this.containerStore = Preconditions.checkNotNull(containerStore);
    }

    public LanternDataHolder(DataContainer container) {
        //TODO: Implement
    }

    public Collection<DataManipulator<?, ?>> getApplicableManipulators() {
        return null; //TODO: Implement
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        if(containerStore.containsKey(containerClass)) {
            return Optional.of((T) containerStore.get(containerClass));
        }
        return Optional.empty();
    }

    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        if(get(containerClass).isPresent()) return get(containerClass);

        return null;//TODO: Construct manipulator
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        return null; //TODO: Implement
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() && result.getSuccessfulData().isEmpty()) {
            return DataTransactionBuilder.successNoData();
        }
        final DataTransactionBuilder builder = DataTransactionBuilder.builder();
        for (ImmutableValue<?> replaced : result.getReplacedData()) {
            builder.absorbResult(offer(replaced));
        }
        for (ImmutableValue<?> successful : result.getSuccessfulData()) {
            builder.absorbResult(remove(successful));
        }
        return builder.build();
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return offer(that.getContainers(), function);
    }

    @Override
    public Collection<DataManipulator<?, ?>> getContainers() {
        return null; //TODO: Implement
    }

    @Override
    public DataContainer toContainer() {
        return null; //TODO: Implement
    }

    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return null; //TODO: Implement
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return null; //TODO: Implement
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        return null; //TODO: Implement
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        return null; //TODO: Implement
    }

    @Override
    public Set<Key<?>> getKeys() {
        ImmutableSet.Builder<Key<?>> builder = ImmutableSet.builder();
        containerStore.values().forEach(data -> builder.addAll(data.getKeys()));
        return builder.build();
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        ImmutableSet.Builder<ImmutableValue<?>> builder = ImmutableSet.builder();
        containerStore.values().forEach(data -> builder.addAll(data.getValues()));
        return builder.build();
    }
}
