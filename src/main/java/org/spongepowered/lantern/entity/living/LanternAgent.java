package org.spongepowered.lantern.entity.living;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import org.spongepowered.api.entity.ai.Goal;
import org.spongepowered.api.entity.ai.GoalType;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.util.persistence.InvalidDataException;

import java.util.Optional;

public abstract class LanternAgent extends LanternLivingEntity implements Agent {

    static {
        defaultManipulators.add(AgentData.class);
    }

    public LanternAgent(DataView container) {
        super(container);
    }

    @Override
    public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return false; //TODO: Regstry
    }

    @Override
    public boolean supports(Key<?> key) {
        return false; //TODO: Registry
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        return super.validateRawData(container); //TODO: Implement
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        super.setRawData(container); //TODO: Implement
    }

    @Override
    public <T extends Agent> Optional<Goal<T>> getGoal(GoalType type) {
        return null; //TODO: Implement
    }
}
