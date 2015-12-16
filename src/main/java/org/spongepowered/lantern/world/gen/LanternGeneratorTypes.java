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
package org.spongepowered.lantern.world.gen;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;

public class LanternGeneratorTypes {

    public static final GeneratorType DEBUG = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "debug";
        }

        @Override
        public String getName() {
            return "Debug";
        }
    };


    public static final GeneratorType FLAT = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "flat";
        }

        @Override
        public String getName() {
            return "Flat";
        }
    };

    public static final GeneratorType NETHER = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "nether";
        }

        @Override
        public String getName() {
            return "Nether";
        }
    };

    public static final GeneratorType OVERWORLD = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "overworld";
        }

        @Override
        public String getName() {
            return "Overworld";
        }
    };

    public static final GeneratorType END = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "the_end";
        }

        @Override
        public String getName() {
            return "The End";
        }
    };

    public static final GeneratorType DEFAULT = new GeneratorType() {
        @Override
        public DataContainer getGeneratorSettings() {
            return new MemoryDataContainer(); //TODO: Implement
        }

        @Override
        public WorldGenerator createGenerator(World world) {
            return null; //TODO: Implement
        }

        @Override
        public String getId() {
            return "default";
        }

        @Override
        public String getName() {
            return "Default";
        }
    };
}
