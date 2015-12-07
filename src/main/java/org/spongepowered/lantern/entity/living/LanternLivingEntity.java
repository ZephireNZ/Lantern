package org.spongepowered.lantern.entity.living;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.LeashData;
import org.spongepowered.api.data.manipulator.mutable.entity.PersistingData;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.util.persistence.InvalidDataException;
import org.spongepowered.lantern.entity.LanternEntity;

public abstract class LanternLivingEntity extends LanternEntity implements Living {

    static {
        defaultManipulators.add(HealthData.class);
        defaultManipulators.add(DamageableData.class);
        defaultManipulators.add(PotionEffectData.class);
        defaultManipulators.add(LeashData.class);
        defaultManipulators.add(PersistingData.class);
    }

    public LanternLivingEntity(DataView container) {
        super(container);
    }

    @Override
    public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return false; //TODO: Implement
    }

    @Override
    public boolean supports(Key<?> key) {
        return false; //TODO: Implement
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        return super.validateRawData(container); //TODO: Implement
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        super.setRawData(container); //TODO: Implement
    }
}
