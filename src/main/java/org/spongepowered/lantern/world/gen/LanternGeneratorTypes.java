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
            return "end";
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
