package org.spongepowered.lantern.data;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;

import java.util.Optional;

public class LanternDataRegistry implements DataManipulatorRegistry {

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> void register(Class<? extends T> manipulatorClass, Class<? extends I> immutableManipulatorClass, DataManipulatorBuilder<T, I> builder) {
        //TODO: Implement
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getBuilder(Class<T> manipulatorClass) {
        return null; //TODO: Implement
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getBuilderForImmutable(Class<I> immutableManipulatorClass) {
        return null; //TODO: Implement
    }
}
