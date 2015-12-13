package org.spongepowered.lantern.registry.type;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.lantern.registry.CatalogRegistryModule;
import org.spongepowered.lantern.registry.util.RegisterCatalog;
import org.spongepowered.lantern.world.difficulty.LanternDifficulty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DifficultyRegistryModule implements CatalogRegistryModule<Difficulty> {

    @RegisterCatalog(Difficulties.class)
    private final Map<String, Difficulty> difficultyMappings = new HashMap<>();

    @Override
    public Optional<Difficulty> getById(String id) {
        return Optional.ofNullable(this.difficultyMappings.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<Difficulty> getAll() {
        return ImmutableList.copyOf(this.difficultyMappings.values());
    }

    @Override
    public void registerDefaults() {
        this.difficultyMappings.put("peaceful", new LanternDifficulty(0, "peaceful"));
        this.difficultyMappings.put("easy", new LanternDifficulty(1, "easy"));
        this.difficultyMappings.put("normal", new LanternDifficulty(2, "normal"));
        this.difficultyMappings.put("hard", new LanternDifficulty(3, "hard"));
    }
}
