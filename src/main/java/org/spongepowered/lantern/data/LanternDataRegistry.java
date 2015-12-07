package org.spongepowered.lantern.data;

import com.google.common.collect.MapMaker;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.lantern.util.persistance.LanternSerializationManager;

import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@SuppressWarnings("unchecked")
public class LanternDataRegistry implements DataManipulatorRegistry {

    private static final LanternDataRegistry instance = new LanternDataRegistry();

    private final Map<Class<? extends DataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> builderMap = new MapMaker()
            .concurrencyLevel(4)
            .makeMap();
    private final Map<Class<? extends ImmutableDataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> immutableBuilderMap = new MapMaker()
            .concurrencyLevel(4)
            .makeMap();


    private static boolean allowRegistrations = true;

    private LanternDataRegistry() {
    }


    public static LanternDataRegistry getInstance() {
        return LanternDataRegistry.instance;
    }

    public void completeRegistration() {
        checkState(allowRegistrations);
        allowRegistrations = false;
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> void register(Class<? extends T> manipulatorClass, Class<? extends I> immutableManipulatorClass, DataManipulatorBuilder<T, I> builder) {
        checkState(allowRegistrations, "Registrations are no longer allowed!");
        if (!this.builderMap.containsKey(checkNotNull(manipulatorClass))) {
            this.builderMap.put(manipulatorClass, checkNotNull(builder));
            this.immutableBuilderMap.put(checkNotNull(immutableManipulatorClass), builder);
            LanternSerializationManager.getInstance().registerBuilder((Class<T>) manipulatorClass, builder);
        } else {
            throw new IllegalStateException("Already registered the DataUtil for " + manipulatorClass.getCanonicalName());
        }
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getBuilder(Class<T> manipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.builderMap.get(checkNotNull(manipulatorClass)));
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getBuilderForImmutable(Class<I> immutableManipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.immutableBuilderMap.get(checkNotNull(immutableManipulatorClass)));
    }
}
