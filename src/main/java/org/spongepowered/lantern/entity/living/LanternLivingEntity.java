/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
