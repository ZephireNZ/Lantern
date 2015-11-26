package org.spongepowered.lantern.entity.living.animal;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreedableData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.animal.Cow;
import org.spongepowered.api.text.Text;
import org.spongepowered.lantern.entity.living.LanternAgent;

public class LanternCow extends LanternAgent implements Cow {

    static {
        defaultManipulators.add(AgeableData.class);
        defaultManipulators.add(BreedableData.class);
    }

    public LanternCow(DataView container) {
        super(container);
    }

    @Override
    public EntityType getType() {
        return EntityTypes.COW;
    }

    @Override
    public Text getTeamRepresentation() {
        return null; //TODO: Implement
    }

    @Override
    public void setScaleForAge() {
        //TODO: Implement
    }

    @Override
    public DataHolder copy() {
        return new LanternCow(this.toContainer());
    }
}
